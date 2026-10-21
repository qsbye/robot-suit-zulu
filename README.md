<p align="center">
  <img src="assets/monocular_depth_map.jpg" width="128" alt="RobotSuitZulu Logo">
</p>

# RobotSuitZulu — 玑函 / Jihan
* 手机机器人实验工具箱 · A smartphone robot experiment toolkit

> 本文档为中英双语：每节先中文、后英文。
> This document is bilingual: each section reads Chinese first, then English.

把手机变成一台“会看、会算、会思考”的机器人实验平台：摄像头、IMU、算力、屏幕、网络全部端上闭环，不需要外接工控机，也不依赖云端推理。

Turn a phone into a robot experiment platform that can see, compute and reason: camera, IMU, compute, display and networking all close the loop on-device — no external industrial PC, no cloud inference.

## 为什么是手机 / Why a Phone

| 维度 | 手机 | 传统工控机 + 深度相机 |
| --- | --- | --- |
| 成本 | 一台旧手机即可 | 数千元起步 |
| 部署 | 装个 App | 驱动、标定、线缆一堆 |
| 传感器 | 摄像头 + IMU + ToF/激光雷达（部分机型） | 需单独采购 |
| 算力 | NPU / GPU / DSP，几十 TOPS | 常需外接 |
| 形态 | 随手可得、可挂到小车上 | 体积大 |

| Dimension | Smartphone | Traditional IPC + depth camera |
| --- | --- | --- |
| Cost | One spare phone is enough | Starts at thousands of CNY |
| Deployment | Install an app | Drivers, calibration, cable spaghetti |
| Sensors | Camera + IMU + ToF/LiDAR (some models) | Must be bought separately |
| Compute | NPU / GPU / DSP, tens of TOPS | Usually external |
| Form factor | At hand, mountable on a small chassis | Bulky |

本项目把机器人感知与决策栈整体搬到手机端，用于教学实验、算法验证与快速原型。

This project moves the whole robot perception-and-decision stack onto the phone, for teaching labs, algorithm validation and rapid prototyping.

## 核心能力 / Core Capabilities

### 1. 端侧深度估计（On-device Depth Estimation）

- 单目深度：基于轻量 CNN / Transformer 的相对深度模型（TFLite / ONNX / Core ML）
- 双目 / 结构光：调用系统原生深度 API（ARKit Depth、ARCore Depth API）
- 融合深度：视觉深度 + IMU + LiDAR（机型支持时）做尺度校正与滤波
- 输出：逐像素深度图、点云、鸟瞰图（BEV），可直接喂给避障与建图模块

- Monocular depth: lightweight CNN / Transformer relative-depth models (TFLite / ONNX / Core ML)
- Stereo / structured light: native depth APIs (ARKit Depth, ARCore Depth API)
- Fused depth: visual depth + IMU + LiDAR (where available) for scale correction and filtering
- Outputs: per-pixel depth map, point cloud, bird's-eye view (BEV) — ready for obstacle avoidance and mapping

### 2. 端侧 SLAM（On-device SLAM）

- 视觉惯性里程计（VIO）：摄像头 + IMU 紧耦合，实时输出 6DoF 位姿
- 稀疏 / 半稠密建图：特征点地图、关键帧数据库、回环检测
- 稠密地图：TSDF / OctoMap / 3DGS 体素地图，用于导航与重建
- 对外接口：标准位姿 / 地图话题，可与 ROS2、小车底盘、机械臂联动

- Visual-inertial odometry (VIO): tightly coupled camera + IMU, real-time 6DoF pose
- Sparse / semi-dense mapping: feature maps, keyframe databases, loop closure
- Dense maps: TSDF / OctoMap / 3DGS volumetric maps for navigation and reconstruction
- Interfaces: standard pose / map topics, interoperable with ROS2, chassis and robotic arms

### 3. 端侧深度学习（On-device Deep Learning）

- 推理后端：TFLite、ONNX Runtime、Core ML、NCNN、MNN、厂商 NPU SDK
- 感知任务：目标检测（YOLO 系列）、语义 / 实例分割、OCR、姿态估计
- 加速手段：量化（INT8/FP16）、算子融合、NPU/GPU 委托、模型蒸馏
- 端上数据闭环：采集 → 标注 → 增量训练 → 量化 → 回灌部署，全在手机完成

- Inference backends: TFLite, ONNX Runtime, Core ML, NCNN, MNN, vendor NPU SDKs
- Perception tasks: object detection (YOLO family), semantic / instance segmentation, OCR, pose estimation
- Acceleration: quantization (INT8/FP16), operator fusion, NPU/GPU delegation, distillation
- On-device data loop: capture → label → incremental training → quantize → redeploy, all on the phone

### 4. 端侧自进化具身模型（On-device Self-evolving Embodied Model）

- 记忆：场景特征库 + 语义拓扑图，跨会话持久化
- 学习：在线自监督（对比学习、聚类新物体）、经验回放与技能库
- 决策：轻量 VLM / LLM（GGUF、Qwen 系列小模型）做任务规划与场景问答
- 进化：失败样本自动回流 → 端上微调（LoRA / Adapter）→ 模型热更新
- 闭环：感知（深度 + 检测）→ 建图（SLAM）→ 规划 → 执行（串口 / BLE 控制底盘）

- Memory: scene feature bank + semantic topology graph, persisted across sessions
- Learning: online self-supervision (contrastive learning, novel-object clustering), experience replay and skill library
- Decision making: lightweight VLM / LLM (GGUF, small Qwen models) for task planning and scene QA
- Evolution: automatic failure-sample feedback → on-device fine-tuning (LoRA / Adapter) → hot model update
- Closed loop: perception (depth + detection) → mapping (SLAM) → planning → actuation (serial / BLE chassis control)

## 联动项目 / Companion Projects

本工具箱的感知与通信底座复用/对接以下两个 Rust 生态项目：

The sensing and communication substrate of this toolkit reuses and integrates two Rust-ecosystem projects:

| 项目 / Project | 定位 / Role | 在本工具箱中的角色 / Part in this toolkit |
| --- | --- | --- |
| [slambook2.rs](https://github.com/ByeIO/slambook2.rs) | 《视觉 SLAM 十四讲》（slambook2）的 Rust 重构，覆盖几何、李代数、特征法/直接法 VO、BA、回环、稠密建图等 1–13 章算法，并提供 wasm 预构建版本<br>*Rust rewrite of “14 Lectures on Visual SLAM” covering ch.1–13: geometry, Lie algebra, feature/direct VO, BA, loop closure, dense mapping, plus a prebuilt wasm build* | 算法底座：提供相机模型、位姿求解（PnP / 对极几何）、曲线拟合与轨迹表示等基础算子，供端侧 SLAM 与二维码定位复用<br>*Algorithm base: camera models, pose solving (PnP / epipolar geometry), curve fitting, trajectory representation — reused by on-device SLAM and QR localization* |
| [bye.rosette.rs](https://github.com/ByeIO/bye.rosette.rs) | Rosette（玫瑰之心）：纯 Rust 实现的 ROS2 风格机器人框架，支持 DDS 分布式通信、topic/service/action、rosbag、webviz 等<br>*Rosette: a pure-Rust ROS2-style robot framework with DDS distributed comms, topic/service/action, rosbag, webviz* | 通信中间件：手机端位姿、路径、检测结果以标准话题发布，与桌面端可视化、底盘控制节点互联（跨机可用 EasyTier 组网）<br>*Middleware: phone-side pose, path and detection results published as standard topics, wired to desktop visualization and chassis-control nodes (EasyTier overlay for multi-machine)* |

典型链路：手机相机帧 → 二维码/特征检测（本仓库 `apps/`）→ 位姿求解（slambook2.rs 算子）→ 路径规划 → 通过 Rosette 话题发布 → 底盘执行 + 屏幕叠加显示预测路径。

Typical pipeline: phone camera frames → QR / feature detection (this repo's `apps/`) → pose solving (slambook2.rs operators) → path planning → published as Rosette topics → chassis actuation + predicted path overlaid on screen.

## 规划实验 / Planned Experiments

### 实验一 / Exp 1 — 基于二维码的引导定位（QR-guided Localization）（已实现 / implemented）✅

> 联动 [slambook2.rs](https://github.com/ByeIO/slambook2.rs) + [bye.rosette.rs](https://github.com/ByeIO/bye.rosette.rs)（话题输出规划中）；纯 Kotlin + Jetpack Compose + CameraX + ZXing，识别方法参照 WaveSend——工程位于 [`sections/exp1-qrcode-pose-estimate/`](sections/exp1-qrcode-pose-estimate/)，并已集成为主 app 的「二维码定位」标签页
>
> *Integrated with [slambook2.rs](https://github.com/ByeIO/slambook2.rs) + [bye.rosette.rs](https://github.com/ByeIO/bye.rosette.rs) (topic output planned); pure Kotlin + Jetpack Compose + CameraX + ZXing, detection approach derived from WaveSend — code in [`sections/exp1-qrcode-pose-estimate/`](sections/exp1-qrcode-pose-estimate/), also shipped as the “QR Localization” page of the main app.*

**目标**：识别场景中的二维码（支持同帧多码、任意可识别尺寸），根据透视畸变估计相机（机器人）相对码面的位姿，并生成一条到达**最近二维码**的路径，以**蓝色曲线**实时绘制在预览画面上。

**Goal**: detect QR codes in the scene (multiple codes per frame, any size ZXing can decode), estimate the camera (robot) pose relative to the code plane from perspective distortion, and draw a real-time **blue curve** on the preview showing the path to the **nearest code**.

**流程**：

1. **检测**：CameraX `ImageAnalysis` 逐帧送 ZXing `GenericMultipleBarcodeReader` 多码解码（Hybrid → Global 直方图两级候选二值化）；由 3 个定位图案中心按平行四边形模型推算第 4 角
2. **位姿估计**：四角像素 ↔ 已知物理边长的平面方形做 4 点 DLT 求单应矩阵（h33=1，8×8 高斯消元），分解 `H = [r1 r2 t]` 得相机系位姿 `T_cw`（Gram-Schmidt 正交化 + z 正向约束），按码内容做 EMA 平滑抑制抖动
3. **路径规划**：相机系下从镜头前方到最近二维码中心的三次贝塞尔曲线
4. **可视化**：每个码画绿色锁定框 + 内容标签，蓝色路径曲线 + 红色目标点指向最近码；顶部状态条实时显示检出数量与最近码内容 / 距离
5. **联动输出**（规划中）：位姿经 slambook2.rs 的 SE(3) 类型规整后，打包为 `geometry_msgs/PoseStamped` 话题，由 Rosette 发布，webviz / 底盘节点订阅执行

**Pipeline**:

1. **Detection**: each frame goes from CameraX `ImageAnalysis` to ZXing `GenericMultipleBarcodeReader` (two-stage binarization: Hybrid → Global histogram); the 4th corner is inferred from the three finder-pattern centers via a parallelogram model
2. **Pose estimation**: 4-point DLT between the four image corners and a planar square of known physical edge length gives a homography (h33=1, 8×8 Gaussian elimination); decomposing `H = [r1 r2 t]` yields the camera-frame pose `T_cw` (Gram-Schmidt orthogonalization + positive-z constraint), EMA-smoothed per code payload to suppress jitter
3. **Path planning**: a cubic Bézier from just in front of the lens to the nearest code center, in camera frame
4. **Visualization**: green lock box + payload label per code, blue path curve + red target dot pointing at the nearest code; a top status bar shows detection count and nearest payload / distance
5. **Downstream output** (planned): poses are normalized through slambook2.rs SE(3), packed as `geometry_msgs/PoseStamped` and published by Rosette for webviz / chassis nodes to consume

**预期效果**：手机对着墙上的二维码缓慢移动，画面中蓝色曲线始终平滑地"指向"并延伸至二维码中心；同帧多码时自动锁定最近的一个，路径实时跟随重算。

**Expected result**: as you slowly move the phone toward a wall-mounted code, the blue curve keeps smoothly pointing at and reaching the code center; with several codes in frame, the nearest one is locked and the path is recomputed live.

**用到的 slambook2.rs 知识点**：相机模型与内参（ch5，固定 62° 视场角近似）、单应与位姿估计（ch7，单应分解替代 EPnP）。

**slambook2.rs concepts used**: camera model and intrinsics (ch5, fixed 62° FOV approximation), homography and pose estimation (ch7, homography decomposition in place of EPnP).

**已知限制**：角点取自定位图案中心（非码面真实角点）、内参未标定，位姿精度为演示级，距离为相对量；详细说明见 [section README](sections/exp1-qrcode-pose-estimate/README.md)。

**Known limits**: corners come from finder-pattern centers (not the true code corners) and intrinsics are uncalibrated, so pose accuracy is demo-grade and distances are relative; see the [section README](sections/exp1-qrcode-pose-estimate/README.md) for details.

### 实验二 / Exp 2 — IMU 小球 + BLE 遥控 esp32（IMU Ball + BLE-Controlled esp32）（已实现 / implemented）✅

> 纯 Kotlin + Jetpack Compose，无第三方 BLE 库；工程位于 [`sections/exp2-switch-page-imu/`](sections/exp2-switch-page-imu/)
>
> *Pure Kotlin + Jetpack Compose, no third-party BLE library; code in [`sections/exp2-switch-page-imu/`](sections/exp2-switch-page-imu/).*

**目标**：一个 App 演示三件事——陀螺仪姿态感知、摇一摇交互、手机经 BLE 遥控 esp32 小车/小灯。

**Goal**: one app demonstrating three things — gyroscope-based orientation sensing, shake interaction, and BLE remote control of an esp32 car / LED.

**功能**：

1. **陀螺仪小球页**：`TYPE_GYROSCOPE` 角速度对时间积分得手机倾角（带回零衰减抑制 MEMS 零偏），倾角映射为小球速度，转动手机让小球在边界框内滚动
2. **摇一摇切页**：`TYPE_ACCELEROMETER` 三轴模长超出重力阈值（12 m/s²，冷却 800ms）判定为摇一摇，在小球页 → 说明页 → 遥控页间循环切换
3. **BLE 遥控页**（复刻传统 XML 布局的 Compose 版本）：
   - 纯 Android BLE API（`BluetoothLeScanner` + `BluetoothGatt` + CCCD 通知），替代 RxBLE/RxJava 封装
   - 扫描连接 esp32 的 Nordic UART 服务（`6E400001-...`，RX 写指令 / TX 收通知）
   - Compose Canvas 虚拟手柄：拖拽判定主方向（forward/backward/left/right），1 秒消抖后写入指令帧；连接成功自动翻转 esp32 小灯
   - 顶部导航栏 / 中部状态区（电量、速度、旋转角度）/ 底部日志区（25 行时间戳日志，自动滚底），马卡龙配色

**Features**:

1. **Gyroscope ball page**: integrate `TYPE_GYROSCOPE` angular velocity over time into phone tilt (with return-to-zero decay suppressing MEMS bias), map tilt to ball velocity and roll the ball inside a bounds box by rotating the phone
2. **Shake to switch pages**: a `TYPE_ACCELEROMETER` vector magnitude above the gravity threshold (12 m/s², 800 ms cooldown) counts as a shake and cycles ball page → info page → remote page
3. **BLE remote page** (a Compose re-creation of a classic XML layout):
   - Plain Android BLE APIs (`BluetoothLeScanner` + `BluetoothGatt` + CCCD notifications) instead of RxBLE/RxJava wrappers
   - Scans for and connects to the esp32 Nordic UART service (`6E400001-...`, RX for commands / TX for notifications)
   - Compose Canvas virtual pad: drag direction decides forward/backward/left/right, debounced 1 s before writing the command frame; a successful connection toggles the esp32 LED
   - Top nav bar / middle status area (battery, speed, rotation) / bottom log area (25 timestamped lines, auto-scrolled), macaron palette

**演示**：

| 小球页 / Ball page | 说明页 / Info page | 摇一摇切页动图 / Shake demo |
| --- | --- | --- |
| ![ball](sections/exp2-switch-page-imu/screenshots/imu-ball-page.png) | ![info](sections/exp2-switch-page-imu/screenshots/imu-info-page.png) | ![demo](sections/exp2-switch-page-imu/screenshots/imu-demo.gif) |

**esp32 端固件 / esp32 firmware**：[esp32笔记[6]-蓝牙(BLE)控制小灯](https://www.cnblogs.com/qsbye/p/17566976.html)

### 实验三 / Exp 3 — AR 画线（FOSS 版 Just a Line）（FOSS Just a Line）✅

> 纯 Kotlin + Jetpack Compose + CameraX + 传感器，净室重写自 [逆向分析报告](reference/justaline/20261019-074718-JustaLine_逆向分析报告.md)，工程位于 [`sections/exp3-ar-draw-line/`](sections/exp3-ar-draw-line/)
>
> *Pure Kotlin + Jetpack Compose + CameraX + sensors, clean-room rewrite based on the [reverse-engineering report](reference/justaline/20261019-074718-JustaLine_逆向分析报告.md); code in [`sections/exp3-ar-draw-line/`](sections/exp3-ar-draw-line/).*

**目标**：复刻 Google AR Experiments 的空间绘画应用 Just a Line——转动手机瞄准，按住屏幕拖动即可在空中画线——并把原版绑定的专有服务（ARCore / Firebase / Nearby / GMS）全部替换为开源栈，可在无 GMS 设备上运行。

**Goal**: recreate Google AR Experiments' spatial drawing app Just a Line — aim by turning the phone, press and drag to draw lines in the air — while replacing every proprietary service of the original (ARCore / Firebase / Nearby / GMS) with an open-source stack so it runs on GMS-free devices.

**FOSS 改造**：

1. **3DoF magic-window 追踪替代 ARCore**：游戏旋转矢量传感器输出设备→世界旋转矩阵 R，笔画点存世界系坐标，每帧用 Rᵀ 重投影到屏幕，实现"线留在空气中"；触摸射线按针孔模型落到固定距离（近/中/远 0.7/1.5/3 m）的平面上
2. **Compose Canvas 替代 OpenGL 渲染器**：2D 线段 + 透视线宽（近粗远细）近似原版 GL 三角带，保留原版 finishStroke 的微小笔画处理逻辑
3. **零遥测零网络**：移除 Firebase Analytics / FCM / Nearby 协作，仅申请相机权限；视频录制降级为 PNG 快照导出（相册 `Pictures/RobotSuitZulu-AR`）
4. **8 色色板 + 三档笔宽 + 撤销/清空 + 追踪状态指示灯**，无相机权限时降级为深色背景天空画线模式

**FOSS substitutions**:

1. **3DoF magic-window tracking instead of ARCore**: the game rotation vector sensor yields a device→world rotation matrix R; stroke points are stored in world space and reprojected to screen with Rᵀ every frame, so lines “stay in the air”; touch rays hit planes at fixed distances (near/mid/far 0.7/1.5/3 m) under a pinhole model
2. **Compose Canvas instead of an OpenGL renderer**: 2D segments with perspective line width (thick near, thin far) approximate the original GL triangle strips, while keeping the original finishStroke micro-stroke handling
3. **Zero telemetry, zero network**: Firebase Analytics / FCM / Nearby collaboration removed; camera permission is the only one requested; video recording degrades to PNG snapshot export (gallery `Pictures/RobotSuitZulu-AR`)
4. **8-color palette + 3 pen widths + undo/clear + tracking status light**; without camera permission it falls back to a dark-sky drawing mode

**已知限制**：3DoF（无平移视差）、旋转矢量长时缓慢漂移、FOV 为 62° 近似值——详见 [section README](sections/exp3-ar-draw-line/README.md)。

**Known limits**: 3DoF only (no translational parallax), slow long-term drift of the rotation vector, and an approximate 62° FOV — see the [section README](sections/exp3-ar-draw-line/README.md).

### 实验四 / Exp 4 — 端侧单目深度估计（On-device Monocular Depth Estimation）🚧

> 参考 [DepthART](https://github.com/AIRI-Institute/DepthART)（arXiv:2409.15010，MIT License）——原仓库已存档至 [`reference/DepthART-main/`](reference/DepthART-main/)
>
> *Based on [DepthART](https://github.com/AIRI-Institute/DepthART) (arXiv:2409.15010, MIT License); upstream archived at [`reference/DepthART-main/`](reference/DepthART-main/).*

**目标**：把 DepthART「单目深度估计 = 自回归细化任务」的范式搬到手机端——复用图像生成模型 VAR 的下一尺度预测先验，端上实时输出逐像素相对深度，伪彩色深度图叠加相机预览，并与实验一的二维码位姿联动做绝对尺度校正。

**Goal**: bring DepthART's “monocular depth = autoregressive refinement” paradigm to the phone — reuse the next-scale prediction prior of the image-generation model VAR to produce per-pixel relative depth in real time, overlay a false-color depth map on the camera preview, and use Exp 1 QR poses for absolute scale correction.

**原理**（DepthART 的三层拆解）：

1. **深度离散化**：预训练 VQ-VAE（`vae_ch160v4096z32`）把深度图编码为多尺度 token 金字塔——从 1×1 的全局摘要到细尺度的局部细节
2. **自回归细化**：VAR d16 以 RGB 图像为条件，按尺度从粗到细逐级预测深度 token；DepthART 的关键改动是把任务定义为「细化」——上一尺度的粗糙深度在下一尺度被修正补全而非从零生成，让生成先验与深度回归任务对齐
3. **解码还原**：VQ-VAE 解码器将最细尺度 token 还原为连续深度图

**How it works** (DepthART in three layers):

1. **Depth discretization**: a pretrained VQ-VAE (`vae_ch160v4096z32`) encodes the depth map into a multi-scale token pyramid — from a 1×1 global summary down to fine local detail
2. **Autoregressive refinement**: VAR d16, conditioned on the RGB image, predicts depth tokens scale by scale from coarse to fine; DepthART's key change is framing the task as *refinement* — the coarse depth of the previous scale is corrected and completed at the next scale rather than generated from scratch, aligning the generative prior with depth regression
3. **Decoding**: the VQ-VAE decoder turns the finest-scale tokens back into a continuous depth map

**流程**：

1. **复现训练**：Hypersim 合成数据集 + 官方预训练权重（VQ-VAE / VAR d16），`tools/dist_train.sh train_depthART.yaml` 训练；`tools/eval.py` 在 NYUv2 / ETH3D / iBims-1 / TUM 上复算 AbsRel、δ<1.25 并与论文对齐
2. **端侧转换**：PyTorch → ONNX 导出 → FP16 / INT8 量化 → ONNX Runtime Mobile / TFLite（NNAPI / NPU 委托）；延迟超标时蒸馏到更小骨干
3. **端侧推理**：CameraX 逐帧 → letterbox 预处理 → 推理 → 深度图上采样对齐
4. **可视化**：turbo 伪彩色深度图实时叠加预览，原图 / 深度 / 混合三档切换
5. **联动输出**：与实验一联动——二维码已知物理边长提供绝对尺度，将相对深度校正为米制深度；深度图规整为点云 / BEV，经 Rosette 以 `sensor_msgs/Image` 话题发布，供避障与建图节点订阅

**Pipeline**:

1. **Reproduce training**: Hypersim synthetic dataset + official pretrained weights (VQ-VAE / VAR d16), trained via `tools/dist_train.sh train_depthART.yaml`; `tools/eval.py` recomputes AbsRel and δ<1.25 on NYUv2 / ETH3D / iBims-1 / TUM and checks them against the paper
2. **On-device conversion**: PyTorch → ONNX → FP16 / INT8 quantization → ONNX Runtime Mobile / TFLite (NNAPI / NPU delegation); distill into a smaller backbone if latency is too high
3. **On-device inference**: CameraX frame → letterbox preprocessing → inference → depth upsampling and alignment
4. **Visualization**: turbo false-color depth overlaid live on the preview, with original / depth / blended modes
5. **Downstream output**: combined with Exp 1 — the known physical edge length of a QR code provides absolute scale, correcting relative depth into metric depth; the depth map is packed into point cloud / BEV and published by Rosette as `sensor_msgs/Image` for avoidance and mapping nodes

**预期效果**：手机对准室内场景，伪彩色深度图实时跟随（近暖远冷，遮挡边界清晰）；场景中放置二维码后，深度读数经尺度校正接近米制真值——实验一的几何位姿与实验四的学习深度互相校验。

**Expected result**: pointing the phone at an indoor scene gives a live false-color depth map (warm near, cool far, crisp occlusion edges); with a QR code in view, scale-corrected depth readings approach metric ground truth — the geometric poses of Exp 1 and the learned depth of Exp 4 cross-check each other.

**对照与知识点**：经典几何法单目稠密重建（slambook2.rs ch12 极线搜索 + 深度滤波）对照生成式学习法（VQ token + 自回归 Transformer）；端侧量化部署（INT8 / FP16、NPU 委托）。

**Contrast and concepts**: classical geometric monocular dense reconstruction (slambook2.rs ch12 epipolar search + depth filtering) versus generative learning (VQ tokens + autoregressive Transformer); on-device quantized deployment (INT8 / FP16, NPU delegation).

**已知限制**：单目深度本质为相对量，绝对尺度依赖外部校正（实验一联动补齐）；VAR d16 + VQ-VAE 参数量对中端机型有压力，需在精度与帧率间权衡。

**Known limits**: monocular depth is inherently relative, so absolute scale needs external correction (supplied by the Exp 1 link); VAR d16 + VQ-VAE parameter counts strain mid-range phones, so accuracy must be traded against frame rate.

### 实验五 / Exp 5 — 端上微型训练框架（TinyTorch On-device）（已实现 / implemented）✅

> 纯 Kotlin + Jetpack Compose，零第三方 ML 框架依赖；参考 [TinyTorch](https://github.com/keith2018/TinyTorch)（MIT License，原仓库已存档至 [`reference/TinyTorch-main/`](reference/TinyTorch-main/)），配套解读文档：[手写一个神经网络训练框架 (TinyTorch)](docs/tinytorch-neural-network-training-framework-robot9.md)。工程位于 [`sections/exp5-tiny-torch-on-device/`](sections/exp5-tiny-torch-on-device/)
>
> *Pure Kotlin + Jetpack Compose, no third-party ML framework; based on [TinyTorch](https://github.com/keith2018/TinyTorch) (MIT License, upstream archived at [`reference/TinyTorch-main/`](reference/TinyTorch-main/)); companion write-up: [Hand-writing a neural network training framework (TinyTorch)](docs/tinytorch-neural-network-training-framework-robot9.md). Code in [`sections/exp5-tiny-torch-on-device/`](sections/exp5-tiny-torch-on-device/).*

**目标**：把 TinyTorch「Tensor/Function 计算图 + 自动微分 + Module/Optimizer」的最小内核用纯 Kotlin 在 Android 端实现——实验四的固定结构 MLP 升级为支持任意算子组合的通用微型训练框架，MNIST 训练全流程端上闭环。

**Goal**: implement TinyTorch's minimal core — Tensor/Function compute graph + autodiff + Module/Optimizer — in pure Kotlin on Android, upgrading Exp 4's fixed-structure MLP into a general micro-framework that supports arbitrary operator compositions, with the whole MNIST training loop closed on-device.

**实现**：

1. **Tensor/TensorImpl 分层**：底层 float32 张量只做数值运算（matmul NN/NT/TN、relu、log_softmax）；上层 Tensor 挂 grad/gradFn
2. **Function 即计算图节点**：算子前向时把输入 gradFn 链入 `nextFunctions` 建图；叶子节点 AccumulateGrad 写回梯度（对应 TinyTorch FuncLeaf）
3. **拓扑调度反传**：消费者计数 + 梯度累加，支持分叉 DAG（同一 Tensor 多处消费），比实验四固定层序反传更通用
4. **Module/Adam/StepLR/DataLoader**：递归参数汇总、train/eval 模式；MNIST 来自 TinyTorch 随附 IDX（gzip 进 assets，训练取 1 万子集）
5. **UI**：损失曲线实时绘制、测试样本预测预览（绿对红错）、checkpoint 保存/加载

**Implementation**:

1. **Tensor/TensorImpl split**: the low-level float32 tensor only does numerics (matmul NN/NT/TN, relu, log_softmax); the upper Tensor carries grad/gradFn
2. **Function as graph node**: during the forward pass an operator links input gradFns into `nextFunctions` to build the graph; leaf nodes accumulate gradients in AccumulateGrad (TinyTorch's FuncLeaf equivalent)
3. **Topological backward scheduling**: consumer counting + gradient accumulation supports branching DAGs (the same Tensor consumed in several places), more general than Exp 4's fixed layer order
4. **Module/Adam/StepLR/DataLoader**: recursive parameter collection, train/eval modes; MNIST comes from the IDX files shipped with TinyTorch (gzipped into assets, a 10k subset is used)
5. **UI**: live loss curve, prediction previews on test samples (green right, red wrong), checkpoint save/load

**验证**：`AutogradTest` 数值梯度校验（解析梯度 vs 有限差分）通过；构建安装见 [section README](sections/exp5-tiny-torch-on-device/README.md)。

**Validation**: `AutogradTest` passes numeric gradient checks (analytic vs finite differences); build and install steps are in the [section README](sections/exp5-tiny-torch-on-device/README.md).

**已知限制**：纯 CPU float32、朴素三重循环 matmul、无卷积算子；教学与范式验证用途，非生产训练。

**Known limits**: CPU-only float32, naive triple-loop matmul, no convolution operators; meant for teaching and paradigm validation, not production training.

### 实验六 / Exp 6 — QuickJS 端上脚本绘图（On-device Script Plotting）（已实现 / implemented）✅

> 参考 [摸鱼笔记[11]-使用quickjs设计绘图指令绘制机器视觉结果图](https://www.cnblogs.com/qsbye/p/19577773)；纯 Kotlin + Jetpack Compose + [taoweiji/quickjs-android](https://github.com/taoweiji/quickjs-android)（Apache-2.0），工程位于 [`sections/exp6-quickjs-editor/`](sections/exp6-quickjs-editor/)
>
> *Based on [blog post #11: designing drawing commands with QuickJS for machine-vision result overlays](https://www.cnblogs.com/qsbye/p/19577773); pure Kotlin + Jetpack Compose + [taoweiji/quickjs-android](https://github.com/taoweiji/quickjs-android) (Apache-2.0); code in [`sections/exp6-quickjs-editor/`](sections/exp6-quickjs-editor/).*

**目标**：在 Android 端复刻博客中 CameraViewerAutoWeld 的 Cognex 风格绘图指令集——JavaScript 脚本通过 `PlotLine`/`PlotCircle`/`PlotString`/`PlotPolygon`/`PlotPoint` 绘制机器视觉标注，实现"改脚本即看效果"的快速迭代。

**Goal**: recreate the Cognex-style drawing command set of CameraViewerAutoWeld from the blog on Android — JavaScript scripts draw machine-vision annotations through `PlotLine`/`PlotCircle`/`PlotString`/`PlotPolygon`/`PlotPoint`, so “edit the script, see the result” becomes a fast iteration loop.

**实现**：

1. **QuickJS 引擎**：`io.github.taoweiji.quickjs:quickjs-android:1.4.6`（Maven Central），注册 5 个 Plot 函数 + `Clear`/`print` 为全局 JS 函数
2. **指令模式**：每个 Plot 调用生成一个 `DrawingCommand`（对应博客的指令收集器模式），脚本执行完毕后统一在 Compose Canvas 上渲染
3. **坐标映射**：图像原始分辨率（5120×5120）→ 屏幕坐标自动缩放，线宽/字号随缩放同步调整
4. **UI**：Compose 多行脚本编辑器（等宽字体）+ 运行/重置按钮 + 预览画布 + 控制台输出

**Implementation**:

1. **QuickJS engine**: `io.github.taoweiji.quickjs:quickjs-android:1.4.6` (Maven Central); five Plot functions plus `Clear`/`print` are registered as global JS functions
2. **Command pattern**: each Plot call produces a `DrawingCommand` (the blog's command-collector pattern), rendered at once on a Compose Canvas after the script finishes
3. **Coordinate mapping**: native image resolution (5120×5120) is auto-scaled to screen coordinates, with line width and font size scaled to match
4. **UI**: Compose multi-line script editor (monospace) + run/reset buttons + preview canvas + console output

**与博客桌面版的差异**：Swing `Graphics2D` → Compose `DrawScope`；`AffineTransform` → 简单 scale/offset 数学；`RSyntaxTextArea` → Compose `BasicTextField`；MQTT 数据源 → 本地脚本执行（联动扩展预留）。

**Differences from the desktop blog version**: Swing `Graphics2D` → Compose `DrawScope`; `AffineTransform` → plain scale/offset math; `RSyntaxTextArea` → Compose `BasicTextField`; MQTT data source → local script execution (integration hooks reserved).

**已知限制**：无语法高亮（Compose TextField 原生不支持）；无图像底图（ShowImage 未实现）；消息源为本地空对象（非 MQTT）。

**Known limits**: no syntax highlighting (not natively supported by Compose TextField); no image backdrop (ShowImage not implemented); the message source is a local empty object (not MQTT).

### 实验七 / Exp 7 — OpenMV for Android：手机端脚本化机器视觉（Mobile Scripted Machine Vision）（功能规划 / planned）📋

> 参考 [OpenMV](https://github.com/openmv/openmv)（MIT License，原仓库已存档至 [`reference/openmv-master/`](reference/openmv-master/)）——把 OpenMV「脚本驱动的嵌入式视觉硬件」范式搬到手机：**手机本身就是摄像头 + 算力 + IDE 一体的机器视觉智能硬件**，不再需要独立摄像头模块。工程目录 [`sections/exp7-openmv-for-android/`](sections/exp7-openmv-for-android/)（占位，暂不实现）
>
> *Based on [OpenMV](https://github.com/openmv/openmv) (MIT License, upstream archived at [`reference/openmv-master/`](reference/openmv-master/)) — bringing OpenMV's “script-driven embedded vision hardware” paradigm to the phone: **the phone itself is a camera + compute + IDE machine-vision device**, no separate camera module needed. Directory [`sections/exp7-openmv-for-android/`](sections/exp7-openmv-for-android/) (placeholder, not implemented yet).*

**目标**：复刻 OpenMV 的核心体验——在设备上用脚本快速搭视觉流水线，改脚本即看结果——但脚本引擎用 QuickJS（复用实验六）替代 MicroPython，视觉算子用纯 Kotlin 在 CameraX 帧流上实现，手机同时承担"IDE（编辑器 + 帧缓冲预览 + 阈值调试）"与"视觉硬件（采集 + 处理 + 对外输出）"双重角色。

**Goal**: reproduce OpenMV's core experience — build a vision pipeline on-device with a script and see results immediately — but with QuickJS (from Exp 6) instead of MicroPython as the script engine and pure-Kotlin vision operators running on CameraX frames, so the phone plays both roles: “IDE” (editor + framebuffer preview + threshold tuning) and “vision hardware” (capture + processing + output).

**与 OpenMV 的对位映射**：

| OpenMV 概念 / OpenMV concept | 手机端对应 / Phone-side counterpart |
| --- | --- |
| MicroPython 脚本 / MicroPython scripts | QuickJS JavaScript 脚本（实验六引擎扩展）<br>*QuickJS JavaScript (Exp 6 engine extended)* |
| imlib 算法库（C 实现）/ imlib (C) | 纯 Kotlin 算子库，逐帧作用于 CameraX ImageAnalysis 帧<br>*Pure-Kotlin operator library running per CameraX ImageAnalysis frame* |
| OpenMV IDE（帧缓冲实时预览、阈值编辑器、直跑脚本）/ OpenMV IDE | Compose IDE：脚本编辑器 + 实时预览 + 结果叠加层<br>*Compose IDE: script editor + live preview + result overlay* |
| RPC 库（UART/I2C/SPI/CAN，同步头+通道+opcode+CRC 包）/ RPC library | BLE Nordic UART（实验二）/ WiFi 对外发布结构化结果，包格式参照 [docs/protocol.md](reference/openmv-master/docs/protocol.md)<br>*BLE Nordic UART (Exp 2) / WiFi publishing of structured results; packet format per [docs/protocol.md](reference/openmv-master/docs/protocol.md)* |
| `print()` → USB VCP | 控制台输出面板（实验六已有）<br>*Console output panel (already in Exp 6)* |

**规划脚本 API**（JS 侧，参照 imlib 能力清单分期实现）：

1. **图像基元**：`image.grayscale()` / `image.binary(threshold)` / `image.erode/dilate` —— 灰度、二值化、形态学
2. **色块追踪**：`image.find_blobs(阈值, roi)` → 返回色块质心/包围盒/面积（对标 imlib `blob.c`，巡线/颜色目标场景）
3. **码识别**：`image.find_qrcodes()`（复用实验一 ZXing）→ 内容 + 四角 + 位姿；后续可扩展 AprilTag（远距鲁棒性优于 QR，实验九候选）
4. **几何与运动**：`image.find_lines()`（Hough）、`image.find_circles()`、帧间相位相关位移估计（对标 `phasecorrelation.c`，可做光流式里程计）
5. **模板与特征**：`image.find_template()`、`image.find_keypoints()`（AGAST/FAST/ORB 对标）
6. **绘图叠加**：复用实验六 `PlotLine`/`PlotCircle`/`PlotString` 指令集，脚本检测结果直接画在预览上

**Planned script API** (JS side, staged after imlib's capability list):

1. **Image primitives**: `image.grayscale()` / `image.binary(threshold)` / `image.erode/dilate` — grayscale, binarization, morphology
2. **Blob tracking**: `image.find_blobs(thresholds, roi)` → centroid / bounding box / area (equivalent of imlib `blob.c`; line-following and color-target scenarios)
3. **Code detection**: `image.find_qrcodes()` (reusing Exp 1's ZXing) → payload + corners + pose; AprilTag extension later (more robust at range than QR; a candidate for Exp 9)
4. **Geometry and motion**: `image.find_lines()` (Hough), `image.find_circles()`, inter-frame phase-correlation displacement (equivalent of `phasecorrelation.c`, usable as optical-flow odometry)
5. **Templates and features**: `image.find_template()`, `image.find_keypoints()` (AGAST/FAST/ORB equivalents)
6. **Drawing overlay**: reuse Exp 6's `PlotLine`/`PlotCircle`/`PlotString` commands so detection results are drawn straight onto the preview

**规划流程**：

1. **流水线骨架**：CameraX 帧 → Kotlin 算子链（每帧一个 `image` 对象传入 JS）→ 脚本 `process(image)` 返回标注 → Compose 叠加渲染，目标 ≥10fps
2. **IDE 交互**：编辑器 + 预览分屏；阈值参数面板（滑条实时改 JS 全局变量，无需重跑脚本——对应 OpenMV IDE 阈值编辑器）
3. **对外输出**：识别结果（色块坐标/码内容/位姿）经 BLE 按 OpenMV RPC 风格包格式发布，供 esp32 底盘/飞控消费；与 Rosette 话题桥对齐预留
4. **性能策略**：算子先纯 Kotlin 实现保 FOSS，热点（如二值化、卷积）后续可选 RenderScript 替代方案或 NNAPI 委托（保持开源栈）

**Planned pipeline**:

1. **Pipeline skeleton**: CameraX frame → Kotlin operator chain (one `image` object per frame passed into JS) → script `process(image)` returns annotations → Compose overlay rendering, targeting ≥10 fps
2. **IDE interaction**: split editor + preview; threshold panel (sliders mutate JS globals live without re-running the script — OpenMV IDE's threshold editor equivalent)
3. **Outbound output**: detection results (blob coordinates / code payloads / poses) published over BLE in an OpenMV-RPC-style packet format for an esp32 chassis or flight controller; Rosette topic bridge alignment reserved
4. **Performance strategy**: operators first in pure Kotlin to stay FOSS; hot spots (binarization, convolution) may later use a RenderScript alternative or NNAPI delegation (staying on an open stack)

**预期效果**：打开「脚本视觉」页，粘贴一段 OpenMV 风格的找色块脚本（语法改 JS），手机对着彩色目标即实时框出质心轨迹；改阈值滑条立即生效——一部旧手机即成为可编程视觉传感器。

**Expected result**: open the “Script Vision” page, paste an OpenMV-style blob-finding script (syntax switched to JS), point the phone at a colored target and watch centroids and tracks appear in real time; moving a threshold slider takes effect immediately — an old phone becomes a programmable vision sensor.

**已知限制**：JS 逐帧回调有 JNI/引擎开销，帧率上限低于原生 C 实现（OpenMV 在 STM32H7 上 blob 可达 30fps+）；imlib 的 SIMD 汇编优化无法直接复用，热点算子需重写；本地存档的 openmv-master 顶层缺 LICENSE 文件，引用其代码片段前需核对上游许可。

**Known limits**: per-frame JS callbacks carry JNI/engine overhead, so the frame-rate ceiling is below native C (OpenMV's blob runs at 30 fps+ on an STM32H7); imlib's SIMD assembly cannot be reused directly, so hot operators need rewriting; the local openmv-master archive has no top-level LICENSE file, so check upstream licensing before copying any snippet.

### 实验八 / Exp 8 — 蜂鸟无人机：昆虫式视觉循迹返航（Hummingbird Drone: Insect-inspired Visual Route Following）（功能规划 / planned）📋

> 参考 [Visual route following for tiny autonomous robots](https://www.science.org/doi/10.1126/scirobotics.adk0310)（van Dijk et al., *Science Robotics* 9, eadk0310, 2024；TU Delft，56g Crazyflie Brushless，STM32F4/192KB RAM 上实现）。工程目录 [`sections/exp8-hummingbird-drone/`](sections/exp8-hummingbird-drone/)（占位，暂不实现）
>
> *Based on [Visual route following for tiny autonomous robots](https://www.science.org/doi/10.1126/scirobotics.adk0310) (van Dijk et al., Science Robotics 9, eadk0310, 2024; TU Delft, implemented on a 56 g Crazyflie Brushless with STM32F4 / 192 KB RAM). Directory [`sections/exp8-hummingbird-drone/`](sections/exp8-hummingbird-drone/) (placeholder, not implemented yet).*

**目标**：把论文的「昆虫式视觉导航」范式搬到手机 + 微型无人机平台——去程沿途记录**极度压缩的全景快照**，返程用「里程计粗走 + 视觉归巢（visual homing）精修」交替前进，周期性地把里程计漂移清零，让算力/内存极度受限的平台也能沿百米级原路返航。

**Goal**: bring the paper's insect-style visual navigation to a phone + micro-drone platform — record **heavily compressed panoramic snapshots** along the outbound route, then return by alternating “odometry legs + visual homing refinement”, periodically resetting odometry drift so even a severely compute/memory-constrained platform can retrace a hundred-metre route.

**原理**（蚂蚁/蜜蜂导航的两要素机器人化）：

1. **路径积分 = 里程计**：去程与返程的"腿"（leg）均用里程计位移矢量连接——论文用光流 + 激光测距（速度 × 时间），手机端对应 VIO/IMU 积分或轮式底盘码盘
2. **视图记忆 = 快照归巢**：在每个快照点把全景图压缩为十几字节的"快照"；返程到达某快照附近后，用视觉归巢把当前视图"对齐"回快照视图，消除该段累积的里程计漂移，再出发下一段
3. **快照间距按里程计精度拉满**：只要里程计漂移能保证无人机落在下一个快照的"捕获区"（catchment area）内即可——相比顺序归巢需 25cm 间距，论文将间距放宽到 1~2m，100m 路线内存消耗 < 20 字节/米

**Principle** (the two ingredients of ant/bee navigation, robotized):

1. **Path integration = odometry**: both outbound and inbound “legs” are chained by odometry displacement vectors — the paper uses optical flow + laser ranging (speed × time); on the phone this maps to VIO/IMU integration or wheel encoders
2. **View memory = snapshot homing**: at each snapshot point the panorama is compressed into a “snapshot” of a dozen-odd bytes; on the way back, once inside the catchment area the current view is “aligned” to the snapshot view, wiping out accumulated odometry drift for that leg before starting the next one
3. **Snapshot spacing pushed to the odometry accuracy limit**: it only has to guarantee the drone lands inside the next snapshot's catchment area — versus the 25 cm spacing of sequential homing, the paper relaxes it to 1–2 m, giving < 20 bytes/metre on a 100 m route

**快照表示与归巢算法**（论文三选一对比后采用 Fourier 法，内存效率最高）：

1. 全景原图 128×96 → 柱面重投影 128×16 → 灰度 + 垂直平均 → 128 点一维周期信号
2. FFT 变换后丢弃直流与高频分量，复系数量化为 8bit 对 → **单个快照仅 16 字节**
3. 归巢矢量由"近似图像差函数"的二次曲面 ℰ(h) = ½hᵀAh + bᵀh + c 解析最小化得到——只需一次 FFT + 3×3 矩阵求逆，微控制器级算力即可实时运行
4. 粗旋向对齐用暴力旋转搜索（替代原相位法，对环境对称性更鲁棒）

**Snapshot representation and homing** (the Fourier method chosen after comparing three options — most memory-efficient):

1. Panorama 128×96 → cylindrical reprojection 128×16 → grayscale + vertical averaging → a 128-sample 1-D periodic signal
2. FFT, then discard DC and high-frequency components and quantize complex coefficients into 8-bit pairs → **a single snapshot is only 16 bytes**
3. The homing vector comes from analytically minimizing the quadratic image-difference surface ℰ(h) = ½hᵀAh + bᵀh + c — one FFT plus a 3×3 matrix inverse, real-time even on microcontroller-class compute
4. Coarse heading alignment uses a brute-force rotation search (replacing the original phase method, more robust to environment symmetry)

**规划流程**（手机端复现）：

1. **平台**：手机（相机 + IMU + 算力）挂载微型四轴底盘，或手机做地面站经 BLE/WiFi 遥控 esp32 级小机（复用实验二 BLE 链路），视觉与导航计算全在手机侧闭环
2. **去程记录**：任意方式飞出路线（手动遥控/航点），按里程计距离每 1~2m 自动落一个快照：CameraX 帧 → 灰度 → 垂直平均 → FFT 压缩 → 连同段间里程计矢量一并存入路线表
3. **返程循迹**：每段先按里程计矢量飞往下一快照点，进入捕获区后切换 Fourier 归巢收敛（约 0.5m 内），到位重置位姿，逐段重复直至回到起点
4. **可视化**：Compose 实时绘制路线拓扑（快照点 + 里程计矢量）、当前归巢矢量方向、IDF 曲线与捕获区状态；**屏幕右下角常驻小地图**（交互参考 [Xaero's Minimap](docs/xaeros-minimap-curseforge.md)）——以开始建图处为世界系原点居中锚定，随去程/返程移动按里程计增量绘制已走过区域（瓦片化离屏缓存，不重绘全图），默认锁定北向 + 方向箭头，快照点与归巢目标以路径点形式叠加
5. **联动输出**（规划中）：快照点/归巢矢量经 Rosette 以 `geometry_msgs/PoseStamped` 话题发布，与 slambook2.rs 位姿类型对齐；与实验一二维码位姿联动可为路线提供绝对尺度锚点

**Planned pipeline** (phone-side reproduction):

1. **Platform**: a phone (camera + IMU + compute) mounted on a micro quad, or the phone acting as ground station controlling an esp32-class drone over BLE/WiFi (reusing Exp 2's BLE link); vision and navigation compute stay closed on the phone
2. **Outbound recording**: fly the route any way you like (manual or waypoints); every 1–2 m of odometry distance a snapshot is dropped automatically: CameraX frame → grayscale → vertical averaging → FFT compression → stored in the route table together with the inter-leg odometry vector
3. **Inbound following**: each leg first flies along its odometry vector toward the next snapshot point; inside the catchment area it switches to Fourier homing (converging to ~0.5 m), resets the pose, and repeats leg by leg until back at the start
4. **Visualization**: Compose draws the route topology (snapshot points + odometry vectors), the current homing direction, the IDF curve and catchment state live; a **persistent minimap in the bottom-right corner** (interaction model per [Xaero's Minimap](docs/xaeros-minimap-curseforge.md)) — anchored on the mapping start point as world origin, incrementally painting traversed areas as the drone moves on outbound/inbound legs (tiled offscreen cache, never redraws the whole map), north-locked by default with a direction arrow, snapshots and homing targets overlaid as waypoints
5. **Downstream output** (planned): snapshot points and homing vectors published by Rosette as `geometry_msgs/PoseStamped` topics aligned with slambook2.rs pose types; combined with Exp 1 QR poses they can anchor the route's absolute scale

**预期效果**：手持手机（或挂载小机）在室内走出一条含弯道的路线后，返程段段"先粗走后对齐"，长距离误差有界不发散；百米路线总内存开销约 1KB 量级，演示"导航不需要 SLAM 大图"的昆虫式极简方案。

**Expected result**: after walking (or flying) a route with bends indoors, the return trip goes “coarse odometry leg, then align” segment by segment, keeping long-range error bounded instead of diverging; a hundred-metre route costs on the order of 1 KB of memory, demonstrating the insect-style minimal approach where “navigation does not need a big SLAM map”.

**对照与知识点**：全景快照/Fourier 归巢对照实验一的几何位姿估计（整体式外观匹配 vs 稀疏特征/几何求解）；里程计漂移建模对照 slambook2.rs ch10 后端；论文基线为 Crazyflie + Flowdeck（光流 RMSE 13cm/5m），手机端可用 VIO 或轮式里程计对标。

**Contrast and concepts**: panoramic snapshot / Fourier homing versus Exp 1's geometric pose estimation (holistic appearance matching vs sparse features and geometry solving); odometry drift modelling versus slambook2.rs ch10 back-end; the paper's baseline is a Crazyflie + Flowdeck (optical-flow RMSE 13 cm over 5 m), which a phone can match with VIO or wheel odometry.

**已知限制**（论文结论）：Fourier 快照依赖地平线附近的垂直特征（墙、门、窗对比），纯色走廊无法修正沿线漂移；归巢仅在捕获区内收敛，路线首次走过必须无碰撞（环境静态假设）；路线复用不含避障，需另配光流/深度避障模块。

**Known limits** (from the paper): Fourier snapshots rely on vertical features near the horizon (wall, door, window contrast), so drift along a featureless corridor cannot be corrected; homing only converges inside the catchment area and the first traversal must be collision-free (static-environment assumption); route reuse includes no obstacle avoidance and needs a separate optical-flow/depth avoidance module.

### 实验九 / Exp 9 — 侦查无人机：室内自主空中侦察（Reconnaissance Drone）（任务规划 / mission plan）📋

> 对应[睿抗机器人开发者大赛工程竞技赛道「空中侦察」项目](../projects/ByeIO/subproject/毕业设计/睿抗/睿抗无人机比赛要求.md)任务要求。工程目录 [`sections/exp9-reconnaissance-drone/`](sections/exp9-reconnaissance-drone/)（占位，仅描述任务，暂不实现）
>
> *Corresponds to the “Aerial Reconnaissance” event of the RIC (RoboCom) robot developer competition ([requirements doc](../projects/ByeIO/subproject/毕业设计/睿抗/睿抗无人机比赛要求.md)). Directory [`sections/exp9-reconnaissance-drone/`](sections/exp9-reconnaissance-drone/) (placeholder, mission description only).*

**赛题背景**：5m×5m×4m 室内安全网场地模拟房间肃清——内有 1 名武装人员（真实目标）与 2 名平民（假目标），无人机需全程自主进入房间、识别并"打击"正确目标，替代人工突击以降低伤亡风险。对应 Vijay Kumar 无人机"5S"趋势（Small/Safe/Smart/Speed/Swarms）中的智能化自主化方向。

**Background**: a 5 m × 5 m × 4 m indoor safety-net arena simulates room clearing — one armed person (true target) and two civilians (decoys) inside; the drone must autonomously enter the room, identify and “strike” the correct target, replacing a human assault to reduce casualties. It aligns with the intelligence/autonomy axis of Vijay Kumar's drone “5S” trend (Small/Safe/Smart/Speed/Swarms).

**比赛任务链**（计时制，10 分钟上限，全程自主——手动飞行按 5 倍计时）：

1. **任务一**：自主起飞，按顺序穿越障碍 1、障碍 2（各 0.5×0.1×3m），到达 1.5m 宽任务区入口
2. **任务二**：穿越入口，绕开圆柱形障碍物（直径 0.3m、高 2m），到达目标识别区
3. **任务三**：悬停于识别区上方，先识别目标编号指示牌（字母/数字/二维码，0.5×0.5m），再对各目标（0.5×0.5m，离地 1m）逐一识别，判定正确目标
4. **任务四**：自主移动至正确目标正前方，开启机载激光器（载波 38kHz、波长 650nm 红光）模拟攻击，命中靶标任意位置触发声光反馈；**识别完成前开激光罚时 60s**
5. **任务五**：裁判确认命中后返航起飞点降落

**Mission chain** (timed, 10-minute cap, fully autonomous — manual flight counts ×5):

1. **Mission 1**: autonomous takeoff, pass obstacles 1 and 2 in order (each 0.5 × 0.1 × 3 m), reach the 1.5 m-wide task-area entrance
2. **Mission 2**: pass the entrance, avoid the cylindrical obstacle (0.3 m diameter, 2 m tall), reach the target-identification zone
3. **Mission 3**: hover above the identification zone, first recognize the target-number sign (letter/digit/QR, 0.5 × 0.5 m), then identify each target in turn (0.5 × 0.5 m, 1 m above ground) and decide which is correct
4. **Mission 4**: fly autonomously in front of the correct target, enable the onboard laser (38 kHz carrier, 650 nm red) to simulate a strike; any hit on the target triggers audible/visual feedback; **enabling the laser before identification completes incurs a 60 s penalty**
5. **Mission 5**: after the judge confirms the hit, return and land at the takeoff point

**罚时要点**：触障 +30s/次、未悬停识别 +60s、误判/错攻 +60s、激光提前开启 +60s、碰撞失控可换机重飞一次。

**Penalties**: obstacle contact +30 s each, no-hover identification +60 s, misjudgement / wrong strike +60 s, early laser +60 s; after a crash you may swap aircraft and fly once more.

**无人机硬指标**：四旋翼轴距 400~550mm、续航 ≥12min、开源飞控可二次开发、自主/手动模式可切换、状态实时回传、控制半径 ≥200m；**禁用 UWB、动捕等外置定位**——室内定位必须机载自闭环。

**Drone requirements**: quadrotor wheelbase 400–550 mm, endurance ≥ 12 min, open-source flight controller with secondary development, switchable autonomous/manual modes, real-time telemetry, control radius ≥ 200 m; **external positioning such as UWB or motion capture is forbidden** — indoor localization must close on the aircraft.

**与本工具箱的对接**（手机作机载协同计算机，替代参考机型 Xavier NX 方案）：

| 比赛需求 / Competition need | 复用模块 / Reused module |
| --- | --- |
| 目标编号牌/目标识别（二维码、字母数字）<br>*Number-sign / target recognition (QR, letters, digits)* | 实验一 ZXing 多码检测 + 位姿估计（0.5m 码天然适配）<br>*Exp 1 ZXing multi-code detection + pose estimation (a 0.5 m code fits naturally)* |
| 室内定位（禁 UWB/动捕）<br>*Indoor localization (no UWB/mocap)* | 实验一二维码锚点 + IMU 航位推算，或实验八 Fourier 快照归巢<br>*Exp 1 QR anchors + IMU dead reckoning, or Exp 8 Fourier snapshot homing* |
| 障碍感知与绕障<br>*Obstacle sensing and avoidance* | 实验四端侧深度估计（相对深度 → 障碍距离场）<br>*Exp 4 on-device depth estimation (relative depth → obstacle distance field)* |
| 任务脚本编排与可视化调试<br>*Mission scripting and visual debugging* | 实验六 QuickJS 脚本引擎（任务状态机脚本化、结果图绘制）<br>*Exp 6 QuickJS engine (scripted state machine, result drawing)* |
| 机载算力/通信<br>*Onboard compute and comms* | 手机 NPU + BLE/串口下传飞控指令（复用实验二 BLE 链路）<br>*Phone NPU + BLE/serial flight-controller commands (Exp 2 BLE link)* |

**规划功能**：

1. **任务状态机**：起飞 → 穿越门 1/2 → 入口 → 绕柱 → 识别区悬停 → 识别 → 机动至目标正面 → 激光攻击 → 返航降落，各阶段超时/失败回退策略
2. **目标识别流水线**：CameraX 帧 → 二维码解码（编号牌）→ 目标列表比对 → 置信度累积判定
3. **激光指向控制**：识别完成后解算目标相对机体系方位，姿态微调对准 + 激光使能（38kHz/650nm 由外置硬件产生，手机侧仅发使能与瞄准指令）
4. **安全互锁**：激光使能严格门控于"识别完成"事件后，杜绝提前开启罚时

**Planned features**:

1. **Mission state machine**: takeoff → gate 1/2 → entrance → around pillar → hover at identification zone → identify → maneuver in front of target → laser strike → return and land, with per-stage timeout/failure fallbacks
2. **Target-recognition pipeline**: CameraX frame → QR decode (number sign) → compare against target list → confidence accumulation
3. **Laser pointing control**: after identification, solve the target bearing in body frame, fine-tune attitude for alignment and enable the laser (38 kHz / 650 nm generated by external hardware; the phone only issues enable and aim commands)
4. **Safety interlock**: laser enablement is hard-gated behind the “identification complete” event, eliminating the early-enable penalty

**已知限制**：任务链路依赖飞控二次开发接口（位置/速度设定点注入），需先选定开源飞控平台（PX4/ArduPilot/国产开源方案）；激光发射与声光靶标为外置硬件，不在本仓库范围；识别区光照、喷绘布地面纹理对视觉算法的影响需现场标定。

**Known limits**: the mission chain relies on flight-controller secondary development APIs (position/velocity setpoint injection), so an open platform must be chosen first (PX4/ArduPilot/domestic open options); the laser emitter and audible/visual target are external hardware outside this repo; lighting in the identification zone and the printed-cloth floor texture need on-site calibration for the vision algorithms.

## 目录结构 / Repository Layout

```
.
├── apps/                  # 手机端 App（Android / iOS）· Mobile apps (Android / iOS)
│   ├── camera/            # 相机采集与标定 · Camera capture and calibration
│   ├── depth/             # 深度估计模块 · Depth estimation
│   ├── slam/              # SLAM 与位姿输出 · SLAM and pose output
│   ├── inference/         # 深度学习推理引擎封装 · DL inference engine wrappers
│   └── embodied/          # 自进化具身决策模块 · Self-evolving embodied decision making
├── models/                # 端侧模型与转换脚本（tflite/onnx/gguf）· On-device models and conversion scripts
├── bridge/                # 手机 ↔ 机器人底盘 / ROS2 通信桥 · Phone ↔ chassis / ROS2 bridge
├── datasets/              # 实验数据集与标注工具配置 · Datasets and labeling tool configs
├── experiments/           # 可复现实验脚本与结果 · Reproducible experiment scripts and results
└── docs/                  # 原理、实验指导书、API 文档 · Theory, lab guides, API docs
```

## 快速开始 / Quick Start

```bash
# 克隆仓库及联动项目 / Clone this repo and its companion projects
git clone <repo-url>
cd <repo-dir>
git clone https://github.com/ByeIO/slambook2.rs      # 算法底座 / algorithm base
git clone https://github.com/ByeIO/bye.rosette.rs    # 通信中间件 / comms middleware

# Android：构建并安装实验 App / Build and install an experiment app
cd apps/android && ./gradlew installDebug

# iOS：用 Xcode 打开工程直接运行 / Open the project in Xcode and run
open apps/ios/PocketBot.xcodeproj

# 下载端侧模型 / Download on-device models
cd models && ./download.sh

# 连接机器人底盘（可选，串口 / BLE） / Connect the chassis (optional, serial / BLE)
cd bridge && cargo run -- --port /dev/ttyUSB0
```

## 典型实验 / Typical Experiments

1. **深度估计算验 / Depth evaluation**：拍摄室内场景，对比单目 / 双目深度精度（AbsRel、δ<1.25）· *Capture indoor scenes and compare monocular vs stereo depth accuracy (AbsRel, δ<1.25)*
2. **VIO 轨迹评估 / VIO trajectory evaluation**：手持或装到小车上绕行一圈，输出轨迹并与真值比对 · *Carry the phone or mount it on a car, walk a loop, output the trajectory and compare with ground truth*
3. **实时检测 + 避障 / Real-time detection + avoidance**：YOLO 端上检测 + 深度图投影，实现跟随 / 避障小车 · *On-device YOLO plus depth projection for a follow/avoid robot car*
4. **场景记忆与问答 / Scene memory and QA**：对着桌面物体提问“这是什么”，模型结合记忆作答 · *Ask “what is this” about desktop objects; the model answers with memory context*
5. **自进化闭环 / Self-evolution loop**：连续多天在同一场景运行，观察新物体类别的增量发现与准确率变化 · *Run in the same scene for days and watch incremental discovery of new object classes and accuracy drift*

## 性能参考（中端手机，2024 机型）/ Performance Reference (mid-range 2024 phone)

| 模块 / Module | 模型 / Model | 延迟 / Latency | 说明 / Notes |
| --- | --- | --- | --- |
| 单目深度 / Monocular depth | MiDaS-small INT8 | ~18 ms | 256×256 输入，NPU · *256×256 input, NPU* |
| 目标检测 / Object detection | YOLOv8n INT8 | ~12 ms | 320×320 输入，NPU · *320×320 input, NPU* |
| VIO | 自研前端 / in-house front end | ~25 ms | 640×480，CPU+DSP |
| 语义分割 / Semantic segmentation | DeepLabV3-small | ~30 ms | 384×384，GPU |

> 以上为量级参考，具体数值随机型与后端变化，复现脚本见 `experiments/`。
> *Order-of-magnitude reference only; actual numbers vary with device and backend. Reproduction scripts live in `experiments/`.*

## 路线图 / Roadmap

- [ ] 多机协同 / Multi-device SLAM：两台手机互为观测节点做分布式 SLAM · *Two phones as mutual observation nodes*
- [ ] 端云协同 / Edge-cloud collaboration：弱网下端侧自治、联网时云端蒸馏下发 · *On-device autonomy on weak links, cloud distillation when online*
- [ ] 更多底盘适配 / More chassis adapters：ROS2、Micro-ROS、串口 PWM · *ROS2, Micro-ROS, serial PWM*
- [ ] 端侧扩散模型 / On-device diffusion models：用于物体抓取位姿生成 · *For grasp-pose generation*

## 依赖与环境 / Requirements

- Android 10+（推荐支持 NNAPI / 厂商 NPU 的机型）· *Android 10+ (NNAPI / vendor NPU recommended)*
- iOS 16+（推荐带 LiDAR 的 Pro 机型）· *iOS 16+ (LiDAR-equipped Pro models recommended)*
- 可选 / *Optional*：ROS2 Humble、Rust 1.75+、Python 3.10+

## 许可证 / License

### 宽松的 FOSS 立场 / A Permissive FOSS Stance

本仓库遵循**宽松的 FOSS 立场**：代码免费使用、自由修改、自由再分发，只要求保留署名；能标注宽松许可的材料尽量标注宽松许可，不设商业限制、不搞闭源分发。

This repository adopts a **permissive FOSS stance**: free to use, modify and redistribute, with attribution as the only requirement; permissive licenses are preferred wherever applicable — no commercial restrictions, no closed-source redistribution.

### 本仓库自研代码：MIT / First-party Code: MIT

主体代码（`app/`、`desktop/`、`sections/` 等自研部分）以 [MIT License](LICENSE) 授权，Copyright (c) 2026 qsbye。

First-party code (`app/`, `desktop/`, `sections/`, etc.) is licensed under the [MIT License](LICENSE), Copyright (c) 2026 qsbye.

<a id="foss-default-license"></a>

### 未注明许可的材料：统一标注 CC BY-SA 3.0 / Unlicensed Material: CC BY-SA 3.0 by Default

对于仓库中收录的、**原始来源未注明任何许可**的第三方内容（参考工程、反编译存档、文章存档、图片素材等），按**互联网默认规则**统一以 [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/) 标注：

- **免费使用**：可自由阅读、复制、修改、再分发，无需付费、无需申请；
- **署名**：须注明原作者与出处（原始来源信息记录在 [NOTICE.md](NOTICE.md) 与各目录的 `PROVENANCE.md`）；
- **相同方式共享**：基于该材料的修改/再分发须以相同许可发布。

Third-party content archived here **whose original source states no license** (reference projects, decompiler archives, article archives, image assets, etc.) is uniformly marked [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/) following the **internet default rule**:

- **Free to use**: read, copy, modify and redistribute freely — no fees, no application needed;
- **Attribution**: credit the original author and source (recorded in [NOTICE.md](NOTICE.md) and each directory's `PROVENANCE.md`);
- **ShareAlike**: modifications/redistributions of that material must be released under the same license.

> 说明：上述统一标注是**本仓库对归档材料采用的整理约定**，用于给出明确的使用预期，避免"未声明 = 不可用"的僵局。
> 它不能替代原权利人的授权：若原作者另有声明（All Rights Reserved 或特定许可），以原作者声明为准，
> 本仓库的标注不构成对其作品的再许可。详见 [NOTICE.md](NOTICE.md)。
>
> *Note: this uniform marking is an **archival convention of this repository**, meant to give clear usage expectations and avoid the “unstated = unusable” deadlock. It does not replace authorization from the rights holder: where the original author states otherwise (All Rights Reserved or a specific license), that statement prevails, and this repository's marking grants no sublicense. See [NOTICE.md](NOTICE.md).*

欢迎 Issue 与 PR 交流实验复现问题。

Issues and PRs about reproducing the experiments are welcome.
