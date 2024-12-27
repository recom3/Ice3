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
import com.recom3.snow3.utility.NumberFormatter;

/**
 * Created by recom3 on 31/08/2023.
 */

public class TotalDistanceDetailsFragment extends AbstractDetailsFragment {

    //@InjectView(R.id.trip_details_total_distance_fragment_image_button_box)
    private ImageButton mBoxButton;
    //@InjectView(R.id.trip_details_total_distance_fragment_text_view_total_distance)
    private TextView mTotalDistance;
    //@InjectView(R.id.trip_details_total_distance_fragment_text_view_total_distance_metric)
    private TextView mTotalDistanceMetric;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflated = inflater.inflate(R.layout.trip_details_total_distance_fragment, container, false);
        TripInternalDetailsFragment internalDetails = TripInternalDetailsFragment.newInstance(R.color.trip_details_yellow, R.string.trip_details_internal_total_distance_top_left_description, R.string.trip_details_internal_total_distance_top_right_description, this.mAirwaveService.getCurrentMetricFromMeterWithValue(this.mTripParcelable.getMaxVerticalChange()), this.mAirwaveService.getCurrentMetricFromMeterWithValue(getExtras().getDouble(TripDetailsActivity.TRIP_DETAILS_TOTAL_DISTANCE_ALL_TIME_DISTANCE)));
        addFragment(R.id.trip_internal_details_fragment, internalDetails);
        return viewInflated;
    }

    @Override // roboguice.fragment.RoboFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTotalDistanceValues();
        this.mBoxButton.setOnClickListener(this.mBoxDetailsClickListener);
    }

    private void setTotalDistanceValues() {
        MetricFormat maxSpeedMetricFormat = this.mAirwaveService.getCurrentMetricFromMeterWithValue(this.mTripParcelable.getTotalDistance());
        this.mTotalDistance.setText(NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(maxSpeedMetricFormat.getValue()));
        this.mTotalDistanceMetric.setText(maxSpeedMetricFormat.getSuffix().suffix());
    }
}
