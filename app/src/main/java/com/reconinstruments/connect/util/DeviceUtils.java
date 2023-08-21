package com.reconinstruments.connect.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by recom3 on 21/08/2023.
 */

public class DeviceUtils {
    public static String getVersionName(Context context) {
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getVersionCode(Context context) {
        try {
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(pinfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            return "-1";
        }
    }

    @SuppressLint({"NewApi"})
    public static String getSerialNumber(Context context) {
        return Build.VERSION.SDK_INT >= 9 ? Build.SERIAL : "0";
    }
}
