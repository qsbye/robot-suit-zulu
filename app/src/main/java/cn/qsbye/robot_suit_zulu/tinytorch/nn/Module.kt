package cn.qsbye.robot_suit_zulu.tinytorch.nn

import cn.qsbye.robot_suit_zulu.tinytorch.core.Tensor
import cn.qsbye.robot_suit_zulu.tinytorch.core.TensorImpl

/**
 * 模块基类（对应 TinyTorch 的 Module）：递归注册子模块并汇总参数；
 * train()/eval() 区分训练与推理行为（如 Dropout）。
 */
abstract class Module {
    private val subModules = mutableListOf<Module>()
    private val ownParams = mutableListOf<Tensor>()

    var training: Boolean = true
        private set

    protected fun registerParam(t: Tensor): Tensor { ownParams.add(t); return t }
    protected fun registerModule(m: Module): Module { subModules.add(m); return m }

    fun parameters(): List<Tensor> = ownParams + subModules.flatMap { it.parameters() }

    fun train() { training = true; subModules.forEach { it.train() } }
    fun eval() { training = false; subModules.forEach { it.eval() } }

    fun zeroGrad() = parameters().forEach { it.grad = null }

    abstract fun forward(x: Tensor): Tensor
    operator fun invoke(x: Tensor): Tensor = forward(x)
}

/** y = x @ w^T + b（kaiming uniform 初始化） */
class Linear(inFeatures: Int, outFeatures: Int) : Module() {
    val weight = registerParam(Tensor.uniformFanIn(inFeatures, outFeatures, inFeatures, requiresGrad = true))
    val bias = registerParam(Tensor.zeros(outFeatures, requiresGrad = true))
    override fun forward(x: Tensor): Tensor = x.linear(weight, bias)
}

/** NLL 损失（输入为 log_softmax 输出）：-mean(logp[target])，作为一个图节点参与反传 */
object NLLLoss {
    fun forward(logp: Tensor, targets: IntArray): Tensor = NllLossFn(targets).apply(logp)

    private class NllLossFn(private val targets: IntArray) : cn.qsbye.robot_suit_zulu.tinytorch.core.Function() {
        override fun forward(inputs: List<TensorImpl>): TensorImpl {
            val logp = inputs[0]
            val (m, n) = logp.shape[0] to logp.shape[1]
            var loss = 0f
            for (r in 0 until m) loss -= logp.data[r * n + targets[r]]
            save(logp)
            return TensorImpl.full(loss / m, 1)
        }
        override fun backward(gradOut: TensorImpl): List<TensorImpl?> {
            val logp = saved[0]
            val (m, n) = logp.shape[0] to logp.shape[1]
            val scale = gradOut.data[0] / m
            val dx = FloatArray(logp.size)
            for (r in 0 until m) dx[r * n + targets[r]] = -scale
            return listOf(TensorImpl(logp.shape.clone(), dx))
        }
    }
}
