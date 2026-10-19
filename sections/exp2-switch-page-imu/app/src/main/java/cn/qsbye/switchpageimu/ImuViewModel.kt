package cn.qsbye.switchpageimu

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlin.math.min
import kotlin.math.sqrt

/** UI 状态：一次性暴露给 Compose，传感器回调里用 copy() 增量更新，避免整包重建丢状态 */
data class ImuUiState(
    val gyroX: Float = 0f,
    val gyroY: Float = 0f,
    val gyroZ: Float = 0f,
    val accelMag: Float = 0f,
    val eventCount: Long = 0L,      // 已收到的传感器事件总数，用于确认数据通道畅通
    val ballX: Float = 0f,          // 像素坐标
    val ballY: Float = 0f,
    val page: Int = 0,              // 0 = 小球页, 1 = 说明页, 2 = BLE遥控页
    val lastShakeText: String = "尚未检测到摇一摇"
)

class ImuViewModel : ViewModel(), SensorEventListener {

    private val _ui = mutableStateOf(ImuUiState())
    val ui: State<ImuUiState> = _ui

    // ---- 画布边界（像素），由 Composable 的 onSizeChanged 写入 ----
    private var boundW = 0f
    private var boundH = 0f
    private var centered = false

    // ---- 陀螺仪积分状态 ----
    private var lastGyroTs = 0L
    private var tiltX = 0f   // 绕 x 轴积分角（前后俯仰，驱动垂直方向）
    private var tiltY = 0f   // 绕 y 轴积分角（左右横滚，驱动水平方向）

    // ---- 摇一摇状态 ----
    private var lastShakeMs = 0L

    companion object {
        const val TILT_GAIN = 900f        // 倾角(rad) -> 速度(px/s) 增益
        const val MAX_TILT = 0.9f         // 积分角钳制，防止长时间旋转后停不下来
        const val DRIFT_DECAY = 0.15f     // 每秒回零衰减比例，抑制陀螺仪零偏漂移
        const val SHAKE_THRESHOLD = 12f   // 超出重力部分的瞬时加速度阈值 m/s^2
        const val SHAKE_COOLDOWN_MS = 800L
        const val PAGE_COUNT = 3
        val PAGE_NAMES = listOf("小球页", "说明页", "遥控页")
    }

    /** 画布尺寸就绪后记录边界并首次居中 */
    fun setBounds(w: Float, h: Float) {
        if (w <= 0f || h <= 0f) return
        boundW = w; boundH = h
        if (!centered) {
            centered = true
            _ui.value = _ui.value.copy(ballX = w / 2f, ballY = h / 2f)
        }
    }

    fun ballRadius(): Float = min(boundW, boundH) * 0.06f

    /** 小球回中（按钮） */
    fun recenter() {
        if (boundW > 0f) {
            tiltX = 0f; tiltY = 0f
            _ui.value = _ui.value.copy(ballX = boundW / 2f, ballY = boundH / 2f)
        }
    }

    /** 手动切页（与摇一摇等效，便于演示与截图） */
    fun togglePage() {
        val s = _ui.value
        val newPage = (s.page + 1) % PAGE_COUNT
        _ui.value = s.copy(
            page = newPage,
            lastShakeText = "手动切页 -> 切换到${PAGE_NAMES[newPage]}"
        )
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> onGyro(event)
            Sensor.TYPE_ACCELEROMETER -> onAccel(event)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /** 陀螺仪：角速度对时间积分得倾角，倾角映射为小球速度 */
    private fun onGyro(e: SensorEvent) {
        if (lastGyroTs == 0L) { lastGyroTs = e.timestamp; return }
        val dt = (e.timestamp - lastGyroTs) / 1e9f   // event.timestamp 是纳秒
        lastGyroTs = e.timestamp
        if (dt <= 0f || dt > 0.2f) return            // 丢帧/异常间隔直接丢弃

        val wx = e.values[0]  // rad/s，绕 x 轴
        val wy = e.values[1]  // rad/s，绕 y 轴
        val wz = e.values[2]  // rad/s，绕 z 轴（仅展示，不参与控制）

        // 积分 + 轻微回零衰减（否则 MEMS 零偏会让小球自己爬）
        tiltX = ((tiltX + wx * dt) * (1f - DRIFT_DECAY * dt)).coerceIn(-MAX_TILT, MAX_TILT)
        tiltY = ((tiltY + wy * dt) * (1f - DRIFT_DECAY * dt)).coerceIn(-MAX_TILT, MAX_TILT)

        if (boundW <= 0f) return
        val vx = tiltY * TILT_GAIN   // 左右横滚 -> 水平速度
        val vy = tiltX * TILT_GAIN   // 前后俯仰 -> 垂直速度
        val r = ballRadius()
        val s = _ui.value
        val nx = (s.ballX + vx * dt).coerceIn(r, boundW - r)
        val ny = (s.ballY + vy * dt).coerceIn(r, boundH - r)
        _ui.value = s.copy(gyroX = wx, gyroY = wy, gyroZ = wz, ballX = nx, ballY = ny, eventCount = s.eventCount + 1)
    }

    /** 加速度计：模长超出重力阈值 + 冷却时间 -> 翻转页面 */
    private fun onAccel(e: SensorEvent) {
        val ax = e.values[0]; val ay = e.values[1]; val az = e.values[2]
        val mag = sqrt(ax * ax + ay * ay + az * az)
        val now = System.currentTimeMillis()
        var s = _ui.value.copy(accelMag = mag)
        val excess = mag - SensorManager.GRAVITY_EARTH
        if (excess > SHAKE_THRESHOLD && now - lastShakeMs > SHAKE_COOLDOWN_MS) {
            lastShakeMs = now
            val newPage = (s.page + 1) % PAGE_COUNT
            s = s.copy(
                page = newPage,
                lastShakeText = "摇一摇触发 -> 切换到${PAGE_NAMES[newPage]}"
            )
        }
        _ui.value = s
    }
}
