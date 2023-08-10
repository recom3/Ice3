package com.recom3.snow3.service;

import com.recom3.jetandroid.services.EngageHudConnectivityService;
import com.recom3.snow3.mobilesdk.MediaPlayerService;
import com.recom3.snow3.mobilesdk.mediaplayer.DBManager;
import com.recom3.snow3.mobilesdk.mediaplayer.IDBManager;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class MediaPlayerHudService extends MediaPlayerService {
    private IDBManager onDBBuilt = new IDBManager() {
        public void onBuildMusicDB(DBManager.DBState param1DBState) {
            //!!!!
            //Logcat.d("----------------------- DONE");
        }

        public void onErrorBuildingDB(DBManager.DBState param1DBState) {
            //!!!!
            //Logcat.e("----------------------- ERROR");
        }

        public void onMusicDBProgress(DBManager.DBState param1DBState) {
            //!!!!
            //Logcat.e("----------------------- PROCESSING");
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
