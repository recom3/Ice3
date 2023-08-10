package com.recom3.snow3.mobilesdk.engageweb;

import android.annotation.SuppressLint;
import android.util.Log;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * Created by Recom3 on 24/01/2022.
 */

public class EngageWebClient {
    public static final String TAG = EngageWebClient.class.getSimpleName();

    private static String apiServerUrl = API_SERVER.LIVE.getUrl();

    private IEngageWebClientCallback mResponseHandler;

    public EngageWebClient(IEngageWebClientCallback paramIEngageWebClientCallback) {
        this.mResponseHandler = paramIEngageWebClientCallback;
    }

    public static String getApiServerUrl() {
        return apiServerUrl;
    }

    @SuppressLint({"DefaultLocale"})
    public static void setApiServerUrl(String paramString) throws MalformedURLException {
        if (paramString.toLowerCase().startsWith("http"))
            throw new MalformedURLException("API server URL should not start with HTTP/HTTPS");
        apiServerUrl = paramString;
    }

    boolean handleResult(EngageWebResponse paramEngageWebResponse) {
        if (paramEngageWebResponse != null) {
            String str1 = paramEngageWebResponse.mResponseString;
            String str2 = str1;
            if (str1 == null)
                str2 = "";
            Log.d(TAG, "response body: " + str2);
            if (paramEngageWebResponse.mResponseCode == 200) {
                Log.d(TAG, "response code 200, success for method: " + paramEngageWebResponse.mRequest.getMethod());
                this.mResponseHandler.onConnectionFinished(paramEngageWebResponse);
                return false;
            }
            Log.d(TAG, "FAILED REQUEST, response code: " + paramEngageWebResponse.mResponseCode);
            this.mResponseHandler.onConnectionFinished(paramEngageWebResponse);
        }
        return false;
    }

    public EngageWebClientRequest sendReq(EngageWebClientRequest.HTTP_METHOD paramHTTP_METHOD, String paramString, boolean paramBoolean, Map<String, String> paramMap) {
        Log.d(TAG, paramHTTP_METHOD.name() + " request to " + getApiServerUrl() + paramString);
        return (EngageWebClientRequest)(new EngageWebClientRequest(paramHTTP_METHOD, getApiServerUrl(), paramString, paramBoolean, paramMap, null)).concurrentExecute((EngageWebClient[]) new EngageWebClient[] { this });
    }

    public EngageWebClientRequest sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD paramHTTP_METHOD, String paramString1, String paramString2, Map<String, String> paramMap) {
        Log.d(TAG, paramHTTP_METHOD.name() + " request to " + getApiServerUrl() + paramString1);
        return (EngageWebClientRequest)(new EngageWebClientRequest(paramHTTP_METHOD, getApiServerUrl(), paramString1, true, paramMap, paramString2)).concurrentExecute((EngageWebClient[]) new EngageWebClient[] { this });
    }

    public enum API_SERVER {
        AGM("agm"),
        DEV("dev"),
        //LIVE("api2.reconinstruments.com"),
        //LIVE("192.168.0.116:51192"),
        LIVE("www.recom3.com"),
        STAGING("api-stage.reconinstruments.com");

        private final String url;

        static {
            //AGM = new API_SERVER("AGM", 3, "agm.wp.reconinstruments.com");
            //$VALUES = new API_SERVER[] { LIVE, STAGING, DEV, AGM };
        }

        API_SERVER(String param1String1) {
            this.url = param1String1;
        }

        public String getName() {
            return name();
        }

        public String getUrl() {
            return this.url;
        }
    }
}
