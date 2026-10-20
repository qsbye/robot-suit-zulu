package cn.qsbye.robot_suit_zulu.depth

import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * 程序化合成场景生成器：产出「室内/街道式」简化场景的亮度图 + 真值相对深度图。
 *
 * 对应 DepthART 训练管线里的合成数据集（Hypersim）角色——真机上没有带真值深度的
 * 实拍数据，就用程序化场景充当训练分布；场景要素：
 *  - 远处墙面/天空 + 地平线 + 透视地面（深度沿纵向递减）
 *  - 随机物体：朗伯着色球体 / 立柱 / 地面色块（遮挡边界 + 明暗线索）
 *  - 全局亮度/对比度抖动 + 逐格噪声（逼推理时的光照差异）
 *
 * 亮度直接生成在网格分辨率上，与相机帧降采样后的分辨率一致，
 * 保证 [FeatureGrid] 特征在训练/推理两个来源下同分布。
 * 深度约定：0=far → 1=near（跨场景一致的先验刻度，不做逐场景归一化）。
 */
object SceneSynthesizer {

    /**
     * 生成一个场景。
     * @param lumOut 输出亮度（长度 gw*gh，0..1）
     * @param depthOut 输出真值相对深度（长度 gw*gh，0=far → 1=near）
     */
    fun generate(rng: Random, gw: Int, gh: Int, lumOut: FloatArray, depthOut: FloatArray) {
        // ---- 全局参数 ----
        val horizonF = 0.3f + rng.nextFloat() * 0.3f            // 地平线高度
        val horizonY = horizonF * (gh - 1)
        val farBright = 0.45f + rng.nextFloat() * 0.4f          // 远景亮度
        val farDepth = 0.04f + rng.nextFloat() * 0.16f          // 远景深度
        val groundBright = 0.3f + rng.nextFloat() * 0.35f       // 地面亮度
        val horizonDepth = 0.3f + rng.nextFloat() * 0.2f        // 地平线处地面深度
        val bottomDepth = 0.82f + rng.nextFloat() * 0.16f       // 画面底部地面深度
        val groundCurve = 0.8 + rng.nextFloat() * 0.8           // 透视曲线指数
        val brightJitter = 0.85f + rng.nextFloat() * 0.3f
        val farGrad = (rng.nextFloat() - 0.5f) * 0.12f          // 远景垂直梯度

        // ---- 背景：远景 + 透视地面 ----
        for (y in 0 until gh) {
            val row = y * gw
            if (y <= horizonY) {
                // 远景：轻微垂直渐变
                val t = if (horizonY > 0f) y / horizonY else 0f
                val b = (farBright + farGrad * t).coerceIn(0.05f, 1f)
                for (x in 0 until gw) {
                    lumOut[row + x] = b
                    depthOut[row + x] = farDepth
                }
            } else {
                // 地面：亮度随机走向 + 深度按透视曲线从 horizonDepth 到 bottomDepth
                val t = (y - horizonY) / (gh - 1 - horizonY).coerceAtLeast(1f)
                val d = horizonDepth + (bottomDepth - horizonDepth) *
                    Math.pow(t.toDouble(), groundCurve).toFloat()
                val b = if (rng.nextBoolean()) {
                    groundBright * (1.15f - 0.3f * t)
                } else {
                    groundBright * (0.85f + 0.3f * t)
                }
                for (x in 0 until gw) {
                    lumOut[row + x] = b.coerceIn(0.05f, 1f)
                    depthOut[row + x] = d
                }
            }
        }

        // ---- 随机物体 ----
        val lightX = -0.35f - rng.nextFloat() * 0.3f
        val lightY = -0.5f - rng.nextFloat() * 0.3f
        val objects = 1 + rng.nextInt(4)
        repeat(objects) {
            when (rng.nextInt(3)) {
                0 -> drawSphere(rng, gw, gh, horizonY, horizonDepth, bottomDepth, lightX, lightY, lumOut, depthOut)
                1 -> drawPillar(rng, gw, gh, horizonY, horizonDepth, bottomDepth, lumOut, depthOut)
                else -> drawFloorPatch(rng, gw, gh, horizonY, bottomDepth, lumOut)
            }
        }

        // ---- 全局亮度/对比度抖动 + 逐格噪声 ----
        for (i in 0 until gw * gh) {
            val v = lumOut[i] * brightJitter + (rng.nextFloat() - 0.5f) * 0.04f
            lumOut[i] = v.coerceIn(0f, 1f)
        }
    }

    /** 纵坐标 row 处的地面深度（与背景生成共用同一透视曲线）。 */
    private fun groundDepthAt(
        row: Int, gh: Int, horizonY: Float, horizonDepth: Float, bottomDepth: Float,
    ): Float {
        if (row <= horizonY) return horizonDepth
        val t = (row - horizonY) / (gh - 1 - horizonY).coerceAtLeast(1f)
        return horizonDepth + (bottomDepth - horizonDepth) * t
    }

    /** 朗伯着色球体：亮度由法线·光照决定，深度带球面外凸。 */
    private fun drawSphere(
        rng: Random, gw: Int, gh: Int, horizonY: Float,
        horizonDepth: Float, bottomDepth: Float,
        lx: Float, ly: Float,
        lum: FloatArray, depth: FloatArray,
    ) {
        val r = 2 + rng.nextInt(5)
        val cx = rng.nextInt(gw).toFloat()
        val cy = (horizonY + rng.nextFloat() * (gh - horizonY))
            .coerceIn(r.toFloat(), (gh - r - 1).toFloat().coerceAtLeast(r.toFloat()))
        // 球体深度：落地在地面，或悬浮
        val baseRow = (cy + r).toInt().coerceIn(0, gh - 1)
        val baseDepth = if (rng.nextBoolean()) {
            groundDepthAt(baseRow, gh, horizonY, horizonDepth, bottomDepth)
        } else {
            0.35f + rng.nextFloat() * 0.45f
        }
        val bulge = 0.08f + rng.nextFloat() * 0.12f
        val ambient = 0.3f + rng.nextFloat() * 0.2f
        val diffuse = 0.5f + rng.nextFloat() * 0.3f

        for (dy in -r..r) {
            val y = cy.toInt() + dy
            if (y < 0 || y >= gh) continue
            for (dx in -r..r) {
                val x = cx.toInt() + dx
                if (x < 0 || x >= gw) continue
                val fx = dx / r.toFloat()
                val fy = dy / r.toFloat()
                val u2 = fx * fx + fy * fy
                if (u2 > 1f) continue
                // 单位法线（z 朝向相机）
                val nz = sqrt(1f - u2)
                val nl = (fx * lx + fy * ly + nz * 0.8f).coerceAtLeast(0f)
                val b = (ambient + diffuse * nl).coerceIn(0.05f, 1f)
                val idx = y * gw + x
                lum[idx] = b
                depth[idx] = (baseDepth + bulge * nz).coerceAtMost(1f)
            }
        }
    }

    /** 立柱/箱体：竖直矩形 + 竖向明暗渐变，模拟侧面受光。 */
    private fun drawPillar(
        rng: Random, gw: Int, gh: Int, horizonY: Float,
        horizonDepth: Float, bottomDepth: Float,
        lum: FloatArray, depth: FloatArray,
    ) {
        val w = 2 + rng.nextInt(4)
        val top = (horizonY * 0.5f).toInt().coerceAtLeast(0)
        val bottom = (horizonY + rng.nextFloat() * (gh - horizonY)).toInt()
            .coerceIn(top + 3, gh - 1)
        val left = rng.nextInt((gw - w).coerceAtLeast(1))
        val faceBright = (0.35f + rng.nextFloat() * 0.45f).coerceIn(0.05f, 1f)
        val baseRow = bottom
        val d = groundDepthAt(baseRow, gh, horizonY, horizonDepth, bottomDepth)
        val shadeDir = if (rng.nextBoolean()) 1 else -1
        for (y in top..bottom) {
            for (x in left until min(left + w, gw)) {
                val t = (y - top).toFloat() / (bottom - top).coerceAtLeast(1)
                // 竖向明暗渐变模拟侧面受光
                val b = (if (shadeDir > 0) faceBright * (0.75f + 0.25f * t)
                else faceBright * (1f - 0.2f * t)) + (x - left).toFloat() / w * 0.06f * shadeDir
                val idx = y * gw + x
                lum[idx] = b.coerceIn(0.05f, 1f)
                depth[idx] = d.coerceAtMost(1f)
            }
        }
    }

    /** 地面色块：椭圆贴地，亮度略异于地面，深度与地面一致（无遮挡凸起）。 */
    private fun drawFloorPatch(
        rng: Random, gw: Int, gh: Int, horizonY: Float, bottomDepth: Float,
        lum: FloatArray,
    ) {
        val rx = 2 + rng.nextInt(6)
        val ry = 1 + rng.nextInt(3)
        val cx = rng.nextInt(gw).toFloat()
        val cy = (horizonY + 3 + rng.nextFloat() * (gh - horizonY - 4))
            .coerceIn(horizonY + 2f, (gh - 1).toFloat())
        val delta = (rng.nextFloat() - 0.5f) * 0.3f
        for (dy in -ry..ry) {
            val y = cy.toInt() + dy
            if (y < 0 || y >= gh) continue
            for (dx in -rx..rx) {
                val x = cx.toInt() + dx
                if (x < 0 || x >= gw) continue
                val e = (dx * dx).toFloat() / (rx * rx) + (dy * dy).toFloat() / (ry * ry)
                if (e > 1f) continue
                // 椭圆边缘羽化
                val feather = sin((1f - e) * PI / 2).toFloat()
                val idx = y * gw + x
                lum[idx] = (lum[idx] + delta * feather).coerceIn(0.05f, 1f)
            }
        }
    }
}
