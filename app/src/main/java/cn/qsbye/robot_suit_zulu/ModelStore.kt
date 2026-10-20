package cn.qsbye.robot_suit_zulu

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * 模型/检查点持久化目录：
 * 优先使用公共 Documents/RobotSuitZulu/（用户可见、可管理），
 * 无权限时回退到 app 专属外部目录（Android/data/.../files/Documents/RobotSuitZulu/）。
 * 目录自动创建。
 */
object ModelStore {

    fun dir(context: Context, subdir: String): File {
        val base = try {
            // 公共 Documents（API 29+ 需 WRITE_EXTERNAL_STORAGE 或 legacy storage）
            @Suppress("DEPRECATION")
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "RobotSuitZulu/$subdir")
        } catch (_: Exception) {
            null
        }
        val fallback = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "RobotSuitZulu/$subdir")
        val dir = base ?: fallback
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
}
