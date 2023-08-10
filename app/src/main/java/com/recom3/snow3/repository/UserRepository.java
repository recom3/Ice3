package com.recom3.snow3.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;
import com.recom3.snow3.model.User;

/**
 * Created by Recom3 on 25/01/2022.
 */

//@ContextSingleton
public class UserRepository extends AbstractRepository {
    private static final String USER_REPOSITORY = "UserRepository";

    //This is a mock up
    //public UserRepository() {
    //    super(null);
    //}

    //@Inject
    public UserRepository(Context paramContext) {
        super(paramContext);
    }

    protected String getSharedPreferencesName() {
        return "UserRepository";
    }

    public User load() {
        User user = new User();
        user.setUid(getSharedPreferences().getString("USER_FIELD_UID", ""));
        user.setToken(getSharedPreferences().getString("USER_FIELD_TOKEN", ""));
        user.setName(getSharedPreferences().getString("USER_FIELD_NAME", ""));
        user.setEmail(getSharedPreferences().getString("USER_FIELD_EMAIL", ""));
        return user;
    }

    public void save(User paramUser) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString("USER_FIELD_UID", paramUser.getUid());
        editor.putString("USER_FIELD_TOKEN", paramUser.getToken());
        editor.putString("USER_FIELD_NAME", paramUser.getName());
        editor.putString("USER_FIELD_EMAIL", paramUser.getEmail());
        editor.commit();
    }
}
