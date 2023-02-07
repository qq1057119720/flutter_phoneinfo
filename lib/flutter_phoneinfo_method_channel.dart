import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_phoneinfo_platform_interface.dart';

/// An implementation of [FlutterPhoneinfoPlatform] that uses method channels.
class MethodChannelFlutterPhoneinfo extends FlutterPhoneinfoPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_phoneinfo');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<List> getInstalledApps() async{
    List<dynamic> apps = await methodChannel.invokeMethod("getInstalledApps");
      return apps;
  }
  @override
  Future<String?> encrypt(String data) async {
    final version = await methodChannel.invokeMethod<String>('encrypt',{"data":data});
    return version;
  }

}
