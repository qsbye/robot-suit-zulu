package cn.qsbye.robot_suit_zulu.tinytorch.data

import cn.qsbye.robot_suit_zulu.tinytorch.core.Tensor
import cn.qsbye.robot_suit_zulu.tinytorch.core.TensorImpl
import java.io.InputStream
import java.util.zip.GZIPInputStream
import kotlin.random.Random

/** 样本：28x28 归一化像素 + 标签 */
class MnistDataset(val images: Array<ByteArray>, val labels: IntArray) {
    val size: Int get() = labels.size

    companion object {
        // 端上训练取子集：全量 60k 对纯 CPU Kotlin 过重
        const val TRAIN_LIMIT = 10000
        const val TEST_LIMIT = 2000

        /**
         * 从 assets 读取 gzip 压缩的 IDX 文件（源自 TinyTorch 仓库 examples/mnist/data）。
         * IDX 格式：magic(4) + count(4) + rows(4) + cols(4) + 像素字节；标签：magic(4) + count(4) + 标签字节
         */
        fun load(assets: android.content.res.AssetManager, train: Boolean): MnistDataset {
            val prefix = if (train) "train" else "t10k"
            val limit = if (train) TRAIN_LIMIT else TEST_LIMIT
            val imgBytes = readGzip(assets, "mnist/$prefix-images-idx3-ubyte.gz")
            val lblBytes = readGzip(assets, "mnist/$prefix-labels-idx1-ubyte.gz")
            val total = readInt(imgBytes, 4)
            val rows = readInt(imgBytes, 8); val cols = readInt(imgBytes, 12)
            val n = minOf(limit, total)
            val images = Array(n) { i ->
                ByteArray(rows * cols) { c -> imgBytes[16 + i * rows * cols + c] }
            }
            val labels = IntArray(n) { lblBytes[8 + it].toInt() and 0xFF }
            return MnistDataset(images, labels)
        }

        private fun readGzip(assets: android.content.res.AssetManager, name: String): ByteArray =
            GZIPInputStream(assets.open(name)).use(InputStream::readBytes)

        private fun readInt(b: ByteArray, off: Int) =
            ((b[off].toInt() and 0xFF) shl 24) or ((b[off + 1].toInt() and 0xFF) shl 16) or
                ((b[off + 2].toInt() and 0xFF) shl 8) or (b[off + 3].toInt() and 0xFF)
    }
}

/** batch 数据：images [B,1,28,28]，labels [B] */
data class Batch(val images: Tensor, val labels: IntArray, val size: Int)

/** DataLoader（对应 TinyTorch DataLoader）：batch + shuffle + 迭代 */
class DataLoader(
    private val dataset: MnistDataset,
    private val batchSize: Int,
    private val shuffle: Boolean = true,
    private val dropLast: Boolean = false
) : Iterable<Batch> {
    val size: Int
        get() = if (dropLast) dataset.size / batchSize
        else (dataset.size + batchSize - 1) / batchSize

    override fun iterator(): Iterator<Batch> = object : Iterator<Batch> {
        private val order = IntArray(dataset.size) { it }.also {
            if (shuffle) it.shuffle(Random)
        }
        private var idx = 0

        override fun hasNext(): Boolean =
            if (dropLast) idx + batchSize <= dataset.size else idx < dataset.size

        override fun next(): Batch {
            val n = minOf(batchSize, dataset.size - idx)
            val pixels = FloatArray(n * 784)
            val labels = IntArray(n)
            for (b in 0 until n) {
                val si = order[idx + b]
                val img = dataset.images[si]
                for (p in 0 until 784) {
                    pixels[b * 784 + p] = (img[p].toInt() and 0xFF) / 255f
                }
                labels[b] = dataset.labels[si]
            }
            idx += n
            return Batch(
                Tensor.of(TensorImpl.from(pixels, n, 1, 28, 28)),
                labels, n
            )
        }
    }
}
