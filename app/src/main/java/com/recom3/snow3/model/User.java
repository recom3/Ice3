package com.recom3.snow3.model;

import com.recom3.snow3.mobilesdk.engageweb.UserInfo;

/**
 * Created by Recom3 on 25/01/2022.
 */

public class User {
    public static final String USER_FIELD_EMAIL = "USER_FIELD_EMAIL";

    public static final String USER_FIELD_NAME = "USER_FIELD_NAME";

    public static final String USER_FIELD_TOKEN = "USER_FIELD_TOKEN";

    public static final String USER_FIELD_UID = "USER_FIELD_UID";

    private String mEmail;

    private String mName;

    private String mToken;

    private String mUid;

    public User() {}

    public User(UserInfo paramUserInfo) {
        this.mUid = paramUserInfo.getUserId();
        this.mToken = paramUserInfo.getAccessToken();
        this.mName = paramUserInfo.getName();
        this.mEmail = paramUserInfo.getEmail();
    }

    public String getEmail() {
        return this.mEmail;
    }

    public String getName() {
        return this.mName;
    }

    public String getToken() {
        return this.mToken;
    }

    public String getUid() {
        return this.mUid;
    }

    public void setEmail(String paramString) {
        this.mEmail = paramString;
    }

    public void setName(String paramString) {
        this.mName = paramString;
    }

    public void setToken(String paramString) {
        this.mToken = paramString;
    }

    public void setUid(String paramString) {
        this.mUid = paramString;
    }
}
