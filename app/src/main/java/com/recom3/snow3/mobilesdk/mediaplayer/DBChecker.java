package com.recom3.snow3.mobilesdk.mediaplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.recom3.connect.music.MusicDBContentProvider;
import com.recom3.connect.music.MusicDBPlaylistColumnsProvider;
import com.recom3.connect.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Recom3 on 17/07/2022.
 */

public class DBChecker {
    private static final String TAG = "MusicDBChecker";

    static String orderById = "_id ASC";

    private static void cursorToByteStream(Cursor paramCursor, ByteArrayOutputStream paramByteArrayOutputStream) {
        if (initCursor(paramCursor)) {
            Exception exception = null;
            while (true) {
                byte b = 0;
                try {
                    while (true) {
                        String str = "";
                        int i = paramCursor.getColumnCount();
                        if (b < i) {
                            try {
                                String str1 = paramCursor.getString(b);
                            } catch (Exception exception1) {
                                exception = exception1;
                            }
                        } else {
                            if (!paramCursor.moveToNext()) {
                                paramCursor.close();
                                return;
                            }
                            continue;
                        }

                        if (exception == null)
                            str = "";
                        byte[] arrayOfByte = str.getBytes();
                        if (arrayOfByte != null)
                            paramByteArrayOutputStream.write(arrayOfByte);
                        b++;

                        break;
                    }
                    //!!!
                    //break;
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }
                return;
            }
        }
    }

    static boolean initCursor(Cursor paramCursor) {
        boolean bool = false;
        if (paramCursor != null) {
            paramCursor.moveToFirst();
            if (paramCursor.getCount() == 0 || paramCursor.isAfterLast()) {
                paramCursor.close();
                return bool;
            }
            bool = true;
        }
        return bool;
    }

    static String internalChecksum(Context paramContext) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        cursorToByteStream(paramContext.getContentResolver().query(MusicDBContentProvider.mediaUri, MusicDBContentProvider.songsTableColumns, MusicDBContentProvider.songsSelection, null, orderById), byteArrayOutputStream);
        Cursor cursor = paramContext.getContentResolver().query(MusicDBContentProvider.artistsUri, MusicDBContentProvider.artistsTableColumns, null, null, orderById);
        cursorToByteStream(cursor, byteArrayOutputStream);
        readInternalPlaylists(paramContext, byteArrayOutputStream, readInternalPlaylistTables(paramContext, byteArrayOutputStream));
        try {
            if (!cursor.isClosed())
                cursor.close();
        } catch (NullPointerException nullPointerException) {
            Log.e("MusicDBChecker", "cursor is null");
        }
        String str = FileUtils.md5(byteArrayOutputStream.toByteArray(), 0);
        Log.i("MusicDBChecker", "internal db md5 checksum: " + str);
        return str;
    }

    static String localChecksum(Context paramContext) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Uri uri = Uri.withAppendedPath(MusicDBContentProvider.CONTENT_URI, "media");
        cursorToByteStream(paramContext.getContentResolver().query(uri, MusicDBContentProvider.songsTableColumns, null, null, orderById), byteArrayOutputStream);
        uri = Uri.withAppendedPath(MusicDBContentProvider.CONTENT_URI, "artists");
        cursorToByteStream(paramContext.getContentResolver().query(uri, MusicDBContentProvider.artistsTableColumns, null, null, orderById), byteArrayOutputStream);
        uri = Uri.withAppendedPath(MusicDBContentProvider.CONTENT_URI, "playlists");
        cursorToByteStream(paramContext.getContentResolver().query(uri, MusicDBPlaylistColumnsProvider.reconPlaylistTableChecksumColumns, null, null, MusicDBPlaylistColumnsProvider.reconPlaylistOrder), byteArrayOutputStream);
        readLocalPlaylists(paramContext, byteArrayOutputStream);
        String str = FileUtils.md5(byteArrayOutputStream.toByteArray(), 0);
        Log.i("MusicDBChecker", "local db md5 checksum: " + str);
        return str;
    }

    private static Uri readInternalPlaylistTables(Context paramContext, ByteArrayOutputStream paramByteArrayOutputStream) {
        Uri uri2;
        Uri uri1 = MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1);
        Cursor cursor = null;
        try {
            cursor = paramContext.getContentResolver().query(uri1, MusicDBPlaylistColumnsProvider.getPlaylistsTableChecksumColumns(1), null, null, null);
            if (cursor == null) {
                uri2 = MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(2);
                Cursor cursor1 = paramContext.getContentResolver().query(uri2, MusicDBPlaylistColumnsProvider.getPlaylistsTableChecksumColumns(2), null, null, null);
                int j = cursor1.getCount();
                cursorToByteStream(cursor1, paramByteArrayOutputStream);
                Log.i("MusicDBChecker", "reading " + j + " internal playlists from external content, since google content was empty.");
                return uri2;
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            cursor = paramContext.getContentResolver().query(uri1, MusicDBPlaylistColumnsProvider.getPlaylistsTableChecksumColumns(2), null, null, null);
            if (cursor == null) {
                uri2 = MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(2);
                Cursor cursor1 = paramContext.getContentResolver().query(uri2, MusicDBPlaylistColumnsProvider.getPlaylistsTableChecksumColumns(2), null, null, null);
                int j = cursor1.getCount();
                cursorToByteStream(cursor1, paramByteArrayOutputStream);
                Log.i("MusicDBChecker", "reading " + j + " internal playlists from external content, since google content was empty.");
                return uri2;
            }
        }
        //!!!
        int i = cursor.getCount();//uri2.getCount();
        //cursorToByteStream((Cursor)uri2, paramByteArrayOutputStream);
        cursorToByteStream((Cursor)cursor, paramByteArrayOutputStream);
        Log.i("MusicDBChecker", "reading " + i + " internal playlists from google content");
        return uri1;
    }

    private static void readInternalPlaylists(Context paramContext, ByteArrayOutputStream paramByteArrayOutputStream, Uri paramUri) {
        Cursor cursor = paramContext.getContentResolver().query(paramUri, new String[] { "_id" }, null, null, null);
        if (initCursor(cursor))
            readSingleInternalPlaylist(paramContext, paramByteArrayOutputStream, cursor, paramUri);
        cursor.close();
    }

    private static void readLocalPlaylists(Context paramContext, ByteArrayOutputStream paramByteArrayOutputStream) {
        Uri uri = Uri.withAppendedPath(MusicDBContentProvider.CONTENT_URI, "playlists");
        ContentResolver contentResolver = paramContext.getContentResolver();
        String str = MusicDBPlaylistColumnsProvider.reconPlaylistOrder;
        Cursor cursor = contentResolver.query(uri, new String[] { "_id" }, null, null, str);
        if (initCursor(cursor))
            while (true) {
                String str1 = cursor.getString(0);
                Uri uri1 = Uri.withAppendedPath(MusicDBContentProvider.CONTENT_URI, "playlist/" + str1);
                try {
                    Cursor cursor1 = paramContext.getContentResolver().query(uri1, MusicDBPlaylistColumnsProvider.reconPlaylistSongsChecksumColumns, null, null, MusicDBPlaylistColumnsProvider.reconPlaylistOrder);
                    cursor1.moveToFirst();
                    cursorToByteStream(cursor1, paramByteArrayOutputStream);
                } catch (SQLiteException sQLiteException) {
                    Log.e("MusicDBChecker", "Error reading playlist table", (Throwable)sQLiteException);
                }
                if (!cursor.moveToNext()) {
                    cursor.close();
                    return;
                }
            }
    }

    private static void readSingleInternalPlaylist(Context paramContext, ByteArrayOutputStream paramByteArrayOutputStream, Cursor paramCursor, Uri paramUri) {
        while (true) {
            Uri uri;
            int i = paramCursor.getInt(0);
            if (paramUri.equals(MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1))) {
                uri = Uri.withAppendedPath(paramUri, i + "/members");
            } else {
                uri = MediaStore.Audio.Playlists.Members.getContentUri("external", i);
            }
            cursorToByteStream(paramContext.getContentResolver().query(uri, MusicDBPlaylistColumnsProvider.getPlaylistSongsChecksumColumns(1), null, null, null), paramByteArrayOutputStream);
            if (!paramCursor.moveToNext())
                return;
        }
    }

    private static Cursor testCursor(Context paramContext, Uri paramUri) {
        Cursor cursor = paramContext.getContentResolver().query(paramUri, new String[] { "_id", "audio_id", "artist", "title", "SongId" }, null, null, null);
        int i = cursor.getCount();
        Log.i("MusicDBChecker", "reading all " + i + " internal playlist columns from google content");
        cursor.getColumnNames();
        return cursor;
    }
}

