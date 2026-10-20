package cn.qsbye.quickjseditor.draw

import androidx.compose.ui.graphics.drawscope.DrawScope

interface DrawingCommand {
    fun draw(scope: DrawScope, scale: Float, offsetX: Float, offsetY: Float)
}
