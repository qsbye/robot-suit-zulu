package cn.qsbye.depthest

import kotlin.random.Random

/**
 * 端上训练器（范式对应主 app 消音器的 AdamTrainer）：
 * 程序化合成场景数据集 → mini-batch Adam 训练 [DepthNet]，
 * 带验证集早停与最优权重回退，真机数十秒收敛。
 *
 * 数据角色对应 DepthART 管线：合成场景 ≈ Hypersim（有真值深度的合成数据），
 * 训练目标 ≈ 「图像特征 → 相对深度先验」回归。
 */
class DepthTrainer(
    private val gw: Int,
    private val gh: Int,
    private val sceneCount: Int = 120,
    private val maxEpochs: Int = 60,
    private val batchSize: Int = 512,
) {

    data class Progress(
        val epoch: Int,
        val totalEpochs: Int,
        val trainLoss: Float,
        val valLoss: Float,
    )

    data class Result(
        val net: DepthNet,
        val initialTrainLoss: Float,
        val finalTrainLoss: Float,
        val finalValLoss: Float,
        val epochsRun: Int,
        val elapsedMs: Long,
        val parameterCount: Int,
        val sampleCount: Int,
    )

    @Volatile
    var cancelled = false

    private val cellsPerScene = gw * gh
    private val f = FeatureGrid.FEATURES

    /**
     * 执行训练（应放在后台线程调用）。
     * @param onProgress 每轮结束回调
     */
    fun train(onProgress: (Progress) -> Unit): Result {
        val started = System.currentTimeMillis()
        val rng = Random(0xD00F)

        // ---- 生成合成数据集并提取特征 ----
        val totalCells = sceneCount * cellsPerScene
        val feat = FloatArray(totalCells * f)
        val targets = FloatArray(totalCells)
        val lum = FloatArray(cellsPerScene)
        val depth = FloatArray(cellsPerScene)
        for (s in 0 until sceneCount) {
            SceneSynthesizer.generate(rng, gw, gh, lum, depth)
            val base = s * cellsPerScene
            System.arraycopy(depth, 0, targets, base, cellsPerScene)
            FeatureGrid.compute(lum, gw, gh, feat, base * f)
        }

        // ---- 按场景划分训练/验证集（防止同场景泄漏） ----
        val sceneOrder = IntArray(sceneCount) { it }
        for (i in sceneCount - 1 downTo 1) {
            val j = rng.nextInt(i + 1)
            val t = sceneOrder[i]; sceneOrder[i] = sceneOrder[j]; sceneOrder[j] = t
        }
        val valScenes = (sceneCount * 0.15f).toInt().coerceIn(1, sceneCount / 2)
        val trainScenes = sceneCount - valScenes
        val trainCells = IntArray(trainScenes * cellsPerScene)
        val valCells = IntArray(valScenes * cellsPerScene)
        var ti = 0
        var vi = 0
        for (k in 0 until sceneCount) {
            val s = sceneOrder[k]
            for (c in 0 until cellsPerScene) {
                if (k < trainScenes) trainCells[ti++] = s * cellsPerScene + c
                else valCells[vi++] = s * cellsPerScene + c
            }
        }

        val net = DepthNet(Random(0x5EED))

        // ---- 训练循环 ----
        val order = trainCells.copyOf()
        var step = 0L
        var initialTrainLoss = Float.NaN
        var bestVal = Float.MAX_VALUE
        var bestParams: FloatArray? = null
        var finalTrainLoss = Float.NaN
        var noImprove = 0
        var epoch = 0

        while (epoch < maxEpochs && !cancelled) {
            epoch++
            // Fisher-Yates 打乱训练格
            for (i in order.size - 1 downTo 1) {
                val j = rng.nextInt(i + 1)
                val t = order[i]; order[i] = order[j]; order[j] = t
            }

            var lossSum = 0.0
            var batches = 0
            var start = 0
            while (start < order.size && !cancelled) {
                val count = minOf(batchSize, order.size - start)
                // 打乱序映射回数据下标：直接把 order 段拷入连续批缓冲
                for (k in 0 until count) batchIndexBuf[k] = order[start + k]
                lossSum += trainShuffledBatch(net, feat, targets, count, learningRateAt(epoch), ++step)
                    .toDouble()
                batches++
                start += count
            }
            if (cancelled) break
            finalTrainLoss = (lossSum / batches).toFloat()
            if (epoch == 1) initialTrainLoss = finalTrainLoss

            val vl = evaluate(net, feat, targets, valCells)
            onProgress(Progress(epoch, maxEpochs, finalTrainLoss, vl))

            if (vl < bestVal) {
                bestVal = vl
                bestParams = net.exportParams()
                noImprove = 0
            } else {
                noImprove++
            }
            if (noImprove >= 10) break
        }

        // 恢复验证集最优权重
        bestParams?.let { net.importParams(it) }

        return Result(
            net = net,
            initialTrainLoss = initialTrainLoss,
            finalTrainLoss = finalTrainLoss,
            finalValLoss = if (bestVal.isFinite()) bestVal else Float.NaN,
            epochsRun = epoch,
            elapsedMs = System.currentTimeMillis() - started,
            parameterCount = net.parameterCount,
            sampleCount = order.size,
        )
    }

    // 批下标缓冲（trainBatch 按连续下标取样本，打乱序需经此间接层）
    private val batchIndexBuf = IntArray(batchSize)
    private var batchFeatureBuf = FloatArray(0)
    private var batchTargetBuf = FloatArray(0)

    /** 把打乱序的一个批 gather 成连续缓冲后送入网络训练。 */
    private fun trainShuffledBatch(
        net: DepthNet,
        feat: FloatArray,
        targets: FloatArray,
        count: Int,
        lr: Float,
        step: Long,
    ): Float {
        val need = count * f
        if (batchFeatureBuf.size < need) {
            batchFeatureBuf = FloatArray(need)
            batchTargetBuf = FloatArray(count)
        }
        for (k in 0 until count) {
            val src = batchIndexBuf[k] * f
            System.arraycopy(feat, src, batchFeatureBuf, k * f, f)
            batchTargetBuf[k] = targets[batchIndexBuf[k]]
        }
        // 连续批缓冲从 0 起：start=0、targets 即批内目标
        return net.trainBatch(batchFeatureBuf, batchTargetBuf, 0, count, lr, step)
    }

    /** 在给定格集合上做前向评估，返回 MSE。 */
    private fun evaluate(net: DepthNet, feat: FloatArray, targets: FloatArray, cells: IntArray): Float {
        var se = 0.0
        val single = FloatArray(f)
        for (idx in cells) {
            System.arraycopy(feat, idx * f, single, 0, f)
            val y = net.predict(single, 0)
            val e = y - targets[idx]
            se += e.toDouble() * e
        }
        return (se / cells.size).toFloat()
    }

    private fun learningRateAt(epoch: Int): Float = when {
        epoch < maxEpochs * 40 / 100 -> 0.01f
        epoch < maxEpochs * 75 / 100 -> 0.004f
        else -> 0.001f
    }
}
