package com.reconinstruments.os.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.reconinstruments.os.connectivity.bluetooth.HUDSPPService;
import com.reconinstruments.os.connectivity.bluetooth.IHUDBTConsumer;
import com.reconinstruments.os.connectivity.bluetooth.IHUDBTService;
import com.reconinstruments.os.connectivity.http.HUDHttpBTConnection;
import com.reconinstruments.os.connectivity.http.HUDHttpRequest;

import java.util.Set;

/**
 * Created by Recom3 on 15/05/2023.
 */

public class HUDConnectivityPhoneConnection implements IHUDConnectivityConnection {

    private HUDHttpBTConnection hudHttpBTConnection = null;

    private HUDSPPService hudSPPService = null;

    //JC: 16.05.2023
    //Should be here:
    //public abstract class HUDBTBaseService
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String TAG = "HUDConnPhoneConnection";

    public HUDConnectivityPhoneConnection(Context paramContext, IHUDConnectivity paramIHUDConnectivity) {
        try {
            this.hudSPPService = new HUDSPPService(paramIHUDConnectivity);
        }
        catch (Exception ex)
        {
            Log.i("HUDConnectivityPhone", ex.getMessage());
        }
        this.hudHttpBTConnection = new HUDHttpBTConnection(paramContext, paramIHUDConnectivity);
        HUDHttpBTConnection hUDHttpBTConnection = this.hudHttpBTConnection;
        HUDSPPService hUDSPPService = this.hudSPPService;
        HUDHttpBTConnection.ihudbtService = (IHUDBTService)hUDSPPService;
        hUDSPPService.a((IHUDBTConsumer)hUDHttpBTConnection);
    }

    @Override
    public boolean hasNetworkAccess() {
        return HUDHttpBTConnection.a();
    }

    @Override
    public void startListening() {
        HUDHttpBTConnection hUDHttpBTConnection = this.hudHttpBTConnection;
        if (hUDHttpBTConnection.context != null) {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            hUDHttpBTConnection.context.registerReceiver(hUDHttpBTConnection.c, intentFilter);
        }
        this.hudSPPService.startListening();
        hUDHttpBTConnection = this.hudHttpBTConnection;
        hUDHttpBTConnection.a(hUDHttpBTConnection.context);
    }

    @Override
    public void stopListening() {
        HUDHttpBTConnection hUDHttpBTConnection = this.hudHttpBTConnection;
        if (hUDHttpBTConnection.context != null)
            hUDHttpBTConnection.context.unregisterReceiver(hUDHttpBTConnection.c);
        this.hudSPPService.stopListening();
    }

    @Override
    public void sendWebRequest(HUDHttpRequest hudHttpRequest) {
        //We need a wating loop for the response
        hudHttpBTConnection.callHttp(hudHttpRequest);
    }

    /**
     * JC: 16.05.2023
     * Connect all the threads
     * @param paramString       uuid? (viene del PairingTabFragment)
     * @param paramInt          number of connection attemps?
     */
    public void connect(String paramString, int paramInt) {

        Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
        BluetoothDevice btDevice = null;

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName.trim().toLowerCase().indexOf("jet")>=0) {
                    btDevice = device;
                    break;
                }
            }
        }

        if(btDevice != null) {
            BluetoothSocket btSocket;
            BluetoothSocket tmp = null;

            /*
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                btSocket = btDevice.createRfcommSocketToServiceRecord(UUID_COMMAND);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            */

            try {

                //mObjectConnectThread = new BTConnectThread(mAdapter, btDevice,
                //        this, HUDConnectivityService.Channel.OBJECT_CHANNEL, UUID_OBJECT.toString(), 1);

                hudSPPService.connect(mAdapter, btDevice);

            } catch (Exception e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
        }


    }
}
