package cn.qsbye.robot_suit_zulu.depth

import kotlin.math.abs
import kotlin.math.sqrt

/**
 * 逐格特征提取：把 w×h 亮度图（0..1）转为每格 FEATURES 维特征。
 * 合成训练数据与相机帧走同一管线，保证训练/推理特征分布一致——
 * 这是对消音器「确定性特征交给信号处理，网络只学小事」范式的沿用。
 *
 * 特征（8 维，全部有界）：
 *  0: x 归一化横坐标
 *  1: y 归一化纵坐标（地面透视/天际线位置的关键线索）
 *  2: 亮度
 *  3: |∂I/∂x| 中心差分
 *  4: |∂I/∂y| 中心差分
 *  5: 3×3 盒滤波亮度（多尺度平滑）
 *  6: 平滑场梯度模（物体边界的强线索）
 *  7: 3×3 局部标准差（纹理/边缘强度）
 */
object FeatureGrid {

    const val FEATURES = 8

    /**
     * 计算 flat 特征数组（布局 out[outOffset + (y*w+x)*FEATURES + k]）。
     * @param lum 亮度图，行优先，取值 0..1
     * @param out 输出数组（可大于一张图，配合 outOffset 写入数据集）
     */
    fun compute(lum: FloatArray, w: Int, h: Int, out: FloatArray, outOffset: Int = 0) {
        require(out.size >= outOffset + w * h * FEATURES)
        // 3×3 盒滤波亮度
        val blur = FloatArray(w * h)
        for (y in 0 until h) {
            val y0 = (y - 1).coerceAtLeast(0)
            val y1 = (y + 1).coerceAtMost(h - 1)
            for (x in 0 until w) {
                val x0 = (x - 1).coerceAtLeast(0)
                val x1 = (x + 1).coerceAtMost(w - 1)
                var s = 0f
                for (yy in y0..y1) {
                    val row = yy * w
                    for (xx in x0..x1) s += lum[row + xx]
                }
                blur[y * w + x] = s / ((y1 - y0 + 1) * (x1 - x0 + 1))
            }
        }
        // 平滑场梯度
        val bgx = FloatArray(w * h)
        val bgy = FloatArray(w * h)
        for (y in 0 until h) {
            for (x in 0 until w) {
                val xm = (x - 1).coerceAtLeast(0)
                val xp = (x + 1).coerceAtMost(w - 1)
                val ym = (y - 1).coerceAtLeast(0)
                val yp = (y + 1).coerceAtMost(h - 1)
                bgx[y * w + x] = (blur[y * w + xp] - blur[y * w + xm]) * 0.5f
                bgy[y * w + x] = (blur[yp * w + x] - blur[ym * w + x]) * 0.5f
            }
        }

        val w1 = w - 1f
        val h1 = h - 1f
        for (y in 0 until h) {
            for (x in 0 until w) {
                val idx = y * w + x
                val xm = (x - 1).coerceAtLeast(0)
                val xp = (x + 1).coerceAtMost(w - 1)
                val ym = (y - 1).coerceAtLeast(0)
                val yp = (y + 1).coerceAtMost(h - 1)
                // 3×3 局部均值与方差
                var s = 0f
                var s2 = 0f
                var n = 0
                for (yy in ym..yp) {
                    val row = yy * w
                    for (xx in xm..xp) {
                        val v = lum[row + xx]
                        s += v
                        s2 += v * v
                        n++
                    }
                }
                val mean = s / n
                val variance = (s2 / n - mean * mean).coerceAtLeast(0f)

                val o = outOffset + idx * FEATURES
                out[o + 0] = x / w1
                out[o + 1] = y / h1
                out[o + 2] = lum[idx]
                out[o + 3] = abs(lum[y * w + xp] - lum[y * w + xm]) * 0.5f
                out[o + 4] = abs(lum[yp * w + x] - lum[ym * w + x]) * 0.5f
                out[o + 5] = blur[idx]
                out[o + 6] = sqrt(bgx[idx] * bgx[idx] + bgy[idx] * bgy[idx])
                out[o + 7] = sqrt(variance)
            }
        }
    }
}
