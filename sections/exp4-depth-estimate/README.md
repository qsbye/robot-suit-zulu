# exp4：端侧单目深度估计（On-device Monocular Depth Estimation）

> 呼应 DepthART（arXiv:2409.15010，仓库存档于 `reference/DepthART-main/`，MIT）「合成数据 + 学习式深度先验」思想的玩具级 FOSS 实现。
> 识别/训练范式参照主 app 消音器的 WaveformMlp + AdamTrainer：**确定性的部分交给信号处理，网络只学一件小事**。
> 纯 Kotlin + Jetpack Compose，包名 `cn.qsbye.depthest`，仅 CAMERA 权限、无 INTERNET。

## 与 DepthART 原版的对照

| DepthART 原版 | 本实验（玩具版） |
| --- | --- |
| VQ-VAE 深度离散化 + VAR 逐尺度自回归（亿级参数） | ~465 参数 MLP，逐格回归相对深度 |
| Hypersim 等真实感合成数据集 | 程序化合成场景（地平线/透视地面/朗伯球体/立柱，带真值深度） |
| Python + PyTorch 训练，端上仅推理 | **真机端上训练**（纯 Kotlin 手写前向/反向 + Adam，数十秒收敛） |
| TFLite/ONNX 导出 + NNAPI/NPU 加速 | 无任何 NN 框架，推理即普通 Kotlin 浮点循环 |
| 绝对/相对深度多任务头 | 相对深度（0=far → 1=near），无绝对尺度 |

## 管线

1. **特征提取**（[FeatureGrid.kt](app/src/main/java/cn/qsbye/depthest/FeatureGrid.kt)）——相机帧降采样为 40×30 网格亮度后逐格提取 8 维特征：归一化坐标 ×2、亮度、|∂I/∂x|、|∂I/∂y|、3×3 盒滤波、平滑场梯度模、局部标准差。合成训练数据与相机帧走**同一管线**，保证特征同分布。
2. **网络**（[DepthNet.kt](app/src/main/java/cn/qsbye/depthest/DepthNet.kt)）——Dense(8→20,tanh) → Dense(20→12,tanh) → Dense(1,sigmoid)，手写反向传播，Adam 优化（β 幂查表），无堆分配的单样本推理接口。
3. **端上训练**（[DepthTrainer.kt](app/src/main/java/cn/qsbye/depthest/DepthTrainer.kt) + [SceneSynthesizer.kt](app/src/main/java/cn/qsbye/depthest/SceneSynthesizer.kt)）——按场景 85/15 划分训练/验证集（防同场景泄漏），mini-batch 512，学习率三段退火，验证集早停 + 最优权重回退。UI 实时绘制训练/验证损失双曲线。
4. **推理与可视化**（[DepthEngine.kt](app/src/main/java/cn/qsbye/depthest/DepthEngine.kt) + [DepthScreen.kt](app/src/main/java/cn/qsbye/depthest/DepthScreen.kt)）——旋转校正的 4×4 均匀采样、逐格推理、时间域 EMA 平滑（0.55/0.45）、turbo 风格七段伪彩色叠加（原图/混合/深度三档），推理限频 ~8 fps。

## 使用

1. 启动 app，授予相机权限
2. 点击「开始训练」（程序化合成 120 个场景 ≈ 10.4 万训练格，真机数十秒；损失曲线实时可见，验证 MSE 平滑下降至 ~0.01–0.05 量级）
3. 对准任意室内/街道场景，伪彩色叠加显示学习到的**相对深度先验**：画面上红=推断近、蓝=推断远

## 已知限制（如实声明）

- **学的是先验，不是几何**：网络从合成分布学到「画面上部远、下部近、暗边=物体边界、亮凸=球体」等统计规律，对真实场景给出的是合理观感而非真实测距；无法估计训练分布外结构（如天花板吊灯、悬空物体）。
- **无绝对尺度**：输出 0–1 相对深度，与 DepthART 的米制头不同；绝对尺度可后续由实验一的二维码已知物理边长校正（规划中）。
- **分辨率极低**（40×30）：受纯 Kotlin 浮点吞吐限制，边界呈大色块；EMA 平滑抑制闪烁的同时进一步模糊细节。
- **合成-实拍域差距**：真实光照/纹理与合成场景差异会显著影响输出质量。
- DepthART 的 VQ-VAE/VAR 结构与 Rosette 话题联动为规划项，未实现。

## 依赖

- AndroidX Compose BOM / Material3 / Activity-Compose（Apache-2.0）
- CameraX core/camera2/lifecycle/view 1.4.0（Apache-2.0）
- 深度网络、训练器、场景合成器、伪彩色表：本仓库原创纯 Kotlin 实现
