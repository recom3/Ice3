package com.recom3.snow3.service.authentication;

import com.recom3.snow3.mobilesdk.engageweb.AuthResponse;
import com.recom3.snow3.mobilesdk.engageweb.IAuthenticationManagerCallback;

/**
 * Created by Recom3 on 25/01/2022.
 */

public class AuthenticationManagerHelper implements IAuthenticationManagerCallback {
    public void onAccountCreated(AuthResponse paramAuthResponse) {}

    public void onAuthConnectionError(AuthResponse paramAuthResponse) {}

    public void onChangedPassword(AuthResponse paramAuthResponse) {}

    public void onFacebookLogin(AuthResponse paramAuthResponse) {}

    public void onLogin(AuthResponse paramAuthResponse) {}

    public void onLoginThirdParty(String paramString) {}

    public void onUserPrefChanged(AuthResponse paramAuthResponse) {}
}