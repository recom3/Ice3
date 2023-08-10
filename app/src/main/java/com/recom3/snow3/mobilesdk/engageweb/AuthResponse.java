package com.recom3.snow3.mobilesdk.engageweb;

/**
 * Created by Recom3 on 24/01/2022.
 */

public class AuthResponse {
    private String mErrorMsg;

    private boolean mLoggedIn;

    private boolean mRegistered;

    private int mResponseCode;

    AuthResponse(boolean paramBoolean1, boolean paramBoolean2, String paramString, int paramInt) {
        this.mLoggedIn = paramBoolean1;
        this.mRegistered = paramBoolean2;
        this.mErrorMsg = paramString;
        this.mResponseCode = paramInt;
    }

    public String getErrorMsg() {
        return this.mErrorMsg;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }

    public boolean isLoggedIn() {
        return this.mLoggedIn;
    }

    public boolean isRegistered() {
        return this.mRegistered;
    }
}
