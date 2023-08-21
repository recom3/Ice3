package com.reconinstruments.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.recom3.snow3.mobilesdk.hudconnectivity.ConnectHelper;
import com.recom3.snow3.mobilesdk.messages.XMLMessage;
import com.reconinstruments.bluetooth.chat.ChatConnectedThread;
import com.reconinstruments.bluetooth.chat.ChatManager;
import com.reconinstruments.bluetooth.filetransfer.FileManager;
import com.reconinstruments.connect.apps.ConnectedDevice;
import com.reconinstruments.connect.apps.DeviceInfo;
import com.reconinstruments.connect.messages.DeviceInfoMessage;
import com.reconinstruments.connect.util.BluetoothUtils;

/**
 * Created by recom3 on 20/08/2023.
 */

public abstract class BluetoothService extends Service {
    public static final int BT_BUFF_LEN = 40960;
    public static boolean btReset = false;
    protected BluetoothDevice device;
    protected String TAG = BluetoothService.class.getSimpleName();
    public ChatManager chatMgr = null;
    public FileManager fileMgr = null;
    private boolean wasConnected = false;
    BroadcastReceiver msgReceiver = new BroadcastReceiver() { // from class: com.reconinstruments.bluetooth.BluetoothService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ConnectHelper.GEN_MSG)) {
                BluetoothService.this.writeXMLMessage(intent.getStringExtra("message"));
            } else if (intent.getAction().equals(XMLMessage.BT_CONNECT_MESSAGE)) {
                DeviceInfo info = DeviceInfoMessage.parse(intent.getExtras().getString("message"));
                ConnectedDevice.updateDeviceInfo(BluetoothService.this, info);
            } else if (intent.getAction().equals(XMLMessage.TRANSFER_REQUEST_MESSAGE) || intent.getAction().equals(XMLMessage.TRANSFER_RESPONSE_MESSAGE)) {
                BluetoothService.this.fileMgr.handleFileTransferMessage(intent.getAction(), intent.getExtras().getString("message"));
            } else if (intent.getAction().equals(BluetoothHelper.APP_MSG)) {
                Bundle bundle = intent.getExtras();
                BluetoothHelper.AppMSG what = (BluetoothHelper.AppMSG) bundle.getSerializable("message");
                ConnectionManager.BTType type = (ConnectionManager.BTType) bundle.getSerializable("type");
                Log.i(BluetoothService.this.TAG, "Message from app: " + what.name());
                switch (AnonymousClass2.$SwitchMap$com$reconinstruments$bluetooth$BluetoothHelper$AppMSG[what.ordinal()]) {
                    case 1:
                        BluetoothService.this.connect(type);
                        break;
                    case 2:
                        BluetoothService.this.listen(type);
                        break;
                    case 3:
                        BluetoothService.this.stop(type);
                        break;
                }
                if (type == ConnectionManager.BTType.BT_FILETRANSFER) {
                    BluetoothService.this.fileMgr.handleAppMessage(what, bundle);
                }
            } else if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int state = intent.getExtras().getInt("android.bluetooth.adapter.extra.STATE");
                Log.i(BluetoothService.this.TAG, "Bluetooth State changed: " + BluetoothUtils.getBluetoothStateName(state));
                if (state == 12) {
                    BluetoothService.this.onInit();
                    BluetoothService.this.onBTEnabled();
                } else if (state == 13) {
                    BluetoothService.this.stop(ConnectionManager.BTType.BT_CHAT);
                    BluetoothService.this.stop(ConnectionManager.BTType.BT_FILETRANSFER);
                    Log.i(BluetoothService.this.TAG, "Bluetooth turning off, stopping bluetooth threads");
                } else if (state == 10 && BluetoothService.btReset) {
                    Log.i(BluetoothService.this.TAG, "listener: Bluetooth disabled by connection timeout, re-enabling");
                    BluetoothAdapter.getDefaultAdapter().enable();
                    BluetoothService.btReset = false;
                }
            }
        }
    };
    private final IBinder mBinder = new LocalBinder();

    public abstract void clearNotifications();

    public abstract String getDeviceInfoMessage();

    public abstract void onBTEnabled();

    public abstract void onDisconnect();

    public abstract void onInit();

    public abstract void saveDevice(BluetoothDevice bluetoothDevice);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        Log.i(this.TAG, "onCreate()");
        IntentFilter filter = new IntentFilter(ConnectHelper.GEN_MSG);
        filter.addAction(BluetoothHelper.APP_MSG);
        filter.addAction(XMLMessage.BT_CONNECT_MESSAGE);
        filter.addAction(XMLMessage.TRANSFER_REQUEST_MESSAGE);
        filter.addAction(XMLMessage.TRANSFER_RESPONSE_MESSAGE);
        filter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(this.msgReceiver, filter);
        clearNotifications();
        this.chatMgr = new ChatManager(this);
        this.fileMgr = new FileManager(this);
        try {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                onInit();
            }
        } catch (NullPointerException e) {
            Log.e(this.TAG, "NullPointer exception in bluetooth");
            e.printStackTrace();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Log.i(this.TAG, "onDestroy!");
        this.chatMgr.cancelThread();
        this.fileMgr.cancelThread();
        unregisterReceiver(this.msgReceiver);
        clearNotifications();
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(this.TAG, "onStartCommand! StartId: " + startId + " Intent: " + intent);
        return START_STICKY;
    }

    public boolean connect(ConnectionManager.BTType type) {
        String addr = ConnectedDevice.getMACAddress(this);
        if (BluetoothAdapter.checkBluetoothAddress(addr)) {
            this.device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addr);
            Log.i(this.TAG, type.name() + " connect(" + this.device.getAddress() + ")");
            getService(type).connect();
            return true;
        }
        Log.i(this.TAG, "Device not found, invalid bluetooth address: " + addr);
        return false;
    }

    public boolean listen(ConnectionManager.BTType type) {
        getService(type).startListening();
        return true;
    }

    public void stop(ConnectionManager.BTType type) {
        getService(type).cancelThread();
    }

    public ConnectionManager.ConnectState getState(ConnectionManager.BTType type) {
        return getService(type).getState();
    }

    public ConnectionManager getService(ConnectionManager.BTType type) {
        if (type == ConnectionManager.BTType.BT_CHAT) {
            return this.chatMgr;
        }
        if (type == ConnectionManager.BTType.BT_FILETRANSFER) {
            return this.fileMgr;
        }
        return null;
    }

    public boolean isConnected() {
        return this.chatMgr.isConnected() && this.fileMgr.isConnected();
    }

    public void onStateChanged() {
        boolean isConnected = isConnected();
        Intent stateMessage = new Intent();
        stateMessage.setAction(BluetoothHelper.BT_STATE_UPDATED);
        stateMessage.putExtra(ConnectedDevice.COL_CHAT_STATE, getState(ConnectionManager.BTType.BT_CHAT));
        stateMessage.putExtra(ConnectedDevice.COL_FT_STATE, getState(ConnectionManager.BTType.BT_FILETRANSFER));
        sendBroadcast(stateMessage);
        if (getState(ConnectionManager.BTType.BT_CHAT) == ConnectionManager.ConnectState.NONE && getState(ConnectionManager.BTType.BT_FILETRANSFER) != ConnectionManager.ConnectState.NONE) {
            stop(ConnectionManager.BTType.BT_FILETRANSFER);
        }
        if (!this.wasConnected && isConnected) {
            Log.i(this.TAG, "bluetooth onConnect");
            onConnect();
        } else if (this.wasConnected && !isConnected) {
            Log.i(this.TAG, "bluetooth onDisconnect");
            onDisconnect();
        }
        this.wasConnected = isConnected;
    }

    public void onConnect() {
        writeXMLMessage(getDeviceInfoMessage());
    }

    public void writeXMLMessage(String xml) {
        if (isConnected()) {
            ((ChatConnectedThread) this.chatMgr.currentThread).write(xml);
        }
    }

    /* renamed from: com.reconinstruments.bluetooth.BluetoothService$2  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$reconinstruments$bluetooth$BluetoothHelper$AppMSG = new int[BluetoothHelper.AppMSG.values().length];

        static {
            try {
                $SwitchMap$com$reconinstruments$bluetooth$BluetoothHelper$AppMSG[BluetoothHelper.AppMSG.START_CONNECT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$reconinstruments$bluetooth$BluetoothHelper$AppMSG[BluetoothHelper.AppMSG.START_LISTEN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$reconinstruments$bluetooth$BluetoothHelper$AppMSG[BluetoothHelper.AppMSG.STOP.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    /* loaded from: classes.dex */
    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }
}