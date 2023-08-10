package com.recom3.connect.music;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.recom3.connect.messages.SongMessage;

/**
 * Created by Recom3 on 22/07/2022.
 */

public class MusicCursorGenerator {
    private static final String TAG = "PlaylistCursorFinder";

    private static final String musicSelection = "is_music = 1";

    private static int tempIndex;

    private static Cursor adjustCursorForAlbumSongs(Cursor paramCursor, String paramString) {
        if (checkCursor(paramCursor))
            while (true) {
                if (paramCursor.getString(0).equals(paramString) || !paramCursor.moveToNext())
                    return paramCursor;
            }
        return paramCursor;
    }

    private static Cursor adjustCursorForAllSongs(Cursor paramCursor, String paramString) {
        if (checkCursor(paramCursor))
            while (true) {
                if (paramCursor.getString(0).equals(paramString) || !paramCursor.moveToNext())
                    return paramCursor;
            }
        return paramCursor;
    }

    private static boolean checkCursor(Cursor paramCursor) {
        return (paramCursor == null || paramCursor.getCount() == 0) ? false : paramCursor.moveToFirst();
    }

    private static Cursor findPlaylistSongCursor(String paramString, String[] paramArrayOfString, Context paramContext) {
        return paramContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "title", "artist", "album", "_data", "album_id" }, paramString, paramArrayOfString, "_id LIMIT 1");
    }

    public static Cursor getAlbumCursor(Context paramContext, String paramString1, String paramString2) {
        Cursor cursor = paramContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "title", "artist", "album", "_data", "album_id" }, "is_music = 1 AND album_id = ?", new String[] { paramString2 }, "track ASC");
        cursor.moveToFirst();
        return adjustCursorForAlbumSongs(cursor, paramString1);
    }

    public static Cursor getArtistCursor(Context paramContext, String paramString1, String paramString2) {
        Cursor cursor = paramContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "title", "artist", "album", "_data", "album_id" }, "is_music = 1 AND artist_id = ?", new String[] { paramString2 }, null);
        cursor.moveToFirst();
        return adjustCursorForAlbumSongs(cursor, paramString1);
    }

    public static Cursor getNewSongListSelectionCursor(Context paramContext, String paramString1, MusicDBFrontEnd.MusicListType paramMusicListType, String paramString2) {
        Cursor cursor = null;
        if (paramMusicListType.equals(MusicDBFrontEnd.MusicListType.ALBUM_SONGS))
            cursor = getAlbumCursor(paramContext, paramString1, paramString2);
        if (paramMusicListType.equals(MusicDBFrontEnd.MusicListType.ARTIST_SONGS))
            cursor = getArtistCursor(paramContext, paramString1, paramString2);
        if (paramMusicListType.equals(MusicDBFrontEnd.MusicListType.PLAYLIST_SONGS))
            cursor = getPlaylistCursor(paramContext, paramString1, paramString2);
        if (paramMusicListType.equals(MusicDBFrontEnd.MusicListType.SONGS))
            cursor = getSongsCursor(paramContext, paramString1);
        return cursor;
    }

    public static Cursor getPlaylistCursor(Context c, String songId, String srcId) {
        /*
        int var_3 = Integer.parseInt(songId);
        Uri uri = MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1);
        return null;
        */

        int playId = Integer.parseInt(srcId);
        Uri uri = Uri.withAppendedPath(MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1), srcId.toString() + "/members");
        String[] projection2 = {"audio_id", "_id", "title", SongMessage.ATTR_ARTIST};
        Cursor cursor = c.getContentResolver().query(uri, projection2, null, null, "_id ASC");
        if (cursor == null || cursor.getCount() == 0) {
            Uri uri2 = MediaStore.Audio.Playlists.Members.getContentUri("external", playId);
            cursor = c.getContentResolver().query(uri2, projection2, null, null, "_id ASC");
            String[] columns = cursor.getColumnNames();
            for (int i = 0; i < columns.length; i++) {
                Log.e(TAG, "column1: " + columns[i]);
            }
        }
        int playlistCount = cursor.getCount();
        Cursor[] cursorHolder = new Cursor[playlistCount];
        cursor.moveToFirst();
        for (int i2 = 0; i2 < playlistCount; i2++) {
            String title = cursor.getString(2);
            String artist = cursor.getString(3);
            if (title.equals("")) {
                title = "<unknown>";
            }
            if (artist.equals("")) {
                artist = "<unknown>";
            }
            if (cursor.getString(0).equals(songId)) {
                tempIndex = i2;
            }
            String selection = "is_music = 1 AND title= ? AND " + SongMessage.ATTR_ARTIST + " = ?";
            Cursor temp = findPlaylistSongCursor(selection, new String[]{title, artist}, c);
            temp.moveToFirst();
            cursorHolder[i2] = temp;
            cursor.moveToNext();
        }
        MergeCursor finalCursor = new MergeCursor(cursorHolder);
        finalCursor.moveToPosition(tempIndex);
        tempIndex = 0;
        return finalCursor;

    }

    public static Cursor getSongsCursor(Context paramContext, String paramString) {
        return adjustCursorForAllSongs(paramContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "title", "artist", "album", "_data", "album_id" }, "is_music = 1", null, "title ASC"), paramString);
    }
}

