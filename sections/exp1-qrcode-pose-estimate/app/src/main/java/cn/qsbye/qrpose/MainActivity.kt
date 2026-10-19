package cn.qsbye.qrpose

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
 * 实验一：基于二维码的引导定位（QR-guided Localization）。
 * 检测二维码 → 单应分解估计位姿 → 蓝色贝塞尔路径叠加。
 * 纯 Kotlin + Compose + CameraX + ZXing，无专有服务。
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
                QrPoseScreen(cameraGranted)
            }
        }
    }
}
