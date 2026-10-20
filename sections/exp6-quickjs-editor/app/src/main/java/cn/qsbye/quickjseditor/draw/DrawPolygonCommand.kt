package cn.qsbye.quickjseditor.draw

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * points: [x1, y1, x2, y2, ...]，奇数位坐标自动闭合为折线（不自动首尾相连）。
 */
class DrawPolygonCommand(
    private val points: DoubleArray,
    private val thickness: Double,
    private val color: Any?
) : DrawingCommand {

    override fun draw(scope: DrawScope, scale: Float, offsetX: Float, offsetY: Float) {
        if (points.size < 4) return
        val path = Path()
        path.moveTo(
            CoordinateMapper.mapX(points[0], scale, offsetX),
            CoordinateMapper.mapY(points[1], scale, offsetY)
        )
        var i = 2
        while (i + 1 < points.size) {
            path.lineTo(
                CoordinateMapper.mapX(points[i], scale, offsetX),
                CoordinateMapper.mapY(points[i + 1], scale, offsetY)
            )
            i += 2
        }
        scope.drawPath(
            path = path,
            color = CoordinateMapper.parseColor(color),
            style = Stroke(width = CoordinateMapper.mapLen(thickness, scale))
        )
    }
}
