package com.reconinstruments.os.connectivity;

import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Recom3 on 15/05/2023.
 */

public abstract class BaseHUDConnectivity implements Parcelable {
    public abstract void a(JSONObject paramJSONObject);

    public abstract void b(JSONObject paramJSONObject);

    public String toString() {
        String str = null;
        try {
            JSONObject jSONObject = new JSONObject();
            a(jSONObject);
            str = jSONObject.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            exception = null;
        }
        return str;
    }
}