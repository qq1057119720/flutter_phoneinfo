
import 'flutter_phoneinfo_platform_interface.dart';

class FlutterPhoneinfo {
  Future<String?> getPlatformVersion() {
    return FlutterPhoneinfoPlatform.instance.getPlatformVersion();
  }

  Future<List> getInstalledApps() {
    return FlutterPhoneinfoPlatform.instance.getInstalledApps();
  }
  Future<String?> encrypt(String data)  {
    return FlutterPhoneinfoPlatform.instance.encrypt(data);
  }

}
