package cn.qsbye.robot_suit_zulu

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/** 笔记[1] 中与 esp32 约定的指令帧（hex 字符串 -> 字节数组后直接写入 RX 特征） */
enum class EspCmd(val label: String, val hex: String) {
    LED_TOGGLE("led_toggle", "3c3100006c656420746f67676c653b00000000000000000000000000000000000000000001003e00"),
    LED_ON("led_on", "3c3100006c6564206f6e3b0000000000000000000000000000000000000000000000000001003e00"),
    LED_OFF("led_off", "3c3100006c6564206f66663b00000000000000000000000000000000000000000000000001003e00"),
    LEFT("left", "3c3100006c6566743b00000000000000000000000000000000000000000000000000000001003e00"),
    RIGHT("right", "3c31000072696768743b000000000000000000000000000000000000000000000000000001003e00"),
    FORWARD("forward", "3c310000666f72776172643b00000000000000000000000000000000000000000000000001003e00"),
    BACKWARD("backward", "3c3100006261636b776172643b000000000000000000000000000000000000000000000001003e00");

    val bytes: ByteArray get() = hexToBytes(hex)
}

fun hexToBytes(hex: String): ByteArray = ByteArray(hex.length / 2) { i ->
    hex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
}

data class BleUiState(
    val connected: Boolean = false,
    val scanning: Boolean = false,
    val statusText: String = "未连接",
    val logs: List<String> = emptyList(),
)

/**
 * 纯 Android BLE API 实现的 esp32 遥控客户端（替代笔记[1] 中的 RxBLE/RxJava 封装）。
 * 扫描 Nordic UART 设备 -> 连接 GATT -> 开启 TX 通知 -> 向 RX 特征写指令。
 */
@SuppressLint("MissingPermission") // 运行时权限由 MainActivity 统一申请，这里所有 SecurityException 均已兜底
class BleController(context: Context) {

    private val appContext = context.applicationContext

    private val _ui = mutableStateOf(BleUiState())
    val ui: State<BleUiState> = _ui

    companion object {
        const val DEVICE_NAME = "ESP BLE Uart"
        val SERVICE_UUID: UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        val CHAR_RX_UUID: UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E") // 手机 -> esp32
        val CHAR_TX_UUID: UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E") // esp32 -> 手机(notify)
        val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        const val SCAN_TIMEOUT_MS = 3000L
        const val MAX_LOG_LINES = 25
    }

    private val adapter: BluetoothAdapter? =
        (appContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
    private val handler = Handler(Looper.getMainLooper())
    private var gatt: BluetoothGatt? = null
    private var rxChar: BluetoothGattCharacteristic? = null

    init {
        log("🎉进入应用!")
        log("👏欢迎!")
        log("帮助: 1. 连接蓝牙; 2. 移动手柄控制机器人;")
    }

    fun log(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _ui.value = _ui.value.copy(logs = (_ui.value.logs + "$time $msg").takeLast(MAX_LOG_LINES))
    }

    fun connect() {
        val ad = adapter
        if (ad == null) {
            log("设备不支持蓝牙")
            return
        }
        if (!ad.isEnabled) {
            log("蓝牙未开启，请先在系统设置中打开蓝牙")
            return
        }
        log("连接蓝牙...")
        stopScan(logTimeout = false)
        _ui.value = _ui.value.copy(scanning = true, statusText = "扫描中...")
        try {
            ad.bluetoothLeScanner?.startScan(scanCallback) ?: log("无法获取 BLE 扫描器")
        } catch (e: SecurityException) {
            log("缺少蓝牙权限，无法扫描")
            _ui.value = _ui.value.copy(scanning = false, statusText = "缺少权限")
            return
        }
        handler.postDelayed({ stopScan(logTimeout = true) }, SCAN_TIMEOUT_MS)
    }

    private fun stopScan(logTimeout: Boolean) {
        if (!_ui.value.scanning) return
        _ui.value = _ui.value.copy(scanning = false)
        try {
            adapter?.bluetoothLeScanner?.stopScan(scanCallback)
        } catch (_: SecurityException) {
        }
        if (logTimeout && !_ui.value.connected) log("扫描结束，未找到 $DEVICE_NAME")
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device ?: return
            val name = try {
                device.name
            } catch (_: SecurityException) {
                null
            }
            if (name?.contains(DEVICE_NAME) == true) {
                stopScan(logTimeout = false)
                log("已找到设备: $name")
                _ui.value = _ui.value.copy(statusText = "连接中...")
                gatt = try {
                    device.connectGatt(appContext, false, gattCallback)
                } catch (_: SecurityException) {
                    log("缺少蓝牙权限，无法连接")
                    null
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            log("扫描失败: $errorCode")
            _ui.value = _ui.value.copy(scanning = false, statusText = "扫描失败")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    log("GATT 已连接，发现服务中...")
                    try {
                        g.discoverServices()
                    } catch (_: SecurityException) {
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    log("已断开")
                    rxChar = null
                    _ui.value = _ui.value.copy(connected = false, statusText = "未连接")
                }
            }
        }

        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            val service = g.getService(SERVICE_UUID)
            val tx = service?.getCharacteristic(CHAR_TX_UUID)
            rxChar = service?.getCharacteristic(CHAR_RX_UUID)
            if (service == null || tx == null || rxChar == null) {
                log("未找到 UART 服务/特征")
                return
            }
            // 注册通知：setCharacteristicNotification + 写 CCCD
            try {
                g.setCharacteristicNotification(tx, true)
                val cccd = tx.getDescriptor(CCCD_UUID)
                @Suppress("DEPRECATION")
                cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                @Suppress("DEPRECATION")
                g.writeDescriptor(cccd)
            } catch (_: SecurityException) {
                log("缺少蓝牙权限，开启通知失败")
            }
        }

        override fun onDescriptorWrite(g: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log("已连接 $DEVICE_NAME，通知已开启")
                _ui.value = _ui.value.copy(connected = true, statusText = "已连接 $DEVICE_NAME")
                write(EspCmd.LED_TOGGLE) // 连接成功后翻转一次小灯
            } else {
                log("开启通知失败: $status")
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            @Suppress("DEPRECATION")
            val data = characteristic.value ?: return
            val text = String(data).trim(' ')
            if (text.isNotEmpty()) log("接收到: $text")
        }
    }

    /** 写入指令（等价于笔记[1] 的 writeData(hexToString(cmd))） */
    fun write(cmd: EspCmd) {
        val g = gatt
        val c = rxChar
        if (g == null || c == null || !_ui.value.connected) {
            log("未连接，忽略指令: ${cmd.label}")
            return
        }
        try {
            @Suppress("DEPRECATION")
            c.value = cmd.bytes
            @Suppress("DEPRECATION")
            if (g.writeCharacteristic(c)) {
                log("已发送: ${cmd.label}")
            }
        } catch (_: SecurityException) {
            log("缺少蓝牙权限，发送失败")
        }
    }
}
