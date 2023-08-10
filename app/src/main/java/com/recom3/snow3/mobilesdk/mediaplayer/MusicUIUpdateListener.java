package com.recom3.snow3.mobilesdk.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Recom3 on 27/01/2022.
 */

public abstract class MusicUIUpdateListener extends BroadcastReceiver {
    public static String keyword = "success";

    private String key = "RECON_MUSIC_UI_UPDATE";

    public abstract void onMusicUIUpdate(boolean paramBoolean);

    public void onReceive(Context paramContext, Intent paramIntent) {
        onMusicUIUpdate(paramIntent.getExtras().getBoolean(keyword));
    }

    public void startListening(Context paramContext) {
        paramContext.registerReceiver(this, new IntentFilter(this.key));
    }

    public void stopListening(Context paramContext) {
        paramContext.unregisterReceiver(this);
    }
}
