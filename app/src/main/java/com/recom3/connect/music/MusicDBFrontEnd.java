package com.recom3.connect.music;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class MusicDBFrontEnd {
    static String ALL_SONGS;

    static String NO_MEDIA = "no_media";

    static String SHUFFLE_ID;

    private static final String TAG = "MusicDBFrontEnd";

    public static MediaInterface getAlbum;

    public static MediaInterface getArtist;

    public static MediaInterface getPlaylist;

    public static MediaInterface getPlaylistSong;

    public static MediaInterface getSong;

    public static String[] noMediaStrings;

    static {
        ALL_SONGS = "all_songs";
        SHUFFLE_ID = "shuffle_songs";
        noMediaStrings = new String[] { "No artists", "No albums", "No songs", "No songs", "No playlists", "No songs", "No songs", "No albums" };
        getSong = new MediaInterface() {
            public ReconMediaData read(Cursor param1Cursor) {
                String str1 = param1Cursor.getString(0);
                String str2 = param1Cursor.getString(1);
                String str3 = param1Cursor.getString(2);
                return new ReconMediaData.ReconSong(str1, str2, param1Cursor.getString(3), str3, param1Cursor.getLong(4));
            }
        };
        getPlaylistSong = new MediaInterface() {
            public ReconMediaData read(Cursor param1Cursor) {
                return new ReconMediaData.ReconSong(param1Cursor.getString(1), param1Cursor.getString(3), "", param1Cursor.getString(2), 0L);
            }
        };
        getPlaylist = new MediaInterface() {
            public ReconMediaData read(Cursor param1Cursor) {
                return new ReconMediaData.ReconPlaylist(param1Cursor.getString(0), param1Cursor.getString(1));
            }
        };
        getArtist = new MediaInterface() {
            public ReconMediaData read(Cursor param1Cursor) {
                return new ReconMediaData.ReconArtist(param1Cursor.getString(0), param1Cursor.getString(1));
            }
        };
        getAlbum = new MediaInterface() {
            public ReconMediaData read(Cursor param1Cursor) {
                String str1 = param1Cursor.getString(0);
                String str2 = param1Cursor.getString(2);
                return new ReconMediaData.ReconAlbum(str1, param1Cursor.getString(3), param1Cursor.getString(4), str2);
            }
        };
    }

    public static MusicListCursor getCursor(Context paramContext, MusicListType paramMusicListType, String paramString) {
        String str;
        switch (paramMusicListType) {
            default:
                return null;
            case PLAYLISTS:
                return getCursor(paramContext, "playlists", MusicDBPlaylistColumnsProvider.reconPlaylistTableSelection, null, "recon_name ASC");
            case PLAYLIST_SONGS:
                return paramString.equals(NO_MEDIA) ? null : getCursor(paramContext, "playlist/" + paramString, MusicDBPlaylistColumnsProvider.reconPlaylistSongsColumns, null, MusicDBPlaylistColumnsProvider.reconPlaylistSongsOrderById);
            case ARTISTS:
                return getCursor(paramContext, "artists", MusicDBContentProvider.artistsTableColumns, null, "artist ASC");
            case ARTIST_ALBUMS:
                if (paramString.equals(NO_MEDIA))
                    return null;
                str = "artist_id = " + paramString;
                return getCursor(paramContext, "media", MusicDBContentProvider.albumsProjection, str, "album");
            case ALL_ALBUMS:
                return getCursor(paramContext, "media", MusicDBContentProvider.albumsProjection, null, "album ASC");
            case ARTIST_SONGS:
                if (paramString.equals(NO_MEDIA))
                    return null;
                str = "artist_id = " + paramString;
                return getCursor(paramContext, "media", MusicDBContentProvider.songsTableColumns, str, null);
            case ALBUM_SONGS:
                if (paramString.equals(NO_MEDIA))
                    return null;
                str = "album_id = " + paramString;
                return getCursor(paramContext, "media", MusicDBContentProvider.songsTableColumns, str, "track ASC");
            case SONGS:
                break;
        }
        return getCursor(paramContext, "media", MusicDBContentProvider.songsTableColumns, null, MusicDBContentProvider.songsOrder);
    }

    public static MusicListCursor getCursor(Context paramContext, String paramString1, String[] paramArrayOfString, String paramString2, String paramString3) {
        Uri uri = Uri.withAppendedPath(MusicDBContentProvider.CONTENT_URI, paramString1);
        MusicListCursor musicListCursor = new MusicListCursor(paramContext.getContentResolver().query(uri, paramArrayOfString, paramString2, null, paramString3));
        if (musicListCursor.getCount() == 0)
            Log.e("MusicDBFrontEnd", "nothing stored in cursor");
        return musicListCursor;
    }

    public static ReconMediaData.ReconSong getSongFromId(Context paramContext, String paramString) {
        Context context = null;
        paramString = "_id = \"" + paramString + "\"";
        MusicListCursor musicListCursor = getCursor(paramContext, "media", MusicDBContentProvider.songsTableColumns, paramString, null);
        if (musicListCursor == null)
            //return (ReconMediaData.ReconSong)context;
            return null;
        musicListCursor.moveToFirst();
        if (musicListCursor.getCount() == 0 || musicListCursor.isAfterLast()) {
            musicListCursor.close();
            //return (ReconMediaData.ReconSong)context;
            return null;
        }
        ReconMediaData.ReconSong reconSong = (ReconMediaData.ReconSong)getSong.read((Cursor)musicListCursor);
        musicListCursor.close();
        return reconSong;
    }

    public static interface MediaInterface {
        ReconMediaData read(Cursor param1Cursor);
    }

    public enum MusicListType {
        ALBUM_SONGS, ALL_ALBUMS, ARTISTS, ARTIST_ALBUMS, ARTIST_SONGS, PLAYLISTS, PLAYLIST_SONGS, SONGS;

        static {
            //!!!!
            //ALL_ALBUMS = new MusicListType("ALL_ALBUMS", 7);
            //$VALUES = new MusicListType[] { ARTISTS, ARTIST_ALBUMS, ALBUM_SONGS, ARTIST_SONGS, PLAYLISTS, PLAYLIST_SONGS, SONGS, ALL_ALBUMS };
        }
    }
}
