package cn.qsbye.robot_suit_zulu.depth

import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * 相对深度小网络（纯 Kotlin MLP，无任何第三方 NN 依赖）。
 *
 * 范式与主 app 消音器的 [cn.qsbye.robot_suit_zulu.dsp.anc.WaveformMlp] 一致：
 * 确定性的逐格特征（位置/亮度/梯度/多尺度模糊）由信号处理给出（[FeatureGrid]），
 * 网络只学「特征 → 相对深度」这一件小事，因此参数极少（~465），
 * 真机端上训练数十秒收敛（[DepthTrainer]）。
 *
 * 输入：FEATURES 维逐格特征（归一化坐标、亮度、梯度、模糊、局部方差）；
 * 结构：Dense(20,tanh) → Dense(12,tanh) → Dense(1,sigmoid)；
 * 输出：相对深度 0=far → 1=near（逐帧仿射不变，呼应 DepthART 的
 * 「单目深度 = 学习式先验回归」思想，这里先验来自程序化合成场景）。
 */
class DepthNet(
    random: Random = Random(0xDE7A)
) {
    private val f = FeatureGrid.FEATURES
    private val h1 = HIDDEN1
    private val h2 = HIDDEN2

    // 权一维展开：w1[f*h1 布局]、w2[h1*h2]、w3[h2]
    private val w1 = FloatArray(f * h1)
    private val b1 = FloatArray(h1)
    private val w2 = FloatArray(h1 * h2)
    private val b2 = FloatArray(h2)
    private val w3 = FloatArray(h2)
    private var b3 = 0f

    // Adam 状态（与参数一一对应）
    private val pm = FloatArray(w1.size + b1.size + w2.size + b2.size + w3.size + 1)
    private val pv = FloatArray(pm.size)

    // 单样本推理工作区（调用方保证同一时刻单线程使用）
    private val a1 = FloatArray(h1)
    private val a2 = FloatArray(h2)

    val parameterCount: Int get() = pm.size

    /** 导出全部参数（顺序 w1,b1,w2,b2,w3,b3），供训练器保存最优权重。 */
    fun exportParams(): FloatArray {
        val out = FloatArray(parameterCount)
        var p = 0
        w1.copyInto(out, p); p += w1.size
        b1.copyInto(out, p); p += b1.size
        w2.copyInto(out, p); p += w2.size
        b2.copyInto(out, p); p += b2.size
        w3.copyInto(out, p); p += w3.size
        out[p] = b3
        return out
    }

    /** 恢复 [exportParams] 导出的参数。 */
    fun importParams(params: FloatArray) {
        require(params.size == parameterCount)
        var p = 0
        params.copyInto(w1, 0, p, p + w1.size); p += w1.size
        params.copyInto(b1, 0, p, p + b1.size); p += b1.size
        params.copyInto(w2, 0, p, p + w2.size); p += w2.size
        params.copyInto(b2, 0, p, p + b2.size); p += b2.size
        params.copyInto(w3, 0, p, p + w3.size); p += w3.size
        b3 = params[p]
    }

    init {
        // He 初始化
        for (i in w1.indices) w1[i] = (random.nextFloat() - 0.5f) * 2f * sqrt(2f / f)
        for (i in w2.indices) w2[i] = (random.nextFloat() - 0.5f) * 2f * sqrt(2f / h1)
        for (i in w3.indices) w3[i] = (random.nextFloat() - 0.5f) * 2f * sqrt(2f / h2)
    }

    /**
     * 对 flat 特征数组中 offset 处的一个样本推理，返回相对深度 (0,1)。
     * 无堆分配，供相机帧逐格调用。
     */
    fun predict(feat: FloatArray, offset: Int): Float {
        for (j in 0 until h1) {
            var z = b1[j]
            val off = j * f
            for (i in 0 until f) z += w1[off + i] * feat[offset + i]
            a1[j] = tanh(z)
        }
        for (j in 0 until h2) {
            var z = b2[j]
            val off = j * h1
            for (i in 0 until h1) z += w2[off + i] * a1[i]
            a2[j] = tanh(z)
        }
        var y = b3
        for (j in 0 until h2) y += w3[j] * a2[j]
        return sigmoid(y)
    }

    /**
     * 一个 mini-batch 的前向+反向+Adam 更新，返回该批 MSE。
     * @param feat 扁平特征数组（布局 feat[(start+s)*FEATURES + i]）
     * @param targets 目标相对深度（0=far → 1=near）
     */
    fun trainBatch(
        feat: FloatArray,
        targets: FloatArray,
        start: Int,
        count: Int,
        lr: Float,
        timeStep: Long
    ): Float {
        val b = count
        val xBuf = Array(b) { FloatArray(f) }
        val z1 = Array(b) { FloatArray(h1) }
        val aa1 = Array(b) { FloatArray(h1) }
        val z2 = Array(b) { FloatArray(h2) }
        val aa2 = Array(b) { FloatArray(h2) }
        val yy = FloatArray(b)

        var se = 0.0
        for (s in 0 until b) {
            val x = xBuf[s]
            val fo = (start + s) * f
            for (i in 0 until f) x[i] = feat[fo + i]
            for (j in 0 until h1) {
                var z = b1[j]
                val off = j * f
                for (i in 0 until f) z += w1[off + i] * x[i]
                z1[s][j] = z
                aa1[s][j] = tanh(z)
            }
            for (j in 0 until h2) {
                var z = b2[j]
                val off = j * h1
                for (i in 0 until h1) z += w2[off + i] * aa1[s][i]
                z2[s][j] = z
                aa2[s][j] = tanh(z)
            }
            var y = b3
            for (j in 0 until h2) y += w3[j] * aa2[s][j]
            yy[s] = sigmoid(y)
            val e = yy[s] - targets[start + s]
            se += e.toDouble() * e
        }

        // 梯度累积（MSE 均值损失）
        val gW1 = FloatArray(w1.size)
        val gB1 = FloatArray(h1)
        val gW2 = FloatArray(w2.size)
        val gB2 = FloatArray(h2)
        val gW3 = FloatArray(h2)
        var gB3 = 0f

        for (s in 0 until b) {
            val y = yy[s]
            // dL/dy = (y-t)/b，乘 sigmoid 导数
            val d3 = ((y - targets[start + s]) / b) * y * (1f - y)
            for (j in 0 until h2) gW3[j] += d3 * aa2[s][j]
            gB3 += d3

            val d2 = FloatArray(h2)
            for (j in 0 until h2) {
                d2[j] = (w3[j] * d3) * (1f - aa2[s][j] * aa2[s][j])
                gB2[j] += d2[j]
            }
            for (j in 0 until h2) {
                val off = j * h1
                for (i in 0 until h1) gW2[off + i] += d2[j] * aa1[s][i]
            }
            val d1 = FloatArray(h1)
            for (i in 0 until h1) {
                var acc = 0f
                for (j in 0 until h2) acc += w2[j * h1 + i] * d2[j]
                d1[i] = acc * (1f - aa1[s][i] * aa1[s][i])
                gB1[i] += d1[i]
            }
            for (j in 0 until h1) {
                val off = j * f
                for (i in 0 until f) gW1[off + i] += d1[j] * xBuf[s][i]
            }
        }

        // 合并为参数序并做 Adam 更新
        var p = 0
        p = adamStep(w1, gW1, p, lr, timeStep)
        p = adamStep(b1, gB1, p, lr, timeStep)
        p = adamStep(w2, gW2, p, lr, timeStep)
        p = adamStep(b2, gB2, p, lr, timeStep)
        p = adamStep(w3, gW3, p, lr, timeStep)
        b3 = adamScalar(b3, gB3, p, lr, timeStep)
        return (se / b).toFloat()
    }

    private fun adamStep(w: FloatArray, g: FloatArray, offset: Int, lr: Float, t: Long): Int {
        for (i in w.indices) {
            val gi = g[i]
            val mi = B1 * pm[offset + i] + (1f - B1) * gi
            val vi = B2 * pv[offset + i] + (1f - B2) * gi * gi
            pm[offset + i] = mi
            pv[offset + i] = vi
            val mHat = mi / (1f - powB1(t))
            val vHat = vi / (1f - powB2(t))
            w[i] -= lr * mHat / (sqrt(vHat) + EPS)
        }
        return offset + w.size
    }

    private fun adamScalar(w: Float, g: Float, offset: Int, lr: Float, t: Long): Float {
        val mi = B1 * pm[offset] + (1f - B1) * g
        val vi = B2 * pv[offset] + (1f - B2) * g * g
        pm[offset] = mi
        pv[offset] = vi
        val mHat = mi / (1f - powB1(t))
        val vHat = vi / (1f - powB2(t))
        return w - lr * mHat / (sqrt(vHat) + EPS)
    }

    private fun tanh(x: Float): Float {
        if (x > 4.5f) return 1f
        if (x < -4.5f) return -1f
        val e2 = exp(2f * x)
        return (e2 - 1f) / (e2 + 1f)
    }

    private fun sigmoid(x: Float): Float =
        if (x >= 0f) 1f / (1f + exp(-x))
        else {
            val e = exp(x)
            e / (1f + e)
        }

    private fun powB1(t: Long) = powTable[((t - 1).toInt()).coerceIn(0, powTable.size - 1)]
    private fun powB2(t: Long) = powTable2[((t - 1).toInt()).coerceIn(0, powTable2.size - 1)]

    companion object {
        const val HIDDEN1 = 20
        const val HIDDEN2 = 12
        private const val B1 = 0.9f
        private const val B2 = 0.999f
        private const val EPS = 1e-8f
        // β^t 查表，避免每步 pow；最多支持 100000 步
        private val powTable = FloatArray(100000) { Math.pow(B1.toDouble(), it + 1.0).toFloat() }
        private val powTable2 = FloatArray(100000) { Math.pow(B2.toDouble(), it + 1.0).toFloat() }
    }
}
