import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_phoneinfo_method_channel.dart';

abstract class FlutterPhoneinfoPlatform extends PlatformInterface {
  /// Constructs a FlutterPhoneinfoPlatform.
  FlutterPhoneinfoPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterPhoneinfoPlatform _instance = MethodChannelFlutterPhoneinfo();

  /// The default instance of [FlutterPhoneinfoPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterPhoneinfo].
  static FlutterPhoneinfoPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterPhoneinfoPlatform] when
  /// they register themselves.
  static set instance(FlutterPhoneinfoPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

    Future<List<dynamic>> getInstalledApps()   {
      throw UnimplementedError('getInstalledApps() has not been implemented.');
    }
  Future<String?> encrypt(String data) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

}
