package com.reconinstruments.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.reconinstruments.bluetooth.chat.ChatConnectedThread;
import com.reconinstruments.bluetooth.filetransfer.FileTransferThread;

import java.util.UUID;

/**
 * Created by Chus on 20/08/2023.
 */

public class ConnectionManager {
    public static final UUID BT_UUID = UUID.fromString("9a69f8d0-9c5e-11e0-aa82-0800200c9a66");
    public static final UUID FT_UUID = UUID.fromString("9a69f8d0-9c5e-11e0-aa82-0800200c9a65");
    public static final String SDPNAMEBT = "BluetoothChat";
    public static final String SDPNAMEFT = "FileTransfer";
    protected BluetoothService service;
    protected BluetoothSocket socket;
    Handler threadMessageHandler;
    public BTType type;
    protected String TAG = "BluetoothConnectionManager";
    public BluetoothThread currentThread = null;
    protected ConnectState state = ConnectState.NONE;
    protected ThreadType nextThread = ThreadType.NONE;

    /* loaded from: classes.dex */
    public enum BTType {
        BT_CHAT,
        BT_FILETRANSFER
    }

    /* loaded from: classes.dex */
    public enum ConnectState {
        NONE,
        LISTEN,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        ERROR
    }

    /* loaded from: classes.dex */
    public enum ThreadType {
        NONE,
        ACCEPT,
        CONNECT,
        CONNECTED
    }

    public ConnectionManager(BluetoothService service, BTType type) {
        Log.d(this.TAG, type.name() + " constructor called");
        this.type = type;
        this.service = service;
        this.threadMessageHandler = new Handler() { // from class: com.reconinstruments.bluetooth.ConnectionManager.1
            @Override // android.os.Handler
            public synchronized void handleMessage(Message msg) {
                ConnectionManager.this.currentThread = null;
                ConnectionManager.this.startNextThread();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void startNextThread() {
        if (this.currentThread != null) {
            cancelThread();
        } else {
            Log.d(this.TAG, this.type.name() + " starting thread: " + this.nextThread.name());
            switch (this.nextThread) {
                case NONE:
                    setState(ConnectState.NONE);
                    break;
                case ACCEPT:
                    this.currentThread = new AcceptThread(this);
                    this.currentThread.start();
                    break;
                case CONNECT:
                    if (this.service.device != null) {
                        this.currentThread = new ConnectThread(this.service.device, this);
                        this.currentThread.start();
                        break;
                    } else {
                        Log.d(this.TAG, this.type.name() + " Can't start connect thread, device is null!");
                        setState(ConnectState.NONE);
                        break;
                    }
                case CONNECTED:
                    if (this.socket != null) {
                        if (this.type == BTType.BT_CHAT) {
                            this.currentThread = new ChatConnectedThread(this.socket, this);
                        } else {
                            this.currentThread = new FileTransferThread(this.socket, this);
                        }
                        this.currentThread.start();
                        break;
                    } else {
                        Log.d(this.TAG, this.type.name() + " Can't start connected thread, socket is null!");
                        setState(ConnectState.NONE);
                        break;
                    }
            }
            this.nextThread = ThreadType.NONE;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void startListening() {
        Log.d(this.TAG, "startListening()");
        this.nextThread = ThreadType.ACCEPT;
        startNextThread();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void connect() {
        this.nextThread = ThreadType.CONNECT;
        startNextThread();
    }

    public void cancelThread() {
        if (this.currentThread != null) {
            this.currentThread.cancel();
        }
    }

    public synchronized void setState(ConnectState newState) {
        if (this.state != newState) {
            Log.d(this.TAG, this.type.name() + " setState() " + this.state.name() + " -> " + newState.name());
            this.state = newState;
            this.service.onStateChanged();
        }
    }

    public ConnectState getState() {
        return this.state;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isConnected() {
        return this.currentThread != null && (this.currentThread instanceof ConnectedThread);
    }

    public UUID getUUID() {
        switch (this.type) {
            case BT_CHAT:
                return BT_UUID;
            case BT_FILETRANSFER:
                return FT_UUID;
            default:
                return null;
        }
    }

    public String getSDPName() {
        switch (this.type) {
            case BT_CHAT:
                return SDPNAMEBT;
            case BT_FILETRANSFER:
                return SDPNAMEFT;
            default:
                return null;
        }
    }
}
