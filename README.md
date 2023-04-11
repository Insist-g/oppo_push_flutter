# oppo_push_plugin

oppo推送SDK Flutter插件  
oppo SDK version 3.1.0

## 使用要求
Flutter Version >= 1.12

详细使用方法参考 example

## 使用
### 接口
|  接口   | 说明           | 参数  | 
|  ----  |--------------| ----  |
| initSDK  | 初始化          | { isDebug: true }
| register  | 注册           | { appKey: **, appSecret: ** }
| unRegister  | 解注册MSP推送服务           | -
| getRegisterID  | 获取registerId         | -
| isSupportPush  | 判断是否手机平台是否支持PUSH       | -
| requestNotificationPermission  | 弹出通知栏权限弹窗（仅一次）       | -
| openNotificationSetting  | 打开通知栏设置界面     | -

### 监听器
添加监听器:`OppoPush.addListener(onOppoPushListener)`，移除监听器:`OppoPush.removeListener(onOppoPushListener)`  
监听器方法原形: `typedef ListenerValue<P> = void Function(OppoPushListenerTypeEnum type, P params);`

仿照 - [FlutterXiaoMiPushPlugin\[\]](https://github.com/JiangJuHong/FlutterXiaoMiPushPlugin) 编写.

菜鸟一枚 水平有限 多多包涵～
