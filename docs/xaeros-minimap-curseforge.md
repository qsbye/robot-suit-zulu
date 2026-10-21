# Xaero's Minimap（Minecraft 小地图 Mod）

> 原文：<https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap>（CurseForge 页面存档，抓取于 2026-10-21）
> 作者：[xaero96](https://www.curseforge.com/members/xaero96) ｜ 许可：All Rights Reserved ｜ 下载量：255.8M ｜ 更新：2026-10-20（26.6.1，Minecraft 26.3，Fabric/Forge/NeoForge/Quilt）
> 本地存档：[`sources/`](sources/) 下同名 HTML；同目录另存 `xaerominimap-fabric-26.3-26.6.1.jar`（Fabric 版主文件，可作逆向参考）

## 简介

Xaero's Minimap 在屏幕角落实时显示附近的地形、玩家、生物与实体，并支持创建路径点（waypoint）标记位置。与多数小地图 Mod 不同，它保持了**原版 Minecraft 的美术风格**，是 Minecraft 的**首个旋转方形小地图**。提供方形/圆形两种外形，可锁定小地图朝向（用方向箭头代替旋转）。实体可显示为**自定义颜色的圆点**或**图标（通常是头颅）**。除默认罗盘方向外，可放置在小地图和游戏世界中都可见的路径点，支持**可选传送**。小地图可显示地上与地下（**洞穴模式**）的方块。Mod 基本为客户端侧，服务端可选安装以获得世界识别等附加功能。分完整版与 fair-play 版（后者不显示实体与洞穴地图，适合 PVP 公平性）。

## 功能特性

### 外观与基础

- 原版 Minecraft 风格外观
- 多档小地图尺寸（含自动）；多档缩放
- 方形 / 圆形两种外形
- 运行比多数小地图 Mod 更流畅
- 可通过「Change Position」设置/界面摆放在屏幕任意位置，甚至逐像素微调，便于与其他 UI Mod 共存
- 两种方块着色模式：**Vanilla**（原版地图配色）与 **Accurate**（按方块纹理与群系取色）；Vanilla 模式可单独开启群系着色
- 快捷键（默认 Z）临时放大小地图查看周围；可配置放大时居中或完全缩小；**所有键位都在原版控制菜单中**
- 地形阴影定制：「Terrain Depth」「Terrain Slopes」
- 与 [Xaero's World Map](https://www.curseforge.com/minecraft/mc-mods/xaeros-world-map) 兼容——默认直接使用世界地图 Mod 生成的纹理，二者同装时提升性能

### 服务端管控

- 服务端配置 + 游戏内 UI，实时控制玩家使用 Mod 的方式，可按 rank/权限区分（`xaero.minimap.enforced_server_profile`），支持 FTB Ranks、Odyssey Roles、LuckPerms 与通用权限 API
- 可将小地图绑定到物品（需在快捷栏或已装备），如配置 `minimapItemId:minecraft:compass` 绑定罗盘
- 药水效果控制：`xaerominimap:no_minimap`、`no_entity_radar`、`no_waypoints`、`no_cave_maps`，默认为中性类型，可追加 `_harmful` 变为有害类型
- 多语言翻译

### 地图模式

- **锁定北方**：小地图不旋转，用箭头显示角色朝向
- **自动洞穴模式**：地上地下都可用，在建筑内也应触发；可配置判定"实心屋顶"的尺寸（3×3 以上可避免砍树时误触发）
- 可切换**区块网格**显示
- 可切换**史莱姆区块**模式（绿色覆盖层标出史莱姆可生成区块）
- **自定义光照覆盖层**：按自定义光照范围（默认方块光照 0–7，即怪物可生成方块）以自定义颜色标记，可改作他用

### 路径点（Waypoints）

- 数量近乎无限；可传送（需聊天命令权限）；在游戏世界中渲染、在小地图上显示；可全部或单独隐藏
- 按 **B** 新建路径点，按 **U** 打开路径点列表（增删改、批量编辑、排序、跨子世界/维度查看等）
- **本地与全局路径点**：本地路径点受「Max WP Draw Distance」限制，可为一大片区域设一个全局路径点 + 区域内多个本地路径点，超出最大绘制距离时仅全局可见
- **路径点集（sets）**：分组管理，快捷键快速切换当前集合
- **路径点分享**：从列表（U）将路径点发到游戏聊天中
- **死亡点**：每次死亡自动生成骷髅头形状路径点；旧死亡点转为普通路径点（可用「Keep Old Deathpoints」关闭）
- 显示 [Open Parties and Claims](https://www.curseforge.com/minecraft/mc-mods/open-parties-and-claims) 的区块圈地与盟友玩家
- 多人多世界检测：服务端同装 Mod 时每个世界有独立路径点"子世界"；否则退化为基于世界出生点的不可靠区分；单世界服务器建议关掉多世界检测（路径点菜单 U → Options）

### 实体雷达

- **生物**显示为黄点，敌对/友好可分色；可显示为图标/头颅（「Entity Radar」设置）
- **玩家**显示为白点或玩家头颅
- **物品**显示为红点
- **其他实体**（箭、物品展示框等）显示为紫点；各类型可在设置中单独禁用
- 可在小地图上显示**实体名称**

### 信息与设置

- 缩放快捷键（默认 I / O）
- 小地图下方可显示：**坐标**、**当前群系名**（默认关，按 Y 开设置）、**光照等级**（默认关）、**当前游戏时间**（12/24 小时制）、**当前相机角度**
- 一键隐藏所有 UI 中的路径点坐标（适合直播/录屏）
- 隐藏下拉菜单中的服务器地址（默认 "Partial"）
- 按 **Y** 打开设置，选项极多
- 两个版本：完整版无限制；[fair-play 版](https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap-fair-play-edition)不显示实体与洞穴地图，适合对无小地图玩家的公平 PVP

相关链接：[论坛帖](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/2379024-xaeros-minimap-1-16-1-forge-fabric-1-15-2-1-14-4-1) ｜ [Xaero's World Map](https://www.curseforge.com/minecraft/mc-mods/xaeros-world-map)

## FAQ

### 实体雷达图标已开启但显示异常（原版生物只显示为圆点+名字）？

使用了修改原版生物模型/动画的 Mod + 资源包组合，破坏了小地图基于模型的图标渲染兼容性。移除对应资源包，或安装包含全部原版生物雷达图标的资源包，如 [Xaero Minimap X Fresh Animations](https://www.curseforge.com/minecraft/texture-packs/icons-xaero-minimap-x-fresh-animations)。

### 路径点传送无效？

Mod 默认发送聊天命令 `/tp @s x y z` 传送，需要相应权限。原版 `/tp` 需要 OP（开启作弊）；非原版服务器装插件后可能有独立权限。可在小地图设置「Default Teleport Command」或路径点菜单「Teleport Chat Command」（按服务器）修改命令；非原版命令常不支持 `@s` 选择器，需删掉。世界地图右键传送有独立设置。

「Teleport」按钮被禁用的常见原因：手动使用了非 "auto" 子世界（同维度）的路径点。在路径点菜单 Options 中把正确子世界设为「Make Sub-world Auto」即可（会交换两个子世界的数据）。若 "auto" 已选中仍禁用，说明该服务器/世界手动关掉了传送，需编辑游戏目录 `xaero/minimap`（旧版 XaeroWaypoints）下的服务器专属配置文件（改配置前退出游戏并备份）。

### 某个服务器的路径点不见了？

别慌，几乎肯定没有真的删除。若同装世界地图 Mod 也"重置"了，先按世界地图说明页恢复。然后按 U 打开路径点菜单，在**右上角**下拉中遍历该服务器地址的所有子世界；若都不对，多半是服务器地址变了——在**左上角**下拉找到旧地址，选中后 *Options → Make World/Server Auto*。

*使用世界地图 Mod 时*：错误子世界被自动选中，最常见原因是多人模式下（1.6 起）选错了地图。在世界地图界面左下角图标里选对当前世界的地图；已确认的地图决定小地图 Mod 中哪个路径点子世界为 (auto)。

*未使用世界地图 Mod 时*：可能是服务器插件改变了原版指南针行为（指向床而非出生点），干扰了 multiworld 检测（默认关闭）。重新在正确的床上睡一觉即可恢复；若因新插件引起且不能移除：① 在右上角下拉找到正确子世界；② *Options → Make sub-world auto*；③ 每个维度都做一次（需先进入该维度）；④ 单世界简单服务器同时关掉 Options 中的「Multiworld Detection」。

### 如何添加/覆盖自定义实体图标？

先确认已在设置 → Entity Radar Categories 开启图标显示（"Always" 或 "Player List"）。

21.6.0 起可用资源包覆盖默认图标，也可用自制 Mod 增强实体变体支持（同类型不同变体不同图标）。默认情况下 Mod 用实体渲染器/模型渲染图标，支持原版生物和模型部件（头、耳、鼻等）易辨识的简单两足/四足生物，失败则回退彩色圆点。相关资源目录为 jar 内 `/assets/xaerominimap/entity/icon/`，内含示例 `definition/example_mod/example_entity.json`（对应实体类型 `example_mod:example_entity`）；`definition/minecraft/` 下有若干原版实体示例。配置中把 `debugEntityVariantIds` 设为 true 可在聊天/日志打印新实体的变体 ID。21.8.0 起还可自定义基于模型的渲染，详见示例文件注释。可参考 [babybluetit 的 modded support 资源包](https://www.curseforge.com/minecraft/texture-packs/xaeros-minimap-modded-support)。

### 如何在我的服务器禁用洞穴模式/实体雷达？

在服务端安装本 Mod（需 NeoForge/Forge/Fabric/Quilt 服务端）。

- **25.3.0 及以上**：用「Minimap Server Settings」键位打开服务端设置，可强制几乎任意选项（含洞穴模式、实体雷达、自定义雷达类别）。
- **旧版**：启动服务端后在 `config` 目录（非存档目录）生成 "common" 配置文件，可禁用指定功能，改后重启。

无法安装 Mod 时，可在玩家登录时发送含以下字符串的系统消息：

| 字符串 | 效果 |
| --- | --- |
| `§f§a§i§r§x§a§e§r§o` | 客户端禁用洞穴模式（含世界地图）与实体雷达（fair-play 模式） |
| `§x§a§e§r§o§m§m§n§e§t§h§e§r§i§s§f§a§i§r` | fair-play 下仍允许下界洞穴模式（世界地图另有近似字符串） |
| `§n§o§m§i§n§i§m§a§p` | 完全禁用小地图 |
| `§r§e§s§e§t§x§a§e§r§o` | 重置所有系统消息标记 |

第三方简化工具（非官方）：Spigot 插件 [XMMForceFairPlay](https://www.spigotmc.org/resources/xmmforcefairplay.129678/)、数据包 [Xaero's Map Server Utils](https://modrinth.com/datapack/xaeros-map-server-utils)。使用第三方附加组件风险自负。

### 如何让原版客户端玩家能进入装有本 Mod 的服务器？

编辑服务端 `config/xaero/minimap` 的 "common" 配置，关闭状态效果注册即可（状态效果注册会阻止原版玩家进入），改后重启；世界地图 Mod 同理。

## 开发者接入

构建脚本中添加仓库：

```groovy
repositories {
    maven {
        url "https://chocolateminecraft.com/maven"
        name "Xaero's Maven"
    }
}
```

Forge 项目需应用 MixinGradle 插件（org.spongepowered.mixin，1.12.2 除外）。依赖声明（版本号见 <https://chocolateminecraft.com/maven/xaero/minimap>）：

```groovy
// Forge + 官方 Mojang mappings（1.21.1 之前，故意不去混淆）
implementation "xaero.minimap:xaerominimap-forge-<minecraft version>:<mod version>:dev"

// Forge + 官方 Mojang mappings（1.21.1 及之后）
implementation "xaero.minimap:xaerominimap-forge-<minecraft version>:<mod version>"

// Forge + 其他 mappings
implementation fg.deobf("xaero.lib:xaerolib-forge-<minecraft version>:+")
implementation fg.deobf("xaero.minimap:xaerominimap-forge-<minecraft version>:<mod version>")

// Fabric
modImplementation "xaero.minimap:xaerominimap-fabric-<minecraft version>:<mod version>"

// NeoForge
implementation "xaero.minimap:xaerominimap-neoforge-<minecraft version>:<mod version>"

// 多加载器工程中的 common 子项目
compileOnly "xaero.minimap:xaerominimap-common-<minecraft version>:<mod version>"
```

## 整合包与其他

允许用本 Mod 制作视频。整合包使用条件：

- 仅允许通过 CurseForge 或 Modrinth 对整合包盈利（含描述中的赞助链接/横幅）；其他渠道盈利需作者书面许可（可私信，通常会给）。
- 在 CurseForge/Modrinth 之外分发的整合包，须以易访问的官方页面链接署名。
- 整合包名称/描述不得与作者 Mod 易混淆。
- 若已获准在外部盈利而作者要求移除，则须移除。

---

## 与本项目的关系（实验八小地图参考）

Xaero's Minimap 是「以出生点为中心、随玩家移动动态增量渲染的角落小地图」的成熟范本，对应本仓库实验八（蜂鸟无人机）规划中的**循迹小地图**需求：

- **地图原点锚定**：Xaero 以区块（16×16）为最小增量单元、以世界出生点为坐标原点组织瓦片；实验八小地图以"开始建图处"为中心，同理把里程计矢量链的世界系原点固定在建图起点，避免漂移污染绝对坐标。
- **增量瓦片化**：只重绘进入视野的新区块、按缩放级别缓存纹理——手机上可用 Compose `Canvas` + 离屏位图缓存实现同等策略，避免每帧全量重绘路线。
- **锁定北方 vs 头朝向旋转**：Xaero 提供「锁定北方 + 方向箭头」与「头朝向旋转」两种模式，实验八小地图可同样提供两档（默认锁北便于对照里程计坐标）。
- **路径点子世界/集合**：多路线管理可参考其 sub-world/set 概念，每条去程路线一个 set。
- **洞穴模式自动切换**：按"头顶实心屋顶"启发式切换地上/地下渲染——对应无人机场景中室内/室外感知模式切换的思路（室内关 GPS 类绝对参照，纯视觉/里程计）。

许可提示：该 Mod 为 **All Rights Reserved**，仅作交互与架构参考，不可复制其代码/资源；本仓库小地图为净室实现。
