package cn.qsbye.hummingbird

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import cn.qsbye.hummingbird.homing.CameraHomingEngine
import cn.qsbye.hummingbird.homing.FourierHoming
import cn.qsbye.hummingbird.minimap.MinimapCard
import cn.qsbye.hummingbird.minimap.MinimapModel
import cn.qsbye.hummingbird.minimap.Phase
import cn.qsbye.hummingbird.minimap.Waypoint
import java.util.concurrent.Executors
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomingApp()
                }
            }
        }
    }
}

enum class Mode { SIM, CAMERA }

@Composable
fun HomingApp() {
    var mode by remember { mutableStateOf(Mode.SIM) }
    val model = remember { MinimapModel() }
    val ctx = LocalContext.current
    var hasCam by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasCam = it }

    // 切模式清空会话；仿真模式用帧循环驱动里程计（相机模式由帧分析回调驱动）
    LaunchedEffect(mode) {
        model.clear()
        if (mode != Mode.SIM) return@LaunchedEffect
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) model.step(((now - last) / 1e9f).coerceAtMost(0.05f))
                last = now
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        // 顶部：模式切换 + 状态
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(selected = mode == Mode.SIM, onClick = { mode = Mode.SIM }, label = { Text("仿真") })
            FilterChip(selected = mode == Mode.CAMERA, onClick = {
                mode = Mode.CAMERA
                if (!hasCam) permLauncher.launch(Manifest.permission.CAMERA)
            }, label = { Text("相机") })
            Column {
                Text(
                    model.statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Text(
                    "里程 %.1f m · 快照 ${model.waypoints.size}（捕获 ${model.capturedCount}）".format(model.distanceM) +
                        if (mode == Mode.CAMERA) " · 残差 %.2f".format(model.homingResidual) else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0x99FFFFFF)
                )
            }
        }

        // 主区（相机预览 / 仿真占位）+ 右下角小地图
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                mode == Mode.SIM -> SimPane(model)
                hasCam -> CameraPane(model)
                else -> PermissionPane { permLauncher.launch(Manifest.permission.CAMERA) }
            }
            MinimapCard(
                model,
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .size(180.dp)
            )
        }

        // IDF 曲线面板
        IdfCurvePanel(
            model,
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(84.dp)
        )

        // 控制（窄屏可横向滚动，避免按钮被挤出屏幕）
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (mode == Mode.SIM) model.startOutbound() else model.startOutboundCamera()
                },
                enabled = model.phase == Phase.IDLE || model.phase == Phase.DONE
            ) { Text(if (mode == Mode.SIM) "去程建图" else "开始建图") }
            Button(
                onClick = {
                    when {
                        model.phase == Phase.OUTBOUND && mode == Mode.CAMERA -> model.finishOutbound()
                        model.phase == Phase.OUTBOUND_DONE -> model.startReturn()
                    }
                },
                enabled = (model.phase == Phase.OUTBOUND && mode == Mode.CAMERA) ||
                    model.phase == Phase.OUTBOUND_DONE
            ) {
                Text(
                    if (model.phase == Phase.OUTBOUND && mode == Mode.CAMERA) "完成去程"
                    else "返程归巢"
                )
            }
            OutlinedButton(
                onClick = { model.togglePause() },
                enabled = model.phase == Phase.OUTBOUND || model.phase == Phase.RETURN
            ) { Text(if (model.paused) "继续" else "暂停") }
            OutlinedButton(onClick = { model.clear() }) { Text("清除") }
        }
    }
}

/** 仿真主区：占位说明 + 合成天际线驱动 ℰ(Δ) 曲线演示 */
@Composable
fun SimPane(model: MinimapModel) {
    LaunchedEffect(Unit) {
        val sig = FloatArray(FourierHoming.N)
        FourierHoming.syntheticSkyline(42, sig)
        val snap = FourierHoming.Snapshot.capture(sig)
        val cur = FloatArray(FourierHoming.N)
        val curve = FloatArray(FourierHoming.N)
        while (true) {
            withFrameNanos { now ->
                val shift = (24 * sin(now / 1e9f * 0.7f)).roundToInt()
                for (i in 0 until FourierHoming.N) {
                    cur[i] = sig[(i + shift + FourierHoming.N) % FourierHoming.N]
                }
                FourierHoming.idfCurve(snap, cur, curve)
                var best = 0
                for (d in 1 until FourierHoming.N) if (curve[d] < curve[best]) best = d
                model.idfCurveState = curve
                model.idfBest = best
            }
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "主视角区（仿真）",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "合成天际线驱动 ℰ(Δ) 曲线演示；\n小地图实时渲染建图 / 归巢；切「相机」体验真机归巢",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0x99FFFFFF),
                textAlign = TextAlign.Center
            )
        }
    }
}

/** 相机主区：预览 + 航向/对齐指示 + Fourier 快照归巢数据流 */
@Composable
fun CameraPane(model: MinimapModel) {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var yaw by remember { mutableFloatStateOf(0f) }

    // 旋转矢量传感器 → 航向（北=0，东=+90°）
    DisposableEffect(ctx) {
        val sm = ctx.getSystemService(SensorManager::class.java)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                if (e.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val r = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(r, e.values)
                    val remap = FloatArray(9)
                    SensorManager.remapCoordinateSystem(
                        r, SensorManager.AXIS_X, SensorManager.AXIS_Z, remap
                    )
                    val o = FloatArray(3)
                    SensorManager.getOrientation(remap, o)
                    yaw = o[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sm?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            sm.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sm?.unregisterListener(listener) }
    }

    // 相机分析线程与 CameraProvider 生命周期
    val executor = remember { Executors.newSingleThreadExecutor() }
    val providerRef = remember { mutableStateOf<ProcessCameraProvider?>(null) }
    DisposableEffect(Unit) {
        onDispose {
            providerRef.value?.unbindAll()
            executor.shutdown()
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { c ->
                val pv = PreviewView(c)
                val preview = Preview.Builder().build()
                    .also { it.setSurfaceProvider(pv.surfaceProvider) }
                val engine = CameraHomingEngine(object : CameraHomingEngine.OnFrameListener {
                    override fun onFrame(
                        signal: FloatArray,
                        dxM: Float,
                        dyM: Float,
                        movedM: Float
                    ) {
                        model.latestSignal = signal
                        // 相机系（右/下为正）→ 世界系（x 东 / y 北），yaw 自北顺时针
                        val cy = cos(yaw)
                        val sy = sin(yaw)
                        val wx = dxM * cy - dyM * sy
                        val wy = -dxM * sy - dyM * cy
                        model.ingest(wx, wy, yaw, movedM)
                        // 返程：对下一未捕获快照做 Fourier 对齐
                        if (model.phase == Phase.RETURN) {
                            val next = model.waypoints.lastOrNull {
                                it.state != Waypoint.State.CAPTURED
                            }
                            val snap = next?.snapshot
                            if (snap != null) {
                                val est = FourierHoming.align(snap, signal)
                                model.homingResidual = est.residual
                                model.homingAngleDeg = est.fineShift * 360f / FourierHoming.N
                                model.idfCurveState = est.curve
                                model.idfBest = est.bestShift
                            }
                        }
                    }
                })
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(executor, engine) }
                // 异步获取 CameraProvider，避免主线程阻塞
                val future = ProcessCameraProvider.getInstance(c)
                future.addListener({
                    val provider = future.get()
                    providerRef.value = provider
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis
                    )
                }, ContextCompat.getMainExecutor(c))
                pv
            },
            modifier = Modifier.fillMaxSize()
        )

        // 顶部航向 / 对齐指示
        Surface(
            Modifier
                .align(Alignment.TopCenter)
                .padding(12.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xCC101820)
        ) {
            Column(
                Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "航向 %.0f° · 残差 %.2f".format(
                        Math.toDegrees(yaw.toDouble()), model.homingResidual
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                if (model.phase == Phase.RETURN) {
                    Text(
                        "旋向修正 %+.1f°".format(model.homingAngleDeg),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFFFFD54F)
                    )
                }
            }
        }
    }
}

/** 相机权限引导 */
@Composable
fun PermissionPane(onRequest: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "相机归巢需要 CAMERA 权限\n（仅本地处理，不上传任何图像）",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0x99FFFFFF),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRequest) { Text("授权相机") }
    }
}

/** ℰ(Δ) 图像差函数曲线面板 */
@Composable
fun IdfCurvePanel(model: MinimapModel, modifier: Modifier = Modifier) {
    val curve = model.idfCurveState
    val best = model.idfBest
    Box(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xE0101820))
    ) {
        if (curve == null) {
            Text(
                "ℰ(Δ) 曲线 —— 返程归巢时显示当前视野 vs 快照的差能",
                Modifier.align(Alignment.Center),
                color = Color(0x66FFFFFF),
                style = MaterialTheme.typography.labelSmall
            )
        } else {
            Canvas(Modifier.fillMaxSize().padding(6.dp)) {
                var lo = Float.MAX_VALUE
                var hi = -Float.MAX_VALUE
                for (v in curve) {
                    if (v < lo) lo = v
                    if (v > hi) hi = v
                }
                val span = (hi - lo).coerceAtLeast(1e-6f)
                val w = size.width
                val h = size.height
                val path = Path()
                for (i in curve.indices) {
                    val x = w * i / (curve.size - 1f)
                    val y = h - (curve[i] - lo) / span * h
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, Color(0xFF4FC3F7), style = Stroke(2.dp.toPx()))
                val bx = w * best / (curve.size - 1f)
                drawLine(Color(0xFFFFD54F), Offset(bx, 0f), Offset(bx, h), 2.dp.toPx())
            }
            Text(
                "ℰ(Δ) min @ $best",
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp),
                color = Color(0xFFFFD54F),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
