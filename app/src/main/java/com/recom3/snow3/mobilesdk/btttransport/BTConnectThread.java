package com.recom3.snow3.mobilesdk.btttransport;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import com.recom3.snow3.mobilesdk.HUDConnectivityService;

import java.io.IOException;

/**
 * Created by Recom3 on 27/01/2022.
 * Reference:
 * https://developer.android.com/guide/topics/connectivity/bluetooth/connect-bluetooth-devices
 */

class BTConnectThread extends Thread {
    private static final String TAG = "BTConnectThread";

    private int connectionAttempts = 1;

    private BluetoothAdapter mAdapter;

    private final HUDConnectivityService.Channel mChannel;

    private BTTransportManager mManager;

    //private final BluetoothSocket mSocket = null;
    private BluetoothSocket mSocket = null;

    BTConnectThread(BluetoothAdapter paramBluetoothAdapter, BluetoothDevice bluetoothDevice, BTTransportManager paramBTTransportManager, HUDConnectivityService.Channel paramChannel, String uuid, int paramInt) {
        this.mAdapter = paramBluetoothAdapter;
        this.mManager = paramBTTransportManager;
        this.connectionAttempts = paramInt;
        this.mChannel = paramChannel;

        BluetoothSocket tmp = null;
        //BluetoothDevice bluetoothDevice = this.mAdapter.getRemoteDevice(uuid);

        try {
            Log.i(TAG, "createRfcommSocketToServiceRecord");
            switch (this.mChannel) {
                default:
                    //Reference from internet
                    //https://stackoverflow.com/questions/36457842/what-is-the-my-uuid-used-as-a-parameter-in-device-createrfcommsockettoservicerec
                    //ParcelUuid[] idArray = bluetoothDevice.getUuids();
                    //java.util.UUID uuidYouCanUse = java.util.UUID.fromString(idArray[0].toString());
                    //this.mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidYouCanUse);
                    return;
                case COMMAND_CHANNEL:
                    this.mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(BTTransportManager.UUID_COMMAND);
                    break;
                case OBJECT_CHANNEL:
                    this.mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(BTTransportManager.UUID_OBJECT);
                    break;
                case FILE_CHANNEL:
                    this.mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(BTTransportManager.UUID_FILE);
                    break;
            }
        } catch (IOException iOException) {
            Log.w(TAG, "create() failed. " + iOException.getMessage());
            iOException.printStackTrace();
        }
    }

    void cancel() {
        Log.d("BTConnectThread", "cancel " + this);
        try {
            this.mSocket.close();
        } catch (IOException iOException) {
            Log.w("BTConnectThread", "close() of connect socket failed. " + iOException.getMessage());
            iOException.printStackTrace();
        }
    }

    public void run() {

        if(true){
            // Cancel discovery because it otherwise slows down the connection.
            mAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            //manageMyConnectedSocket(mmSocket);
            this.mManager.connected(mChannel, mSocket);

        }
        //Alternative implementation
        else {

            setName("BTConnectThread " + this);
            Log.d(TAG, "BEGIN " + getName());
            this.mAdapter.cancelDiscovery();
            int count = 0;
            boolean success = false;

            while (this.mSocket != null && count <= this.connectionAttempts && !success) {
                try {
                    Log.d(TAG, "mSocket.connect() " + getName());
                    this.mSocket.connect();
                    success = true;
                    count = 0;
                } catch (IOException e) {
                    Log.d(TAG, "IOException: Could not connect()");
                    if (count == this.connectionAttempts) {
                        try {
                            Log.d(TAG, "mSocket.close() " + getName());
                            this.mSocket.close();
                        } catch (IOException e2) {
                            Log.w(TAG, "unable to close() socket during connection failure. " + e2.getMessage());
                            e2.printStackTrace();
                        }
                        this.mManager.connectionFailed();
                        return;
                    }
                }
                Log.d(TAG, "Finished attempt " + count + " to connect " + getName() + ". success: " + success);
                count++;
                if (count >= 3) {
                    try {
                        Thread.sleep(3 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e3) {
                        e3.printStackTrace();
                    }
                }
            }
            this.mManager.connected(this.mChannel, this.mSocket);

        }
    }
}
