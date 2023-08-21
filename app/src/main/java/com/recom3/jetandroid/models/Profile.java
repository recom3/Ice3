package com.recom3.jetandroid.models;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Recom3 on 18/03/2022.
 */

public class Profile {
    private static final String a = Profile.class.getSimpleName();

    private static final String b = Profile.class.getCanonicalName();

    private static SharedPreferences c;

    //public Profile(App paramApp) {
    //    c = paramApp.getSharedPreferences(b, 0);
    //}

    public static String a() {
        return c.getString("hud_mac_address", null);
    }

    /*
    public static void a(LoggedInUser paramLoggedInUser) {
        SharedPreferences.Editor editor = c.edit();
        if (AuthenticationManager.d() && paramLoggedInUser != null)
            try {
                editor.putString("user_info", paramLoggedInUser.b().toString());
            } catch (Exception exception) {
                Log.i(a, "Could not save login info: " + exception.getMessage());
            }
        editor.apply();
    }
*/
    public static void a(String paramString) {
        SharedPreferences.Editor editor = c.edit();
        editor.putString("hud_mac_address", paramString);
        editor.apply();
    }

    public static void a(boolean paramBoolean) {
        SharedPreferences.Editor editor = c.edit();
        editor.putBoolean("account_creation_incomplete", paramBoolean);
        editor.apply();
    }

    public static String b() {
        return c.getString("cached_login_email", null);
    }

    public static void b(String paramString) {
        SharedPreferences.Editor editor = c.edit();
        editor.putString("cached_login_email", paramString);
        editor.apply();
    }

    public static void b(boolean paramBoolean) {
        SharedPreferences.Editor editor = c.edit();
        editor.putBoolean("hud_update_notifications_enabled", paramBoolean);
        editor.apply();
    }

    public static void c() {
        SharedPreferences.Editor editor = c.edit();
        editor.remove("user_info");
        editor.apply();
    }
/*
    public static LoggedInUser d() {
        LoggedInUser loggedInUser = null;
        String str = c.getString("user_info", null);
        if (str != null)
            try {
                JSONObject jSONObject = new JSONObject(str);
                //this(str);
                //this(jSONObject);
                LoggedInUser loggedInUser1 = new LoggedInUser(jSONObject);
                loggedInUser = loggedInUser1;
            } catch (JSONException jSONException) {
                Log.i(a, "Could not parse saved user info object: " + jSONException.getMessage());
                Log.e(a, "Saved user info object: " + str);
            }
        return loggedInUser;
    }
*/
    public static void e() {
        SharedPreferences.Editor editor = c.edit();
        editor.putBoolean("terms_of_service_accpeted", true);
        editor.apply();
    }

    public static boolean f() {
        return c.getBoolean("terms_of_service_accpeted", false);
    }

    public static void g() {
        SharedPreferences.Editor editor = c.edit();
        editor.putBoolean("welcome_completed", true);
        editor.apply();
    }

    public static boolean h() {
        return c.getBoolean("welcome_completed", false);
    }

    public static boolean i() {
        return c.getBoolean("account_creation_incomplete", false);
    }

    public static boolean j() {
        return c.getBoolean("hud_update_notifications_enabled", true);
    }
}