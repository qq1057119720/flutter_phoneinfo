package com.flutter.appvailability.plugin.flutter_phoneinfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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
  private static final String KEY_ALGORITHM = "AES";
  private static final String UNICODE_FORMAT = "UTF-8";
  private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
  //    private static final String KEY = "0121170eedf910c65bf10b2cf5820202";
  private static final String KEY = "56db229b8662ddc71cac76f9e07f3d80";
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
    }else  if (call.method.equals("encrypt")) {
//      call.arguments
      result.success(encrypt(call.argument("data").toString()));
    } else  if (call.method.equals("openWx")) {
//      call.arguments
      Intent intent = new Intent();
      ComponentName cmp=new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
      intent.setAction(Intent.ACTION_MAIN);
      intent.addCategory(Intent.CATEGORY_LAUNCHER);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setComponent(cmp);
      activity.startActivity(intent);
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
//      if ((pInfo.applicationInfo.flags & systemAppMask) != 0) {
//        continue;
//      }

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
    map.put("flags",info.applicationInfo.flags);
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
  public void getSystemApp(Context context){
    PackageManager packageManager = context.getPackageManager();
    Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
    mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    List<ResolveInfo> listAllApps = packageManager.queryIntentActivities(mIntent, 0);
//    判断是否系统应用：
//    ResolveInfo appInfo = listAllApps.get(position);
//    String pkgName = appInfo.activityInfo.packageName;//获取包名
//    //根据包名获取PackageInfo mPackageInfo;（需要处理异常）
//    mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
//    if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
//      //第三方应用
//    } else {
//      //系统应用
//    }
  }

  private  Key toKey(byte[] key) {
    return new SecretKeySpec(key, KEY_ALGORITHM);
  }


  public  String encrypt(String data) {
    try {
      Key k = toKey(KEY.getBytes(UNICODE_FORMAT));
      @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, k);
      return bytes2String(cipher.doFinal(data.getBytes(UNICODE_FORMAT)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  public  String bytes2String(byte[] buf) {
    StringBuilder sb = new StringBuilder();
    for (byte b : buf) {
      String hex = Integer.toHexString(b & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      sb.append(hex);
    }
    return sb.toString();
  }


}
