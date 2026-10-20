package cn.qsbye.robot_suit_zulu.quickjseditor.draw

import androidx.compose.ui.graphics.drawscope.DrawScope

interface DrawingCommand {
    fun draw(scope: DrawScope, scale: Float, offsetX: Float, offsetY: Float)
}
