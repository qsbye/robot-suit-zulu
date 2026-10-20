package cn.qsbye.robot_suit_zulu.quickjseditor.draw

import android.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

class DrawStringCommand(
    private val x: Double,
    private val y: Double,
    private val text: String,
    private val fontSize: Double,
    private val color: Any?
) : DrawingCommand {

    override fun draw(scope: DrawScope, scale: Float, offsetX: Float, offsetY: Float) {
        val composeColor = CoordinateMapper.parseColor(color)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = android.graphics.Color.argb(
                (composeColor.alpha * 255).toInt(),
                (composeColor.red * 255).toInt(),
                (composeColor.green * 255).toInt(),
                (composeColor.blue * 255).toInt()
            )
            textSize = CoordinateMapper.mapLen(fontSize, scale)
        }
        scope.drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(
                text,
                CoordinateMapper.mapX(x, scale, offsetX),
                CoordinateMapper.mapY(y, scale, offsetY),
                paint
            )
        }
    }
}
