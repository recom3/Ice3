package com.recom3.snow3.activity.buddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Recom3 on 04/07/2022.
 */

public class BuddiesEnableTrackingFragment extends AppCompatActivity {
    //@InjectView(2131165196)
    private Button mEnableBuddyTracking;

    //@InjectView(2131165195)
    private Button mTermsAndConditionsButton;

    private final View.OnClickListener onClickEnableBuddyTrackingButtonListener = new View.OnClickListener() {
        public void onClick(View param1View) {
            //BuddiesEnableTrackingFragment.this.getListener().showBuddiesListView(param1View);
        }
    };

    private final View.OnClickListener onClickTermsAndConditionsButtonListener = new View.OnClickListener() {
        public void onClick(View param1View) {
            BuddiesEnableTrackingFragment.this.goToURL(2131230865);
        }
    };

    /*
    private BuddiesEnableTrackingListener getListener() {
        return (BuddiesEnableTrackingListener)getTargetFragment();
    }
    */

    /*
    public static BuddiesEnableTrackingFragment newInstance(BuddiesEnableTrackingListener paramBuddiesEnableTrackingListener) {
        BuddiesEnableTrackingFragment buddiesEnableTrackingFragment = new BuddiesEnableTrackingFragment();
        buddiesEnableTrackingFragment.setTargetFragment((Fragment)paramBuddiesEnableTrackingListener, 0);
        return buddiesEnableTrackingFragment;
    }
    */

    public void goToURL(int paramInt) {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(paramInt))));
    }

    //public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        //return paramLayoutInflater.inflate(2130903041, paramViewGroup, false);
    //}

    public void onViewCreated(View paramView, Bundle paramBundle) {
        //super.onViewCreated(paramView, paramBundle);
        this.mTermsAndConditionsButton.setOnClickListener(this.onClickTermsAndConditionsButtonListener);
        this.mEnableBuddyTracking.setOnClickListener(this.onClickEnableBuddyTrackingButtonListener);
    }

    public static interface BuddiesEnableTrackingListener {
        void showBuddiesListView(View param1View);
    }
}
