package com.reconinstruments.connect.apps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.reconinstruments.bluetooth.ConnectionManager;
import com.reconinstruments.connect.DeviceInfoProvider;
import com.reconinstruments.connect.util.CursorUtils;

import java.util.HashMap;

/**
 * Created by recom3 on 20/08/2023.
 */

public class ConnectedDevice {
    public static final String BT_STATE = "bluetooth_state";
    public static final String COL_CHAT_STATE = "chat_state";
    public static final String COL_CONN_STATE = "connected";
    public static final String COL_FT_STATE = "ft_state";
    public static final String CONN_STATE = "connection_state";
    public static final String DEV_INFO = "device_info";
    public static final String TAG = "ConnectedDevice";

    /* loaded from: classes.dex */
    public static class ConnectionState {
        public boolean connected;
        public DeviceInfo.DeviceType lastDeviceType;

        public ConnectionState(boolean connected, DeviceInfo.DeviceType lastDeviceType) {
            this.connected = connected;
            this.lastDeviceType = lastDeviceType;
        }
    }

    public static ConnectionState getConnectionState(Context context) {
        ConnectionState connectionState;
        Cursor cursor = context.getContentResolver().query(DeviceInfoProvider.CONTENT_URI, null, CONN_STATE, null, null);
        if (CursorUtils.checkCursor(cursor)) {
            boolean connected = CursorUtils.getString(cursor, COL_CONN_STATE).equals("true");
            DeviceInfo.DeviceType type = DeviceInfo.DeviceType.valueOf(CursorUtils.getString(cursor, DeviceInfo.DEV_TYPE));
            connectionState = new ConnectionState(connected, type);
        } else {
            connectionState = new ConnectionState(false, DeviceInfo.DeviceType.NONE);
        }
        if (cursor != null) {
            cursor.close();
        }
        return connectionState;
    }

    public static DeviceInfo getDeviceInfo(Context context) {
        Cursor cursor = context.getContentResolver().query(DeviceInfoProvider.CONTENT_URI, null, DEV_INFO, null, null);
        DeviceInfo deviceInfo = null;
        if (CursorUtils.checkCursor(cursor)) {
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                map.put(cursor.getColumnName(i), cursor.getString(i));
            }
            deviceInfo = new DeviceInfo(map);
        }
        if (cursor != null) {
            cursor.close();
        }
        return deviceInfo;
    }

    public static ConnectionManager.ConnectState[] getBluetoothState(Context context) {
        Cursor cursor = context.getContentResolver().query(DeviceInfoProvider.CONTENT_URI, null, BT_STATE, null, null);
        ConnectionManager.ConnectState[] state = {ConnectionManager.ConnectState.NONE, ConnectionManager.ConnectState.NONE};
        if (CursorUtils.checkCursor(cursor)) {
            state[0] = ConnectionManager.ConnectState.valueOf(CursorUtils.getString(cursor, COL_CHAT_STATE));
            state[1] = ConnectionManager.ConnectState.valueOf(CursorUtils.getString(cursor, COL_FT_STATE));
        }
        if (cursor != null) {
            cursor.close();
        }
        return state;
    }

    public static void updateDeviceInfo(Context context, DeviceInfo info) {
        ContentValues cv = new ContentValues();
        HashMap<String, String> map = info.getMap();
        for (String key : map.keySet()) {
            cv.put(key, map.get(key));
        }
        context.getContentResolver().update(DeviceInfoProvider.CONTENT_URI, cv, null, null);
    }

    public static boolean isConnected(Context context) {
        return getConnectionState(context).connected;
    }

    public static String getMACAddress(Context context) {
        DeviceInfo info = getDeviceInfo(context);
        if (info != null) {
            return info.macAddress;
        }
        return null;
    }

    public static String getSerialNumber(Context context) {
        DeviceInfo info = getDeviceInfo(context);
        if (info != null) {
            return info.serialNumber;
        }
        return null;
    }

    public static int getVersionCode(Context context) {
        DeviceInfo info = getDeviceInfo(context);
        return (info != null ? info.versionCode : null).intValue();
    }
}