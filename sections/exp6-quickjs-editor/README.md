# exp6：QuickJS 端上脚本绘图

> 参考 [摸鱼笔记[11]](https://www.cnblogs.com/qsbye/p/19577773) 的 Cognex 风格绘图指令设计

在 Android 端用 JavaScript 脚本绘制机器视觉标注，实现"改脚本即看效果"。

## 功能

- QuickJS 引擎执行 JavaScript 脚本
- 5 个绘图函数：PlotLine / PlotCircle / PlotString / PlotPolygon / PlotPoint
- Clear / print 辅助函数
- Compose Canvas 实时预览渲染结果
- 脚本编辑器（等宽字体）+ 控制台输出

## 技术栈

| 组件 | 选型 |
| --- | --- |
| JS 引擎 | [taoweiji/quickjs-android](https://github.com/taoweiji/quickjs-android) 1.4.6（Apache-2.0） |
| UI | Jetpack Compose |
| 渲染 | Compose DrawScope |

## 绘图函数签名

```javascript
PlotLine(imgW, imgH, x1, y1, x2, y2, thickness, color)
PlotCircle(imgW, imgH, cx, cy, diameter, thickness, color)
PlotString(imgW, imgH, x, y, text, fontSize, color)
PlotPolygon(imgW, imgH, [x0,y0,x1,y1,...], thickness, color)
PlotPoint(imgW, imgH, x, y, thickness, color)
Clear()
print(msg)
```

- 坐标系：左手坐标系，原点左上角，像素级
- 颜色：整数 `0xFF0000` 或字符串 `"#FF0000"`
- 脚本入口：`function process(message) { ... }`

## 构建

```bash
./gradlew :app:assembleDebug
# 需要 JDK 17
```

## 已知限制

- 无语法高亮
- ShowImage 未实现（无图像底图叠加）
- 消息源为本地空对象（非 MQTT/网络数据源）
