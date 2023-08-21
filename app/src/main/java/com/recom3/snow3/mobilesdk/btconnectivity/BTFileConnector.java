package com.recom3.snow3.mobilesdk.btconnectivity;

import android.content.Context;
import android.util.Log;

import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.btttransport.BTTransportManager;

/**
 * Created by Recom3 on 24/07/2022.
 */

public class BTFileConnector extends BTConnector {

    private static final String TAG = "BTFileConnector";

    BTFileConnector(Context paramContext, BTTransportManager paramBTTransportManager) {
        super(paramContext, HUDConnectivityService.Channel.FILE_CHANNEL, paramBTTransportManager);
    }

    boolean processing(QueueMessage paramQueueMessage) {
        Log.i("BTFileConnector", "Processing the message " + paramQueueMessage.toString());
        //if (BTMfiSessionManager.getInstance(this.mContext).isInUse()) {
        //    Log.i("BTObjectConnector", "Sending the message data to iOS device: " + paramQueueMessage.toString());
        //    return BTMfiSessionManager.getInstance(this.mContext).sendSessionData(HUDConnectivityService.Channel.FILE_CHANNEL, paramQueueMessage.toByteArray());
        //}
        Log.i("BTFileConnector", "Sending the message data: " + paramQueueMessage.toString());
        return this.mBTTransportManager.write(HUDConnectivityService.Channel.FILE_CHANNEL, paramQueueMessage.toByteArray());
    }
}
