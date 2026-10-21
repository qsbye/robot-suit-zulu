package cn.qsbye.hummingbird.minimap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.math.floor

/**
 * 瓦片化离屏轨迹缓存（类似 Minecraft 小地图插件的瓦片渲染）。
 *
 * 世界系：x 向东、y 向北（单位米），原点 = 开始建图处。
 * 每块瓦片覆盖 tileMeters × tileMeters 的世界区域，位图分辨率 tilePx × tilePx（北向上）。
 * 轨迹按里程计增量画入瓦片后不再重绘；视图层每帧只做平移/缩放采样（drawImage）。
 */
class MinimapTileCache(
    val tileMeters: Float = 1f,
    val tilePx: Int = 128,
) {
    val pxPerMeter = tilePx / tileMeters

    private class Tile(bitmap: Bitmap) {
        val image: ImageBitmap = bitmap.asImageBitmap()
        val canvas = Canvas(bitmap)
    }

    private val tiles = HashMap<Long, Tile>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    /** 增量绘制一段轨迹（世界坐标，米；argb 为 ARGB 整数色） */
    fun drawSegment(x0: Float, y0: Float, x1: Float, y1: Float, argb: Int, widthMeters: Float) {
        paint.strokeWidth = (widthMeters * pxPerMeter).coerceAtLeast(1.5f)
        paint.color = argb
        val minX = minOf(x0, x1)
        val maxX = maxOf(x0, x1)
        val minY = minOf(y0, y1)
        val maxY = maxOf(y0, y1)
        val txStart = floor(minX / tileMeters).toInt()
        val txEnd = floor(maxX / tileMeters).toInt()
        val tyStart = floor(minY / tileMeters).toInt()
        val tyEnd = floor(maxY / tileMeters).toInt()
        for (tx in txStart..txEnd) {
            for (ty in tyStart..tyEnd) {
                val t = tile(tx, ty)
                val left = tx * tileMeters   // 瓦片西缘
                val top = (ty + 1) * tileMeters // 瓦片北缘
                t.canvas.save()
                t.canvas.clipRect(0f, 0f, tilePx.toFloat(), tilePx.toFloat())
                t.canvas.drawLine(
                    (x0 - left) * pxPerMeter, (top - y0) * pxPerMeter,
                    (x1 - left) * pxPerMeter, (top - y1) * pxPerMeter,
                    paint
                )
                t.canvas.restore()
            }
        }
    }

    /** 遍历所有已分配瓦片（视图层采样用） */
    fun forEach(action: (tx: Int, ty: Int, image: ImageBitmap) -> Unit) {
        for ((k, t) in tiles) {
            action((k shr 32).toInt(), (k and 0xffffffffL).toInt(), t.image)
        }
    }

    /** 清空全部瓦片 */
    fun clear() {
        tiles.clear()
    }

    private fun tile(tx: Int, ty: Int): Tile {
        val key = (tx.toLong() shl 32) or (ty.toLong() and 0xffffffffL)
        return tiles.getOrPut(key) {
            Tile(Bitmap.createBitmap(tilePx, tilePx, Bitmap.Config.ARGB_8888))
        }
    }
}
