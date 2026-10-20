package cn.qsbye.robot_suit_zulu.quickjseditor.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

class DrawCircleCommand(
    private val cx: Double,
    private val cy: Double,
    private val diameter: Double,
    private val thickness: Double,
    private val color: Any?
) : DrawingCommand {

    override fun draw(scope: DrawScope, scale: Float, offsetX: Float, offsetY: Float) {
        scope.drawCircle(
            color = CoordinateMapper.parseColor(color),
            radius = CoordinateMapper.mapLen(diameter / 2.0, scale),
            center = Offset(
                CoordinateMapper.mapX(cx, scale, offsetX),
                CoordinateMapper.mapY(cy, scale, offsetY)
            ),
            style = Stroke(width = CoordinateMapper.mapLen(thickness, scale))
        )
    }
}
