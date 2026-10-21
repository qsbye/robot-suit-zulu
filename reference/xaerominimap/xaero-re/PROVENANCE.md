# 来源与许可声明（PROVENANCE）

## 本目录内容

本目录是 **Xaero's Minimap 26.6.1**（作者 xaero96）的**反编译衍生源码存档**，由项目维护者从本地参考用 jar 生成，供小地图架构研究（实验八）查阅类结构与算法思路。

| 项 | 说明 |
| --- | --- |
| 原始作品 | Xaero's Minimap（Fabric 版，对应 Minecraft 26.3–26.6.1） |
| 原始 jar | `reference/xaerominimap/xaerominimap-fabric-26.3-26.6.1.jar`（不随 `src/`、`libsrc/` 目录分发，仅本地参考） |
| 原作者 | xaero96 |
| 原许可 | **All Rights Reserved**（jar 内 `LICENSE_xaerohud` 全文仅此两词，`fabric.mod.json` 的 `"license"` 字段相同） |
| 反编译器 | CFR 0.152（`--comments false`，即剥离全部原始注释与行号信息） |
| 生成日期 | 2026-10-21 |
| 目录内容 | `src/`：主 jar 反编译结果（544 个 `.java`，`xaero.*` 包）；`libsrc/`：内嵌 `xaerolib-fabric-26.3-1.8.3.jar` 反编译结果（295 个 `.java`）；两个 `summary.txt` 为 CFR 的方法签名校验告警日志 |

## 许可标注：CC BY-SA 3.0

依仓库维护者要求，本目录的**整理成果**按
[Creative Commons Attribution-ShareAlike 3.0 Unported](https://creativecommons.org/licenses/by-sa/3.0/)
（CC BY-SA 3.0）标注：

- 署名（Attribution）：Xaero's Minimap，作者 xaero96。
- 相同方式共享（ShareAlike）：基于本目录的修改/再分发须以相同许可发布。
- 许可全文：<https://creativecommons.org/licenses/by-sa/3.0/legalcode>

## 重要声明（请务必阅读）

1. **上述 CC BY-SA 3.0 标注是本仓库维护者单方面添加的，未经原版权人 xaero96 授权。**
   原作品明确声明 All Rights Reserved，权利人并未以 CC BY-SA 3.0 或任何其他许可发布该作品，
   因此本仓库**无权**就这些反编译源码授予任何许可；该标注仅表示「本仓库整理归档的部分按此条款共享」的意愿，
   不构成对原作品的再许可，也不产生对第三方的任何授权或豁免。
2. 反编译源码是原作品的**衍生复制**。依著作权法，其复制、改编与再分发应取得权利人许可，
   All Rights Reserved 状态下通常并不存在此类许可。本目录**仅作研究与兼容性分析存档**，不用于构建、分发或商用。
3. 本目录内容**不适用**仓库根目录 [LICENSE](../../../LICENSE)（MIT）。MIT 仅覆盖 `app/`、`desktop/`、`sections/` 等自研代码。
4. 若你是权利人并希望移除本目录，请通过仓库 Issue 联系维护者，我们将立即删除。
5. 使用本目录内容造成的任何法律风险由使用方自行承担。

## 复现方式

```bash
# 主 jar
java -Xmx3g -jar cfr.jar reference/xaerominimap/xaerominimap-fabric-26.3-26.6.1.jar \
    --outputdir <out>/src --comments false

# 内嵌 xaerolib（先解出 jar）
unzip -o reference/xaerominimap/xaerominimap-fabric-26.3-26.6.1.jar \
    'META-INF/jars/*' -d <tmp>
java -Xmx3g -jar cfr.jar <tmp>/META-INF/jars/xaerolib-fabric-26.3-1.8.3.jar \
    --outputdir <out>/libsrc --comments false
```

（CFR 0.152 可直接解析 Java 25 / major 69 字节码；`cfr.jar` 本身未入库。）

## 备份说明

本目录不含反编译器的输出以外的原始资源：`assets/` 贴图、语言文件、`fabric.mod.json`、
原始 `.class` 与 jar 均未收录。仅作为对照阅读的文本源码。
