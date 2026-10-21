package cn.qsbye.hummingbird.homing

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Fourier 快照归巢（van Dijk et al., Science Robotics 9, eadk0310, 2024 的一维手机端实现）。
 *
 * - 快照：一维视野信号（128 点列均值，零均值单位 RMS）FFT 后仅保留 k=1..8 低频复系数，
 *   各量化为 8bit 实/虚部对 → **单快照 16 字节**（论文同款压缩率）
 * - 对齐：粗对齐用暴力移位搜索（遍历 128 个循环移位找图像差函数 ℰ(Δ) 最小，
 *   对应论文的暴力旋向搜索），细对齐用最小值三点抛物线插值
 *   （对应论文 ℰ(h) = ½hᵀAh + bᵀh + c 二次曲面解析最小化的一维形式）
 * - 里程计：1D 相位相关循环移位估计（帧间列/行均值），对应论文「速度 × 时间」里程计
 */
object FourierHoming {
    const val N = Fft128.N
    const val KEEP = 8
    const val SNAPSHOT_BYTES = 2 * KEEP // 16 字节

    /**
     * 量化满幅：单位 RMS 信号的 |F_k| 理论上界 = N/2·√2 ≈ 90.5，
     * 取 96 可保证任意归一化信号不被削顶（量化步长 0.76，16 个分量的量化噪声可忽略）。
     */
    private const val QUANT_SCALE = 96f

    /** 16 字节 Fourier 快照 */
    class Snapshot(val bytes: ByteArray) {
        /** 重建 k=1..KEEP 复系数到 outRe/outIm（下标 k 直接使用） */
        fun coefficients(outRe: FloatArray, outIm: FloatArray) {
            for (i in 0 until KEEP) {
                outRe[i + 1] = ((bytes[2 * i].toInt() and 0xFF) - 128) / 127f * QUANT_SCALE
                outIm[i + 1] = ((bytes[2 * i + 1].toInt() and 0xFF) - 128) / 127f * QUANT_SCALE
            }
        }

        companion object {
            /** 一维视野信号（长度 N，任意幅度）→ 16 字节快照 */
            fun capture(signal: FloatArray): Snapshot {
                val re = FloatArray(N)
                val im = FloatArray(N)
                normalize(signal, re)
                Fft128.forward(re, im)
                val bytes = ByteArray(SNAPSHOT_BYTES)
                for (k in 1..KEEP) {
                    bytes[2 * (k - 1)] = quantize(re[k])
                    bytes[2 * (k - 1) + 1] = quantize(im[k])
                }
                return Snapshot(bytes)
            }

            private fun quantize(v: Float): Byte {
                val q = (v / QUANT_SCALE * 127f).toInt().coerceIn(-127, 127)
                return (q + 128).toByte()
            }
        }
    }

    /** 信号 → 零均值、单位 RMS，写入 out（长度须为 N） */
    fun normalize(signal: FloatArray, out: FloatArray) {
        var mean = 0f
        for (v in signal) mean += v
        mean /= signal.size
        var e = 0f
        for (i in signal.indices) {
            val v = signal[i] - mean
            out[i] = v
            e += v * v
        }
        val rms = sqrt(e / signal.size).coerceAtLeast(1e-6f)
        for (i in out.indices) out[i] /= rms
    }

    /** 对齐结果：曲线 + 最优移位 + 亚像素细化 + 归一化残差（越小越匹配） */
    class Estimate(
        val curve: FloatArray,
        val bestShift: Int,
        val fineShift: Float,
        val residual: Float,
    )

    /**
     * 当前视野信号 vs 快照的对齐估计：
     * 暴力移位搜索（0..127）+ 最小值三点抛物线细化（亚像素）。
     * 约定：cur[i] = ref[(i + d) mod N] 时 bestShift = d。
     */
    fun align(snapshot: Snapshot, curSignal: FloatArray): Estimate {
        val curve = FloatArray(N)
        idfCurve(snapshot, curSignal, curve)
        var best = 0
        for (d in 1 until N) if (curve[d] < curve[best]) best = d
        val y0 = curve[best]
        val ym = curve[(best + N - 1) % N]
        val yp = curve[(best + 1) % N]
        val denom = ym - 2f * y0 + yp
        val fine = if (abs(denom) > 1e-9f) (0.5f * (ym - yp) / denom).coerceIn(-1f, 1f) else 0f
        // 归一化残差 = ℰmin / 常数项（完全匹配 ≈ 0，纯噪声 ≈ 1）
        var c = 0f
        val cre = FloatArray(N)
        val cim = FloatArray(N)
        normalize(curSignal, cre)
        Fft128.forward(cre, cim)
        val sre = FloatArray(N)
        val sim = FloatArray(N)
        snapshot.coefficients(sre, sim)
        for (k in 1..KEEP) {
            c += cre[k] * cre[k] + cim[k] * cim[k] + sre[k] * sre[k] + sim[k] * sim[k]
        }
        val residual = (curve[best] / c.coerceAtLeast(1e-9f)).coerceIn(0f, 2f)
        return Estimate(curve, best, best + fine, residual)
    }

    /**
     * 图像差函数 ℰ(Δ)（仅 K 个低频的循环移位差能），写入 out[0..N-1]：
     * ℰ(Δ) = C − 2·Re Σ_k G_k · F_k* · e^{i2πkΔ/N}
     */
    fun idfCurve(snapshot: Snapshot, curSignal: FloatArray, out: FloatArray) {
        val cre = FloatArray(N)
        val cim = FloatArray(N)
        normalize(curSignal, cre)
        Fft128.forward(cre, cim)
        val sre = FloatArray(N)
        val sim = FloatArray(N)
        snapshot.coefficients(sre, sim)
        var c = 0f
        for (k in 1..KEEP) {
            c += cre[k] * cre[k] + cim[k] * cim[k] + sre[k] * sre[k] + sim[k] * sim[k]
        }
        for (d in 0 until N) {
            var cross = 0f
            for (k in 1..KEEP) {
                // ℰ(Δ) = C − 2·Re Σ_k G_k·conj(F_k)·e^(−i2πkΔ/N)，最小值处 Δ=d
                val ang = -2.0 * PI * k * d / N
                val ca = cos(ang).toFloat()
                val sa = sin(ang).toFloat()
                // G_k · conj(F_k) 的实/虚部
                val pr = cre[k] * sre[k] + cim[k] * sim[k]
                val pi = cim[k] * sre[k] - cre[k] * sim[k]
                cross += pr * ca - pi * sa
            }
            out[d] = c - 2f * cross
        }
    }

    /**
     * 1D 相位相关循环移位估计（cur 相对 prev），返回 -N/2..N/2-1。
     * 约定：cur[i] = prev[(i + d) mod N] 时返回 d。
     * 用作帧间里程计：列均值→水平移位，行均值→垂直移位（OpenMV phasecorrelation.c 同族）。
     */
    fun estimateShift(cur: FloatArray, prev: FloatArray): Int {
        val are = FloatArray(N)
        val aim = FloatArray(N)
        val bre = FloatArray(N)
        val bim = FloatArray(N)
        normalize(cur, are)
        Fft128.forward(are, aim)
        normalize(prev, bre)
        Fft128.forward(bre, bim)
        // 互谱相位归一：R = F · conj(P) / |F · conj(P)|
        for (k in 0 until N) {
            val pr = are[k] * bre[k] + aim[k] * bim[k]
            val pi = aim[k] * bre[k] - are[k] * bim[k]
            val mag = sqrt(pr * pr + pi * pi).coerceAtLeast(1e-6f)
            are[k] = pr / mag
            aim[k] = pi / mag
        }
        Fft128.inverse(are, aim)
        var best = 0
        for (i in 1 until N) if (are[i] > are[best]) best = i
        // 互相关峰在 N−d，换算回约定 cur[i] = prev[(i + d) mod N] 的 d
        val d = (N - best) % N
        return if (d > N / 2) d - N else d
    }

    /** 合成天际线信号（仿真模式演示 ℰ/归巢）：k=1..KEEP 次随机幅相谐波，单位 RMS */
    fun syntheticSkyline(seed: Long, out: FloatArray) {
        val r = Random(seed)
        val amp = FloatArray(KEEP + 1) { if (it == 0) 0f else (r.nextFloat() * 0.9f + 0.1f) / it }
        val pha = FloatArray(KEEP + 1) { r.nextFloat() * 6.28f }
        for (i in 0 until N) {
            val x = 2.0 * PI * i / N
            var v = 0f
            for (k in 1..KEEP) v += amp[k] * sin((k * x + pha[k]).toFloat())
            out[i] = v
        }
        normalize(out, out)
    }
}
