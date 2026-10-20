package cn.qsbye.quickjseditor.draw

import androidx.compose.ui.graphics.Color

object CoordinateMapper {

    fun mapX(x: Double, scale: Float, offsetX: Float): Float =
        (x * scale + offsetX).toFloat()

    fun mapY(y: Double, scale: Float, offsetY: Float): Float =
        (y * scale + offsetY).toFloat()

    fun mapLen(len: Double, scale: Float): Float =
        (len * scale).toFloat().coerceAtLeast(1f)

    /**
     * 解析脚本传入的颜色：支持 Int/Long/Double（如 0xFF0000）、
     * String（"#FF0000" 或 "FF0000"），解析失败默认白色。
     * 数值约定为 RGB，高位补不透明 alpha。
     */
    fun parseColor(color: Any?): Color {
        return when (color) {
            is Int -> Color(0xFF000000L or (color.toLong() and 0xFFFFFFL))
            is Long -> Color(0xFF000000L or (color and 0xFFFFFFL))
            is Double -> Color(0xFF000000L or (color.toLong() and 0xFFFFFFL))
            is Float -> Color(0xFF000000L or (color.toLong() and 0xFFFFFFL))
            is String -> parseColorString(color)
            null -> Color.White
            else -> Color.White
        }
    }

    private fun parseColorString(s: String): Color {
        val hex = s.trim().removePrefix("#")
        return try {
            when (hex.length) {
                6 -> Color(0xFF000000L or (hex.toLong(16) and 0xFFFFFFL))
                8 -> Color(hex.toLong(16))
                else -> Color.White
            }
        } catch (e: Exception) {
            Color.White
        }
    }
}
