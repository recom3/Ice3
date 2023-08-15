package com.recom3.snow3;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.recom3.jetandroid.services.EngageHudConnectivityService;
import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
//import com.recom3.snow3.service.ConnectivityHudService;
import com.recom3.snow3.mobilesdk.messages.PhoneMessage;
import com.reconinstruments.os.connectivity.HUDWebService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivityTest extends AppCompatActivity implements BuddyEnableCallback {

    public static String RECON_CLIENT_ID = "uo2UWK5iHY1XTAxkQNJD";

    public static String RECON_CLIENT_SECRET = "pGqHdY94EmMrTjlBNLtyzcC2x5i0DoRfIAek31gK";

    public static String RECON_REDIRECT_URI = "oakley-oauth://airwave-android";

    SectionsPagerAdapter mSectionsPagerAdapter;

    public static HUDConnectivityService mConnectivityHudService;

    public static HUDWebService mHUDWebService;

    private boolean hudBound = false;

    int NUM_PAGES = 3;

    public static boolean mDoAuthLogin = false;

    @Override
    public void onBuddyTrackingEnabled(boolean activate) {
        //Call page adapter to replace buddy fragment
        mSectionsPagerAdapter.setBuddiesListFragment(activate);
    }

    public class SectionsPagerAdapter2 extends FragmentPagerAdapter {

        Drawable myDrawable;
        String title;

        public SectionsPagerAdapter2(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }

    private String getBluetoothMac(final Context context) {

        String result = null;
        //if (context.checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH)
        //        == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Hardware ID are restricted in Android 6+
                // https://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id
                // Getting bluetooth mac via reflection for devices with Android 6+
                result = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        "bluetooth_address");
            } else {
                BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
                result = bta != null ? bta.getAddress() : "";
            }
        //}
        return result;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Debug
        //String macAddress = getBluetoothMac(this);

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //configActionBar();

        //Floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //View pager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        //viewPager.setAdapter(new CustomPagerAdapter(this));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(), this, this);
        viewPager.setAdapter(mSectionsPagerAdapter);

        //addTabIcon(R.drawable.tab_icon_buddies_selector);
        //addTabIcon(R.drawable.tab_icon_trips_selector);
        //addTabIcon(R.drawable.tab_icon_pairing_selector);

        startServices();

        //startWebServices();
    }

    private ServiceConnection hudServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
            //MainActivity.this.startService(ConnectivityHudService.class);
            startService(new Intent(MainActivityTest.this,EngageHudConnectivityService.class));
            MainActivityTest.mConnectivityHudService = ((HUDConnectivityService.LocalBinder)param1IBinder).getService();
            MainActivityTest.this.hudBound = true;
            //Logcat.d("OakleyHUD Connected!");
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            //MainActivity.this.stopService(ConnectivityHudService.class);
            stopService(new Intent(MainActivityTest.this,EngageHudConnectivityService.class));
            MainActivityTest.mConnectivityHudService = null;
            MainActivityTest.this.hudBound = false;
            //Logcat.d("OakleyHUD Disconnected!");
        }
    };

    private ServiceConnection hudWebSrvConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {

            Log.i("HUDWebService", "onServiceConnected");

            HUDWebService.LocalBinder binder = (HUDWebService.LocalBinder) param1IBinder;
            MainActivityTest.this.mHUDWebService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName param1ComponentName) {
            MainActivityTest.this.mHUDWebService = null;
        }
    };

    public void startServices() {
        //recom3
        //Intent intent = new Intent(this, ConnectivityHudService.class);
        Intent intent = new Intent(this, EngageHudConnectivityService.class);
        bindService(intent, this.hudServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void startWebServices() {
        Intent intent = new Intent(this, HUDWebService.class);
        bindService(intent, this.hudWebSrvConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * Used to send an incoming task test to the Goggles
     * This code is duplicated in other parts
     * @param param1View
     */
    //public void onBuddyTrack(View param1View) {

        /*
        EngageLocationManager.getInstance().enableLocationTracking((Context)getActivity(), new GooglePlayServicesClient.OnConnectionFailedListener() {
            public void onConnectionFailed(ConnectionResult param1ConnectionResult) {
                if (param1ConnectionResult.hasResolution())
                    try {
                        param1ConnectionResult.startResolutionForResult((Activity)BuddiesListFragment.this.getActivity(), -102);
                        return;
                    } catch (android.content.IntentSender.SendIntentException sendIntentException) {
                        throw new RuntimeException(sendIntentException);
                    }
            }
        }(HUDConnectivityService)MainViewPagerActivity.mConnectivityHudService);
        */
        //BuddiesTabFragment.mBuddyAirwaveService.enableBuddyTracking();
    //}

    private void configActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        }
    }

    private void addTabIcon(int icon) {
        //getSupportActionBar().addTab(getActionBar().newTab().setIcon(icon).setTabListener(this));
    }

    public void showBuddiesListView() {
        mSectionsPagerAdapter.notifyDataSetChanged();
    }

    public static void incommingCall()
    {
        Log.i("LoginAcivity", "Incommming call");

        if(mConnectivityHudService!=null) {
            /*
            HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
            hUDConnectivityMessage.setIntentFilter("RECON_MUSIC_MESSAGE");
            hUDConnectivityMessage.setRequestKey(0);
            hUDConnectivityMessage.setSender("com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService");
            hUDConnectivityMessage.setData((new MusicMessage(paramPlayerInfo, MusicMessage.Type.STATUS)).toXML().getBytes());
            Log.i("MediaPlayerServiceSDK", "size of music status message: " + (hUDConnectivityMessage.toByteArray()).length);
            hudSrvc.push(hUDConnectivityMessage, HUDConnectivityService.Channel.OBJECT_CHANNEL);
            */

            HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();

            //Error: sample trace
            /*
            04-13 08:35:46.804 854-915/? D/BTAcceptThread: mServerSocket.accept()
            04-13 08:35:46.804 854-1174/? D/BTConnectedThread: BEGIN BTConnectedThread OBJECT_CHANNEL Thread[Thread-75,5,main]
            04-13 08:35:56.453 854-854/? I/BTConnectivityManager: Start receiving new HUDConnectivityMessage data block, total size = 219
            04-13 08:35:56.453 854-854/? D/BTConnectivityManager: dataReceived= 219
            04-13 08:35:56.453 854-854/? I/BTConnectivityManager: Stop receiving data, constructing the HUDConnectivityMessage
            04-13 08:35:56.460 854-854/? I/BTConnectivityManager: HUDConnectivityMessage md5 = 175a68a176e560d53f51395df4b0684b
            04-13 08:35:56.460 854-854/? D/BTConnectivityManager: Sent out the broadcast to RECON_SMARTPHONE_CONNECTION_MESSAGE
            04-13 08:35:56.468 854-854/? W/Bundle: Key message expected String but value was a [B.  The default value <null> was returned.
            04-13 08:35:56.468 585-585/? W/Bundle: Key message expected String but value was a [B.  The default value <null> was returned.
            04-13 08:35:56.476 854-854/? W/Bundle: Attempt to cast generated internal exception:
                                                   java.lang.ClassCastException: byte[] cannot be cast to java.lang.String
                                                       at android.os.Bundle.getString(Bundle.java:1061)
                                                       at com.reconinstruments.hudservice.HUDService$1.onReceive(HUDService.java:92)

             */

            //hUDConnectivityMessage.setIntentFilter("RECON_SMARTPHONE_CONNECTION_MESSAGE");

            //Nothing happens
            //hUDConnectivityMessage.setIntentFilter("SMARTPHONE_STATE_UPDATED");

            //hudStateUpdateListener.startListening(this);

            hUDConnectivityMessage.setIntentFilter("RECON_PHONE_MESSAGE");
            hUDConnectivityMessage.setRequestKey(0);
            hUDConnectivityMessage.setSender("com.reconinstruments.mobilesdk.phonecontrol.PhoneControlService");
            String[] param = {"123456", "myfriend", "xx", "yy"};
            String testStr = new PhoneMessage(PhoneMessage.Status.RINGING, param).toXML().toString();

            byte[] bytes = (new PhoneMessage(PhoneMessage.Status.RINGING, param)).toXML().getBytes();
            //hUDConnectivityMessage.setData(bytes);
            hUDConnectivityMessage.setData((new PhoneMessage(PhoneMessage.Status.RINGING, param)).toXML().getBytes());

            Log.i("PhoneServiceSDK", "size of phone status message: " + (hUDConnectivityMessage.toByteArray()).length);

            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(hUDConnectivityMessage.getData());
            byte[] digest = md.digest();
            Log.i("PhoneServiceSDK", "md5: " + md.toString());

            //Test deserialize

            HUDConnectivityMessage hudConnectivityMessage = new HUDConnectivityMessage(hUDConnectivityMessage.toByteArray());

            PhoneMessage phnMessage = new PhoneMessage(new String(hudConnectivityMessage.getData()));

            mConnectivityHudService.push(hUDConnectivityMessage, HUDConnectivityService.Channel.OBJECT_CHANNEL);

            //To compare with other messages...:
            /*
            HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
            hUDConnectivityMessage.setIntentFilter("RECON_MUSIC_MESSAGE");
            hUDConnectivityMessage.setRequestKey(0);
            hUDConnectivityMessage.setSender("com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService");
            hUDConnectivityMessage.setData((new MusicMessage(paramPlayerInfo, MusicMessage.Type.STATUS)).toXML().getBytes());
            Log.i("MediaPlayerServiceSDK", "size of music status message: " + (hUDConnectivityMessage.toByteArray()).length);
            hudSrvc.push(hUDConnectivityMessage, HUDConnectivityService.Channel.OBJECT_CHANNEL);
            */
        }
    }
}
