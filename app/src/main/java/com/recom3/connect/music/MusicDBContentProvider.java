package com.recom3.connect.music;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.recom3.connect.util.FileUtils;

import java.io.File;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class MusicDBContentProvider extends ContentProvider {
    public static final String ARTISTS_TABLE = "artists";

    //private static final String AUTHORITY = "com.reconinstruments.connect.music";
    private static final String AUTHORITY = "com.recom3.connect.music";

    public static final Uri CONTENT_URI;

    public static final Uri CONTENT_URI_MOD_LIVE;

    public static final String DATABASE_NAME = "reconmusic.db";

    public static final String MEDIA_TABLE = "media";

    public static final String PLAYLISTS_TABLE = "playlists";

    public static final String PLAYLIST_TABLE = "playlist";

    private static final String TAG = "MusicDBContentProvider";

    public static String[] albumsProjection;

    public static Uri albumsUri;

    public static String[] artistsTableColumnProps;

    public static DBDataType[] artistsTableColumnTypes;

    public static String[] artistsTableColumns;

    public static Uri artistsUri;

    static Context cpContext;

    private static SQLiteDatabase database;

    public static Uri mediaUri;

    public static String songsOrder;

    public static String songsSelection;

    public static String[] songsTableColumnProps;

    public static DBDataType[] songsTableColumnTypes;

    public static String[] songsTableColumns = new String[] { "_id", "artist", "title", "album", "artist_id", "album_id", "duration", "track" };

    private static final UriMatcher uriMatcher;

    public boolean emptyDB = true;

    BroadcastReceiver fileTransferReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {}
    };

    static {
        songsTableColumnTypes = new DBDataType[] { DBDataType.INTEGER, DBDataType.TEXT, DBDataType.TEXT, DBDataType.TEXT, DBDataType.INTEGER, DBDataType.INTEGER, DBDataType.INTEGER, DBDataType.INTEGER };
        songsTableColumnProps = new String[] { "", "COLLATE NOCASE", "COLLATE NOCASE", "COLLATE NOCASE", "", "", "", "" };
        artistsTableColumns = new String[] { "_id", "artist" };
        artistsTableColumnTypes = new DBDataType[] { DBDataType.INTEGER, DBDataType.TEXT };
        artistsTableColumnProps = new String[] { "", "COLLATE NOCASE" };
        albumsProjection = new String[] { "album_id", "_id", "album", "artist_id", "artist" };
        songsSelection = "is_music = 1";
        songsOrder = "title ASC";
        mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        artistsUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        albumsUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        CONTENT_URI = Uri.parse("content://com.recom3.connect.music");
        CONTENT_URI_MOD_LIVE = Uri.parse("content://com.reconinstruments.modlivemobile.music");
        uriMatcher = new UriMatcher(-1);
        uriMatcher.addURI("com.recom3.connect.music", "media", TableCode.media.ordinal());
        uriMatcher.addURI("com.recom3.connect.music", "artists", TableCode.artists.ordinal());
        uriMatcher.addURI("com.recom3.connect.music", "playlists", TableCode.playlists.ordinal());
        uriMatcher.addURI("com.recom3.connect.music", "playlist/#", TableCode.playlist.ordinal());
    }

    public static void createTable(SQLiteDatabase paramSQLiteDatabase, String paramString, String[] paramArrayOfString1, DBDataType[] paramArrayOfDBDataType, String[] paramArrayOfString2) {
        paramString = "CREATE TABLE IF NOT EXISTS " + paramString + " (";
        for (byte b = 0; b < paramArrayOfString1.length; b++) {
            String str = paramString + paramArrayOfString1[b] + " " + paramArrayOfDBDataType[b].name() + " " + paramArrayOfString2[b];
            paramString = str;
            if (b != paramArrayOfString1.length - 1)
                paramString = str + ",";
        }
        paramSQLiteDatabase.execSQL(paramString + ");");
    }

    public static void createTables(SQLiteDatabase paramSQLiteDatabase) {
        createTable(paramSQLiteDatabase, "media", songsTableColumns, songsTableColumnTypes, songsTableColumnProps);
        createTable(paramSQLiteDatabase, "artists", artistsTableColumns, artistsTableColumnTypes, artistsTableColumnProps);
        createTable(paramSQLiteDatabase, "playlists", MusicDBPlaylistColumnsProvider.reconPlaylistsTableColumns, MusicDBPlaylistColumnsProvider.reconPlaylistsTableColumnsTypes, MusicDBPlaylistColumnsProvider.reconPlaylistsTableColumnsProps);
    }

    public static void deleteTable(SQLiteDatabase paramSQLiteDatabase, String paramString) {
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + paramString);
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }

    public static void openOrCreateDatabase(Context paramContext) {
        paramContext.getContentResolver().update(CONTENT_URI, new ContentValues(), "where", null);
    }

    public static void openOrCreateDatabaseMODLive(Context paramContext) {
        paramContext.getContentResolver().update(CONTENT_URI_MOD_LIVE, new ContentValues(), "where", null);
    }

    public static void recreateDatabase(Context paramContext) {
        paramContext.getContentResolver().delete(CONTENT_URI, "where", null);
    }

    public boolean DBExists() {
        return cpContext.getDatabasePath("reconmusic.db").exists();
    }

    public int delete(Uri paramUri, String paramString, String[] paramArrayOfString) {
        recreateDatabase();
        return 0;
    }

    public SQLiteDatabase getDB() {
        return database;
    }

    public File getDBFile() {
        return cpContext.getDatabasePath("reconmusic.db");
    }

    public String getFullDBPath() {
        return cpContext.getDatabasePath("reconmusic.db").getPath();
    }

    public String getType(Uri paramUri) {
        return null;
    }

    public Uri insert(Uri paramUri, ContentValues paramContentValues) {
        Log.d("MusicDBContentProvider", paramContentValues.toString());
        TableCode tableCode = TableCode.values()[uriMatcher.match(paramUri)];
        String str1 = tableCode.name();
        String str2 = str1;
        if (tableCode == TableCode.playlist) {
            str2 = paramUri.getLastPathSegment();
            str2 = str1 + str2;
        }
        long l = database.insert(str2, null, paramContentValues);
        if (l > 0L) {
            paramUri = ContentUris.withAppendedId(CONTENT_URI, l);
            getContext().getContentResolver().notifyChange(paramUri, null);
            return paramUri;
        }
        throw new SQLException("Failed to insert row into " + paramUri);
    }

    public boolean onCreate() {
        cpContext = getContext();
        return openOrCreateDatabase();
    }

    public boolean openOrCreateDatabase() {
        try {
            boolean bool;
            String str;
            if (database != null)
                database.close();
            cpContext.getDatabasePath("reconmusic.db").getParentFile().mkdirs();
            database = SQLiteDatabase.openDatabase(getFullDBPath(), null, 268435472);
            createTables(database);
            Cursor cursor = database.query("media", null, null, null, null, null, null);
            if (cursor.getCount() == 0) {
                bool = true;
            } else {
                bool = false;
            }
            this.emptyDB = bool;
            StringBuilder stringBuilder2 = new StringBuilder();
            //this();
            StringBuilder stringBuilder1 = stringBuilder2.append("opened database! num songs = ").append(cursor.getCount()).append("");
            if (this.emptyDB) {
                str = ", database empty!";
            } else {
                str = "";
            }
            Log.d("MusicDBContentProvider", stringBuilder1.append(str).toString());
            if (database == null)
                return false;
        } catch (Exception exception) {
            Log.e("MusicDBContentProvider", "couldn't open database", exception);
            return false;
        }
        return true;
    }

    public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2) {
        if (!FileUtils.hasStorage(false)) {
            Log.d("MusicDBContentProvider", "Can't query music db, storage not available");
            return null;
        }
        if (database == null) {
            Log.d("MusicDBContentProvider", "Can't query music db, database is null");
            return null;
        }
        if (!database.isOpen()) {
            Log.d("MusicDBContentProvider", "Can't query music db, database is closed");
            return null;
        }
        if (this.emptyDB)
            Log.d("MusicDBContentProvider", "Can't query music db, no songs");
        Log.d("MusicDBContentProvider", "database page size: " + database.getPageSize());
        int i = uriMatcher.match(paramUri);
        Log.d("MusicDBContentProvider", "musicdb uri code: " + i);
        if (i == -1)
            return (Cursor)new MatrixCursor(new String[0]);
        TableCode tableCode = TableCode.values()[i];
        String str2 = tableCode.name();
        if (tableCode == TableCode.playlist) {
            i = 1;
        } else {
            i = 0;
        }
        String str1 = str2;
        if (i != 0) {
            str1 = paramUri.getLastPathSegment();
            str1 = str2 + str1;
        }
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables(str1);
        i = paramArrayOfString1.length;
        Cursor cursor = sQLiteQueryBuilder.query(database, paramArrayOfString1, paramString1, paramArrayOfString2, paramArrayOfString1[0], null, paramString2);
        cursor.setNotificationUri(getContext().getContentResolver(), paramUri);
        return cursor;
    }

    public void recreateDatabase() {
        if (database != null)
            database.close();
        File file = cpContext.getDatabasePath("reconmusic.db");
        if (file.exists()) {
            file.delete();
            Log.d("MusicDBContentProvider", "deleted old database, recreating");
        }
        openOrCreateDatabase();
    }

    public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString) {
        openOrCreateDatabase();
        return 0;
    }

    public enum DBDataType {
        INTEGER, TEXT;

        static {

        }
    }

    enum TableCode {
        media, playlist, playlists, artists;

        static {
            //!!!!
            //playlist = new TableCode("playlist", 3);
            //$VALUES = new TableCode[] { media, artists, playlists, playlist };
        }
    }
}