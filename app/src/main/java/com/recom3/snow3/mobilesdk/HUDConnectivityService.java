package com.recom3.snow3.mobilesdk;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.btconnectivity.BTConnectivityManager;
import com.recom3.snow3.mobilesdk.btconnectivity.QueueMessage;
import com.recom3.snow3.mobilesdk.btmfi.BTMfiSessionManager;
import com.recom3.snow3.mobilesdk.hudconnectivity.Constants;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDConnectivityService$Channel;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDStateUpdateListener;

import static com.google.common.collect.ComparisonChain.start;

/**
 * Created by Recom3 on 25/01/2022.
 * Main class to communicate to HUD
 * This class task are:
 * 1. Push bluetooth messages to HUD
 * Check this:
 * https://developer.android.com/guide/topics/connectivity/bluetooth
 * https://developer.android.com/guide/topics/connectivity/bluetooth/transfer-data
 */

public class HUDConnectivityService extends Service {
    private static final String INTENT_COMMAND = "com.reconinstruments.mobilesdk.hudconnectivity.channel.command";

    private static final String INTENT_CONNECT = "com.reconinstruments.mobilesdk.hudconnectivity.connect";

    private static final String INTENT_DISCONNECT = "com.reconinstruments.mobilesdk.hudconnectivity.disconnect";

    private static final String INTENT_FILE = "com.reconinstruments.mobilesdk.hudconnectivity.channel.file";

    private static final String INTENT_OBJECT = "com.reconinstruments.mobilesdk.hudconnectivity.channel.object";

    private static final String INTENT_REQUEST_DISCONNECT = "com.reconinstruments.mobilesdk.hudconnectivity.request.disconnect";

    private static final int PUSHINGBUFFSIZE = 25600;

    private static final String TAG = "HUDConnectivityService";

    //This member is the main actor in holding Bluetooth communications
    //It seems that the mode is client mode (App is client of HUD, check android documentation)
    //https://developer.android.com/guide/topics/connectivity/bluetooth
    //https://developer.android.com/guide/topics/connectivity/bluetooth/connect-bluetooth-devices
    //https://developer.android.com/guide/topics/connectivity/bluetooth/transfer-data
    private BTConnectivityManager mBTConnectivityManager;//In recon engage app is a

    private final IBinder mBinder = (IBinder)new LocalBinder();

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            if (param1Intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                Intent intent;
                switch (param1Intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -2147483648)) {//-0x80000000
                    default:
                        return;
                    case BluetoothAdapter.STATE_TURNING_OFF://13:
                        Log.i("HUDConnectivityService", "Bluetooth is turning off...");
                        HUDConnectivityService.this.stop();
                    case BluetoothAdapter.STATE_OFF://10:
                        Log.i("HUDConnectivityService", "Bluetooth is off");
                        intent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//0x10000000
                        //!!!
                        //HUDConnectivityService.this.mContext.startActivity(intent);
                        Log.i("HUDConnectivityService", "Ask the user to enable the bluetooth first.");
                    case BluetoothAdapter.STATE_ON://12:
                        break;
                }
                Log.i("HUDConnectivityService", "Bluetooth is on");
                //!!!
                //HUDConnectivityService.this.start();
            }
        }
    };

    private Context mContext;

    private final BroadcastReceiver mHUDConnectivityClientBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            HUDConnectivityMessage hUDConnectivityMessage = null;
            String str2 = null;
            String str1 = param1Intent.getAction();
            if (str1.equals("com.reconinstruments.mobilesdk.hudconnectivity.connect")) {
                str1 = param1Intent.getStringExtra("address");
                str2 = param1Intent.getStringExtra("deviceType");
                Log.i("HUDConnectivityService", "Received the connect message, address " + str1);
                if (str2 == null) {
                    HUDConnectivityService.this.connect(HUDConnectivityService.DeviceType.ANDROID, str1);
                    return;
                }
                if ("IOS".equals(str2)) {
                    HUDConnectivityService.this.connect(HUDConnectivityService.DeviceType.IOS, str1);
                    return;
                }
                HUDConnectivityService.this.connect(HUDConnectivityService.DeviceType.ANDROID, str1);
                return;
            }
            if (str1.equals("com.reconinstruments.mobilesdk.hudconnectivity.disconnect")) {
                Log.i("HUDConnectivityService", "Received the disconnect message");
                //str1 = str2.getStringExtra("deviceType");
                str1 = param1Intent.getStringExtra("deviceType");
                HUDConnectivityService.this.disconnect(HUDConnectivityService.DeviceType.ANDROID);
                if (str1 == null) {
                    HUDConnectivityService.this.disconnect(HUDConnectivityService.DeviceType.ANDROID);
                    return;
                }
                if ("IOS".equals(str1)) {
                    HUDConnectivityService.this.disconnect(HUDConnectivityService.DeviceType.IOS);
                    return;
                }
                HUDConnectivityService.this.disconnect(HUDConnectivityService.DeviceType.ANDROID);
                return;
            }
            if (str1.equals("com.reconinstruments.mobilesdk.hudconnectivity.channel.command")) {
                //byte[] arrayOfByte = str2.getByteArrayExtra(HUDConnectivityMessage.TAG);
                byte[] arrayOfByte = param1Intent.getByteArrayExtra(HUDConnectivityMessage.TAG);
                if (arrayOfByte != null && arrayOfByte.length > 0 && arrayOfByte.length <= 25600) {
                    hUDConnectivityMessage = new HUDConnectivityMessage(arrayOfByte);
                    Log.i("HUDConnectivityService", "Received the message " + hUDConnectivityMessage.toString());
                    HUDConnectivityService.this.push(hUDConnectivityMessage, HUDConnectivityService.Channel.COMMAND_CHANNEL);
                    return;
                }
                Log.w("HUDConnectivityService", "The data is null or its length larger than 25k, rejected.");
                return;
            }
            if (str1.equals("com.reconinstruments.mobilesdk.hudconnectivity.channel.object")) {
                //byte[] arrayOfByte = str2.getByteArrayExtra(HUDConnectivityMessage.TAG);
                byte[] arrayOfByte = param1Intent.getByteArrayExtra(HUDConnectivityMessage.TAG);
                if (arrayOfByte != null && arrayOfByte.length > 0 && arrayOfByte.length <= 25600) {
                    hUDConnectivityMessage = new HUDConnectivityMessage(arrayOfByte);
                    Log.i("HUDConnectivityService", "Received the message " + hUDConnectivityMessage.toString());
                    HUDConnectivityService.this.push(hUDConnectivityMessage, HUDConnectivityService.Channel.OBJECT_CHANNEL);
                    return;
                }
                Log.w("HUDConnectivityService", "The data is null or its length larger than 25k, rejected.");
                return;
            }
            if (str1.equals("com.reconinstruments.mobilesdk.hudconnectivity.channel.file")) {
                //byte[] arrayOfByte = str2.getByteArrayExtra(HUDConnectivityMessage.TAG);
                byte[] arrayOfByte = param1Intent.getByteArrayExtra(HUDConnectivityMessage.TAG);
                if (arrayOfByte != null && arrayOfByte.length > 0) {
                    HUDConnectivityMessage hUDConnectivityMessage1 = new HUDConnectivityMessage(arrayOfByte);
                    Log.i("HUDConnectivityService", "Received the message " + hUDConnectivityMessage1.toString());
                    HUDConnectivityService.this.push(hUDConnectivityMessage1, HUDConnectivityService.Channel.FILE_CHANNEL);
                }
            }
        }
    };

    private void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.reconinstruments.mobilesdk.hudconnectivity.connect");
        intentFilter.addAction("com.reconinstruments.mobilesdk.hudconnectivity.disconnect");
        intentFilter.addAction("com.reconinstruments.mobilesdk.hudconnectivity.channel.command");
        intentFilter.addAction("com.reconinstruments.mobilesdk.hudconnectivity.channel.object");
        intentFilter.addAction("com.reconinstruments.mobilesdk.hudconnectivity.channel.file");
        //!!!
        registerReceiver(this.mHUDConnectivityClientBroadcastReceiver, intentFilter);

        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            this.mBTConnectivityManager = new BTConnectivityManager(this.mContext);
            //if (Build.PRODUCT.contains("jet"))
            //    BTMfiSessionManager.getInstance(this.mContext).init(this.mBTConnectivityManager);
            return;
        }

        Log.w("HUDConnectivityService", "Bluetooth is off, skipping until it is enabled");
    }

    private void stop() {

        try {
            unregisterReceiver(this.mHUDConnectivityClientBroadcastReceiver);
        } catch (IllegalArgumentException illegalArgumentException) {}
        if (this.mBTConnectivityManager != null)
            this.mBTConnectivityManager.stop();
        //if (Build.PRODUCT.contains("jet"))
        //    BTMfiSessionManager.getInstance(this.mContext).cleanup();
    }

    public void connect(DeviceType paramDeviceType, String paramString) {
        connect(paramDeviceType, paramString, 9);
    }

    public void connect(DeviceType paramDeviceType, String paramString, int paramInt) {
        if (DeviceType.IOS.compareTo(paramDeviceType) == 0) {
            if (this.mBTConnectivityManager != null)
                this.mBTConnectivityManager.disconnectThreads();
            //!!!
            //BTMfiSessionManager.getInstance(this.mContext).setLastMfiAddress(paramString);
            //BTMfiSessionManager.getInstance(this.mContext).connectRemoteDevice(1, false);
            return;
        }
        if (this.mBTConnectivityManager != null) {
            this.mBTConnectivityManager.connect(paramString, paramInt);
            if (Build.PRODUCT.contains("jet")) {
                //!!!
                //BTMfiSessionManager.getInstance(this.mContext).cleanup();
                //BTMfiSessionManager.getInstance(this.mContext).init(this.mBTConnectivityManager);
            }
            return;
        }
        Log.w("HUDConnectivityService", "BTConnectivityManager doesn't started yet");
    }

    public void disconnect(DeviceType paramDeviceType) {
        if (DeviceType.IOS.compareTo(paramDeviceType) == 0) {
            //BTMfiSessionManager.getInstance(this.mContext).disconnectRemoteDevice(true, 5000);
            return;
        }
        if (this.mBTConnectivityManager != null) {
            this.mBTConnectivityManager.disConnect();
            return;
        }
        Log.w("HUDConnectivityService", "BTConnectivityManager doesn't started yet");
    }

    public BTConnectivityManager getBTConnectivityManager() {
        return this.mBTConnectivityManager;
    }

    public HUDStateUpdateListener.HUD_STATE getConnectionState() {
        int i = 0;
        if (this.mBTConnectivityManager != null)
            i = this.mBTConnectivityManager.getConnectionState();
        return HUDStateUpdateListener.HUD_STATE.values()[i];
    }

    public IBinder onBind(Intent paramIntent) {

        return this.mBinder;
    }

    public void onCreate() {
        this.mContext = (Context)this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        registerReceiver(this.mBluetoothReceiver, intentFilter);
        start();
    }

    public void onDestroy() {
        unregisterReceiver(this.mBluetoothReceiver);
        stop();
    }

    public void onRebind(Intent paramIntent) {}

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {

        return Service.START_STICKY;
    }

    public boolean onUnbind(Intent paramIntent) {
        return super.onUnbind(paramIntent);
    }

    public void push(HUDConnectivityMessage paramHUDConnectivityMessage, Channel paramChannel) {
        if (this.mBTConnectivityManager != null) {
            this.mBTConnectivityManager.push(paramHUDConnectivityMessage, paramChannel);
            return;
        }
        Log.w("HUDConnectivityService", "BTConnectivityManager doesn't started yet");
    }

    //From recom_engage
    //This has been inserted here, but maybe is not useful
    //It is call from EngageHudConnectivityService extends HUDConnectivityService
    //From this funtion
    //  private final BroadcastReceiver s = new BroadcastReceiver(this) {
    //Search in sources for RECON_SMARTPHONE_CONNECTION_MESSAGE (!)
    //This is to send phone status to HUD
    //Another class interesting to analyze is in Oakley App
    //Seems to ask HUD about battery, gps
    // public class HudPhoneStatusExchanger {
    public final void a(HUDConnectivityMessage paramHUDConnectivityMessage, HUDConnectivityService$Channel paramHUDConnectivityService$Channel) {
        //if (this.a != null) {
            //BTConnectivityManager bTConnectivityManager = this.a;
            //QueueMessage queueMessage = new QueueMessage(paramHUDConnectivityMessage, paramHUDConnectivityService$Channel);
            //queueMessage.a = (IBTConnectorCallBack)new Object(bTConnectivityManager, queueMessage);
            /*
            switch (BTConnectivityManager.null.a[paramHUDConnectivityService$Channel.ordinal()]) {
                default:
                    return;
                case 1:
                    Log.b("BTConnectivityManager", "Putting the message " + paramHUDConnectivityMessage.toString() + " into the command queue");
                    if (Constants.a)
                        Toast.makeText(bTConnectivityManager.a.getApplicationContext(), "Putting the message " + paramHUDConnectivityMessage.toString() + " into the command queue", 1).show();
                    if (bTConnectivityManager.b != null)
                        bTConnectivityManager.b.b(queueMessage);
                case 2:
                    Log.b("BTConnectivityManager", "Putting the message " + paramHUDConnectivityMessage.toString() + " into the object queue");
                    if (Constants.a)
                        Toast.makeText(bTConnectivityManager.a.getApplicationContext(), "Putting the message " + paramHUDConnectivityMessage.toString() + " into the object queue", 1).show();
                    if (bTConnectivityManager.c != null)
                        bTConnectivityManager.c.b(queueMessage);
                case 3:
                    break;
            }
            */
            //Log.b("BTConnectivityManager", "Putting the message " + paramHUDConnectivityMessage.toString() + " into the file queue");
            //if (Constants.a)
            //    Toast.makeText(bTConnectivityManager.a.getApplicationContext(), "Putting the message " + paramHUDConnectivityMessage.toString() + " into the file queue", 1).show();
            //if (bTConnectivityManager.d != null)
            //    bTConnectivityManager.d.b(queueMessage);
        //}
        Log.i("HUDConnectivityService", "BTConnectivityManager doesn't started yet");
    }
/*
    public void requestToDisconnect() {
        HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
        hUDConnectivityMessage.setIntentFilter("com.reconinstruments.mobilesdk.hudconnectivity.request.disconnect");
        hUDConnectivityMessage.setRequestKey(0);
        hUDConnectivityMessage.setSender("HUDConnectivityService");
        hUDConnectivityMessage.setData("".getBytes());
        push(hUDConnectivityMessage, Channel.COMMAND_CHANNEL);
    }
*/

    public enum Channel {
        COMMAND_CHANNEL, FILE_CHANNEL, OBJECT_CHANNEL;

        static {
            //$VALUES = new Channel[] { COMMAND_CHANNEL, OBJECT_CHANNEL, FILE_CHANNEL };
        }
    }

    public enum DeviceType {
        ANDROID, IOS;

        static {

        }
    }

    public class LocalBinder extends Binder {
        public HUDConnectivityService getService() {
            return HUDConnectivityService.this;
        }
    }

}
