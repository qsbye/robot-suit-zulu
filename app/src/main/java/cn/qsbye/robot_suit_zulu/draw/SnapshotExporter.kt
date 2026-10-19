package cn.qsbye.robot_suit_zulu.draw

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import cn.qsbye.robot_suit_zulu.draw.Projection
import cn.qsbye.robot_suit_zulu.draw.Stroke
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * 快照导出：把当前笔画按最新姿态重投影到深色底 Bitmap，存入系统相册。
 * （FOSS 版用 PNG 导出替代原版的 RecordableSurfaceView 视频录制。）
 */
object SnapshotExporter {

    private const val BG_COLOR = 0xFF0E1116.toInt()
    private const val MAX_WIDTH_PX = 600f

    /** 用同一套投影数学离屏渲染笔画 */
    private fun renderStrokes(
        strokes: List<Stroke>, r: FloatArray, width: Int, height: Int,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(BG_COLOR)
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        val wf = width.toFloat()
        val hf = height.toFloat()
        val focal = Projection.focalPx(wf)
        for (stroke in strokes) {
            paint.color = stroke.colorArgb.toInt()
            if (stroke.points.size == 1) {
                val p = Projection.worldToScreen(stroke.points[0], r, wf, hf)
                if (p != null) {
                    paint.style = Paint.Style.FILL
                    canvas.drawCircle(
                        p[0], p[1],
                        (stroke.widthMeters * focal / p[2]).coerceAtMost(MAX_WIDTH_PX) / 2f,
                        paint,
                    )
                    paint.style = Paint.Style.STROKE
                }
                continue
            }
            for (i in 1 until stroke.points.size) {
                val a = Projection.worldToScreen(stroke.points[i - 1], r, wf, hf)
                val b = Projection.worldToScreen(stroke.points[i], r, wf, hf)
                if (a == null || b == null) continue
                paint.strokeWidth = strokeWidthPx(stroke.widthMeters, focal, (a[2] + b[2]) / 2f)
                canvas.drawLine(a[0], a[1], b[0], b[1], paint)
            }
        }
        // 水印
        val text = Paint().apply {
            color = 0x66FFFFFF.toInt()
            textSize = 26f
            isAntiAlias = true
        }
        canvas.drawText(
            "RobotSuitZulu · AR Draw Line (FOSS)", 24f, height - 30f, text,
        )
        return bitmap
    }

    private fun strokeWidthPx(widthMeters: Float, focalPx: Float, depth: Float): Float =
        (widthMeters * focalPx / depth).coerceIn(1.5f, MAX_WIDTH_PX)

    /** 写入系统相册 Pictures/RobotSuitZulu-AR，返回提示文案 */
    fun export(context: Context, strokes: List<Stroke>, r: FloatArray, aspectWH: Float) {
        if (strokes.isEmpty()) {
            Toast.makeText(context, "还没有笔画可以保存", Toast.LENGTH_SHORT).show()
            return
        }
        val width = 1080
        val height = (width / aspectWH).roundToInt().coerceAtLeast(1)
        val bitmap = renderStrokes(strokes, r, width, height)
        val name = "ardrawline-" +
            SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date()) + ".png"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/RobotSuitZulu-AR")
            }
        }
        try {
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values,
            )
            if (uri == null) {
                Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show()
                return
            }
            context.contentResolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Toast.makeText(context, "已保存到相册 Pictures/RobotSuitZulu-AR", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "保存失败：${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
