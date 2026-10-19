package cn.qsbye.robot_suit_zulu.qrpose

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * 二维码定位标签页入口：处理相机权限后宿主 [QrPoseScreen]。
 * （来自 sections/exp1-qrcode-pose-estimate 的集成）
 */
@Composable
fun QrPosePage() {
    val context = LocalContext.current
    var hasCamera by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCamera = granted }
    LaunchedEffect(Unit) {
        if (!hasCamera) launcher.launch(Manifest.permission.CAMERA)
    }
    QrPoseScreen(hasCamera)
}
