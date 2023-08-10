package com.recom3.snow3.mobilesdk.tripviewer;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class RunMeta {
    private static final String TAG = RunMeta.class.getSimpleName();
    public Boolean active;
    public String average_speed;
    public String average_temperature;
    public String avg_air_time;
    public String avg_jump_distance;
    public String avg_jump_drop;
    public String avg_jump_height;
    public String avg_moving_speed;
    public String created;
    public Boolean jump_stats;
    public String max_air_time;
    public String max_altitude;
    public String max_jump_distance;
    public String max_jump_drop;
    public String max_jump_height;
    public String max_speed;
    public String max_temperature;
    public String min_temperature;
    public String modified;
    public String num_jumps;
    public String num_records;
    public String quality;
    public String run_duration;
    public String run_id;
    public String run_time;
    public String start_datetime;
    public String temperature_change;
    public String total_distance;
    public String trip_id;
    public String user_id;
    public String vertical_distance;

    public RunMeta(JSONObject jobj) {
        this.run_id = null;
        this.user_id = null;
        this.trip_id = null;
        this.active = null;
        this.quality = null;
        this.start_datetime = null;
        this.run_time = null;
        this.num_records = null;
        this.run_duration = null;
        this.total_distance = null;
        this.vertical_distance = null;
        this.max_speed = null;
        this.average_speed = null;
        this.avg_moving_speed = null;
        this.max_altitude = null;
        this.temperature_change = null;
        this.average_temperature = null;
        this.min_temperature = null;
        this.max_temperature = null;
        this.created = null;
        this.modified = null;
        this.jump_stats = null;
        this.num_jumps = null;
        this.max_jump_height = null;
        this.max_jump_distance = null;
        this.avg_jump_height = null;
        this.avg_jump_distance = null;
        this.max_jump_drop = null;
        this.max_air_time = null;
        this.avg_jump_drop = null;
        this.avg_air_time = null;
        try {
            this.run_id = jobj.getString("run_id");
            this.user_id = jobj.getString("user_id");
            this.trip_id = jobj.getString("trip_id");
            this.quality = jobj.getString("quality");
            this.start_datetime = jobj.getString("start_datetime");
            this.jump_stats = Boolean.valueOf(jobj.getBoolean("jump_stats"));
            this.run_time = jobj.getString("run_time");
            this.num_records = jobj.getString("num_records");
            this.total_distance = jobj.getString("total_distance");
            this.max_speed = jobj.getString("max_speed");
            this.average_speed = jobj.getString("average_speed");
            this.max_altitude = jobj.getString("max_altitude");
            this.vertical_distance = jobj.getString("vertical_distance");
            this.temperature_change = jobj.getString("temperature_change");
            this.average_temperature = jobj.getString("average_temperature");
            this.min_temperature = jobj.getString("min_temperature");
            this.max_temperature = jobj.getString("max_temperature");
            this.avg_moving_speed = jobj.getString("avg_moving_speed");
            this.active = Boolean.valueOf(jobj.getBoolean("active"));
            this.created = jobj.getString("created");
            this.modified = jobj.getString("modified");
            this.max_jump_height = jobj.getString("max_jump_height");
            this.max_jump_distance = jobj.getString("max_jump_distance");
            this.avg_jump_height = jobj.getString("avg_jump_height");
            this.avg_jump_distance = jobj.getString("avg_jump_distance");
            this.max_jump_drop = jobj.getString("max_jump_drop");
            this.max_air_time = jobj.getString("max_air_time");
            this.avg_jump_drop = jobj.getString("avg_jump_drop");
            this.avg_air_time = jobj.getString("avg_air_time");
            this.num_jumps = jobj.getString("num_jumps");
            this.run_duration = jobj.getString("run_duration");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + jobj.toString());
            throw new RuntimeException(e);
        }
    }
}