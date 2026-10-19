package cn.qsbye.robot_suit_zulu.ardrawline.ui

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

/**
 * CameraX 相机预览（替代原版 RecordableSurfaceView 的取景部分）。
 * 后置摄像头 + FILL_CENTER 铺满，作为 magic-window 的现实背景。
 */
@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val future = ProcessCameraProvider.getInstance(ctx)
            future.addListener({
                try {
                    val provider = future.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview,
                    )
                } catch (_: Exception) {
                    // 相机不可用时保持深色底，天空画线模式仍可用
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
    )
}
