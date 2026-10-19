package cn.qsbye.robot_suit_zulu.ardrawline

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import cn.qsbye.robot_suit_zulu.ardrawline.ui.DrawScreen

/**
 * AR 画线（FOSS 版 Just a Line）主界面。
 * 复刻自逆向报告中 DrawARActivity 的本地绘画行为，去掉全部专有服务：
 *  - ARCore 6DoF 追踪 -> 游戏旋转矢量传感器 3DoF
 *  - Firebase 协作/遥测 -> 移除（纯单机，无网络权限）
 *  - 视频录制 -> PNG 快照导出
 */
class MainActivity : ComponentActivity() {

    private val vm: DrawViewModel by viewModels()
    private lateinit var tracker: OrientationTracker

    private var cameraGranted by androidx.compose.runtime.mutableStateOf(false)

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            cameraGranted = granted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tracker = OrientationTracker(
            getSystemService(Context.SENSOR_SERVICE) as android.hardware.SensorManager,
        )
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            cameraGranted = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                DrawScreen(vm, tracker, cameraGranted)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        tracker.start()
    }

    override fun onStop() {
        tracker.stop()
        super.onStop()
    }
}
