package com.recom3.snow3.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Recom3 on 25/01/2022.
 */

public class MetricFormat implements Parcelable {
    public static final Parcelable.Creator<MetricFormat> CREATOR = new Parcelable.Creator<MetricFormat>() {
        public MetricFormat createFromParcel(Parcel param1Parcel) {
            //return new MetricFormat(param1Parcel, null);
            return new MetricFormat(param1Parcel);
        }

        public MetricFormat[] newArray(int param1Int) {
            return new MetricFormat[param1Int];
        }
    };

    private MetricSuffix mSuffix;

    private double mValue;

    private MetricFormat(Parcel paramParcel) {
        this.mSuffix = MetricSuffix.valueOf(paramParcel.readString());
        this.mValue = paramParcel.readDouble();
    }

    public MetricFormat(MetricSuffix paramMetricSuffix, double paramDouble) {
        this.mSuffix = paramMetricSuffix;
        this.mValue = paramDouble;
    }

    public int describeContents() {
        return 0;
    }

    public MetricSuffix getSuffix() {
        return this.mSuffix;
    }

    public double getValue() {
        return this.mValue;
    }

    public void setSuffix(MetricSuffix paramMetricSuffix) {
        this.mSuffix = paramMetricSuffix;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt) {
        paramParcel.writeDouble(this.mValue);
        paramParcel.writeString(this.mSuffix.suffix());
    }

    public enum MetricSuffix {
        FEET("FEET"),
        KILOMETER("KILOMETER"),
        KILOMETER_PER_HOUR("KILOMETER_PER_HOUR"),
        METER("m"),
        MILES("m"),
        MILES_PER_HOUR("m");

        private final String mSuffix;

        static {
            //FEET = new MetricSuffix("FEET", 2, "ft");
            //KILOMETER_PER_HOUR = new MetricSuffix("KILOMETER_PER_HOUR", 3, "km/h");
            //MILES_PER_HOUR = new MetricSuffix("MILES_PER_HOUR", 4, "mph");
            //MILES = new MetricSuffix("MILES", 5, "mile");

            //ENUM$VALUES = new MetricSuffix[] { METER, KILOMETER, FEET, KILOMETER_PER_HOUR, MILES_PER_HOUR, MILES };
        }

        MetricSuffix(String param1String1) {
            this.mSuffix = param1String1;
        }

        public String suffix() {
            return this.mSuffix;
        }
    }
}
