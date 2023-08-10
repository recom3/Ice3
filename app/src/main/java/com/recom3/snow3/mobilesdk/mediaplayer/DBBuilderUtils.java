package com.recom3.snow3.mobilesdk.mediaplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.recom3.connect.music.MusicDBContentProvider;
import com.recom3.connect.music.MusicDBPlaylistColumnsProvider;

/**
 * Created by Recom3 on 17/07/2022.
 */

public class DBBuilderUtils {
    private static final String TAG = "BuilderUtils";

    private static void createTableForPlaylist(SQLiteDatabase paramSQLiteDatabase, int paramInt) {
        MusicDBContentProvider.createTable(paramSQLiteDatabase, "playlist" + paramInt, MusicDBPlaylistColumnsProvider.reconPlaylistSongsColumns, MusicDBPlaylistColumnsProvider.reconPlaylistSongsColumnsTypes, MusicDBPlaylistColumnsProvider.reconPlaylistSongsColumnsProps);
    }

    static int writePlaylists(Context paramContext, SQLiteDatabase paramSQLiteDatabase) {
        String[] arrayOfString = new String[1];
        arrayOfString[0] = "_id";
        int i = 0;
        Uri uri = MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1);
        Cursor cursor = paramContext.getContentResolver().query(uri, arrayOfString, null, null, null);
        if (DBChecker.initCursor(cursor)) {
            i = 0 + writeTableToReconDB(paramContext, cursor, paramSQLiteDatabase, uri);
            Log.d("BuilderUtils", "writing playlists from google content");
        }
        int j = i;
        if (i == 0) {
            uri = MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(2);
            Cursor cursor1 = paramContext.getContentResolver().query(uri, arrayOfString, null, null, null);
            cursor = cursor1;
            j = i;
            if (DBChecker.initCursor(cursor1)) {
                j = i + writeTableToReconDB(paramContext, cursor1, paramSQLiteDatabase, uri);
                Log.d("BuilderUtils", "writing playlists from external content");
                cursor = cursor1;
            }
        }
        cursor.close();
        return j;
    }

    static int writeTable(Context paramContext, SQLiteDatabase paramSQLiteDatabase, String paramString1, Uri paramUri, String[] paramArrayOfString, MusicDBContentProvider.DBDataType[] paramArrayOfDBDataType, String paramString2, boolean paramBoolean) {
        return writeTableBulk(paramContext, paramSQLiteDatabase, paramString1, paramUri, paramArrayOfString, paramArrayOfDBDataType, paramString2, paramBoolean);
    }

    private static int writeTableBulk(Context paramContext, SQLiteDatabase paramSQLiteDatabase, String paramString1, Uri paramUri, String[] paramArrayOfString, MusicDBContentProvider.DBDataType[] paramArrayOfDBDataType, String paramString2, boolean paramBoolean) {
        int i = 0;
        Cursor cursor = paramContext.getContentResolver().query(paramUri, paramArrayOfString, paramString2, null, null);
        if (!DBChecker.initCursor(cursor))
            return 0;
        DatabaseUtils.InsertHelper insertHelper = new DatabaseUtils.InsertHelper(paramSQLiteDatabase, paramString1);
        while (true) {
            insertHelper.prepareForInsert();
            int j = 0;
            while (j < paramArrayOfString.length) {
                switch (paramArrayOfDBDataType[j]) {
                    case INTEGER:
                        insertHelper.bind(j + 1, cursor.getInt(j));
                        j++;
                        break;
                    case TEXT:
                        insertHelper.bind(j + 1, cursor.getString(j));
                        j++;
                        break;
                }
            }
            if (paramBoolean) {
                insertHelper.bind(paramArrayOfString.length + 1, i + 1);
                Log.d("BuilderUtils", "recon_playlist_order: " + (i + 1));
            }
            insertHelper.execute();
            j = i + 1;
            i = j;
            if (!cursor.moveToNext()) {
                cursor.close();
                return j;
            }
        }
    }

    private static int writeTableToReconDB(Context paramContext, Cursor paramCursor, SQLiteDatabase paramSQLiteDatabase, Uri paramUri) {
        int i = 0;
        while (true) {
            Uri uri;
            int j = paramCursor.getInt(0);
            MusicDBContentProvider.deleteTable(paramSQLiteDatabase, "playlist" + j);
            createTableForPlaylist(paramSQLiteDatabase, j);
            if (paramUri.equals(MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1))) {
                uri = Uri.withAppendedPath(paramUri, j + "/members");
            } else {
                uri = MediaStore.Audio.Playlists.Members.getContentUri("external", j);
            }
            int k = writeTable(paramContext, paramSQLiteDatabase, "playlist" + j, uri, MusicDBPlaylistColumnsProvider.getPlaylistSongsBuildColumns(1), MusicDBPlaylistColumnsProvider.getPlaylistSongsBuildColumnsTypes(), null, true);
            Log.d("BuilderUtils", "saved " + k + " songs to table playlist" + j + " from uri " + uri.toString());
            j = i + 1;
            i = j;
            if (!paramCursor.moveToNext())
                return j;
        }
    }
}

