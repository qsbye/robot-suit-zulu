package cn.qsbye.robot_suit_zulu.draw

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
 * 绘画标签页入口：处理相机权限后宿主 [DrawScreen]。
 * （来自 sections/exp3-ar-draw-line 的 FOSS 版 Just a Line 集成）
 */
@Composable
fun DrawPage(vm: DrawViewModel, tracker: OrientationTracker) {
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
    DrawScreen(vm, tracker, hasCamera)
}
