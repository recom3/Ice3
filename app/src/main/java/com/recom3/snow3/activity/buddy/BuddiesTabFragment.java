package com.recom3.snow3.activity.buddy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.recom3.mobilesdk.buddytracking.BuddyAirwaveService;
import com.recom3.snow3.R;
import com.recom3.snow3.SectionsPagerAdapter;
import com.recom3.snow3.activity.buddy.BuddiesListFragment;
import com.recom3.snow3.mobilesdk.EngageSdkService;
import com.recom3.snow3.util.validation.ICallback;

/**
 * Created by Recom3 on 26/05/2023.
 */

public class BuddiesTabFragment extends Fragment {

    public static final String BUDDY_ID_EXTRA_KEY = "BUDDY_ID_EXTRA_KEY";
    private static final int REQUEST_RESOLVE_LOCATION = -102;
    private String mBuddiesOnlineStringHeader;
    private String mBuddiesPendingRequestString;

    private boolean mBuddyAirwaveServiceIsBound;

    public static BuddyAirwaveService mBuddyAirwaveService = null;

    private ServiceConnection buddyServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {

            //!!!
            //startService(BuddyAirwaveService.class);
            mBuddyAirwaveService = (BuddyAirwaveService)((EngageSdkService.LocalBinder)param1IBinder).getService();
            mBuddyAirwaveServiceIsBound = true;
            mBuddyAirwaveService.bindDependentServices(new Handler.Callback() {
                public boolean handleMessage(Message param2Message) {
                    return false;
                }
            });
            //Logcat.d("BuddyAirwaveService Connected!");

            displayView(0); // fragment at 0 position
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            mBuddyAirwaveService = null;
            //Logcat.d("BuddyAirwaveService Disconnected!");
        }
    };

    private void doBindService(Context context) {
        if(mBuddyAirwaveService==null) {
            //bindService((Service)mBuddyAirwaveService, BuddyAirwaveService.class, this.buddyServiceConnection);
            Intent intent = new Intent(context, BuddyAirwaveService.class);
            context.bindService(intent, this.buddyServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buddies_layout, container, false);

        doBindService(view.getContext());

        //displayView(0); // fragment at 0 position

        return view;
    }

    public void displayView(int position) {
        switch (position) {
            case 0:
                //tvTitle.setText(getResources().getString(R.string.signin_tile));
                showFragment(new BuddiesListFragment(), position);
                break;
            case 1:
                //tvTitle.setText(getResources().getString(R.string.forgot_password_tile));
                //showFragment(new ForgotPasswordFragment(), position);
                break;
            case 2:
                //tvTitle.setText(getResources().getString(R.string.change_password_tile));
                //showFragment(new ChangePasswordFragment(), position);
                break;

        }
    }

    public void showFragment(Fragment fragment, int position) {
        FragmentTransaction mTransactiont = SectionsPagerAdapter.mFragmentManager.beginTransaction();

        mTransactiont.replace(R.id.buddies_layout_center_container_fragment, fragment, fragment.getClass().getName());
        mTransactiont.commit();
    }
}
