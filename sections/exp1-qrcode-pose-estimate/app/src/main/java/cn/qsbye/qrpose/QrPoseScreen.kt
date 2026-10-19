package cn.qsbye.qrpose

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

/**
 * 单个二维码的显示项：检出 + 平滑后位姿（路径仅用于最近的一个）。
 */
private class TrackedCode(
    val detection: QrDetection,
    val t: FloatArray,
)

/**
 * 二维码引导定位主界面（支持同帧多码）：
 * 相机预览 + 每个码的绿色锁定框与内容标签 + 指向最近码的蓝色路径 + 位姿信息。
 * 检出在分析线程完成，按内容做 EMA 平滑后写入 Compose 状态驱动重绘。
 */
@Composable
fun QrPoseScreen(hasCamera: Boolean) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var codes by remember { mutableStateOf<List<TrackedCode>>(emptyList()) }
    var path by remember { mutableStateOf<FloatArray?>(null) }
    var status by remember { mutableStateOf("对准二维码…") }

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
                    // 按内容（文本）维系的 EMA 平滑位姿缓存
                    val smoothT = HashMap<String, FloatArray>()
                    val analyzer = QrAnalyzer { detections ->
                        val tracked = detections.mapNotNull { d ->
                            val pose = Pose.estimate(d.points, d.frameWidth, d.frameHeight)
                                ?: return@mapNotNull null
                            val prev = smoothT[d.text]
                            val t = if (prev == null) {
                                pose.t.copyOf()
                            } else {
                                FloatArray(3) { prev[it] * 0.7f + pose.t[it] * 0.3f }
                            }
                            smoothT[d.text] = t
                            TrackedCode(d, t)
                        }
                        // 路径指向最近（dz 最小）的码
                        val nearest = tracked.minByOrNull { it.t[2] }
                        val pathPts = nearest?.let { Pose.bezierPath(it.t) }
                        val text = if (tracked.isEmpty()) "未检出二维码"
                        else buildString {
                            append("检出 ").append(tracked.size).append(" 个二维码")
                            nearest?.let {
                                append("  最近：")
                                append(it.detection.text.take(24))
                                append("  dz=")
                                append("%.2f".format(it.t[2]))
                                append("m")
                            }
                        }
                        mainExecutor.execute {
                            codes = tracked
                            path = pathPts
                            status = text
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

            // 叠加层：锁定框 + 内容标签 + 路径 + 目标点
            Canvas(Modifier.fillMaxSize()) {
                val list = codes
                if (list.isEmpty()) return@Canvas
                val first = list.first().detection
                // 分析帧 -> 预览视图坐标（FILL_CENTER：等比放大铺满，居中裁剪）
                val scale = maxOf(
                    size.width / first.frameWidth,
                    size.height / first.frameHeight,
                )
                val offX = (size.width - first.frameWidth * scale) / 2f
                val offY = (size.height - first.frameHeight * scale) / 2f
                fun mapX(x: Float) = x * scale + offX
                fun mapY(y: Float) = y * scale + offY

                val green = Color(0xFF66BB6A)
                // 每个码的绿色锁定框 + 内容标签
                for (code in list) {
                    val p = code.detection.points
                    for (i in 0 until 4) {
                        val j = (i + 1) % 4
                        drawLine(
                            green,
                            Offset(mapX(p[2 * i]), mapY(p[2 * i + 1])),
                            Offset(mapX(p[2 * j]), mapY(p[2 * j + 1])),
                            strokeWidth = 4f,
                        )
                    }
                    if (code.detection.text.isNotEmpty()) {
                        drawContext.canvas.nativeCanvas.drawText(
                            code.detection.text.take(24),
                            mapX(p[0]),
                            mapY(p[1]) - 12f,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.rgb(102, 187, 106)
                                textSize = 34f
                            },
                        )
                    }
                }

                // 蓝色路径曲线（指向最近的码）
                val pathPts = path
                if (pathPts != null) {
                    val blue = Color(0xFF42A5F5)
                    var prev: Offset? = null
                    for (i in 0 until pathPts.size / 3) {
                        val pt = Pose.project(
                            floatArrayOf(pathPts[3 * i], pathPts[3 * i + 1], pathPts[3 * i + 2]),
                            first.frameWidth, first.frameHeight,
                        ) ?: continue
                        val cur = Offset(mapX(pt[0]), mapY(pt[1]))
                        prev?.let { drawLine(blue, it, cur, strokeWidth = 6f) }
                        prev = cur
                    }
                    // 目标点（路径末端即最近码中心，画红点）
                    val n = pathPts.size / 3
                    if (n > 0) {
                        Pose.project(
                            floatArrayOf(
                                pathPts[3 * (n - 1)], pathPts[3 * (n - 1) + 1], pathPts[3 * (n - 1) + 2],
                            ),
                            first.frameWidth, first.frameHeight,
                        )?.let {
                            drawCircle(
                                Color(0xFFEF5350),
                                radius = 8f,
                                center = Offset(mapX(it[0]), mapY(it[1])),
                            )
                        }
                    }
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
                status,
                color = Color.White,
                fontSize = 13.sp,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}
