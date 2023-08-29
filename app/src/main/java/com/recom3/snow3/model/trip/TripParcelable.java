package com.recom3.snow3.model.trip;

import android.os.Parcel;
import android.os.Parcelable;

import com.recom3.snow3.mobilesdk.tripviewer.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chus on 28/08/2023.
 */

public class TripParcelable implements Parcelable {
    public static final Parcelable.Creator<TripParcelable> CREATOR = new Parcelable.Creator<TripParcelable>() { // from class: com.oakley.snow.model.trip.TripParcelable.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TripParcelable createFromParcel(Parcel in) {
            return new TripParcelable(in, null);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TripParcelable[] newArray(int size) {
            return new TripParcelable[size];
        }
    };
    private String mAvgSpeed;
    private String mCity;
    private String mCountry;
    private String mMaxAltitude;
    private String mMaxJumpDistance;
    private String mMaxJumpHeight;
    private String mMaxSpeed;
    private String mMaxVerticalChange;
    private String mNumJumps;
    private String mNumSegments;
    private String mTotalDistance;
    private String mTotalVertical;

    public TripParcelable(Trip trip) {
        this.mCountry = trip.trip_country;
        this.mCity = trip.trip_city;
        this.mNumSegments = trip.trip_num_segments;
        this.mMaxAltitude = trip.trip_max_alt;
        this.mMaxVerticalChange = trip.trip_max_vertical_change;
        this.mAvgSpeed = trip.trip_avg_speed;
        this.mMaxSpeed = trip.trip_max_speed;
        this.mTotalDistance = trip.trip_total_distance;
        this.mMaxJumpDistance = trip.trip_max_jump_distance;
        this.mMaxJumpHeight = trip.trip_max_jump_height;
        this.mNumJumps = trip.trip_num_jumps;
        this.mTotalVertical = trip.trip_total_vertical;
    }

    private TripParcelable(Parcel in) {
        List<String> fields = new ArrayList<>();
        in.readStringList(fields);
        this.mCountry = fields.get(0);
        this.mCity = fields.get(1);
        this.mNumSegments = fields.get(2);
        this.mMaxAltitude = fields.get(3);
        this.mMaxVerticalChange = fields.get(4);
        this.mAvgSpeed = fields.get(5);
        this.mMaxSpeed = fields.get(6);
        this.mTotalDistance = fields.get(7);
        this.mMaxJumpDistance = fields.get(8);
        this.mMaxJumpHeight = fields.get(9);
        this.mNumJumps = fields.get(10);
        this.mTotalVertical = fields.get(11);
    }

    /* synthetic */ TripParcelable(Parcel parcel, TripParcelable tripParcelable) {
        this(parcel);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        List<String> fields = new ArrayList<>();
        fields.add(this.mCountry);
        fields.add(this.mCity);
        fields.add(this.mNumSegments);
        fields.add(this.mMaxAltitude);
        fields.add(this.mMaxVerticalChange);
        fields.add(this.mAvgSpeed);
        fields.add(this.mMaxSpeed);
        fields.add(this.mTotalDistance);
        fields.add(this.mMaxJumpDistance);
        fields.add(this.mMaxJumpHeight);
        fields.add(this.mNumJumps);
        fields.add(this.mTotalVertical);
        out.writeStringList(fields);
    }

    public String getCity() {
        return this.mCity;
    }

    public String getCountry() {
        return this.mCountry;
    }

    public String getNumSegments() {
        return this.mNumSegments;
    }

    public String getMaxAltitude() {
        return this.mMaxAltitude;
    }

    public String getMaxVerticalChange() {
        return this.mMaxVerticalChange;
    }

    public String getAvgSpeed() {
        return this.mAvgSpeed;
    }

    public String getMaxSpeed() {
        return this.mMaxSpeed;
    }

    public String getTotalDistance() {
        return this.mTotalDistance;
    }

    public String getMaxJumpDistance() {
        return this.mMaxJumpDistance;
    }

    public String getMaxJumpHeight() {
        return this.mMaxJumpHeight;
    }

    public String getNumJumps() {
        return this.mNumJumps;
    }

    public String getTotalVertical() {
        return this.mTotalVertical;
    }
}