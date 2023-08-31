package com.recom3.snow3.activity.trip.details;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;

import com.recom3.snow3.activity.trip.TripDetailsActivity;
import com.recom3.snow3.model.trip.TripParcelable;
import com.recom3.snow3.service.AirwaveService;

/**
 * Created by Chus on 29/08/2023.
 */

public abstract class AbstractDetailsFragment extends Fragment {
    //@Inject
    protected AirwaveService mAirwaveService;
    protected View.OnClickListener mBoxDetailsClickListener = new View.OnClickListener() { // from class: com.oakley.snow.activity.trip.details.AbstractDetailsFragment.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            AbstractDetailsFragment.this.mBoxDetailsSelectedListener.onBoxDetailsSelected(AbstractDetailsFragment.this.mInternalDetailsFragment);
        }
    };
    protected OnBoxDetailsSelectedListener mBoxDetailsSelectedListener;
    //@InjectView(R.id.trip_internal_details_fragment)
    protected FrameLayout mInternalDetailsFragment;
    protected TripParcelable mTripParcelable;

    /* loaded from: classes.dex */
    public interface OnBoxDetailsSelectedListener {
        void onBoxDetailsSelected(View view);
    }

    @Override // android.support.v4.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mBoxDetailsSelectedListener = (OnBoxDetailsSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(String.valueOf(activity.toString()) + " must implement OnBoxDetailsSelectedListener");
        }
    }

    @Override // com.sook.android.activity.robo.fragment.AbstractRoboFragment, roboguice.fragment.RoboFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Bundle data = getActivity().getIntent().getExtras();
        this.mTripParcelable = (TripParcelable) data.getParcelable(TripDetailsActivity.TRIP_PARCELABLE_KEY);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Bundle getExtras() {
        return getActivity().getIntent().getExtras();
    }

    public void addFragment(int fragmentId, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //transaction.replace(fragmentId, fragment).commit();

        Bundle bundle = new Bundle();
        bundle.putInt("some_int", 0);

        //getSupportFragmentManager().beginTransaction()
        //        .setReorderingAllowed(true)
        //        .add(R.id.fragment_container_view, ExampleFragment.class, bundle)
        //        .commit();

        transaction.setReorderingAllowed(true)
                .add(fragmentId, fragment)
                .commit();
    }
}