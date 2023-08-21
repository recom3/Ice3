package com.recom3.snow3.mobilesdk.btconnectivity;

import android.content.Context;
import android.util.Log;

import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.btmfi.BTMfiSessionManager;
import com.recom3.snow3.mobilesdk.btttransport.BTTransportManager;

/**
 * Created by Recom3 on 12/04/2022.
 */

class BTCommandConnector extends BTConnector {
    private static final String TAG = "BTCommandConnector";

    BTCommandConnector(Context paramContext, BTTransportManager paramBTTransportManager) {
        super(paramContext, HUDConnectivityService.Channel.COMMAND_CHANNEL, paramBTTransportManager);
    }

    boolean processing(QueueMessage paramQueueMessage) {
        Log.i("BTCommandConnector", "Processing the message " + paramQueueMessage.toString());

        //if (BTMfiSessionManager.getInstance(this.mContext).isInUse()) {
        //    Log.d("BTCommandConnector", "Sending the message data to iOS device: " + paramQueueMessage.toString());
        //    return BTMfiSessionManager.getInstance(this.mContext).sendSessionData(HUDConnectivityService.Channel.COMMAND_CHANNEL, paramQueueMessage.toByteArray());
        //}

        Log.i("BTCommandConnector", "Sending the message data: " + paramQueueMessage.toString());
        return this.mBTTransportManager.write(HUDConnectivityService.Channel.COMMAND_CHANNEL, paramQueueMessage.toByteArray());
    }
}
