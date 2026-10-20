package cn.qsbye.quickjseditor.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

class DrawPointCommand(
    private val x: Double,
    private val y: Double,
    private val thickness: Double,
    private val color: Any?
) : DrawingCommand {

    override fun draw(scope: DrawScope, scale: Float, offsetX: Float, offsetY: Float) {
        scope.drawCircle(
            color = CoordinateMapper.parseColor(color),
            radius = CoordinateMapper.mapLen(thickness / 2.0, scale),
            center = Offset(
                CoordinateMapper.mapX(x, scale, offsetX),
                CoordinateMapper.mapY(y, scale, offsetY)
            )
        )
    }
}
