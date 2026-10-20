package cn.qsbye.robot_suit_zulu.tinytorch.train

import cn.qsbye.robot_suit_zulu.tinytorch.core.Tensor
import cn.qsbye.robot_suit_zulu.tinytorch.core.TensorImpl
import cn.qsbye.robot_suit_zulu.tinytorch.nn.Module
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File

/** 极简 checkpoint：逐参数写入 (ndim, shape..., float...) 序列，含参数个数头 */
object Serializer {
    fun save(module: Module, file: File) {
        DataOutputStream(file.outputStream().buffered()).use { out ->
            val params = module.parameters()
            out.writeInt(params.size)
            for (p in params) {
                val s = p.impl.shape
                out.writeInt(s.size)
                s.forEach(out::writeInt)
                p.impl.data.forEach(out::writeFloat)
            }
        }
    }

    fun load(module: Module, file: File): Boolean {
        if (!file.exists()) return false
        DataInputStream(file.inputStream().buffered()).use { inp ->
            val count = inp.readInt()
            val params = module.parameters()
            if (count != params.size) return false
            for (p in params) {
                val ndim = inp.readInt()
                val shape = IntArray(ndim) { inp.readInt() }
                if (!shape.contentEquals(p.impl.shape)) return false
                for (i in p.impl.data.indices) p.impl.data[i] = inp.readFloat()
            }
        }
        return true
    }
}

fun tensorOf(data: FloatArray, vararg shape: Int): Tensor = Tensor.of(TensorImpl.from(data, *shape))
