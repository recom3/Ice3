package com.recom3.snow3.mobilesdk.btttransport;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.recom3.snow3.mobilesdk.HUDConnectivityService;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Recom3 on 27/01/2022.
 */

class BTAcceptThread extends Thread {
    private static final String TAG = "BTAcceptThread";

    private BluetoothAdapter mAdapter;

    private HUDConnectivityService.Channel mChannel;

    private BTTransportManager mManager;

    private String mName;

    private BluetoothServerSocket mServerSocket;

    private UUID mUuid;

    BTAcceptThread(BluetoothAdapter paramBluetoothAdapter, BTTransportManager paramBTTransportManager, HUDConnectivityService.Channel paramChannel, String paramString, UUID paramUUID) {
        this.mAdapter = paramBluetoothAdapter;
        this.mManager = paramBTTransportManager;
        this.mUuid = paramUUID;
        this.mChannel = paramChannel;
        byte b = 0;
        paramBluetoothAdapter = null;
        while (this.mServerSocket == null && b < 3) {
            BluetoothServerSocket bluetoothServerSocket = null;
            boolean bool;
            try {
                StringBuilder stringBuilder1 = new StringBuilder();
                //this();
                Log.i("BTAcceptThread", stringBuilder1.append("listenUsingRfcommWithServiceRecord on ").append(paramString).toString());
                BluetoothServerSocket bluetoothServerSocket1 = this.mAdapter.listenUsingRfcommWithServiceRecord(paramString, paramUUID);
                bluetoothServerSocket = bluetoothServerSocket1;
            } catch (IOException iOException) {
                Log.w("BTAcceptThread", "listenUsingRfcommWithServiceRecord on " + paramString + " failed. " + iOException.getMessage());
                iOException.printStackTrace();
            }
            //!!!! maybe not initialized
            this.mServerSocket = bluetoothServerSocket;
            Log.i("BTAcceptThread", "channel " + paramChannel.name() + " attempt:" + b);
            StringBuilder stringBuilder = (new StringBuilder()).append("socket null: ");
            if (this.mServerSocket == null) {
                bool = true;
            } else {
                bool = false;
            }
            Log.i("BTAcceptThread", stringBuilder.append(bool).toString());
            b++;
        }
        if (this.mServerSocket == null) {
            Log.e("BTAcceptThread", "server socket is still null after 3 attempts!!!");
            this.mManager.showBtErrorToastToUser();
        }
    }

    private BTTransportManager.ConnectionState getChannelState() {
        BTTransportManager.ConnectionState connectionState = null;
        if (BTTransportManager.UUID_COMMAND.compareTo(this.mUuid) == 0) {
            connectionState = this.mManager.getState(HUDConnectivityService.Channel.COMMAND_CHANNEL);
        } else if (BTTransportManager.UUID_OBJECT.compareTo(this.mUuid) == 0) {
            connectionState = this.mManager.getState(HUDConnectivityService.Channel.OBJECT_CHANNEL);
        } else if (BTTransportManager.UUID_FILE.compareTo(this.mUuid) == 0) {
            connectionState = this.mManager.getState(HUDConnectivityService.Channel.FILE_CHANNEL);
        }
        if (connectionState == null)
            Log.w("BTAcceptThread", "getChannelState() return invalid state.");
        return connectionState;
    }

    void cancel() {
        Log.i("BTAcceptThread", "cancel " + this);
        try {
            if (this.mServerSocket != null) {
                this.mServerSocket.close();
                this.mServerSocket = null;
            }
        } catch (IOException iOException) {
            Log.w("BTAcceptThread", "close() of server failed. " + iOException.getMessage());
            iOException.printStackTrace();
        }
    }

    public void run() {
        setName("BTAcceptThread " + this.mChannel.name() + " " + this);
        Log.i(TAG, "BEGIN " + getName());
        Log.w(TAG, "mSErverSocket is null : " + (this.mServerSocket == null));
        while (true) {
            if (this.mServerSocket != null) {
                try {
                    Log.i(TAG, "mServerSocket.accept()");
                    if (this.mServerSocket != null) {
                        BluetoothSocket socket = this.mServerSocket.accept();
                        Log.i(TAG, this.mChannel.name() + " connecting request came in");
                        if (socket != null) {
                            Log.i(TAG, "Accepted incoming request from " + socket.getRemoteDevice().getName());
                            this.mManager.resetDeviceType();
                            synchronized (this.mManager) {
                                switch (getChannelState()) {
                                    case LISTEN:
                                    case CONNECTING:
                                        if (BTTransportManager.UUID_COMMAND.compareTo(this.mUuid) == 0) {
                                            this.mManager.connected(HUDConnectivityService.Channel.COMMAND_CHANNEL, socket);
                                            break;
                                        } else if (BTTransportManager.UUID_OBJECT.compareTo(this.mUuid) == 0) {
                                            this.mManager.connected(HUDConnectivityService.Channel.OBJECT_CHANNEL, socket);
                                            break;
                                        } else if (BTTransportManager.UUID_FILE.compareTo(this.mUuid) == 0) {
                                            this.mManager.connected(HUDConnectivityService.Channel.FILE_CHANNEL, socket);
                                            break;
                                        }
                                        break;
                                    case NONE:
                                    case CONNECTED:
                                        try {
                                            Log.i(TAG, "closing the socket on " + this.mUuid + ". ");
                                            socket.close();
                                            break;
                                        } catch (IOException e) {
                                            Log.w(TAG, "Could not close unwanted socket on " + this.mUuid + ". " + e.getMessage());
                                            e.printStackTrace();
                                            break;
                                        }
                                }
                            }
                        } else {
                            Log.w(TAG, this.mChannel.name() + " socket is null");
                        }
                    } else {
                        Log.w(TAG, "mServerSocket is null at this point, the hudservice has to restart");
                    }
                } catch (IOException e2) {
                    Log.w(TAG, "accept() on " + this.mUuid + " failed. " + e2.getMessage());
                    e2.printStackTrace();
                }
            }
        }
        //Log.i(TAG, "END BTAcceptThread");
    }
}
