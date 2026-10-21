package cn.qsbye.hummingbird

import cn.qsbye.hummingbird.homing.Fft128
import cn.qsbye.hummingbird.homing.FourierHoming
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

/** Fourier 快照归巢引擎的数值校验（纯 JVM） */
class FourierHomingTest {

    private fun mag(re: Float, im: Float) = sqrt(re * re + im * im)

    @Test
    fun fftRoundtrip() {
        val n = Fft128.N
        val re = FloatArray(n) { sin(2.0 * PI * 7 * it / n).toFloat() * 3f + 0.5f }
        val orig = re.copyOf()
        val im = FloatArray(n)
        Fft128.forward(re, im)
        Fft128.inverse(re, im)
        var maxErr = 0f
        for (i in 0 until n) maxErr = maxOf(maxErr, abs(re[i] - orig[i]))
        assertTrue("maxErr=$maxErr", maxErr < 1e-2f)
    }

    @Test
    fun fftSinePeak() {
        val n = Fft128.N
        val re = FloatArray(n) { sin(2.0 * PI * 5 * it / n).toFloat() }
        val im = FloatArray(n)
        Fft128.forward(re, im)
        var best = 1
        for (k in 2 until n / 2) {
            if (mag(re[k], im[k]) > mag(re[best], im[best])) best = k
        }
        assertEquals(5, best)
    }

    @Test
    fun alignRecoversShift() {
        val n = Fft128.N
        val sig = FloatArray(n)
        FourierHoming.syntheticSkyline(7, sig)
        val snap = FourierHoming.Snapshot.capture(sig)
        // cur[i] = sig[i - 23] ⇒ d = -23 ≡ 105（wrap）
        val cur = FloatArray(n) { sig[(it - 23 + n) % n] }
        val est = FourierHoming.align(snap, cur)
        assertTrue("best=${est.bestShift}", abs(est.bestShift - (n - 23) % n) <= 1)
        assertTrue("residual=${est.residual}", est.residual < 0.3f)
    }

    @Test
    fun snapshotIs16Bytes() {
        val n = Fft128.N
        val sig = FloatArray(n) { sin(2.0 * PI * 3 * it / n).toFloat() }
        val snap = FourierHoming.Snapshot.capture(sig)
        assertEquals(FourierHoming.SNAPSHOT_BYTES, snap.bytes.size)
        assertEquals(16, snap.bytes.size)
    }

    @Test
    fun phaseCorrelationShift() {
        val n = Fft128.N
        val sig = FloatArray(n)
        FourierHoming.syntheticSkyline(9, sig)
        // cur[i] = sig[i + 5] ⇒ 内容左移 5 ⇒ 估计 d = 5（wrap 后仍为 5）
        val cur = FloatArray(n) { sig[(it + 5) % n] }
        assertEquals(5, FourierHoming.estimateShift(cur, sig))
        // 反方向：cur[i] = sig[i - 5] ⇒ -5 ≡ 123 → wrap 回 -5
        val cur2 = FloatArray(n) { sig[(it - 5 + n) % n] }
        assertEquals(-5, FourierHoming.estimateShift(cur2, sig))
    }
}
