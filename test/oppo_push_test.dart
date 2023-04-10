import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:oppo_push/oppo_push.dart';

void main() {
  const MethodChannel channel = MethodChannel('oppo_push');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await OppoPush.platformVersion, '42');
  });
}
