package com.recom3.snow3.repository;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Recom3 on 25/01/2022.
 */

public abstract class AbstractRepository {
    private SharedPreferences mSharedPreferences;

    public AbstractRepository(Context paramContext) {
        this.mSharedPreferences = paramContext.getSharedPreferences(getSharedPreferencesName(), 0);
    }

    public void delete() {
        SharedPreferences.Editor editor = getEditor();
        editor.clear();
        editor.commit();
    }

    protected SharedPreferences.Editor getEditor() {
        return this.mSharedPreferences.edit();
    }

    protected SharedPreferences getSharedPreferences() {
        return this.mSharedPreferences;
    }

    protected abstract String getSharedPreferencesName();

    public void saveBoolean(String paramString, Boolean paramBoolean) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(paramString, paramBoolean.booleanValue());
        editor.commit();
    }
}
