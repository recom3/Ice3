package com.recom3.snow3.mobilesdk.engageweb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Recom3 on 24/01/2022.
 */

public class UserInfo {
    private static String ACCESS_TOKEN_KEY = "access_token";

    private static String AUTO_UPLOAD_TRIPS_KEY;

    private static String BIO_KEY;

    private static String BUDDY_TRACKING_ENABLED_KEY;

    private static String BUDDY_TRACKING_STEALTH_MODE_ENABLED_KEY;

    private static String CITY_KEY;

    private static String COUNTRY_ID_KEY;

    private static String EMAIL_KEY;

    private static String FACEBOOK_ID_KEY;

    private static String FIRST_NAME_KEY;

    private static String GENDER_KEY;

    private static String LAST_LOGIN_KEY;

    private static String LAST_NAME_KEY;

    private static String MEASUREMENT_KEY;

    private static String MOBILE_ACTIVE_KEY;

    private static String PHONE_NUMBER_KEY;

    private static String REFRESH_TOKEN_KEY;

    private static String TOKEN_EXPIRES_KEY;

    private static String TOKEN_TYPE_KEY = "token_type";

    private static String USER_ID_KEY;

    private static String USER_JSON_OBJECT_KEY;

    private static String USER_PROFILE_ID_KEY;

    private static String USER_PROFILE_JSON_OBJECT_KEY;

    private String access_token = "";

    private boolean auto_upload_trips = true;

    private String bio = "";

    private boolean buddy_tracking_enabled = false;

    private boolean buddy_tracking_stealth_mode_enabled = false;

    private String city = "";

    private String country_id = "";

    private String email = "";

    private String facebook_id = "";

    private String first_name = "";

    private String gender = "";

    private String last_login = "";

    private String last_name = "";

    private UNIT_TYPE measurment = UNIT_TYPE.METRIC;

    private String mobile_active = "";

    private String phone_number = "";

    private String refresh_token = "";

    private long token_expires = 0L;

    private String token_type = "";

    private String user_id = "";

    private String user_profile_id = "";

    static {
        TOKEN_EXPIRES_KEY = "expires";
        REFRESH_TOKEN_KEY = "refresh_token";
        USER_JSON_OBJECT_KEY = "User";
        USER_ID_KEY = "id";
        EMAIL_KEY = "email";
        FIRST_NAME_KEY = "first_name";
        LAST_NAME_KEY = "last_name";
        FACEBOOK_ID_KEY = "facebook_id";
        MOBILE_ACTIVE_KEY = "mobile_active";
        LAST_LOGIN_KEY = "last_login";
        MEASUREMENT_KEY = "measurement";
        BUDDY_TRACKING_ENABLED_KEY = "buddy_tracking_enabled";
        BUDDY_TRACKING_STEALTH_MODE_ENABLED_KEY = "buddy_tracking_stealth_mode_enabled";
        USER_PROFILE_JSON_OBJECT_KEY = "UserProfile";
        USER_PROFILE_ID_KEY = "id";
        AUTO_UPLOAD_TRIPS_KEY = "auto_upload_trips";
        PHONE_NUMBER_KEY = "phone_number";
        GENDER_KEY = "gender";
        CITY_KEY = "city";
        COUNTRY_ID_KEY = "country_id";
        BIO_KEY = "bio";
    }

    public UserInfo() {}

    public UserInfo(JSONObject paramJSONObject, boolean paramBoolean) throws JSONException {
        parse(paramJSONObject, paramBoolean);
    }

    private void parse(JSONObject paramJSONObject, boolean paramBoolean) throws JSONException {
        UNIT_TYPE uNIT_TYPE;
        if (paramBoolean) {
            this.access_token = paramJSONObject.getString(ACCESS_TOKEN_KEY);
            this.token_type = paramJSONObject.getString(TOKEN_TYPE_KEY);
            this.token_expires = paramJSONObject.getLong(TOKEN_EXPIRES_KEY);
            if (paramJSONObject.has(REFRESH_TOKEN_KEY)) {
                this.refresh_token = paramJSONObject.getString(REFRESH_TOKEN_KEY);
            } else {
                this.refresh_token = null;
            }
        }
        JSONObject jSONObject = paramJSONObject.getJSONObject(USER_JSON_OBJECT_KEY);
        this.user_id = jSONObject.getString(USER_ID_KEY);
        this.email = jSONObject.getString(EMAIL_KEY);
        this.first_name = jSONObject.getString(FIRST_NAME_KEY);
        this.last_name = jSONObject.getString(LAST_NAME_KEY);
        this.facebook_id = jSONObject.getString(FACEBOOK_ID_KEY);
        this.mobile_active = jSONObject.getString(MOBILE_ACTIVE_KEY);
        this.last_login = jSONObject.getString(LAST_LOGIN_KEY);
        if (jSONObject.getString(MEASUREMENT_KEY).equals("imperial")) {
            uNIT_TYPE = UNIT_TYPE.IMPERIAL;
        } else {
            uNIT_TYPE = UNIT_TYPE.METRIC;
        }
        this.measurment = uNIT_TYPE;
        this.buddy_tracking_enabled = jSONObject.getBoolean(BUDDY_TRACKING_ENABLED_KEY);
        this.buddy_tracking_stealth_mode_enabled = jSONObject.getBoolean(BUDDY_TRACKING_STEALTH_MODE_ENABLED_KEY);
        paramJSONObject = paramJSONObject.getJSONObject(USER_PROFILE_JSON_OBJECT_KEY);
        this.user_profile_id = paramJSONObject.getString(USER_PROFILE_ID_KEY);
        this.auto_upload_trips = paramJSONObject.getBoolean(AUTO_UPLOAD_TRIPS_KEY);
        this.phone_number = paramJSONObject.getString(PHONE_NUMBER_KEY);
        this.gender = paramJSONObject.getString(GENDER_KEY);
        this.country_id = paramJSONObject.getString(COUNTRY_ID_KEY);
        this.bio = paramJSONObject.getString(BIO_KEY);
    }

    public String getAccessToken() {
        return this.access_token;
    }

    public long getAccessTokenExpires() {
        return this.token_expires;
    }

    public String getAccessTokenType() {
        return this.token_type;
    }

    public String getBio() {
        return this.bio;
    }

    public String getCity() {
        return this.city;
    }

    public String getCountryId() {
        return this.country_id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFacebookId() {
        return this.facebook_id;
    }

    public String getFirstName() {
        return this.first_name;
    }

    public String getGender() {
        return this.gender;
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject jSONObject1 = new JSONObject();
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(FIRST_NAME_KEY, this.first_name);
        jSONObject2.put(LAST_NAME_KEY, this.last_name);
        jSONObject2.put(EMAIL_KEY, this.email);
        jSONObject2.put(BUDDY_TRACKING_ENABLED_KEY, this.buddy_tracking_enabled);
        jSONObject2.put(BUDDY_TRACKING_STEALTH_MODE_ENABLED_KEY, this.buddy_tracking_stealth_mode_enabled);
        String str1 = MEASUREMENT_KEY;
        if (this.measurment == UNIT_TYPE.METRIC) {
            String str = "metric";
            jSONObject2.put(str1, str);
            jSONObject1.put(USER_JSON_OBJECT_KEY, jSONObject2);
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(GENDER_KEY, this.gender);
            jSONObject.put(PHONE_NUMBER_KEY, this.phone_number);
            jSONObject.put(CITY_KEY, this.city);
            jSONObject.put(BIO_KEY, this.bio);
            jSONObject1.put(USER_PROFILE_JSON_OBJECT_KEY, jSONObject);
            return jSONObject1;
        }
        String str2 = "imperial";
        jSONObject2.put(str1, str2);
        jSONObject1.put(USER_JSON_OBJECT_KEY, jSONObject2);
        JSONObject jSONObject3 = new JSONObject();
        jSONObject3.put(GENDER_KEY, this.gender);
        jSONObject3.put(PHONE_NUMBER_KEY, this.phone_number);
        jSONObject3.put(CITY_KEY, this.city);
        jSONObject3.put(BIO_KEY, this.bio);
        jSONObject1.put(USER_PROFILE_JSON_OBJECT_KEY, jSONObject3);
        return jSONObject1;
    }

    public String getLastLogin() {
        return this.last_login;
    }

    public String getLastName() {
        return this.last_name;
    }

    public UNIT_TYPE getMeasurmentUnit() {
        return this.measurment;
    }

    public String getMobileActive() {
        return this.mobile_active;
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    public String getPhoneNumber() {
        return this.phone_number;
    }

    public String getRefreshToken() {
        return this.refresh_token;
    }

    public String getUserId() {
        return this.user_id;
    }

    public String getUserProfileId() {
        return this.user_profile_id;
    }

    public boolean isAutoUploadTrips() {
        return this.auto_upload_trips;
    }

    public boolean isBuddyTrackingEnabled() {
        return this.buddy_tracking_enabled;
    }

    public boolean isBuddyTrackingStealthModeEnabled() {
        return this.buddy_tracking_stealth_mode_enabled;
    }

    protected void setAccessToken(String paramString) {
        this.access_token = paramString;
    }

    protected void setAccessTokenExpires(long paramLong) {
        this.token_expires = paramLong;
    }

    protected void setAccessTokenType(String paramString) {
        this.token_type = paramString;
    }

    public void setAutoUploadTrips(boolean paramBoolean) {
        this.auto_upload_trips = paramBoolean;
    }

    public void setBio(String paramString) {
        this.bio = paramString;
    }

    public void setBuddyTrackingEnabled(boolean paramBoolean) {
        this.buddy_tracking_enabled = paramBoolean;
    }

    public void setBuddyTrackingStealthModeEnabled(boolean paramBoolean) {
        this.buddy_tracking_stealth_mode_enabled = paramBoolean;
    }

    public void setCity(String paramString) {
        this.city = paramString;
    }

    public void setCountryId(String paramString) {
        this.country_id = paramString;
    }

    public void setEmail(String paramString) {
        this.email = paramString;
    }

    protected void setFacebookId(String paramString) {
        this.facebook_id = paramString;
    }

    public void setFirstName(String paramString) {
        this.first_name = paramString;
    }

    public void setGender(String paramString) {
        this.gender = paramString;
    }

    protected void setLastLogin(String paramString) {
        this.last_login = paramString;
    }

    public void setLastName(String paramString) {
        this.last_name = paramString;
    }

    public void setMeasurmentUnit(UNIT_TYPE paramUNIT_TYPE) {
        this.measurment = paramUNIT_TYPE;
    }

    protected void setMobileActive(String paramString) {
        this.mobile_active = paramString;
    }

    public void setPhoneNumber(String paramString) {
        this.phone_number = paramString;
    }

    protected void setRefreshToken(String paramString) {
        this.refresh_token = paramString;
    }

    protected void setUserId(String paramString) {
        this.user_id = paramString;
    }

    protected void setUserProfileId(String paramString) {
        this.user_profile_id = paramString;
    }

    public enum UNIT_TYPE {
        IMPERIAL, METRIC;

        //static {
        //    $VALUES = new UNIT_TYPE[] { METRIC, IMPERIAL };
        //}

        /*
        private int value;
        private static Map map = new HashMap<>();

        private SpriteType(int value) {
            this.value = value;
        }

        static {
            for (SpriteType pageType : SpriteType.values()) {
                map.put(pageType.value, pageType);
            }
        }

        public static SpriteType valueOf(int pageType) {
            return (SpriteType) map.get(pageType);
        }

        public int getValue() {
            return value;
        }
                */
    }
}
