package com.ctrlhealth.oppo_push.util

import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.lang.RuntimeException

/**
 * 工具类
 */
object CommonUtil {

    /**
     * 主线程处理器
     */
    private val MAIN_HANDLER: Handler = Handler(Looper.getMainLooper())

    /**
     * 通用方法，获得参数值，如果未找到参数，则直接中断
     *
     * @param methodCall 方法调用对象
     * @param result     返回对象
     * @param param      参数名
     */
    fun <T> getParam(methodCall: MethodCall, result: MethodChannel.Result, param: String) : T {
        val par: T? = methodCall.argument(param)
        if (par == null) {
            result.error("Missing parameter",
                "Cannot find parameter `$param` or `$param` is null!",
                5)
            throw RuntimeException("Cannot find parameter `$param` or `$param` is null!")
        }
        return par
    }

    /**
     * 运行主线程代码
     * @param runnable 线程对象
     */
    fun runMainThread(runnable: Runnable?) {
        if(runnable != null) {
            MAIN_HANDLER.post(runnable)
        }
    }

    /**
     * 运行主线程返回结果执行
     * @param result 返回结果对象
     * @param param  返回参数
     */
    fun runMainThreadReturn(result: MethodChannel.Result, param: Any?) {
        MAIN_HANDLER.post { result.success(param) }
    }

    /**
     * 运行主线程返回错误结果执行
     * @param result   返回结果对象
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param errorDetails 错误内容
     */
    fun runMainThreadReturnError(
        result: MethodChannel.Result,
        errorCode: String?,
        errorMessage: String?,
        errorDetails: Any?
    ) {
        MAIN_HANDLER.post{ errorCode?.let { result.error(it, errorMessage, errorDetails) } }
    }

}
