
import 'flutter_phoneinfo_platform_interface.dart';

class FlutterPhoneinfo {
  Future<String?> getPlatformVersion() {
    return FlutterPhoneinfoPlatform.instance.getPlatformVersion();
  }
}
