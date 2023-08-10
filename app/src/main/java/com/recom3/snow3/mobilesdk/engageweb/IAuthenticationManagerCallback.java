package com.recom3.snow3.mobilesdk.engageweb;

/**
 * Created by Recom3 on 24/01/2022.
 */

public interface IAuthenticationManagerCallback {
    void onAccountCreated(AuthResponse paramAuthResponse);

    void onAuthConnectionError(AuthResponse paramAuthResponse);

    void onChangedPassword(AuthResponse paramAuthResponse);

    void onFacebookLogin(AuthResponse paramAuthResponse);

    void onLogin(AuthResponse paramAuthResponse);

    void onLoginThirdParty(String paramString);

    void onUserPrefChanged(AuthResponse paramAuthResponse);
}
