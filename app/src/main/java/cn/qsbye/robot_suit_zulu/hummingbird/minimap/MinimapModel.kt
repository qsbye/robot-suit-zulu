package cn.qsbye.robot_suit_zulu.hummingbird.minimap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cn.qsbye.robot_suit_zulu.hummingbird.homing.FourierHoming
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt
import kotlin.random.Random

/** 位姿：x 东 / y 北（米），yaw 自北顺时针（弧度） */
data class Pose(val x: Float, val y: Float, val yaw: Float)

/** 快照路径点：去程落点，返程归巢目标（相机模式携带 Fourier 快照） */
class Waypoint(val x: Float, val y: Float, val snapshot: FourierHoming.Snapshot? = null) {
    enum class State { PENDING, NEXT, CAPTURED }

    var state by mutableStateOf(State.PENDING)
}

enum class Phase { IDLE, OUTBOUND, OUTBOUND_DONE, RETURN, DONE }

/**
 * 小地图 + 归巢数据模型。
 * - 仿真模式：OdometrySimulator 驱动（带漂移的航位推算）
 * - 相机模式：外部 ingest() 喂入（CameraHomingEngine 相位相关里程计 + 旋转矢量航向）
 * 去程每 2m 落一个快照路径点（相机模式同时压入 16 字节 Fourier 快照），
 * 返程按快照逐段归巢：里程计粗走 → 距离+残差门限捕获 → 漂移清零。
 */
class MinimapModel {
    companion object {
        /** 仿真演示路线（x 东 / y 北，米），含两个回头弯 */
        val ROUTE = listOf(
            0f to 0f, 5f to 2f, 9f to 7f, 15f to 9f, 18f to 15f,
            12f to 18f, 6f to 16f, 2f to 20f,
        )
        const val SNAPSHOT_SPACING_M = 2f
        const val CAPTURE_RADIUS_M = 0.5f

        /** 归巢捕获的 Fourier 残差门限（<0.35 视为视野与快照对齐） */
        const val HOMING_RESIDUAL_GATE = 0.35f
    }

    val tiles = MinimapTileCache()
    private val sim = OdometrySimulator(ROUTE)

    var phase by mutableStateOf(Phase.IDLE); private set
    var paused by mutableStateOf(false); private set
    var pose by mutableStateOf(Pose(0f, 0f, 0f)); private set
    var distanceM by mutableStateOf(0f); private set
    var statusText by mutableStateOf("待建图 —— 点「去程建图」开始"); private set
    val waypoints = mutableStateListOf<Waypoint>()

    // ---- 相机模式状态（由 CameraHomingEngine 回调写入） ----

    /** 最新相机视野信号（去程落快照用） */
    var latestSignal: FloatArray? = null

    /** 最近一次归巢对齐残差（0=完全匹配） */
    var homingResidual by mutableStateOf(1f)

    /** 最近一次旋向修正角（度，正=向右转） */
    var homingAngleDeg by mutableStateOf(0f)

    /** ℰ(Δ) 曲线与最小值位置（IDF 面板数据源） */
    var idfCurveState by mutableStateOf<FloatArray?>(null)
    var idfBest by mutableIntStateOf(0)

    private var lastX = 0f
    private var lastY = 0f
    private var sinceSnapshot = 0f

    val capturedCount: Int get() = waypoints.count { it.state == Waypoint.State.CAPTURED }

    init {
        sim.reset()
    }

    // ---- 仿真模式 ----

    fun startOutbound() {
        resetInternal()
        phase = Phase.OUTBOUND
        sim.running = true
        statusText = "去程建图中（每 2m 落快照）"
    }

    fun startReturn() {
        if (phase != Phase.OUTBOUND_DONE) return
        sim.direction = -1
        sim.running = true
        phase = Phase.RETURN
        updateNextWaypoint()
        statusText = "返程归巢中（里程计粗走 + 快照对齐）"
    }

    // ---- 相机模式 ----

    fun startOutboundCamera() {
        resetInternal()
        phase = Phase.OUTBOUND
        statusText = "相机建图中（每 2m 落快照）"
    }

    fun finishOutbound() {
        if (phase != Phase.OUTBOUND) return
        phase = Phase.OUTBOUND_DONE
        statusText = "去程完成 —— 点「返程归巢」"
    }

    /** 相机里程计喂数（世界系增量，米） */
    fun ingest(dxM: Float, dyM: Float, yaw: Float, moved: Float) {
        if (phase != Phase.OUTBOUND && phase != Phase.RETURN) return
        if (paused) return
        advance(pose.x + dxM, pose.y + dyM, yaw, moved)
    }

    fun togglePause() {
        paused = !paused
        statusText = when {
            paused -> "已暂停"
            phase == Phase.OUTBOUND -> if (sim.running) "去程建图中（每 2m 落快照）" else "相机建图中（每 2m 落快照）"
            phase == Phase.RETURN -> "返程归巢中（里程计粗走 + 快照对齐）"
            else -> statusText
        }
    }

    fun clear() {
        resetInternal()
        phase = Phase.IDLE
        statusText = "待建图 —— 点「去程建图」开始"
    }

    private fun resetInternal() {
        sim.reset()
        tiles.clear()
        waypoints.clear()
        pose = Pose(sim.ox, sim.oy, sim.oheading)
        lastX = pose.x
        lastY = pose.y
        distanceM = 0f
        sinceSnapshot = 0f
        paused = false
        homingResidual = 1f
        homingAngleDeg = 0f
        idfCurveState = null
        idfBest = 0
    }

    /** 仿真帧步进 */
    fun step(dt: Float) {
        if (phase == Phase.IDLE || phase == Phase.DONE || paused) return
        val before = sim.s
        sim.step(dt)
        val moved = abs(sim.s - before)
        if (moved <= 0f) return
        advance(sim.ox, sim.oy, sim.oheading, moved)
        if (phase == Phase.OUTBOUND && sim.s >= sim.totalLength - 1e-3f) {
            sim.running = false
            phase = Phase.OUTBOUND_DONE
            statusText = "去程完成 —— 点「返程归巢」"
        }
    }

    /** 公共推进：轨迹增量入瓦片 + 快照落点 + 归巢捕获判定 */
    private fun advance(nx: Float, ny: Float, yaw: Float, moved: Float) {
        distanceM += moved
        if (hypot(nx - lastX, ny - lastY) > 0.03f) {
            val color = if (phase == Phase.OUTBOUND) 0xFF4FC3F7.toInt() else 0xFFFFB74D.toInt()
            tiles.drawSegment(lastX, lastY, nx, ny, color, 0.07f)
            lastX = nx
            lastY = ny
        }
        pose = Pose(nx, ny, yaw)

        when (phase) {
            Phase.OUTBOUND -> {
                sinceSnapshot += moved
                if (sinceSnapshot >= SNAPSHOT_SPACING_M) {
                    sinceSnapshot = 0f
                    val snap = latestSignal?.let { FourierHoming.Snapshot.capture(it) }
                    waypoints.add(Waypoint(pose.x, pose.y, snap))
                }
            }
            Phase.RETURN -> {
                val next = waypoints.lastOrNull { it.state != Waypoint.State.CAPTURED }
                if (next == null) {
                    sim.running = false
                    phase = Phase.DONE
                    statusText = "已返航起点 —— 演示完成"
                } else {
                    val near = hypot(pose.x - next.x, pose.y - next.y) < CAPTURE_RADIUS_M
                    // 相机模式还需视野与快照对齐（残差门限）；仿真模式仅距离判定
                    val aligned = if (next.snapshot != null) {
                        homingResidual < HOMING_RESIDUAL_GATE
                    } else true
                    if (near && aligned) {
                        next.state = Waypoint.State.CAPTURED
                        sim.correctDrift() // 归巢：漂移清零
                        updateNextWaypoint()
                    }
                }
            }
            else -> {}
        }
    }

    private fun updateNextWaypoint() {
        for (w in waypoints) {
            if (w.state == Waypoint.State.NEXT) w.state = Waypoint.State.PENDING
        }
        waypoints.lastOrNull { it.state != Waypoint.State.CAPTURED }?.state = Waypoint.State.NEXT
    }
}

/**
 * 演示用里程计仿真：沿预定路径匀速走，输出带漂移的航位推算位姿。
 * 漂移模型 = 固定航向偏置（引起随距离增长的横向偏差）+ 位置偏置随机游走。
 * 「归巢修正」= 落到快照点时清零累积偏置（对应论文中视觉归巢消除段内漂移）。
 */
class OdometrySimulator(
    route: List<Pair<Float, Float>>,
    private val speed: Float = 1.5f,
    seed: Int = 2026,
) {
    private val rnd = Random(seed)
    private val xs = FloatArray(route.size) { route[it].first }
    private val ys = FloatArray(route.size) { route[it].second }
    private val cum = FloatArray(route.size) // cum[i] = 到第 i 点的弧长
    val totalLength: Float

    init {
        for (i in 1 until route.size) {
            cum[i] = cum[i - 1] + hypot(xs[i] - xs[i - 1], ys[i] - ys[i - 1])
        }
        totalLength = cum[route.size - 1]
    }

    var s = 0f; private set          // 沿路径弧长
    var direction = 1                 // 1=去程 -1=返程
    var running = false

    // 里程计输出（含漂移）
    var ox = 0f; private set
    var oy = 0f; private set
    var oheading = 0f; private set

    private var posBiasX = 0f
    private var posBiasY = 0f
    private var headingBias = 0f

    fun reset() {
        s = 0f
        direction = 1
        running = false
        posBiasX = 0f
        posBiasY = 0f
        headingBias = (rnd.nextFloat() - 0.5f) * 0.05f // ±1.4° 航向偏置
        val (tx, ty, th) = truthAt(0f)
        ox = tx; oy = ty; oheading = th + headingBias
    }

    fun step(dt: Float) {
        if (!running) return
        val prev = s
        s = (s + speed * dt * direction).coerceIn(0f, totalLength)
        val moved = abs(s - prev)
        val (tx, ty, th) = truthAt(s)
        // 位置偏置随机游走（每走 1 米累积约 2cm 偏差）
        val k = 0.02f * sqrt(moved.coerceAtLeast(1e-4f))
        posBiasX += (rnd.nextDouble().toFloat() * 2f - 1f) * k
        posBiasY += (rnd.nextDouble().toFloat() * 2f - 1f) * k
        ox = tx + posBiasX
        oy = ty + posBiasY
        oheading = th + headingBias
    }

    /** 归巢修正：落到快照点时清零累积漂移 */
    fun correctDrift() {
        posBiasX = 0f
        posBiasY = 0f
    }

    /** 弧长 s 处的真值位姿 */
    fun truthAt(s: Float): Triple<Float, Float, Float> {
        var i = 1
        while (i < cum.size - 1 && cum[i] < s) i++
        val segLen = (cum[i] - cum[i - 1]).coerceAtLeast(1e-6f)
        val t = ((s - cum[i - 1]) / segLen).coerceIn(0f, 1f)
        val x = xs[i - 1] + (xs[i] - xs[i - 1]) * t
        val y = ys[i - 1] + (ys[i] - ys[i - 1]) * t
        val yaw = atan2(xs[i] - xs[i - 1], ys[i] - ys[i - 1])
        return Triple(x, y, yaw)
    }
}
