import 'dart:convert';

import 'package:flutter/services.dart';

class OppoPushListener {
  static Set<ListenerValue> listeners = {};
  final String TAG = "| OPPO | Push |";

  OppoPushListener(MethodChannel channel) {
    channel.setMethodCallHandler((methodCall) async {
      Map<dynamic, dynamic>? arguments = methodCall.arguments;

      switch (methodCall.method) {
        case 'onListener':
          String typeStr = arguments!['type'].toString();
          var params = arguments['params'] != null
              ? jsonDecode(arguments['params'])
              : null;

          OppoPushListenerTypeEnum? type;

          for (var item in OppoPushListenerTypeEnum.values) {
            var es = item.toString().split(".");
            if (es[es.length - 1] == typeStr) {
              type = item;
              break;
            }
          }

          if (type == null) {
            throw MissingPluginException();
          }
          //
          // switch (type) {
          //   case OppoPushListenerTypeEnum.RegisterResponse:
          //     break;
          //   case OppoPushListenerTypeEnum.UnRegisterResponse:
          //     params = OppoPushMessageEntity.fromJson(params);
          //     break;
          //   case OppoPushListenerTypeEnum.SetPushTimeResult:
          //     params = OppoPushMessageEntity.fromJson(params);
          //     break;
          //   case OppoPushListenerTypeEnum.GetNotificationStatus:
          //     params = OppoPushCommandMessageEntity.fromJson(params);
          //     break;
          //   case OppoPushListenerTypeEnum.CommandResult:
          //     params = OppoPushCommandMessageEntity.fromJson(params);
          //     break;
          //   case OppoPushListenerTypeEnum.GetPushStatusResult:
          //     params = OppoPushMessageEntity.fromJson(params);
          //     break;
          //   case OppoPushListenerTypeEnum.OnError:
          //     params = OppoPushMessageEntity.fromJson(params);
          //     break;
          // }
          for (var item in listeners) {
            item(type, params);
          }
          break;
        default:
          throw MissingPluginException();
      }
    });
  }

  void addListener(ListenerValue func) {
    listeners.add(func);
  }

  void removeListener(ListenerValue func) {
    listeners.remove(func);
  }
}

typedef ListenerValue<P> = void Function(
    OppoPushListenerTypeEnum type, P? params);

enum OppoPushListenerTypeEnum {
  RegisterResponse,
  UnRegisterResponse,
  SetPushTimeResult,
  GetPushStatusResult,
  GetNotificationStatus,
  OnError,
  OnAppNotification,
  GetAppNotificationSwitch,
}
