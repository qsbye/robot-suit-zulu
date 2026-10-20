package cn.qsbye.quickjseditor.script

import cn.qsbye.quickjseditor.draw.DrawCircleCommand
import cn.qsbye.quickjseditor.draw.DrawLineCommand
import cn.qsbye.quickjseditor.draw.DrawPointCommand
import cn.qsbye.quickjseditor.draw.DrawPolygonCommand
import cn.qsbye.quickjseditor.draw.DrawStringCommand
import cn.qsbye.quickjseditor.draw.DrawingCommand

/**
 * Cognex 风格 Plot* 绘图 API 的收集器：脚本每调用一次 PlotXXX，
 * 就生成一条 DrawingCommand 供 Compose Canvas 回放。
 * imgW/imgH 为视觉坐标系尺寸（如 5120x5120），缩放/平移在绘制端完成。
 */
class DrawingApi {

    private val commands = mutableListOf<DrawingCommand>()

    fun plotLine(
        imgW: Int, imgH: Int,
        x1: Double, y1: Double, x2: Double, y2: Double,
        thickness: Double, color: Any?
    ) {
        commands.add(DrawLineCommand(x1, y1, x2, y2, thickness, color))
    }

    fun plotCircle(
        imgW: Int, imgH: Int,
        cx: Double, cy: Double, diameter: Double,
        thickness: Double, color: Any?
    ) {
        commands.add(DrawCircleCommand(cx, cy, diameter, thickness, color))
    }

    fun plotString(
        imgW: Int, imgH: Int,
        x: Double, y: Double, text: String,
        fontSize: Int, color: Any?
    ) {
        commands.add(DrawStringCommand(x, y, text, fontSize.toDouble(), color))
    }

    fun plotPolygon(
        imgW: Int, imgH: Int,
        points: DoubleArray,
        thickness: Double, color: Any?
    ) {
        commands.add(DrawPolygonCommand(points, thickness, color))
    }

    fun plotPoint(
        imgW: Int, imgH: Int,
        x: Double, y: Double,
        thickness: Double, color: Any?
    ) {
        commands.add(DrawPointCommand(x, y, thickness, color))
    }

    fun clear() {
        commands.clear()
    }

    fun getCommands(): List<DrawingCommand> = commands.toList()
}
