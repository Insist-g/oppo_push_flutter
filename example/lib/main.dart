import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:oppo_push/oppo_push.dart';
import 'package:oppo_push/oppo_push_listener.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  TextEditingController controller = TextEditingController();
  Map<String, Function> methods = {};
  String appSecret = "2cbadf067ef54d69a7dfa799d53d7681";
  String appKey = "8598b6de4fa84028b5b249e276b1fc40";
  String registerId = '';

  @override
  void initState() {
    super.initState();
    // initPlatformState();
    this.methods = {
      'initSDK': () => OppoPush.initSDK(isDebug: true),
      'register': () => OppoPush.register(key: appKey, secret: appSecret),
      'setAppKeySecret': () =>
          OppoPush.setAppKeySecret(appKey: appKey, appSecret: appSecret),
      'unRegister': () => OppoPush.unRegister(),
      'setRegisterID': () => OppoPush.setRegisterID(registerId: registerId),
      'setPushCallback': () => OppoPush.setPushCallback(),
      'getPushVersionName': () async =>
          controller.text = jsonEncode(await OppoPush.getPushVersionName()),
      'getSDKVersionCode': () async =>
          controller.text = jsonEncode(await OppoPush.getSDKVersionCode()),
      'isSupportPush': () async =>
          controller.text = jsonEncode(await OppoPush.isSupportPush()),
      'openNotificationSetting': () => OppoPush.openNotificationSetting(),
      'getPushStatus': () => OppoPush.getPushStatus(),
      'getNotificationStatus': () => OppoPush.getNotificationStatus(),
      'disableAppNotificationSwitch': () => OppoPush.disableAppNotificationSwitch(),
      'enableAppNotificationSwitch': () => OppoPush.enableAppNotificationSwitch(),
      'getAppNotificationSwitch': () => OppoPush.getAppNotificationSwitch(),
      'requestNotificationPermission': () => OppoPush.requestNotificationPermission(),
      'getRegisterID': () async {
        var res = await OppoPush.getRegisterID();
        registerId = res ?? '';
        controller.text = jsonEncode(res);
      },
    };
    OppoPush.addListener(onOppoPushListener);
  }

  @override
  void dispose() {
    super.dispose();
    OppoPush.removeListener(onOppoPushListener);
  }

  // // Platform messages are asynchronous, so we initialize in an async method.
  // Future<void> initPlatformState() async {
  //   String platformVersion;
  //   // Platform messages may fail, so we use a try/catch PlatformException.
  //   // We also handle the message potentially returning null.
  //   try {
  //     platformVersion =
  //         await OppoPush.platformVersion ?? 'Unknown platform version';
  //   } on PlatformException {
  //     platformVersion = 'Failed to get platform version.';
  //   }
  //
  //   // If the widget was removed from the tree while the asynchronous platform
  //   // message was in flight, we want to discard the reply rather than calling
  //   // setState to update our non-existent appearance.
  //   if (!mounted) return;
  //
  //   setState(() {
  //     _platformVersion = platformVersion;
  //   });
  // }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: Column(
          children: [
            TextField(
              controller: controller,
              maxLines: 10,
            ),
            Expanded(
                child: SingleChildScrollView(
                  child: Wrap(
                      runSpacing: 10,
                      spacing: 10,
                      children: methods.keys
                          .map(
                            (key) => OutlinedButton(
                              onPressed: methods[key] as void Function()?,
                              child: Text(key),
                            ),
                          )
                          .toList()),
                ))
          ],
        )),
      ),
    );
  }

  void onOppoPushListener(OppoPushListenerTypeEnum type, params) {
    controller.text = ""
        "============================\n"
        "Listener ${type.toString().split(".")[1]}:\n"
        "------------------------------------------\n"
        "${jsonEncode(params)}\n"
        "============================";
    print(controller.text);
  }


}
