package com.recom3.snow3.mobilesdk.tripviewer;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class Trip {
    private final String TAG = Trip.class.getSimpleName();
    public Boolean trip_active;
    public String trip_avg_air_time;
    public String trip_avg_jump_distance;
    public String trip_avg_jump_drop;
    public String trip_avg_jump_height;
    public String trip_avg_speed;
    public String trip_avg_temp;
    public String trip_city;
    public String trip_country;
    public String trip_country_id;
    public String trip_created;
    public String trip_day_first_timestamp;
    public String trip_duration;
    public String trip_id;
    public Boolean trip_jumps;
    public String trip_latitude;
    public String trip_longitude;
    public String trip_max_air_time;
    public String trip_max_alt;
    public String trip_max_jump_distance;
    public String trip_max_jump_drop;
    public String trip_max_jump_height;
    public String trip_max_speed;
    public String trip_max_temp;
    public String trip_max_temp_change;
    public String trip_max_vertical_change;
    public String trip_min_temp;
    public String trip_modified;
    public String trip_num_jumps;
    public String trip_num_records;
    public String trip_num_segments;
    public String trip_public_share_override;
    public String trip_resort;
    public String trip_resort_id;
    public String trip_sport;
    public String trip_sport_id;
    public String trip_time;
    public String trip_total_distance;
    public String trip_total_vertical;
    public String trip_user_id;
    public String trip_ws_trip_id;

    public Trip(JSONObject json) {
        this.trip_id = null;
        this.trip_user_id = null;
        this.trip_resort_id = null;
        this.trip_sport_id = null;
        this.trip_country_id = null;
        this.trip_ws_trip_id = null;
        this.trip_active = null;
        this.trip_latitude = null;
        this.trip_longitude = null;
        this.trip_sport = null;
        this.trip_resort = null;
        this.trip_city = null;
        this.trip_country = null;
        this.trip_public_share_override = null;
        this.trip_time = null;
        this.trip_day_first_timestamp = null;
        this.trip_duration = null;
        this.trip_created = null;
        this.trip_modified = null;
        this.trip_num_segments = null;
        this.trip_num_records = null;
        this.trip_max_temp_change = null;
        this.trip_max_temp = null;
        this.trip_min_temp = null;
        this.trip_avg_temp = null;
        this.trip_jumps = null;
        this.trip_num_jumps = null;
        this.trip_max_jump_height = null;
        this.trip_avg_jump_height = null;
        this.trip_max_jump_distance = null;
        this.trip_avg_jump_distance = null;
        this.trip_max_jump_drop = null;
        this.trip_avg_jump_drop = null;
        this.trip_max_air_time = null;
        this.trip_avg_air_time = null;
        this.trip_max_speed = null;
        this.trip_avg_speed = null;
        this.trip_max_alt = null;
        this.trip_total_distance = null;
        this.trip_total_vertical = null;
        this.trip_max_vertical_change = null;
        try {
            this.trip_id = json.getString("id");
            this.trip_user_id = json.getString("user_id");
            this.trip_resort_id = json.getString("resort_id");
            this.trip_sport_id = json.getString("sport_id");
            this.trip_country_id = json.getString("country_id");
            this.trip_ws_trip_id = json.getString("ws_trip_id");
            this.trip_latitude = json.getString("latitude");
            this.trip_longitude = json.getString("longitude");
            this.trip_total_vertical = json.getString("total_vert");
            this.trip_city = json.getString("city");
            this.trip_day_first_timestamp = json.getString("start_datetime");
            this.trip_jumps = Boolean.valueOf(json.getBoolean("jumps"));
            this.trip_time = json.getString("trip_time");
            this.trip_num_records = json.getString("num_records");
            this.trip_total_distance = json.getString("total_distance");
            this.trip_max_speed = json.getString("max_speed");
            this.trip_max_alt = json.getString("max_altitude");
            this.trip_max_temp = json.getString("max_temp");
            this.trip_min_temp = json.getString("min_temp");
            this.trip_avg_temp = json.getString("avg_temp");
            this.trip_avg_speed = json.getString("avg_speed");
            this.trip_max_vertical_change = json.getString("max_vertical_change");
            this.trip_max_temp_change = json.getString("max_temp_change");
            this.trip_active = Boolean.valueOf(json.getBoolean("active"));
            this.trip_created = json.getString("created");
            this.trip_modified = json.getString("modified");
            this.trip_num_segments = json.getString("num_segments");
            this.trip_max_jump_height = json.getString("max_jump_height");
            this.trip_max_jump_distance = json.getString("max_jump_distance");
            this.trip_avg_jump_height = json.getString("avg_jump_height");
            this.trip_avg_jump_distance = json.getString("avg_jump_distance");
            this.trip_max_jump_drop = json.getString("max_jump_drop");
            this.trip_max_air_time = json.getString("max_air_time");
            this.trip_avg_jump_drop = json.getString("avg_jump_drop");
            this.trip_avg_air_time = json.getString("avg_air_time");
            this.trip_num_jumps = json.getString("num_jumps");
            this.trip_public_share_override = json.getString("public_share_override");
            this.trip_duration = json.getString("trip_duration");
            this.trip_sport = json.getString("sport");
            this.trip_resort = json.getString("resort");
            this.trip_country = json.getString("country");
            Log.d(this.TAG, "Finished parsing trip " + this.trip_id);
        } catch (JSONException e) {
            Log.e(this.TAG, "Error parsing JSON: " + json.toString());
            throw new RuntimeException(e);
        }
    }
}