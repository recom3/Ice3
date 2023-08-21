package com.reconinstruments.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.reconinstruments.connect.util.BluetoothUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Chus on 20/08/2023.
 */

public class ConnectThread extends BluetoothThread {
    private static final String TAG = "ConnectThread";
    private final BluetoothDevice device;

    public ConnectThread(BluetoothDevice device, ConnectionManager service) {
        super(service, ConnectionManager.ThreadType.CONNECT);
        Log.d(TAG, getName() + "(" + device + ") pairing state " + BluetoothUtils.getBondStateName(device.getBondState()));
        this.device = device;
    }

    public BluetoothSocket createSocket() {
        UUID uuid = this.connMgr.getUUID();
        try {
            BluetoothSocket socket = this.device.createRfcommSocketToServiceRecord(uuid);
            Log.d(TAG, getName() + ": created socket with UUID: " + uuid);
            return socket;
        } catch (IOException e) {
            Log.d(TAG, getName() + ": failed to createRfCommSocket with UUID: " + uuid, e);
            this.connMgr.setState(ConnectionManager.ConnectState.ERROR);
            return null;
        }
    }

    public BluetoothSocket createReflection(int channel) {
        BluetoothSocket tmp = null;
        try {
            Log.d(TAG, getName() + ": trying to create socket using reflection on channel " + channel);
            Method m = this.device.getClass().getMethod("createRfcommSocket", Integer.TYPE);
            try {
                tmp = (BluetoothSocket) m.invoke(this.device, Integer.valueOf(channel));
                Log.d(TAG, getName() + ": created socket on channel " + channel);
                return tmp;
            } catch (Exception e) {
                Log.d(TAG, getName() + ": failed to createRfCommSocket via reflection", e);
                return tmp;
            }
        } catch (NoSuchMethodException e2) {
            Log.d(TAG, getName() + ": failed to get method createRfCommSocket via reflection", e2);
            return tmp;
        } catch (SecurityException e3) {
            Log.d(TAG, getName() + ": failed to get method createRfCommSocket via reflection", e3);
            return tmp;
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Log.d(TAG, getName() + ": run()");
        this.socket = createSocket();
        if (this.socket != null) {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            int channel = 1;
            this.connMgr.setState(ConnectionManager.ConnectState.CONNECTING);
            while (true) {
                if (this.connMgr.getState() != ConnectionManager.ConnectState.CONNECTING || this.cancelled) {
                    break;
                }
                try {
                    this.connMgr.setState(ConnectionManager.ConnectState.CONNECTING);
                    Log.d(TAG, getName() + ": Attempting to connect.");
                    ((BluetoothSocket) this.socket).connect();
                    connected();
                    break;
                } catch (IOException e) {
                    Log.d(TAG, getName() + ": connection error " + e.getMessage());
                    if (this.cancelled || channel >= 4) {
                        break;
                    }
                    this.socket = createReflection(channel);
                    if (this.socket == null) {
                        Log.d(TAG, getName() + " run() failed, no socket");
                        break;
                    }
                    channel++;
                    sleepThread(500);
                    if (!BluetoothUtils.isDevicePaired(this.device.getAddress())) {
                        Log.d(TAG, getName() + ": device is not paired");
                        break;
                    }
                }
            }
        }
        threadFinished();
    }

    @Override // com.reconinstruments.bluetooth.BluetoothThread
    public synchronized void cancel() {
        super.cancel();
        closeSocketSafe();
    }

    public void connected() {
        Log.d(TAG, getName() + ": Successfully Connected to " + this.device.getName() + "!!!");
        if (this.connMgr.type == ConnectionManager.BTType.BT_CHAT) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, this + ": Connecting File Transfer");
            BluetoothHelper.connectFT(this.connMgr.service);
        }
        this.connMgr.socket = (BluetoothSocket) this.socket;
        this.connMgr.nextThread = ConnectionManager.ThreadType.CONNECTED;
    }
}
