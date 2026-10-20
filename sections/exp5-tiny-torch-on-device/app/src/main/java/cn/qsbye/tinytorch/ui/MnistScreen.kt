package cn.qsbye.tinytorch.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.set
import cn.qsbye.tinytorch.data.MnistDataset
import kotlinx.coroutines.launch

@Composable
fun MnistScreen(
    trainer: Trainer,
    trainDs: MnistDataset?,
    testDs: MnistDataset?,
    onStartTrain: () -> Unit
) {
    val st by trainer.state.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("实验五：TinyTorch 端上训练", style = MaterialTheme.typography.headlineSmall)

        // 状态区
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("状态: ${st.msg}")
            Text("Loss: ${"%.4f".format(st.loss)}")
            Text("Acc: ${"%.2f".format(st.acc * 100)}%")
        }

        // 按钮区
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (trainDs != null && testDs != null) {
                        scope.launch { trainer.fit(trainDs, testDs, epochs = 10) }
                    } else onStartTrain()
                },
                enabled = !st.running && trainDs != null
            ) { Text(if (trainDs == null) "加载数据..." else "开始训练") }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        trainer.evaluate(testDs ?: return@launch)
                    }
                },
                enabled = !st.running && testDs != null
            ) { Text("评估") }

            OutlinedButton(onClick = { trainer.save() }, enabled = !st.running) { Text("保存") }
            OutlinedButton(onClick = { trainer.load() }, enabled = !st.running) { Text("加载") }
        }

        // 损失曲线
        Text("Loss 曲线")
        LossChart(losses = trainer.lossHistory, modifier = Modifier.fillMaxWidth().height(120.dp))

        // 样本预览
        if (testDs != null && trainer.lastTestPredictions.first.isNotEmpty()) {
            Text("样本预测（前8）")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(8) { i ->
                    val pred = trainer.lastTestPredictions.first[i]
                    val truth = trainer.lastTestPredictions.second[i]
                    val ok = pred == truth
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            bitmap = mnistBitmap(testDs.images[i]).asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp)
                        )
                        Text("$pred", color = if (ok) Color(0xFF4CAF50) else Color(0xFFF44336))
                        Text("($truth)", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun LossChart(losses: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (losses.size < 2) return@Canvas
        val maxL = losses.max()!!; val minL = losses.min()!!
        val range = (maxL - minL).coerceAtLeast(1e-6f)
        val stepX = size.width / (losses.size - 1)
        val path = androidx.compose.ui.graphics.Path()
        losses.forEachIndexed { i, l ->
            val x = i * stepX
            val y = size.height - (l - minL) / range * size.height
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color = Color(0xFF2196F3), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
    }
}

fun mnistBitmap(img: ByteArray): Bitmap {
    val bmp = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888)
    for (i in 0 until 784) {
        val v = img[i].toInt() and 0xFF
        bmp[i % 28, i / 28] = android.graphics.Color.rgb(v, v, v)
    }
    return bmp
}
