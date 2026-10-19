# exp1：基于二维码的引导定位（QR-guided Localization）

实验一的 FOSS 实现：逐帧检测二维码 → 由四角透视估计相机相对码面的位姿 →
生成到达二维码的三次贝塞尔路径，以**蓝色曲线**实时叠加在预览画面上。

独立可构建：`./gradlew :app:assembleDebug`，包名 `cn.qsbye.qrpose`。

## 功能流程

1. **检测**：CameraX `ImageAnalysis` 逐帧送 ZXing 解码（`QRCodeReader`，
   Hybrid 分块二值化 → Global 直方图两级候选，参照 WaveSend 的
   [QrAnalyzer](https://github.com/ByeIO/wave-send-zulu-air-fountain-android) 实现），
   由 3 个定位图案中心按平行四边形模型推算第 4 角
2. **位姿估计**：四角像素 ↔ 已知边长（默认 10cm）的平面方形做 4 点 DLT
   求单应矩阵（h33=1，8x8 高斯消元），分解 `H = [r1 r2 t]` 得相机系下的
   旋转与平移 `T_cw`（Gram-Schmidt 正交化，z 正向约束）
3. **路径规划**：相机系下从镜头前方到二维码中心的三次贝塞尔曲线
   （控制点贴合目标高度，EMA 平滑抑制逐帧抖动）
4. **可视化**：绿色锁定框 + 蓝色路径曲线 + 红色目标点叠加在预览画面，
   顶部状态条实时显示 `dx/dy/dz`

## 依赖（FOSS）

- ZXing core（Apache-2.0）——二维码识别
- CameraX（Apache-2.0）——预览与帧分析
- Jetpack Compose（Apache-2.0）——UI 与叠加层

无 INTERNET 权限、无专有服务（GMS/ML Kit）。

## 与主 README 规划的对照 / 简化

- PnP（EPnP + 迭代优化）→ 简化为单应分解（演示级精度）
- SE(2) 地面规划 / min-jerk → 简化为相机系三次贝塞尔
- 内参标定 → 固定视场角 62° 近似（与 exp3 同约定）
- 角点取自定位图案中心而非码面真实角点，物理尺寸存在系统偏差；
  `Pose.CODE_SIZE_M` 可按实际打印尺寸调整

## 已知限制

- 位姿精度演示级（见上），距离读数相对量有意义、绝对量有偏差
- 码面太小 / 过远 / 大角度斜视时检出率下降
