package com.reconinstruments.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Chus on 20/08/2023.
 */

public class BluetoothHelper {
    public static final String APP_MSG = "BLUETOOTH_CONTROL_MESSAGE";
    public static final String BT_STATE_UPDATED = "BLUETOOTH_STATE_UPDATED";
    public static final String TAG = "BluetoothHelper";

    /* loaded from: classes.dex */
    public enum AppMSG {
        NONE,
        START_CONNECT,
        START_LISTEN,
        STOP,
        START_DOWNLOAD,
        SYNC_FILE
    }

    public static void connectBT(Context context) {
        messageFrontEnd(context, AppMSG.START_CONNECT, ConnectionManager.BTType.BT_CHAT, null);
    }

    public static void connectFT(Context context) {
        messageFrontEnd(context, AppMSG.START_CONNECT, ConnectionManager.BTType.BT_FILETRANSFER, null);
    }

    public static void listenBT(Context context) {
        messageFrontEnd(context, AppMSG.START_LISTEN, ConnectionManager.BTType.BT_CHAT, null);
    }

    public static void listenFT(Context context) {
        messageFrontEnd(context, AppMSG.START_LISTEN, ConnectionManager.BTType.BT_FILETRANSFER, null);
    }

    public static void stopBT(Context context) {
        messageFrontEnd(context, AppMSG.STOP, ConnectionManager.BTType.BT_CHAT, null);
    }

    public static void stopFT(Context context) {
        messageFrontEnd(context, AppMSG.STOP, ConnectionManager.BTType.BT_FILETRANSFER, null);
    }

    public static void messageFrontEnd(Context context, AppMSG message, ConnectionManager.BTType type, Bundle extras) {
        Intent myi = new Intent();
        myi.setAction(APP_MSG);
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putSerializable("message", message);
        extras.putSerializable("type", type);
        myi.putExtras(extras);
        context.sendBroadcast(myi);
        Log.d(TAG, "messageFrontEnd: " + message + " type: " + type.name());
    }
}
