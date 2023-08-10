package com.recom3.mobilesdk.buddytracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * Created by Recom3 on 25/05/2023.
 */

public abstract class BuddyUpdateListener extends BroadcastReceiver {
    private String DELTA_BUDDY_JSON_KEY = "buddiesXml";

    public abstract void onBuddiesUpdated(String str);

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context c, Intent intent) {
        Bundle b = intent.getExtras();
        String deltaBuddies = b.getString(this.DELTA_BUDDY_JSON_KEY);
        onBuddiesUpdated(deltaBuddies);
    }

    public void startListening(Context c) {
        c.registerReceiver(this, new IntentFilter(BuddyService.BUDDIES_UPDATED));
    }

    public void stopListening(Context c) {
        c.unregisterReceiver(this);
    }
}