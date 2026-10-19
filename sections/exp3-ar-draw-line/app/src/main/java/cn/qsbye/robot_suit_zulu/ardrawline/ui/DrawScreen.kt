package cn.qsbye.robot_suit_zulu.ardrawline.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.qsbye.robot_suit_zulu.ardrawline.DrawViewModel
import cn.qsbye.robot_suit_zulu.ardrawline.OrientationTracker
import cn.qsbye.robot_suit_zulu.ardrawline.Projection
import cn.qsbye.robot_suit_zulu.ardrawline.Stroke
import kotlinx.coroutines.delay

/** 笔刷色板（对应原版笔刷选择区） */
private val palette = listOf(
    0xFFFFFFFF, 0xFFFF5252, 0xFFFFB74D, 0xFFFFEE58,
    0xFF66BB6A, 0xFF4DD0E1, 0xFF5C6BC0, 0xFFBA68C8,
)

/** 笔刷粗细档位（米），对应原版 BrushSelector */
private val brushWidths = listOf(
    "细" to 0.006f, "中" to 0.015f, "粗" to 0.035f,
)

/** 画线距离档位（米）：决定手指触点映射到多远的空间平面 */
private val distances = listOf(
    "近" to 0.7f, "中" to 1.5f, "远" to 3.0f,
)

@Composable
fun DrawScreen(vm: DrawViewModel, tracker: OrientationTracker, hasCamera: Boolean) {
    var showInfo by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1116)),
    ) {
        if (hasCamera) {
            CameraPreview(Modifier.fillMaxSize())
        } else {
            Text(
                "未授予相机权限——仍可在深色背景上体验“天空画线”\n（相机画面仅作背景，不影响笔画锚定）",
                color = Color(0x88FFFFFF),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        // 追踪状态指示（对应原版 TrackingIndicator）
        TrackingIndicator(tracker, Modifier.align(Alignment.TopCenter))

        // ⓘ 说明按钮
        Text(
            "ⓘ",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .clickable { showInfo = true }
                .padding(4.dp),
        )

        // 3D 笔画渲染层 + 触摸绘画
        DrawingCanvas(
            vm, tracker,
            Modifier
                .fillMaxSize()
                .align(Alignment.Center),
        )

        // 底部控制栏
        ControlBar(
            vm, tracker,
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
        )
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            confirmButton = { TextButton(onClick = { showInfo = false }) { Text("开始画线") } },
            title = { Text("AR 画线 · FOSS 版") },
            text = {
                Text(
                    "转动手机瞄准，按住屏幕拖动即可在空中画线；线条锚定在传感器的世界坐标系里，不会跟着屏幕跑。\n\n" +
                        "· 色板 / 细中粗 / 近中远：调整笔刷与画线距离\n" +
                        "· 保存：导出 PNG 到相册 Pictures/RobotSuitZulu-AR\n\n" +
                        "FOSS 说明：纯 CameraX + 旋转矢量传感器 + Compose 实现，" +
                        "不含 ARCore / Firebase / 遥测。追踪为 3DoF（仅转动），" +
                        "平移手机时线条不产生视差。",
                )
            },
        )
    }
}

/** 传感器追踪状态点：绿=高、黄=中、红=低、灰=无传感器 */
@Composable
private fun TrackingIndicator(tracker: OrientationTracker, modifier: Modifier = Modifier) {
    var accuracy by remember {
        mutableIntStateOf(android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)
    }
    LaunchedEffect(tracker) {
        while (true) {
            accuracy = tracker.accuracy
            delay(500)
        }
    }
    val (dot, label) = when {
        !tracker.available -> Color(0xFF9E9E9E) to "无姿态传感器"
        accuracy == android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH ->
            Color(0xFF66BB6A) to "追踪中"
        accuracy == android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ->
            Color(0xFFFFEE58) to "追踪中（中）"
        else -> Color(0xFFFF5252) to "信号弱"               // LOW / UNRELIABLE
    }
    Surface(
        modifier = modifier.padding(top = 12.dp),
        shape = CircleShape,
        color = Color(0x66000000),
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(10.dp)
                    .background(dot, CircleShape),
            )
            Spacer(Modifier.size(8.dp))
            Text(label, color = Color.White, fontSize = 12.sp)
        }
    }
}

/**
 * 笔画渲染层：每帧把世界系 3D 点重投影到屏幕（对应原版 c.c GLStrokeRenderer，
 * FOSS 版用 Compose Canvas 的 2D 线段近似 GL 三角带）。
 */
@Composable
private fun DrawingCanvas(vm: DrawViewModel, tracker: OrientationTracker, modifier: Modifier = Modifier) {
    // 每帧泵：姿态在传感器线程持续更新，需驱动 Canvas 逐帧重绘
    val tick = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { }
            tick.intValue++
        }
    }
    Canvas(
        modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    vm.beginStroke(tracker.latest(), down.position.x, down.position.y, w, h)
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.pressed }
                        if (change == null) break
                        vm.extendStroke(
                            tracker.latest(), change.position.x, change.position.y, w, h,
                        )
                    }
                    vm.endStroke()
                }
            },
    ) {
        tick.intValue // 订阅重绘
        val r = tracker.latest()
        val w = size.width
        val h = size.height
        val focal = Projection.focalPx(w)

        fun drawStroke(stroke: Stroke) {
            val color = Color(stroke.colorArgb)
            if (stroke.points.size == 1) {
                val p = Projection.worldToScreen(stroke.points[0], r, w, h) ?: return
                val radius = (stroke.widthMeters * focal / p[2]).coerceIn(1.5f, 400f) / 2f
                drawCircle(color, radius, Offset(p[0], p[1]))
                return
            }
            for (i in 1 until stroke.points.size) {
                val a = Projection.worldToScreen(stroke.points[i - 1], r, w, h) ?: continue
                val b = Projection.worldToScreen(stroke.points[i], r, w, h) ?: continue
                // 线宽随深度缩放：近粗远细（透视一致）
                val widthPx = (stroke.widthMeters * focal / ((a[2] + b[2]) / 2f))
                    .coerceIn(1.5f, 400f)
                drawLine(color, Offset(a[0], a[1]), Offset(b[0], b[1]), widthPx, StrokeCap.Round)
            }
        }

        vm.strokes.forEach(::drawStroke)
        vm.currentStroke?.let(::drawStroke)
    }
}

/** 底部控制栏：色板 + 笔刷粗细 + 画线距离 + 撤销/清空/保存 */
@Composable
private fun ControlBar(vm: DrawViewModel, tracker: OrientationTracker, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenAspect = configuration.screenWidthDp.toFloat() /
        configuration.screenHeightDp.toFloat().coerceAtLeast(1f)
    Surface(
        modifier = modifier,
        color = Color(0xB314181D),
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            // 色板
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                palette.forEach { argb ->
                    val selected = vm.brushColor == Color(argb)
                    Box(
                        Modifier
                            .size(if (selected) 34.dp else 28.dp)
                            .background(Color(argb), CircleShape)
                            .then(
                                if (selected) Modifier.border(2.dp, Color.White, CircleShape)
                                else Modifier,
                            )
                            .clickable { vm.brushColor = Color(argb) },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 粗细档位
                brushWidths.forEach { (label, width) ->
                    val selected = vm.brushWidthMeters == width
                    Text(
                        label,
                        color = if (selected) Color(0xFFFFEE58) else Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { vm.brushWidthMeters = width }
                            .padding(8.dp),
                    )
                }
                Text("｜", color = Color(0x44FFFFFF))
                // 距离档位
                distances.forEach { (label, distance) ->
                    val selected = vm.drawDistanceMeters == distance
                    Text(
                        label,
                        color = if (selected) Color(0xFF4DD0E1) else Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { vm.drawDistanceMeters = distance }
                            .padding(8.dp),
                    )
                }
                Text("｜", color = Color(0x44FFFFFF))
                TextButton(onClick = { vm.undo() }) { Text("撤销") }
                TextButton(onClick = { vm.clear() }) { Text("清空") }
                TextButton(onClick = {
                    SnapshotExporter.export(
                        context, vm.strokes, tracker.latest(), screenAspect,
                    )
                }) { Text("保存") }
            }
        }
    }
}
