package cn.qsbye.depthest

import android.os.Handler
import android.os.Looper
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * 深度估计引擎：持有网络与训练状态，负责
 *  1. 相机帧 → 摆正网格亮度（含旋转校正）→ 特征 → 推理 → EMA 平滑 → 伪彩色
 *  2. 在后台线程跑 [DepthTrainer]（合成数据端上训练），回调经主线程投递
 *
 * 推理节流 ~8 fps（KEEP_ONLY_LATEST 下再限频，省电且平滑够用）。
 */
class DepthEngine(
    val gridW: Int = 40,
    val gridH: Int = 30,
) {

    @Volatile
    var isTrained = false
        private set

    @Volatile
    var isTraining = false
        private set

    private var net: DepthNet? = null
    private var trainer: DepthTrainer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    // 推理工作区（分析线程独占）
    private val lumGrid = FloatArray(gridW * gridH)
    private val featGrid = FloatArray(gridW * gridH * FeatureGrid.FEATURES)
    private val emaDepth = FloatArray(gridW * gridH)
    private val pixels = IntArray(gridW * gridH)
    private var hasEma = false
    private var lastFrameMs = 0L

    /**
     * 处理一帧原始 Y 平面（紧缩布局，srcW×srcH），返回伪彩色像素（gridW×gridH，ARGB）。
     * 节流窗口内或网络未训练时返回 null。
     */
    @Synchronized
    fun onRawFrame(raw: ByteArray, srcW: Int, srcH: Int, rotation: Int): IntArray? {
        val now = System.currentTimeMillis()
        if (now - lastFrameMs < MIN_INTERVAL_MS) return null
        lastFrameMs = now
        val n = net ?: return null

        sampleUprightGrid(raw, srcW, srcH, rotation)
        FeatureGrid.compute(lumGrid, gridW, gridH, featGrid)
        for (i in 0 until gridW * gridH) {
            val d = n.predict(featGrid, i * FeatureGrid.FEATURES)
            emaDepth[i] = if (hasEma) emaDepth[i] * 0.55f + d * 0.45f else d
        }
        hasEma = true
        for (i in pixels.indices) pixels[i] = depthColor(emaDepth[i])
        // 返回副本：共享缓冲可能被下一帧覆写，UI 侧异步消费
        return pixels.copyOf()
    }

    /** 相机坐标系到摆正网格的降采样：每格均匀采 4×4 个点取均值。 */
    private fun sampleUprightGrid(raw: ByteArray, srcW: Int, srcH: Int, rotation: Int) {
        val uprightW = if (rotation % 180 == 0) srcW else srcH
        val uprightH = if (rotation % 180 == 0) srcH else srcW
        for (gy in 0 until gridH) {
            val uy0 = gy * uprightH / gridH
            val uy1 = (gy + 1) * uprightH / gridH
            for (gx in 0 until gridW) {
                val ux0 = gx * uprightW / gridW
                val ux1 = (gx + 1) * uprightW / gridW
                val cellW = (ux1 - ux0).coerceAtLeast(1)
                val cellH = (uy1 - uy0).coerceAtLeast(1)
                var acc = 0f
                var cnt = 0
                for (sy in 0 until 4) {
                    val uy = uy0 + ((sy + 0.5f) * cellH / 4f).toInt()
                    for (sx in 0 until 4) {
                        val ux = ux0 + ((sx + 0.5f) * cellW / 4f).toInt()
                        acc += rawAt(raw, srcW, srcH, ux, uy, rotation)
                        cnt++
                    }
                }
                lumGrid[gy * gridW + gx] = (acc / cnt.coerceAtLeast(1)) / 255f
            }
        }
    }

    /** 摆正坐标 (ux,uy) → 原始 Y 平面取值（最近邻，含旋转逆映射）。 */
    private fun rawAt(raw: ByteArray, srcW: Int, srcH: Int, ux: Int, uy: Int, rotation: Int): Float {
        val rx: Int
        val ry: Int
        when (rotation) {
            90 -> { rx = uy; ry = srcH - 1 - ux }
            180 -> { rx = srcW - 1 - ux; ry = srcH - 1 - uy }
            270 -> { rx = srcW - 1 - uy; ry = ux }
            else -> { rx = ux; ry = uy }
        }
        val x = rx.coerceIn(0, srcW - 1)
        val y = ry.coerceIn(0, srcH - 1)
        return raw[y * srcW + x].toInt().and(0xFF).toFloat()
    }

    /**
     * 启动端上训练（后台线程）。重复调用被忽略；再次训练会覆盖旧模型。
     * @param onProgress 主线程回调（每轮）
     * @param onDone 主线程回调（完成/取消，result 为 null 表示被取消且无历史结果）
     */
    fun startTraining(
        onProgress: (DepthTrainer.Progress) -> Unit,
        onDone: (DepthTrainer.Result?) -> Unit,
    ) {
        if (isTraining) return
        isTraining = true
        hasEma = false
        val t = DepthTrainer(gridW, gridH)
        trainer = t
        Thread {
            var result: DepthTrainer.Result? = null
            try {
                result = t.train { p -> mainHandler.post { if (isTraining) onProgress(p) } }
            } catch (_: Exception) {
            } finally {
                mainHandler.post {
                    isTraining = false
                    if (result != null) {
                        net = result.net
                        isTrained = true
                    }
                    onDone(result)
                }
            }
        }.apply { priority = Thread.NORM_PRIORITY - 1 }.start()
    }

    /** 取消训练（保留训练中已保存的最优权重）。 */
    fun cancelTraining() {
        trainer?.cancelled = true
    }

    /** 待机页展示用：固定种子合成场景的深度伪彩色预览。 */
    fun syntheticPreview(): IntArray {
        val lum = FloatArray(gridW * gridH)
        val depth = FloatArray(gridW * gridH)
        SceneSynthesizer.generate(Random(0x51DE), gridW, gridH, lum, depth)
        val out = IntArray(gridW * gridH)
        for (i in out.indices) out[i] = depthColor(depth[i])
        return out
    }

    companion object {
        private const val MIN_INTERVAL_MS = 120L

        /**
         * 相对深度伪彩色（0=far 蓝 → 1=near 红，turbo 风格七段线性插值，
         * 自实现色表，无第三方依赖）。
         */
        fun depthColor(t: Float): Int {
            val x = t.coerceIn(0f, 1f) * (STOPS.size - 1)
            val i = x.toInt().coerceIn(0, STOPS.size - 2)
            val f = x - i
            val a = STOPS[i]
            val b = STOPS[i + 1]
            val r = (a[0] + (b[0] - a[0]) * f).toInt()
            val g = (a[1] + (b[1] - a[1]) * f).toInt()
            val bl = (a[2] + (b[2] - a[2]) * f).toInt()
            return 0xFF000000.toInt() or (r shl 16) or (g shl 8) or bl
        }

        private val STOPS = arrayOf(
            floatArrayOf(28f, 44f, 98f),
            floatArrayOf(33f, 98f, 178f),
            floatArrayOf(42f, 170f, 178f),
            floatArrayOf(92f, 200f, 99f),
            floatArrayOf(222f, 205f, 62f),
            floatArrayOf(237f, 126f, 42f),
            floatArrayOf(178f, 34f, 34f),
        )
    }
}
