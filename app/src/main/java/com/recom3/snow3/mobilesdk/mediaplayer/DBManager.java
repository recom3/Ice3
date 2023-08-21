package com.recom3.snow3.mobilesdk.mediaplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.recom3.connect.util.FileUtils;
import com.recom3.connect.util.XMLUtils;
import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.MediaPlayerService;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDStateUpdateListener;

import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class DBManager {
    private static final String TAG = "DBManager";

    static MediaPlayerService mOwner;

    private DBBuilderTask builder;

    private DBState mState = DBState.CHECKING;

    private IDBBuilder onBuildMusicDB = new IDBBuilder() {
        public void onDBBuild(DBManager.DBState state) {
            DBManager.this.mState = state;
            if (state == DBState.ERROR || state == DBState.NOSTORAGE) {
                DBManager.this.respHandler.onErrorBuildingDB(state);
                return;
            }
            if (MediaPlayerService.hudSrvc != null) {
                if (MediaPlayerService.hudSrvc.getConnectionState() == HUDStateUpdateListener.HUD_STATE.CONNECTED) {
                    DBManager.this.buildGoggleDb();
                }
            } else {
                Log.e(DBManager.TAG, "hudService connection for media is NULL");
            }
            DBManager.this.respHandler.onBuildMusicDB(state);
        }

        public void onDBProgressUpdate(DBManager.DBState param1DBState) {
            DBManager.this.respHandler.onMusicDBProgress(param1DBState);
        }
    };

    IDBManager respHandler = null;

    public DBManager(IDBManager paramIDBManager, Context paramContext) {
        this.respHandler = paramIDBManager;
        mOwner = (MediaPlayerService)paramContext;
    }

    private void checkGoggleDB() {
        HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
        hUDConnectivityMessage.setIntentFilter("REMOTE_DB_CHECKSUM_REQUEST");
        hUDConnectivityMessage.setRequestKey(0);
        hUDConnectivityMessage.setSender("com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService");
        hUDConnectivityMessage.setData(writeXmlForRemoteChecksum().getBytes());
        MediaPlayerService.hudSrvc.push(hUDConnectivityMessage, HUDConnectivityService.Channel.OBJECT_CHANNEL);
    }

    private static byte[] getBytesForDbFilePath() {
        File file = new File(mOwner.getDatabasePath("reconmusic.db").getPath());
        int i = (int)file.length();
        byte[] arrayOfByte = new byte[i];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.read(arrayOfByte, 0, arrayOfByte.length);
            bufferedInputStream.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
        Log.i("DBManager", "file size in bytes: " + i);
        return writeFile(arrayOfByte).getBytes();
    }

    private static String getNewDbFile(byte[] paramArrayOfbyte) {
        String str1 = mOwner.getDatabasePath("reconmusic.db").getPath();
        String str2 = str1;
        if (str1.endsWith(".db")) {
            BufferedOutputStream bufferedOutputStream = null;
            str1 = str1.replace(".db", "2.db");
            Log.i("DBManager", "new path is :" + str1);
            str2 = null;
            try {
                File file = new File(str1);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream1 = new BufferedOutputStream(fileOutputStream);
                bufferedOutputStream = bufferedOutputStream1;
            } catch (FileNotFoundException fileNotFoundException) {
                Log.e("DBManager", "caught exception: " + fileNotFoundException);
                fileNotFoundException.printStackTrace();
            }
            try {
                bufferedOutputStream.write(paramArrayOfbyte);
            } catch (IOException iOException) {
                Log.e("DBManager", "caught exception: " + iOException);
                iOException.printStackTrace();
            }
            try {
                bufferedOutputStream.flush();
            } catch (IOException iOException) {
                Log.e("DBManager", "caught exception: " + iOException);
                iOException.printStackTrace();
            }
            try {
                bufferedOutputStream.close();
                String str = str1;
            } catch (IOException iOException) {
                Log.e("DBManager", "caught exception: " + iOException);
                iOException.printStackTrace();
                str2 = str1;
            }
        }
        return str2;
    }

    private static String performChecksum(byte[] paramArrayOfbyte) {
        return FileUtils.md5(paramArrayOfbyte, 0);
    }

    public static String writeFile(byte[] paramArrayOfbyte) {
        String str1;
        String str2 = performChecksum(paramArrayOfbyte) + ".tmp";
        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp/");
        if (!file2.mkdirs())
            Log.i("DBManager", "Parent directories were not created. Possibly since they already exist.");
        str2 = file2.getAbsolutePath() + "/" + str2;
        Log.i("DBManager", "temporary path: " + str2);
        file2 = new File(str2);
        boolean bool = false;
        if (file2.exists())
            bool = file2.delete();
        if (bool) {
            Log.i("DBManager", "file was succesfully deleted");
        } else {
            Log.i("DBManager", "no file found to delete");
        }
        File file1 = new File(str2);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            try {
                bufferedOutputStream.write(paramArrayOfbyte);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                str1 = file1.getAbsolutePath();
            } catch (IOException iOException) {
                Log.w("DBManager", "caught exception closing file : " + iOException);
                iOException.printStackTrace();
                str1 = "";
            }
        } catch (FileNotFoundException fileNotFoundException) {
            Log.w("DBManager", "caught exception opening buffer: " + fileNotFoundException);
            fileNotFoundException.printStackTrace();
            str1 = "";
        }
        return str1;
    }

    private String writeXmlForRemoteChecksum() {
        ArrayList<BasicNameValuePair> arrayList = new ArrayList();
        arrayList.add(new BasicNameValuePair("localChecksum", performOwnChecksumForGoggle()));
        return XMLUtils.composeSimpleMessage("REMOTE_DB_CHECKSUM_REQUEST", "status", arrayList);
    }

    public void buildGoggleDb() {
        Log.i("DBManager", "checking goggle DB for latest local checksum");
        checkGoggleDB();
    }

    public void buildMusicDB() {
        if (this.builder == null || this.builder.getStatus() == AsyncTask.Status.FINISHED) {
            this.builder = new DBBuilderTask(this.onBuildMusicDB, mOwner);
        }
    }

    public DBBuilderTask getBuilder() {
        return this.builder;
    }

    public DBState getMusicDBBuilderState() {
        return this.builder != null ? this.builder.getState() : DBState.ERROR;
    }

    public String performOwnChecksumForGoggle() {
        return FileUtils.md5(mOwner.getDatabasePath("reconmusic.db").getPath(), true);
    }

    public void pushGoggleDB() {
        HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
        hUDConnectivityMessage.setIntentFilter("UPLOAD_DB_REQUEST");
        hUDConnectivityMessage.setRequestKey(0);
        hUDConnectivityMessage.setSender("com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService");
        hUDConnectivityMessage.setData(getBytesForDbFilePath());
        MediaPlayerService.hudSrvc.push(hUDConnectivityMessage, HUDConnectivityService.Channel.FILE_CHANNEL);
    }

    public enum DBState {
        BUILDING, CHECKING, ERROR, NOSTORAGE, READY, SENDING;

        static {
            //!!!!
            //NOSTORAGE = new DBState("NOSTORAGE", 3);
            //READY = new DBState("READY", 4);
            //ERROR = new DBState("ERROR", 5);
            //$VALUES = new DBState[] { CHECKING, BUILDING, SENDING, NOSTORAGE, READY, ERROR };
        }
    }
}
