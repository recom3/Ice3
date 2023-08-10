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

import java.util.Date;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class DBBuilderTask extends ConcurrentAsyncTask<Void, DBManager.DBState, DBManager.DBState> {
    private static final String TAG = "DBBuilderTaskSDK";

    public static boolean registered;

    static boolean running = false;

    public BroadcastReceiver bluetoothMessageReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            if (param1Intent.getAction().equals("RECON_TRANSFER_RESPONSE")) {
                TransferResponseMessage.ResponseBundle responseBundle = TransferResponseMessage.parseResponse(param1Intent.getExtras().getString("message"));
                if (responseBundle.type == TransferResponseMessage.TransferResponse.CHECK) {
                    Bundle bundle = (Bundle)responseBundle.data;
                    if (bundle.getString("file").contains("reconmusic.db")) {
                        String str2 = bundle.getString("sum");
                        String str1 = FileUtils.md5(param1Context.getDatabasePath("reconmusic.db").getPath(), true);
                        Log.d("DBBuilderTaskSDK", "Local checksum: " + str1 + " remote checksum: " + str2);
                        if (str2.equals(str1)) {
                            //!!!!
                            //DBBuilderTask.access$002(DBBuilderTask.this, DBManager.DBState.READY);
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

    DBBuilderTask(IDBBuilder paramIDBBuilder, MediaPlayerService paramMediaPlayerService) {
        if (!running) {
            this.mRespHandler = paramIDBBuilder;
            this.mSrvc = paramMediaPlayerService;
            paramMediaPlayerService.registerReceiver(this.bluetoothMessageReceiver, new IntentFilter("RECON_TRANSFER_RESPONSE"));
            registered = true;
            //!!!!
            //concurrentExecute((Object[])new Void[0]);
            concurrentExecute((Void[]) new Void[0]);
        }
    }

    protected DBManager.DBState doInBackground(Void... paramVarArgs) {
        // Byte code:
        //   0: iconst_0
        //   1: invokestatic hasStorage : (Z)Z
        //   4: ifne -> 13
        //   7: getstatic com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState.NOSTORAGE : Lcom/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState;
        //   10: astore_1
        //   11: aload_1
        //   12: areturn
        if (!FileUtils.hasStorage(false)) {
            return DBManager.DBState.NOSTORAGE;
        }
        //   13: aload_0
        //   14: iconst_1
        //   15: anewarray com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState
        //   18: dup
        //   19: iconst_0
        //   20: getstatic com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState.CHECKING : Lcom/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState;
        //   23: aastore
        //   24: invokevirtual publishProgress : ([Ljava/lang/Object;)V
        publishProgress(DBManager.DBState.CHECKING);
        //   27: new java/util/Date
        //   30: dup
        //   31: invokespecial <init> : ()V
        //   34: invokevirtual getTime : ()J

        long timeStart = new Date().getTime();

        //   37: lstore_2
        //   38: aload_0
        //   39: invokestatic currentThread : ()Ljava/lang/Thread;
        //   42: putfield buildThread : Ljava/lang/Thread;
        //   45: aload_0
        //   46: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;

        //!!!Exception
        //Caused by: java.lang.SecurityException: Permission Denial: opening provider com.reconinstruments.connect.music.MusicDBContentProvider from
        // ProcessRecord{de9616c 6512:com.recom3.snow3/u0a415} (pid=6512, uid=10415) that is not exported from uid 10416
        //Caused by: java.lang.SecurityException: Permission Denial: opening provider com.reconinstruments.connect.music.MusicDBContentProvider from ProcessRecord{e856b66 7946:com.recom3.snow3/u0a415} (pid=7946, uid=10415) that is not exported from uid 10416

        String localChecksum = null;
        String internalChecksum = null;

        try {
            MusicDBContentProvider.openOrCreateDatabase(this.mSrvc.getBaseContext());

            //   49: invokestatic openOrCreateDatabase : (Landroid/content/Context;)V
            //   52: ldc ''
            //   54: astore_1
            //   55: aload_0
            //   56: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
            //   59: invokestatic localChecksum : (Landroid/content/Context;)Ljava/lang/String;

            localChecksum = DBChecker.localChecksum(mSrvc.getBaseContext());

            //   62: astore #4
            //   64: aload #4
            //   66: astore_1
            //   67: aload_0
            //   68: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
            //   71: invokestatic internalChecksum : (Landroid/content/Context;)Ljava/lang/String;

            internalChecksum = DBChecker.internalChecksum(mSrvc.getBaseContext());

            //   74: astore #4
            //   76: aload #4
            //   78: ldc ''
            //   80: invokevirtual equals : (Ljava/lang/Object;)Z
            //   83: ifeq -> 114

            if (!localChecksum.equals(internalChecksum)) {

                //   86: ldc 'DBBuilderTaskSDK'
                //   88: ldc 'internal checksum is 0!'
                //   90: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
                //   93: pop
                //   94: getstatic com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState.NOSTORAGE : Lcom/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState;
                //   97: astore_1
                //   98: goto -> 11
                Log.d("DBBuilderTaskSDK", "internal checksum is 0!");
                return DBManager.DBState.NOSTORAGE;
            }
            else
            {
                return DBManager.DBState.READY;
            }

            //   101: astore #4
            //   103: ldc 'DBBuilderTaskSDK'
            //   105: ldc 'SQLiteException caught when reading local db. Ignoring error as it may be an old database.'
            //   107: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
            //   110: pop
            //   111: goto -> 67

            //   114: aload #4
            //   116: aload_1
            //   117: invokevirtual equals : (Ljava/lang/Object;)Z
            //   120: ifeq -> 135
            //   123: aload_0
            //   124: aload_1
            //   125: putfield checksum : Ljava/lang/String;
            //   128: getstatic com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState.READY : Lcom/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState;
            //   131: astore_1
            //   132: goto -> 11

        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
            msg = "";
        }

        //   135: aload_0
        //   136: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   139: invokestatic recreateDatabase : (Landroid/content/Context;)V
        //   142: aload_0
        //   143: invokestatic getDatabase : ()Landroid/database/sqlite/SQLiteDatabase;
        //!!!
        //MusicDBContentProvider.recreateDatabase(mSrvc.getBaseContext());

        //   146: putfield database : Landroid/database/sqlite/SQLiteDatabase;
        //   149: aload_0
        //   150: getfield database : Landroid/database/sqlite/SQLiteDatabase;
        //   153: ifnonnull -> 163
        database = MusicDBContentProvider.getDatabase();

        if(database==null)
        {
            //   156: getstatic com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState.ERROR : Lcom/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState;
            //   159: astore_1
            //   160: goto -> 11
            return DBManager.DBState.ERROR;
        }

        //   163: aload_0
        //   164: iconst_1
        //   165: anewarray com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState
        //   168: dup
        //   169: iconst_0
        //   170: getstatic com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState.BUILDING : Lcom/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState;
        //   173: aastore
        //   174: invokevirtual publishProgress : ([Ljava/lang/Object;)V
        publishProgress(DBManager.DBState.BUILDING);
        //   177: aload_0
        //   178: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   181: aload_0
        //   182: getfield database : Landroid/database/sqlite/SQLiteDatabase;
        //   185: ldc 'media'

        //   187: getstatic com/reconinstruments/connect/music/MusicDBContentProvider.mediaUri : Landroid/net/Uri;
        //   190: getstatic com/reconinstruments/connect/music/MusicDBContentProvider.songsTableColumns : [Ljava/lang/String;
        //   193: getstatic com/reconinstruments/connect/music/MusicDBContentProvider.songsTableColumnTypes : [Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;
        //   196: getstatic com/reconinstruments/connect/music/MusicDBContentProvider.songsSelection : Ljava/lang/String;
        //   199: iconst_0
        //   200: invokestatic writeTable : (Landroid/content/Context;Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Landroid/net/Uri;[Ljava/lang/String;[Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;Ljava/lang/String;Z)I
        //   203: istore #5

        int songs = DBBuilderUtils.writeTable(this.mSrvc.getBaseContext(), this.database, "media", MusicDBContentProvider.mediaUri,
                MusicDBContentProvider.songsTableColumns, MusicDBContentProvider.songsTableColumnTypes,
                MusicDBContentProvider.songsSelection, false);

        //   205: ldc 'DBBuilderTaskSDK'
        //   207: new java/lang/StringBuilder
        StringBuilder sb = new StringBuilder();
        sb.append("saved ");
        sb.append(songs);
        sb.append(" songs");

        //   210: dup
        //   211: invokespecial <init> : ()V
        //   214: ldc 'saved '
        //   216: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   219: iload #5
        //   221: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   224: ldc ' songs'
        //   226: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   229: invokevirtual toString : ()Ljava/lang/String;
        //   232: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   235: pop
        Log.d("DBBuilderTaskSDK", sb.toString());

        //   236: aload_0
        //   237: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   240: aload_0
        //   241: getfield database : Landroid/database/sqlite/SQLiteDatabase;
        //   244: ldc 'artists'
        //   246: getstatic com/reconinstruments/connect/music/MusicDBContentProvider.artistsUri : Landroid/net/Uri;
        //   249: getstatic com/reconinstruments/connect/music/MusicDBContentProvider.artistsTableColumns : [Ljava/lang/String;
        //   252: getstatic com/reconinstruments/connect/music/MusicDBContentProvider.artistsTableColumnTypes : [Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;
        //   255: aconst_null
        //   256: iconst_0
        //   257: invokestatic writeTable : (Landroid/content/Context;Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Landroid/net/Uri;[Ljava/lang/String;[Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;Ljava/lang/String;Z)I
        //   260: istore #5

        int artists = DBBuilderUtils.writeTable(this.mSrvc.getBaseContext(), this.database, "artists", MusicDBContentProvider.artistsUri,
                MusicDBContentProvider.artistsTableColumns, MusicDBContentProvider.artistsTableColumnTypes,
                null, false);

        //   262: ldc 'DBBuilderTaskSDK'
        //   264: new java/lang/StringBuilder
        //   267: dup
        //   268: invokespecial <init> : ()V
        //   271: ldc 'saved '
        //   273: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   276: iload #5
        //   278: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   281: ldc ' artists'
        //   283: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   286: invokevirtual toString : ()Ljava/lang/String;
        //   289: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   292: pop
        //   293: aload_0
        sb = new StringBuilder();
        sb.append("saved ");
        sb.append(artists);
        sb.append(" artists");
        Log.d("DBBuilderTaskSDK", sb.toString());

        //   294: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   297: aload_0
        //   298: getfield database : Landroid/database/sqlite/SQLiteDatabase;
        //   301: ldc 'playlists'
        //   303: iconst_1
        //   304: invokestatic getPlaylistsTableUri : (I)Landroid/net/Uri;
        //   307: iconst_1
        //   308: invokestatic getPlaylistsTableBuildColumns : (I)[Ljava/lang/String;
        //   311: invokestatic getPlaylistsTableColumnTypes : ()[Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;
        //   314: aconst_null
        //   315: iconst_1
        //   316: invokestatic writeTable : (Landroid/content/Context;Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Landroid/net/Uri;[Ljava/lang/String;[Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;Ljava/lang/String;Z)I

        int playlist = DBBuilderUtils.writeTable(this.mSrvc.getBaseContext(), this.database, "playlists",
                MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1),
                MusicDBPlaylistColumnsProvider.getPlaylistsTableBuildColumns(1),
                MusicDBPlaylistColumnsProvider.getPlaylistsTableColumnTypes(),
                null, true);

        if(playlist==0) {
            //   319: istore #5
            //   321: iload #5
            //   323: istore #6
            //   325: iload #5
            //   327: ifne -> 361

            //   330: iload #5
            //   332: aload_0
            //   333: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
            //   336: aload_0
            //   337: getfield database : Landroid/database/sqlite/SQLiteDatabase;
            //   340: ldc 'playlists'
            //   342: iconst_2
            //   343: invokestatic getPlaylistsTableUri : (I)Landroid/net/Uri;
            //   346: iconst_2
            //   347: invokestatic getPlaylistsTableBuildColumns : (I)[Ljava/lang/String;
            //   350: invokestatic getPlaylistsTableColumnTypes : ()[Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;
            //   353: aconst_null
            //   354: iconst_1
            //   355: invokestatic writeTable : (Landroid/content/Context;Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Landroid/net/Uri;[Ljava/lang/String;[Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;Ljava/lang/String;Z)I
            //   358: iadd
            //   359: istore #6

            playlist = DBBuilderUtils.writeTable(this.mSrvc.getBaseContext(), this.database, "playlists",
                    MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(2),
                    MusicDBPlaylistColumnsProvider.getPlaylistsTableBuildColumns(2),
                    MusicDBPlaylistColumnsProvider.getPlaylistsTableColumnTypes(),
                    null, true);
        }

        //   361: ldc 'DBBuilderTaskSDK'
        //   363: new java/lang/StringBuilder
        //   366: dup
        //   367: invokespecial <init> : ()V
        //   370: ldc 'saved '
        //   372: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   375: iload #6
        //   377: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   380: ldc ' playlists from playlist table'
        //   382: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   385: invokevirtual toString : ()Ljava/lang/String;
        //   388: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   391: pop
        //   392: aload_0

        sb = new StringBuilder();
        sb.append("saved ");
        sb.append(playlist);
        sb.append(" playlists from playlist table");
        Log.d("DBBuilderTaskSDK", sb.toString());

        //   393: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   396: aload_0
        //   397: getfield database : Landroid/database/sqlite/SQLiteDatabase;
        //   400: invokestatic writePlaylists : (Landroid/content/Context;Landroid/database/sqlite/SQLiteDatabase;)I

        int writePlaylist = DBBuilderUtils.writePlaylists(mSrvc.getBaseContext(), database);

        //   403: istore #5
        //   405: ldc 'DBBuilderTaskSDK'
        //   407: new java/lang/StringBuilder
        //   410: dup
        //   411: invokespecial <init> : ()V
        //   414: ldc 'saved '
        //   416: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   419: iload #5
        //   421: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   424: ldc ' playlists'
        //   426: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   429: invokevirtual toString : ()Ljava/lang/String;
        //   432: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I

        sb = new StringBuilder();
        sb.append("saved ");
        sb.append(writePlaylist);
        sb.append(" playlists");
        Log.d("DBBuilderTaskSDK", sb.toString());

        //   435: pop
        //   436: ldc 'DBBuilderTaskSDK'
        //   438: new java/lang/StringBuilder
        //   441: dup
        //   442: invokespecial <init> : ()V
        //   445: ldc 'copied database in '
        //   447: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   450: new java/util/Date
        //   453: dup
        //   454: invokespecial <init> : ()V
        //   457: invokevirtual getTime : ()J
        //   460: lload_2
        //   461: lsub
        //   462: ldc2_w 1000
        //   465: ldiv
        //   466: invokevirtual append : (J)Ljava/lang/StringBuilder;
        //   469: ldc ' seconds'
        //   471: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   474: invokevirtual toString : ()Ljava/lang/String;
        //   477: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   480: pop
        //   481: aload_0

        sb = new StringBuilder();
        sb.append("copied database in ");
        sb.append((new Date().getTime()-timeStart) / 1000);
        sb.append(" seconds");
        Log.d("DBBuilderTaskSDK", sb.toString());

        //   482: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   485: invokestatic openOrCreateDatabase : (Landroid/content/Context;)V
        //   488: aload_0
        //   489: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   492: invokestatic localChecksum : (Landroid/content/Context;)Ljava/lang/String;
        //   495: astore_1
        MusicDBContentProvider.openOrCreateDatabase(this.mSrvc.getBaseContext());

        //   49: invokestatic openOrCreateDatabase : (Landroid/content/Context;)V
        //   52: ldc ''
        //   54: astore_1
        //   55: aload_0
        //   56: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   59: invokestatic localChecksum : (Landroid/content/Context;)Ljava/lang/String;

        localChecksum = DBChecker.localChecksum(mSrvc.getBaseContext());

        //   496: ldc 'DBBuilderTaskSDK'
        //   498: new java/lang/StringBuilder
        //   501: dup
        //   502: invokespecial <init> : ()V
        //   505: ldc 'ReconMusicDB checksum: '
        //   507: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   510: aload_1
        //   511: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   514: ldc ' AndroidMusicDB checksum: '
        //   516: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   519: aload #4
        //   521: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   524: invokevirtual toString : ()Ljava/lang/String;
        //   527: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   530: pop

        sb = new StringBuilder();
        sb.append("ReconMusicDB checksum: ");
        sb.append(localChecksum);
        sb.append("  AndroidMusicDB checksum: ");
        sb.append(internalChecksum);
        Log.d("DBBuilderTaskSDK", sb.toString());

        //   531: aload #4
        //   533: aload_1
        //   534: invokevirtual equals : (Ljava/lang/Object;)Z
        //   537: ifne -> 587

        if(localChecksum.equals(internalChecksum))
        {
            return DBManager.DBState.NOSTORAGE.READY;
        }

        //   540: ldc 'DBBuilderTaskSDK'
        //   542: ldc 'internal and built db have checksum mismatch!'
        //   544: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I

        Log.d("DBBuilderTaskSDK", "internal and built db have checksum mismatch!");
        return DBManager.DBState.ERROR;

        //   547: pop
        //   548: getstatic com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState.ERROR : Lcom/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState;
        //   551: astore_1
        //   552: goto -> 11

        //!!!How is this code reached: exception?
        //   555: astore_1
        //   556: aload_0
        //   557: getfield mSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   560: aload_0
        //   561: getfield database : Landroid/database/sqlite/SQLiteDatabase;
        //   564: ldc 'playlists'
        //   566: iconst_1
        //   567: invokestatic getPlaylistsTableUri : (I)Landroid/net/Uri;
        //   570: iconst_2
        //   571: invokestatic getPlaylistsTableBuildColumns : (I)[Ljava/lang/String;
        //   574: invokestatic getPlaylistsTableColumnTypes : ()[Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;
        //   577: aconst_null
        //   578: iconst_1
        //   579: invokestatic writeTable : (Landroid/content/Context;Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Landroid/net/Uri;[Ljava/lang/String;[Lcom/reconinstruments/connect/music/MusicDBContentProvider$DBDataType;Ljava/lang/String;Z)I
        //   582: istore #5
        //   584: goto -> 321

        //   587: aload_0
        //   588: aload_1
        //   589: putfield checksum : Ljava/lang/String;
        //   592: getstatic com/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState.READY : Lcom/reconinstruments/mobilesdk/mediaplayer/DBManager$DBState;
        //   595: astore_1
        //   596: goto -> 11
        // Exception table:
        //   from	to	target	type
        //   55	64	101	android/database/sqlite/SQLiteException
        //   293	321	555	java/lang/IllegalArgumentException
    }

    public DBManager.DBState getState() {
        return this.state;
    }

    protected void onPostExecute(DBManager.DBState paramDBState) {
        Log.d("DBBuilderTaskSDK", "builder task finished, result: " + paramDBState.name());
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
        Log.d("DBBuilderTaskSDK", "builder updated: " + this.state.name());
        this.mRespHandler.onDBProgressUpdate(this.state);
    }
}