package com.recom3.snow3.mobilesdk.hudconnectivity;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class BTProperty {
    private static final String TAG = "BTProperty";

    public static String getLastPairedDeviceAddress(Context paramContext) {
        Log.d("BTProperty", "LastPairedDeviceAddress from Settings.System");
        String str2 = Settings.System.getString(paramContext.getContentResolver(), "LastPairedDeviceAddress");
        String str1 = str2;
        if (str2 == null)
            str1 = "";
        return str1;
    }

    public static String getLastPairedDeviceName(Context paramContext) {
        Log.d("BTProperty", "LastPairedDeviceName from Settings.System");
        String str2 = Settings.System.getString(paramContext.getContentResolver(), "LastPairedDeviceName");
        String str1 = str2;
        if (str2 == null)
            str1 = "";
        return str1;
    }

    public static int getLastPairedDeviceType(Context paramContext) {
        Log.d("BTProperty", "LastPairedDeviceType from Settings.System");
        int i = 0;
        try {
            int j = Settings.System.getInt(paramContext.getContentResolver(), "LastPairedDeviceType");
            i = j;
        } catch (android.provider.Settings.SettingNotFoundException settingNotFoundException) {
            settingNotFoundException.printStackTrace();
        }
        return i;
    }

    public static boolean isReconnect(Context paramContext) {
        boolean bool = true;
        Log.d("BTProperty", "Reconnect from Settings.System");
        int i = 0;
        try {
            int j = Settings.System.getInt(paramContext.getContentResolver(), "Reconnect");
            i = j;
        } catch (android.provider.Settings.SettingNotFoundException settingNotFoundException) {
            settingNotFoundException.printStackTrace();
        }
        if (i != 1)
            bool = false;
        return bool;
    }

    public static void setBTConnectedDeviceAddress(Context paramContext, String paramString) {
        if (Build.PRODUCT.contains("jet") || Build.PRODUCT.contains("limo")) {
            Log.d("BTProperty", "Settings.System: set deviceAddress to " + paramString);
            Settings.System.putString(paramContext.getContentResolver(), "BTConnectedDeviceAddress", paramString);
        }
    }

    public static void setBTConnectedDeviceName(Context paramContext, String paramString) {
        if (Build.PRODUCT.contains("jet") || Build.PRODUCT.contains("limo")) {
            Log.d("BTProperty", "Settings.System: set deviceName to " + paramString);
            Settings.System.putString(paramContext.getContentResolver(), "BTConnectedDeviceName", paramString);
        }
    }

    public static void setBTConnectedDeviceType(Context paramContext, int paramInt) {
        if (Build.PRODUCT.contains("jet") || Build.PRODUCT.contains("limo")) {
            Log.d("BTProperty", "Settings.System: set deviceType to " + paramInt);
            Settings.System.putInt(paramContext.getContentResolver(), "BTConnectedDeviceType", paramInt);
        }
    }

    public static void setBTConnectionState(Context paramContext, int paramInt) {
        if (Build.PRODUCT.contains("jet") || Build.PRODUCT.contains("limo")) {
            Log.d("BTProperty", "Settings.System: set connectionState to " + paramInt);
            Settings.System.putInt(paramContext.getContentResolver(), "BTConnectionState", paramInt);
        }
    }

    public static void setLastPairedDeviceAddress(Context paramContext, String paramString) {
        if (Build.PRODUCT.contains("jet") || Build.PRODUCT.contains("limo")) {
            Log.d("BTProperty", "Settings.System: set LastPairedDeviceAddress to " + paramString);
            Settings.System.putString(paramContext.getContentResolver(), "LastPairedDeviceAddress", paramString);
        }
    }

    public static void setLastPairedDeviceName(Context paramContext, String paramString) {
        if (Build.PRODUCT.contains("jet") || Build.PRODUCT.contains("limo")) {
            Log.d("BTProperty", "Settings.System: set LastPairedDeviceName to " + paramString);
            Settings.System.putString(paramContext.getContentResolver(), "LastPairedDeviceName", paramString);
        }
    }

    public static void setLastPairedDeviceType(Context paramContext, int paramInt) {
        if (Build.PRODUCT.contains("jet") || Build.PRODUCT.contains("limo")) {
            Log.d("BTProperty", "Settings.System: set LastPairedDeviceType to " + paramInt);
            Settings.System.putInt(paramContext.getContentResolver(), "LastPairedDeviceType", paramInt);
        }
    }

    public static void setReconnect(Context paramContext, boolean paramBoolean) {
        if (Build.PRODUCT.contains("jet") || Build.PRODUCT.contains("limo")) {
            boolean bool = false;
            if (paramBoolean)
                bool = true;
            Log.d("BTProperty", "Settings.System: set Reconnect to " + bool);
            Settings.System.putInt(paramContext.getContentResolver(), "Reconnect", bool ? 1 : 0);
        }
    }
}
