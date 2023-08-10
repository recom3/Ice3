package com.recom3.snow3.activity.buddy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Recom3 on 04/07/2022.
 */

public class BuddiesTabFragment0 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
    public void onResume() {
        if (this.mAirwaveService.isBuddyTracking()) {
            showBuddiesListView((View)null);
        } else {
            if (mBuddyAirwaveService != null)
                mBuddyAirwaveService.disableBuddyTracking();
            addFragment(2131165197, (Fragment)BuddiesEnableTrackingFragment.newInstance(this));
        }
        super.onResume();
    }
    */

    public void showBuddiesListView(/*View paramView*/Bundle savedInstanceState) {

        //addFragment(2131165197, (Fragment)new BuddiesListFragment());
        //this.mAirwaveService.setBuddyTracking(Boolean.valueOf(true));

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction()
            //        .setReorderingAllowed(true)
            //        .add(R.id.fragment_container_view, ExampleFragment.class, null)
            //        .commit();
        }
    }
}
