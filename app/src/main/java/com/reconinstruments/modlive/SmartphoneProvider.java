package com.reconinstruments.modlive;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.reconinstruments.bletest.IBLEService;
import com.reconinstruments.bluetooth.BluetoothProvider;

/**
 * Created by recom3 on 21/08/2023.
 */

public class SmartphoneProvider extends BluetoothProvider {
    private static final String TAG = "SmartphoneProvider";
    private ServiceConnection BLEServiceConnection = new ServiceConnection() { // from class: com.reconinstruments.modlive.SmartphoneProvider.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder boundService) {
            SmartphoneProvider.this.bleService = IBLEService.Stub.asInterface(boundService);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            Log.i(SmartphoneProvider.TAG, "ble service disconnected");
            SmartphoneProvider.this.bleService = null;
        }
    };
    private IBLEService bleService;

    @Override // com.reconinstruments.bluetooth.BluetoothProvider, com.reconinstruments.connect.DeviceInfoProvider, android.content.ContentProvider
    public boolean onCreate() {
        super.onCreate();
        bindBLE();
        return true;
    }

    @Override // com.reconinstruments.bluetooth.BluetoothProvider, com.reconinstruments.connect.DeviceInfoProvider
    public boolean isConnected() {
        boolean btConnected = this.btService == null ? false : this.btService.isConnected();
        boolean bleConnected = false;
        if (this.bleService != null) {
            try {
                if (!this.bleService.getIsMaster()) {
                    if (this.bleService.isConnected()) {
                        bleConnected = true;
                    }
                }
                bleConnected = false;
            } catch (RemoteException e) {
                Log.i(TAG, "RemoteException getting ble isConnected", e);
            }
        }
        return btConnected | bleConnected;
    }

    public void bindBLE() {
        getContext().bindService(new Intent("RECON_BLE_TEST_SERVICE"), this.BLEServiceConnection, 1);
    }
}