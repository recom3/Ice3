package com.reconinstruments.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by recom3 on 20/08/2023.
 */

public abstract class BluetoothThread extends Thread {
    protected String TAG;
    protected boolean cancelled;
    protected ConnectionManager connMgr;
    protected BluetoothService service;
    protected Closeable socket;
    protected ConnectionManager.ThreadType type;

    public BluetoothThread(ConnectionManager connMgr, ConnectionManager.ThreadType type) {
        super(type.name() + "-" + connMgr.type.name());
        this.TAG = "BluetoothThread";
        this.cancelled = false;
        this.type = type;
        this.connMgr = connMgr;
        this.service = connMgr.service;
    }

    public void cancel() {
        Log.i(this.TAG, getName() + " cancelling thread");
        interrupt();
        this.cancelled = true;
    }

    public void threadFinished() {
        Log.i(this.TAG, getName() + ": thread finished");
        this.connMgr.threadMessageHandler.sendEmptyMessageDelayed(0, 100L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void connectionEnded() {
        Log.i(this.TAG, getName() + ": Bluetooth Connection ended.");
        this.connMgr.nextThread = ConnectionManager.ThreadType.NONE;
    }

    public void closeSocket() {
        if (this.socket != null) {
            synchronized (this.socket) {
                Log.i(this.TAG, getName() + ".closeSocket(" + this.socket + ")");
                try {
                    this.socket.close();
                    this.socket = null;
                    Log.i(this.TAG, getName() + " closed socket");
                } catch (IOException e) {
                    Log.i(this.TAG, getName() + ": closeSocket() failed", e);
                } catch (NullPointerException e2) {
                    Log.i(this.TAG, getName() + " no socket");
                }
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [com.reconinstruments.bluetooth.BluetoothThread$1] */
    public void closeSocketSafe() {
        if (this.socket == null) {
            Log.i(this.TAG, getName() + ": socket null.");
            return;
        }
        this.connMgr.setState(ConnectionManager.ConnectState.DISCONNECTING);
        Log.i(this.TAG, getName() + ": Attempting to close connect socket");
        new Thread() { // from class: com.reconinstruments.bluetooth.BluetoothThread.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                setName("CloseBluetoothSocketThread");
                try {
                    if (BluetoothThread.this.socket != null) {
                        synchronized (BluetoothThread.this.socket) {
                            Log.i(BluetoothThread.this.TAG, getName() + ": closing socket (thread)");
                            ((BluetoothSocket) BluetoothThread.this.socket).getInputStream().close();
                            ((BluetoothSocket) BluetoothThread.this.socket).getOutputStream().close();
                            BluetoothThread.this.socket.close();
                            BluetoothThread.this.socket = null;
                            Log.i(BluetoothThread.this.TAG, getName() + ": socket closed.");
                        }
                    }
                } catch (IOException e) {
                    Log.i(BluetoothThread.this.TAG, getName() + ": close() of connect socket failed", e);
                } catch (NullPointerException e2) {
                    Log.i(BluetoothThread.this.TAG, getName() + ": close() of connect socket failed", e2);
                }
            }
        }.start();
    }

    public void sleepThread(int milli) {
        Log.v(this.TAG, getName() + ".sleepThread(" + milli + ")");
        try {
            sleep(milli);
        } catch (InterruptedException e) {
            Log.i(this.TAG, getName() + ": Failed to sleepThread(" + milli + ")", e);
        }
    }
}
