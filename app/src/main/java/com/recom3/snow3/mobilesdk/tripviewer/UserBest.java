package com.recom3.snow3.mobilesdk.tripviewer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class UserBest implements Parcelable {
    public static final Parcelable.Creator<UserBest> CREATOR = new Parcelable.Creator<UserBest>() { // from class: com.reconinstruments.mobilesdk.tripviewer.UserBest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserBest createFromParcel(Parcel source) {
            return new UserBest(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserBest[] newArray(int size) {
            return new UserBest[size];
        }
    };
    private static final String TAG = "UserBestData";
    public String best_max_alt;
    public String best_max_speed;
    public String best_total_distance;
    public String best_total_jumps;
    public String best_total_vertical;
    public String sport_type;

    public UserBest() {
        this.best_max_alt = "0";
        this.best_max_speed = "0";
        this.best_total_distance = "0";
        this.best_total_jumps = "0";
        this.best_total_vertical = "0";
        this.sport_type = "0";
    }

    private UserBest(Parcel parcel) {
        this.best_max_alt = "0";
        this.best_max_speed = "0";
        this.best_total_distance = "0";
        this.best_total_jumps = "0";
        this.best_total_vertical = "0";
        this.sport_type = "0";
        this.best_max_alt = parcel.readString();
        this.best_max_speed = parcel.readString();
        this.best_total_distance = parcel.readString();
        this.best_total_jumps = parcel.readString();
        this.best_total_vertical = parcel.readString();
        this.sport_type = parcel.readString();
    }

    public UserBest(JSONObject jobj) {
        this.best_max_alt = "0";
        this.best_max_speed = "0";
        this.best_total_distance = "0";
        this.best_total_jumps = "0";
        this.best_total_vertical = "0";
        this.sport_type = "0";
        initialize(jobj, true);
    }

    public UserBest(JSONObject jobj, boolean metric) {
        this.best_max_alt = "0";
        this.best_max_speed = "0";
        this.best_total_distance = "0";
        this.best_total_jumps = "0";
        this.best_total_vertical = "0";
        this.sport_type = "0";
        initialize(jobj, metric);
    }

    private void initialize(JSONObject jobj, boolean metric) {
        try {
            JSONObject firstData = jobj.getJSONObject("data").getJSONArray("data").getJSONObject(0);
            JSONObject userBest = firstData.getJSONObject("UserBest");
            JSONObject sportType = firstData.getJSONObject("SportType");
            this.best_max_alt = userBest.getString("max_alt");
            this.best_max_speed = userBest.getString("max_speed");
            this.best_total_distance = userBest.getString("total_distance");
            this.best_total_jumps = userBest.getString("total_jumps");
            this.best_total_vertical = userBest.getString("total_vertical");
            this.sport_type = sportType.getString("id");
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.best_max_alt);
        dest.writeString(this.best_max_speed);
        dest.writeString(this.best_total_distance);
        dest.writeString(this.best_total_jumps);
        dest.writeString(this.best_total_vertical);
        dest.writeString(this.sport_type);
    }
}
