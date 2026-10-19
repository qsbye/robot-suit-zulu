package cn.qsbye.switchpageimu

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.activity.viewModels

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var gyro: Sensor? = null
    private var accel: Sensor? = null
    // 用 by viewModels() 在 onCreate 之前即建好 ViewModel，
    // 避免原实现里 onResume 时 Compose 尚未执行、vm 为 null 导致监听从未注册
    private val vm: ImuViewModel by viewModels()
    private val ble by lazy { BleController(applicationContext) }

    // BLE 运行时权限：Android 12+ 用 BLUETOOTH_SCAN/CONNECT，更早版本用定位权限
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        requestBlePermissions()
        setContent {
            MaterialTheme { AppRoot(vm, ble, gyro != null, accel != null) }
        }
    }

    private fun requestBlePermissions() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        permissionLauncher.launch(perms)
    }

    override fun onResume() {
        super.onResume()
        // GAME 档约 50Hz，兼顾响应速度与功耗
        gyro?.let { sensorManager.registerListener(vm, it, SensorManager.SENSOR_DELAY_GAME) }
        accel?.let { sensorManager.registerListener(vm, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(vm)
    }
}

@Composable
fun AppRoot(vm: ImuViewModel, ble: BleController, hasGyro: Boolean, hasAccel: Boolean) {
    val s by vm.ui
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F1E8))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "${ImuViewModel.PAGE_NAMES[s.page]}（第 ${s.page + 1} 页，共 ${ImuViewModel.PAGE_COUNT} 页）",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.clickable { vm.togglePage() }
        )
        SensorCard(s, hasGyro, hasAccel)
        when (s.page) {
            0 -> BallPage(vm)
            1 -> InfoPage()
            else -> BleControlPage(ble)
        }
        Text(s.lastShakeText, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B5B3E))
    }
}

@Composable
fun SensorCard(s: ImuUiState, hasGyro: Boolean, hasAccel: Boolean) {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("陀螺仪 rad/s：x=%.3f  y=%.3f  z=%.3f".format(s.gyroX, s.gyroY, s.gyroZ))
            Text("加速度模长 m/s²：%.2f（静止应≈9.8）".format(s.accelMag))
            Text("已收到传感器事件：%d 个".format(s.eventCount))
            if (s.eventCount == 0L) {
                Text("未收到任何传感器事件：请到系统设置授予本应用传感器权限", color = Color.Red)
            }
            if (!hasGyro) Text("警告：设备无陀螺仪", color = Color.Red)
            if (!hasAccel) Text("警告：设备无加速度计", color = Color.Red)
        }
    }
}

@Composable
fun BallPage(vm: ImuViewModel) {
    val s by vm.ui
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("转动手机让小球滚动，碰壁即停")
            Spacer(Modifier.height(8.dp))
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .background(Color(0xFFEDE6D6), RoundedCornerShape(8.dp))
                    .onSizeChanged { size -> vm.setBounds(size.width.toFloat(), size.height.toFloat()) }
            ) {
                val r = vm.ballRadius()
                if (r > 0f) {
                    // 边界框
                    drawRect(
                        color = Color(0xFF8C7A5B),
                        topLeft = Offset(0f, 0f),
                        size = this.size,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )
                    drawCircle(Color(0xFFC05B2E), radius = r, center = Offset(s.ballX, s.ballY))
                    drawCircle(Color(0x66FFFFFF), radius = r * 0.35f, center = Offset(s.ballX - r * 0.3f, s.ballY - r * 0.3f))
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.recenter() }) { Text("小球回中") }
        }
    }
}

@Composable
fun InfoPage() {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("原理说明", style = MaterialTheme.typography.titleMedium)
            Text("1. 陀螺仪 TYPE_GYROSCOPE 输出三轴角速度(rad/s)，对时间积分得到手机倾角，倾角映射为小球速度：绕 y 轴转动（左右横滚）驱动水平移动，绕 x 轴转动（前后俯仰）驱动垂直移动。")
            Text("2. 积分角带轻微回零衰减并钳制在 ±0.9rad，抑制 MEMS 零偏导致的自漂移。")
            Text("3. 加速度计 TYPE_ACCELEROMETER 计算三轴模长，超出重力 9.8 的部分大于阈值 12m/s² 且距上次触发超过 800ms 时判定为摇一摇，三个页面循环切换。")
            Text("4. 遥控页通过 BLE 连接 esp32（Nordic UART 服务），拖动手柄发送 forward/backward/left/right 指令，1 秒消抖。")
        }
    }
}
