package com.reconinstruments.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Chus on 20/08/2023.
 */

public class AcceptThread extends BluetoothThread {
    public AcceptThread(ConnectionManager service) {
        super(service, ConnectionManager.ThreadType.ACCEPT);
    }

    public BluetoothServerSocket createServerSocket() {
        UUID uuid = this.connMgr.getUUID();
        String SDPName = this.connMgr.getSDPName();
        Log.d(this.TAG, getName() + " create(" + uuid + "," + SDPName + ")");
        try {
            return BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(SDPName, uuid);
        } catch (IOException e) {
            Log.d(this.TAG, getName() + ": failed to open server socket", e);
            return null;
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Log.d(this.TAG, getName() + ": run()");
        this.socket = createServerSocket();
        if (this.socket != null) {
            this.connMgr.setState(ConnectionManager.ConnectState.LISTEN);
            try {
                BluetoothSocket btSocket = ((BluetoothServerSocket) this.socket).accept();
                connected(btSocket);
            } catch (IOException e) {
                Log.d(this.TAG, getName() + ": socket.accept() failed", e);
            }
            closeSocket();
        } else {
            Log.d(this.TAG, getName() + ": server socket is null!");
        }
        threadFinished();
    }

    @Override // com.reconinstruments.bluetooth.BluetoothThread
    public synchronized void cancel() {
        super.cancel();
        closeSocket();
    }

    public synchronized void connected(BluetoothSocket socket) {
        Log.d(this.TAG, getName() + ": Successfully accepted connection from " + socket.getRemoteDevice().getName() + "!!!");
        this.connMgr.socket = socket;
        this.connMgr.nextThread = ConnectionManager.ThreadType.CONNECTED;
        this.connMgr.service.saveDevice(socket.getRemoteDevice());
    }
}