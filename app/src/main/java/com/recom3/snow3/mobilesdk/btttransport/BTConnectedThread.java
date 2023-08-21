package com.recom3.snow3.mobilesdk.btttransport;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.recom3.snow3.mobilesdk.HUDConnectivityService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Recom3 on 27/01/2022.
 */

class BTConnectedThread extends Thread {
    private static final int BUFFSIZE = 25600;

    private static final String TAG = "BTConnectedThread";

    private HUDConnectivityService.Channel mChannel;

    private InputStream mInStream = null;

    private BTTransportManager mManager;

    private OutputStream mOutStream = null;

    private final BluetoothSocket mSocket;

    BTConnectedThread(BTTransportManager paramBTTransportManager, BluetoothSocket paramBluetoothSocket, HUDConnectivityService.Channel paramChannel) {
        OutputStream outputStream1 = null;
        HUDConnectivityService.Channel channel = null;
        OutputStream outputStream2;
        this.mManager = paramBTTransportManager;
        this.mSocket = paramBluetoothSocket;
        this.mChannel = paramChannel;
        paramBTTransportManager = null;
        paramChannel = null;
        try {
            InputStream inputStream2 = this.mSocket.getInputStream();
            //InputStream inputStream1 = inputStream2;
            this.mInStream = inputStream2;
            OutputStream outputStream = this.mSocket.getOutputStream();
            //outputStream1 = outputStream;
            this.mOutStream = outputStream;
        } catch (IOException iOException) {
            Log.e("BTConnectedThread", "temp sockets not created. " + iOException.getMessage());
            iOException.printStackTrace();
            //!!!! not ini
            //outputStream2 = outputStream1;
            //channel = paramChannel;
        }
        //!!!! commented
        //this.mInStream = (InputStream)outputStream2;
        //!!!! commented
        //this.mOutStream = (OutputStream)channel;
    }

    void cancel() {
        Log.i("BTConnectedThread", "cancel " + this);
        try {
            this.mSocket.close();
        } catch (IOException iOException) {
            Log.w("BTConnectedThread", "close() of connect socket failed. " + iOException.getMessage());
            iOException.printStackTrace();
        }
    }

    public void run() {
        setName("BTConnectedThread " + this.mChannel.name() + " " + this);
        Log.i("BTConnectedThread", "BEGIN " + getName());
        this.mManager.mHandler.obtainMessage(5, this.mChannel.name() + " connected to " + this.mSocket.getRemoteDevice().getName()).sendToTarget();
        this.mManager.connectionSuccessed(this.mChannel, this.mSocket.getRemoteDevice());
        byte[] arrayOfByte = new byte[25600];
        while (true) {
            try {
                int i = this.mInStream.read(arrayOfByte);
                if (i > 0)
                    if (this.mChannel.equals(HUDConnectivityService.Channel.FILE_CHANNEL)) {
                        Log.i("BTConnectedThread", "Going to write file to storage and pass pointer");
                        this.mManager.mHandler.obtainMessage(7, i, -1, arrayOfByte.clone()).sendToTarget();
                    } else {
                        this.mManager.mHandler.obtainMessage(2, i, -1, arrayOfByte.clone()).sendToTarget();
                    }
                arrayOfByte = new byte[25600];
                try {
                    //Original implment were 50
                    //Thread.sleep(50L);
                    Thread.sleep(100L);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            } catch (IOException iOException) {
                Log.w("BTConnectedThread", "connection lost: " + iOException.getMessage());
                iOException.printStackTrace();
                this.mManager.connectionLost();
                return;
            }
        }
    }

    void write(byte[] paramArrayOfbyte) {
        try {
            this.mOutStream.write(paramArrayOfbyte);
            this.mOutStream.flush();
        } catch (IOException iOException) {
            Log.w("BTConnectedThread", "Exception during write." + iOException.getMessage());
            iOException.printStackTrace();
        }
    }
}