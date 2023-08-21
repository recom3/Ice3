package com.reconinstruments.connect.util;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by recom3 on 20/08/2023.
 */

public class BluetoothUtils {
    public static String getBondStateName(int bondState) {
        switch (bondState) {
            case 10:
                return "BOND_NONE";
            case 11:
                return "BOND_BONDING";
            case 12:
                return "BOND_BONDED";
            default:
                return "ERROR";
        }
    }

    public static String getBluetoothStateName(int state) {
        switch (state) {
            case 10:
                return "OFF";
            case 11:
                return "TURNING_ON";
            case 12:
                return "ON";
            case 13:
                return "TURNING_OFF";
            default:
                return "ERROR";
        }
    }

    public static boolean isDevicePaired(String addr) {
        return (addr == null || addr.length() == 0 || BluetoothAdapter.getDefaultAdapter().getRemoteDevice(addr).getBondState() != 12) ? false : true;
    }

    public static boolean isBTEnabled() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }
}