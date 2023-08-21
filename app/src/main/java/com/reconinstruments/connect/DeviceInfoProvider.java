package com.reconinstruments.connect;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;

import com.reconinstruments.connect.apps.DeviceInfo;

import org.apache.commons.codec.binary.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chus on 20/08/2023.
 */

public abstract class DeviceInfoProvider extends ContentProvider {
    private static final String AUTHORITY = "com.reconinstruments.connect";
    public static final Uri CONTENT_URI = Uri.parse("content://com.reconinstruments.connect");
    private static final String PREFS_FILE = "DeviceInfo";
    private static final String TAG = "DeviceInfoProvider";
    protected DeviceInfo deviceInfo;
    protected SharedPreferences sharedPrefs;

    public abstract boolean isConnected();

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.sharedPrefs = getContext().getSharedPreferences(PREFS_FILE, 0);
        loadDeviceInfo();
        return true;
    }

    public DeviceInfo.DeviceType getDeviceType() {
        return (this.deviceInfo == null || this.deviceInfo.type == null) ? DeviceInfo.DeviceType.NONE : this.deviceInfo.type;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        HashMap<String, String> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : values.valueSet()) {
            map.put(entry.getKey(), (String) entry.getValue());
        }
        this.deviceInfo.update(map);
        this.deviceInfo.write(this.sharedPrefs);
        return map.size();
    }

    private void loadDeviceInfo() {
        this.deviceInfo = new DeviceInfo(this.sharedPrefs);
        //!recom3
        //if (!StringUtils.isEmpty(this.deviceInfo.macAddress)) {
        if(!this.deviceInfo.macAddress.isEmpty()) {
            if (!BluetoothAdapter.checkBluetoothAddress(this.deviceInfo.macAddress)) {
                this.deviceInfo.macAddress = "";
                this.deviceInfo.write(this.sharedPrefs);
            } else if (BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.deviceInfo.macAddress).getBondState() == 10) {
                this.deviceInfo.macAddress = null;
                this.deviceInfo.write(this.sharedPrefs);
            }
        }
    }
}