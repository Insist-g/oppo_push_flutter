import 'dart:async';
import 'package:flutter/services.dart';
import 'package:oppo_push/oppo_push_listener.dart';

class OppoPush {
  static const MethodChannel _channel = MethodChannel('oppo_push');

  static OppoPushListener? listener;

  static void addListener(ListenerValue func) {
    listener ??= OppoPushListener(_channel);
    listener!.addListener(func);
  }

  static void removeListener(ListenerValue func) {
    listener ??= OppoPushListener(_channel);
    listener!.removeListener(func);
  }

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  ///初始化MSP服务，创建默认通道
  ///@param needLog是否需要设置log
  static Future<void> initSDK({bool isDebug = true}) async {
    return await _channel.invokeMethod('initSDK', {'isDebug': isDebug});
  }

  /// 注册MSP推送服务
  /// @param appKey 在开发者网站上注册时生成的，与AppKey相对应
  /// @param appSecret 与AppSecret相对应
  static Future<void> register(
      {required String key, required String secret}) async {
    return await _channel
        .invokeMethod('register', {'key': key, 'secret': secret});
  }

  /// 设置appKey等参数,可以覆盖register中的appkey设置
  /// @param appKey 在开发者网站上注册时生成的key
  /// @param appSecret
  static Future<void> setAppKeySecret(
      {required String appKey, required String appSecret}) async {
    return await _channel.invokeMethod(
        'setAppKeySecret', {'appKey': appKey, 'appSecret': appSecret});
  }

  /// 解注册MSP推送服务
  static Future<void> unRegister() async {
    return await _channel.invokeMethod('unRegister');
  }

  ///获取registerId
  static Future<String?> getRegisterID() async {
    return await _channel.invokeMethod('getRegisterID');
  }

  ///设置registerId
  static Future<String?> setRegisterID({required String registerId}) async {
    if (registerId.isEmpty) return "";
    return await _channel
        .invokeMethod('setRegisterID', {'registerId': registerId});
  }

  ///设置sdk操作回调处理
  static void setPushCallback() {
    _channel.invokeMethod('setPushCallback');
  }

  ///获取MSP推送服务SDK版本（例如”2.1.0”）
  static Future<int?> getSDKVersionCode() async {
    return await _channel.invokeMethod('getSDKVersionCode');
  }

  ///获取MSP推送服务MCS版本(例如“2.4.0”)
  static Future<String?> getPushVersionName() async {
    return await _channel.invokeMethod('getPushVersionName');
  }

  ///获取MSP推送服务MCS版本(例如“2400”)
  static Future<int?> getPushVersionCode() async {
    return await _channel.invokeMethod('getPushVersionCode');
  }

  ///判断是否手机平台是否支持PUSH
  static Future<bool> isSupportPush() async {
    return await _channel.invokeMethod('isSupportPush');
  }

  ///获取MSP推送服务状态
  static void getPushStatus() {
    _channel.invokeMethod('getPushStatus');
  }

  ///弹出通知栏权限弹窗（仅一次）
  static Future<void> requestNotificationPermission() async {
    return await _channel.invokeMethod('requestPermission');
  }

  ///打开通知栏设置界面
  static Future<void> openNotificationSetting() async {
    return await _channel.invokeMethod('openNotificationSetting');
  }

  ///获取通知栏状态，从callbackresultservice回调结果
  static Future<void> getNotificationStatus() async {
    return await _channel.invokeMethod('getNotificationStatus');
  }

  ///打开应用内通知
  static Future<void> enableAppNotificationSwitch() async {
    return await _channel.invokeMethod('openAppNotification');
  }

  ///关闭应用内通知
  static Future<void> disableAppNotificationSwitch() async {
    return await _channel.invokeMethod('closeAppNotification');
  }

  ///获取应用内通知开关
  static Future<void> getAppNotificationSwitch() async {
    return await _channel.invokeMethod('getAppNotificationSwitch');
  }
}
