package cn.qsbye.tinytorch.core

/**
 * 计算图节点（对应 TinyTorch 的 Function）：
 * 前向时通过 [apply] 把输入的 gradFn 链入 [nextFunctions] 建立反向依赖图；
 * 反传由 [Engine.backward] 拓扑调度。
 */
abstract class Function {
    /** 与 forward 的输入一一对应；null 表示该输入无需梯度 */
    val nextFunctions = mutableListOf<Function?>()
    /** save_for_backward：forward 期间保存的输入/输出 impl */
    protected val saved = mutableListOf<TensorImpl>()

    abstract fun forward(inputs: List<TensorImpl>): TensorImpl
    /** 返回与各输入对齐的梯度列表（null = 不需要） */
    abstract fun backward(gradOut: TensorImpl): List<TensorImpl?>

    fun apply(vararg inputs: Tensor): Tensor {
        saved.clear()
        val out = forward(inputs.map { it.impl })
        val needsGrad = inputs.any { it.requiresGrad }
        if (needsGrad) {
            nextFunctions.clear()
            for (t in inputs) {
                nextFunctions.add(
                    when {
                        !t.requiresGrad -> null
                        t.gradFn != null -> t.gradFn          // 非叶子：接着往上游走
                        else -> AccumulateGrad(t)              // 叶子：写回梯度
                    }
                )
            }
        }
        return Tensor(out, requiresGrad = needsGrad, gradFn = if (needsGrad) this else null)
    }

    protected fun save(vararg impls: TensorImpl) { saved.addAll(impls) }
}

/** 叶子节点的梯度收集器（对应 FuncLeaf）：把到达的梯度累加进 tensor.grad */
class AccumulateGrad(private val tensor: Tensor) : Function() {
    override fun forward(inputs: List<TensorImpl>) = throw UnsupportedOperationException()
    override fun backward(gradOut: TensorImpl): List<TensorImpl?> {
        tensor.grad = tensor.grad?.add_(gradOut) ?: gradOut
        return emptyList()
    }
}

/**
 * 反传引擎（对应 AutogradMeta::backward）：
 * 1. 从根 gradFn 出发遍历 nextFunctions 收集全图
 * 2. 统计每个节点的"消费者数"（入度），消费者全部产完梯度才执行其 backward
 * 3. 支持分叉结构：同一 Tensor 被多个算子消费时梯度自动累加
 */
object Engine {
    fun backward(root: Tensor, gradOut: TensorImpl? = null) {
        val rootFn = root.gradFn ?: return
        val consumers = HashMap<Function, Int>()
        val seen = HashSet<Function>()
        val stack = ArrayDeque<Function>()
        stack.add(rootFn); seen.add(rootFn)
        while (stack.isNotEmpty()) {
            val f = stack.removeLast()
            for (nf in f.nextFunctions) {
                if (nf == null) continue
                consumers[nf] = (consumers[nf] ?: 0) + 1
                if (seen.add(nf)) stack.add(nf)
            }
        }
        val grads = HashMap<Function, TensorImpl>()
        val ready = ArrayDeque<Function>()
        ready.add(rootFn)
        grads[rootFn] = gradOut ?: TensorImpl.full(1f, *root.impl.shape)
        while (ready.isNotEmpty()) {
            val f = ready.removeLast()
            val g = grads.remove(f) ?: continue
            val inputGrads = f.backward(g)
            for (i in f.nextFunctions.indices) {
                val nf = f.nextFunctions[i] ?: continue
                val ig = inputGrads.getOrNull(i) ?: continue
                val acc = grads[nf]
                grads[nf] = if (acc == null) ig else acc.add_(ig)
                val remain = (consumers[nf] ?: 0) - 1
                consumers[nf] = remain
                if (remain == 0) ready.add(nf)
            }
        }
    }
}
