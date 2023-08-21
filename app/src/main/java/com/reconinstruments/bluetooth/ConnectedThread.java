package com.reconinstruments.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Chus on 20/08/2023.
 */

public abstract class ConnectedThread extends BluetoothThread {
    protected InputStream btInStream;
    protected OutputStream btOutStream;

    public ConnectedThread(ConnectionManager connMgr) {
        super(connMgr, ConnectionManager.ThreadType.CONNECTED);
        this.btInStream = null;
        this.btOutStream = null;
    }

    public boolean getStreams() {
        try {
            this.btInStream = ((BluetoothSocket) this.socket).getInputStream();
            this.btOutStream = ((BluetoothSocket) this.socket).getOutputStream();
            return true;
        } catch (IOException e) {
            Log.d(this.TAG, getName() + ": failed to get streams", e);
            return false;
        } catch (NullPointerException e2) {
            Log.d(this.TAG, getName() + ": failed to get streams", e2);
            return false;
        }
    }

    @Override // com.reconinstruments.bluetooth.BluetoothThread
    public void cancel() {
        super.cancel();
        closeSocketSafe();
    }

    public void write(byte[] buffer, int index, int size) {
        Log.v(this.TAG, getName() + ".write(bufferSize: " + buffer.length + ", index: " + index + ", size: " + size + ")");
        try {
            this.btOutStream.write(buffer, index, size);
        } catch (IOException e) {
            Log.d(this.TAG, getName() + ": Exception during write", e);
        }
    }

    public void write(int write) {
        try {
            this.btOutStream.write(write);
        } catch (IOException e) {
            Log.d(this.TAG, getName() + ": Exception during write", e);
        }
    }
}