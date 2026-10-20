package cn.qsbye.tinytorch.optim

import cn.qsbye.tinytorch.core.Tensor
import kotlin.math.pow
import kotlin.math.sqrt

abstract class Optimizer(protected val params: List<Tensor>, var lr: Float) {
    abstract fun step()
    fun zeroGrad() = params.forEach { it.grad = null }
}

class SGD(params: List<Tensor>, lr: Float, private val weightDecay: Float = 0f) : Optimizer(params, lr) {
    override fun step() {
        for (p in params) {
            val g = p.grad ?: continue
            val d = p.impl.data
            for (i in d.indices) {
                val grad = g.data[i] + weightDecay * d[i]
                d[i] -= lr * grad
            }
        }
    }
}

/** Adam（对应 TinyTorch Adam，与 exp4 AdamTrainer 同源） */
class Adam(
    params: List<Tensor>,
    lr: Float = 1e-3f,
    private val beta1: Float = 0.9f,
    private val beta2: Float = 0.999f,
    private val eps: Float = 1e-8f
) : Optimizer(params, lr) {
    private var t = 0
    private val m = params.map { FloatArray(it.impl.size) }
    private val v = params.map { FloatArray(it.impl.size) }
    // pow 查表：t 递增
    private val pow1 = HashMap<Int, Float>()
    private val pow2 = HashMap<Int, Float>()

    override fun step() {
        t++
        val b1t = pow1.getOrPut(t) { beta1.pow(t) }
        val b2t = pow2.getOrPut(t) { beta2.pow(t) }
        val lrT = lr * sqrt(1f - b2t) / (1f - b1t)
        for ((pi, p) in params.withIndex()) {
            val g = p.grad ?: continue
            val d = p.impl.data
            val mi = m[pi]; val vi = v[pi]
            for (i in d.indices) {
                val gi = g.data[i]
                mi[i] = beta1 * mi[i] + (1f - beta1) * gi
                vi[i] = beta2 * vi[i] + (1f - beta2) * gi * gi
                d[i] -= lrT * mi[i] / (sqrt(vi[i]) + eps)
            }
        }
    }
}

/** StepLR：每 stepSize 个 epoch lr *= gamma */
class StepLR(
    private val optimizer: Optimizer,
    private val stepSize: Int,
    private val gamma: Float = 0.1f
) {
    private val baseLr = optimizer.lr
    private var epoch = 0
    fun step() {
        epoch++
        optimizer.lr = baseLr * gamma.pow(epoch / stepSize)
    }
}
