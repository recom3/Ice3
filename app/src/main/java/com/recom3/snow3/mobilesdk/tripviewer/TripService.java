package com.recom3.snow3.mobilesdk.tripviewer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.recom3.connect.messages.MusicMessage;
import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.EngageSdkService;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.engageweb.AuthenticationService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Recom3 on 28/05/2023.
 */

public class TripService extends EngageSdkService {
    public static final String ID_INTENT = "ID_FILE";
    public static final String URL_TRIP = "/api/meetripss";//"/me/trips.json";
    public static final String URL_TRIP_BASE = "/trips";
    static AuthenticationService authSrvc = null;
    static HUDConnectivityService hudSrvc = null;
    public static final String hudStateChangedIntent = "HUD_STATE_CHANGED";
    private Class<? extends AuthenticationService> authServiceClass;
    private Class<? extends HUDConnectivityService> hudServiceClass;
    private TripListManager tripListMgr;
    private TripSyncManager tripSyncMgr;
    private static final String TAG = TripService.class.getSimpleName();
    static String mLastDayMd5 = "";
    static String mLastDayName = "";
    static String mLastEventMd5 = "";
    static String mLastEventName = "";
    static boolean syncing = false;
    boolean containsDayFile = false;
    boolean containsEventFile = false;
    private BroadcastReceiver hudTripSyncer = new BroadcastReceiver() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context c, Intent intent) {
            Log.i(TripService.TAG, "Received broadcast, action: " + intent.getAction());
            if (intent.getAction().equals(TripSyncManager.downloadTripFromHUD)) {
                Bundle b = intent.getExtras();
                HUDConnectivityMessage msg = new HUDConnectivityMessage(b.getByteArray("message"));
                int separatorIndex = msg.getInfo().indexOf("|");
                String f = msg.getInfo().substring(0, separatorIndex);
                String md5 = msg.getInfo().substring(separatorIndex + 1);
                Log.v(TripService.TAG, "received filename: " + f);
                Log.v(TripService.TAG, "received file md5: " + md5);
                boolean subbedFile = TripService.this.parseFileAndSaveFile(msg.getData(), "EngageTripTransfer", f);
                if (subbedFile) {
                    if (f.contains("DAY")) {
                        TripService.this.containsDayFile = true;
                        TripService.mLastDayName = f;
                        TripService.mLastDayMd5 = md5;
                    } else if (!f.contains("EVENT")) {
                        Log.e(TripService.TAG, "error occured as filename written does not contain DAY or EVENT");
                    } else {
                        TripService.this.containsEventFile = true;
                        TripService.mLastEventName = f;
                        TripService.mLastEventMd5 = md5;
                    }
                }
                if (TripService.this.containsEventFile && TripService.this.containsDayFile) {
                    TripService.this.containsEventFile = false;
                    TripService.this.containsDayFile = false;
                    File directory = TripService.this.getDir("EngageTripTransfer", 0);
                    File day = new File(directory.getAbsolutePath(), TripService.mLastDayName);
                    File event = new File(directory.getAbsolutePath(), TripService.mLastEventName);
                    File id = new File(directory.getAbsolutePath(), "ID.RIB");
                    Log.i(TripService.TAG, "SUCCESS got both files, uploading files to server");
                    TripService.this.tripSyncMgr.postTrip(day, event, id);
                }
            } else if (intent.getAction().equals(TripSyncManager.successTripRequest)) {
                TripService.this.toastUserSuccess(true);
                TripService.syncFinished();
                if (TripService.this.tripSyncMgr.mResponseHandler != null) {
                    TripService.this.tripSyncMgr.mResponseHandler.onFinishedSync(true);
                }
            } else if (intent.getAction().equals(TripSyncManager.failedTripRequest)) {
                TripService.this.toastUserSuccess(false);
                TripService.syncFinished();
                if (TripService.this.tripSyncMgr.mResponseHandler != null) {
                    TripService.this.tripSyncMgr.mResponseHandler.onFinishedSync(false);
                }
            } else if (intent.getAction().equals(TripService.hudStateChangedIntent)) {
                Bundle bundle = intent.getExtras();
                int b2 = bundle.getInt(MusicMessage.ATTR_STATE);
                if (b2 == 0) {
                    TripService.syncFinished();
                }
            } else if (intent.getAction().equals(TripSyncManager.mPath)) {
                boolean result = intent.getBooleanExtra("result", false);
                if (result) {
                    Log.i(TripService.TAG, "message was successfully sent");
                    return;
                }
                TripService.syncFinished();
                Log.w(TripService.TAG, "message was NOT sent");
            }
        }
    };
    private BroadcastReceiver hudIDReceiver = new BroadcastReceiver() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(TripService.ID_INTENT)) {
                Log.i(TripService.TAG, "received broadcast with intent: " + intent.getAction());
                byte[] msgBytes = intent.getExtras().getByteArray("message");
                HUDConnectivityMessage hMsg = new HUDConnectivityMessage(msgBytes);
                TripService.this.parseFileAndSaveFile(hMsg.getData(), "EngageTripTransfer", "ID.RIB");
            }
        }
    };
    private ServiceConnection AuthConnection = new ServiceConnection() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripService.3
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            TripService.authSrvc = (AuthenticationService) ((EngageSdkService.LocalBinder) service).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            TripService.authSrvc = null;
        }
    };
    private ServiceConnection HudConnection = new ServiceConnection() { // from class: com.reconinstruments.mobilesdk.tripviewer.TripService.4
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            TripService.hudSrvc = ((HUDConnectivityService.LocalBinder) service).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            TripService.hudSrvc = null;
        }
    };

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        return 1;
    }

    public TripService(Class<? extends AuthenticationService> authServiceClass, Class<? extends HUDConnectivityService> hudServiceClass) {
        this.authServiceClass = authServiceClass;
        this.hudServiceClass = hudServiceClass;
    }

    @Override // com.reconinstruments.mobilesdk.EngageSdkService
    public List<ServiceDependency> getDependentServices() {
        List<EngageSdkService.ServiceDependency> deps = new ArrayList<>();
        deps.add(new EngageSdkService.ServiceDependency(this.authServiceClass, this.AuthConnection, authSrvc != null));
        deps.add(new EngageSdkService.ServiceDependency(this.hudServiceClass, this.HudConnection, hudSrvc != null));
        return deps;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        registerReceiver(this.hudIDReceiver, new IntentFilter(ID_INTENT));
        registerReceiver(this.hudTripSyncer, new IntentFilter(TripSyncManager.downloadTripFromHUD));
        registerReceiver(this.hudTripSyncer, new IntentFilter(TripSyncManager.mPath));
        registerReceiver(this.hudTripSyncer, new IntentFilter(TripSyncManager.failedTripRequest));
        registerReceiver(this.hudTripSyncer, new IntentFilter(TripSyncManager.successTripRequest));
        registerReceiver(this.hudTripSyncer, new IntentFilter(hudStateChangedIntent));
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.hudTripSyncer);
        unregisterReceiver(this.hudIDReceiver);
        unbindDependentServices();
        authSrvc = null;
        hudSrvc = null;
    }

    public void newTripListManager(ITripListQueryCallback callback) {
        this.tripListMgr = new TripListManager(callback);
    }

    public TripListManager getTripListManager() {
        if (this.tripListMgr == null) {
            this.tripListMgr = new TripListManager(null);
        }
        return this.tripListMgr;
    }

    public void newTripSyncManager(ITripSyncCallback callback) {
        this.tripSyncMgr = new TripSyncManager(callback);
    }

    public TripSyncManager getTripSyncManager() {
        if (this.tripSyncMgr == null) {
            this.tripSyncMgr = new TripSyncManager(null);
        }
        return this.tripSyncMgr;
    }

    public boolean syncInProgress() {
        return syncing;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void syncFinished() {
        syncing = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void toastUserSuccess(boolean success) {
        String text;
        if (success) {
            text = "Success syncing trips from HUD";
        } else {
            text = "Failed syncing trips from HUD";
        }
        Toast.makeText(this, text, 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean parseFileAndSaveFile(byte[] filePathBytes, String path, String filename) {
        byte[] b = new byte[0];
        try {
            b = readFile(new String(filePathBytes));
        } catch (IOException e) {
            Log.i(TAG, "Error reading file from storage");
        }
        File directory = getDir(path, 0);
        File fileToDel = new File(directory.getAbsolutePath(), filename);
        Log.i(TAG, "path to delete file: " + fileToDel.getAbsolutePath());
        if (fileToDel.exists()) {
            fileToDel.delete();
        }
        File directory2 = getDir(path, 0);
        File fullPath = new File(directory2.getAbsolutePath(), filename);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fullPath));
            try {
                bos.write(b);
                bos.flush();
                bos.close();
                Log.i(TAG, "success writting file to: " + fullPath);
                return true;
            } catch (IOException e2) {
                Log.w(TAG, "caught exception writing to file: " + e2);
                e2.printStackTrace();
                return false;
            }
        } catch (FileNotFoundException e3) {
            Log.w(TAG, "caught exception opening buffer: " + e3);
            e3.printStackTrace();
            return false;
        }
    }

    public static byte[] readFile(String path) throws IOException {
        RandomAccessFile f = new RandomAccessFile(new File(path), "r");
        try {
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) {
                throw new IOException("File size >= 2 GB");
            }
            byte[] data = new byte[length];
            f.readFully(data);
            Log.i(TAG, "raw file: " + data.length);
            return data;
        } finally {
            f.close();
        }
    }
}