package cn.qsbye.tinytorch

import cn.qsbye.tinytorch.core.Tensor
import cn.qsbye.tinytorch.core.TensorImpl
import cn.qsbye.tinytorch.nn.Linear
import cn.qsbye.tinytorch.nn.NLLLoss
import org.junit.Assert.assertEquals
import org.junit.Test

/** 数值梯度校验：线性层 + logSoftmax + NLL 的解析梯度 vs 有限差分 */
class AutogradTest {

    @Test
    fun linearWeightGradientMatchesNumeric() {
        val x = TensorImpl.from(floatArrayOf(0.5f, -0.3f, 0.8f, 1.2f, -0.7f, 0.1f), 2, 3)
        val net = Linear(3, 2)
        val targets = intArrayOf(0, 1)

        fun forwardLoss(): Float {
            val t = Tensor.of(x.clone())
            return NLLLoss.forward(net(t).logSoftmax(), targets).impl.data[0]
        }

        // 解析梯度
        val t = Tensor.of(x.clone())
        NLLLoss.forward(net(t).logSoftmax(), targets).backward()
        val analytic = net.weight.grad!!.data.clone()

        // 数值梯度
        val eps = 1e-3f
        for (i in analytic.indices) {
            val orig = net.weight.impl.data[i]
            net.weight.impl.data[i] = orig + eps
            val lp = forwardLoss()
            net.weight.impl.data[i] = orig - eps
            val lm = forwardLoss()
            net.weight.impl.data[i] = orig
            val numeric = (lp - lm) / (2 * eps)
            assertEquals("weight[$i]", numeric, analytic[i], 2e-2f)
        }
    }

    @Test
    fun biasGradientMatchesNumeric() {
        val x = TensorImpl.from(floatArrayOf(0.2f, 0.9f, -0.4f), 1, 3)
        val net = Linear(3, 4)
        val targets = intArrayOf(2)

        fun forwardLoss(): Float {
            val t = Tensor.of(x.clone())
            return NLLLoss.forward(net(t).logSoftmax(), targets).impl.data[0]
        }

        NLLLoss.forward(net(Tensor.of(x.clone())).logSoftmax(), targets).backward()
        val analytic = net.bias.grad!!.data.clone()

        val eps = 1e-3f
        for (i in analytic.indices) {
            val orig = net.bias.impl.data[i]
            net.bias.impl.data[i] = orig + eps
            val lp = forwardLoss()
            net.bias.impl.data[i] = orig - eps
            val lm = forwardLoss()
            net.bias.impl.data[i] = orig
            assertEquals("bias[$i]", (lp - lm) / (2 * eps), analytic[i], 2e-2f)
        }
    }
}
