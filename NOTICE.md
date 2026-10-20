# 第三方内容与许可声明（NOTICE）

本仓库主体代码（`app/`、`desktop/`、`sections/` 等）以 [MIT License](LICENSE) 授权，Copyright (c) 2026 qsbye。

下列内容为第三方版权材料，仅作学习参考收录，**不属于** MIT License 覆盖范围，版权归原作者或机构所有。如需再分发或商用，请自行获得权利人授权。

## 1. reference/justaline/

- `JustaLine_v2.1.6_danji100.com.apk`：Google AR Experiments 应用 **Just a Line**（包名 `com.arexperiments.justaline`）的发行版二进制，获取自第三方下载站，Google 保留全部权利。该应用的源码在 [googlecreativelab/justaline-android](https://github.com/googlecreativelab/justaline-android) 以 Apache-2.0 开源，但本仓库中的 APK 二进制并非来自官方渠道。
- `20261019-074718-JustaLine_逆向分析报告.md`：基于上述 APK 的静态分析学习报告，仅供研究参考。

## 2. reference/SLAM_AR_Android-master/

GitHub 第三方 SLAM AR Android 工程的完整副本，原仓库**未附带许可证**，依著作权法原作者保留所有权利；本仓库仅作本地编译参考收录。

- 其中 `OpenCVLibrary450/` 为 OpenCV 4.5.0 Android 模块；OpenCV 本身以 [Apache-2.0](https://opencv.org/license/) 授权。

## 3. docs/sources/*.html

第三方网页文章的本地存档（来源包括机器之心、博客园、声网 Agora、Reddit、jmvalin.ca 等），版权归原作者所有，存档仅供离线查阅。

## 4. docs/*.md（整理稿）

由 `docs/sources/` 中的 HTML 经 [markitdown](https://github.com/microsoft/markitdown) 转换并人工整理排版，各篇文首均标注原文来源与链接，仅供学习索引。

## 5. assets/

- `monocular_depth_map.jpg`、`waveform.png`：项目演示与 Logo 素材。
- `story_*.jpg`、`server-fan-noise*.m4a`：参考素材，版权归原作者。

## 6. reference/DepthART-main/

GitHub [AIRI-Institute/DepthART](https://github.com/AIRI-Institute/DepthART) 工程的完整副本（论文 *DepthART: Monocular Depth Estimation as Autoregressive Refinement Task*，arXiv:2409.15010），以 [MIT License](reference/DepthART-main/LICENSE) 授权，Copyright (c) 2024 AIRI。本仓库仅作端侧深度估计实验（实验四）的技术参考收录；原仓库的数据集下载链接与预训练权重（VAR d16 / VQ-VAE，源自 FoundationVision/VAR）不随本仓库分发。

## 7. reference/TinyTorch-main/

GitHub [keith2018/TinyTorch](https://github.com/keith2018/TinyTorch) 工程的完整副本（从零手写 C++ 神经网络训练框架），以 [MIT License](reference/TinyTorch-main/LICENSE) 授权，Copyright (c) 2024 Keith。本仓库仅作端上微型训练框架实验（实验五）的技术参考收录；随附 MNIST 数据集（`examples/mnist/data/`）为原始 Yann LeCun 公开数据，版权归原作者。

## 运行时依赖（构建时由 Gradle 自动拉取，不随本仓库分发）

| 依赖 | 许可证 |
| --- | --- |
| AndroidX / Jetpack Compose / Material Components | Apache-2.0 |
| Kotlin（JetBrains） | Apache-2.0 |
| Compose Desktop（JetBrains） | Apache-2.0 |
| CameraX（`sections/exp3-ar-draw-line/` 使用） | Apache-2.0 |
| ZXing core（`sections/exp1-qrcode-pose-estimate/` 与主 app 二维码定位页使用） | Apache-2.0 |
| JUnit 4（仅测试） | EPL-1.0 |
