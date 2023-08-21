package com.recom3.snow3.service;

import android.util.Log;

import com.recom3.jetandroid.services.EngageHudConnectivityService;
import com.recom3.snow3.mobilesdk.MediaPlayerService;
import com.recom3.snow3.mobilesdk.mediaplayer.DBManager;
import com.recom3.snow3.mobilesdk.mediaplayer.IDBManager;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class MediaPlayerHudService extends MediaPlayerService {
    public static final String TAG = MediaPlayerService.class.getSimpleName();

    private IDBManager onDBBuilt = new IDBManager() {
        public void onBuildMusicDB(DBManager.DBState param1DBState) {
            Log.i(TAG, "----------------------- DONE");
        }

        public void onErrorBuildingDB(DBManager.DBState param1DBState) {
            Log.e(TAG, "----------------------- ERROR");
        }

        public void onMusicDBProgress(DBManager.DBState param1DBState) {
            Log.e(TAG, "----------------------- PROCESSING");
        }
    };

    public MediaPlayerHudService() {
        //super(ConnectivityHudService.class);
        super(EngageHudConnectivityService.class);
    }

    public void onCreate() {
        super.onCreate();
        setDbMgr(this.onDBBuilt);
        getDbMgr().buildMusicDB();
    }
}
