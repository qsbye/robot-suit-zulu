# exp8 — Hummingbird Drone（蜂鸟无人机）

昆虫式视觉循迹返航（Insect-inspired Visual Route Following）实验工程。

- 参考：van Dijk et al., Science Robotics 9, eadk0310 (2024)
- 状态：🚧 实现中——已完成小地图界面，后续接入相机与视觉归巢
- 规划细节见项目根目录 README.md「实验八」章节

## 已实现

### 小地图界面（`app/src/main/java/cn/qsbye/hummingbird/minimap/`）

交互参考 [Xaero's Minimap](../../docs/xaeros-minimap-curseforge.md)：

- **瓦片化离屏缓存**（`MinimapTileCache`）：世界系（x 东 / y 北，米，原点=开始建图处）按 1m×1m 分瓦片，轨迹按里程计增量画入 128px 瓦片位图，视图每帧只做平移/缩放采样，不重绘全图
- **北向锁定 + 原点居中**（`MinimapCanvas`）：世界→屏幕 y 翻转，1m 网格对齐瓦片边界，右上角 +/− 缩放（1×/0.5×/0.25×），左下角图例
- **路径点叠加**：去程每 2m 落快照（蓝点），返程最近未捕获快照高亮为归巢目标（黄圈，0.5m 捕获半径），捕获后变绿
- **里程计仿真**（`OdometrySimulator`）：沿预定路线匀速走，固定航向偏置 + 位置偏置随机游走模拟航位推算漂移；落到快照点时清零偏置（对应视觉归巢消除段内漂移）；后续接真实 VIO/光流只需替换此类
- **演示控制**：去程建图 / 返程归巢 / 暂停 / 清除，左上状态条显示状态、里程、快照捕获数
