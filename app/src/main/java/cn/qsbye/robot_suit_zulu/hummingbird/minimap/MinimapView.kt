package cn.qsbye.robot_suit_zulu.hummingbird.minimap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.cos

/**
 * 右下角常驻小地图卡片（交互参考 Xaero's Minimap）：
 * 世界系原点（开始建图处）固定居中、锁定北向，右下角叠加缩放按钮与图例。
 */
@Composable
fun MinimapCard(model: MinimapModel, modifier: Modifier = Modifier) {
    val zooms = listOf(1f, 0.5f, 0.25f)
    var zoomIdx by remember { mutableIntStateOf(1) }

    Surface(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0x55FFFFFF), RoundedCornerShape(12.dp)),
        color = Color(0xE0101820)
    ) {
        Box(Modifier.fillMaxSize()) {
            MinimapCanvas(model, zooms[zoomIdx], Modifier.fillMaxSize())

            // 北向指示
            Text(
                "N",
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(4.dp),
                color = Color(0xFF81C784),
                style = MaterialTheme.typography.labelSmall
            )

            // 缩放按钮
            Column(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                ZoomButton("+") { if (zoomIdx > 0) zoomIdx-- }
                ZoomButton("−") { if (zoomIdx < zooms.size - 1) zoomIdx++ }
            }

            // 图例
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
            ) {
                LegendRow(Color(0xFF4FC3F7), "快照")
                LegendRow(Color(0xFF81C784), "已捕获")
            }
        }
    }
}

@Composable
private fun ZoomButton(label: String, onClick: () -> Unit) {
    Text(
        label,
        Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x33FFFFFF))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 1.dp),
        color = Color.White,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun LegendRow(color: Color, label: String) {
    androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(6.dp)
                .background(color, RoundedCornerShape(3.dp))
        )
        Text(
            " $label",
            color = Color(0xB3FFFFFF),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

/**
 * 小地图画布：北向锁定、原点居中。
 * 世界→屏幕：sx = cx + x·scale，sy = cy − y·scale（北向上，y 翻转）。
 */
@Composable
fun MinimapCanvas(model: MinimapModel, zoom: Float, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val ppm = 64f * zoom * density // 视图比例：像素/米
        val cx = size.width / 2f
        val cy = size.height / 2f
        fun sx(x: Float) = cx + x * ppm
        fun sy(y: Float) = cy - y * ppm

        // 1m 网格（与瓦片边界对齐）
        val halfW = cx / ppm
        val halfH = cy / ppm
        val kx0 = floor(-halfW).toInt()
        val kx1 = floor(halfW).toInt()
        val ky0 = floor(-halfH).toInt()
        val ky1 = floor(halfH).toInt()
        for (k in kx0..kx1) {
            drawLine(
                Color(0x14FFFFFF),
                Offset(sx(k.toFloat()), sy(-halfH)),
                Offset(sx(k.toFloat()), sy(halfH)),
                1f
            )
        }
        for (k in ky0..ky1) {
            drawLine(
                Color(0x14FFFFFF),
                Offset(sx(-halfW), sy(k.toFloat())),
                Offset(sx(halfW), sy(k.toFloat())),
                1f
            )
        }

        // 瓦片层：离屏缓存位图只做平移/缩放采样，不重绘轨迹
        val tm = model.tiles.tileMeters
        val dst = tm * ppm
        model.tiles.forEach { tx, ty, img ->
            drawImage(
                image = img,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(model.tiles.tilePx, model.tiles.tilePx),
                dstOffset = IntOffset(
                    sx(tx * tm).roundToInt(),
                    sy((ty + 1) * tm).roundToInt()
                ),
                dstSize = IntSize(dst.roundToInt(), dst.roundToInt()),
                filterQuality = FilterQuality.Low
            )
        }

        // 原点十字（开始建图处）
        val o = Offset(sx(0f), sy(0f))
        val arm = 6.dp.toPx()
        drawLine(Color(0xB3FFFFFF), Offset(o.x - arm, o.y), Offset(o.x + arm, o.y), 1.5f)
        drawLine(Color(0xB3FFFFFF), Offset(o.x, o.y - arm), Offset(o.x, o.y + arm), 1.5f)

        // 快照路径点
        for (w in model.waypoints) {
            val c = Offset(sx(w.x), sy(w.y))
            val color = when (w.state) {
                Waypoint.State.PENDING -> Color(0xFF4FC3F7)
                Waypoint.State.NEXT -> Color(0xFFFFD54F)
                Waypoint.State.CAPTURED -> Color(0xFF81C784)
            }
            if (w.state == Waypoint.State.NEXT) {
                drawCircle(color, 0.45f * ppm, c, style = Stroke(1.5.dp.toPx()))
            }
            drawCircle(color, 0.16f * ppm, c)
        }

        // 当前位姿箭头（橙色三角）
        val p = model.pose
        val hx = sin(p.yaw)
        val hy = cos(p.yaw)
        val tip = Offset(sx(p.x + hx * 0.9f), sy(p.y + hy * 0.9f))
        val bx = p.x - hx * 0.2f
        val by = p.y - hy * 0.2f
        val l = Offset(sx(bx + hy * 0.35f), sy(by - hx * 0.35f))
        val r = Offset(sx(bx - hy * 0.35f), sy(by + hx * 0.35f))
        val path = Path().apply {
            moveTo(tip.x, tip.y)
            lineTo(l.x, l.y)
            lineTo(r.x, r.y)
            close()
        }
        drawPath(path, Color(0xFFFF7043))
        drawCircle(Color(0xFFFF7043), 2.dp.toPx(), Offset(sx(p.x), sy(p.y)))
    }
}
