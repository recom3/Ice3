package com.recom3.snow3.mobilesdk.engageweb;

import android.content.Context;
import android.util.Log;

import com.recom3.snow3.mobilesdk.EngageSdkService;
import com.recom3.snow3.mobilesdk.utils.InternetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Recom3 on 24/01/2022.
 */

public class AuthenticationService extends EngageSdkService {

    private static final String TAG = AuthenticationService.class.getSimpleName();

    private static final String URL_CHANGE_PASSWORD = "/me/change_password.json";

    private static final String URL_EDIT_USER_INFO = "/me.json";

    private static final String URL_LOGIN = "/login.json";

    private static final String URL_LOGIN_ACCESS_TOKEN = "/me.json";

    private static final String URL_LOGIN_FACEBOOK = "/facebook_connect.json";

    private static final String URL_LOGOUT = "/logout.json";

    private static final String URL_OAUTH_ACCESS_TOKEN = "/oauth2/access_token";

    private static final String URL_REGISTER = "/register.json";

    private UserInfo mUserInfo;

    private IAuthenticationManagerCallback nopCallback = new IAuthenticationManagerCallback() {
        public void onAccountCreated(AuthResponse param1AuthResponse) {}

        public void onAuthConnectionError(AuthResponse param1AuthResponse) {}

        public void onChangedPassword(AuthResponse param1AuthResponse) {}

        public void onFacebookLogin(AuthResponse param1AuthResponse) {}

        public void onLogin(AuthResponse param1AuthResponse) {}

        public void onLoginThirdParty(String param1String) {}

        public void onUserPrefChanged(AuthResponse param1AuthResponse) {}
    };

    public static String getOauth2Url(String paramString1, String paramString2, String paramString3) throws UnsupportedEncodingException {
        return getOauth2Url(paramString1, paramString2, paramString3, "users", "code");
    }

    private static String getOauth2Url(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws UnsupportedEncodingException {
        String str = String.format("http://%s/api/oauth2?response_type=%s&client_id=%s&client_secret=%s&scope=%s&redirect_uri=%s", new Object[] { EngageWebClient.getApiServerUrl(), URLEncoder.encode(paramString5, "UTF-8"), URLEncoder.encode(paramString1, "UTF-8"), URLEncoder.encode(paramString2, "UTF-8"), URLEncoder.encode(paramString4, "UTF-8"), URLEncoder.encode(paramString3, "UTF-8") });
        return str;
        //!recom3: was before with https
        //return String.format("https://%s/oauth2/?response_type=%s&client_id=%s&client_secret=%s&scope=%s&redirect_uri=%s", new Object[] { EngageWebClient.getApiServerUrl(), URLEncoder.encode(paramString5, "UTF-8"), URLEncoder.encode(paramString1, "UTF-8"), URLEncoder.encode(paramString2, "UTF-8"), URLEncoder.encode(paramString4, "UTF-8"), URLEncoder.encode(paramString3, "UTF-8") });
    }

    private AuthResponse handleConnectionError(EngageWebResponse paramEngageWebResponse) {
        switch (paramEngageWebResponse.mResponseCode) {
            default:
                return null;
            case 408:
                Log.w(TAG, "HTTP 408, timeout connection error");
                return new AuthResponse(false, false, "Connection timeout, try again later.", paramEngageWebResponse.mResponseCode);
            case 503:
                break;
        }
        Log.w(TAG, "HTTP 503, unknown connection error");
        return new AuthResponse(false, false, "Unknown error, check log.", paramEngageWebResponse.mResponseCode);
    }

    private AuthResponse handleGenericApiError(EngageWebResponse paramEngageWebResponse) {
        String str;
        try {
            JSONObject jSONObject = new JSONObject();
            //this(paramEngageWebResponse.mResponseString);
            str = jSONObject.getString("error");
        } catch (JSONException jSONException) {
            Log.e(TAG, String.format("No 'error' key in %d login response. Response: '%s'", new Object[] { Integer.valueOf(paramEngageWebResponse.mResponseCode), paramEngageWebResponse.mResponseString }));
            str = String.format(Locale.US, "Unknown error (%d).", new Object[] { Integer.valueOf(paramEngageWebResponse.mResponseCode) });
        }
        return new AuthResponse(false, false, str, paramEngageWebResponse.mResponseCode);
    }

    private AuthResponse handleKeyedApiError(EngageWebResponse paramEngageWebResponse, String... paramVarArgs) {
        AuthResponse authResponse = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            JSONObject jSONObject = new JSONObject();
            //this(paramEngageWebResponse.mResponseString);
            jSONObject = jSONObject.getJSONObject("error");
            int i = paramVarArgs.length;
            for (byte b = 0; b < i; b++) {
                String str = paramVarArgs[b];
                if (jSONObject.has(str)) {
                    JSONArray jSONArray = jSONObject.getJSONArray(str);
                    for (byte b1 = 0; b1 < jSONArray.length(); b1++) {
                        StringBuilder stringBuilder1 = new StringBuilder();
                        //this();
                        stringBuilder.append(stringBuilder1.append(jSONArray.getString(b1)).append("\n").toString());
                    }
                }
            }
            stringBuilder.replace(stringBuilder.lastIndexOf("\n"), stringBuilder.length(), "");
            authResponse = new AuthResponse(false, false, stringBuilder.toString(), paramEngageWebResponse.mResponseCode);
        } catch (JSONException jSONException) {
            //Log.e(TAG, "Error parsing JSON for error keys: " + Arrays.toString((Object[])paramVarArgs) + ", json: " + ((EngageWebResponse)authResponse).mResponseString);
            //authResponse = handleGenericApiError((EngageWebResponse)authResponse);
        }
        return authResponse;
    }

    private void handleRequestWithNoInternet(IAuthenticationManagerCallback paramIAuthenticationManagerCallback) {
        paramIAuthenticationManagerCallback.onAuthConnectionError(new AuthResponse(false, false, "Your action could not be performed since there is no internet connection.", -1));
    }

    public void changePassword(IAuthenticationManagerCallback paramIAuthenticationManagerCallback, String paramString1, String paramString2) {
        Log.i(TAG, "POST CHANGE PASSWORD");
        if (paramIAuthenticationManagerCallback == null)
            paramIAuthenticationManagerCallback = this.nopCallback;
        if (InternetUtils.isInternetConnected((Context)this))
            try {
                HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
                JSONObject jSONObject1 = new JSONObject();
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("old_password", paramString1);
                jSONObject2.put("new_password", paramString2);
                jSONObject2.put("confirm_password", paramString2);
                jSONObject1.put("User", jSONObject2);
                hashMap.put("json", jSONObject1.toString());
                //EngageWebClient engageWebClient = new EngageWebClient();
                //OnChangePassword onChangePassword = new OnChangePassword();
                //this(this, paramIAuthenticationManagerCallback);
                //this(onChangePassword);
                //engageWebClient.sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD.PUT, "/me/change_password.json", this.mUserInfo.getAccessToken(), (Map)hashMap);
                return;
            } catch (JSONException jSONException) {
                throw new RuntimeException(jSONException);
            }
        //handleRequestWithNoInternet((IAuthenticationManagerCallback)jSONException);
    }

    public void createEngageAccount(IAuthenticationManagerCallback paramIAuthenticationManagerCallback, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
        Log.i(TAG, "CREATE ACCOUNT REQUEST");
        if (paramIAuthenticationManagerCallback == null)
            paramIAuthenticationManagerCallback = this.nopCallback;
        if (InternetUtils.isInternetConnected((Context)this)) {
            HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
            hashMap.put("email", paramString3);
            hashMap.put("password", paramString4);
            hashMap.put("client_id", paramString1);
            hashMap.put("client_secret", paramString2);
            if (paramString5 != null) {
                hashMap.put("device_token", paramString5);
                hashMap.put("device_type", "android");
            }
            (new EngageWebClient(new OnAccountCreated(paramIAuthenticationManagerCallback))).sendReq(EngageWebClientRequest.HTTP_METHOD.POST, "/register.json", true, (Map)hashMap);
            return;
        }
        handleRequestWithNoInternet(paramIAuthenticationManagerCallback);
    }

    public List<ServiceDependency> getDependentServices() {
        return null;
    }

    public UserInfo getUserInfo() {
        return this.mUserInfo;
    }

    public void loginToEngage(final IAuthenticationManagerCallback cb, final String accessToken, final String accessTokenType, final long accessTokenExpiry, final String refreshToken) {
        Log.i(TAG, "LOGIN ACCESS TOKEN");
        //if (cb == null)
        //    cb = this.nopCallback;
        if (InternetUtils.isInternetConnected((Context)this)) {
            (new EngageWebClient(new IEngageWebClientCallback() {
                public void onConnectionFinished(EngageWebResponse param1EngageWebResponse) {
                    AuthResponse authResponse = AuthenticationService.this.handleConnectionError(param1EngageWebResponse);
                    if (authResponse != null) {
                        cb.onAuthConnectionError(authResponse);
                        return;
                    }
                    if (param1EngageWebResponse.mResponseCode != 200) {
                        cb.onAuthConnectionError(new AuthResponse(false, false, "Failed login refresh.", param1EngageWebResponse.mResponseCode));
                        return;
                    }
                    try {
                        AuthenticationService authenticationService = AuthenticationService.this;

                        UserInfo userInfo;
                        JSONObject jSONObject;
                        jSONObject = new JSONObject(param1EngageWebResponse.mResponseString);
                        userInfo = new UserInfo(jSONObject, false);

                        //AuthenticationService.access$202(authenticationService, userInfo);
                        AuthenticationService.this.mUserInfo = userInfo;
                        AuthenticationService.this.mUserInfo.setAccessToken(accessToken);
                        AuthenticationService.this.mUserInfo.setAccessTokenType(accessTokenType);
                        AuthenticationService.this.mUserInfo.setAccessTokenExpires(accessTokenExpiry);
                        AuthenticationService.this.mUserInfo.setRefreshToken(refreshToken);
                        IAuthenticationManagerCallback iAuthenticationManagerCallback = cb;
                        AuthResponse authResponse1 = new AuthResponse(true, true, null, 200);
                        iAuthenticationManagerCallback.onLogin(authResponse1);
                        return;
                    } catch (Exception jSONException) {
                        Log.e(AuthenticationService.TAG, "Error parsing JSON: " + param1EngageWebResponse.mResponseString);
                        throw new RuntimeException(jSONException);
                    }
                }
            })).sendReqWithAuth(EngageWebClientRequest.HTTP_METHOD.GET, "/api/me", accessToken, null);
            return;
        }
        handleRequestWithNoInternet(cb);
    }

    public void loginToEngageThirdParty(IAuthenticationManagerCallback paramIAuthenticationManagerCallback, String paramString1, String paramString2, String paramString3, String paramString4) {
        Log.i(TAG, "LOGIN REQUEST THIRD PARTY");
        if (paramIAuthenticationManagerCallback == null)
            paramIAuthenticationManagerCallback = this.nopCallback;
        if (InternetUtils.isInternetConnected((Context)this)) {
            HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
            hashMap.put("client_id", paramString1);
            hashMap.put("client_secret", paramString2);
            hashMap.put("redirect_uri", paramString3);
            hashMap.put("grant_type", "authorization_code");
            hashMap.put("code", paramString4);
            (new EngageWebClient(new OnLoginToEngageThirdParty(paramIAuthenticationManagerCallback))).sendReq(EngageWebClientRequest.HTTP_METHOD.POST, "/api/oauth2/access_token", true, (Map)hashMap);
            return;
        }
        handleRequestWithNoInternet(paramIAuthenticationManagerCallback);
    }

    private abstract class AuthMgrWebClientCallback implements IEngageWebClientCallback {
        IAuthenticationManagerCallback cb;

        public AuthMgrWebClientCallback(IAuthenticationManagerCallback param1IAuthenticationManagerCallback) {
            this.cb = param1IAuthenticationManagerCallback;
        }
    }

    private class OnAccountCreated extends AuthMgrWebClientCallback {
        public OnAccountCreated(IAuthenticationManagerCallback param1IAuthenticationManagerCallback) {
            super(param1IAuthenticationManagerCallback);
        }

        public void onConnectionFinished(EngageWebResponse param1EngageWebResponse) {
            AuthResponse authResponse = AuthenticationService.this.handleConnectionError(param1EngageWebResponse);
            if (authResponse != null) {
                this.cb.onAuthConnectionError(authResponse);
                return;
            }
            if (param1EngageWebResponse.mResponseCode != 200) {
                this.cb.onAuthConnectionError(AuthenticationService.this.handleKeyedApiError(param1EngageWebResponse, new String[] { "email", "password" }));
                return;
            }
            try {
                AuthenticationService authenticationService = AuthenticationService.this;
                UserInfo userInfo = new UserInfo();
                JSONObject jSONObject = new JSONObject();
                //this(param1EngageWebResponse.mResponseString);
                //this(jSONObject, true);
                //AuthenticationService.access$202(authenticationService, userInfo);
                IAuthenticationManagerCallback iAuthenticationManagerCallback = this.cb;
                //AuthResponse authResponse1 = new AuthResponse();
                //this(true, true, null, 200);
                //iAuthenticationManagerCallback.onAccountCreated(authResponse1);
                return;
            } catch (Exception jSONException) {
                Log.e(AuthenticationService.TAG, "Error parsing JSON: " + param1EngageWebResponse.mResponseString);
                throw new RuntimeException(jSONException);
            }
        }
    }

    private class OnChangePassword extends AuthMgrWebClientCallback {
        public OnChangePassword(IAuthenticationManagerCallback param1IAuthenticationManagerCallback) {
            super(param1IAuthenticationManagerCallback);
        }

        public void onConnectionFinished(EngageWebResponse param1EngageWebResponse) {
            AuthResponse authResponse = AuthenticationService.this.handleConnectionError(param1EngageWebResponse);
            if (authResponse != null) {
                this.cb.onAuthConnectionError(authResponse);
                return;
            }
            if (param1EngageWebResponse.mResponseCode != 200) {
                this.cb.onAuthConnectionError(AuthenticationService.this.handleKeyedApiError(param1EngageWebResponse, new String[] { "new_password", "confirm_password" }));
                return;
            }
            try {
                AuthenticationService authenticationService = AuthenticationService.this;
                UserInfo userInfo = new UserInfo();
                JSONObject jSONObject = new JSONObject();
                //this(param1EngageWebResponse.mResponseString);
                //this(jSONObject, true);
                //AuthenticationService.access$202(authenticationService, userInfo);
                IAuthenticationManagerCallback iAuthenticationManagerCallback = this.cb;
                //AuthResponse authResponse1 = new AuthResponse();
                //this(true, true, null, 200);
                //iAuthenticationManagerCallback.onChangedPassword(authResponse1);
                return;
            //} catch (JSONException jSONException) {
            } catch (Exception jSONException) {
                Log.e(AuthenticationService.TAG, "Error parsing JSON: " + param1EngageWebResponse.mResponseString);
                throw new RuntimeException(jSONException);
            }
        }
    }

    private class OnLoginToEngageThirdParty extends AuthMgrWebClientCallback {
        public OnLoginToEngageThirdParty(IAuthenticationManagerCallback param1IAuthenticationManagerCallback) {
            super(param1IAuthenticationManagerCallback);
        }

        public void onConnectionFinished(EngageWebResponse param1EngageWebResponse) {
            AuthResponse authResponse = AuthenticationService.this.handleConnectionError(param1EngageWebResponse);
            if (authResponse != null) {
                this.cb.onAuthConnectionError(authResponse);
                return;
            }
            if (param1EngageWebResponse.mResponseCode != 200) {
                this.cb.onAuthConnectionError(AuthenticationService.this.handleGenericApiError(param1EngageWebResponse));
                return;
            }
            try {
                AuthenticationService authenticationService = AuthenticationService.this;
                JSONObject jSONObject = new JSONObject(param1EngageWebResponse.mResponseString);
                UserInfo userInfo = new UserInfo(jSONObject, true);
                //AuthenticationService.access$202(authenticationService, userInfo);
                AuthenticationService.this.mUserInfo = userInfo;
                this.cb.onLoginThirdParty(AuthenticationService.this.mUserInfo.getAccessToken());
                return;
            } catch (JSONException jSONException) {
                Log.e(AuthenticationService.TAG, "Error parsing JSON: " + param1EngageWebResponse.mResponseString);
                throw new RuntimeException(jSONException);
            }
        }
    }
}
