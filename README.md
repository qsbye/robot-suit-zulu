<p align="center">
  <img src="assets/monocular_depth_map.jpg" width="128" alt="RobotSuitZulu Logo">
</p>

# RobotSuitZulu — 玑函
* 手机机器人实验工具箱

把手机变成一台“会看、会算、会思考”的机器人实验平台：摄像头、IMU、算力、屏幕、网络全部端上闭环，不需要外接工控机，也不依赖云端推理。

## 为什么是手机

| 维度 | 手机 | 传统工控机 + 深度相机 |
| --- | --- | --- |
| 成本 | 一台旧手机即可 | 数千元起步 |
| 部署 | 装个 App | 驱动、标定、线缆一堆 |
| 传感器 | 摄像头 + IMU + ToF/激光雷达（部分机型） | 需单独采购 |
| 算力 | NPU / GPU / DSP，几十 TOPS | 常需外接 |
| 形态 | 随手可得、可挂到小车上 | 体积大 |

本项目把机器人感知与决策栈整体搬到手机端，用于教学实验、算法验证与快速原型。

## 核心能力

### 1. 端侧深度估计（On-device Depth Estimation）

- 单目深度：基于轻量 CNN / Transformer 的相对深度模型（TFLite / ONNX / Core ML）
- 双目 / 结构光：调用系统原生深度 API（ARKit Depth、ARCore Depth API）
- 融合深度：视觉深度 + IMU + LiDAR（机型支持时）做尺度校正与滤波
- 输出：逐像素深度图、点云、鸟瞰图（BEV），可直接喂给避障与建图模块

### 2. 端侧 SLAM（On-device SLAM）

- 视觉惯性里程计（VIO）：摄像头 + IMU 紧耦合，实时输出 6DoF 位姿
- 稀疏 / 半稠密建图：特征点地图、关键帧数据库、回环检测
- 稠密地图：TSDF / OctoMap / 3DGS 体素地图，用于导航与重建
- 对外接口：标准位姿 / 地图话题，可与 ROS2、小车底盘、机械臂联动

### 3. 端侧深度学习（On-device Deep Learning）

- 推理后端：TFLite、ONNX Runtime、Core ML、NCNN、MNN、厂商 NPU SDK
- 感知任务：目标检测（YOLO 系列）、语义 / 实例分割、OCR、姿态估计
- 加速手段：量化（INT8/FP16）、算子融合、NPU/GPU 委托、模型蒸馏
- 端上数据闭环：采集 → 标注 → 增量训练 → 量化 → 回灌部署，全在手机完成

### 4. 端侧自进化具身模型（On-device Self-evolving Embodied Model）

- 记忆：场景特征库 + 语义拓扑图，跨会话持久化
- 学习：在线自监督（对比学习、聚类新物体）、经验回放与技能库
- 决策：轻量 VLM / LLM（GGUF、Qwen 系列小模型）做任务规划与场景问答
- 进化：失败样本自动回流 → 端上微调（LoRA / Adapter）→ 模型热更新
- 闭环：感知（深度 + 检测）→ 建图（SLAM）→ 规划 → 执行（串口 / BLE 控制底盘）

## 联动项目

本工具箱的感知与通信底座复用/对接以下两个 Rust 生态项目：

| 项目 | 定位 | 在本工具箱中的角色 |
| --- | --- | --- |
| [slambook2.rs](https://github.com/ByeIO/slambook2.rs) | 《视觉 SLAM 十四讲》（slambook2）的 Rust 重构，覆盖几何、李代数、特征法/直接法 VO、BA、回环、稠密建图等 1–13 章算法，并提供 wasm 预构建版本 | 算法底座：提供相机模型、位姿求解（PnP / 对极几何）、曲线拟合与轨迹表示等基础算子，供端侧 SLAM 与二维码定位复用 |
| [bye.rosette.rs](https://github.com/ByeIO/bye.rosette.rs) | Rosette（玫瑰之心）：纯 Rust 实现的 ROS2 风格机器人框架，支持 DDS 分布式通信、topic/service/action、rosbag、webviz 等 | 通信中间件：手机端位姿、路径、检测结果以标准话题发布，与桌面端可视化、底盘控制节点互联（跨机可用 EasyTier 组网） |

典型链路：手机相机帧 → 二维码/特征检测（本仓库 `apps/`）→ 位姿求解（slambook2.rs 算子）→ 路径规划 → 通过 Rosette 话题发布 → 底盘执行 + 屏幕叠加显示预测路径。

## 规划实验

### 实验一：基于二维码的引导定位（QR-guided Localization）（已实现）✅

> 联动 [slambook2.rs](https://github.com/ByeIO/slambook2.rs) + [bye.rosette.rs](https://github.com/ByeIO/bye.rosette.rs)（话题输出规划中）；纯 Kotlin + Jetpack Compose + CameraX + ZXing，识别方法参照 WaveSend——工程位于 [`sections/exp1-qrcode-pose-estimate/`](sections/exp1-qrcode-pose-estimate/)，并已集成为主 app 的「二维码定位」标签页

**目标**：识别场景中的二维码（支持同帧多码、任意可识别尺寸），根据透视畸变估计相机（机器人）相对码面的位姿，并生成一条到达**最近二维码**的路径，以**蓝色曲线**实时绘制在预览画面上。

**流程**：

1. **检测**：CameraX `ImageAnalysis` 逐帧送 ZXing `GenericMultipleBarcodeReader` 多码解码（Hybrid → Global 直方图两级候选二值化）；由 3 个定位图案中心按平行四边形模型推算第 4 角
2. **位姿估计**：四角像素 ↔ 已知物理边长的平面方形做 4 点 DLT 求单应矩阵（h33=1，8×8 高斯消元），分解 `H = [r1 r2 t]` 得相机系位姿 `T_cw`（Gram-Schmidt 正交化 + z 正向约束），按码内容做 EMA 平滑抑制抖动
3. **路径规划**：相机系下从镜头前方到最近二维码中心的三次贝塞尔曲线
4. **可视化**：每个码画绿色锁定框 + 内容标签，蓝色路径曲线 + 红色目标点指向最近码；顶部状态条实时显示检出数量与最近码内容 / 距离
5. **联动输出**（规划中）：位姿经 slambook2.rs 的 SE(3) 类型规整后，打包为 `geometry_msgs/PoseStamped` 话题，由 Rosette 发布，webviz / 底盘节点订阅执行

**预期效果**：手机对着墙上的二维码缓慢移动，画面中蓝色曲线始终平滑地"指向"并延伸至二维码中心；同帧多码时自动锁定最近的一个，路径实时跟随重算。

**用到的 slambook2.rs 知识点**：相机模型与内参（ch5，固定 62° 视场角近似）、单应与位姿估计（ch7，单应分解替代 EPnP）。

**已知限制**：角点取自定位图案中心（非码面真实角点）、内参未标定，位姿精度为演示级，距离为相对量；详细说明见 [section README](sections/exp1-qrcode-pose-estimate/README.md)。

### 实验二：IMU 小球 + BLE 遥控 esp32（已实现）✅

> 纯 Kotlin + Jetpack Compose，无第三方 BLE 库；工程位于 [`sections/exp2-switch-page-imu/`](sections/exp2-switch-page-imu/)

**目标**：一个 App 演示三件事——陀螺仪姿态感知、摇一摇交互、手机经 BLE 遥控 esp32 小车/小灯。

**功能**：

1. **陀螺仪小球页**：`TYPE_GYROSCOPE` 角速度对时间积分得手机倾角（带回零衰减抑制 MEMS 零偏），倾角映射为小球速度，转动手机让小球在边界框内滚动
2. **摇一摇切页**：`TYPE_ACCELEROMETER` 三轴模长超出重力阈值（12 m/s²，冷却 800ms）判定为摇一摇，在小球页 → 说明页 → 遥控页间循环切换
3. **BLE 遥控页**（复刻传统 XML 布局的 Compose 版本）：
   - 纯 Android BLE API（`BluetoothLeScanner` + `BluetoothGatt` + CCCD 通知），替代 RxBLE/RxJava 封装
   - 扫描连接 esp32 的 Nordic UART 服务（`6E400001-...`，RX 写指令 / TX 收通知）
   - Compose Canvas 虚拟手柄：拖拽判定主方向（forward/backward/left/right），1 秒消抖后写入指令帧；连接成功自动翻转 esp32 小灯
   - 顶部导航栏 / 中部状态区（电量、速度、旋转角度）/ 底部日志区（25 行时间戳日志，自动滚底），马卡龙配色

**演示**：

| 小球页 | 说明页 | 摇一摇切页动图 |
| --- | --- | --- |
| ![ball](sections/exp2-switch-page-imu/screenshots/imu-ball-page.png) | ![info](sections/exp2-switch-page-imu/screenshots/imu-info-page.png) | ![demo](sections/exp2-switch-page-imu/screenshots/imu-demo.gif) |

**esp32 端固件**：[esp32笔记[6]-蓝牙(BLE)控制小灯](https://www.cnblogs.com/qsbye/p/17566976.html)

### 实验三：AR 画线（FOSS 版 Just a Line）✅

> 纯 Kotlin + Jetpack Compose + CameraX + 传感器，净室重写自 [逆向分析报告](reference/justaline/20261019-074718-JustaLine_逆向分析报告.md)，工程位于 [`sections/exp3-ar-draw-line/`](sections/exp3-ar-draw-line/)

**目标**：复刻 Google AR Experiments 的空间绘画应用 Just a Line——转动手机瞄准，按住屏幕拖动即可在空中画线——并把原版绑定的专有服务（ARCore / Firebase / Nearby / GMS）全部替换为开源栈，可在无 GMS 设备上运行。

**FOSS 改造**：

1. **3DoF magic-window 追踪替代 ARCore**：游戏旋转矢量传感器输出设备→世界旋转矩阵 R，笔画点存世界系坐标，每帧用 Rᵀ 重投影到屏幕，实现"线留在空气中"；触摸射线按针孔模型落到固定距离（近/中/远 0.7/1.5/3 m）的平面上
2. **Compose Canvas 替代 OpenGL 渲染器**：2D 线段 + 透视线宽（近粗远细）近似原版 GL 三角带，保留原版 finishStroke 的微小笔画处理逻辑
3. **零遥测零网络**：移除 Firebase Analytics / FCM / Nearby 协作，仅申请相机权限；视频录制降级为 PNG 快照导出（相册 `Pictures/RobotSuitZulu-AR`）
4. **8 色色板 + 三档笔宽 + 撤销/清空 + 追踪状态指示灯**，无相机权限时降级为深色背景天空画线模式

**已知限制**：3DoF（无平移视差）、旋转矢量长时缓慢漂移、FOV 为 62° 近似值——详见 [section README](sections/exp3-ar-draw-line/README.md)。

### 实验四：端侧单目深度估计（On-device Monocular Depth Estimation）🚧

> 参考 [DepthART](https://github.com/AIRI-Institute/DepthART)（arXiv:2409.15010，MIT License）——原仓库已存档至 [`reference/DepthART-main/`](reference/DepthART-main/)

**目标**：把 DepthART「单目深度估计 = 自回归细化任务」的范式搬到手机端——复用图像生成模型 VAR 的下一尺度预测先验，端上实时输出逐像素相对深度，伪彩色深度图叠加相机预览，并与实验一的二维码位姿联动做绝对尺度校正。

**原理**（DepthART 的三层拆解）：

1. **深度离散化**：预训练 VQ-VAE（`vae_ch160v4096z32`）把深度图编码为多尺度 token 金字塔——从 1×1 的全局摘要到细尺度的局部细节
2. **自回归细化**：VAR d16 以 RGB 图像为条件，按尺度从粗到细逐级预测深度 token；DepthART 的关键改动是把任务定义为「细化」——上一尺度的粗糙深度在下一尺度被修正补全而非从零生成，让生成先验与深度回归任务对齐
3. **解码还原**：VQ-VAE 解码器将最细尺度 token 还原为连续深度图

**流程**：

1. **复现训练**：Hypersim 合成数据集 + 官方预训练权重（VQ-VAE / VAR d16），`tools/dist_train.sh train_depthART.yaml` 训练；`tools/eval.py` 在 NYUv2 / ETH3D / iBims-1 / TUM 上复算 AbsRel、δ<1.25 并与论文对齐
2. **端侧转换**：PyTorch → ONNX 导出 → FP16 / INT8 量化 → ONNX Runtime Mobile / TFLite（NNAPI / NPU 委托）；延迟超标时蒸馏到更小骨干
3. **端侧推理**：CameraX 逐帧 → letterbox 预处理 → 推理 → 深度图上采样对齐
4. **可视化**：turbo 伪彩色深度图实时叠加预览，原图 / 深度 / 混合三档切换
5. **联动输出**：与实验一联动——二维码已知物理边长提供绝对尺度，将相对深度校正为米制深度；深度图规整为点云 / BEV，经 Rosette 以 `sensor_msgs/Image` 话题发布，供避障与建图节点订阅

**预期效果**：手机对准室内场景，伪彩色深度图实时跟随（近暖远冷，遮挡边界清晰）；场景中放置二维码后，深度读数经尺度校正接近米制真值——实验一的几何位姿与实验四的学习深度互相校验。

**对照与知识点**：经典几何法单目稠密重建（slambook2.rs ch12 极线搜索 + 深度滤波）对照生成式学习法（VQ token + 自回归 Transformer）；端侧量化部署（INT8 / FP16、NPU 委托）。

**已知限制**：单目深度本质为相对量，绝对尺度依赖外部校正（实验一联动补齐）；VAR d16 + VQ-VAE 参数量对中端机型有压力，需在精度与帧率间权衡。

### 实验五：端上微型训练框架（TinyTorch On-device）（已实现）✅

> 纯 Kotlin + Jetpack Compose，零第三方 ML 框架依赖；参考 [TinyTorch](https://github.com/keith2018/TinyTorch)（MIT License，原仓库已存档至 [`reference/TinyTorch-main/`](reference/TinyTorch-main/)），配套解读文档：[手写一个神经网络训练框架 (TinyTorch)](docs/tinytorch-neural-network-training-framework-robot9.md)。工程位于 [`sections/exp5-tiny-torch-on-device/`](sections/exp5-tiny-torch-on-device/)

**目标**：把 TinyTorch「Tensor/Function 计算图 + 自动微分 + Module/Optimizer」的最小内核用纯 Kotlin 在 Android 端实现——实验四的固定结构 MLP 升级为支持任意算子组合的通用微型训练框架，MNIST 训练全流程端上闭环。

**实现**：

1. **Tensor/TensorImpl 分层**：底层 float32 张量只做数值运算（matmul NN/NT/TN、relu、log_softmax）；上层 Tensor 挂 grad/gradFn
2. **Function 即计算图节点**：算子前向时把输入 gradFn 链入 `nextFunctions` 建图；叶子节点 AccumulateGrad 写回梯度（对应 TinyTorch FuncLeaf）
3. **拓扑调度反传**：消费者计数 + 梯度累加，支持分叉 DAG（同一 Tensor 多处消费），比实验四固定层序反传更通用
4. **Module/Adam/StepLR/DataLoader**：递归参数汇总、train/eval 模式；MNIST 来自 TinyTorch 随附 IDX（gzip 进 assets，训练取 1 万子集）
5. **UI**：损失曲线实时绘制、测试样本预测预览（绿对红错）、checkpoint 保存/加载

**验证**：`AutogradTest` 数值梯度校验（解析梯度 vs 有限差分）通过；构建安装见 [section README](sections/exp5-tiny-torch-on-device/README.md)。

**已知限制**：纯 CPU float32、朴素三重循环 matmul、无卷积算子；教学与范式验证用途，非生产训练。

### 实验六：QuickJS 端上脚本绘图（On-device Script Plotting）✅

> 参考 [摸鱼笔记[11]-使用quickjs设计绘图指令绘制机器视觉结果图](https://www.cnblogs.com/qsbye/p/19577773)；纯 Kotlin + Jetpack Compose + [taoweiji/quickjs-android](https://github.com/taoweiji/quickjs-android)（Apache-2.0），工程位于 [`sections/exp6-quickjs-editor/`](sections/exp6-quickjs-editor/)

**目标**：在 Android 端复刻博客中 CameraViewerAutoWeld 的 Cognex 风格绘图指令集——JavaScript 脚本通过 `PlotLine`/`PlotCircle`/`PlotString`/`PlotPolygon`/`PlotPoint` 绘制机器视觉标注，实现"改脚本即看效果"的快速迭代。

**实现**：

1. **QuickJS 引擎**：`io.github.taoweiji.quickjs:quickjs-android:1.4.6`（Maven Central），注册 5 个 Plot 函数 + `Clear`/`print` 为全局 JS 函数
2. **指令模式**：每个 Plot 调用生成一个 `DrawingCommand`（对应博客的指令收集器模式），脚本执行完毕后统一在 Compose Canvas 上渲染
3. **坐标映射**：图像原始分辨率（5120×5120）→ 屏幕坐标自动缩放，线宽/字号随缩放同步调整
4. **UI**：Compose 多行脚本编辑器（等宽字体）+ 运行/重置按钮 + 预览画布 + 控制台输出

**与博客桌面版的差异**：Swing `Graphics2D` → Compose `DrawScope`；`AffineTransform` → 简单 scale/offset 数学；`RSyntaxTextArea` → Compose `BasicTextField`；MQTT 数据源 → 本地脚本执行（联动扩展预留）。

**已知限制**：无语法高亮（Compose TextField 原生不支持）；无图像底图（ShowImage 未实现）；消息源为本地空对象（非 MQTT）。

### 实验七：OpenMV for Android——手机端脚本化机器视觉（功能规划）📋

> 参考 [OpenMV](https://github.com/openmv/openmv)（MIT License，原仓库已存档至 [`reference/openmv-master/`](reference/openmv-master/)）——把 OpenMV「脚本驱动的嵌入式视觉硬件」范式搬到手机：**手机本身就是摄像头 + 算力 + IDE 一体的机器视觉智能硬件**，不再需要独立摄像头模块。工程目录 [`sections/exp7-openmv-for-android/`](sections/exp7-openmv-for-android/)（占位，暂不实现）

**目标**：复刻 OpenMV 的核心体验——在设备上用脚本快速搭视觉流水线，改脚本即看结果——但脚本引擎用 QuickJS（复用实验六）替代 MicroPython，视觉算子用纯 Kotlin 在 CameraX 帧流上实现，手机同时承担"IDE（编辑器 + 帧缓冲预览 + 阈值调试）"与"视觉硬件（采集 + 处理 + 对外输出）"双重角色。

**与 OpenMV 的对位映射**：

| OpenMV 概念 | 手机端对应 |
| --- | --- |
| MicroPython 脚本 | QuickJS JavaScript 脚本（实验六引擎扩展） |
| imlib 算法库（C 实现） | 纯 Kotlin 算子库，逐帧作用于 CameraX ImageAnalysis 帧 |
| OpenMV IDE（帧缓冲实时预览、阈值编辑器、直跑脚本） | Compose IDE：脚本编辑器 + 实时预览 + 结果叠加层 |
| RPC 库（UART/I2C/SPI/CAN，同步头+通道+opcode+CRC 包） | BLE Nordic UART（实验二）/ WiFi 对外发布结构化结果，包格式参照 [docs/protocol.md](reference/openmv-master/docs/protocol.md) |
| `print()` → USB VCP | 控制台输出面板（实验六已有） |

**规划脚本 API**（JS 侧，参照 imlib 能力清单分期实现）：

1. **图像基元**：`image.grayscale()` / `image.binary(threshold)` / `image.erode/dilate` —— 灰度、二值化、形态学
2. **色块追踪**：`image.find_blobs(阈值, roi)` → 返回色块质心/包围盒/面积（对标 imlib `blob.c`，巡线/颜色目标场景）
3. **码识别**：`image.find_qrcodes()`（复用实验一 ZXing）→ 内容 + 四角 + 位姿；后续可扩展 AprilTag（远距鲁棒性优于 QR，实验九候选）
4. **几何与运动**：`image.find_lines()`（Hough）、`image.find_circles()`、帧间相位相关位移估计（对标 `phasecorrelation.c`，可做光流式里程计）
5. **模板与特征**：`image.find_template()`、`image.find_keypoints()`（AGAST/FAST/ORB 对标）
6. **绘图叠加**：复用实验六 `PlotLine`/`PlotCircle`/`PlotString` 指令集，脚本检测结果直接画在预览上

**规划流程**：

1. **流水线骨架**：CameraX 帧 → Kotlin 算子链（每帧一个 `image` 对象传入 JS）→ 脚本 `process(image)` 返回标注 → Compose 叠加渲染，目标 ≥10fps
2. **IDE 交互**：编辑器 + 预览分屏；阈值参数面板（滑条实时改 JS 全局变量，无需重跑脚本——对应 OpenMV IDE 阈值编辑器）
3. **对外输出**：识别结果（色块坐标/码内容/位姿）经 BLE 按 OpenMV RPC 风格包格式发布，供 esp32 底盘/飞控消费；与 Rosette 话题桥对齐预留
4. **性能策略**：算子先纯 Kotlin 实现保 FOSS，热点（如二值化、卷积）后续可选 RenderScript 替代方案或 NNAPI 委托（保持开源栈）

**预期效果**：打开「脚本视觉」页，粘贴一段 OpenMV 风格的找色块脚本（语法改 JS），手机对着彩色目标即实时框出质心轨迹；改阈值滑条立即生效——一部旧手机即成为可编程视觉传感器。

**已知限制**：JS 逐帧回调有 JNI/引擎开销，帧率上限低于原生 C 实现（OpenMV 在 STM32H7 上 blob 可达 30fps+）；imlib 的 SIMD 汇编优化无法直接复用，热点算子需重写；本地存档的 openmv-master 顶层缺 LICENSE 文件，引用其代码片段前需核对上游许可。

### 实验八：蜂鸟无人机——昆虫式视觉循迹返航（Hummingbird Drone: Insect-inspired Visual Route Following）（功能规划）📋

> 参考 [Visual route following for tiny autonomous robots](https://www.science.org/doi/10.1126/scirobotics.adk0310)（van Dijk et al., *Science Robotics* 9, eadk0310, 2024；TU Delft，56g Crazyflie Brushless，STM32F4/192KB RAM 上实现）。工程目录 [`sections/exp8-hummingbird-drone/`](sections/exp8-hummingbird-drone/)（占位，暂不实现）

**目标**：把论文的「昆虫式视觉导航」范式搬到手机 + 微型无人机平台——去程沿途记录**极度压缩的全景快照**，返程用「里程计粗走 + 视觉归巢（visual homing）精修」交替前进，周期性地把里程计漂移清零，让算力/内存极度受限的平台也能沿百米级原路返航。

**原理**（蚂蚁/蜜蜂导航的两要素机器人化）：

1. **路径积分 = 里程计**：去程与返程的"腿"（leg）均用里程计位移矢量连接——论文用光流 + 激光测距（速度 × 时间），手机端对应 VIO/IMU 积分或轮式底盘码盘
2. **视图记忆 = 快照归巢**：在每个快照点把全景图压缩为十几字节的"快照"；返程到达某快照附近后，用视觉归巢把当前视图"对齐"回快照视图，消除该段累积的里程计漂移，再出发下一段
3. **快照间距按里程计精度拉满**：只要里程计漂移能保证无人机落在下一个快照的"捕获区"（catchment area）内即可——相比顺序归巢需 25cm 间距，论文将间距放宽到 1~2m，100m 路线内存消耗 < 20 字节/米

**快照表示与归巢算法**（论文三选一对比后采用 Fourier 法，内存效率最高）：

1. 全景原图 128×96 → 柱面重投影 128×16 → 灰度 + 垂直平均 → 128 点一维周期信号
2. FFT 变换后丢弃直流与高频分量，复系数量化为 8bit 对 → **单个快照仅 16 字节**
3. 归巢矢量由"近似图像差函数"的二次曲面 ℰ(h) = ½hᵀAh + bᵀh + c 解析最小化得到——只需一次 FFT + 3×3 矩阵求逆，微控制器级算力即可实时运行
4. 粗旋向对齐用暴力旋转搜索（替代原相位法，对环境对称性更鲁棒）

**规划流程**（手机端复现）：

1. **平台**：手机（相机 + IMU + 算力）挂载微型四轴底盘，或手机做地面站经 BLE/WiFi 遥控 esp32 级小机（复用实验二 BLE 链路），视觉与导航计算全在手机侧闭环
2. **去程记录**：任意方式飞出路线（手动遥控/航点），按里程计距离每 1~2m 自动落一个快照：CameraX 帧 → 灰度 → 垂直平均 → FFT 压缩 → 连同段间里程计矢量一并存入路线表
3. **返程循迹**：每段先按里程计矢量飞往下一快照点，进入捕获区后切换 Fourier 归巢收敛（约 0.5m 内），到位重置位姿，逐段重复直至回到起点
4. **可视化**：Compose 实时绘制路线拓扑（快照点 + 里程计矢量）、当前归巢矢量方向、IDF 曲线与捕获区状态
5. **联动输出**（规划中）：快照点/归巢矢量经 Rosette 以 `geometry_msgs/PoseStamped` 话题发布，与 slambook2.rs 位姿类型对齐；与实验一二维码位姿联动可为路线提供绝对尺度锚点

**预期效果**：手持手机（或挂载小机）在室内走出一条含弯道的路线后，返程段段"先粗走后对齐"，长距离误差有界不发散；百米路线总内存开销约 1KB 量级，演示"导航不需要 SLAM 大图"的昆虫式极简方案。

**对照与知识点**：全景快照/Fourier 归巢对照实验一的几何位姿估计（整体式外观匹配 vs 稀疏特征/几何求解）；里程计漂移建模对照 slambook2.rs ch10 后端；论文基线为 Crazyflie + Flowdeck（光流 RMSE 13cm/5m），手机端可用 VIO 或轮式里程计对标。

**已知限制**（论文结论）：Fourier 快照依赖地平线附近的垂直特征（墙、门、窗对比），纯色走廊无法修正沿线漂移；归巢仅在捕获区内收敛，路线首次走过必须无碰撞（环境静态假设）；路线复用不含避障，需另配光流/深度避障模块。

### 实验九：侦查无人机——室内自主空中侦察（Reconnaissance Drone）（任务规划）📋

> 对应[睿抗机器人开发者大赛工程竞技赛道「空中侦察」项目](../projects/ByeIO/subproject/毕业设计/睿抗/睿抗无人机比赛要求.md)任务要求。工程目录 [`sections/exp9-reconnaissance-drone/`](sections/exp9-reconnaissance-drone/)（占位，仅描述任务，暂不实现）

**赛题背景**：5m×5m×4m 室内安全网场地模拟房间肃清——内有 1 名武装人员（真实目标）与 2 名平民（假目标），无人机需全程自主进入房间、识别并"打击"正确目标，替代人工突击以降低伤亡风险。对应 Vijay Kumar 无人机"5S"趋势（Small/Safe/Smart/Speed/Swarms）中的智能化自主化方向。

**比赛任务链**（计时制，10 分钟上限，全程自主——手动飞行按 5 倍计时）：

1. **任务一**：自主起飞，按顺序穿越障碍 1、障碍 2（各 0.5×0.1×3m），到达 1.5m 宽任务区入口
2. **任务二**：穿越入口，绕开圆柱形障碍物（直径 0.3m、高 2m），到达目标识别区
3. **任务三**：悬停于识别区上方，先识别目标编号指示牌（字母/数字/二维码，0.5×0.5m），再对各目标（0.5×0.5m，离地 1m）逐一识别，判定正确目标
4. **任务四**：自主移动至正确目标正前方，开启机载激光器（载波 38kHz、波长 650nm 红光）模拟攻击，命中靶标任意位置触发声光反馈；**识别完成前开激光罚时 60s**
5. **任务五**：裁判确认命中后返航起飞点降落

**罚时要点**：触障 +30s/次、未悬停识别 +60s、误判/错攻 +60s、激光提前开启 +60s、碰撞失控可换机重飞一次。

**无人机硬指标**：四旋翼轴距 400~550mm、续航 ≥12min、开源飞控可二次开发、自主/手动模式可切换、状态实时回传、控制半径 ≥200m；**禁用 UWB、动捕等外置定位**——室内定位必须机载自闭环。

**与本工具箱的对接**（手机作机载协同计算机，替代参考机型 Xavier NX 方案）：

| 比赛需求 | 复用模块 |
| --- | --- |
| 目标编号牌/目标识别（二维码、字母数字） | 实验一 ZXing 多码检测 + 位姿估计（0.5m 码天然适配） |
| 室内定位（禁 UWB/动捕） | 实验一二维码锚点 + IMU 航位推算，或实验八 Fourier 快照归巢 |
| 障碍感知与绕障 | 实验四端侧深度估计（相对深度 → 障碍距离场） |
| 任务脚本编排与可视化调试 | 实验六 QuickJS 脚本引擎（任务状态机脚本化、结果图绘制） |
| 机载算力/通信 | 手机 NPU + BLE/串口下传飞控指令（复用实验二 BLE 链路） |

**规划功能**：

1. **任务状态机**：起飞 → 穿越门 1/2 → 入口 → 绕柱 → 识别区悬停 → 识别 → 机动至目标正面 → 激光攻击 → 返航降落，各阶段超时/失败回退策略
2. **目标识别流水线**：CameraX 帧 → 二维码解码（编号牌）→ 目标列表比对 → 置信度累积判定
3. **激光指向控制**：识别完成后解算目标相对机体系方位，姿态微调对准 + 激光使能（38kHz/650nm 由外置硬件产生，手机侧仅发使能与瞄准指令）
4. **安全互锁**：激光使能严格门控于"识别完成"事件后，杜绝提前开启罚时

**已知限制**：任务链路依赖飞控二次开发接口（位置/速度设定点注入），需先选定开源飞控平台（PX4/ArduPilot/国产开源方案）；激光发射与声光靶标为外置硬件，不在本仓库范围；识别区光照、喷绘布地面纹理对视觉算法的影响需现场标定。

## 目录结构

```
.
├── apps/                  # 手机端 App（Android / iOS）
│   ├── camera/            # 相机采集与标定
│   ├── depth/             # 深度估计模块
│   ├── slam/              # SLAM 与位姿输出
│   ├── inference/         # 深度学习推理引擎封装
│   └── embodied/          # 自进化具身决策模块
├── models/                # 端侧模型与转换脚本（tflite/onnx/gguf）
├── bridge/                # 手机 ↔ 机器人底盘 / ROS2 通信桥
├── datasets/              # 实验数据集与标注工具配置
├── experiments/           # 可复现实验脚本与结果
└── docs/                  # 原理、实验指导书、API 文档
```

## 快速开始

```bash
# 克隆仓库及联动项目
git clone <repo-url>
cd <repo-dir>
git clone https://github.com/ByeIO/slambook2.rs      # 算法底座
git clone https://github.com/ByeIO/bye.rosette.rs    # 通信中间件

# Android：构建并安装实验 App
cd apps/android && ./gradlew installDebug

# iOS：用 Xcode 打开工程直接运行
open apps/ios/PocketBot.xcodeproj

# 下载端侧模型
cd models && ./download.sh

# 连接机器人底盘（可选，串口 / BLE）
cd bridge && cargo run -- --port /dev/ttyUSB0
```

## 典型实验

1. **深度估计算验**：拍摄室内场景，对比单目 / 双目深度精度（AbsRel、δ<1.25）
2. **VIO 轨迹评估**：手持或装到小车上绕行一圈，输出轨迹并与真值比对
3. **实时检测 + 避障**：YOLO 端上检测 + 深度图投影，实现跟随 / 避障小车
4. **场景记忆与问答**：对着桌面物体提问“这是什么”，模型结合记忆作答
5. **自进化闭环**：连续多天在同一场景运行，观察新物体类别的增量发现与准确率变化

## 性能参考（中端手机，2024 机型）

| 模块 | 模型 | 延迟 | 说明 |
| --- | --- | --- | --- |
| 单目深度 | MiDaS-small INT8 | ~18 ms | 256×256 输入，NPU |
| 目标检测 | YOLOv8n INT8 | ~12 ms | 320×320 输入，NPU |
| VIO | 自研前端 | ~25 ms | 640×480，CPU+DSP |
| 语义分割 | DeepLabV3-small | ~30 ms | 384×384，GPU |

> 以上为量级参考，具体数值随机型与后端变化，复现脚本见 `experiments/`。

## 路线图

- [ ] 多机协同：两台手机互为观测节点做分布式 SLAM
- [ ] 端云协同：弱网下端侧自治、联网时云端蒸馏下发
- [ ] 更多底盘适配：ROS2、Micro-ROS、串口 PWM
- [ ] 端侧扩散模型：用于物体抓取位姿生成

## 依赖与环境

- Android 10+（推荐支持 NNAPI / 厂商 NPU 的机型）
- iOS 16+（推荐带 LiDAR 的 Pro 机型）
- 可选：ROS2 Humble、Rust 1.75+、Python 3.10+

## 许可证

本仓库主体代码以 [MIT License](LICENSE) 授权，Copyright (c) 2026 qsbye。

仓库中收录的第三方内容（参考工程、应用存档、文章存档等）**不属于** MIT 覆盖范围，版权归原作者，明细见 [NOTICE.md](NOTICE.md)。

欢迎 Issue 与 PR 交流实验复现问题。
