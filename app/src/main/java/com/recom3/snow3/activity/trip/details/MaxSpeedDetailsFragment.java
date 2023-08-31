package com.recom3.snow3.activity.trip.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.recom3.snow3.R;
import com.recom3.snow3.activity.trip.TripDetailsActivity;
import com.recom3.snow3.model.MetricFormat;
import com.recom3.snow3.util.validation.MetricFormatter;
import com.recom3.snow3.utility.NumberFormatter;

/**
 * Created by Chus on 29/08/2023.
 */

public class MaxSpeedDetailsFragment extends AbstractDetailsFragment {
    //@InjectView(R.id.trip_details_max_speed_fragment_image_button_box)
    private ImageButton mBoxButton;
    //@InjectView(R.id.trip_details_max_speed_fragment_text_view_max_speed)
    private TextView mMaxSpeed;
    //@InjectView(R.id.trip_details_max_speed_fragment_text_view_max_speed_metric)
    private TextView mMaxSpeedMetric;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflated = inflater.inflate(R.layout.trip_details_max_speed_fragment, container, false);
        if(this.mAirwaveService!=null) {
            MetricFormat averageSpeed = this.mAirwaveService.getCurrentMetricFromMeterWithValue(this.mTripParcelable.getAvgSpeed());
            MetricFormat allTimeBest = this.mAirwaveService.getCurrentMetricFromMeterWithValue(getExtras().getDouble(TripDetailsActivity.TRIP_DETAILS_MAX_SPEED_ALL_TIME_BEST));
            averageSpeed.setSuffix(MetricFormat.MetricSuffix.KILOMETER_PER_HOUR);
            allTimeBest.setSuffix(MetricFormat.MetricSuffix.KILOMETER_PER_HOUR);
            if (this.mAirwaveService.isMilesMetric()) {
                averageSpeed = MetricFormatter.formatMilesPerHour(Double.parseDouble(this.mTripParcelable.getAvgSpeed()));
                allTimeBest = MetricFormatter.formatMilesPerHour(getExtras().getDouble(TripDetailsActivity.TRIP_DETAILS_MAX_SPEED_ALL_TIME_BEST));
            }
            TripInternalDetailsFragment internalDetails = TripInternalDetailsFragment.newInstance(R.color.trip_details_green, R.string.trip_details_internal_max_speed_top_left_description, R.string.trip_details_internal_max_speed_top_right_description, averageSpeed, allTimeBest);
            //!recom3
            addFragment(R.id.trip_internal_details_fragment, internalDetails);
        }
        return viewInflated;
    }

    @Override // roboguice.fragment.RoboFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setMaxSpeedValues();
        if(this.mBoxButton!=null) {
            this.mBoxButton.setOnClickListener(this.mBoxDetailsClickListener);
        }
    }

    private void setMaxSpeedValues() {
        if(this.mAirwaveService!=null) {
            MetricFormat maxSpeedMetricFormat = this.mAirwaveService.getCurrentMetricFromMeterWithValue(this.mTripParcelable.getMaxSpeed());
            maxSpeedMetricFormat.setSuffix(MetricFormat.MetricSuffix.KILOMETER_PER_HOUR);
            if (this.mAirwaveService.isMilesMetric()) {
                maxSpeedMetricFormat = MetricFormatter.formatMilesPerHour(Double.parseDouble(this.mTripParcelable.getMaxSpeed()));
            }
            this.mMaxSpeed.setText(NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(maxSpeedMetricFormat.getValue()));
            this.mMaxSpeedMetric.setText(maxSpeedMetricFormat.getSuffix().suffix());
        }
    }
}