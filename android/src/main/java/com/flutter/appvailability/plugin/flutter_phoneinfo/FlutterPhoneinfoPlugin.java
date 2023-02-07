package com.flutter.appvailability.plugin.flutter_phoneinfo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterPhoneinfoPlugin */
public class FlutterPhoneinfoPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_phoneinfo");
    channel.setMethodCallHandler(this);
    activity=flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else  if (call.method.equals("getInstalledApps")) {
      result.success(getInstalledApps());
    } else {
      result.notImplemented();
    }
  }
  private List<Map<String, Object>> getInstalledApps() {
    PackageManager packageManager = activity.getPackageManager();
    List<PackageInfo> apps = packageManager.getInstalledPackages(0);
    List<Map<String, Object>> installedApps = new ArrayList<>(apps.size());
    int systemAppMask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;

    for (PackageInfo pInfo : apps) {
      if ((pInfo.applicationInfo.flags & systemAppMask) != 0) {
        continue;
      }

      Map<String, Object> map = this.convertPackageInfoToJson(pInfo);
      installedApps.add(map);
    }

    return installedApps;
  }

  private Map<String, Object> convertPackageInfoToJson(PackageInfo info) {
    Map<String, Object> map = new HashMap<>();
    map.put("app_name", info.applicationInfo.loadLabel(activity.getPackageManager()).toString());
    map.put("package_name", info.packageName);
    map.put("version_code", String.valueOf(info.versionCode));
    map.put("version_name", info.versionName);
    map.put("firstInstallTime",info.firstInstallTime);
    map.put("lastUpdateTime",info.lastUpdateTime);
    map.put("app_type",isSystemApplication(activity,info.packageName));

    return map;
  }
  public static boolean isSystemApplication(Context context, String packageName){
    PackageManager mPackageManager = context.getPackageManager();
    try {
      final PackageInfo packageInfo =
              mPackageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
      if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)!=0){
        return true;
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }



  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
