package com.recom3.connect.music;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class MusicDBPlaylistColumnsProvider {
    private static Uri externalContentUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

    private static Uri googleContentUri = Uri.parse("content://com.google.android.music.MusicContent/playlists");

    static String reconPlaylistName;

    public static String reconPlaylistOrder;

    static String reconPlaylistSongTitle;

    public static String[] reconPlaylistSongsChecksumColumns;

    public static String[] reconPlaylistSongsColumns;

    public static String[] reconPlaylistSongsColumnsProps;

    public static MusicDBContentProvider.DBDataType[] reconPlaylistSongsColumnsTypes;

    public static String reconPlaylistSongsOrderById;

    public static String[] reconPlaylistSongsSelection = new String[] { "_id", "recon_audio_id", "recon_title", "recon_artist" };

    public static String[] reconPlaylistTableChecksumColumns;

    public static String[] reconPlaylistTableSelection = new String[] { "_id", "recon_name" };

    public static String[] reconPlaylistsTableColumns;

    public static String[] reconPlaylistsTableColumnsProps;

    public static MusicDBContentProvider.DBDataType[] reconPlaylistsTableColumnsTypes;

    static {
        reconPlaylistSongsOrderById = "_id ASC";
        reconPlaylistName = "recon_name";
        reconPlaylistTableChecksumColumns = new String[] { "_id", reconPlaylistName };
        reconPlaylistsTableColumns = new String[] { "_id", reconPlaylistName, "recon_playlist_order" };
        reconPlaylistsTableColumnsTypes = new MusicDBContentProvider.DBDataType[] { MusicDBContentProvider.DBDataType.INTEGER, MusicDBContentProvider.DBDataType.TEXT, MusicDBContentProvider.DBDataType.INTEGER };
        reconPlaylistsTableColumnsProps = new String[] { "", "COLLATE NOCASE", "" };
        reconPlaylistSongTitle = "recon_title";
        reconPlaylistSongsColumns = new String[] { "_id", "recon_audio_id", reconPlaylistSongTitle, "recon_artist", "recon_playlist_order" };
        reconPlaylistSongsColumnsTypes = new MusicDBContentProvider.DBDataType[] { MusicDBContentProvider.DBDataType.INTEGER, MusicDBContentProvider.DBDataType.INTEGER, MusicDBContentProvider.DBDataType.TEXT, MusicDBContentProvider.DBDataType.TEXT, MusicDBContentProvider.DBDataType.INTEGER };
        reconPlaylistSongsColumnsProps = new String[] { "", "", "COLLATE NOCASE", "COLLATE NOCASE", "" };
        reconPlaylistOrder = "recon_playlist_order ASC";
        reconPlaylistSongsChecksumColumns = new String[] { "_id", "recon_audio_id", reconPlaylistSongTitle, "recon_artist" };
    }

    public static String[] getPlaylistSongsBuildColumns(int paramInt) {
        if (paramInt == 1) {
            String[] arrayOfString = new String[4];
            arrayOfString[0] = "_id";
            arrayOfString[1] = "audio_id";
            arrayOfString[2] = "title";
            arrayOfString[3] = "artist";
            return arrayOfString;
        }
        if (paramInt == 2) {
            String[] arrayOfString = new String[4];
            arrayOfString[0] = "_id";
            arrayOfString[1] = "audio_id";
            arrayOfString[2] = "title";
            arrayOfString[3] = "artist";
            return arrayOfString;
        }
        return null;
    }

    public static MusicDBContentProvider.DBDataType[] getPlaylistSongsBuildColumnsTypes() {
        return new MusicDBContentProvider.DBDataType[] { MusicDBContentProvider.DBDataType.INTEGER, MusicDBContentProvider.DBDataType.INTEGER, MusicDBContentProvider.DBDataType.TEXT, MusicDBContentProvider.DBDataType.TEXT };
    }

    public static String[] getPlaylistSongsChecksumColumns(int paramInt) {
        return new String[] { "_id", "audio_id", "title", "artist" };
    }

    public static String[] getPlaylistsTableBuildColumns(int paramInt) {
        if (paramInt == 1) {
            String[] arrayOfString = new String[2];
            arrayOfString[0] = "_id";
            arrayOfString[1] = "playlist_name";
            return arrayOfString;
        }
        if (paramInt == 2) {
            String[] arrayOfString = new String[2];
            arrayOfString[0] = "_id";
            arrayOfString[1] = "name";
            return arrayOfString;
        }
        return null;
    }

    public static String[] getPlaylistsTableChecksumColumns(int paramInt) {
        if (paramInt == 1) {
            String[] arrayOfString = new String[2];
            arrayOfString[0] = "_id";
            arrayOfString[1] = "playlist_name";
            return arrayOfString;
        }
        if (paramInt == 2) {
            String[] arrayOfString = new String[2];
            arrayOfString[0] = "_id";
            arrayOfString[1] = "name";
            return arrayOfString;
        }
        return null;
    }

    public static MusicDBContentProvider.DBDataType[] getPlaylistsTableColumnTypes() {
        return new MusicDBContentProvider.DBDataType[] { MusicDBContentProvider.DBDataType.INTEGER, MusicDBContentProvider.DBDataType.TEXT };
    }

    private static String getPlaylistsTableOrder() {
        return "_id";
    }

    public static Uri getPlaylistsTableUri(int paramInt) {
        return (paramInt == 1) ? googleContentUri : ((paramInt == 2) ? externalContentUri : null);
    }
}
