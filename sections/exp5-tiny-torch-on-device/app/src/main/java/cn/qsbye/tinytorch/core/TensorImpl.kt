package cn.qsbye.tinytorch.core

import kotlin.math.sqrt
import kotlin.random.Random

/**
 * 纯数值张量实现（对应 TinyTorch 的 TensorImpl）：
 * 只管 shape / strides / 一维 float32 存储与基础算子，不涉及任何梯度逻辑。
 * 仅支持连续（contiguous）存储，view 类操作直接改 shape。
 */
class TensorImpl(
    var shape: IntArray,
    val data: FloatArray
) {
    val size: Int get() = data.size

    companion object {
        fun zeros(vararg shape: Int) = TensorImpl(shape, FloatArray(shape.fold(1, Int::times)))
        fun full(value: Float, vararg shape: Int) =
            TensorImpl(shape, FloatArray(shape.fold(1, Int::times)) { value })
        /** kaiming/uniform(-k, k)，k = 1/sqrt(fanIn) */
        fun uniformFanIn(fanIn: Int, vararg shape: Int): TensorImpl {
            val k = 1f / sqrt(fanIn.toFloat())
            return TensorImpl(shape, FloatArray(shape.fold(1, Int::times)) { Random.nextFloat() * 2f * k - k })
        }
        fun from(data: FloatArray, vararg shape: Int): TensorImpl {
            require(data.size == shape.fold(1, Int::times)) { "data size != shape size" }
            return TensorImpl(shape, data)
        }
    }

    fun clone(): TensorImpl = TensorImpl(shape.clone(), data.clone())

    fun add_(o: TensorImpl): TensorImpl { for (i in data.indices) data[i] += o.data[i]; return this }

    /** 矩阵乘：a[m,k] (x b[k,n])，支持转置标记；二维专用，MNIST 链路足够 */
    fun matmul(o: TensorImpl, transA: Boolean = false, transB: Boolean = false): TensorImpl {
        val (am, ak) = if (transA) shape[1] to shape[0] else shape[0] to shape[1]
        val (bk, bn) = if (transB) o.shape[1] to o.shape[0] else o.shape[0] to o.shape[1]
        require(ak == bk) { "matmul shape mismatch: $am x $ak vs $bk x $bn" }
        val out = zeros(am, bn)
        for (m in 0 until am) {
            for (k in 0 until ak) {
                val av = data[if (transA) k * am + m else m * ak + k]
                if (av == 0f) continue
                for (n in 0 until bn) {
                    out.data[m * bn + n] += av * o.data[if (transB) n * bk + k else k * bn + n]
                }
            }
        }
        return out
    }

    /** relu，返回 (输出, 掩码)——掩码供 backward 复用 */
    fun relu(): Pair<TensorImpl, BooleanArray> {
        val out = FloatArray(size); val mask = BooleanArray(size)
        for (i in data.indices) { val p = data[i] > 0f; mask[i] = p; out[i] = if (p) data[i] else 0f }
        return TensorImpl(shape.clone(), out) to mask
    }

    /** 按行 log_softmax（二维 [M,N]，dim=1） */
    fun logSoftmax(): TensorImpl {
        val (m, n) = shape[0] to shape[1]
        val out = FloatArray(size)
        for (r in 0 until m) {
            var max = Float.NEGATIVE_INFINITY
            for (c in 0 until n) max = maxOf(max, data[r * n + c])
            var sum = 0f
            for (c in 0 until n) sum += kotlin.math.exp(data[r * n + c] - max)
            val lse = max + kotlin.math.ln(sum)
            for (c in 0 until n) out[r * n + c] = data[r * n + c] - lse
        }
        return TensorImpl(shape.clone(), out)
    }

    fun meanScalar(): Float {
        var s = 0f; for (v in data) s += v
        return s / size
    }

    fun mulScalar(s: Float): TensorImpl {
        val out = FloatArray(size); for (i in data.indices) out[i] = data[i] * s
        return TensorImpl(shape.clone(), out)
    }

    /** log_softmax backward：dx = dy - softmax * sum(dy)（按行） */
    fun logSoftmaxBackward(grad: TensorImpl, out: TensorImpl): TensorImpl {
        val (m, n) = shape[0] to shape[1]
        val dx = FloatArray(size)
        for (r in 0 until m) {
            var gsum = 0f
            for (c in 0 until n) gsum += grad.data[r * n + c]
            for (c in 0 until n) {
                dx[r * n + c] = grad.data[r * n + c] - kotlin.math.exp(out.data[r * n + c]) * gsum
            }
        }
        return TensorImpl(shape.clone(), dx)
    }

    fun view(vararg newShape: Int): TensorImpl {
        require(newShape.fold(1, Int::times) == size) { "view size mismatch" }
        return TensorImpl(newShape, data) // 共享底层数组（框架内约定 view 后立即消费，不做写穿透）
    }
}
