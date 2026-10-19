package cn.qsbye.switchpageimu

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

/**
 * 笔记[2] XML 布局的 Compose 复刻：
 * 顶部导航栏（进入设置 / 标题 / 连接蓝牙 / 关于本应用）
 * 中部信息显示区（摄像头画面占位 + 电量/速度/旋转角度状态）
 * 底部控制区（日志区 + 虚拟手柄）
 */
@Composable
fun BleControlPage(ble: BleController) {
    val s by ble.ui
    Column(Modifier.fillMaxWidth()) {

        // ---- 顶部导航栏 ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFB8D38F)) // 马卡龙抹茶奶霜
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "进入设置",
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xFFAE716E), androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                    .clickable { ble.log("进入设置...") }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Text("BLE遥控", fontSize = 14.sp, color = Color.Black)
            Text(
                "连接蓝牙",
                fontSize = 14.sp,
                color = Color(0xFF37474F),
                modifier = Modifier.clickable { ble.connect() }
            )
            Text(
                "关于本应用",
                fontSize = 14.sp,
                color = Color(0xFFEF5767),
                modifier = Modifier.clickable { ble.log("作者:qsbye") }
            )
        }

        // ---- 中部信息显示区 ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFF1CCB8)) // 盐焦糖杏仁饼
        ) {
            // 摄像头画面占位（对应 @mipmap/no_signal）
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF424242)),
                contentAlignment = Alignment.Center
            ) {
                Text("no signal", color = Color.White, fontSize = 10.sp)
            }
            // 状态显示区
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text("电量状态：100%", fontSize = 14.sp)
                Text("速度,加速度：0 m/s, 0 m/s²", fontSize = 14.sp)
                Text("机身旋转角度：0°", fontSize = 14.sp)
                Text("👈摄像头", fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
            }
        }

        // ---- 底部控制区 ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(Color(0xFFEF5767)) // 马卡龙基尔
        ) {
            // 日志显示区
            val scrollState = rememberScrollState()
            LaunchedEffect(s.logs.size) { scrollState.animateScrollTo(scrollState.maxValue) }
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .background(Color.White)
                    .verticalScroll(scrollState)
                    .padding(4.dp)
            ) {
                Text(
                    text = if (s.logs.isEmpty()) "无日志" else s.logs.joinToString("\n"),
                    fontSize = 10.sp,
                    color = Color.Black
                )
            }
            // 控制区：虚拟手柄
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                JoystickControl(ble)
            }
        }

        // 连接状态行
        Text(
            "状态：${s.statusText}${if (s.scanning) "（扫描中...）" else ""}",
            fontSize = 12.sp,
            color = Color(0xFF6B5B3E),
            modifier = Modifier.padding(8.dp)
        )
    }
}

/** 手柄方向判定 + 1 秒消抖 + 发送指令（对应笔记[1] 的 onJoystickMoved） */
@Composable
fun JoystickControl(ble: BleController) {
    var lastResponseMs by remember { mutableLongStateOf(0L) }
    JoystickPad(Modifier.size(200.dp)) { xPercent, yPercent ->
        val now = System.currentTimeMillis()
        if (now - lastResponseMs < 1000L) return@JoystickPad
        val cmd = directionOf(xPercent, yPercent) ?: return@JoystickPad
        lastResponseMs = now
        ble.write(cmd)
    }
}

/** 主方向判定：与笔记[1] 嵌套象限判断等价 —— 水平占优则左/右，垂直占优则前/后 */
private fun directionOf(x: Float, y: Float): EspCmd? {
    val threshold = 0.1f
    if (abs(x) < threshold && abs(y) < threshold) return null
    return if (abs(x) > abs(y)) {
        if (x < 0) EspCmd.LEFT else EspCmd.RIGHT
    } else {
        if (y < 0) EspCmd.FORWARD else EspCmd.BACKWARD
    }
}

/** Compose 实现的虚拟手柄（替代笔记[1] 中自定义 View JoystickView） */
@Composable
fun JoystickPad(modifier: Modifier = Modifier, onMove: (xPercent: Float, yPercent: Float) -> Unit) {
    var knob by remember { mutableStateOf(Offset.Zero) }
    var radiusPx by remember { mutableStateOf(0f) }
    Canvas(
        modifier = modifier
            .onSizeChanged { radiusPx = minOf(it.width, it.height) / 2f }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { knob = Offset.Zero; onMove(0f, 0f) },
                    onDragCancel = { knob = Offset.Zero; onMove(0f, 0f) }
                ) { change, dragAmount ->
                    change.consume()
                    if (radiusPx > 0f) {
                        val next = knob + dragAmount
                        knob = if (next.getDistance() <= radiusPx) {
                            next
                        } else {
                            next * (radiusPx / next.getDistance())
                        }
                        onMove(knob.x / radiusPx, knob.y / radiusPx)
                    }
                }
            }
    ) {
        val r = minOf(size.width, size.height) / 2f
        // 底座
        drawCircle(Color(0x55FFFFFF), radius = r, center = center)
        drawCircle(Color(0xFF8C7A5B), radius = r, center = center, style = Stroke(width = 4f))
        // 十字参考线
        drawLine(Color(0x338C7A5B), Offset(center.x - r, center.y), Offset(center.x + r, center.y), strokeWidth = 2f)
        drawLine(Color(0x338C7A5B), Offset(center.x, center.y - r), Offset(center.x, center.y + r), strokeWidth = 2f)
        // 摇杆头（跟随手指，松手回中）
        drawCircle(Color(0xFFC05B2E), radius = r * 0.32f, center = center + knob)
        drawCircle(
            Color(0x66FFFFFF),
            radius = r * 0.12f,
            center = center + knob - Offset(r * 0.1f, r * 0.1f)
        )
    }
}
