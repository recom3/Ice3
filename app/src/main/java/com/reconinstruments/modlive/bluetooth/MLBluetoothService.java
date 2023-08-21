package com.reconinstruments.modlive.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;

import com.recom3.snow3.mobilesdk.hudconnectivity.ConnectHelper;
import com.reconinstruments.bluetooth.BluetoothService;
import com.reconinstruments.bluetooth.ConnectionManager;
import com.reconinstruments.connect.apps.ConnectedDevice;
import com.reconinstruments.connect.apps.DeviceInfo;
import com.reconinstruments.connect.messages.DeviceInfoMessage;
import com.reconinstruments.connect.util.DeviceUtils;
import com.reconinstruments.modlive.NotificationHelper;

/**
 * Created by recom3 on 21/08/2023.
 */

public class MLBluetoothService extends BluetoothService {
    protected String TAG = "MLBluetoothService";
    boolean timerStarted = false;
    Handler timeoutHandler = new Handler(Looper.getMainLooper()) { // from class: com.reconinstruments.modlive.bluetooth.MLBluetoothService.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            MLBluetoothService.this.listen(ConnectionManager.BTType.BT_CHAT);
            MLBluetoothService.this.listen(ConnectionManager.BTType.BT_FILETRANSFER);
            MLBluetoothService.this.timerStarted = false;
        }
    };

    @Override // com.reconinstruments.bluetooth.BluetoothService
    public void onInit() {
        listen(ConnectionManager.BTType.BT_CHAT);
        listen(ConnectionManager.BTType.BT_FILETRANSFER);
    }

    @Override // com.reconinstruments.bluetooth.BluetoothService
    public void onConnect() {
        super.onConnect();
        NotificationHelper.notifyConnected(this);
        Settings.System.putString(getContentResolver(), "DisableSmartphone", "false");
        Intent connectMessage = new Intent();
        connectMessage.setAction(ConnectHelper.MSG_STATE_UPDATED);
        connectMessage.putExtra(ConnectedDevice.COL_CONN_STATE, true);
        connectMessage.putExtra("device", DeviceInfo.DeviceType.ANDROID.name());
        sendBroadcast(connectMessage);
    }

    @Override // com.reconinstruments.bluetooth.BluetoothService
    public void onDisconnect() {
        clearNotifications();
        Intent connectMessage = new Intent();
        connectMessage.setAction(ConnectHelper.MSG_STATE_UPDATED);
        connectMessage.putExtra(ConnectedDevice.COL_CONN_STATE, false);
        connectMessage.putExtra("device", DeviceInfo.DeviceType.ANDROID.name());
        sendBroadcast(connectMessage);
    }

    @Override // com.reconinstruments.bluetooth.BluetoothService
    public void onBTEnabled() {
        String name = BluetoothAdapter.getDefaultAdapter().getName();
        if (name == null || name.equals("limo")) {
            BluetoothAdapter.getDefaultAdapter().setName("MOD Live " + Build.SERIAL);
        }
    }

    @Override // com.reconinstruments.bluetooth.BluetoothService
    public void clearNotifications() {
        NotificationHelper.clearNotifications(this);
    }

    @Override // com.reconinstruments.bluetooth.BluetoothService
    public void onStateChanged() {
        super.onStateChanged();
        if (this.chatMgr.getState() == ConnectionManager.ConnectState.ERROR || this.fileMgr.getState() == ConnectionManager.ConnectState.ERROR) {
            BluetoothAdapter.getDefaultAdapter().disable();
            BluetoothService.btReset = true;
        } else if (this.chatMgr.getState() == ConnectionManager.ConnectState.NONE || this.fileMgr.getState() == ConnectionManager.ConnectState.NONE) {
            if (this.timerStarted) {
                this.timeoutHandler.removeMessages(0);
            }
            this.timeoutHandler.sendEmptyMessageDelayed(0, 15000L);
            this.timerStarted = true;
        } else {
            this.timeoutHandler.removeMessages(0);
            this.timerStarted = false;
        }
    }

    @Override // com.reconinstruments.bluetooth.BluetoothService
    public void saveDevice(BluetoothDevice device) {
        this.device = device;
        DeviceInfo deviceInfo = new DeviceInfo(DeviceInfo.DeviceType.ANDROID, device.getAddress(), null, null);
        ConnectedDevice.updateDeviceInfo(this, deviceInfo);
    }

    @Override // com.reconinstruments.bluetooth.BluetoothService
    public String getDeviceInfoMessage() {
        String packageName = getPackageName();
        packageName = (packageName.equalsIgnoreCase("com.reconinstruments.engage") || packageName.equalsIgnoreCase("com.oakley.airwave")) ? "com.reconinstruments.hqmobile" : "com.reconinstruments.hqmobile";
        String versionCode = DeviceUtils.getVersionCode(this);
        String serialNumber = DeviceUtils.getSerialNumber(this);
        return DeviceInfoMessage.compose(packageName, versionCode, serialNumber);
    }
}
