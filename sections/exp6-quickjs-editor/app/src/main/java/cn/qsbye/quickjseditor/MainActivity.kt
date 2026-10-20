package cn.qsbye.quickjseditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import cn.qsbye.quickjseditor.ui.EditorScreen

/**
 * 实验六：端上 QuickJS 绘图脚本编辑器。
 * Cognex 风格 Plot* 视觉绘图函数 + Compose Canvas 实时预览。
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                EditorScreen()
            }
        }
    }
}
