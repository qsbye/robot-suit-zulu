package cn.qsbye.robot_suit_zulu.draw

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * 姿态追踪器：FOSS 版用"游戏旋转矢量"传感器替代专有 ARCore 的 6DoF 追踪。
 *
 * 输出 3x3 旋转矩阵（设备系 -> 世界系），每帧供投影使用。
 * 局限：只有 3DoF（转动），不追踪平移——抬起手机平移时线条不会产生视差，
 * 这是与 ARCore 版的本质差别（详见 section README）。
 */
class OrientationTracker(private val sensorManager: SensorManager) : SensorEventListener {

    /** 优先游戏旋转矢量（不依赖磁力计，线条更稳）；缺失时回退普通旋转矢量 */
    private val sensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    /** 最新旋转矩阵（行优先，设备->世界）；传感器线程整体换引用，UI 线程直接读 */
    @Volatile
    private var rotationMatrix: FloatArray = identity()

    /** 传感器精度（SensorManager.SENSOR_STATUS_*），供追踪指示灯使用 */
    @Volatile
    var accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM
        private set

    val available: Boolean get() = sensor != null

    fun start() {
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    /** 当前旋转矩阵快照（设备->世界） */
    fun latest(): FloatArray = rotationMatrix

    override fun onSensorChanged(event: SensorEvent) {
        val m = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(m, event.values)
        rotationMatrix = m
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        this.accuracy = accuracy
    }

    companion object {
        fun identity(): FloatArray = floatArrayOf(
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f,
        )
    }
}
