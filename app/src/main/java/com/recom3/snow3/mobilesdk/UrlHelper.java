package com.recom3.snow3.mobilesdk;

/**
 * Created by Recom3 on 07/03/2022.
 */

public class UrlHelper {
    public static String getCode(String paramString) {
        String str = "";
        int i = paramString.indexOf("&");
        if (i != -1)
            str = paramString.substring(0, i);
        i = str.indexOf("code=");
        paramString = str;
        if (i != -1)
            paramString = str.substring("code=".length() + i);
        return paramString;
    }
}
