package cn.qsbye.hummingbird.homing

import android.os.Handler
import android.os.Looper
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlin.math.hypot

/**
 * 相机帧分析器（FOSS：CameraX ImageAnalysis，YUV_420_888 亮度平面）：
 * 1) 下采样灰度 → 128 桶列均值 = 「视野信号」（Fourier 快照/归巢输入，对应论文柱面重投影+垂直平均的手机端近似）
 * 2) 帧间 1D 相位相关（列均值→水平移位、行均值→垂直移位）→ 光流式里程计
 *    （对应论文「速度 × 时间」里程计；尺度按假定工作距离换算，演示级）
 * 回调统一切到主线程。
 */
class CameraHomingEngine(
    private val listener: OnFrameListener,
) : ImageAnalysis.Analyzer {
    interface OnFrameListener {
        /** signal=128 点视野信号；dxM/dyM=相机系平移（米，右/下为正）；movedM=位移模长 */
        fun onFrame(signal: FloatArray, dxM: Float, dyM: Float, movedM: Float)
    }

    companion object {
        const val SIGNAL_LEN = Fft128.N
        const val H_FOV_DEG = 62f
        const val WORK_DIST_M = 1.5f

        /** 视野宽度（米）：2·D·tan(FOV/2)，62° FOV + 1.5m 工作距 ≈ 1.8m */
        val VIEW_WIDTH_M =
            2f * WORK_DIST_M * Math.tan(Math.toRadians(H_FOV_DEG / 2.0)).toFloat()
    }

    private val main = Handler(Looper.getMainLooper())
    private val prevCol = FloatArray(SIGNAL_LEN)
    private val prevRow = FloatArray(SIGNAL_LEN)
    private var hasPrev = false
    private var frames = 0
    private var fpsAt = 0L

    /** 实测帧率（供状态条显示） */
    @Volatile
    var fps = 0
        private set

    private val col = FloatArray(SIGNAL_LEN)
    private val row = FloatArray(SIGNAL_LEN)
    private val rowSum = FloatArray(SIGNAL_LEN)
    private val rowCnt = IntArray(SIGNAL_LEN)

    override fun analyze(image: ImageProxy) {
        val w = image.width
        val h = image.height
        val buf = image.planes[0].buffer
        val rowStride = image.planes[0].rowStride

        java.util.Arrays.fill(col, 0f)
        java.util.Arrays.fill(rowSum, 0f)
        java.util.Arrays.fill(rowCnt, 0)

        for (y in 0 until h step 2) {
            val base = y * rowStride
            // 列均值（128 桶）
            for (b in 0 until SIGNAL_LEN) {
                val x0 = b * w / SIGNAL_LEN
                val x1 = (b + 1) * w / SIGNAL_LEN
                var s = 0
                var n = 0
                var x = x0
                while (x < x1) {
                    s += buf.get(base + x).toInt() and 0xFF
                    n++
                    x++
                }
                col[b] += s / n.toFloat()
            }
            // 行均值桶（同一行整行采样 → 落入 y 对应桶）
            val rb = y * SIGNAL_LEN / h
            var rs = 0
            var rn = 0
            var x = 0
            while (x < w) {
                rs += buf.get(base + x).toInt() and 0xFF
                rn++
                x += 4
            }
            rowSum[rb] += rs / rn.toFloat()
            rowCnt[rb]++
        }
        for (b in 0 until SIGNAL_LEN) {
            row[b] = if (rowCnt[b] > 0) rowSum[b] / rowCnt[b] else 0f
        }

        // 帧间相位相关里程计
        val dxB = if (hasPrev) FourierHoming.estimateShift(col, prevCol) else 0
        val dyB = if (hasPrev) FourierHoming.estimateShift(row, prevRow) else 0
        val mbx = VIEW_WIDTH_M / SIGNAL_LEN
        val mby = VIEW_WIDTH_M * h / w.toFloat() / SIGNAL_LEN
        val dxM = dxB * mbx
        val dyM = dyB * mby

        val sigOut = col.copyOf()
        System.arraycopy(col, 0, prevCol, 0, SIGNAL_LEN)
        System.arraycopy(row, 0, prevRow, 0, SIGNAL_LEN)
        hasPrev = true
        image.close()

        // FPS 统计
        frames++
        val now = System.currentTimeMillis()
        if (now - fpsAt > 1000) {
            fps = (frames * 1000 / (now - fpsAt).coerceAtLeast(1)).toInt()
            frames = 0
            fpsAt = now
        }

        val moved = hypot(dxM, dyM)
        main.post { listener.onFrame(sigOut, dxM, dyM, moved) }
    }
}
