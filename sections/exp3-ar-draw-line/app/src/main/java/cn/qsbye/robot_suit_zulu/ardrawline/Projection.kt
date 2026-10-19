package cn.qsbye.robot_suit_zulu.ardrawline

import kotlin.math.tan

/**
 * 针孔相机投影工具（magic-window 模式，替代专有 ARCore 的坐标变换）。
 *
 * 坐标系约定（对应逆向报告中的 javax.a.h Vec3 / c.d PoseHelper 职责）：
 *  - R：3x3 行优先旋转矩阵，设备坐标系 -> 世界坐标系
 *    （与 SensorManager.getRotationMatrixFromVector 输出一致，逆变换用转置）。
 *  - 世界系：传感器参考系（X≈东、Y≈北、Z≈天），会话开始即固定，
 *    笔画全部锚定在该系中——这是"线留在空气中"的原理。
 *  - 设备系：X 右、Y 上（屏幕顶部）、Z 出屏；后置相机视线方向为 -Z。
 */
object Projection {

    /** 后置相机水平视场角近似值（多数机型 55°~70°）。放置与投影共用同一值，保证自洽。 */
    const val H_FOV_DEG = 62f

    private fun tanHalfH(): Float = tan(Math.toRadians(H_FOV_DEG / 2.0)).toFloat()

    private fun tanHalfV(aspectWH: Float): Float = tanHalfH() / aspectWH

    /** 水平方向焦距（像素）：width / (2 * tan(fov/2))，用于按深度换算线宽 */
    fun focalPx(widthPx: Float): Float = (widthPx / 2f) / tanHalfH()

    /** 3x3 行优先矩阵乘向量：v' = M * v */
    private fun mulMV(m: FloatArray, v: FloatArray): FloatArray = floatArrayOf(
        m[0] * v[0] + m[1] * v[1] + m[2] * v[2],
        m[3] * v[0] + m[4] * v[1] + m[5] * v[2],
        m[6] * v[0] + m[7] * v[1] + m[8] * v[2],
    )

    /** 设备系方向 -> 世界系方向（v_world = R * v_device） */
    fun deviceToWorld(v: FloatArray, r: FloatArray): FloatArray = mulMV(r, v)

    /**
     * 屏幕触点 -> 世界系中位于 [distance] 米处的 3D 点。
     * 对应原版 DrawARActivity.onTouchEvent：屏幕坐标经 ARCore Frame 变换到世界坐标，
     * 本实现用旋转矢量 + 针孔模型完成同样的映射。
     */
    fun screenPointToWorld(
        xPx: Float, yPx: Float, widthPx: Float, heightPx: Float,
        r: FloatArray, distanceMeters: Float,
    ): FloatArray {
        val u = 2f * xPx / widthPx - 1f          // 左 -1，右 +1
        val vy = 1f - 2f * yPx / heightPx        // 上 +1（设备 Y 轴朝上）
        val dirDevice = floatArrayOf(
            u * tanHalfH(),
            vy * tanHalfV(widthPx / heightPx),
            -1f,                                 // 后置相机视线为 -Z
        )
        val len = kotlin.math.sqrt(
            dirDevice[0] * dirDevice[0] + dirDevice[1] * dirDevice[1] + dirDevice[2] * dirDevice[2],
        )
        for (i in 0..2) dirDevice[i] /= len
        val dirWorld = deviceToWorld(dirDevice, r)
        return floatArrayOf(
            dirWorld[0] * distanceMeters,
            dirWorld[1] * distanceMeters,
            dirWorld[2] * distanceMeters,
        )
    }

    /**
     * 世界系 3D 点 -> 屏幕像素坐标。
     * 返回 [sx, sy, depth]；点在相机身后或过近时返回 null（裁剪）。
     */
    fun worldToScreen(p: FloatArray, r: FloatArray, widthPx: Float, heightPx: Float): FloatArray? {
        // 逆变换：v_device = R^T * p_world（行优先转置乘）
        val q = floatArrayOf(
            r[0] * p[0] + r[3] * p[1] + r[6] * p[2],
            r[1] * p[0] + r[4] * p[1] + r[7] * p[2],
            r[2] * p[0] + r[5] * p[1] + r[8] * p[2],
        )
        val depth = -q[2]
        if (depth < 0.05f) return null
        val aspect = widthPx / heightPx
        val sx = widthPx / 2f * (1f + q[0] / (depth * tanHalfH()))
        val sy = heightPx / 2f * (1f - q[1] / (depth * tanHalfV(aspect)))
        return floatArrayOf(sx, sy, depth)
    }
}
