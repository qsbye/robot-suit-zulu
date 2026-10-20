package cn.qsbye.tinytorch.ui

import android.content.Context
import cn.qsbye.tinytorch.data.Batch
import cn.qsbye.tinytorch.data.MnistDataset
import cn.qsbye.tinytorch.nn.NLLLoss
import cn.qsbye.tinytorch.optim.Adam
import cn.qsbye.tinytorch.optim.StepLR
import cn.qsbye.tinytorch.train.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File

/** 训练状态 */
data class TrainState(
    val epoch: Int = 0,
    val batch: Int = 0,
    val loss: Float = 0f,
    val acc: Float = 0f,
    val running: Boolean = false,
    val msg: String = "就绪"
)

/** Trainer：在 Default 调度器上跑 MNIST 训练循环，通过 StateFlow 驱动 Compose UI */
class Trainer(context: Context) {
    val model = cn.qsbye.tinytorch.model.MnistNet()
    val optimizer = Adam(model.parameters(), lr = 1e-3f)
    val scheduler = StepLR(optimizer, stepSize = 5, gamma = 0.5f)

    private val _state = MutableStateFlow(TrainState())
    val state: StateFlow<TrainState> = _state
    val lossHistory = mutableListOf<Float>()
    var lastTestPredictions: Pair<IntArray, IntArray> = Pair(intArrayOf(), intArrayOf())

    private val ckpt = File(context.filesDir, "mnist_ckpt.bin")

    suspend fun fit(train: MnistDataset, test: MnistDataset, epochs: Int) {
        withContext(Dispatchers.Default) {
            val dl = cn.qsbye.tinytorch.data.DataLoader(train, batchSize = 64, shuffle = true)
            _state.value = _state.value.copy(running = true, msg = "训练中...")
            for (e in 1..epochs) {
                if (!isActive) break
                model.train()
                var runningLoss = 0f
                var bi = 0
                for (batch in dl) {
                    if (!isActive) break
                    optimizer.zeroGrad()
                    val out = model(batch.images)
                    val loss = NLLLoss.forward(out, batch.labels)
                    loss.backward()
                    optimizer.step()
                    runningLoss += loss.impl.data[0]
                    bi++
                }
                val avgLoss = runningLoss / maxOf(1, dl.size)
                lossHistory.add(avgLoss)

                // 评估
                val (acc, preds, truths) = evaluate(test, batchSize = 200)
                lastTestPredictions = Pair(preds, truths)
                _state.value = TrainState(
                    epoch = e, batch = dl.size, loss = avgLoss,
                    acc = acc, running = isActive, msg = "Epoch $e / $epochs"
                )
                scheduler.step()
            }
            _state.value = _state.value.copy(running = false, msg = "完成")
        }
    }

    /** 评估准确率，返回 (acc, predictions, truths) */
    fun evaluate(dataset: MnistDataset, batchSize: Int = 200): Triple<Float, IntArray, IntArray> {
        model.eval()
        val dl = cn.qsbye.tinytorch.data.DataLoader(dataset, batchSize, shuffle = false)
        var correct = 0
        var total = 0
        val allPreds = mutableListOf<Int>()
        val allTruths = mutableListOf<Int>()
        for (batch in dl) {
            val preds = model.predict(batch.images)
            for (i in 0 until batch.size) {
                if (preds[i] == batch.labels[i]) correct++
                allPreds.add(preds[i])
                allTruths.add(batch.labels[i])
            }
            total += batch.size
        }
        return Triple(correct.toFloat() / maxOf(1, total), allPreds.toIntArray(), allTruths.toIntArray())
    }

    fun save() = Serializer.save(model, ckpt)
    fun load(): Boolean = Serializer.load(model, ckpt)
}
