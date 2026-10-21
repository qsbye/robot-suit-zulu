# 第三方内容与许可声明（NOTICE）

本仓库主体代码（`app/`、`desktop/`、`sections/` 等）以 [MIT License](LICENSE) 授权，Copyright (c) 2026 qsbye。

下列内容为第三方版权材料，仅作学习参考收录，**不属于** MIT License 覆盖范围，版权归原作者或机构所有。如需再分发或商用，请自行获得权利人授权。

其中**原始来源未注明任何许可**的材料，按本仓库的宽松 FOSS 约定统一以 [CC BY-SA 3.0](https://creativecommons.org/licenses/by-sa/3.0/) 标注（免费使用 + 署名 + 相同方式共享），见 [README.md](README.md#未注明许可的材料统一标注-cc-by-sa-30)。该标注是本仓库的归档约定，不能替代原权利人的授权；原作品另有声明时以原声明为准。

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

## 8. reference/xaerominimap/xaero-re/

- `xaerominimap-fabric-26.3-26.6.1.jar`：Xaero's Minimap（作者 **xaero96**）Fabric 版发行 jar，原许可为 **All Rights Reserved**（jar 内 `LICENSE_xaerohud` 全文仅此两词）。仅作本地参考。
- `xaero-re/src/`、`xaero-re/libsrc/`：上述 jar（及内嵌 `xaerolib-fabric-26.3-1.8.3.jar`）经 CFR 0.152 反编译得到的 Java 源码存档，是原作品的**衍生复制**。
- 该目录按仓库维护者要求以 **CC BY-SA 3.0** 标注，但**该标注未经版权人授权**：原作品为 All Rights Reserved，权利人并未以 CC BY-SA 3.0 或任何其他许可发布，本仓库无权再许可。详见 [PROVENANCE.md](reference/xaerominimap/xaero-re/PROVENANCE.md)。仅作架构研究存档，不用于构建、分发或商用。
- `20261021-124516-XaeroMinimap_逆向分析报告.md`：静态分析学习报告，不含反编译代码，仅记录架构与参数事实。

## 运行时依赖（构建时由 Gradle 自动拉取，不随本仓库分发）

| 依赖 | 许可证 |
| --- | --- |
| AndroidX / Jetpack Compose / Material Components | Apache-2.0 |
| Kotlin（JetBrains） | Apache-2.0 |
| Compose Desktop（JetBrains） | Apache-2.0 |
| CameraX（`sections/exp3-ar-draw-line/` 使用） | Apache-2.0 |
| ZXing core（`sections/exp1-qrcode-pose-estimate/` 与主 app 二维码定位页使用） | Apache-2.0 |
| quickjs-android（`sections/exp6-quickjs-editor/` 使用） | Apache-2.0 |
| JUnit 4（仅测试） | EPL-1.0 |
