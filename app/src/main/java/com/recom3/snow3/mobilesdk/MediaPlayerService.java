package com.recom3.snow3.mobilesdk;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.recom3.connect.messages.MusicMessage;
import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDStateUpdateListener;
import com.recom3.snow3.mobilesdk.mediaplayer.DBBuilderTask;
import com.recom3.snow3.mobilesdk.mediaplayer.DBManager;
import com.recom3.snow3.mobilesdk.mediaplayer.IDBManager;
import com.recom3.snow3.mobilesdk.mediaplayer.MediaManager;
import com.recom3.snow3.mobilesdk.mediaplayer.MusicUIUpdateListener;
import com.recom3.snow3.mobilesdk.mediaplayer.ReconMusicPlayer;

import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Recom3 on 27/01/2022.
 */

public abstract class MediaPlayerService extends EngageSdkService {
    private static final String TAG = "MediaPlayerServiceSDK";

    public static final String UI_UPDATE_MESSAGE = "RECON_MUSIC_UI_UPDATE";

    static final String failedMODLiveRequest = "FAILED_DB_REQUEST";

    public static HUDConnectivityService hudSrvc = null;

    static final String localChecksumMODLiveRequest = "LOCAL_DB_CHECKSUM_REQUEST";

    static final String mPath = "com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService";

    static final String remoteChecksumMODLiveRequest = "REMOTE_DB_CHECKSUM_REQUEST";

    static final String successMODLiveRequest = "SUCCESS_DB_REQUEST";

    static final String uploadLocalDbRequest = "UPLOAD_DB_REQUEST";

    BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            if (param1Intent.getAction().equals("RECON_MUSIC_MESSAGE")) {
                Bundle bundle = param1Intent.getExtras();
                if (bundle != null) {
                    String str = new String((new HUDConnectivityMessage(bundle.getByteArray("message"))).getData());
                    Log.i("MediaPlayerServiceSDK", "music message received :" + str);
                    MusicMessage musicMessage = new MusicMessage(str);
                    if (musicMessage != null && musicMessage.type == MusicMessage.Type.CONTROL)
                        MediaPlayerService.this.mMediaManager.getMPlayer().gotMessage(musicMessage);
                }
                return;
            }
            if (param1Intent.getAction().equals("REMOTE_DB_CHECKSUM_REQUEST")) {
                Log.i("MediaPlayerServiceSDK", "received broadcast with intent: " + param1Intent.getAction());
                Bundle bundle = param1Intent.getExtras();
                if (bundle != null) {
                    HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage(bundle.getByteArray("message"));
                    String str = MediaPlayerService.this.parseSimpleMessageForValue(new String(hUDConnectivityMessage.getData()), "synced");
                    Log.i("MediaPlayerServiceSDK", "match: " + str);
                    if (!str.equals("true")) {
                        MediaPlayerService.this.getDbMgr().pushGoggleDB();
                        Log.i("MediaPlayerServiceSDK", "mismatch, pushed db to goggles");
                        return;
                    }
                    Log.i("MediaPlayerServiceSDK", "goggles have correct DB!");
                }
                return;
            }
            if (param1Intent.getAction().equals("LOCAL_DB_CHECKSUM_REQUEST")) {
                Log.i("MediaPlayerServiceSDK", "received broadcast with intent: " + param1Intent.getAction());
                Bundle bundle = param1Intent.getExtras();
                if (bundle != null) {
                    HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage(bundle.getByteArray("message"));
                    String str = MediaPlayerService.this.parseSimpleMessageForValue(new String(hUDConnectivityMessage.getData()), "remoteChecksum");
                    Log.i("MediaPlayerServiceSDK", "remoteChecksum: " + str);
                    if (MediaPlayerService.this.getDbMgr().performOwnChecksumForGoggle().equals(str)) {
                        Log.i("MediaPlayerServiceSDK", "goggles have correct DB!");
                        return;
                    }
                    if (MediaPlayerService.this.getDbMgr().getMusicDBBuilderState().equals(DBManager.DBState.READY)) {
                        MediaPlayerService.this.getDbMgr().buildGoggleDb();
                        Log.i("MediaPlayerServiceSDK", "request for building db for goggles");
                    }
                }
                return;
            }
            if (param1Intent.getAction().equals("SUCCESS_DB_REQUEST")) {
                //!!!
                //MediaPlayerService.access$102(MediaPlayerService.this, 0);
                MediaPlayerService.this.countSyncRetries = 0;
                return;
            }
            if (param1Intent.getAction().equals("FAILED_DB_REQUEST")) {
                Log.i("MediaPlayerServiceSDK", "received broadcast with intent: " + param1Intent.getAction());
                Log.w("MediaPlayerServiceSDK", "failed uploading DB!");
                if (MediaPlayerService.this.countSyncRetries < 5) {
                    MediaPlayerService.this.getDbMgr().buildMusicDB();
                    //!!!!
                    //MediaPlayerService.access$108(MediaPlayerService.this);
                    Log.i("MediaPlayerServiceSDK", "reattempting to uploading DB by building it again.");
                    return;
                }
                Log.e("MediaPlayerServiceSDK", "Failed to upload DB 5 times, stopping now.");
                return;
            }
            if (param1Intent.getAction().equals("com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService")) {
                Log.i("MediaPlayerServiceSDK", "received broadcast with intent: " + param1Intent.getAction());
                if (param1Intent.getBooleanExtra("result", false)) {
                    Log.i("MediaPlayerServiceSDK", "message was successfully sent");
                    return;
                }
                Log.e("MediaPlayerServiceSDK", "message was NOT sent");
            }
        }
    };

    boolean connected = false;

    private int countSyncRetries = 0;

    DBManager dbMgr;

    private ServiceConnection hudConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
            MediaPlayerService.hudSrvc = ((HUDConnectivityService.LocalBinder)param1IBinder).getService();
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            MediaPlayerService.hudSrvc = null;
        }
    };

    private HUDStateUpdateListener hudListener = new HUDStateUpdateListener() {
        public void onHUDStateUpdate(HUDStateUpdateListener.HUD_STATE param1HUD_STATE) {
            boolean bool;
            if (!MediaPlayerService.this.connected && param1HUD_STATE == HUDStateUpdateListener.HUD_STATE.CONNECTED)
                MediaPlayerService.this.getDbMgr().buildMusicDB();
            MediaPlayerService mediaPlayerService = MediaPlayerService.this;
            if (param1HUD_STATE == HUDStateUpdateListener.HUD_STATE.CONNECTED) {
                bool = true;
            } else {
                bool = false;
            }
            mediaPlayerService.connected = bool;
        }
    };

    private Class<? extends HUDConnectivityService> hudServiceClass;

    MediaManager mMediaManager;

    BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            String str = param1Intent.getAction();
            Log.i("MediaPlayerServiceSDK", str);
            if (str.equals("android.intent.action.MEDIA_UNMOUNTED") || str.equals("android.intent.action.MEDIA_EJECT")) {
                if (MediaPlayerService.this.mMediaManager.getMPlayer().getPlayerState() != MusicMessage.PlayerState.NOT_INIT)
                    MediaPlayerService.this.mMediaManager.getMPlayer().setPlayerState(MusicMessage.PlayerState.NOT_INIT);
                return;
            }
            if (str.equals("android.intent.action.MEDIA_MOUNTED")) {
                if (MediaPlayerService.this.mMediaManager.getMPlayer().getPlayerState() != MusicMessage.PlayerState.STOPPED)
                    MediaPlayerService.this.mMediaManager.getMPlayer().setPlayerState(MusicMessage.PlayerState.STOPPED);
                MediaPlayerService.this.getDbMgr().buildMusicDB();
            }
        }
    };

    ContentObserver musicObserver = new ContentObserver(null) {
        public void onChange(boolean param1Boolean) {
            super.onChange(param1Boolean);
            MediaPlayerService.this.getDbMgr().buildMusicDB();
        }
    };

    BroadcastReceiver volChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            MediaPlayerService.this.broadcastStatus(new MusicMessage.PlayerInfo(null, null, Float.valueOf(MediaPlayerService.this.getVolume()), null, null, null, null));
        }
    };

    public MediaPlayerService(Class<? extends HUDConnectivityService> paramClass) {
        this.hudServiceClass = paramClass;
    }

    private void broadcastStatus(MusicMessage.PlayerInfo paramPlayerInfo) {
        HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
        hUDConnectivityMessage.setIntentFilter("RECON_MUSIC_MESSAGE");
        hUDConnectivityMessage.setRequestKey(0);
        hUDConnectivityMessage.setSender("com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService");
        hUDConnectivityMessage.setData((new MusicMessage(paramPlayerInfo, MusicMessage.Type.STATUS)).toXML().getBytes());
        Log.i("MediaPlayerServiceSDK", "size of music status message: " + (hUDConnectivityMessage.toByteArray()).length);
        hudSrvc.push(hUDConnectivityMessage, HUDConnectivityService.Channel.OBJECT_CHANNEL);
    }

    private float getVolume() {
        return (getMediaManager().getMPlayer()).audioManager.getStreamVolume(3) / (getMediaManager().getMPlayer()).audioManager.getStreamMaxVolume(3);
    }

    private String parseSimpleMessageForValue(String paramString1, String paramString2) {
        String str;
        try {
            InputSource inputSource = new InputSource();
            //this();
            StringReader stringReader = new StringReader(paramString1);
            //this(paramString1);
            inputSource.setCharacterStream(stringReader);
            str = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon").item(0).getFirstChild().getAttributes().getNamedItem(paramString2).getNodeValue();
            StringBuilder stringBuilder = new StringBuilder();
            //this();
            Log.i("MediaPlayerServiceSDK", stringBuilder.append("handled ").append(paramString2).append(": ").append(paramString1).toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            str = "";
        }
        return str;
    }

    public DBManager getDbMgr() {
        return this.dbMgr;
    }

    public List<ServiceDependency> getDependentServices() {
        ArrayList<ServiceDependency> arrayList = new ArrayList();
        Class<? extends HUDConnectivityService> clazz = this.hudServiceClass;
        ServiceConnection serviceConnection = this.hudConnection;
        if (hudSrvc != null) {
            boolean bool1 = true;
            arrayList.add(new EngageSdkService.ServiceDependency(clazz, serviceConnection, bool1));
            return arrayList;
        }
        boolean bool = false;
        arrayList.add(new EngageSdkService.ServiceDependency(clazz, serviceConnection, bool));
        return arrayList;
    }

    public MediaManager getMediaManager() {
        return this.mMediaManager;
    }

    public void onCreate() {
        super.onCreate();
        this.mMediaManager = new MediaManager(this);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addDataScheme("file");
        registerReceiver(this.mediaReceiver, intentFilter);
        getContentResolver().registerContentObserver(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, this.musicObserver);
        getContentResolver().registerContentObserver(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, true, this.musicObserver);
        registerReceiver(this.bluetoothReceiver, new IntentFilter("RECON_MUSIC_MESSAGE"));
        registerReceiver(this.bluetoothReceiver, new IntentFilter("REMOTE_DB_CHECKSUM_REQUEST"));
        registerReceiver(this.bluetoothReceiver, new IntentFilter("LOCAL_DB_CHECKSUM_REQUEST"));
        registerReceiver(this.bluetoothReceiver, new IntentFilter("com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService"));
        registerReceiver(this.bluetoothReceiver, new IntentFilter("FAILED_DB_REQUEST"));
        registerReceiver(this.bluetoothReceiver, new IntentFilter("SUCCESS_DB_REQUEST"));
        registerReceiver(this.volChangeReceiver, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
        this.hudListener.startListening((Context)this);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mediaReceiver);
        getContentResolver().unregisterContentObserver(this.musicObserver);
        unregisterReceiver(this.bluetoothReceiver);
        unregisterReceiver(this.volChangeReceiver);
        if ((this.mMediaManager.getMPlayer()).focusChangeListener != null)
            (this.mMediaManager.getMPlayer()).audioManager.abandonAudioFocus((this.mMediaManager.getMPlayer()).focusChangeListener);
        DBBuilderTask dBBuilderTask = getDbMgr().getBuilder();
        if (dBBuilderTask != null && dBBuilderTask.getStatus() == AsyncTask.Status.RUNNING) {
            unregisterReceiver(dBBuilderTask.bluetoothMessageReceiver);
            DBBuilderTask.registered = false;
        }
        this.mMediaManager.getMPlayer().release();
        this.hudListener.stopListening((Context)this);
        unbindDependentServices();
        hudSrvc = null;
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        return 1;
    }

    public void setDbMgr(IDBManager paramIDBManager) {
        this.dbMgr = new DBManager(paramIDBManager, (Context)this);
    }

    public void updatePlayerUI() {
        Integer integer;
        sendBroadcast((new Intent("RECON_MUSIC_UI_UPDATE")).putExtra(MusicUIUpdateListener.keyword, true));
        float f = (getMediaManager().getMPlayer()).audioManager.getStreamVolume(3) / (getMediaManager().getMPlayer()).audioManager.getStreamMaxVolume(3);
        if (getMediaManager().getMPlayer().getPlayerState() == MusicMessage.PlayerState.PLAYING || getMediaManager().getMPlayer().getPlayerState() == MusicMessage.PlayerState.PAUSED) {
            integer = Integer.valueOf(getMediaManager().getMPlayer().getCurrentPosition());
        } else {
            integer = null;
        }
        ReconMusicPlayer reconMusicPlayer = getMediaManager().getMPlayer();
        broadcastStatus(new MusicMessage.PlayerInfo(reconMusicPlayer.getPlayerState(), reconMusicPlayer.song, Float.valueOf(f), integer, Boolean.valueOf(reconMusicPlayer.isShuffle()), Boolean.valueOf(reconMusicPlayer.isLoop()), Boolean.valueOf(reconMusicPlayer.isMute())));
    }
}
