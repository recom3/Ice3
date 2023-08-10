package com.recom3.snow3;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.recom3.jetandroid.services.EngageHudConnectivityService;

/**
 * Created by Recom3 on 25/01/2022.
 */

public class MainViewPagerActivity extends Activity {

    //public static ConnectivityHudService mConnectivityHudService = null;
    public static EngageHudConnectivityService mConnectivityHudService = null;

    private final int PAIRING_TAB_ITEM_INDEX = 2;

    private boolean hudBound = false;

/*
    private ServiceConnection hudServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
            MainViewPagerActivity.this.startService(ConnectivityHudService.class);
            MainViewPagerActivity.mConnectivityHudService = (ConnectivityHudService)((HUDConnectivityService.LocalBinder)param1IBinder).getService();
            MainViewPagerActivity.this.hudBound = true;
            //Logcat.d("OakleyHUD Connected!");
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            MainViewPagerActivity.this.stopService(ConnectivityHudService.class);
            MainViewPagerActivity.mConnectivityHudService = null;
            MainViewPagerActivity.this.hudBound = false;
            //Logcat.d("OakleyHUD Disconnected!");
        }
    };*/
}
