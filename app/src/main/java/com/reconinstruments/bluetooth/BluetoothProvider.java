package com.reconinstruments.bluetooth;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.reconinstruments.connect.DeviceInfoProvider;
import com.reconinstruments.connect.apps.ConnectedDevice;
import com.reconinstruments.connect.apps.DeviceInfo;

import java.util.HashMap;

/**
 * Created by recom3 on 21/08/2023.
 */

public class BluetoothProvider extends DeviceInfoProvider {
    private static final String TAG = "BluetoothProvider";
    protected BluetoothService btService;
    protected boolean btBound = false;
    private ServiceConnection BTServiceConnection = new ServiceConnection() { // from class: com.reconinstruments.bluetooth.BluetoothProvider.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothProvider.this.btService = ((BluetoothService.LocalBinder) service).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            Log.i(BluetoothProvider.TAG, "bluetooth service disconnected");
            BluetoothProvider.this.btService = null;
        }
    };

    @Override // com.reconinstruments.connect.DeviceInfoProvider, android.content.ContentProvider
    public boolean onCreate() {
        super.onCreate();
        return true;
    }

    public synchronized void bindBT() {
        if (this.btService == null) {
            getContext().bindService(new Intent("RECON_BLUETOOTH_SERVICE"), this.BTServiceConnection, 1);
        }
    }

    public synchronized void unbindBT() {
        if (this.btService != null) {
            getContext().unbindService(this.BTServiceConnection);
            getContext().stopService(new Intent(getContext(), BluetoothService.class));
            this.btService = null;
        }
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (selection.equals(ConnectedDevice.CONN_STATE)) {
            MatrixCursor cursor = new MatrixCursor(new String[]{ConnectedDevice.COL_CONN_STATE, DeviceInfo.DEV_TYPE});
            Object[] values = new Object[2];
            values[0] = isConnected() ? "true" : "false";
            values[1] = getDeviceType().name();
            cursor.addRow(values);
            return cursor;
        } else if (selection.equals(ConnectedDevice.DEV_INFO)) {
            if (this.deviceInfo == null) {
                return null;
            }
            HashMap<String, String> map = this.deviceInfo.getMap();
            String[] keys = new String[map.keySet().size()];
            int i = 0;
            for (String s : map.keySet()) {
                keys[i] = s;
                i++;
            }
            MatrixCursor cursor2 = new MatrixCursor(keys);
            cursor2.addRow(map.values().toArray());
            return cursor2;
        } else if (selection.equals(ConnectedDevice.BT_STATE)) {
            MatrixCursor cursor3 = new MatrixCursor(new String[]{ConnectedDevice.COL_CHAT_STATE, ConnectedDevice.COL_FT_STATE});
            cursor3.addRow(btState());
            return cursor3;
        } else {
            return null;
        }
    }

    @Override // com.reconinstruments.connect.DeviceInfoProvider
    public boolean isConnected() {
        if (this.btService == null) {
            return false;
        }
        return this.btService.isConnected();
    }

    public String[] btState() {
        return this.btService == null ? new String[]{ConnectionManager.ConnectState.NONE.name(), ConnectionManager.ConnectState.NONE.name()} : new String[]{this.btService.chatMgr.getState().name(), this.btService.fileMgr.getState().name()};
    }

    /*
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }
    */

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        bindBT();
        return null;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        unbindBT();
        return 0;
    }
}