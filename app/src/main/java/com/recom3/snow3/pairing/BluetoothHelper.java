package com.recom3.snow3.pairing;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * Created by Recom3 on 17/07/2022.
 */

public class BluetoothHelper {
    public static Set<BluetoothDevice> getPairedBluetoothDevices() {
        return BluetoothAdapter.getDefaultAdapter().getBondedDevices();
    }
}
