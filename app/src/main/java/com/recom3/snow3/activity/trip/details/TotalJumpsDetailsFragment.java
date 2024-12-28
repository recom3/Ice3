package com.recom3.snow3.activity.trip.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.recom3.snow3.R;

/**
 * Created by recom3 on 13/10/2023.
 */

public class TotalJumpsDetailsFragment extends AbstractDetailsFragment {
    //@InjectView(R.id.trip_details_total_jumps_fragment_image_button_box)
    private ImageButton mBoxButton;
    //@InjectView(R.id.trip_details_total_jumps_fragment_text_view_total_jumps)
    private TextView mTotalJumps;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflated = inflater.inflate(R.layout.trip_details_total_jumps_fragment, container, false);
        TripInternalDetailsFragment internalDetails = TripInternalDetailsFragment.newInstance(R.color.trip_details_pink, R.string.trip_details_internal_total_jumps_top_left_description, R.string.trip_details_internal_total_jumps_top_right_description, this.mAirwaveService.getCurrentMetricFromMeterWithValue(this.mTripParcelable.getMaxJumpDistance()), this.mAirwaveService.getCurrentMetricFromMeterWithValue(this.mTripParcelable.getMaxJumpHeight()));
        addFragment(R.id.trip_internal_details_fragment, internalDetails);
        return viewInflated;
    }

    @Override // roboguice.fragment.RoboFragment, android.support.v4.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mTotalJumps.setText(this.mTripParcelable.getNumJumps());
        this.mBoxButton.setOnClickListener(this.mBoxDetailsClickListener);
    }

}
