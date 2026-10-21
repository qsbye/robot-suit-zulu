package cn.qsbye.robot_suit_zulu

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import cn.qsbye.robot_suit_zulu.depth.DepthPage
import cn.qsbye.robot_suit_zulu.dsp.HowlingSuppressor
import cn.qsbye.robot_suit_zulu.dsp.NlmsAec
import cn.qsbye.robot_suit_zulu.draw.DrawPage
import cn.qsbye.robot_suit_zulu.draw.DrawViewModel
import cn.qsbye.robot_suit_zulu.draw.OrientationTracker
import cn.qsbye.robot_suit_zulu.qrpose.QrPosePage
import cn.qsbye.robot_suit_zulu.hummingbird.HummingbirdPage
import cn.qsbye.robot_suit_zulu.quickjseditor.QuickJSPage
import cn.qsbye.robot_suit_zulu.tinytorch.ui.TinyTorchPage
import cn.qsbye.robot_suit_zulu.ui.theme.RobotSuitZuluTheme
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    private var isRecording by mutableStateOf(false)
    private var gainDb by mutableDoubleStateOf(0.0)
    // 顶层标签索引：0=功能 1=关于 2=设置
    private var selectedTab by mutableIntStateOf(0)
    // 功能页内当前选中的功能（null=功能列表，非null=功能详情）
    private var functionPage by mutableStateOf<String?>(null)
    private var volume by mutableIntStateOf(0)
    private var maxVolume by mutableIntStateOf(1)
    private var aecEnabled by mutableStateOf(true)
    private var howlingEnabled by mutableStateOf(true)

    private lateinit var audioRecord: AudioRecord
    private lateinit var audioTrack: AudioTrack
    private lateinit var executorService: ExecutorService
    private lateinit var audioManager: AudioManager
    private lateinit var silencerEngine: SilencerEngine
    private val waveformController = WaveformController()
    private val aec = NlmsAec()
    private val howlingSuppressor = HowlingSuppressor(SAMPLE_RATE)

    // 音频线程读取的使能标志（由 UI 状态镜像写入）
    @Volatile
    private var aecOn = true

    @Volatile
    private var howlingOn = true

    private val handler = Handler(Looper.getMainLooper())

    // ---- 实验二（IMU 小球 + BLE 遥控） ----
    private lateinit var sensorManager: SensorManager
    private var gyro: Sensor? = null
    private var accel: Sensor? = null
    private val imuVm: ImuViewModel by viewModels()
    private val ble by lazy { BleController(applicationContext) }

    // ---- 绘画页（FOSS 版 AR 画线） ----
    private val drawVm: DrawViewModel by viewModels()
    private val drawTracker by lazy { OrientationTracker(sensorManager) }

    // ---- 设置页 ----
    private var forcePortrait by mutableStateOf(true)
    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }

    private val blePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    private val volumePoller = object : Runnable {
        override fun run() {
            updateVolumeDisplay()
            handler.postDelayed(this, 500)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) initializeAudioComponents()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioManager = getSystemService(AudioManager::class.java)
        executorService = Executors.newSingleThreadExecutor()
        silencerEngine = SilencerEngine(applicationContext)

        sensorManager = getSystemService(SensorManager::class.java)
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        requestBlePermissions()

        // 恢复设置并应用屏幕方向
        forcePortrait = prefs.getBoolean("force_portrait", true)
        applyOrientation()

        setContent {
            RobotSuitZuluTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.fillMaxSize()) {
                        // 顶层标签页：功能 / 关于 / 设置；四个功能页移入「功能」子标签页
                        TabRow(selectedTabIndex = selectedTab) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectTab(0) },
                                text = { Text("功能") }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectTab(1) },
                                text = { Text("关于") }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { selectTab(2) },
                                text = { Text("设置") }
                            )
                        }

                        when (selectedTab) {
                            0 -> FunctionPageContent(
                                currentPage = functionPage,
                                onSelectPage = { functionPage = it },
                                onBack = { functionPage = null },
                                imuVm = imuVm,
                                ble = ble,
                                hasGyro = gyro != null,
                                hasAccel = accel != null,
                                drawVm = drawVm,
                                drawTracker = drawTracker
                            )
                            1 -> AboutPage(versionName = BuildConfig.VERSION_NAME)
                            2 -> SettingsPage(
                                forcePortrait = forcePortrait,
                                onForcePortraitChange = { updateForcePortrait(it) }
                            )
                        }
                    }
                }
            }
        }

        updateVolumeDisplay()
        checkPermissions()
    }

    /**
     * 切 Tab 时保证两页音频会话互斥（FR-1）：离开扩音器页按住会话立即停止，
     * 离开消音器页停止其录制/标定/实时消音并释放音频设备。
     */
    private fun selectTab(index: Int) {
        if (index == selectedTab) return
        // 离开功能页时停掉音频组件（扩音器/消音器已隐藏，防御性调用）
        if (selectedTab == 0) {
            stopStreaming()
            silencerEngine.stopAll()
        }
        selectedTab = index
    }

    /** 按设置锁定/放开屏幕方向 */
    private fun applyOrientation() {
        requestedOrientation = if (forcePortrait) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    private fun updateForcePortrait(value: Boolean) {
        forcePortrait = value
        prefs.edit().putBoolean("force_portrait", value).apply()
        applyOrientation()
    }

    private fun requestBlePermissions() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        blePermissionLauncher.launch(perms)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED
        ) {
            initializeAudioComponents()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun initializeAudioComponents() {
        val sampleRate = SAMPLE_RATE
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
        )

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        audioRecord = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .build()

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    private fun updateVolumeDisplay() {
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    private fun startStreaming() {
        if (!::audioRecord.isInitialized || !::audioTrack.isInitialized) return
        isRecording = true
        audioRecord.startRecording()
        audioTrack.play()
        updateVolumeDisplay()
        executorService.submit {
            val buffer = ShortArray(CHUNK_SAMPLES)
            val processed = ShortArray(CHUNK_SAMPLES)
            // DSP 使能边沿跟踪与参考信号都限定在音频线程内，无需加锁
            var aecActive = aecOn
            var howlingActive = howlingOn
            aec.reset()
            howlingSuppressor.reset()
            var reference = 0f
            while (isRecording) {
                val readResult = audioRecord.read(buffer, 0, buffer.size)
                if (readResult > 0) {
                    if (aecOn != aecActive) {
                        aec.reset()
                        reference = 0f
                        aecActive = aecOn
                    }
                    if (howlingOn != howlingActive) {
                        howlingSuppressor.reset()
                        howlingActive = howlingOn
                    }
                    reference = processBuffer(
                        buffer, processed, readResult, aecActive, howlingActive, reference
                    )
                    audioTrack.write(processed, 0, readResult)
                    waveformController.addSamples(buffer, processed, readResult)
                }
            }
            // 停止后清空回声路径估计与陷波，避免下次启动出现瞬态
            aec.reset()
            howlingSuppressor.reset()
        }
    }

    /**
     * 处理一帧：回声消除 → 防啸叫陷波 → 增益。
     * AEC 的参考信号是真正送往扬声器的播放信号（增益后），返回本帧最后一个参考采样。
     */
    private fun processBuffer(
        raw: ShortArray,
        out: ShortArray,
        count: Int,
        aecActive: Boolean,
        howlingActive: Boolean,
        referenceIn: Float
    ): Float {
        val multiplier = 10.0.pow(gainDb / 20.0).toFloat()
        var reference = referenceIn
        for (i in 0 until count) {
            var sample = raw[i] / MAX_SAMPLE
            if (aecActive) sample = aec.processSample(sample, reference)
            if (howlingActive) sample = howlingSuppressor.processSample(sample)
            val played = (sample * multiplier).coerceIn(-1f, 1f)
            out[i] = (played * 32767f).toInt().toShort()
            reference = played
        }
        return reference
    }

    private fun stopStreaming() {
        if (!::audioRecord.isInitialized) return
        isRecording = false
        audioRecord.stop()
        audioTrack.stop()
        waveformController.clear()
        updateVolumeDisplay()
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(volumePoller)
        updateVolumeDisplay()
        handler.post(volumePoller)
        // GAME 档约 50Hz，兼顾响应速度与功耗
        gyro?.let { sensorManager.registerListener(imuVm, it, SensorManager.SENSOR_DELAY_GAME) }
        accel?.let { sensorManager.registerListener(imuVm, it, SensorManager.SENSOR_DELAY_GAME) }
        if (::sensorManager.isInitialized) drawTracker.start()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(volumePoller)
        sensorManager.unregisterListener(imuVm)
        if (::sensorManager.isInitialized) drawTracker.stop()
        // 退后台自动停止两页音频会话并释放设备（FR-7 / NFR-4）
        stopStreaming()
        if (::silencerEngine.isInitialized) silencerEngine.stopAll()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (::silencerEngine.isInitialized) silencerEngine.destroy()
        executorService.shutdownNow()
        if (::audioRecord.isInitialized) audioRecord.release()
        if (::audioTrack.isInitialized) audioTrack.release()
    }

    private companion object {
        const val SAMPLE_RATE = 44100
        const val CHUNK_SAMPLES = 512
        const val MAX_SAMPLE = 32768f
    }
}

@Composable
private fun FunctionPageContent(
    currentPage: String?,
    onSelectPage: (String) -> Unit,
    onBack: () -> Unit,
    imuVm: ImuViewModel,
    ble: BleController,
    hasGyro: Boolean,
    hasAccel: Boolean,
    drawVm: DrawViewModel,
    drawTracker: OrientationTracker
) {
    Column(Modifier.fillMaxSize()) {
        if (currentPage != null) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "返回"
                    )
                }
                Text(
                    text = "功能 > ${
                        when (currentPage) {
                            "imu" -> "转小球"
                            "draw" -> "绘画"
                            "qr" -> "二维码定位"
                            "depth" -> "深度估计"
                            "ai" -> "深度学习"
                            "script" -> "脚本绘图"
                            "scriptvision" -> "脚本视觉"
                            "hummingbird" -> "蜂鸟无人机"
                            "recon" -> "侦查无人机"
                            else -> currentPage
                        }
                    }",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            when (currentPage) {
                "imu" -> ImuBlePage(imuVm, ble, hasGyro, hasAccel)
                "draw" -> DrawPage(drawVm, drawTracker)
                "qr" -> QrPosePage()
                "depth" -> DepthPage()
                "ai" -> TinyTorchPage()
                "script" -> QuickJSPage()
                "scriptvision" -> PlannedFeaturePage(
                    summary = "OpenMV for Android：QuickJS 替代 MicroPython 作脚本引擎，" +
                        "纯 Kotlin 算子库对标 imlib（灰度/二值化/色块/二维码/Hough/模板匹配），" +
                        "Compose IDE 集成编辑器、实时预览与阈值调试，识别结果经 BLE 对外发布——" +
                        "手机即摄像头 + 算力 + IDE 一体的可编程视觉传感器。",
                    status = "功能规划中，暂不实现（sections/exp7-openmv-for-android）"
                )
                "hummingbird" -> HummingbirdPage()
                "recon" -> PlannedFeaturePage(
                    summary = "睿抗「空中侦察」任务链：自主起飞 → 穿越双障碍 → 绕柱 → " +
                        "悬停识别目标编号牌与真假目标 → 激光攻击正确目标 → 返航降落。" +
                        "手机作机载协同计算机，复用二维码定位、深度估计、脚本引擎与 BLE 链路。",
                    status = "任务规划中，暂不实现（sections/exp9-reconnaissance-drone）"
                )
            }
        } else {
            val scroll = rememberScrollState()
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FunctionCard("转小球", "IMU 小球 + BLE 遥控") { onSelectPage("imu") }
                FunctionCard("绘画", "FOSS 版 AR 画线") { onSelectPage("draw") }
                FunctionCard("二维码定位", "同帧多码位姿估计") { onSelectPage("qr") }
                FunctionCard("深度估计", "端侧单目深度估计") { onSelectPage("depth") }
                FunctionCard("深度学习", "TinyTorch 端上训练") { onSelectPage("ai") }
                FunctionCard("脚本绘图", "QuickJS 指令绘图") { onSelectPage("script") }
                FunctionCard("脚本视觉", "OpenMV for Android（规划）") { onSelectPage("scriptvision") }
                FunctionCard("蜂鸟无人机", "昆虫式视觉循迹返航") { onSelectPage("hummingbird") }
                FunctionCard("侦查无人机", "室内自主空中侦察（规划）") { onSelectPage("recon") }
            }
        }
    }
}

@Composable
private fun PlannedFeaturePage(summary: String, status: String) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            status,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(summary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FunctionCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
