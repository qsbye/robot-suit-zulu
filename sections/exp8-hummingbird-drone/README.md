# exp8 — Hummingbird Drone（蜂鸟无人机）

昆虫式视觉循迹返航（Insect-inspired Visual Route Following）实验工程。

- 参考：van Dijk et al., Science Robotics 9, eadk0310 (2024)
- 状态：🚧 部分实现——小地图、Fourier 快照归巢引擎、相机模式已可用；待接入真实无人机飞控链路
- 规划细节见项目根目录 README.md「实验八」章节

## 工程结构

```
app/src/main/java/cn/qsbye/hummingbird/
├── MainActivity.kt        # 双模式 UI（仿真 / 相机）+ IDF 曲线面板 + 控制按钮
├── homing/
│   ├── Fft128.kt          # 128 点 radix-2 复数 FFT（正/逆变换，纯 Kotlin）
│   ├── FourierHoming.kt   # 16 字节快照捕获、ℰ(Δ) 曲线、暴力+抛物线细化对齐、相位相关移位
│   └── CameraHomingEngine.kt # CameraX YUV 亮度平面 → 128 桶视野信号 + 帧间里程计
└── minimap/
    ├── MinimapTileCache.kt # 1m×1m 瓦片离屏缓存（128px/瓦片）
    ├── MinimapModel.kt     # 里程计仿真 + 快照落点 + 归巢捕获状态机
    └── MinimapView.kt      # 右下角小地图卡片 + 北向锁定画布
```

## 已实现

### 1. Fourier 快照归巢引擎

- **快照**：128 点视野信号（零均值、单位 RMS）→ FFT → 仅保留 k=1..8 复系数，各量化为 8bit → **16 字节/快照**。量化满幅取 96（> |F_k| 理论上界 90.5），不削顶
- **图像差函数** ℰ(Δ) = C − 2·Re Σ_k G_k·conj(F_k)·e^(−i2πkΔ/N)：只算 8 个低频分量即可得到完整 128 点曲线
- **对齐**：暴力遍历 128 个循环移位取 ℰ 最小（论文的暴力旋向搜索）→ 三点抛物线插值求亚像素 → 用 ℰ_min/C 作归一化残差（完全匹配≈0，不相关≈1），0.35 为捕获门限
- **里程计**：帧间 1D 相位相关（列均值→水平移位、行均值→垂直），62° FOV + 1.5m 工作距换算米制尺度

### 2. 小地图（交互参考 [Xaero's Minimap](../../docs/xaeros-minimap-curseforge.md)）

- **瓦片化离屏缓存**：世界系（x 东 / y 北，米，原点=开始建图处）按 1m×1m 分瓦片（128px），轨迹按里程计增量画入瓦片后不再重绘；视图层每帧只做 `drawImage` 平移/缩放采样
- **北向锁定 + 原点居中**：世界→屏幕 y 翻转，1m 网格对齐瓦片边界，+/− 三档缩放（1×/0.5×/0.25×），N 指示与图例
- **路径点**：去程每 2m 落一个快照（蓝点），返程最近未捕获点高亮为归巢目标（黄圈，0.5m 捕获半径），捕获后变绿；橙色三角为当前位姿箭头

### 3. 双模式

- **仿真模式**：`OdometrySimulator` 沿 8 点弯道路线匀速行进，固定航向偏置 + 位置偏置随机游走模拟航位推算漂移；落到快照点时清零偏置。同时用合成天际线（8 次谐波）驱动 ℰ(Δ) 曲线与最小值追移动画
- **相机模式**：CameraX 后摄预览 + `CameraHomingEngine` 逐帧：128 桶列均值 = 视野信号，帧间相位相关 = 里程计，旋转矢量传感器 = 航向；返程实时对下一快照做 Fourier 对齐，显示残差与旋向修正角
- **状态机**：IDLE → OUTBOUND（每 2m 落快照）→ OUTBOUND_DONE → RETURN（距离 + 残差双门限捕获）→ DONE

## 验证

- `./gradlew :app:testDebugUnitTest`：5 项数值校验（FFT 往返、单频峰值定位、对齐移位复原、快照 16 字节、相位相关正/负移位）全部通过
- 真机（XPL0219C06017003）实测：仿真模式 43.5m 路线自动落 21 个快照，返程 12s 内捕获 10 个；相机模式预览正常、航向读数与残差实时更新

## 待实现

- 真实里程计接入（VIO / 光流测距）替换 `OdometrySimulator`
- 全景快照（当前为单帧水平视野；论文用 128×96 柱面全景，可在相机模式用多帧拼接或多摄视场近似）
- 飞控链路：位置/速度设定点经 BLE/串口下传（复用实验二链路）
- Rosette 话题输出：`geometry_msgs/PoseStamped`（快照点与归巢矢量）
