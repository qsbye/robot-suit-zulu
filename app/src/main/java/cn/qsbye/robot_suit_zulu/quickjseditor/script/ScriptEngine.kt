package cn.qsbye.robot_suit_zulu.quickjseditor.script

import com.quickjs.JSArray
import com.quickjs.JSContext
import com.quickjs.JSObject
import com.quickjs.JSValue
import com.quickjs.JavaVoidCallback
import com.quickjs.QuickJS

/**
 * QuickJS 脚本引擎封装：注册 Plot 系列 / Clear / print 全局函数，
 * 执行用户脚本后调用全局 process(message) 入口。
 *
 * 注意（taoweiji quickjs-android 1.4.6 实测 API）：
 * - 包名为 com.quickjs；JSContext 继承 JSObject 即全局对象，无 getGlobalObject()
 * - JSArray.getDouble(int) 遇到整数字面量会返回 0.0，数值参数必须经 argDouble/argInt 安全转换
 * - 取嵌套数组用 getArray(int)，调用全局函数用 executeVoidFunction(name, JSArray)
 */
class ScriptEngine {

    private var quickJS: QuickJS? = null
    private var context: JSContext? = null
    private val drawingApi = DrawingApi()
    private val consoleLogs = mutableListOf<String>()

    var lastError: String? = null
        private set

    fun getDrawingApi() = drawingApi

    fun getConsoleLogs(): List<String> = consoleLogs.toList()

    fun init() {
        close()
        quickJS = QuickJS.createRuntime()
        context = quickJS!!.createContext()
        registerFunctions()
    }

    private fun registerFunctions() {
        val global = context!!

        global.registerJavaMethod(JavaVoidCallback { _, args ->
            drawingApi.plotLine(
                argInt(args, 0), argInt(args, 1),
                argDouble(args, 2), argDouble(args, 3),
                argDouble(args, 4), argDouble(args, 5),
                argDouble(args, 6), args.get(7)
            )
        }, "PlotLine")

        global.registerJavaMethod(JavaVoidCallback { _, args ->
            drawingApi.plotCircle(
                argInt(args, 0), argInt(args, 1),
                argDouble(args, 2), argDouble(args, 3),
                argDouble(args, 4), argDouble(args, 5),
                args.get(6)
            )
        }, "PlotCircle")

        global.registerJavaMethod(JavaVoidCallback { _, args ->
            drawingApi.plotString(
                argInt(args, 0), argInt(args, 1),
                argDouble(args, 2), argDouble(args, 3),
                args.getString(4), argInt(args, 5),
                args.get(6)
            )
        }, "PlotString")

        global.registerJavaMethod(JavaVoidCallback { _, args ->
            val ptsArr = args.getArray(2)
            val n = ptsArr.length()
            val points = DoubleArray(n)
            for (i in 0 until n) points[i] = argDouble(ptsArr, i)
            drawingApi.plotPolygon(
                argInt(args, 0), argInt(args, 1),
                points, argDouble(args, 3), args.get(4)
            )
        }, "PlotPolygon")

        global.registerJavaMethod(JavaVoidCallback { _, args ->
            drawingApi.plotPoint(
                argInt(args, 0), argInt(args, 1),
                argDouble(args, 2), argDouble(args, 3),
                argDouble(args, 4), args.get(5)
            )
        }, "PlotPoint")

        global.registerJavaMethod(JavaVoidCallback { _, _ ->
            drawingApi.clear()
        }, "Clear")

        global.registerJavaMethod(JavaVoidCallback { _, args ->
            val text = if (args.length() > 0) {
                when (val v = args.get(0)) {
                    null -> "null"
                    is JSArray -> v.toJSONArray().toString()
                    is JSObject -> v.toJSONObject().toString()
                    else -> v.toString()
                }
            } else ""
            consoleLogs.add(text)
        }, "print")
    }

    fun run(script: String) {
        lastError = null
        consoleLogs.clear()
        drawingApi.clear()
        val ctx = context
        if (ctx == null) {
            lastError = "引擎未初始化"
            return
        }
        try {
            ctx.executeVoidScript(script, "user.js")
            if (ctx.getType("process") != JSValue.TYPE.JS_FUNCTION) {
                lastError = "未找到 process(message) 函数"
                return
            }
            val msg = JSObject(ctx)
            val args = JSArray(ctx)
            args.push(msg)
            ctx.executeVoidFunction("process", args)
        } catch (e: Exception) {
            lastError = e.message ?: e.toString()
        }
    }

    fun close() {
        try {
            context?.close()
        } catch (_: Exception) {
        }
        try {
            quickJS?.close()
        } catch (_: Exception) {
        }
        context = null
        quickJS = null
    }

    companion object {
        /** QuickJS 整数字面量会以 Integer 返回，getDouble 得到 0.0，故统一安全转换。 */
        private fun argDouble(args: JSArray, i: Int): Double = when (val v = args.get(i)) {
            is Int -> v.toDouble()
            is Double -> v
            is Long -> v.toDouble()
            is Float -> v.toDouble()
            is Boolean -> if (v) 1.0 else 0.0
            is String -> v.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }

        private fun argInt(args: JSArray, i: Int): Int = when (val v = args.get(i)) {
            is Int -> v
            is Double -> v.toInt()
            is Long -> v.toInt()
            is Float -> v.toInt()
            is String -> v.toIntOrNull() ?: 0
            else -> 0
        }
    }
}
