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

import com.recom3.connect.music.MusicCursorGenerator;
import com.recom3.connect.music.MusicDBFrontEnd;
import com.recom3.connect.music.MusicDBPlaylistColumnsProvider;
import com.recom3.connect.music.ReconMediaData;
import com.recom3.connect.messages.MusicMessage;
import com.recom3.connect.util.FileUtils;
import com.recom3.snow3.mobilesdk.MediaPlayerService;

import java.io.FileNotFoundException;
import java.io.IOException;

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
        // Byte code:
        //   0: aload_0
        //   1: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
        //   4: getfield srcId : Ljava/lang/String;
        //   7: invokestatic parseInt : (Ljava/lang/String;)I
        //   10: istore_1
        int songId = Integer.parseInt(song.srcId);
        //   11: ldc ''
        //   13: astore_2
        //   14: ldc ''
        //   16: astore_3

        //   17: iconst_1
        //   18: invokestatic getPlaylistsTableUri : (I)Landroid/net/Uri;
        //Get google play list
        Uri uriPlaylistTable = MusicDBPlaylistColumnsProvider.getPlaylistsTableUri(1);
        //   21: new java/lang/StringBuilder
        //   24: dup
        //   25: invokespecial <init> : ()V
        StringBuilder sb = new StringBuilder();
        //   28: aload_0
        //   29: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
        //   32: getfield srcId : Ljava/lang/String;
        //   35: invokevirtual toString : ()Ljava/lang/String;
        //   38: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   41: ldc '/members'
        //   43: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   46: invokevirtual toString : ()Ljava/lang/String;
        //   49: invokestatic withAppendedPath : (Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
        sb.append(this.song.srcId.toString());
        sb.append("/members");
        Uri uri = Uri.withAppendedPath(uriPlaylistTable, sb.toString());
        //   52: astore #4
        //   54: getstatic com/reconinstruments/connect/music/MusicDBPlaylistColumnsProvider.reconPlaylistSongsSelection : [Ljava/lang/String;
        //{ "_id", "recon_audio_id", "recon_title", "recon_artist" }
        String[] reconPlaylistSongsSelection = MusicDBPlaylistColumnsProvider.reconPlaylistSongsSelection;
        //   57: astore #5
        //   59: aload_0
        //   60: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   63: invokevirtual getContentResolver : ()Landroid/content/ContentResolver;
        //   66: aload #4
        //   68: aload #5
        //   70: aconst_null
        //   71: aconst_null
        //   72: aconst_null
        //   73: invokevirtual query : (Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        Cursor cursor = mediaSrvc.getContentResolver().query(uri, reconPlaylistSongsSelection, null, null, null);
        //   76: astore #6
        //   78: aload #6
        //   80: ifnull -> 97
        //   83: aload #6
        //   85: astore #4
        //   87: aload #6
        //   89: invokeinterface getCount : ()I
        //   94: ifne -> 181
        if (cursor == null || cursor.getCount() == 0) {
            //   97: ldc 'external'
            //   99: iload_1 //songId
            //   100: i2l
            //   101: invokestatic getContentUri : (Ljava/lang/String;J)Landroid/net/Uri;
            uri = MediaStore.Audio.Playlists.Members.getContentUri("external", songId);
            //   104: astore #4
            //   106: aload_0
            //   107: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
            //   110: invokevirtual getContentResolver : ()Landroid/content/ContentResolver;
            //   113: aload #4
            //   115: aload #5
            //   117: aconst_null
            //   118: aconst_null
            //   119: aconst_null
            //   120: invokevirtual query : (Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
            cursor = mediaSrvc.getContentResolver().query(uri, reconPlaylistSongsSelection, null, null, null);

            //   123: astore #6
            //   125: aload #6
            //   127: invokeinterface getColumnNames : ()[Ljava/lang/String;
            String[] columnsNames = cursor.getColumnNames();
            //   132: astore #5
            //   134: iconst_0
            //   135: istore_1

            //   136: aload #6
            //   138: astore #4 //cursor
            //   140: iload_1

            //   141: aload #5  //columnsNames
            //   143: arraylength
            //   144: if_icmpge -> 181

            //Dump column names
            int var_1 = 0;
            while (var_1 < columnsNames.length) {
                //   147: ldc 'ReconMusicPlayer'
                //   149: new java/lang/StringBuilder
                //   152: dup
                //   153: invokespecial <init> : ()V
                //   156: ldc 'column1: '
                //   158: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   161: aload #5
                //   163: iload_1
                //   164: aaload
                //   165: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   168: invokevirtual toString : ()Ljava/lang/String;
                //   171: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
                sb = new StringBuilder();
                sb.append("column1: ");
                sb.append(columnsNames[var_1]);
                Log.d("ReconMusicPlayer", sb.toString());
                //   174: pop
                //   175: iinc #1, 1
                var_1++;
                //   178: goto -> 136
            }
        }

        cursor.moveToFirst();
        //   181: aload #4
        //   183: invokeinterface moveToFirst : ()Z
        //   188: pop

        //Loop
        do {
            //   189: aload #4 //cursor
            //   191: iconst_1
            //   192: invokeinterface getString : (I)Ljava/lang/String;
            //{ "_id", "recon_audio_id", "recon_title", "recon_artist" }
            String col_1 = cursor.getString(1).trim();
            //   197: invokevirtual trim : ()Ljava/lang/String;
            //   200: aload_0
            //   201: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
            //   204: getfield songId : Ljava/lang/String;
            //   207: invokevirtual equals : (Ljava/lang/Object;)Z
            //   210: ifeq -> 291
            if (song.songId.equals(col_1)) {
                //   213: aload #4
                //   215: iconst_2
                //   216: invokeinterface getString : (I)Ljava/lang/String;
                //{ "_id", "recon_audio_id", "recon_title", "recon_artist" }
                String var_2 = cursor.getString(2);
                //   221: astore_2

                //   222: aload #4
                //   224: iconst_3
                //   225: invokeinterface getString : (I)Ljava/lang/String;
                //   230: astore #4
                String var_4 = cursor.getString(3);

                //   232: aload_0
                //   233: aload_2
                //   234: aload #4
                //   236: invokespecial findPlaylistCursor : (Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
                //title-artist
                cursor = findPlaylistCursor(var_2, var_4);

                //   239: astore #4
                //   241: aload #4
                //   243: invokeinterface getColumnNames : ()[Ljava/lang/String;
                String[] columnsNames = cursor.getColumnNames();
                //   248: astore_2
                //   249: iconst_0
                //   250: istore_1

                int var_1 = 0;

                //Loop
                //   251: iload_1
                //   252: aload_2
                //   253: arraylength
                //   254: if_icmpge -> 307
                while (var_1 < columnsNames.length) {

                    //   257: ldc 'ReconMusicPlayer'
                    //   259: new java/lang/StringBuilder
                    //   262: dup
                    //   263: invokespecial <init> : ()V
                    //   266: ldc_w 'column2: '
                    //   269: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
                    sb = new StringBuilder();
                    sb.append("column2: ");
                    //   272: aload_2   // column names
                    //   273: iload_1   // =0
                    //   274: aaload
                    sb.append(columnsNames[var_1]);
                    //   275: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
                    //   278: invokevirtual toString : ()Ljava/lang/String;
                    //   281: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
                    Log.d("ReconMusicPlayer", sb.toString());

                    //   284: pop
                    //   285: iinc #1, 1
                    var_1++;
                    //   288: goto -> 251
                }

                return cursor;
            }

            //   291: aload #4
            //   293: invokeinterface moveToNext : ()Z
            //   298: ifne -> 189
        }while(cursor.moveToNext());

        //   301: aload_3
        //   302: astore #4 // cursor
        //   304: goto -> 232

        //   307: aload #4
        //   309: areturn

        return cursor;
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

    private void loadSong(String paramString) {
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        synchronized (this) {
            //   2: aload_0
            //   3: invokevirtual reset : ()V
            reset();
            //   6	15	18	java/io/IOException
            //   6: aload_0
            //   7: aload_1
            //   8: invokevirtual setDataSource : (Ljava/lang/String;)V
            try {
                setDataSource(paramString);
                //   11: aload_0
                //   12: invokevirtual prepare : ()V
                prepare();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
                throw(ex);
            }
            //   15: aload_0
            //   16: monitorexit
            //   17: return
            return;
        }

        //   18: astore_1
        //   19: aload_1
        //   20: invokevirtual printStackTrace : ()V
        //   23: goto -> 15

        //   26: astore_1
        //   27: aload_0
        //   28: monitorexit
        //   29: aload_1
        //   30: athrow
        //   31: astore_1
        //   32: aload_1
        //   33: invokevirtual printStackTrace : ()V
        //   36: goto -> 15
        // Exception table:
        //   from	to	target	type
        //   2	6	26	finally
        //   6	15	18	java/io/IOException
        //   6	15	31	java/lang/IllegalStateException
        //   6	15	26	finally
        //   15	17	26	finally
        //   19	23	26	finally
        //   27	29	26	finally
        //   32	36	26	finally
    }

    private void pauseSong(boolean paramBoolean) {
        // Byte code:
        //   0: aload_0
        //   1: getfield focusChangeListener : Lcom/reconinstruments/mobilesdk/mediaplayer/ReconMusicPlayer$FocusChangeListener;
        if(focusChangeListener!=null && paramBoolean == false) {
            //   4: ifnull -> 23
            //   7: iload_1
            //   8: ifne -> 23
            //   11: aload_0
            //   12: getfield audioManager : Landroid/media/AudioManager;
            //   15: aload_0
            //   16: getfield focusChangeListener : Lcom/reconinstruments/mobilesdk/mediaplayer/ReconMusicPlayer$FocusChangeListener;
            //   19: invokevirtual abandonAudioFocus : (Landroid/media/AudioManager$OnAudioFocusChangeListener;)I
            //   22: pop
            //   23: aload_0
            audioManager.abandonAudioFocus(focusChangeListener);
        }
        synchronized (this)
        {
            pause();
            playerState = MusicMessage.PlayerState.PAUSED;
            mediaSrvc.updatePlayerUI();
        }
        //   24: monitorenter
        //   25: aload_0
        //   26: ifnonnull -> 32
        //   29: aload_0
        //   30: monitorexit
        //   31: return

        //   32: aload_0
        //   33: invokevirtual pause : ()V
        //   36: aload_0
        //   37: getstatic com/reconinstruments/connect/messages/MusicMessage$PlayerState.PAUSED : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   40: putfield playerState : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   43: aload_0
        //   44: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   47: invokevirtual updatePlayerUI : ()V
        //   50: aload_0
        //   51: monitorexit
        //   52: goto -> 31

        //   55: astore_2
        //   56: aload_0
        //   57: monitorexit
        //   58: aload_2
        //   59: athrow
        // Exception table:
        //   from	to	target	type
        //   29	31	55	finally
        //   32	52	55	finally
        //   56	58	55	finally
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

    public void gotMessage(MusicMessage paramMusicMessage) {

        int var_4;

        // Byte code:
        //   0: iconst_1
        //   1: istore_2    //local variable 2 = 1
        //   2: aload_0     //push this on to the stack
        //   3: monitorenter

        //   4: new java/lang/StringBuilder
        //   7: astore_3
        //   8: aload_3
        //   9: invokespecial <init> : ()V
        StringBuilder sb = new StringBuilder("");
        sb.append("MODLive message: ");
        //   12: ldc 'ReconMusicPlayer'
        //   14: aload_3
        //   15: ldc_w 'MODLive message: '
        //   18: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   21: aload_1    //paramMusicMessage to the stack
        //   22: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
        sb.append(paramMusicMessage.info.toString());
        //   25: invokevirtual toString : ()Ljava/lang/String;
        //   28: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   31: invokevirtual toString : ()Ljava/lang/String;
        //   34: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        Log.d("ReconMusicPlayer", sb.toString());

        //   37: pop
        //   38: aload_1
        //   39: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
        //   42: ifnull -> 457
        if(paramMusicMessage.info!=null) {
            //   45: aload_1
            //   46: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
            //   49: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
            if (paramMusicMessage.info.song != null) {
                //   52: ifnull -> 183
                //   55: new java/lang/StringBuilder
                //   58: astore_3
                //   59: aload_3
                //   60: invokespecial <init> : ()V
                //   63: ldc 'ReconMusicPlayer'
                //   65: aload_3
                //   66: ldc_w 'MODLive message: '
                //   69: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   72: aload_1
                //   73: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                //   76: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
                //   79: invokevirtual toString : ()Ljava/lang/String;
                //   82: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   85: invokevirtual toString : ()Ljava/lang/String;
                //   88: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
                sb = new StringBuilder("");
                sb.append("MODLive message: ");
                sb.append(paramMusicMessage.info.song.toString());
                Log.d("ReconMusicPlayer", sb.toString());
                //   91: pop
                //   92: aload_0
                //   93: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
                //   96: ifnull -> 116
                //   99: aload_1
                //   100: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                //   103: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
                //   106: aload_0
                //   107: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
                //   110: invokevirtual equals : (Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;)Z
                //   113: ifne -> 570
                if (this.song != null && !this.song.equals(paramMusicMessage.info.song)) {
                    //   570: aload_0
                    //   571: aload_1
                    //   572: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                    //   575: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
                    //   578: putfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
                    //   581: goto -> 183
                    this.song = paramMusicMessage.info.song;
                } else {
                    //   116: aload_1
                    //   117: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                    //   120: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
                    //   123: astore_3 //song
                    //   124: aload_3
                    //   125: getfield songId : Ljava/lang/String;
                    //   128: aload_0
                    //   129: getfield NO_MEDIA : Ljava/lang/String;
                    //   132: if_acmpne -> 523
                    if (!this.NO_MEDIA.equals(paramMusicMessage.info.song)) {
                        //   523: iconst_0
                        //   524: istore #4 //var#4=0
                        //   526: goto -> 138
                        var_4 = 0;
                    } else {
                        //   135: iconst_1
                        //   136: istore #4 //var#4=1
                        var_4 = 1;
                    }

                    //   138: aload_3 //paramMusicMessage.info.song
                    //   139: getfield srcType : Lcom/reconinstruments/connect/music/MusicDBFrontEnd$MusicListType;
                    //   142: getstatic com/reconinstruments/connect/music/MusicDBFrontEnd$MusicListType.PLAYLIST_SONGS : Lcom/reconinstruments/connect/music/MusicDBFrontEnd$MusicListType;
                    //   145: invokevirtual equals : (Ljava/lang/Object;)Z
                    //   148: ifeq -> 529

                    int var_2 = 1;
                    if (MusicDBFrontEnd.MusicListType.PLAYLIST_SONGS.equals(paramMusicMessage.info.song)) {
                        //   529: iconst_0
                        //   530: istore_2 //local variable 2 = 0
                        //   531: goto -> 165
                        var_2 = 1;
                    } else {
                        //   151: aload_3
                        //   152: getfield songId : Ljava/lang/String;
                        //   155: aload_0
                        //   156: getfield playlistNoSongsId : Ljava/lang/String;
                        //   159: invokevirtual contains : (Ljava/lang/CharSequence;)Z
                        //   162: ifeq -> 529

                        if (this.playlistNoSongsId.contains(paramMusicMessage.info.song.songId)) {
                            //   529: iconst_0
                            //   530: istore_2 //local variable 2 = 0
                            //   531: goto -> 165
                            var_2 = 0;
                        }
                    }

                    //   165: iload #4
                    //   167: ifne -> 174
                    if (var_4 != 0) {
                        Log.d("ReconMusicPlayer", "No song id passed. not loading any song");
                    } else {
                        //   170: iload_2
                        //   171: ifeq -> 534
                        if (var_2 == 0) {
                            //   534: aload_0
                            //   535: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
                            //   538: aload_3
                            //   539: getfield songId : Ljava/lang/String;
                            //   542: aload_3
                            //   543: getfield srcType : Lcom/reconinstruments/connect/music/MusicDBFrontEnd$MusicListType;
                            //   546: aload_3
                            //   547: getfield srcId : Ljava/lang/String;

                            //(Context paramContext, String paramString1, MusicDBFrontEnd.MusicListType paramMusicListType, String paramString2) {
                            Cursor cursor = MusicCursorGenerator.getNewSongListSelectionCursor(mediaSrvc.getBaseContext(), paramMusicMessage.info.song.srcId, MusicDBFrontEnd.MusicListType.SONGS, null);

                            //   550: invokestatic getNewSongListSelectionCursor : (Landroid/content/Context;Ljava/lang/String;Lcom/reconinstruments/connect/music/MusicDBFrontEnd$MusicListType;Ljava/lang/String;)Landroid/database/Cursor;
                            //   553: astore_3
                            //   554: invokestatic flagToGenerateNewShuffleCursor : ()V
                            flagToGenerateNewShuffleCursor();
                            this.loadSongWithCursor(cursor);
                            //   557: aload_0
                            //   558: aload_3
                            //   559: invokevirtual loadSongWithCursor : (Landroid/database/Cursor;)V
                            //   562: goto -> 183
                        }
                    }


                    //   174: ldc 'ReconMusicPlayer'
                    //   176: ldc_w 'no song id passed. not loading any song'

                    //   179: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
                    //   182: pop
                }
            }

            if (paramMusicMessage.info.state != null) {
                //   183: aload_1
                //   184: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                //   187: getfield state : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;

                //   190: ifnull -> 248
                //   193: aload_1
                //   194: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                //   197: getfield state : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
                //paramMusicMessage.info.state;
                //   200: aload_0
                //   201: invokevirtual getPlayerState : ()Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
                //   204: if_acmpeq -> 606
                if(getPlayerState().equals(paramMusicMessage.info.state))
                {
                    //   606: aload_0
                    //   607: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
                    //   610: invokevirtual updatePlayerUI : ()V
                    //   613: goto -> 248

                    this.mediaSrvc.updatePlayerUI();
                }
                else
                {
                    //   207: getstatic com/reconinstruments/mobilesdk/mediaplayer/ReconMusicPlayer$2.$SwitchMap$com$reconinstruments$connect$messages$MusicMessage$PlayerState : [I
                    //   210: aload_1
                    //   211: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                    //   214: getfield state : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
                    //   217: invokevirtual ordinal : ()I
                    //   220: iaload
                    //   221: tableswitch default -> 248, 1 -> 584, 2 -> 591, 3 -> 599
                    switch(paramMusicMessage.info.state)
                    {
                        case PAUSED:
                            //   584: aload_0
                            //   585: invokevirtual playSong : ()V
                            //   588: goto -> 248
                            this.playSong();
                            break;
                        case PLAYING:
                            //   591: aload_0
                            //   592: iconst_0
                            //   593: invokespecial pauseSong : (Z)V
                            //   596: goto -> 248
                            this.pauseSong(false);
                            break;

                        case STOPPED:
                            //   599: aload_0
                            //   600: invokevirtual stop : ()V
                            //   603: goto -> 248
                            this.stop();
                            break;
                    }

                }

            }

            //   248: aload_1
            //   249: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
            //   252: getfield volume : Ljava/lang/Float;
            if(paramMusicMessage.info.volume!=null)
            {


            //   255: ifnull -> 348
            //   258: aload_1
            //   259: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
            //   262: getfield volume : Ljava/lang/Float;
            //   265: invokevirtual floatValue : ()F
                paramMusicMessage.info.volume.floatValue();
            //   268: aload_0
            //   269: getfield audioManager : Landroid/media/AudioManager;
            //   272: iconst_3
            //   273: invokevirtual getStreamMaxVolume : (I)I
            //   276: i2f
            //   277: fmul
            //   278: f2i
            //   279: istore #4
            //   281: aload_0
            //   282: getfield audioManager : Landroid/media/AudioManager;
            //   285: iconst_3
            //   286: iload #4
            //   288: iconst_4
            //   289: invokevirtual setStreamVolume : (III)V
                int indexVolume=Math.round(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*paramMusicMessage.info.volume.floatValue());

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,indexVolume, AudioManager.FLAG_SHOW_UI);

                //   292: new java/lang/StringBuilder
                //   295: astore_3
                //   296: aload_3
                //   297: invokespecial <init> : ()V
                //   300: ldc 'ReconMusicPlayer'
                //   302: aload_3
                //   303: ldc_w 'Volume set to: '
                //   306: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   309: aload_1
                //   310: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                //   313: getfield volume : Ljava/lang/Float;
                //   316: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
                //   319: ldc_w ' index: '
                //   322: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
                //   325: iload #4
                //   327: invokevirtual append : (I)Ljava/lang/StringBuilder;
                //   330: invokevirtual toString : ()Ljava/lang/String;
                //   333: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I

                sb = new StringBuilder("");
                sb.append("Volume set to: ");
                sb.append(paramMusicMessage.info.volume);
                sb.append(" index: ");
                sb.append(indexVolume);
                Log.d("ReconMusicPlayer", sb.toString());

                //   336: pop
                //   337: aload_0
                //   338: fconst_1
                //   339: fconst_1
                //   340: invokevirtual setVolume : (FF)V
                //   343: aload_0
                //   344: iconst_0
                //   345: putfield mute : Z

                setVolume(1.0f, 1.0f);
                mute = false;
            }

            //   348: aload_1
            //   349: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
            //   352: getfield progress : Ljava/lang/Integer;
            //   355: ifnull -> 372

            if(paramMusicMessage.info.progress!=null)
            {


                //   358: aload_0
                //   359: aload_1
                //   360: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                //   363: getfield progress : Ljava/lang/Integer;
                //   366: invokevirtual intValue : ()I
                //   369: invokevirtual seekTo : (I)V
                seekTo(paramMusicMessage.info.progress.intValue());

            }
            //   372: aload_1
            //   373: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
            //   376: getfield shuffle : Ljava/lang/Boolean;
            //   379: ifnull -> 396
            if(paramMusicMessage.info.shuffle!=null) {
                //   382: aload_0
                //   383: aload_1
                //   384: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                //   387: getfield shuffle : Ljava/lang/Boolean;
                //   390: invokevirtual booleanValue : ()Z
                //   393: putfield shuffle : Z
                shuffle = paramMusicMessage.info.shuffle.booleanValue();
            }
            //   396: aload_1
            //   397: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
            //   400: getfield loop : Ljava/lang/Boolean;
            //   403: ifnull -> 420
            if(paramMusicMessage.info.loop!=null) {
                //   406: aload_0
                //   407: aload_1
                //   408: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                //   411: getfield loop : Ljava/lang/Boolean;
                //   414: invokevirtual booleanValue : ()Z
                //   417: putfield loop : Z
                loop = paramMusicMessage.info.loop.booleanValue();
            }
            //   420: aload_1
            //   421: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
            //   424: getfield mute : Ljava/lang/Boolean;
            //   427: ifnull -> 457
            if(paramMusicMessage.info.mute!=null) {
                //   430: aload_0
                //   431: aload_1
                //   432: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
                //   435: getfield mute : Ljava/lang/Boolean;
                //   438: invokevirtual booleanValue : ()Z
                //   441: putfield mute : Z
                mute = paramMusicMessage.info.mute.booleanValue();
                //   444: aload_0
                //   445: getfield mute : Z
                if(!mute) {
                    //   448: ifeq -> 616
                        //   616: aload_0
                        //   617: fconst_1
                        //   618: fconst_1
                        //   619: invokevirtual setVolume : (FF)V
                        //   622: goto -> 457
                    setVolume(1.0f, 1.0f);
                    //   451: aload_0
                    //   452: fconst_0
                    //   453: fconst_0
                    //   454: invokevirtual setVolume : (FF)V
                    setVolume(0.0f, 0.0f);
                }
            }
        }

        //   457: aload_1
        //   458: getfield action : Lcom/reconinstruments/connect/messages/MusicMessage$Action;
        //   461: ifnull -> 520
        if(paramMusicMessage.action!=null)
        {
            return;
        }
        //   464: getstatic com/reconinstruments/mobilesdk/mediaplayer/ReconMusicPlayer$2.$SwitchMap$com$reconinstruments$connect$messages$MusicMessage$Action : [I
        //   467: aload_1
        //   468: getfield action : Lcom/reconinstruments/connect/messages/MusicMessage$Action;
        //   471: invokevirtual ordinal : ()I
        //   474: iaload
        //   475: istore #4
        //   477: iload #4
        //   479: tableswitch default -> 520, 1 -> 625(start song), 2 -> 632(next song), 3 -> 639(previous song), 4 -> 646(player state), 5 -> 656(vol down), 6 -> 671(vol up), 7 -> 686(toggle pause)
        /*
        public enum Action {
            GET_PLAYER_STATE, NEXT_SONG, PREVIOUS_SONG, START_SONG, TOGGLE_PAUSE, VOLUME_DOWN, VOLUME_UP;
            START_SONG=1, NEXT_SONG=2, PREVIOUS_SONG=3,  GET_PLAYER_STATE=4, VOLUME_DOWN=5, VOLUME_UP=6, TOGGLE_PAUSE=7;

            static {
                //!!!!
                //START_SONG = new Action("START_SONG", 2);
                //PREVIOUS_SONG = new Action("PREVIOUS_SONG", 3);
                //NEXT_SONG = new Action("NEXT_SONG", 4);
                //TOGGLE_PAUSE = new Action("TOGGLE_PAUSE", 5);
                //GET_PLAYER_STATE = new Action("GET_PLAYER_STATE", 6);
                //$VALUES = new Action[] { VOLUME_UP, VOLUME_DOWN, START_SONG, PREVIOUS_SONG, NEXT_SONG, TOGGLE_PAUSE, GET_PLAYER_STATE };
            }
        }
        */
        switch(paramMusicMessage.action)
        {
            default:
                break;

        //   520: aload_0
        //   521: monitorexit
        //   522: return

        //   523: iconst_0
        //   524: istore #4
        //   526: goto -> 138

        //   529: iconst_0
        //   530: istore_2
        //   531: goto -> 165

        //   534: aload_0
        //   535: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   538: aload_3
        //   539: getfield songId : Ljava/lang/String;
        //   542: aload_3
        //   543: getfield srcType : Lcom/reconinstruments/connect/music/MusicDBFrontEnd$MusicListType;
        //   546: aload_3
        //   547: getfield srcId : Ljava/lang/String;
        //   550: invokestatic getNewSongListSelectionCursor : (Landroid/content/Context;Ljava/lang/String;Lcom/reconinstruments/connect/music/MusicDBFrontEnd$MusicListType;Ljava/lang/String;)Landroid/database/Cursor;
        //   553: astore_3
        //   554: invokestatic flagToGenerateNewShuffleCursor : ()V
        //   557: aload_0
        //   558: aload_3
        //   559: invokevirtual loadSongWithCursor : (Landroid/database/Cursor;)V
        //   562: goto -> 183

        //   565: astore_1
        //   566: aload_0
        //   567: monitorexit
        //   568: aload_1
        //   569: athrow
        //   570: aload_0
        //   571: aload_1
        //   572: getfield info : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerInfo;
        //   575: getfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
        //   578: putfield song : Lcom/reconinstruments/connect/messages/MusicMessage$SongInfo;
        //   581: goto -> 183

        //   584: aload_0
        //   585: invokevirtual playSong : ()V
        //   588: goto -> 248

        //   591: aload_0
        //   592: iconst_0
        //   593: invokespecial pauseSong : (Z)V
        //   596: goto -> 248

        //   599: aload_0
        //   600: invokevirtual stop : ()V
        //   603: goto -> 248

        //   606: aload_0
        //   607: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   610: invokevirtual updatePlayerUI : ()V
        //   613: goto -> 248

        //   616: aload_0
        //   617: fconst_1
        //   618: fconst_1
        //   619: invokevirtual setVolume : (FF)V
        //   622: goto -> 457

        //   625: aload_0
        //   626: invokevirtual playSong : ()V
        //   629: goto -> 520
            case START_SONG:
                this.playSong();
                break;
        //   632: aload_0
        //   633: invokevirtual nextSong : ()V
        //   636: goto -> 520
            case NEXT_SONG:
                this.playSong();
                break;
        //   639: aload_0
        //   640: invokevirtual previousSong : ()V
        //   643: goto -> 520
            case PREVIOUS_SONG:
                this.previousSong();
                break;
        //   646: aload_0
        //   647: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   650: invokevirtual updatePlayerUI : ()V
        //   653: goto -> 520
            case GET_PLAYER_STATE:
                this.mediaSrvc.updatePlayerUI();
                break;
        //   656: aload_0
        //   657: iconst_0
        //   658: putfield mute : Z
        //   661: aload_0
        //   662: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   665: invokevirtual updatePlayerUI : ()V
        //   668: goto -> 520
            case VOLUME_DOWN:
                this.mute=false;
                break;
        //   671: aload_0
        //   672: iconst_0
        //   673: putfield mute : Z
        //   676: aload_0
        //   677: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
        //   680: invokevirtual updatePlayerUI : ()V
        //   683: goto -> 520
            case VOLUME_UP:
                this.mute=false;
                break;
        //   686: aload_0
        //   687: invokevirtual togglePause : ()V
        //   690: goto -> 520
            case TOGGLE_PAUSE:
                this.togglePause();
                break;
        }
        return;

        // Exception table:
        //   from	to	target	type
        //   4	116	565	finally
        //   116	135	565	finally
        //   138	165	565	finally
        //   174	183	565	finally
        //   183	248	565	finally
        //   248	348	565	finally
        //   348	372	565	finally
        //   372	396	565	finally
        //   396	420	565	finally
        //   420	457	565	finally
        //   457	477	565	finally
        //   534	562	565	finally
        //   570	581	565	finally
        //   584	588	565	finally
        //   591	596	565	finally
        //   599	603	565	finally
        //   606	613	565	finally
        //   616	622	565	finally
        //   625	629	565	finally
        //   632	636	565	finally
        //   639	643	565	finally
        //   646	653	565	finally
        //   656	668	565	finally
        //   671	683	565	finally
        //   686	690	565	finally
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
        // Byte code:
        //   0: aload_0
        //   1: getfield focusChangeListener : Lcom/reconinstruments/mobilesdk/mediaplayer/ReconMusicPlayer$FocusChangeListener;
        //   4: ifnull -> 21
        if(focusChangeListener!=null) {
            //   7: aload_0
            //   8: getfield audioManager : Landroid/media/AudioManager;
            //   11: aload_0
            //   12: getfield focusChangeListener : Lcom/reconinstruments/mobilesdk/mediaplayer/ReconMusicPlayer$FocusChangeListener;
            //   15: iconst_3
            //   16: iconst_1
            //   17: invokevirtual requestAudioFocus : (Landroid/media/AudioManager$OnAudioFocusChangeListener;II)I
            //   20: pop
            //OnAudioFocusChangeListener l, int streamType, int durationHint)
            audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        synchronized (this) {
        //   21: aload_0
        //   22: monitorenter
        //   23: aload_0
        //   24: ifnonnull -> 30
        //   27: aload_0
        //   28: monitorexit
        //   29: return
        //   30: aload_0
        //   31: invokevirtual start : ()V

            start();
            //   34: aload_0
            //   35: getstatic com/reconinstruments/connect/messages/MusicMessage$PlayerState.PLAYING : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
            //   38: putfield playerState : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
            playerState = MusicMessage.PlayerState.PLAYING;
            //   41: aload_0
            //   42: getfield mediaSrvc : Lcom/reconinstruments/mobilesdk/mediaplayer/MediaPlayerService;
            //   45: invokevirtual updatePlayerUI : ()V
            mediaSrvc.updatePlayerUI();
            //   48: aload_0
            //   49: monitorexit
            //   50: goto -> 29
        }

        //   53: astore_1
        //   54: aload_0
        //   55: monitorexit
        //   56: aload_1
        //   57: athrow
        // Exception table:
        //   from	to	target	type
        //   27	29	53	finally
        //   30	50	53	finally
        //   54	56	53	finally
    }

    public void previousSong() {
        // Byte code:
        //   0: aload_0
        //   1: getfield playerState : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   4: getstatic com/reconinstruments/connect/messages/MusicMessage$PlayerState.PLAYING : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   7: if_acmpeq -> 20
        if(playerState!=MusicMessage.PlayerState.PLAYING) {
            //   10: aload_0
            //   11: getfield playerState : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
            //   14: getstatic com/reconinstruments/connect/messages/MusicMessage$PlayerState.PAUSED : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
            //   17: if_acmpne -> 42
            if(playerState!=MusicMessage.PlayerState.PAUSED) {
                switchTrack(false);
                return;
            }
        }
        synchronized (this)
        {
            if(getCurrentPosition()>=3000)
            {
                seekTo(0);
                return;
            }
            else
            {
                switchTrack(false);
                return;
            }
        }
        //   20: aload_0
        //   21: monitorenter
        //   22: aload_0
        //   23: invokevirtual getCurrentPosition : ()I
        //   26: sipush #3000
        //   29: if_icmplt -> 40
        //   32: aload_0
        //   33: iconst_0
        //   34: invokevirtual seekTo : (I)V
        //   37: aload_0
        //   38: monitorexit
        //   39: return
        //   40: aload_0
        //   41: monitorexit
        //   42: aload_0
        //   43: iconst_0
        //   44: invokespecial switchTrack : (Z)V
        //   47: goto -> 39
        //   50: astore_1
        //   51: aload_0
        //   52: monitorexit
        //   53: aload_1
        //   54: athrow
        // Exception table:
        //   from	to	target	type
        //   22	39	50	finally
        //   40	42	50	finally
        //   51	53	50	finally
    }

    public void setPlayerState(MusicMessage.PlayerState paramPlayerState) {
        this.playerState = paramPlayerState;
    }

    public void stopSong() {
        // Byte code:
        //   0: aload_0
        //   1: getfield focusChangeListener : Lcom/reconinstruments/mobilesdk/mediaplayer/ReconMusicPlayer$FocusChangeListener;
        //   4: ifnull -> 19
        if(focusChangeListener!=null) {

        }
        //   7: aload_0
        //   8: getfield audioManager : Landroid/media/AudioManager;
        //   11: aload_0
        //   12: getfield focusChangeListener : Lcom/reconinstruments/mobilesdk/mediaplayer/ReconMusicPlayer$FocusChangeListener;
        //   15: invokevirtual abandonAudioFocus : (Landroid/media/AudioManager$OnAudioFocusChangeListener;)I
        //   18: pop
        //   19: aload_0
        //   20: monitorenter
        //   21: aload_0
        //   22: ifnonnull -> 28
        //   25: aload_0
        //   26: monitorexit
        //   27: return
        //   28: aload_0
        //   29: getfield playerState : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   32: getstatic com/reconinstruments/connect/messages/MusicMessage$PlayerState.PLAYING : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   35: if_acmpeq -> 48
        //   38: aload_0
        //   39: getfield playerState : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   42: getstatic com/reconinstruments/connect/messages/MusicMessage$PlayerState.PAUSED : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   45: if_acmpne -> 52
        //   48: aload_0
        //   49: invokevirtual stop : ()V
        //   52: aload_0
        //   53: monitorexit
        //   54: goto -> 27
        //   57: astore_1
        //   58: aload_0
        //   59: monitorexit
        //   60: aload_1
        //   61: athrow
        // Exception table:
        //   from	to	target	type
        //   25	27	57	finally
        //   28	48	57	finally
        //   48	52	57	finally
        //   52	54	57	finally
        //   58	60	57	finally
    }

    public void toggleLoop(boolean paramBoolean) {
        this.loop = paramBoolean;
        this.mediaSrvc.updatePlayerUI();
    }

    public void togglePause() {
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: aload_0
        //   3: invokevirtual getPlayerState : ()Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   6: getstatic com/reconinstruments/connect/messages/MusicMessage$PlayerState.PLAYING : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   9: if_acmpne -> 20
        //   12: aload_0
        //   13: iconst_0
        //   14: invokespecial pauseSong : (Z)V
        //   17: aload_0
        //   18: monitorexit
        //   19: return
        //   20: aload_0
        //   21: invokevirtual getPlayerState : ()Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   24: getstatic com/reconinstruments/connect/messages/MusicMessage$PlayerState.PAUSED : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   27: if_acmpne -> 42
        //   30: aload_0
        //   31: invokevirtual playSong : ()V
        //   34: goto -> 17
        //   37: astore_1
        //   38: aload_0
        //   39: monitorexit
        //   40: aload_1
        //   41: athrow
        //   42: aload_0
        //   43: invokevirtual getPlayerState : ()Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   46: astore_1
        //   47: getstatic com/reconinstruments/connect/messages/MusicMessage$PlayerState.STOPPED : Lcom/reconinstruments/connect/messages/MusicMessage$PlayerState;
        //   50: astore_2
        //   51: aload_1
        //   52: aload_2
        //   53: if_acmpne -> 17
        //   56: aload_0
        //   57: invokevirtual prepare : ()V
        //   60: aload_0
        //   61: iconst_0
        //   62: invokevirtual seekTo : (I)V
        //   65: aload_0
        //   66: invokevirtual playSong : ()V
        //   69: goto -> 17
        //   72: astore_1
        //   73: aload_0
        //   74: invokevirtual reset : ()V
        //   77: aload_1
        //   78: invokevirtual printStackTrace : ()V
        //   81: goto -> 17
        // Exception table:
        //   from	to	target	type
        //   2	17	37	finally
        //   17	19	37	finally
        //   20	34	37	finally
        //   38	40	37	finally
        //   42	51	37	finally
        //   56	69	72	java/io/IOException
        //   56	69	37	finally
        //   73	81	37	finally
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
