package cn.qsbye.depthest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

/**
 * 实验四：端侧单目深度估计（On-device Monocular Depth Estimation）。
 * 纯 Kotlin 手写小网络 + 程序化合成场景端上训练 + 伪彩色深度叠加预览。
 * 呼应 DepthART「合成数据 + 学习式深度先验」思路的玩具级 FOSS 实现：
 * 无 TFLite / ONNX / ML Kit，无 INTERNET 权限。
 */
class MainActivity : ComponentActivity() {

    private var cameraGranted by mutableStateOf(false)

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            cameraGranted = granted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            cameraGranted = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                DepthScreen(cameraGranted)
            }
        }
    }
}
