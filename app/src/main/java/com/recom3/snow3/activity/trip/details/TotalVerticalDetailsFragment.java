package com.recom3.snow3.activity.trip.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.recom3.snow3.GreenFragment;
import com.recom3.snow3.R;
import com.recom3.snow3.activity.trip.TripDetailsActivity;
import com.recom3.snow3.model.MetricFormat;
import com.recom3.snow3.utility.NumberFormatter;

/**
 * Created by Chus on 30/08/2023.
 */

public class TotalVerticalDetailsFragment extends AbstractDetailsFragment {
    //@InjectView(R.id.trip_details_total_vertical_fragment_image_button_box)
    private ImageButton mBoxButton;
    //@InjectView(R.id.trip_details_total_vertical_fragment_text_view_total_vertical)
    private TextView mTotalVertical;
    //@InjectView(R.id.trip_details_total_vertical_fragment_text_view_total_vertical_metric)
    private TextView mTotalVerticalMetric;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflated = inflater.inflate(R.layout.trip_details_total_vertical_fragment, container, false);
        double maxVerticalChange = Double.parseDouble(this.mTripParcelable.getMaxVerticalChange());
        if(mAirwaveService!=null) {
            TripInternalDetailsFragment internalDetails = TripInternalDetailsFragment.newInstance(R.color.trip_details_blue, R.string.trip_details_internal_total_vertical_top_left_description, R.string.trip_details_internal_total_vertical_top_right_description, this.mAirwaveService.getCurrentMetricFromMeterWithValue(Math.abs(maxVerticalChange)), this.mAirwaveService.getCurrentMetricFromMeterWithValue(getExtras().getDouble(TripDetailsActivity.TRIP_DETAILS_TOTAL_VERTICAL_ALL_TIME_VERTICAL)));
            //!recom3
            //addFragment(R.id.trip_internal_details_fragment, internalDetails);
        }
        return viewInflated;
    }

    @Override // roboguice.fragment.RoboFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTotalVerticalValues();
        if(this.mBoxButton!=null) this.mBoxButton.setOnClickListener(this.mBoxDetailsClickListener);
    }

    private void setTotalVerticalValues() {
        MetricFormat maxSpeedMetricFormat = new MetricFormat(MetricFormat.MetricSuffix.METER, Double.parseDouble(this.mTripParcelable.getTotalVertical()));
        if(this.mAirwaveService!=null) {
            maxSpeedMetricFormat = this.mAirwaveService.getCurrentMetricFromMeterWithValue(this.mTripParcelable.getTotalVertical());
        }

        if(this.mTotalVertical!=null) this.mTotalVertical.setText(NumberFormatter.formatDecimalWithTwoMaximumFractionDigits(Math.abs(maxSpeedMetricFormat.getValue())));
        if(this.mTotalVerticalMetric!=null) this.mTotalVerticalMetric.setText(maxSpeedMetricFormat.getSuffix().suffix());
    }
}
