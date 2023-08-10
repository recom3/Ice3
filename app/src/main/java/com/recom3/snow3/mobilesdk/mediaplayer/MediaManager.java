package com.recom3.snow3.mobilesdk.mediaplayer;

import android.database.Cursor;
import android.util.Log;

import com.recom3.connect.messages.MusicMessage;
import com.recom3.connect.util.FileUtils;
import com.recom3.snow3.mobilesdk.MediaPlayerService;

/**
 * Created by Recom3 on 26/01/2022.
 */

public class MediaManager {

    private static final String TAG = "ReconMediaManager";

    private ReconMusicPlayer mediaPlayer;

    private MediaPlayerService mediaSrvc;

    public MediaManager(MediaPlayerService paramMediaPlayerService) {
        this.mediaSrvc = paramMediaPlayerService;
        this.mediaPlayer = new ReconMusicPlayer(paramMediaPlayerService);
        if (FileUtils.hasStorage(false)) {
            this.mediaPlayer.setPlayerState(MusicMessage.PlayerState.STOPPED);
            return;
        }
        this.mediaPlayer.setPlayerState(MusicMessage.PlayerState.NOT_INIT);
    }

    public ReconMusicPlayer getMPlayer() {
        if (this.mediaPlayer == null && this.mediaSrvc != null)
            this.mediaPlayer = new ReconMusicPlayer(this.mediaSrvc);
        return this.mediaPlayer;
    }

    public void loadSongWithCursor(Cursor paramCursor) {
        Log.d("ReconMediaManager", "loading song with new cursor!");
        ReconNextTrackFinder.flagToGenerateNewShuffleCursor();
        getMPlayer().loadSongWithCursor(paramCursor);
    }

}
