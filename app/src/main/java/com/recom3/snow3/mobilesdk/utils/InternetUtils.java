package com.recom3.snow3.mobilesdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Recom3 on 24/01/2022.
 */

public class InternetUtils {
    private static final String STRING_TO_SIGN = "q5E2kWHuWLjCnjbX";

    private static final String TAG = "InternetUtils";

    public static String SHA1(String paramString) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(paramString.getBytes("iso-8859-1"), 0, paramString.length());
        return convertToHex(messageDigest.digest());
    }

    private static String convertToHex(byte[] paramArrayOfbyte) {
        StringBuilder stringBuilder = new StringBuilder();
        byte b = 0;
        label18: while (true) {
            if (b < paramArrayOfbyte.length) {
                int i = paramArrayOfbyte[b] >>> 4 & 0xF;
                for (byte b1 = 0;; b1++) {
                    if (i >= 0 && i <= 9) {
                        stringBuilder.append((char)(i + 48));
                    } else {
                        stringBuilder.append((char)(i - 10 + 97));
                    }
                    i = paramArrayOfbyte[b] & 0xF;
                    if (b1 >= 1) {
                        b++;
                        continue label18;
                    }
                }
                //break;
            }
            return stringBuilder.toString();
        }
    }

    public static boolean isInternetConnected(Context paramContext) {
        NetworkInfo networkInfo = ((ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE/*"connectivity"*/)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Log.d("InternetUtils", networkInfo.toString());
            return true;
        }
        Log.d("InternetUtils", "Internet Not Connected");
        return false;
    }
}

