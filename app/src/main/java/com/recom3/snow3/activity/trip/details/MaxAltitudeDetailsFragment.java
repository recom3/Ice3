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
import com.recom3.snow3.model.trip.TripParcelable;
import com.recom3.snow3.utility.NumberFormatter;

/**
 * Created by recom3 on 13/10/2023.
 */

public class MaxAltitudeDetailsFragment extends AbstractDetailsFragment {
    //@InjectView(R.id.trip_details_max_altitude_fragment_image_button_box)
    private ImageButton mBoxButton;
    //@InjectView(R.id.trip_details_max_altitude_fragment_text_view_max_altitude)
    private TextView mMaxAltitude;
    //@InjectView(R.id.trip_details_max_altitude_fragment_text_view_altitude_metric)
    private TextView mMaxAltitudeMetric;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflated = inflater.inflate(R.layout.trip_details_max_altitude_fragment, container, false);
        TripInternalDetailsFragment internalDetails = TripInternalDetailsFragment.newInstance(R.color.trip_details_orange, R.string.trip_details_internal_max_altitude_top_left_description, R.string.trip_details_internal_max_altitude_top_right_description, this.mAirwaveService.getCurrentMetricFromMeterWithValue(getMinAltitude(this.mTripParcelable)), this.mAirwaveService.getCurrentMetricFromMeterWithValue(getExtras().getDouble(TripDetailsActivity.TRIP_DETAILS_MAX_ALTITUDE_ALL_TIME_BEST)));
        addFragment(R.id.trip_internal_details_fragment, internalDetails);
        return viewInflated;
    }

    private double getMinAltitude(TripParcelable tripParcelable) {
        return Math.abs(Math.abs(Double.parseDouble(tripParcelable.getMaxAltitude())) - Math.abs(Double.parseDouble(tripParcelable.getMaxVerticalChange())));
    }

    @Override // roboguice.fragment.RoboFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setMaxAltitudeValues();
        this.mBoxButton.setOnClickListener(this.mBoxDetailsClickListener);
    }

    private void setMaxAltitudeValues() {
        MetricFormat maxSpeedMetricFormat = this.mAirwaveService.getCurrentMetricFromMeterWithValue(this.mTripParcelable.getMaxAltitude());
        this.mMaxAltitude.setText(NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(maxSpeedMetricFormat.getValue()));
        this.mMaxAltitudeMetric.setText(maxSpeedMetricFormat.getSuffix().suffix());
    }

}
