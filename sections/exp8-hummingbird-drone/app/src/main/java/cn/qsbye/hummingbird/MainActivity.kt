package cn.qsbye.hummingbird

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.qsbye.hummingbird.minimap.MinimapCard
import cn.qsbye.hummingbird.minimap.MinimapModel
import cn.qsbye.hummingbird.minimap.Phase
import cn.qsbye.hummingbird.minimap.Waypoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    DemoScreen()
                }
            }
        }
    }
}

@Composable
fun DemoScreen(model: MinimapModel = remember { MinimapModel() }) {
    // 帧循环驱动里程计仿真（~60Hz）
    LaunchedEffect(model) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { now ->
                if (lastNanos != 0L) {
                    model.step(((now - lastNanos) / 1e9f).coerceAtMost(0.05f))
                }
                lastNanos = now
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        // 主视角占位（后续接入 CameraX 相机预览 + Fourier 快照归巢画面）
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "主视角区",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "待接入：CameraX 相机预览 / Fourier 快照归巢画面",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0x99FFFFFF),
                textAlign = TextAlign.Center
            )
        }

        // 左上状态条
        Surface(
            Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xCC101820)
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    "实验八 · 蜂鸟无人机",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF4FC3F7)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "状态：${model.statusText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Text(
                    "里程：%.1f m".format(model.distanceM),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Text(
                    "快照：${model.waypoints.size} 个（已捕获 ${model.capturedCount}）",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }

        // 底部控制
        Row(
            Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { model.startOutbound() },
                enabled = model.phase == Phase.IDLE || model.phase == Phase.DONE
            ) { Text("去程建图") }
            Button(
                onClick = { model.startReturn() },
                enabled = model.phase == Phase.OUTBOUND_DONE
            ) { Text("返程归巢") }
            OutlinedButton(
                onClick = { model.togglePause() },
                enabled = model.phase == Phase.OUTBOUND || model.phase == Phase.RETURN
            ) { Text(if (model.paused) "继续" else "暂停") }
            OutlinedButton(onClick = { model.clear() }) { Text("清除") }
        }

        // 右下角常驻小地图
        MinimapCard(
            model,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(200.dp)
        )
    }
}
