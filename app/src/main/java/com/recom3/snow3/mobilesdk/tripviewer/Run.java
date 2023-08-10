package com.recom3.snow3.mobilesdk.tripviewer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class Run {
    private static final String TAG = Run.class.getSimpleName();
    public JSONArray run_events;
    public JSONArray run_records;
    public String run_type;

    public Run(JSONObject jobj) {
        this.run_type = null;
        this.run_records = null;
        this.run_events = null;
        try {
            this.run_type = jobj.getString("type");
            this.run_records = jobj.getJSONArray("records");
            this.run_events = jobj.getJSONArray("events");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + jobj.toString());
            throw new RuntimeException(e);
        }
    }
}
