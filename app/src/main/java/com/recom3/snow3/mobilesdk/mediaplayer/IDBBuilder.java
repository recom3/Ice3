package com.recom3.snow3.mobilesdk.mediaplayer;

/**
 * Created by Recom3 on 27/01/2022.
 */

public interface IDBBuilder {
    void onDBBuild(DBManager.DBState paramDBState);

    void onDBProgressUpdate(DBManager.DBState paramDBState);
}
