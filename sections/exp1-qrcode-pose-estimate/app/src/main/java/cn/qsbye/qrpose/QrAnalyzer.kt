package cn.qsbye.qrpose

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import java.util.EnumMap

/**
 * 检出二维码的四角点坐标（"摆正后"帧画面，像素）。
 * 四点顺时针（左上/右上/右下/左下），跟随二维码的旋转；
 * ZXing 仅返回 3 个定位图案中心，第 4 角按仿射（平行四边形）模型推算，
 * frameWidth/frameHeight 供 FILL_CENTER 预览映射使用。
 */
data class QrQuad(
    /** [x0,y0, x1,y1, x2,y2, x3,y3]，顺序：左上/右上/右下/左下 */
    val points: FloatArray,
    val frameWidth: Int,
    val frameHeight: Int,
)

/**
 * CameraX 帧分析器：ZXing 解出二维码后回调检出四边形（null = 本帧未检出）。
 * 识别方法参照 WaveSend（wave-send-zulu-air-fountain-android）的 QrAnalyzer，
 * 这里简化为「Hybrid → Global 直方图」两级候选（打印/贴纸码场景足够，
 * 屏幕拍摄场景的 Sauvola 兜底可后续按原实现补齐）。
 * STRATEGY_KEEP_ONLY_LATEST：检测慢于帧率时直接丢旧帧。
 *
 * 注：为满足 FOSS 依赖要求，使用 ZXing（Apache-2.0）而非 Google ML Kit（专有）。
 */
class QrAnalyzer(
    private val onQuad: (QrQuad?) -> Unit,
) : ImageAnalysis.Analyzer {

    private val reader = QRCodeReader()
    private val hints: Map<DecodeHintType, Any> =
        EnumMap<DecodeHintType, Any>(DecodeHintType::class.java).apply {
            put(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
            put(DecodeHintType.TRY_HARDER, true)
        }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        try {
            val mediaImage = imageProxy.image ?: return
            val rotation = imageProxy.imageInfo.rotationDegrees
            val srcW = mediaImage.width
            val srcH = mediaImage.height
            // 摆正后的帧尺寸（旋转 90/270 时宽高互换），四边形坐标基于该空间
            val uprightW = if (rotation % 180 == 0) srcW else srcH
            val uprightH = if (rotation % 180 == 0) srcH else srcW

            val data = extractLuma(imageProxy, srcW, srcH)

            // ZXing 仅给出 3 个定位图案中心，旋转到摆正坐标系后推算第 4 角
            val result = decode(data, srcW, srcH)
            val srcPoints = result?.resultPoints ?: emptyArray()
            val quad: QrQuad? = if (srcPoints.size >= 3) {
                val rotated = srcPoints.take(3).map { rotatePoint(it.x, it.y, srcW, srcH, rotation) }
                buildQuad(rotated, uprightW, uprightH)
            } else null
            onQuad(quad)
        } catch (_: Exception) {
            onQuad(null)
        } finally {
            imageProxy.close()
            reader.reset()
        }
    }

    /** 提取 Y 平面亮度（处理行/像素步长，逐字节紧缩拷贝） */
    private fun extractLuma(imageProxy: ImageProxy, w: Int, h: Int): ByteArray {
        val yPlane = imageProxy.planes[0]
        val rowStride = yPlane.rowStride
        val pixelStride = yPlane.pixelStride
        val buffer = yPlane.buffer
        return when {
            pixelStride == 1 && rowStride == w ->
                ByteArray(buffer.remaining()).also { buffer.get(it) }
            pixelStride == 1 -> {
                val tight = ByteArray(w * h)
                val row = ByteArray(w)
                for (y in 0 until h) {
                    buffer.position(y * rowStride)
                    buffer.get(row)
                    System.arraycopy(row, 0, tight, y * w, w)
                }
                tight
            }
            else -> {
                val tight = ByteArray(w * h)
                for (y in 0 until h) {
                    buffer.position(y * rowStride)
                    for (x in 0 until w) tight[y * w + x] = buffer.get()
                }
                tight
            }
        }
    }

    /** 两级候选解码：Hybrid 分块二值化 → Global 全局直方图 */
    private fun decode(data: ByteArray, w: Int, h: Int): com.google.zxing.Result? {
        val src = PlanarYUVLuminanceSource(data, w, h, 0, 0, w, h, false)
        try {
            return reader.decode(BinaryBitmap(HybridBinarizer(src)), hints)
        } catch (_: Exception) { }
        try {
            return reader.decode(BinaryBitmap(GlobalHistogramBinarizer(src)), hints)
        } catch (_: Exception) { }
        return null
    }

    /** 将传感器原始坐标 (x,y) 旋转到摆正坐标系。 */
    private fun rotatePoint(x: Float, y: Float, w: Int, h: Int, rotation: Int): Pair<Float, Float> =
        when (rotation) {
            90 -> (h - 1 - y) to x
            180 -> (w - 1 - x) to (h - 1 - y)
            270 -> y to (w - 1 - x)
            else -> x to y
        }

    /**
     * 由 3 个定位图案中心构建 4 角四边形。
     * 三个定位图案位于左上、右上、左下；先找出直角点（左上），
     * 再按 y 区分右上 / 左下，第 4 角（右下）按平行四边形模型推算。
     */
    private fun buildQuad(points: List<Pair<Float, Float>>, fw: Int, fh: Int): QrQuad? {
        val (a, b, c) = points
        // 两两距离平方，最远的一对为对角线（右上↔左下），第三点即左上
        val dab = dist2(a, b)
        val dac = dist2(a, c)
        val dbc = dist2(b, c)
        val topLeft: Pair<Float, Float>
        val p1: Pair<Float, Float>
        val p2: Pair<Float, Float>
        when {
            dab >= dac && dab >= dbc -> { topLeft = c; p1 = a; p2 = b }
            dac >= dab && dac >= dbc -> { topLeft = b; p1 = a; p2 = c }
            else -> { topLeft = a; p1 = b; p2 = c }
        }
        // y 较小者为右上，较大者为左下（图像坐标 y 向下增长）
        val (topRight, bottomLeft) = if (p1.second <= p2.second) p1 to p2 else p2 to p1
        val bottomRight = (topRight.first + (bottomLeft.first - topLeft.first)) to
            (topRight.second + (bottomLeft.second - topLeft.second))

        val pts = listOf(topLeft, topRight, bottomRight, bottomLeft)
        return QrQuad(
            points = pts.flatMap { listOf(it.first, it.second) }.toFloatArray(),
            frameWidth = fw,
            frameHeight = fh,
        )
    }

    private fun dist2(p: Pair<Float, Float>, q: Pair<Float, Float>): Float {
        val dx = p.first - q.first
        val dy = p.second - q.second
        return dx * dx + dy * dy
    }
}
