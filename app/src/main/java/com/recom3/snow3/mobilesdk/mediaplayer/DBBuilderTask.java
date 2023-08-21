package com.recom3.snow3.mobilesdk.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.recom3.connect.messages.TransferResponseMessage;
import com.recom3.connect.music.MusicDBContentProvider;
import com.recom3.connect.music.MusicDBPlaylistColumnsProvider;
import com.recom3.connect.util.FileUtils;
import com.recom3.snow3.mobilesdk.ConcurrentAsyncTask;
import com.recom3.snow3.mobilesdk.MediaPlayerService;
import com.recom3.snow3.mobilesdk.messages.XMLMessage;

import java.util.Date;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class DBBuilderTask extends ConcurrentAsyncTask<Void, DBManager.DBState, DBManager.DBState> {
    private static final String TAG = "DBBuilderTaskSDK";

    public static boolean registered;

    static boolean running = false;

    public BroadcastReceiver bluetoothMessageReceiver = new BroadcastReceiver() {
        /*public void onReceive(Context param1Context, Intent param1Intent) {
            if (param1Intent.getAction().equals("RECON_TRANSFER_RESPONSE")) {
                TransferResponseMessage.ResponseBundle responseBundle = TransferResponseMessage.parseResponse(param1Intent.getExtras().getString("message"));
                if (responseBundle.type == TransferResponseMessage.TransferResponse.CHECK) {
                    Bundle bundle = (Bundle)responseBundle.data;
                    if (bundle.getString("file").contains("reconmusic.db")) {
                        String str2 = bundle.getString("sum");
                        String str1 = FileUtils.md5(param1Context.getDatabasePath("reconmusic.db").getPath(), true);
                        Log.i("DBBuilderTaskSDK", "Local checksum: " + str1 + " remote checksum: " + str2);
                        if (str2.equals(str1)) {
                            //!!!!
                            //DBBuilderTask.access$002(DBBuilderTask.this, DBManager.DBState.READY);
                            DBBuilderTask.this.buildThread.interrupt();
                        }
                    }
                }
            }
        }*/
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(XMLMessage.TRANSFER_RESPONSE_MESSAGE)) {
                TransferResponseMessage.ResponseBundle resResult = TransferResponseMessage.parseResponse(intent.getExtras().getString("message"));
                if (resResult.type == TransferResponseMessage.TransferResponse.CHECK) {
                    Bundle sumInfo = (Bundle) resResult.data;
                    if (sumInfo.getString("file").contains(MusicDBContentProvider.DATABASE_NAME)) {
                        String remoteSum = sumInfo.getString("sum");
                        String localPath = context.getDatabasePath(MusicDBContentProvider.DATABASE_NAME).getPath();
                        String localSum = FileUtils.md5(localPath, true);
                        Log.i(DBBuilderTask.TAG, "Local checksum: " + localSum + " remote checksum: " + remoteSum);
                        if (remoteSum.equals(localSum)) {
                            DBBuilderTask.this.state = DBManager.DBState.READY;
                            DBBuilderTask.this.buildThread.interrupt();
                        }
                    }
                }
            }
        }

    };

    Thread buildThread;

    private String checksum;

    SQLiteDatabase database;

    IDBBuilder mRespHandler;

    MediaPlayerService mSrvc;

    private DBManager.DBState state = DBManager.DBState.CHECKING;

    static {
        registered = false;
    }

    DBBuilderTask(IDBBuilder respHandler, MediaPlayerService service) {
        /*
        if (!running) {
            this.mRespHandler = paramIDBBuilder;
            this.mSrvc = paramMediaPlayerService;
            paramMediaPlayerService.registerReceiver(this.bluetoothMessageReceiver, new IntentFilter("RECON_TRANSFER_RESPONSE"));
            registered = true;
            //!!!!
            //concurrentExecute((Object[])new Void[0]);
            concurrentExecute((Void[]) new Void[0]);
        }
        */

        if (!running) {
            this.mRespHandler = respHandler;
            this.mSrvc = service;
            service.registerReceiver(this.bluetoothMessageReceiver, new IntentFilter(XMLMessage.TRANSFER_RESPONSE_MESSAGE));
            registered = true;
            concurrentExecute(new Void[0]);
        }

    }

    protected DBManager.DBState doInBackground(Void... paramVarArgs) {

        int numPlaylists;

        if (!FileUtils.hasStorage(false)) {
            return DBManager.DBState.NOSTORAGE;
        }

        //!recom3
        //publishProgress(DBManager.DBState.CHECKING);
        publishProgress(new DBManager.DBState[]{DBManager.DBState.CHECKING});

        long startTime = new Date().getTime();
        this.buildThread = Thread.currentThread();

        String localChecksum = null;
        String internalMusicChecksum = null;

        //!recom3
        //MusicDBContentProvider.openOrCreateDatabase(this.mSrvc.getBaseContext());
        MusicDBContentProvider.openOrCreateDatabase(this.mSrvc);

        try {

            //localChecksum = DBChecker.localChecksum(mSrvc.getBaseContext());
            localChecksum = DBChecker.localChecksum(this.mSrvc);
        }
        catch (Exception ex)
        {
            Log.i(TAG, "SQLiteException caught when reading local db. Ignoring error as it may be an old database.");
        }

        //internalChecksum = DBChecker.internalChecksum(mSrvc.getBaseContext());
        internalMusicChecksum = DBChecker.internalChecksum(this.mSrvc);

        if (internalMusicChecksum.equals("")) {
            Log.i(TAG, "internal checksum is 0!");
            return DBManager.DBState.NOSTORAGE;
        }
        else if (!localChecksum.equals(internalMusicChecksum)) {

            Log.i("DBBuilderTaskSDK", "internal checksum is 0!");
            return DBManager.DBState.NOSTORAGE;
        }

        MusicDBContentProvider.recreateDatabase(this.mSrvc);

        database = MusicDBContentProvider.getDatabase();

        if(database==null)
        {
            return DBManager.DBState.ERROR;
        }

        //publishProgress(DBManager.DBState.BUILDING);
        publishProgress(new DBManager.DBState[]{DBManager.DBState.BUILDING});

        //int songs = DBBuilderUtils.writeTable(this.mSrvc, this.database, "media", MusicDBContentProvider.mediaUri,
        //        MusicDBContentProvider.songsTableColumns, MusicDBContentProvider.songsTableColumnTypes,
        //        MusicDBContentProvider.songsSelection, false);

        int numSongs = DBBuilderUtils.writeTable(this.mSrvc, this.database, MusicDBContentProvider.MEDIA_TABLE, MusicDBContentProvider.mediaUri, MusicDBContentProvider.songsTableColumns, MusicDBContentProvider.songsTableColumnTypes, MusicDBContentProvider.songsSelection, false);
        Log.i(TAG, "saved " + numSongs + " songs");

        //int artists = DBBuilderUtils.writeTable(this.mSrvc.getBaseContext(), this.database, "artists", MusicDBContentProvider.artistsUri,
        //        MusicDBContentProvider.artistsTableColumns, MusicDBContentProvider.artistsTableColumnTypes,
        //        null, false);
        int numArtists = DBBuilderUtils.writeTable(this.mSrvc, this.database, MusicDBContentProvider.ARTISTS_TABLE, MusicDBContentProvider.artistsUri, MusicDBContentProvider.artistsTableColumns, MusicDBContentProvider.artistsTableColumnTypes, null, false);
        Log.i(TAG, "saved " + numArtists + " artists");

        /*
        int playlist = DBBuilderUtils.writeTable(this.mSrvc.getBaseContext(), this.database, "playlists",
                MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1),
                MusicDBPlaylistColumnsProvider.getPlaylistsTableBuildColumns(1),
                MusicDBPlaylistColumnsProvider.getPlaylistsTableColumnTypes(),
                null, true);

        if(playlist==0) {

            playlist = DBBuilderUtils.writeTable(this.mSrvc.getBaseContext(), this.database, "playlists",
                    MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(2),
                    MusicDBPlaylistColumnsProvider.getPlaylistsTableBuildColumns(2),
                    MusicDBPlaylistColumnsProvider.getPlaylistsTableColumnTypes(),
                    null, true);
        }
        */

        try {
            numPlaylists = DBBuilderUtils.writeTable(this.mSrvc, this.database, MusicDBContentProvider.PLAYLISTS_TABLE, MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1), MusicDBPlaylistColumnsProvider.getPlaylistsTableBuildColumns(1), MusicDBPlaylistColumnsProvider.getPlaylistsTableColumnTypes(), null, true);
        } catch (IllegalArgumentException e2) {
            numPlaylists = DBBuilderUtils.writeTable(this.mSrvc, this.database, MusicDBContentProvider.PLAYLISTS_TABLE, MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1), MusicDBPlaylistColumnsProvider.getPlaylistsTableBuildColumns(2), MusicDBPlaylistColumnsProvider.getPlaylistsTableColumnTypes(), null, true);
        }
        if (numPlaylists == 0) {
            numPlaylists += DBBuilderUtils.writeTable(this.mSrvc, this.database, MusicDBContentProvider.PLAYLISTS_TABLE, MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(2), MusicDBPlaylistColumnsProvider.getPlaylistsTableBuildColumns(2), MusicDBPlaylistColumnsProvider.getPlaylistsTableColumnTypes(), null, true);
        }
        Log.i(TAG, "saved " + numPlaylists + " playlists from playlist table");
        int numPlaylistTables = DBBuilderUtils.writePlaylists(this.mSrvc, this.database);
        Log.i(TAG, "saved " + numPlaylistTables + " playlists");
        Log.i(TAG, "copied database in " + ((new Date().getTime() - startTime) / 1000) + " seconds");
        MusicDBContentProvider.openOrCreateDatabase(this.mSrvc);
        String localMusicChecksum2 = DBChecker.localChecksum(this.mSrvc);
        Log.i(TAG, "ReconMusicDB checksum: " + localMusicChecksum2 + " AndroidMusicDB checksum: " + internalMusicChecksum);
        if (!internalMusicChecksum.equals(localMusicChecksum2)) {
            Log.e(TAG, "internal and built db have checksum mismatch!");
            return DBManager.DBState.ERROR;
        }
        this.checksum = localMusicChecksum2;
        return DBManager.DBState.READY;
    }

    public DBManager.DBState getState() {
        return this.state;
    }

    protected void onPostExecute(DBManager.DBState paramDBState) {
        Log.i("DBBuilderTaskSDK", "builder task finished, result: " + paramDBState.name());
        this.state = paramDBState;
        if (registered) {
            this.mSrvc.unregisterReceiver(this.bluetoothMessageReceiver);
            registered = false;
        }
        running = false;
        this.mRespHandler.onDBBuild(this.state);
    }

    protected void onProgressUpdate(DBManager.DBState... paramVarArgs) {
        this.state = paramVarArgs[0];
        Log.i("DBBuilderTaskSDK", "builder updated: " + this.state.name());
        this.mRespHandler.onDBProgressUpdate(this.state);
    }
}