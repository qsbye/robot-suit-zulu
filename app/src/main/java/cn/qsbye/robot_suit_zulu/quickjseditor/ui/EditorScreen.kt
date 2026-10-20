package cn.qsbye.robot_suit_zulu.quickjseditor.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.qsbye.robot_suit_zulu.quickjseditor.script.ScriptEngine
import kotlin.math.min

private const val IMG_W = 5120f
private const val IMG_H = 5120f

private val DEFAULT_SCRIPT = """
function process(message) {
    var imgW = 5120, imgH = 5120;
    PlotPolygon(imgW, imgH, [100, 100, 5020, 100, 5020, 5020, 100, 5020], 10, 0x00FFFF);
    PlotLine(imgW, imgH, 100, 100, 5020, 5020, 8, 0xFF0000);
    PlotLine(imgW, imgH, 5020, 100, 100, 5020, 8, 0xFF0000);
    PlotCircle(imgW, imgH, 2560, 2560, 800, 12, 0x00FF00);
    PlotPoint(imgW, imgH, 100, 100, 40, 0xFFFF00);
    PlotPoint(imgW, imgH, 5020, 100, 40, 0xFFFF00);
    PlotPoint(imgW, imgH, 5020, 5020, 40, 0xFFFF00);
    PlotPoint(imgW, imgH, 100, 5020, 40, 0xFFFF00);
    PlotString(imgW, imgH, 200, 200, "QuickJS Plot Test", 80, 0xFFFFFF);
    print("绘图测试完成");
}
""".trimIndent()

/**
 * 实验六：端上 QuickJS 脚本编辑器。
 * 上：等宽字体脚本编辑区；中：绘图预览（5120x5120 视觉坐标系适配）；
 * 下：print 控制台输出与错误展示。
 */
@Composable
fun EditorScreen() {
    val engine = remember { ScriptEngine().apply { init() } }
    DisposableEffect(Unit) {
        onDispose { engine.close() }
    }

    var script by rememberSaveable { mutableStateOf(DEFAULT_SCRIPT) }
    var commandsVersion by remember { mutableIntStateOf(0) }
    var logs by remember { mutableStateOf<List<String>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    val commands = remember(commandsVersion) { engine.getDrawingApi().getCommands() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101418))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1) 脚本编辑区（约 40%）
        TextField(
            value = script,
            onValueChange = { script = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
                .border(1.dp, Color(0xFF2E3A46)),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = Color(0xFFE6EDF3),
                lineHeight = 16.sp
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0D1117),
                unfocusedContainerColor = Color(0xFF0D1117),
                cursorColor = Color(0xFF58A6FF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // 2) 运行按钮行
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                engine.run(script)
                logs = engine.getConsoleLogs()
                error = engine.lastError
                commandsVersion++
            }) {
                Text("▶ 运行脚本")
            }
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Text(
                text = if (error == null) "图形指令：${commands.size} 条" else "执行出错",
                color = if (error == null) Color(0xFF8B949E) else Color(0xFFFF7B72),
                fontSize = 13.sp
            )
        }

        // 3) 绘图预览（约 30%），5120x5120 视觉坐标系等比缩放居中
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
                .background(Color(0xFF000000))
                .border(1.dp, Color(0xFF2E3A46))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val scale = min(size.width / IMG_W, size.height / IMG_H)
                val offsetX = (size.width - IMG_W * scale) / 2f
                val offsetY = (size.height - IMG_H * scale) / 2f
                commands.forEach { it.draw(this, scale, offsetX, offsetY) }
            }
        }

        // 4) 控制台
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
                .background(Color(0xFF0D1117))
                .border(1.dp, Color(0xFF2E3A46))
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = "控制台",
                color = Color(0xFF8B949E),
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(logs) { line ->
                    Text(
                        text = line,
                        color = Color(0xFF7EE787),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                error?.let { err ->
                    item {
                        Text(
                            text = "错误：$err",
                            color = Color(0xFFFF7B72),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                if (logs.isEmpty() && error == null) {
                    item {
                        Text(
                            text = "（暂无输出，点击运行脚本）",
                            color = Color(0xFF484F58),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(0.dp))
    }
}
