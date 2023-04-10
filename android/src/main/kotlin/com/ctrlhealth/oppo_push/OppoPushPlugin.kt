package com.ctrlhealth.oppo_push

import android.annotation.TargetApi
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.getSystemService
import com.ctrlhealth.oppo_push.emum.OppoPushListenerTypeEnum
import com.ctrlhealth.oppo_push.util.CommonUtil
import com.ctrlhealth.oppo_push.util.ConstantStr
import com.heytap.msp.push.HeytapPushManager
import com.heytap.msp.push.callback.ICallBackResultService
import com.heytap.msp.push.callback.IGetAppNotificationCallBackService
import com.heytap.msp.push.callback.ISetAppNotificationCallBackService
import com.heytap.msp.push.mode.ErrorCode
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import org.json.JSONObject


/** OppoPushPlugin */
class OppoPushPlugin : FlutterPlugin, MethodCallHandler, ICallBackResultService,
    ISetAppNotificationCallBackService, IGetAppNotificationCallBackService {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity


    private val TAG = "| OPPO | Push | Android"

    private var mApplication: Application? = null

    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        mApplication = flutterPluginBinding.applicationContext as Application
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "oppo_push")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "initSDK" -> {
                init(call)
            }
            "register" -> {
                register(call)
            }
            "setAppKeySecret" -> {
                setAppKeySecret(call)
            }
            "unRegister" -> {
                unRegister()
            }
            "getRegisterID" -> {
                getRegisterID(result)
            }
            "setRegisterID" -> {
                setRegisterID(call)
            }
            "setPushCallback" -> {
                setPushCallback()
            }
            "getPushVersionName" -> {
                getPushVersionName(result)
            }
            "getPushVersionCode" -> {
                getPushVersionCode(result)
            }
            "getSDKVersionCode" -> {
                getSDKVersionCode(result)
            }
            "isSupportPush" -> {
                isSupportPush(result)
            }
            "getPushStatus" -> {
                getPushStatus()
            }
            "requestPermission" -> {
                HeytapPushManager.requestNotificationPermission() // 弹出通知栏权限弹窗（仅一次）
            }
            "openNotificationSetting" -> {
                HeytapPushManager.openNotificationSettings() // 打开通知栏设置界面
            }
            "getNotificationStatus" -> {
                HeytapPushManager.getNotificationStatus() // 获取通知栏状态，从callbackresultservice回调结果
            }
            "openAppNotification" -> {
                HeytapPushManager.enableAppNotificationSwitch(this) //打开应用内通知
            }
            "closeAppNotification" -> {
                HeytapPushManager.disableAppNotificationSwitch(this) //关闭应用内通知
            }
            "getAppNotificationSwitch" -> {
                HeytapPushManager.getAppNotificationSwitch(this) //获取应用内通知开关
            }
            else -> {
                result.notImplemented()
            }
        }
    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    /**
     * 初始化MSP服务，创建默认通道
     * @ context必须传入当前app的context
     * @ needLog是否需要设置log
     */
    private fun init(call: MethodCall) {
        var isDebug = call.argument<Boolean>("key")
        HeytapPushManager.init(mApplication, isDebug == false)
        notifyChannel()
    }

    lateinit var key: String
    lateinit var secret: String

    /**
     * 注册MSP推送服务
     * @ applicatoinContext必须传入当前app的applicationcontet
     * @ appKey 在开发者网站上注册时生成的，与AppKey相对应
     * @ appSecret 与AppSecret相对应
     * @ ICallBackResultService SDK操作的回调
     */
    private fun register(call: MethodCall) {
        key = call.argument<String>("key").toString()
        secret = call.argument<String>("secret").toString()
        HeytapPushManager.register(
            mApplication, key, secret, this
        )
    }

    /**
     * 设置appKey等参数,可以覆盖register中的appkey设置
     * @ appKey 在开发者网站上注册时生成的key
     * @ appSecret
     */
    private fun setAppKeySecret(call: MethodCall) {
        HeytapPushManager.setAppKeySecret(
            call.argument("appKey"), call.argument("appSecret")
        )
    }

    //解绑
    private fun unRegister() {
        if (mApplication?.applicationContext == null || key == null || secret == null) {
            unRegister()
            return
        }
        HeytapPushManager.unRegister(
            mApplication!!.applicationContext, key, secret,
            null as JSONObject?, this
        )
    }

    //获取registerId
    private fun getRegisterID(result: Result) {
        val registerID: String? = HeytapPushManager.getRegisterID()
        result.success(registerID ?: "")
        Log.d(TAG, "获取registerId:$registerID")
    }

    //设置registerId
    private fun setRegisterID(call: MethodCall) {
        HeytapPushManager.setRegisterID(call.argument("registerId"))
    }

    //设置sdk操作回调处理
    private fun setPushCallback() {
        HeytapPushManager.setPushCallback(this)
    }

//    //获取PushCall回调
//    private fun getPushCallback() {
//        HeytapPushManager.getPushCallback().
//    }

    //获取MSP推送服务SDK名称
    private fun getPushVersionName(result: Result) {
        val vName: String = HeytapPushManager.getPushVersionName()
        result.success(vName)
        Log.d(TAG, "获取MSP推送服务SDK名称:$vName")
    }

    //获取MSP推送服务MCS版本(例如“2400”)
    private fun getPushVersionCode(result: Result) {
        val vCode: Int = HeytapPushManager.getPushVersionCode()
        result.success(vCode)
        Log.d(TAG, "获取MSP推送服务MCS版本:$vCode")
    }

    //获取MSP推送服务SDK版本（例如”2.1.0”）
    private fun getSDKVersionCode(result: Result) {
        val vCode: Int = HeytapPushManager.getSDKVersionCode()
        result.success(vCode)
        Log.d(TAG, "获取MSP推送服务SDK版本:$vCode")
    }

    //判断是否手机平台是否支持PUSH
    private fun isSupportPush(result: Result) {
        val isSupport: Boolean = HeytapPushManager.isSupportPush(mApplication)
        result.success(isSupport)
        Log.d(TAG, "判断是否手机平台是否支持PUSH:$isSupport")
    }

    //获取MSP推送服务状态
    private fun getPushStatus() {
        HeytapPushManager.getPushStatus()
    }

    //oppo 设置通知通道 兼容Android8.0及以上机型
    //设置通知通道
    private fun notifyChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = ConstantStr.NOTIFICATION_CHANNEL_ID
            val channelName = ConstantStr.NOTIFICATION_CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_HIGH
            createNotificationChannel(channelId, channelName, importance)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        val notificationManager = getSystemService(
            mApplication!!.applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        var channel =
            notificationManager.getNotificationChannel(ConstantStr.NOTIFICATION_CHANNEL_ID)
        if (channel == null) {
            channel = NotificationChannel(channelId, channelName, importance)
            channel.setShowBadge(true)//显示红点提示
            channel.description = ConstantStr.NOTIFICATION_CHANNEL_DESCRIPTION
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setShowBadge(true)
            channel.lightColor = Color.GREEN
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "createNotificationChannel");
        }
    }


    ///-------------------------------------
    override fun onRegister(p0: Int, p1: String?) {
        if (p0 == ErrorCode.SUCCESS) {
            //注册成功
            Log.d(TAG, "注册成功，registerId=$p1")
        } else {
            //注册失败
            Log.d(TAG, "注册失败 第二次 註冊 $p1")
            // 如果第一次注册失败,第二次可以直接调用HeytapPushManager.getRegister()进行重试,此方法默认会使用第一次传入的参数掉调用注册。
            HeytapPushManager.getRegister()
        }
        val jsonObj = JSONObject()
        jsonObj.put("code", p0)
        jsonObj.put("msg", p1)
        invokeListener(OppoPushListenerTypeEnum.RegisterResponse, jsonObj)
    }

    override fun onUnRegister(p0: Int) {
        if (p0 == ErrorCode.SUCCESS) {
            //反注册成功
            Log.d(TAG, "反注册成功")
        } else {
            //注册失败
            Log.d(TAG, "反注册失败")
        }
        val jsonObj = JSONObject()
        jsonObj.put("code", p0)
        invokeListener(OppoPushListenerTypeEnum.UnRegisterResponse, jsonObj)
    }

    override fun onSetPushTime(p0: Int, p1: String?) {
        if (p0 == ErrorCode.SUCCESS) {
            Log.d(TAG, "设置推送时间 success $p1")
        } else {
            Log.d(TAG, "设置推送时间 error $p1")
        }
        val jsonObj = JSONObject()
        jsonObj.put("code", p0)
        jsonObj.put("msg", p1)
        invokeListener(OppoPushListenerTypeEnum.SetPushTimeResult, jsonObj)
    }

    override fun onGetPushStatus(p0: Int, p1: Int) {
        if (p0 == ErrorCode.SUCCESS) {
            Log.d(TAG, "获取当前的push状态返回 success $p1")
        } else {
            Log.d(TAG, "获取当前的push状态返回 error $p1")
        }
        val jsonObj = JSONObject()
        jsonObj.put("code", p0)
        jsonObj.put("code1", p1)
        invokeListener(OppoPushListenerTypeEnum.GetPushStatusResult, jsonObj)
    }

    override fun onGetNotificationStatus(p0: Int, p1: Int) {
        if (p0 == ErrorCode.SUCCESS) {
            Log.d(TAG, "获取当前通知栏状态 success $p1")
        } else {
            Log.d(TAG, "获取当前通知栏状态 error $p1")
        }
        val jsonObj = JSONObject()
        jsonObj.put("code", p0)
        jsonObj.put("code1", p1)
        invokeListener(OppoPushListenerTypeEnum.GetNotificationStatus, jsonObj)
    }

    override fun onError(p0: Int, p1: String?) {
        val jsonObj = JSONObject()
        jsonObj.put("code", p0)
        jsonObj.put("msg", p1)
        invokeListener(OppoPushListenerTypeEnum.OnError, jsonObj)
        Log.d(TAG, "onError | code | $p0 | msg | $p1")
    }

    override fun onSetAppNotificationSwitch(p0: Int) {
        Log.d(TAG, "设置应用内通知开关结果 code : p0")
        val jsonObj = JSONObject()
        jsonObj.put("code", p0)
        invokeListener(OppoPushListenerTypeEnum.OnAppNotification, jsonObj)
    }

    //获取应用内通知开关结果,如果成功返回0，失败返回非0，具体指参考错误码
    //appSwich：0：未定义状态（不校验开关），1：打开状态，2：关闭状态
    override fun onGetAppNotificationSwitch(p0: Int, p1: Int) {
        Log.d(TAG, "获取应用内通知开关结果 code : $p0 state:$p1")
        val jsonObj = JSONObject()
        jsonObj.put("code", p0)
        jsonObj.put("code1", p1)
        invokeListener(OppoPushListenerTypeEnum.GetAppNotificationSwitch, jsonObj)
    }


    private fun invokeListener(type: OppoPushListenerTypeEnum, params: JSONObject?) {
        val p: String = params?.toString() ?: ""
        Log.d(TAG, p)
        CommonUtil.runMainThread {
            channel.invokeMethod(
                "onListener", mapOf(
                    "type" to type.name,
                    "params" to p
                )
            )
        }
    }


}
