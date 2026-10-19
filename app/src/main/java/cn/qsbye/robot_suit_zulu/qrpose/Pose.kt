package cn.qsbye.robot_suit_zulu.qrpose

import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * 二维码位姿估计与路径生成（纯 Kotlin，无 OpenCV）。
 *
 * 方法（对应 slambook2 ch5/ch7 的单应分解）：
 *  - 世界系：二维码平面 z=0，中心为原点，边长 [CODE_SIZE_M] 米
 *  - 由四角像素 ↔ 平面方形的 4 组对应，DLT（h33=1，8x8 高斯消元）求单应 H
 *  - 归一化图像坐标下 K=I，故 H = [r1 r2 t]：λ 归一 +  Gram-Schmidt 正交化得 R、t
 *  - 相机系为图像系（x 右、y 下、z 前），z 前方为正；t 即二维码中心相对相机的位置
 *
 * 简化说明：角点来自 ZXing 定位图案中心（非码面真实角点），位姿精度为演示级；
 * 内参用固定视场角近似（与 exp3 相同的约定）。
 */
object Pose {

    /** 二维码物理边长（米），按实际打印尺寸调整 */
    const val CODE_SIZE_M = 0.10f

    /** 后置相机水平视场角近似值 */
    const val H_FOV_DEG = 62f

    /** 位姿：t=平移（米，相机系），r=3x3 行优先旋转（码系->相机系） */
    class Pose6(val t: FloatArray, val r: FloatArray)

    fun focalPx(frameW: Int): Float =
        (frameW / 2f) / tan(Math.toRadians(H_FOV_DEG / 2.0)).toFloat()

    /**
     * 由四角（upright 帧像素，顺序 左上/右上/右下/左下）估计位姿。
     * 检出失败或退化时返回 null。
     */
    fun estimate(corners: FloatArray, frameW: Int, frameH: Int): Pose6? {
        val f = focalPx(frameW).toDouble()
        val cx = frameW / 2.0
        val cy = frameH / 2.0
        // 归一化图像点
        val img = Array(4) { i ->
            doubleArrayOf((corners[2 * i] - cx) / f, (corners[2 * i + 1] - cy) / f)
        }
        val s = CODE_SIZE_M / 2.0
        val obj = arrayOf(
            doubleArrayOf(-s, -s), doubleArrayOf(s, -s),
            doubleArrayOf(s, s), doubleArrayOf(-s, s),
        )
        val h = homography(obj, img) ?: return null

        // 分解 H = [r1 r2 t]（K=I）
        val l1 = norm3(h[0][0], h[1][0], h[2][0])
        val l2 = norm3(h[0][1], h[1][1], h[2][1])
        if (l1 < 1e-12 || l2 < 1e-12) return null
        val lam = 2.0 / (l1 + l2)
        var r1 = doubleArrayOf(h[0][0], h[1][0], h[2][0]).map { it * lam }
        var r2 = doubleArrayOf(h[0][1], h[1][1], h[2][1]).map { it * lam }
        var t = doubleArrayOf(h[0][2], h[1][2], h[2][2]).map { it * lam }
        // 码面必须位于相机前方（z>0），否则整体取反
        if (t[2] < 0) {
            r1 = r1.map { -it }; r2 = r2.map { -it }; t = t.map { -it }
        }
        // Gram-Schmidt 正交化
        val r1n = normalize(r1)
        val d = dot(r1n, r2)
        val r2o = DoubleArray(3) { r2[it] - d * r1n[it] }
        val r2n = normalize(r2o.toList())
        val r3 = cross(r1n, r2n)

        return Pose6(
            t = floatArrayOf(t[0].toFloat(), t[1].toFloat(), t[2].toFloat()),
            r = floatArrayOf(
                r1n[0].toFloat(), r2n[0].toFloat(), r3[0].toFloat(),
                r1n[1].toFloat(), r2n[1].toFloat(), r3[1].toFloat(),
                r1n[2].toFloat(), r2n[2].toFloat(), r3[2].toFloat(),
            ),
        )
    }

    /**
     * 生成相机系下的三次贝塞尔路径：从镜头前方 (0,0,0.2) 平滑延伸到二维码中心，
     * 中间控制点贴合目标高度，曲线视觉上始终"指向并延伸至"二维码。
     */
    fun bezierPath(t: FloatArray, samples: Int = 40): FloatArray {
        val p0 = doubleArrayOf(0.0, 0.0, 0.2)
        val p1 = doubleArrayOf(0.0, t[1] * 0.5, t[2] * 0.35)
        val p2 = doubleArrayOf(t[0].toDouble(), t[1].toDouble(), t[2] * 0.7)
        val p3 = doubleArrayOf(t[0].toDouble(), t[1].toDouble(), t[2].toDouble())
        val out = FloatArray(samples * 3)
        for (i in 0 until samples) {
            val u = i / (samples - 1.0)
            val v = 1.0 - u
            val b0 = v * v * v
            val b1 = 3 * v * v * u
            val b2 = 3 * v * u * u
            val b3 = u * u * u
            out[3 * i] = (b0 * p0[0] + b1 * p1[0] + b2 * p2[0] + b3 * p3[0]).toFloat()
            out[3 * i + 1] = (b0 * p0[1] + b1 * p1[1] + b2 * p2[1] + b3 * p3[1]).toFloat()
            out[3 * i + 2] = (b0 * p0[2] + b1 * p1[2] + b2 * p2[2] + b3 * p3[2]).toFloat()
        }
        return out
    }

    /** 相机系 3D 点 -> 帧像素坐标；z<=近裁剪面返回 null */
    fun project(p: FloatArray, frameW: Int, frameH: Int): FloatArray? {
        if (p[2] < 0.05f) return null
        val f = focalPx(frameW)
        return floatArrayOf(
            frameW / 2f + p[0] * f / p[2],
            frameH / 2f + p[1] * f / p[2],
        )
    }

    // ---- 小工具 ----

    private fun norm3(a: Double, b: Double, c: Double) = sqrt(a * a + b * b + c * c)

    private fun normalize(v: List<Double>): DoubleArray {
        val n = norm3(v[0], v[1], v[2])
        return doubleArrayOf(v[0] / n, v[1] / n, v[2] / n)
    }

    private fun dot(a: DoubleArray, b: List<Double>) = a[0] * b[0] + a[1] * b[1] + a[2] * b[2]

    private fun cross(a: DoubleArray, b: DoubleArray) = doubleArrayOf(
        a[1] * b[2] - a[2] * b[1],
        a[2] * b[0] - a[0] * b[2],
        a[0] * b[1] - a[1] * b[0],
    )

    /**
     * DLT 求单应（h33 固定为 1，8x8 高斯消元 + 部分主元）。
     * obj: 平面点 (X,Y)；img: 归一化图像点 (x,y)。
     */
    private fun homography(obj: Array<DoubleArray>, img: Array<DoubleArray>): Array<DoubleArray>? {
        val n = 8
        val a = Array(n) { DoubleArray(n + 1) }
        for (i in 0 until 4) {
            val (X, Y) = obj[i]
            val (x, y) = img[i]
            // -X -Y -1  0  0  0  xX xY | -x
            a[2 * i] = doubleArrayOf(-X, -Y, -1.0, 0.0, 0.0, 0.0, x * X, x * Y, -x)
            //  0  0  0 -X -Y -1  yX yY | -y
            a[2 * i + 1] = doubleArrayOf(0.0, 0.0, 0.0, -X, -Y, -1.0, y * X, y * Y, -y)
        }
        // 高斯消元
        for (col in 0 until n) {
            var pivot = col
            for (row in col + 1 until n) {
                if (abs(a[row][col]) > abs(a[pivot][col])) pivot = row
            }
            if (abs(a[pivot][col]) < 1e-12) return null
            if (pivot != col) {
                val tmp = a[pivot]; a[pivot] = a[col]; a[col] = tmp
            }
            for (row in col + 1 until n) {
                val factor = a[row][col] / a[col][col]
                for (k in col until n + 1) a[row][k] -= factor * a[col][k]
            }
        }
        val x = DoubleArray(n)
        for (row in n - 1 downTo 0) {
            var s = a[row][n]
            for (k in row + 1 until n) s -= a[row][k] * x[k]
            x[row] = s / a[row][row]
        }
        return arrayOf(
            doubleArrayOf(x[0], x[1], x[2]),
            doubleArrayOf(x[3], x[4], x[5]),
            doubleArrayOf(x[6], x[7], 1.0),
        )
    }
}
