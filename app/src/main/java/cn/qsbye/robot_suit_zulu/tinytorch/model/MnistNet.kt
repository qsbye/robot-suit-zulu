package cn.qsbye.robot_suit_zulu.tinytorch.model

import cn.qsbye.robot_suit_zulu.tinytorch.core.Tensor
import cn.qsbye.robot_suit_zulu.tinytorch.nn.Linear
import cn.qsbye.robot_suit_zulu.tinytorch.nn.Module

/** MNIST MLP：784 → 128 → 10（relu + log_softmax），对应 TinyTorch demo 的简化版 */
class MnistNet : Module() {
    private val fc1 = registerModule(Linear(784, 128)) as Linear
    private val fc2 = registerModule(Linear(128, 10)) as Linear

    override fun forward(x: Tensor): Tensor {
        var h = x.flatten()
        h = fc1(h).relu()
        h = fc2(h)
        return h.logSoftmax()
    }

    fun predict(x: Tensor): IntArray {
        eval()
        val out = forward(x).impl
        val (m, n) = out.shape[0] to out.shape[1]
        return IntArray(m) { r ->
            var best = 0
            for (c in 1 until n) if (out.data[r * n + c] > out.data[r * n + best]) best = c
            best
        }
    }
}
