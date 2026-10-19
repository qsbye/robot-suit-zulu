package cn.qsbye.robot_suit_zulu.draw

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel

/**
 * 绘画状态容器：笔画列表、当前笔画、笔刷参数。
 * 对应原版 DrawARActivity 中的笔画集合管理（本地部分）。
 */
class DrawViewModel : ViewModel() {

    /** 已完成笔画（屏幕锚定在世界系，换手机姿态时由渲染层实时投影） */
    var strokes by mutableStateOf<List<Stroke>>(emptyList())
        private set

    /** 正在绘制的笔画 */
    var currentStroke by mutableStateOf<Stroke?>(null)
        private set

    // ---- 笔刷参数 ----
    var brushColor by mutableStateOf(Color(0xFFFFFFFF))
    var brushWidthMeters by mutableStateOf(0.015f)
    var drawDistanceMeters by mutableStateOf(1.5f)

    /** 触摸点 -> 世界点的指数平滑（对应原版 animationFilter 的作用） */
    private var lastRawPoint: FloatArray? = null

    fun beginStroke(r: FloatArray, x: Float, y: Float, w: Float, h: Float) {
        val point = Projection.screenPointToWorld(x, y, w, h, r, drawDistanceMeters)
        lastRawPoint = point
        currentStroke = Stroke(brushColor.toArgb().toLong() and 0xFFFFFFFFL, brushWidthMeters)
            .apply { addPoint(point) }
    }

    fun extendStroke(r: FloatArray, x: Float, y: Float, w: Float, h: Float) {
        val stroke = currentStroke ?: return
        val raw = Projection.screenPointToWorld(x, y, w, h, r, drawDistanceMeters)
        // 一阶低通：display = lerp(prev, raw, 0.5)，抑制手抖产生的锯齿
        val prev = lastRawPoint ?: raw
        val smooth = FloatArray(3) { i -> prev[i] * 0.5f + raw[i] * 0.5f }
        lastRawPoint = smooth
        stroke.addPoint(smooth)
    }

    fun endStroke() {
        val stroke = currentStroke ?: return
        stroke.finish()
        strokes = strokes + stroke
        currentStroke = null
        lastRawPoint = null
    }

    /** 撤销最后一笔（原版 undo_button） */
    fun undo() {
        if (strokes.isNotEmpty()) strokes = strokes.dropLast(1)
    }

    /** 清除全部（原版 menu_item_clear） */
    fun clear() {
        strokes = emptyList()
    }
}
