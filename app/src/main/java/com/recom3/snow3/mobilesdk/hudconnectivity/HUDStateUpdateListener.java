package com.recom3.snow3.mobilesdk.hudconnectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by Recom3 on 27/01/2022.
 */

public abstract class HUDStateUpdateListener extends BroadcastReceiver {
    private static final String TAG = HUDStateUpdateListener.class.getSimpleName();

    private final String key = "HUD_STATE_CHANGED";

    private final String keyword = "state";

    public abstract void onHUDStateUpdate(HUD_STATE paramHUD_STATE);

    public void onReceive(Context paramContext, Intent paramIntent) {
        int i = paramIntent.getExtras().getInt("state");
        if (i > (HUD_STATE.values()).length - 1) {
            Log.w(TAG, String.format("Received unknown %s intent with state: %d", new Object[] { "HUD_STATE_CHANGED", Integer.valueOf(i) }));
            return;
        }
        onHUDStateUpdate(HUD_STATE.values()[i]);
    }

    public void startListening(Context paramContext) {
        paramContext.registerReceiver(this, new IntentFilter("HUD_STATE_CHANGED"));
    }

    public void stopListening(Context paramContext) {
        paramContext.unregisterReceiver(this);
    }

    public enum HUD_STATE {
        DISCONNECTED, CONNECTING, CONNECTED;

        static {
            //!!!!
            //CONNECTED = new HUD_STATE("CONNECTED", 2);
            //$VALUES = new HUD_STATE[] { DISCONNECTED, CONNECTING, CONNECTED };
        }
    }
}
