# RobotSuitZulu — 手机机器人实验工具箱

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

### 实验一：基于二维码的引导定位（QR-guided Localization）🚧

> 联动 [slambook2.rs](https://github.com/ByeIO/slambook2.rs) + [bye.rosette.rs](https://github.com/ByeIO/bye.rosette.rs)

**目标**：识别场景中的二维码，根据其透视畸变估计相机（机器人）相对二维码的 6DoF 位姿，并规划一条到达二维码的路径，以**蓝色曲线**实时绘制在预览画面上。

**流程**：

1. **检测**：逐帧检测二维码（如 AprilTag / QR），提取四角像素坐标
2. **位姿估计**：利用码面已知物理尺寸 + 透视畸变，由单应矩阵（Homography）初始化，再用 PnP（EPnP + 迭代优化）求解相机坐标系下二维码的相对位姿 `T_cw`
3. **路径规划**：将目标位姿投影到地面平面（2D SE(2)），结合当前估计位姿生成平滑可达路径（贝塞尔 / 最小 jerk 曲线）
4. **可视化**：路径以蓝色曲线叠加到相机预览画面（或俯视图），随位姿更新实时重绘
5. **联动输出**：位姿经 slambook2.rs 的 SE(3) 类型规整后，打包为 `geometry_msgs/PoseStamped`、`nav_msgs/Path` 话题，由 Rosette 发布，webviz / 底盘节点订阅执行

**预期效果**：手机对着墙上的二维码缓慢移动，画面中蓝色曲线始终平滑地"指向"并延伸至二维码中心；移动到不同位置，路径实时跟随重算。

**用到的 slambook2.rs 知识点**：相机模型与内参标定（ch5）、对极几何与位姿估计（ch7）、李群李代数与曲线拟合（ch4/ch6）。

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

MIT License. 欢迎 Issue 与 PR 交流实验复现问题。
