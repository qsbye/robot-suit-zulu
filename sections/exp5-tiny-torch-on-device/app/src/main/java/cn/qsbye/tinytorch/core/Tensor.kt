package cn.qsbye.tinytorch.core

/**
 * 对外张量（对应 TinyTorch 的 Tensor）：包装 [TensorImpl] + 梯度元信息。
 * 全部算子经 [Function.apply] 走计算图。
 */
class Tensor internal constructor(
    val impl: TensorImpl,
    val requiresGrad: Boolean = false,
    var gradFn: Function? = null
) {
    var grad: TensorImpl? = null
        internal set

    val shape: IntArray get() = impl.shape

    companion object {
        fun of(impl: TensorImpl, requiresGrad: Boolean = false) = Tensor(impl, requiresGrad)
        fun zeros(vararg shape: Int, requiresGrad: Boolean = false) = Tensor(TensorImpl.zeros(*shape), requiresGrad)
        fun uniformFanIn(fanIn: Int, vararg shape: Int, requiresGrad: Boolean = false) =
            Tensor(TensorImpl.uniformFanIn(fanIn, *shape), requiresGrad)
    }

    fun backward() = Engine.backward(this)

    // ---- 算子（均经计算图） ----

    fun relu(): Tensor = ReluFn().apply(this)
    fun logSoftmax(): Tensor = LogSoftmaxFn().apply(this)
    /** x[M,in] @ w[out,in]^T + b[out] */
    fun linear(w: Tensor, b: Tensor): Tensor = LinearFn().apply(this, w, b)
    fun flatten(batchDim: Int = 1): Tensor {
        require(batchDim == 1 && impl.shape.size == 4) { "仅支持 [N,C,H,W] -> [N,C*H*W]" }
        val s = impl.shape
        return ViewFn(intArrayOf(s[0], s[1] * s[2] * s[3])).apply(this)
    }
}

/** y = relu(x) */
class ReluFn : Function() {
    private var mask: BooleanArray? = null
    override fun forward(inputs: List<TensorImpl>): TensorImpl {
        val (out, m) = inputs[0].relu()
        mask = m
        return out
    }
    override fun backward(gradOut: TensorImpl): List<TensorImpl?> {
        val m = mask!!
        val data = FloatArray(gradOut.size)
        for (i in data.indices) data[i] = if (m[i]) gradOut.data[i] else 0f
        return listOf(TensorImpl(gradOut.shape.clone(), data))
    }
}

/** y = log_softmax(x, dim=1)，二维 [M,N] */
class LogSoftmaxFn : Function() {
    override fun forward(inputs: List<TensorImpl>): TensorImpl {
        val out = inputs[0].logSoftmax()
        save(inputs[0], out)
        return out
    }
    override fun backward(gradOut: TensorImpl): List<TensorImpl?> =
        listOf(saved[0].logSoftmaxBackward(gradOut, saved[1]))
}

/** y = x @ w^T + b；x[M,in] w[out,in] b[out] */
class LinearFn : Function() {
    override fun forward(inputs: List<TensorImpl>): TensorImpl {
        val (x, w, b) = inputs
        val y = x.matmul(w, transB = true)
        val (m, n) = y.shape[0] to y.shape[1]
        for (r in 0 until m) for (c in 0 until n) y.data[r * n + c] += b.data[c]
        save(x, w)
        return y
    }
    override fun backward(gradOut: TensorImpl): List<TensorImpl?> {
        val (x, w) = saved
        val gx = gradOut.matmul(w)                    // [M,out] @ [out,in] = [M,in]
        val gw = gradOut.matmul(x, transA = true)     // [out,M]... 实为 gradOut^T @ x = [out,in]
        val gb = TensorImpl.zeros(*intArrayOf(gradOut.shape[1]))
        val m = gradOut.shape[0]; val n = gradOut.shape[1]
        for (r in 0 until m) for (c in 0 until n) gb.data[c] += gradOut.data[r * n + c]
        return listOf(gx, gw, gb)
    }
}

/** 纯视图变换（flatten），backward 还原 shape */
class ViewFn(private val newShape: IntArray) : Function() {
    private var oldShape: IntArray = intArrayOf()
    override fun forward(inputs: List<TensorImpl>): TensorImpl {
        oldShape = inputs[0].shape.clone()
        return inputs[0].view(*newShape)
    }
    override fun backward(gradOut: TensorImpl): List<TensorImpl?> =
        listOf(TensorImpl(oldShape, gradOut.data)) // view 共享数组，梯度同理
}
