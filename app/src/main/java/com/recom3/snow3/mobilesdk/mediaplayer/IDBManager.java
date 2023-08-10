package com.recom3.snow3.mobilesdk.mediaplayer;

/**
 * Created by Recom3 on 27/01/2022.
 */

public interface IDBManager {
    void onBuildMusicDB(DBManager.DBState paramDBState);

    void onErrorBuildingDB(DBManager.DBState paramDBState);

    void onMusicDBProgress(DBManager.DBState paramDBState);
}