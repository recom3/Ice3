package com.recom3.snow3.service;

import com.recom3.snow3.MainActivityTest;
import com.recom3.snow3.mobilesdk.engageweb.AuthenticationService;
import com.recom3.snow3.mobilesdk.engageweb.IAuthenticationManagerCallback;

/**
 * Created by Recom3 on 24/01/2022.
 */

public class AuthenticationAirwaveService extends AuthenticationService {
    public void loginToEngage(IAuthenticationManagerCallback paramIAuthenticationManagerCallback, String paramString) {
        loginToEngage(paramIAuthenticationManagerCallback, paramString, null, 0L, null);
    }

    public void loginToEngageThirdParty(IAuthenticationManagerCallback paramIAuthenticationManagerCallback, String paramString) {
        loginToEngageThirdParty(paramIAuthenticationManagerCallback, MainActivityTest.RECON_CLIENT_ID, MainActivityTest.RECON_CLIENT_SECRET, MainActivityTest.RECON_REDIRECT_URI, paramString);
    }
}