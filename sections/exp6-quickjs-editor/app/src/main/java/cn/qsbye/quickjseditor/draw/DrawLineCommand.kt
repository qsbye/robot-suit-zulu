package cn.qsbye.quickjseditor.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

class DrawLineCommand(
    private val x1: Double,
    private val y1: Double,
    private val x2: Double,
    private val y2: Double,
    private val thickness: Double,
    private val color: Any?
) : DrawingCommand {

    override fun draw(scope: DrawScope, scale: Float, offsetX: Float, offsetY: Float) {
        scope.drawLine(
            color = CoordinateMapper.parseColor(color),
            start = Offset(
                CoordinateMapper.mapX(x1, scale, offsetX),
                CoordinateMapper.mapY(y1, scale, offsetY)
            ),
            end = Offset(
                CoordinateMapper.mapX(x2, scale, offsetX),
                CoordinateMapper.mapY(y2, scale, offsetY)
            ),
            strokeWidth = CoordinateMapper.mapLen(thickness, scale)
        )
    }
}
