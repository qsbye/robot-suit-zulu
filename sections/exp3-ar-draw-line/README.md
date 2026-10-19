# exp3 · AR 画线（FOSS 版 Just a Line）

> 对 Google AR Experiments 应用 **Just a Line**（`com.arexperiments.justaline`）的净室 FOSS 重写。
> 行为参照 [逆向分析报告](../../reference/justaline/20261019-074718-JustaLine_逆向分析报告.md)，
> 未复用任何原版代码与资源。

转动手机瞄准，按住屏幕拖动，即可在空中画出留在真实空间里的 3D 线条——
线条锚定在传感器的世界坐标系中，不会跟着屏幕跑。

## 与原版的对照：专有服务 → FOSS 替代

原版虽以 Apache-2.0 开源，但运行时强绑定多个专有服务（ARCore、Firebase、Nearby、GMS），
在无 GMS 设备上无法使用。本工程全部替换为开源栈：

| 原版（专有） | 本工程（FOSS） | 说明 |
| --- | --- | --- |
| ARCore 6DoF 追踪 + `Frame.hitTest` 触摸落点 | CameraX 预览 + **游戏旋转矢量传感器**（3DoF）+ 针孔投影 | 纯 AOSP 传感器 API，无 GMS 依赖 |
| `c.c` GLStrokeRenderer（OpenGL 三角带） | Compose Canvas 2D 线段 + 透视线宽 | 视觉近似，代码量大幅降低 |
| Firebase Realtime DB 多人协作 | 移除（纯单机） | 后续可用 [bye.rosette.rs](https://github.com/ByeIO/bye.rosette.rs) 话题做多机同步 |
| Google Nearby 设备发现 | 移除 | 同上 |
| Firebase Analytics / FCM / 安装来源统计 | 移除 | **无 INTERNET 权限，零遥测** |
| RecordableSurfaceView 视频录制 | PNG 快照导出（MediaStore → 相册） | 声明式复用同一套投影数学离屏渲染 |
| `b.e` Stroke / `javax.a.h` Vec3 | [Stroke.kt](app/src/main/java/cn/qsbye/robot_suit_zulu/ardrawline/Stroke.kt) / [Projection.kt](app/src/main/java/cn/qsbye/robot_suit_zulu/ardrawline/Projection.kt) | 保留原版 finishStroke 微小笔画处理逻辑 |

依赖仅：Kotlin、AndroidX Compose、CameraX、core-ktx，全部 Apache-2.0。

## 原理（magic-window 3DoF AR）

```
触摸放置：  世界点 p = R · dir(触点) · D        # R: 设备->世界旋转矩阵，D: 画线距离
逐帧渲染：  设备点 q = Rᵀ · p                   # 转置即逆变换
            屏幕  sx = W/2·(1 + qx/(|qz|·tanα))
                  sy = H/2·(1 - qy/(|qz|·tanβ))  # α/β: 相机半视场角
线宽透视：  widthPx = 笔宽(米) · f / |qz|        # f: 像素焦距，近粗远细
```

- **锚定原理**：笔画点存世界系坐标，渲染时用当前姿态重投影，所以转手机时"线留在空气中"。
- **画线距离**（近/中/远 0.7 / 1.5 / 3 m）：无深度估计时的触摸射线落点假设。
- **视场角**：常量 62° 近似（放置与投影共用同一值，几何自洽，仅与真实取景边缘存在轻微错位）。

## 功能

- 8 色色板、细/中/粗三档笔宽、近/中/远三档画线距离
- 撤销最后一笔、清空全部
- 追踪状态指示灯（传感器精度：绿/黄/红/灰，对应原版 TrackingIndicator）
- 保存 PNG 到相册 `Pictures/RobotSuitZulu-AR`
- 无相机权限时降级为深色背景"天空画线"模式，功能不受影响

## 已知限制（相对 ARCore 版）

- **3DoF 追踪**：只追踪转动不追踪平移，平移手机时线条不产生视差（ARCore 版靠 VIO 输出 6DoF）
- 游戏旋转矢量不使用磁力计，长时间会缓慢漂移；重新启动 App 即归零
- FOV 为近似值，边缘笔画与真实取景可能轻微错位
- 单机版，无多人协作（可结合 Rosette 话题扩展）

## 构建

```bash
cd sections/exp3-ar-draw-line
./gradlew :app:assembleDebug        # 产物 app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:installDebug         # 真机安装（minSdk 24）
```
