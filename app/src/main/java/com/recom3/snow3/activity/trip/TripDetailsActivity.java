package com.recom3.snow3.activity.trip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.recom3.snow3.R;
import com.recom3.snow3.model.trip.TripParcelable;
import com.recom3.snow3.service.AirwaveService;

/**
 * Created by Chus on 28/08/2023.
 */

public class TripDetailsActivity extends AppCompatActivity {
        // extends AbstractRoboFragmentActivity implements AbstractDetailsFragment.OnBoxDetailsSelectedListener {
    public static final String TRIP_DETAILS_MAX_ALTITUDE_ALL_TIME_BEST = "TRIP_DETAILS_MAX_ALTITUDE_ALL_TIME_BEST";
    public static final String TRIP_DETAILS_MAX_SPEED_ALL_TIME_BEST = "TRIP_DETAILS_MAX_SPEED_ALL_TIME_BEST";
    public static final String TRIP_DETAILS_TOTAL_DISTANCE_ALL_TIME_DISTANCE = "TRIP_DETAILS_TOTAL_DISTANCE_ALL_TIME_DISTANCE";
    public static final String TRIP_DETAILS_TOTAL_VERTICAL_ALL_TIME_VERTICAL = "TRIP_DETAILS_TOTAL_VERTICAL_ALL_TIME_VERTICAL";
    public static final String TRIP_PARCELABLE_KEY = "TRIP_PARCELABLE_KEY";
    //@Inject
    private AirwaveService mAirwaveService;
    private View mCurrentBoxDetailsSelected;
    //@InjectView(R.id.trip_details_layout_text_view_location)
    private TextView mLocation;
    //@InjectView(R.id.trip_details_layout_text_view_runs_text)
    private TextView mRunDescription;
    //@InjectView(R.id.trip_details_layout_text_view_run_number)
    private TextView mRunNumber;
    //@InjectView(R.id.trip_details_layout_text_view_time_spent)
    private TextView mTimeSpent;

    /* JADX INFO: Access modifiers changed from: protected */
    //@Override // com.sook.android.activity.robo.AbstractRoboFragmentActivity, roboguice.activity.RoboFragmentActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onCreate(Bundle arg0) {
        //!recom3
        super.onCreate(arg0);
        setContentView(R.layout.trip_details_layout);

        if(getActionBar()!=null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            //getActionBar().setIcon(R.drawable.home_icon);
            getActionBar().setTitle(R.string.trip_details);
        }

        //!recom3
        Bundle data = getIntent().getExtras();
        TripParcelable tripParcelable = (TripParcelable) data.getParcelable(TRIP_PARCELABLE_KEY);
        setHeader(tripParcelable);

        mLocation = findViewById(R.id.trip_details_layout_text_view_location);
        //mRunNumber = findViewById(R.id.trip_details_layout_text_view_run_number);
    }

    private void setHeader(TripParcelable tripParcelable) {
        if(mLocation==null)
        {
            mLocation = findViewById(R.id.trip_details_layout_text_view_location);
        }
        this.mLocation.setText(String.format("%s, %s", tripParcelable.getCity(), tripParcelable.getCountry()));
        //this.mRunNumber.setText(tripParcelable.getNumSegments());
        if (Integer.parseInt(tripParcelable.getNumSegments()) > 1) {
            //!recom3
            //this.mRunDescription.setText(getString(R.string.trips_stats_list_image_runs_text));
        }
    }

    //@Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                //finish();
                return true;
            default:
                //!recom3
                //return super.onOptionsItemSelected(item);
                return true;
        }
    }

    //@Override // com.oakley.snow.activity.trip.details.AbstractDetailsFragment.OnBoxDetailsSelectedListener
    public void onBoxDetailsSelected(View fragment) {
        if (fragment.getVisibility() == View.VISIBLE) {
            fragment.setVisibility(View.GONE);
            this.mCurrentBoxDetailsSelected = null;
            return;
        }
        fragment.setVisibility(View.VISIBLE);
        if (this.mCurrentBoxDetailsSelected != null) {
            this.mCurrentBoxDetailsSelected.setVisibility(View.GONE);
        }
        this.mCurrentBoxDetailsSelected = fragment;
    }
}