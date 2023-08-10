package com.recom3.snow3.mobilesdk.tripviewer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class TripMeta {
    private static final String TAG = TripMeta.class.getSimpleName();
    public String trip_id;
    public ArrayList<RunMeta> trip_runs_meta = new ArrayList<>();

    public TripMeta(JSONObject json) {
        this.trip_id = null;
        try {
            JSONObject trip = json.getJSONObject("Trip");
            this.trip_id = trip.getString("id");
            JSONArray tripSegments = json.getJSONArray("TripSegment");
            for (int i = 0; i < tripSegments.length(); i++) {
                this.trip_runs_meta.add(new RunMeta(tripSegments.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + json.toString());
            throw new RuntimeException(e);
        }
    }
}
