package com.reconinstruments.connect.apps;

import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Chus on 20/08/2023.
 */

public class DeviceInfo {
    public static final String DEV_ADDR = "deviceAddr";
    public static final String DEV_SERIAL = "serialNumber";
    public static final String DEV_TYPE = "deviceType";
    public static final String DEV_VER = "versionCode";
    public String macAddress;
    public String serialNumber;
    public DeviceType type;
    public Integer versionCode;

    /* loaded from: classes.dex */
    public enum DeviceType {
        NONE,
        ANDROID,
        IOS,
        MODLIVE
    }

    public DeviceInfo(DeviceType type, String mac, Integer version, String serial) {
        this.type = type;
        this.macAddress = mac;
        this.versionCode = version;
        this.serialNumber = serial;
    }

    public DeviceInfo(SharedPreferences sp) {
        this.macAddress = sp.getString(DEV_ADDR, null);
        this.versionCode = Integer.valueOf(Integer.parseInt(sp.getString(DEV_VER, "0")));
        this.serialNumber = sp.getString(DEV_SERIAL, null);
    }

    public DeviceInfo(HashMap<String, String> map) {
        this.macAddress = map.get(DEV_ADDR);
        this.versionCode = Integer.valueOf(Integer.parseInt(map.get(DEV_VER)));
        this.serialNumber = map.get(DEV_SERIAL);
    }

    public void write(SharedPreferences sp) {
        SharedPreferences.Editor ed = sp.edit();
        HashMap<String, String> map = getMap();
        for (String key : map.keySet()) {
            ed.putString(key, map.get(key));
        }
        ed.commit();
    }

    public HashMap<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        if (this.type != null) {
            map.put(DEV_TYPE, this.type.name());
        }
        if (this.macAddress != null) {
            map.put(DEV_ADDR, this.macAddress);
        }
        if (this.versionCode != null) {
            map.put(DEV_VER, "" + this.versionCode);
        }
        if (this.serialNumber != null) {
            map.put(DEV_SERIAL, this.serialNumber);
        }
        return map;
    }

    public void update(HashMap<String, String> map) {
        if (map.containsKey(DEV_TYPE)) {
            this.type = DeviceType.valueOf(map.get(DEV_TYPE));
        }
        if (map.containsKey(DEV_ADDR)) {
            this.macAddress = map.get(DEV_ADDR);
        }
        if (map.containsKey(DEV_VER)) {
            this.versionCode = Integer.valueOf(Integer.parseInt(map.get(DEV_VER)));
        }
        if (map.containsKey(DEV_SERIAL)) {
            this.serialNumber = map.get(DEV_SERIAL);
        }
    }
}
