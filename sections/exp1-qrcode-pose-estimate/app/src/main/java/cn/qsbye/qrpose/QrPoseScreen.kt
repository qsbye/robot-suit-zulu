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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

/**
 * 二维码引导定位主界面：
 * 相机预览 + 绿色锁定框 + 蓝色路径曲线 + 位姿信息。
 * 检出在位姿线程完成，结果经平滑后写入 Compose 状态驱动重绘。
 */
@Composable
fun QrPoseScreen(hasCamera: Boolean) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var quad by remember { mutableStateOf<QrQuad?>(null) }
    var path by remember { mutableStateOf<FloatArray?>(null) }
    var status by remember { mutableStateOf("对准二维码…") }

    // 位姿平滑（EMA），抑制逐帧抖动
    val smoothT = remember { arrayOfNulls<Float>(3) }

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
                    val analyzer = QrAnalyzer { q ->
                        var pathPts: FloatArray? = null
                        var text = "未检出二维码"
                        if (q != null) {
                            val pose = Pose.estimate(q.points, q.frameWidth, q.frameHeight)
                            if (pose != null) {
                                // EMA 平滑平移向量
                                for (i in 0..2) {
                                    val prev = smoothT[i]
                                    smoothT[i] = if (prev == null) pose.t[i]
                                    else prev * 0.7f + pose.t[i] * 0.3f
                                }
                                val t = FloatArray(3) { smoothT[it]!! }
                                pathPts = Pose.bezierPath(t)
                                text = "二维码中心  dx=%.2f  dy=%.2f  dz=%.2f m".format(t[0], t[1], t[2])
                            }
                        } else {
                            smoothT.fill(null)
                        }
                        val qq = q
                        mainExecutor.execute {
                            quad = qq
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

            // 叠加层：锁定框 + 路径 + 目标点
            Canvas(Modifier.fillMaxSize()) {
                val q = quad ?: return@Canvas
                // 分析帧 -> 预览视图坐标（FILL_CENTER：等比放大铺满，居中裁剪）
                val scale = maxOf(
                    size.width / q.frameWidth,
                    size.height / q.frameHeight,
                )
                val offX = (size.width - q.frameWidth * scale) / 2f
                val offY = (size.height - q.frameHeight * scale) / 2f
                fun mapX(x: Float) = x * scale + offX
                fun mapY(y: Float) = y * scale + offY

                // 绿色锁定框（四角连线）
                val green = Color(0xFF66BB6A)
                val p = q.points
                for (i in 0 until 4) {
                    val j = (i + 1) % 4
                    drawLine(
                        green,
                        Offset(mapX(p[2 * i]), mapY(p[2 * i + 1])),
                        Offset(mapX(p[2 * j]), mapY(p[2 * j + 1])),
                        strokeWidth = 4f,
                    )
                }

                // 蓝色路径曲线
                val pathPts = path
                if (pathPts != null) {
                    val blue = Color(0xFF42A5F5)
                    var prev: Offset? = null
                    for (i in 0 until pathPts.size / 3) {
                        val pt = Pose.project(
                            floatArrayOf(pathPts[3 * i], pathPts[3 * i + 1], pathPts[3 * i + 2]),
                            q.frameWidth, q.frameHeight,
                        ) ?: continue
                        val cur = Offset(mapX(pt[0]), mapY(pt[1]))
                        prev?.let { drawLine(blue, it, cur, strokeWidth = 6f) }
                        prev = cur
                    }
                    // 目标点（路径末端即二维码中心，画红点）
                    val n = pathPts.size / 3
                    if (n > 0) {
                        Pose.project(
                            floatArrayOf(
                                pathPts[3 * (n - 1)], pathPts[3 * (n - 1) + 1], pathPts[3 * (n - 1) + 2],
                            ),
                            q.frameWidth, q.frameHeight,
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
