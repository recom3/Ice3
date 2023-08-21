package com.recom3.mobilesdk.buddytracking;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.recom3.connect.messages.BuddyInfoMessage;
import com.recom3.snow3.mobilesdk.EngageSdkService;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.engageweb.AuthenticationService;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDStateUpdateListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Recom3 on 06/07/2022.
 */

public class BuddyService extends EngageSdkService implements IBuddyManager, LocationListener {

    private static final String TAG;

    private static final String BUDDIES_DELTA_KEY = "buddiesXml";
    public static final String BUDDIES_SEARCHED = "com.reconinstruments.mobilesdk.BUDDIES_SEARCHED";
    public static final String BUDDIES_UPDATED = "com.reconinstruments.mobilesdk.BUDDIES_UPDATED";

    static {
        TAG = BuddyService.class.getSimpleName();
    }

    public static AuthenticationService authSrvc = null;

    public static HUDConnectivityService hudSrvc = null;

    private Class<? extends AuthenticationService> authServiceClass;

    private Timer getFromWebTimer;

    private Class<? extends HUDConnectivityService> hudServiceClass;

    private BuddyManager mBuddyManager = new BuddyManager(this);

    private ServiceConnection AuthConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
            Log.i(BuddyService.TAG, "EngageAuthentication Connected!");
            BuddyService.authSrvc = (AuthenticationService)((EngageSdkService.LocalBinder)param1IBinder).getService();
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            Log.i(BuddyService.TAG, "EngageAuthentication Disconnected!");
            BuddyService.authSrvc = null;
        }
    };

    private ServiceConnection HudConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
            Log.i(BuddyService.TAG, "BuddyService Connected to HUDService!");
            BuddyService.hudSrvc = ((HUDConnectivityService.LocalBinder)param1IBinder).getService();
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            Log.i(BuddyService.TAG, "BuddyService Disconnected to HUDService!");
            BuddyService.hudSrvc = null;
        }
    };

    public BuddyService(Class<? extends AuthenticationService> paramClass, Class<? extends HUDConnectivityService> paramClass1) {
        this.authServiceClass = paramClass;
        this.hudServiceClass = paramClass1;
    }

    public BuddyManager getBuddyManager() {
        return this.mBuddyManager;
    }

    public void setUpdateFromWebPeriod(int paramInt) {
        if (this.getFromWebTimer != null)
            this.getFromWebTimer.cancel();
        if (paramInt <= 0) {
            this.getFromWebTimer = null;
            return;
        }
        this.getFromWebTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                BuddyService.this.mBuddyManager.updateBuddies();
            }
        };
        this.getFromWebTimer.scheduleAtFixedRate(timerTask, 0L, (paramInt * 1000));
    }

    public void updateGoggles() {
        //!!!
        /*
        if (hudSrvc.getConnectionState() == HUDStateUpdateListener.HUD_STATE.CONNECTED) {
            ArrayList<BuddyInfoMessage.BuddyInfo> arrayList = new ArrayList(0);
            for (Buddy buddy : getBuddyManager().getAllBuddies().getBuddies().values()) {
                if (buddy.getState() == Buddy.UserState.ONLINE && buddy.getLocation() != null) {
                    Location location = this.sentLocations.get(buddy.getID());
                    if (location == null || !location.equals(buddy.getLocation())) {
                        arrayList.add(buddy.getBuddyInfo());
                        this.sentLocations.put(buddy.getID(), buddy.getLocation());
                    }
                }
            }
            if (arrayList.size() > 0) {
                sendBuddies(arrayList);
                return;
            }
            Log.i(TAG, "No buddies with new locations to send");
        }
        */
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onBuddiesUpdated(String paramString) {
        Intent intent = new Intent("com.reconinstruments.mobilesdk.BUDDIES_UPDATED");
        intent.putExtra("buddiesXml", paramString);
        sendBroadcast(intent);
        //This has been added to the original implementation
        updateGoggles();
    }

    @Override
    public void onBuddiesUpdatedError(String paramString) {

    }

    @Override
    public List<EngageSdkService.ServiceDependency> getDependentServices() {
        boolean bool1 = true;
        boolean bool2 = false;
        ArrayList<EngageSdkService.ServiceDependency> arrayList = new ArrayList();
        Class<? extends AuthenticationService> clazz = this.authServiceClass;
        ServiceConnection serviceConnection2 = this.AuthConnection;
        if (authSrvc != null) {
            bool1 = true;
        } else {
            bool1 = false;
        }
        arrayList.add(new EngageSdkService.ServiceDependency(clazz, serviceConnection2, bool1));
        Class<? extends HUDConnectivityService> clazz1 = this.hudServiceClass;
        ServiceConnection serviceConnection1 = this.HudConnection;
        if (hudSrvc != null) {
            bool2 = bool1;
            arrayList.add(new EngageSdkService.ServiceDependency(clazz1, serviceConnection1, bool2));
            return arrayList;
        }
        arrayList.add(new EngageSdkService.ServiceDependency(clazz1, serviceConnection1, bool2));
        return arrayList;
    }
}
