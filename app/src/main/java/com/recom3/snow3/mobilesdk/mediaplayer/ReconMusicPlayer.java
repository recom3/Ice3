package com.recom3.snow3.mobilesdk.mediaplayer;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.recom3.connect.music.MusicCursorGenerator;
import com.recom3.connect.music.MusicDBFrontEnd;
import com.recom3.connect.music.MusicDBPlaylistColumnsProvider;
import com.recom3.connect.music.ReconMediaData;
import com.recom3.connect.messages.MusicMessage;
import com.recom3.connect.util.FileUtils;
import com.recom3.snow3.mobilesdk.MediaPlayerService;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.recom3.connect.messages.MusicMessage.Action.GET_PLAYER_STATE;
import static com.recom3.connect.messages.MusicMessage.Action.NEXT_SONG;
import static com.recom3.connect.messages.MusicMessage.Action.PREVIOUS_SONG;
import static com.recom3.connect.messages.MusicMessage.Action.START_SONG;
import static com.recom3.connect.messages.MusicMessage.Action.TOGGLE_PAUSE;
import static com.recom3.connect.messages.MusicMessage.Action.VOLUME_DOWN;
import static com.recom3.connect.messages.MusicMessage.Action.VOLUME_UP;
import static com.recom3.snow3.mobilesdk.mediaplayer.ReconNextTrackFinder.flagToGenerateNewShuffleCursor;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class ReconMusicPlayer extends MediaPlayer {
    private static final String TAG = "ReconMusicPlayer";

    private String NO_MEDIA = "no_media";

    public AudioManager audioManager;

    private Bitmap currentAlbumArt = null;

    private ReconMediaData.ReconSong currentSong = null;

    public FocusChangeListener focusChangeListener;

    boolean loop = false;

    MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer param1MediaPlayer) {
            if (ReconMusicPlayer.this.playerState == MusicMessage.PlayerState.PLAYING)
                ReconMusicPlayer.this.nextSong();
        }
    };

    private MediaPlayerService mediaSrvc;

    boolean mute = false;

    private MusicMessage.PlayerState playerState = MusicMessage.PlayerState.NOT_INIT;

    private String playlistNoSongsId = "No songs";

    private boolean resumePlayback;

    boolean shuffle = false;

    public MusicMessage.SongInfo song;

    ReconMusicPlayer(MediaPlayerService paramMediaPlayerService) {
        this.mediaSrvc = paramMediaPlayerService;
        setOnCompletionListener(this.mCompletionListener);
        if (Build.VERSION.SDK_INT >= 8)
            this.focusChangeListener = new FocusChangeListener();
        this.audioManager = (AudioManager)paramMediaPlayerService.getSystemService(Context.AUDIO_SERVICE);
    }

    private Cursor findPlaylistCursor(String paramString1, String paramString2) {
        String str = paramString1;
        if (paramString1.equals(""))
            str = "<unknown>";
        paramString1 = paramString2;
        if (paramString2.equals(""))
            paramString1 = "<unknown>";
        paramString1 = "is_music != 0 AND title = \"" + str + "\"" + " AND " + "artist" + " = \"" + paramString1 + "\"";
        String[] arrayOfString = new String[6];
        arrayOfString[0] = "_id";
        arrayOfString[1] = "title";
        arrayOfString[2] = "artist";
        arrayOfString[3] = "album";
        arrayOfString[4] = "_data";
        arrayOfString[5] = "album_id";
        Cursor cursor2 = this.mediaSrvc.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOfString, paramString1, null, null);
        if (cursor2 != null);
        Cursor cursor1 = cursor2;
        if (cursor2 != null) {
            cursor1 = cursor2;
            if (cursor2.getCount() > 1) {
                cursor2.moveToFirst();
                String str1 = cursor2.getString(0);
                str1 = "is_music != 0 AND _id = \"" + str1 + "\"";
                cursor1 = this.mediaSrvc.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOfString, str1, null, null);
                Log.d("ReconMusicPlayer", "DUPLICATES, we do not have a unique song with this information, making arbitrary choice!!");
            }
        }
        return cursor1;
    }

    /*
    JC 23.07.2022
    This function has to be re-checked, sinde the logic is not clear
    1. Store song id in variable 1 (songId)
    2. Get a cursor
    3. Dump column names
    */
    private Cursor handlePlaylistSong() {
        int playId = Integer.parseInt(this.song.srcId);
        String title = "";
        String artist = "";
        Uri uri = Uri.withAppendedPath(MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1), this.song.srcId.toString() + "/members");
        String[] projection2 = MusicDBPlaylistColumnsProvider.reconPlaylistSongsSelection;
        Cursor cursor = this.mediaSrvc.getContentResolver().query(uri, projection2, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            Uri uri2 = MediaStore.Audio.Playlists.Members.getContentUri("external", playId);
            cursor = this.mediaSrvc.getContentResolver().query(uri2, projection2, null, null, null);
            String[] columns = cursor.getColumnNames();
            for (int i = 0; i < columns.length; i++) {
                Log.d(TAG, "column1: " + columns[i]);
            }
        }
        cursor.moveToFirst();
        while (true) {
            String tempSongAudioId = cursor.getString(1).trim();
            if (tempSongAudioId.equals(this.song.songId)) {
                title = cursor.getString(2);
                artist = cursor.getString(3);
                break;
            } else if (!cursor.moveToNext()) {
                break;
            }
        }
        Cursor cursor2 = findPlaylistCursor(title, artist);
        String[] columns2 = cursor2.getColumnNames();
        for (int i2 = 0; i2 < columns2.length; i2++) {
            Log.d(TAG, "column2: " + columns2[i2]);
        }
        return cursor2;
    }

    private boolean isCursorValid(Cursor paramCursor) {
        boolean bool = false;
        if (paramCursor.getColumnCount() == 6 && paramCursor.getColumnName(0).equals("_id"))
            bool = true;
        return bool;
    }

    private void loadSong(MusicMessage.SongInfo paramSongInfo) {
        this.song = paramSongInfo;
        String str = "is_music != 0 AND _id = " + paramSongInfo.songId;
        Cursor cursor = this.mediaSrvc.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "title", "artist", "album", "_data", "album_id" }, str, null, null);
        Log.d("ReconMusicPlayer", "songId requested: " + paramSongInfo.songId + " cursor size: " + cursor.getCount());
        if (paramSongInfo.srcType.toString().equals("PLAYLIST_SONGS"))
            cursor = handlePlaylistSong();
        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
            updateCurrentSong(cursor);
            this.mediaSrvc.updatePlayerUI();
        } else if (cursor.getCount() == 0) {
            Log.e("ReconMusicPlayer", "couldn't load song, no item returned for audio_id: " + paramSongInfo.songId);
        } else {
            Log.e("ReconMusicPlayer", "couldn't load song, query returned multiple items");
        }
        cursor.close();
    }

    private void loadSong(String path) {
        synchronized (this) {
            reset();
            try {
                try {
                    setDataSource(path);
                    prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IllegalStateException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void pauseSong(boolean temporary) {
        if (this.focusChangeListener != null && !temporary) {
            this.audioManager.abandonAudioFocus(this.focusChangeListener);
        }
        synchronized (this) {
            if (this != null) {
                pause();
                this.playerState = MusicMessage.PlayerState.PAUSED;
                this.mediaSrvc.updatePlayerUI();
            }
        }
    }

    private void switchTrack(boolean paramBoolean) {
        if (!FileUtils.hasStorage(false)) {
            Log.e("ReconMusicPlayer", "no storage");
            return;
        }
        String str = "" + ReconNextTrackFinder.findNext(paramBoolean, this.mediaSrvc);
        if (!str.equals("-1")) {
            this.song.songId = str;
            loadSong(this.song);
            if (getPlayerState() == MusicMessage.PlayerState.PLAYING)
                playSong();
            return;
        }
        Log.e("ReconMusicPlayer", "Couldn't find next song!");
    }

    private void updateAlbumArt(Cursor paramCursor) {
        int i = paramCursor.getInt(5);
        Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), i);
        try {
            this.currentAlbumArt = BitmapFactory.decodeStream(this.mediaSrvc.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException fileNotFoundException) {
            this.currentAlbumArt = null;
        }
    }

    private void updateCurrentSong(Cursor paramCursor) {
        String str1 = paramCursor.getString(1);
        String str2 = paramCursor.getString(2);
        String str3 = paramCursor.getString(3);
        this.currentSong = new ReconMediaData.ReconSong("" + this.song.songId, str2, str3, str1, 0L);
        updateAlbumArt(paramCursor);
        loadSong(paramCursor.getString(paramCursor.getColumnIndex("_data")));
    }

    public Bitmap getCurrentAlbumArt() {
        return this.currentAlbumArt;
    }

    public ReconMediaData.ReconSong getCurrentSong() {
        return this.currentSong;
    }

    public MusicMessage.PlayerState getPlayerState() {
        return this.playerState;
    }

    public void gotMessage(MusicMessage msg) {

        boolean playlistNoSong = true;
        synchronized (this) {
            Log.d(TAG, "MODLive message: " + msg.info.toString());
            if (msg.info != null) {
                if (msg.info.song != null) {
                    Log.d(TAG, "MODLive message: " + msg.info.song.toString());
                    if (this.song == null || !msg.info.song.equals(this.song)) {
                        MusicMessage.SongInfo song = msg.info.song;
                        boolean empty = song.songId == this.NO_MEDIA;
                        if (!song.srcType.equals(MusicDBFrontEnd.MusicListType.PLAYLIST_SONGS) || !song.songId.contains(this.playlistNoSongsId)) {
                            playlistNoSong = false;
                        }
                        if (empty || playlistNoSong) {
                            Log.d(TAG, "no song id passed. not loading any song");
                        } else {
                            Cursor cursor = MusicCursorGenerator.getNewSongListSelectionCursor(this.mediaSrvc, song.songId, song.srcType, song.srcId);
                            ReconNextTrackFinder.flagToGenerateNewShuffleCursor();
                            loadSongWithCursor(cursor);
                        }
                    } else {
                        this.song = msg.info.song;
                    }
                }
                if (msg.info.state != null) {
                    if (msg.info.state != getPlayerState()) {
                        switch (msg.info.state) {
                            case PLAYING:
                                playSong();
                                break;
                            case PAUSED:
                                pauseSong(false);
                                break;
                            case STOPPED:
                                stop();
                                break;
                        }
                    } else {
                        this.mediaSrvc.updatePlayerUI();
                    }
                }
                if (msg.info.volume != null) {
                    int volIndex = (int) (msg.info.volume.floatValue() * this.audioManager.getStreamMaxVolume(3));
                    this.audioManager.setStreamVolume(3, volIndex, 4);
                    Log.d(TAG, "Volume set to: " + msg.info.volume + " index: " + volIndex);
                    setVolume(1.0f, 1.0f);
                    this.mute = false;
                }
                if (msg.info.progress != null) {
                    seekTo(msg.info.progress.intValue());
                }
                if (msg.info.shuffle != null) {
                    this.shuffle = msg.info.shuffle.booleanValue();
                }
                if (msg.info.loop != null) {
                    this.loop = msg.info.loop.booleanValue();
                }
                if (msg.info.mute != null) {
                    this.mute = msg.info.mute.booleanValue();
                    if (this.mute) {
                        setVolume(BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_RED);
                    } else {
                        setVolume(1.0f, 1.0f);
                    }
                }
            }
            if (msg.action != null) {
                switch (msg.action) {
                    case START_SONG:
                        playSong();
                        break;
                    case NEXT_SONG:
                        nextSong();
                        break;
                    case PREVIOUS_SONG:
                        previousSong();
                        break;
                    case GET_PLAYER_STATE:
                        this.mediaSrvc.updatePlayerUI();
                        break;
                    case VOLUME_UP:
                        this.mute = false;
                        this.mediaSrvc.updatePlayerUI();
                        break;
                    case VOLUME_DOWN:
                        this.mute = false;
                        this.mediaSrvc.updatePlayerUI();
                        break;
                    case TOGGLE_PAUSE:
                        togglePause();
                        break;
                }
            }
        }
    }

    public boolean isLoop() {
        return this.loop;
    }

    public boolean isMute() {
        return this.mute;
    }

    public boolean isShuffle() {
        return this.shuffle;
    }

    void loadSongWithCursor(Cursor paramCursor) {
        if (!isCursorValid(paramCursor)) {
            Log.e("ReconMusicPlayer", "not a valid cursor! please provide valid column span");
            return;
        }
        ReconNextTrackFinder.setPlayingCursor(paramCursor);
        Log.d("ReconMusicPlayer", "loading song with cursor. cursor size: " + paramCursor.getCount());
        if (!paramCursor.isAfterLast() && !paramCursor.isBeforeFirst()) {
            if (this.song == null) {
                this.song = new MusicMessage.SongInfo(paramCursor.getString(0), MusicDBFrontEnd.MusicListType.SONGS, "-1");
            } else {
                this.song.songId = paramCursor.getString(0);
            }
            updateCurrentSong(paramCursor);
            this.mediaSrvc.updatePlayerUI();
            return;
        }
        if (paramCursor.moveToFirst()) {
            Log.d("ReconMusicPlayer", "retrying to load song!");
            if (!paramCursor.isAfterLast() && !paramCursor.isBeforeFirst()) {
                if (this.song == null) {
                    this.song = new MusicMessage.SongInfo(paramCursor.getString(0), MusicDBFrontEnd.MusicListType.SONGS, "-1");
                } else {
                    this.song.songId = paramCursor.getString(0);
                }
                updateCurrentSong(paramCursor);
                this.mediaSrvc.updatePlayerUI();
                return;
            }
            Log.e("ReconMusicPlayer", "could not load song, something wrong with cursor!");
        }
    }

    public void nextSong() {
        switchTrack(true);
    }

    public void playSong() {
        if (this.focusChangeListener != null) {
            this.audioManager.requestAudioFocus(this.focusChangeListener, 3, 1);
        }
        synchronized (this) {
            if (this != null) {
                start();
                this.playerState = MusicMessage.PlayerState.PLAYING;
                this.mediaSrvc.updatePlayerUI();
            }
        }
    }

    public void previousSong() {
        if (this.playerState == MusicMessage.PlayerState.PLAYING || this.playerState == MusicMessage.PlayerState.PAUSED) {
            synchronized (this) {
                if (getCurrentPosition() >= 3000) {
                    seekTo(0);
                    return;
                }
            }
        }
        switchTrack(false);
    }

    public void setPlayerState(MusicMessage.PlayerState paramPlayerState) {
        this.playerState = paramPlayerState;
    }

    public void stopSong() {
        if (this.focusChangeListener != null) {
            this.audioManager.abandonAudioFocus(this.focusChangeListener);
        }
        synchronized (this) {
            if (this != null) {
                if (this.playerState == MusicMessage.PlayerState.PLAYING || this.playerState == MusicMessage.PlayerState.PAUSED) {
                    stop();
                }
            }
        }
    }

    public void toggleLoop(boolean paramBoolean) {
        this.loop = paramBoolean;
        this.mediaSrvc.updatePlayerUI();
    }

    public void togglePause() {
        synchronized (this) {
            if (getPlayerState() == MusicMessage.PlayerState.PLAYING) {
                pauseSong(false);
            } else if (getPlayerState() == MusicMessage.PlayerState.PAUSED) {
                playSong();
            } else if (getPlayerState() == MusicMessage.PlayerState.STOPPED) {
                try {
                    prepare();
                    seekTo(0);
                    playSong();
                } catch (IOException e) {
                    reset();
                    e.printStackTrace();
                }
            }
        }
    }

    public void toggleShuffle(boolean paramBoolean) {
        this.shuffle = paramBoolean;
        flagToGenerateNewShuffleCursor();
        this.mediaSrvc.updatePlayerUI();
    }

    class FocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        public void onAudioFocusChange(int param1Int) {
            if (param1Int == -1) {
                if (ReconMusicPlayer.this.getPlayerState() == MusicMessage.PlayerState.PLAYING) {
                    ReconMusicPlayer.this.pauseSong(false);
                    //ReconMusicPlayer.access$202(ReconMusicPlayer.this, false);
                }
                return;
            }
            if (param1Int == -2) {
                if (ReconMusicPlayer.this.getPlayerState() == MusicMessage.PlayerState.PLAYING) {
                    ReconMusicPlayer.this.pauseSong(true);
                    //ReconMusicPlayer.access$202(ReconMusicPlayer.this, true);
                }
                return;
            }
            if (param1Int == 1) {
                if (ReconMusicPlayer.this.resumePlayback && ReconMusicPlayer.this.getPlayerState() == MusicMessage.PlayerState.PAUSED)
                    ReconMusicPlayer.this.playSong();
                //ReconMusicPlayer.access$202(ReconMusicPlayer.this, false);
            }
        }
    }
}
