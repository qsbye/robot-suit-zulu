package cn.qsbye.robot_suit_zulu.hummingbird.homing

import kotlin.math.cos
import kotlin.math.sin

/**
 * 128 点复数 FFT（迭代 radix-2 Cooley-Tukey，纯 Kotlin）。
 * 用于 Fourier 快照压缩与 1D 相位相关里程计（对应 OpenMV imlib/fft.c 的角色）。
 */
object Fft128 {
    const val N = 128

    private val COS = FloatArray(N / 2) { cos(2.0 * kotlin.math.PI * it / N).toFloat() }
    private val SIN = FloatArray(N / 2) { sin(2.0 * kotlin.math.PI * it / N).toFloat() }
    private val REV = IntArray(N) { Integer.reverse(it) ushr 25 } // 7 位位反转

    /** 原地前向 FFT（re/im 长度必须为 N） */
    fun forward(re: FloatArray, im: FloatArray) = transform(re, im, inverse = false)

    /** 原地逆 FFT（含 1/N 归一化） */
    fun inverse(re: FloatArray, im: FloatArray) = transform(re, im, inverse = true)

    private fun transform(re: FloatArray, im: FloatArray, inverse: Boolean) {
        for (i in 0 until N) {
            val j = REV[i]
            if (j > i) {
                var t = re[i]; re[i] = re[j]; re[j] = t
                t = im[i]; im[i] = im[j]; im[j] = t
            }
        }
        var len = 2
        while (len <= N) {
            val half = len / 2
            val step = N / len
            var base = 0
            while (base < N) {
                for (k in 0 until half) {
                    val w = k * step
                    val wr = COS[w]
                    val wi = if (inverse) SIN[w] else -SIN[w]
                    val i0 = base + k
                    val i1 = i0 + half
                    val tr = re[i1] * wr - im[i1] * wi
                    val ti = re[i1] * wi + im[i1] * wr
                    re[i1] = re[i0] - tr
                    im[i1] = im[i0] - ti
                    re[i0] += tr
                    im[i0] += ti
                }
                base += len
            }
            len = len shl 1
        }
        if (inverse) {
            for (i in 0 until N) {
                re[i] = re[i] / N
                im[i] = im[i] / N
            }
        }
    }
}
