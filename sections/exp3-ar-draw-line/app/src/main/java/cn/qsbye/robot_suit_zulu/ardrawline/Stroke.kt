package cn.qsbye.robot_suit_zulu.ardrawline

import kotlin.math.sqrt

/**
 * 笔画数据模型（对应逆向报告中 b.e / Stroke 的本地部分：
 * points + lineWidth + finished + totalLength + finishStroke 的微小笔画处理）。
 *
 * points 存世界系 3D 坐标；与原版不同，不含 Firebase 引用——
 * FOSS 版为纯单机，无云端同步。
 */
class Stroke(val colorArgb: Long, val widthMeters: Float) {

    val points = ArrayList<FloatArray>()

    /** 结束标志（原版 finished 字段） */
    var finished = false
        private set

    /** 加入世界系点；与上一点间距过小时丢弃，避免点数爆炸 */
    fun addPoint(world: FloatArray) {
        points.lastOrNull()?.let { last ->
            val dx = world[0] - last[0]
            val dy = world[1] - last[1]
            val dz = world[2] - last[2]
            if (dx * dx + dy * dy + dz * dz < MIN_SPACING * MIN_SPACING) return
        }
        points.add(world.copyOf())
    }

    /** 总长度（米），原版 totalLength 逻辑 */
    fun totalLength(): Float {
        var length = 0f
        for (i in 0 until points.size - 1) {
            val a = points[i]
            val b = points[i + 1]
            val dx = a[0] - b[0]
            val dy = a[1] - b[1]
            val dz = a[2] - b[2]
            length += sqrt(dx * dx + dy * dy + dz * dz)
        }
        return length
    }

    /**
     * 结束笔画，复刻原版 finishStroke 对微小笔画的处理：
     * 总长 < 1cm 时，多点笔画只保留首尾，单点笔画微抬 0.5mm 使其可见。
     */
    fun finish() {
        finished = true
        if (totalLength() >= 0.01f) return
        when {
            points.size > 2 -> {
                val first = points.first()
                val last = points.last()
                points.clear()
                points.add(first)
                points.add(last)
            }
            points.size == 1 -> points[0][1] += 5.0E-4f
        }
    }

    companion object {
        /** 最小采样间距（米）：1.5cm，兼顾平滑与点数 */
        const val MIN_SPACING = 0.015f
    }
}
