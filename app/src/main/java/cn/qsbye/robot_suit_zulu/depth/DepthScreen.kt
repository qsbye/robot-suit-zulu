@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package cn.qsbye.robot_suit_zulu.depth

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

/**
 * 端侧单目深度估计主界面：
 * 相机预览 + 伪彩色深度叠加（原图/混合/深度三档）+ 端上训练面板（损失曲线）。
 * 网络为纯 Kotlin 手写实现，点「开始训练」在真机上用程序化合成场景训练数十秒。
 */
@Composable
fun DepthScreen(hasCamera: Boolean) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val engine = remember { DepthEngine() }

    // 0=原图 1=混合 2=深度
    var mode by remember { mutableIntStateOf(1) }
    var framePixels by remember { mutableStateOf<IntArray?>(null) }
    var training by remember { mutableStateOf(false) }
    val progressHistory = remember { mutableStateListOf<DepthTrainer.Progress>() }
    var result by remember { mutableStateOf<DepthTrainer.Result?>(null) }
    val previewPixels = remember { engine.syntheticPreview() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1116)),
    ) {
        if (hasCamera) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val analysisExecutor = Executors.newSingleThreadExecutor()
                    val mainExecutor = ContextCompat.getMainExecutor(ctx)
                    val analyzer = ImageAnalysis.Analyzer { proxy: ImageProxy ->
                        try {
                            val img = proxy.image ?: return@Analyzer
                            val rotation = proxy.imageInfo.rotationDegrees
                            val w = img.width
                            val h = img.height
                            val data = extractLuma(proxy, w, h)
                            val px = engine.onRawFrame(data, w, h, rotation)
                            if (px != null) {
                                mainExecutor.execute { framePixels = px }
                            }
                        } catch (_: Exception) {
                        } finally {
                            proxy.close()
                        }
                    }
                    val future = ProcessCameraProvider.getInstance(ctx)
                    future.addListener({
                        try {
                            val provider = future.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val analysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST,
                                )
                                .build()
                                .also { it.setAnalyzer(analysisExecutor, analyzer) }
                            provider.unbindAll()
                            provider.bindToLifecycle(
                                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA,
                                preview, analysis,
                            )
                        } catch (_: Exception) {
                            // 相机不可用时保持深色底
                        }
                    }, mainExecutor)
                    previewView
                },
            )

            // 深度伪彩色叠加
            Canvas(Modifier.fillMaxSize()) {
                val px = framePixels
                if (px != null && mode != 0) {
                    drawDepthOverlay(px, engine.gridW, engine.gridH, if (mode == 1) 0.6f else 1f)
                }
            }
        } else {
            Text(
                "未授予相机权限",
                color = Color(0x88FFFFFF),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        // 顶部状态条
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp),
            shape = CircleShape,
            color = Color(0x66000000),
        ) {
            Text(
                when {
                    training -> "端上训练中…（合成场景 mini-batch Adam）"
                    engine.isTrained && result != null ->
                        "推理中 · 网格 ${engine.gridW}×${engine.gridH} · 参数 ${result!!.parameterCount}" +
                            " · 验证 MSE ${"%.4f".format(result!!.finalValLoss)}"
                    else -> "网络未训练 · 点「开始训练」（真机数十秒收敛）"
                },
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }

        // 待机时的合成样本示意
        if (framePixels == null && !training) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xB3000000),
            ) {
                Column(
                    Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "合成训练样本示意（深度真值）",
                        color = Color(0xAAFFFFFF),
                        fontSize = 11.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Canvas(
                        Modifier
                            .width(180.dp)
                            .height(135.dp),
                    ) {
                        drawDepthOverlay(previewPixels, engine.gridW, engine.gridH, 1f)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "纯 Kotlin 小网络（~465 参数）学习\n「逐格特征 → 相对深度」先验",
                        color = Color(0x88FFFFFF),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                    )
                }
            }
        }

        // 底部控制面板
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xB3000000),
        ) {
            Column(Modifier.padding(12.dp)) {
                // 显示模式切换
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("原图", "混合", "深度").forEachIndexed { i, label ->
                        FilterChip(
                            selected = mode == i,
                            onClick = { mode = i },
                            label = { Text(label, fontSize = 12.sp) },
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    if (training) {
                        OutlinedButton(
                            onClick = { engine.cancelTraining() },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 14.dp, vertical = 6.dp,
                            ),
                        ) { Text("停止", fontSize = 12.sp) }
                    } else {
                        Button(
                            onClick = {
                                progressHistory.clear()
                                result = null
                                training = true
                                engine.startTraining(
                                    onProgress = { p ->
                                        progressHistory.add(p)
                                    },
                                    onDone = { r ->
                                        training = false
                                        result = r
                                    },
                                )
                            },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 14.dp, vertical = 6.dp,
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF42A5F5),
                            ),
                        ) {
                            Text(
                                if (engine.isTrained) "重新训练" else "开始训练",
                                fontSize = 12.sp,
                            )
                        }
                    }
                }

                // 训练进度与损失曲线
                if (progressHistory.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    val last = progressHistory.last()
                    Text(
                        buildString {
                            append("epoch ${last.epoch}/${last.totalEpochs}")
                            append("  train=${"%.4f".format(last.trainLoss)}")
                            if (last.valLoss.isFinite()) append("  val=${"%.4f".format(last.valLoss)}")
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                    )
                    Spacer(Modifier.height(4.dp))
                    Canvas(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    ) { drawLossCurve(progressHistory.toList()) }
                }

                // 结果摘要
                result?.let { r ->
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "训练完成：${r.epochsRun} 轮 · ${(r.elapsedMs / 1000f).format1()}s · " +
                            "样本 ${r.sampleCount} 格 · train ${"%.4f".format(r.finalTrainLoss)} → " +
                            "val ${"%.4f".format(r.finalValLoss)}",
                        color = Color(0xAAFFFFFF),
                        fontSize = 11.sp,
                    )
                }
            }
        }
    }
}

/** 把 gridW×gridH 的 ARGB 像素按全屏拉伸绘制（双线性）。 */
private fun DrawScope.drawDepthOverlay(pixels: IntArray, gw: Int, gh: Int, alpha: Float) {
    val bmp = Bitmap.createBitmap(gw, gh, Bitmap.Config.ARGB_8888)
    bmp.setPixels(pixels, 0, gw, 0, 0, gw, gh)
    drawImage(
        bmp.asImageBitmap(),
        srcSize = androidx.compose.ui.unit.IntSize(gw, gh),
        dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), size.height.toInt()),
        alpha = alpha,
        filterQuality = FilterQuality.Medium,
    )
}

/** 训练/验证损失双曲线（横轴 epoch，纵轴按最大值归一）。 */
private fun DrawScope.drawLossCurve(history: List<DepthTrainer.Progress>) {
    if (history.size < 2) return
    val maxLoss = history.maxOf { maxOf(it.trainLoss, if (it.valLoss.isFinite()) it.valLoss else 0f) }
        .coerceAtLeast(1e-6f)
    fun mapX(i: Int) = size.width * i / (history.size - 1)
    fun mapY(v: Float) = size.height * (1f - (v / maxLoss).coerceIn(0f, 1f))

    val trainColor = Color(0xFF42A5F5)
    val valColor = Color(0xFFEF5350)
    for (pair in listOf(
        trainColor to history.map { it.trainLoss },
        valColor to history.map { it.valLoss }.map { if (it.isFinite()) it else Float.NaN },
    )) {
        val (color, values) = pair
        var prev: Offset? = null
        for (i in values.indices) {
            val v = values[i]
            if (v.isNaN()) { prev = null; continue }
            val cur = Offset(mapX(i), mapY(v))
            prev?.let { drawLine(color, it, cur, strokeWidth = 3f) }
            prev = cur
        }
    }
    // 标尺基线
    drawLine(Color(0x33FFFFFF), Offset(0f, size.height), Offset(size.width, size.height), 1f)
}

/** 提取 Y 平面为紧缩字节数组（处理行/像素步长）。 */
@OptIn(ExperimentalGetImage::class)
private fun extractLuma(imageProxy: ImageProxy, w: Int, h: Int): ByteArray {
    val yPlane = imageProxy.planes[0]
    val rowStride = yPlane.rowStride
    val pixelStride = yPlane.pixelStride
    val buffer = yPlane.buffer
    return when {
        pixelStride == 1 && rowStride == w ->
            ByteArray(buffer.remaining()).also { buffer.get(it) }
        pixelStride == 1 -> {
            val tight = ByteArray(w * h)
            val row = ByteArray(w)
            for (y in 0 until h) {
                buffer.position(y * rowStride)
                buffer.get(row)
                System.arraycopy(row, 0, tight, y * w, w)
            }
            tight
        }
        else -> {
            val tight = ByteArray(w * h)
            for (y in 0 until h) {
                buffer.position(y * rowStride)
                for (x in 0 until w) tight[y * w + x] = buffer.get()
            }
            tight
        }
    }
}

private fun Float.format1(): String = String.format("%.1f", this)
