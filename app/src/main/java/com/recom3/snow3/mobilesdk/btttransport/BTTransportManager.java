package com.recom3.snow3.mobilesdk.btttransport;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.common.util.concurrent.Monitor;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.btmfi.BTMfiSessionManager;
import com.recom3.snow3.mobilesdk.hudconnectivity.BTProperty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static com.recom3.snow3.mobilesdk.HUDConnectivityService.Channel.COMMAND_CHANNEL;
import static com.recom3.snow3.mobilesdk.HUDConnectivityService.Channel.FILE_CHANNEL;
import static com.recom3.snow3.mobilesdk.HUDConnectivityService.Channel.OBJECT_CHANNEL;

/**
 * Created by Recom3 on 27/01/2022.
 * Holding the threads for communications.
 */

public class BTTransportManager {

    private static final int BUFFSIZE = 25600;

    private static final String NAME_COMMAND = "BTCOMMAND";

    private static final String NAME_FILE = "BTFILE";

    private static final String NAME_OBJECT = "BTOBJECT";

    public static final int STATE_CONNECTED = 2;

    public static final int STATE_CONNECTING = 1;

    public static final int STATE_DISCONNECTED = 0;

    private static final String STATE_EXTRA_FIELD = "state";

    private static final String STATE_INTENT = "HUD_STATE_CHANGED";

    private static final String TAG = "BTTransportManager";

    static final UUID UUID_COMMAND;
    static final UUID UUID_FILE;
    static final UUID UUID_OBJECT;

    private static int currentState = 0;

    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

    private BTAcceptThread mCommandAcceptThread;

    BTConnectThread mCommandConnectThread;

    private BTConnectedThread mCommandConnectedThread;

    private ConnectionState mCommandState = ConnectionState.NONE;

    private Context mContext;

    private BTAcceptThread mFileAcceptThread;

    BTConnectThread mFileConnectThread;

    private BTConnectedThread mFileConnectedThread;

    private ConnectionState mFileState = ConnectionState.NONE;

    final Handler mHandler;

    private BTAcceptThread mObjectAcceptThread;

    BTConnectThread mObjectConnectThread;

    private BTConnectedThread mObjectConnectedThread;

    private ConnectionState mObjectState = ConnectionState.NONE;

    private String m_lstAddress = "";
    private int m_lstAttempts = 0;

    public enum ConnectionState {
        //!!!
        //NONE, LISTEN, CONNECTING, CONNECTED
        CONNECTED, CONNECTING, LISTEN, NONE;

    }

    static {
        UUID_COMMAND = UUID.fromString("B29E4260-9D8A-11E2-9E96-0800200C9A66");
        UUID_OBJECT = UUID.fromString("B29E4261-9D8A-11E2-9E96-0800200C9A66");
        UUID_FILE = UUID.fromString("B29E4262-9D8A-11E2-9E96-0800200C9A66");
    }

    public BTTransportManager(Context paramContext, Handler paramHandler) {
        this.mHandler = paramHandler;
        this.mContext = paramContext;
    }

    public static void broadcastStateChanged(Context paramContext, int paramInt) {
        currentState = paramInt;
        Log.i("BTTransportManager", "currentState changed to " + currentState);
        BTProperty.setBTConnectionState(paramContext, paramInt);
        Intent intent = new Intent("HUD_STATE_CHANGED");
        intent.putExtra("state", currentState);
        paramContext.sendBroadcast(intent);
    }

    private String bytesToHex(byte[] paramArrayOfbyte) {
        char[] arrayOfChar1 = new char[16];
        arrayOfChar1[0] = '0';
        arrayOfChar1[1] = '1';
        arrayOfChar1[2] = '2';
        arrayOfChar1[3] = '3';
        arrayOfChar1[4] = '4';
        arrayOfChar1[5] = '5';
        arrayOfChar1[6] = '6';
        arrayOfChar1[7] = '7';
        arrayOfChar1[8] = '8';
        arrayOfChar1[9] = '9';
        arrayOfChar1[10] = 'A';
        arrayOfChar1[11] = 'B';
        arrayOfChar1[12] = 'C';
        arrayOfChar1[13] = 'D';
        arrayOfChar1[14] = 'E';
        arrayOfChar1[15] = 'F';
        char[] arrayOfChar2 = new char[paramArrayOfbyte.length * 2];
        for (byte b = 0; b < paramArrayOfbyte.length; b++) {
            int i = paramArrayOfbyte[b] & 0xFF;
            arrayOfChar2[b * 2] = (char)arrayOfChar1[i >>> 4];
            arrayOfChar2[b * 2 + 1] = (char)arrayOfChar1[i & 0xF];
        }
        return new String(arrayOfChar2);
    }

    private void setState(HUDConnectivityService.Channel paramChannel, ConnectionState state) {

        switch (paramChannel)
        {
            case COMMAND_CHANNEL:
                mCommandState = state;
                break;
            case FILE_CHANNEL:
                mFileState = state;
                break;
            case OBJECT_CHANNEL:
                mObjectState  = state;
                break;
        }
        //!recom3
        //this.mHandler.obtainMessage(1, -1, -1, state).sendToTarget();
    }

    private byte[] subArray(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
        byte[] arrayOfByte = new byte[paramInt2];
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
            arrayOfByte[i - paramInt1] = (byte)paramArrayOfbyte[i];
        return arrayOfByte;
    }

    /**
     * Connect all the threads
     * @param address       uuid? (comes from PairingTabFragment)
     * @param attempts       number of connection attemps
     */
    public void connect(String address, int attempts) {

        stop(false);
        start(false);

        BTProperty.setBTConnectionState(this.mContext, STATE_DISCONNECTED);

        if(currentState==STATE_CONNECTED) {
            broadcastStateChanged(mContext, STATE_DISCONNECTED);
        }
        else {
            currentState = STATE_DISCONNECTED;
        }

        int nImpl = 1;

        if(nImpl>=2) {

            if ((ConnectionState.NONE.compareTo(getState(COMMAND_CHANNEL)) == 0 &&
                ConnectionState.NONE.compareTo(getState(OBJECT_CHANNEL)) == 0 &&
                ConnectionState.NONE.compareTo(getState(FILE_CHANNEL)) == 0)
                || (ConnectionState.LISTEN.compareTo(getState(COMMAND_CHANNEL)) == 0 &&
                ConnectionState.LISTEN.compareTo(getState(OBJECT_CHANNEL)) == 0 &&
                ConnectionState.LISTEN.compareTo(getState(FILE_CHANNEL)) == 0)) {

                //Keeping last address and attempt for maybe reconnecting
                m_lstAddress = address;
                m_lstAttempts = attempts;

                Log.i(TAG, "starting mConnectThread");
                broadcastStateChanged(this.mContext, 1);
                this.mCommandConnectThread = new BTConnectThread(this.mAdapter, null, this, COMMAND_CHANNEL, address, attempts);
                this.mCommandConnectThread.start();
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setState(COMMAND_CHANNEL, ConnectionState.CONNECTING);
                this.mObjectConnectThread = new BTConnectThread(this.mAdapter, null,this, OBJECT_CHANNEL, address, attempts);
                this.mObjectConnectThread.start();
                setState(OBJECT_CHANNEL, ConnectionState.CONNECTING);
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                setState(FILE_CHANNEL, ConnectionState.CONNECTING);
                this.mFileConnectThread = new BTConnectThread(this.mAdapter, null, this, FILE_CHANNEL, address, attempts);
                this.mFileConnectThread.start();
                Log.i(TAG, "starting mCommandConnectedThread/mObjectConnectedThread/mFileConnectedThread");
            } else {
                Log.w(TAG, "Skipped. Connecting or Connected already.");
            }

            return;
        }

        ConnectionState connectionState = getState(COMMAND_CHANNEL);

        if(connectionState==ConnectionState.NONE) {
            connectionState = getState(OBJECT_CHANNEL);
            if(connectionState==ConnectionState.NONE) {
                connectionState = getState(FILE_CHANNEL);
            }
        }

        if(connectionState!=ConnectionState.NONE) {
            connectionState = getState(COMMAND_CHANNEL);
            if(connectionState!=ConnectionState.LISTEN)
            {
                Log.w(TAG, "Skipped. Connecting or Connected already.");
                return;
            }
            connectionState = getState(OBJECT_CHANNEL);

            if(connectionState!=ConnectionState.LISTEN)
            {
                Log.w(TAG, "Skipped. Connecting or Connected already.");
                return;
            }
            connectionState = getState(FILE_CHANNEL);
            if(connectionState!=ConnectionState.LISTEN)
            {
                Log.w(TAG, "Skipped. Connecting or Connected already.");
                return;
            }
        }

        //Keeping last address and attempt for maybe reconnecting
        m_lstAddress = address;
        m_lstAttempts = attempts;

        Log.i(TAG, "starting mConnectThread");
        broadcastStateChanged(mContext, STATE_CONNECTING);

        //For reference:
        //https://developer.android.com/guide/topics/connectivity/bluetooth/connect-bluetooth-devices
        //https://developer.android.com/reference/android/bluetooth/BluetoothAdapter

        Set<BluetoothDevice> pairedDevices = mAdapter.getBondedDevices();
        BluetoothDevice btDevice = null;

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName.trim().indexOf("Snow2")>=0
                        || deviceName.trim().toLowerCase().indexOf("jet")>=0) {

                    boolean isAddress = false;
                    if(device.getAddress().equals(address))
                    {
                        isAddress = true;
                    }

                    if(isAddress) {
                        btDevice = device;
                        break;
                    }
                }
            }
        }

        if(btDevice != null) {
            BluetoothSocket btSocket;
            BluetoothSocket tmp = null;

        /*
        https://developer.android.com/reference/android/bluetooth/BluetoothAdapter

        Represents the local device Bluetooth adapter. The BluetoothAdapter lets you perform fundamental Bluetooth tasks,
        such as initiate device discovery, query a list of bonded (paired) devices, instantiate a BluetoothDevice using a known MAC address,
        and create a BluetoothServerSocket to listen for connection requests from other devices, and start a scan for Bluetooth LE devices.

        To get a BluetoothAdapter representing the local Bluetooth adapter, call the BluetoothManager#getAdapter function on BluetoothManager.
        On JELLY_BEAN_MR1 and below you will need to use the static getDefaultAdapter() method instead.

        Fundamentally, this is your starting point for all Bluetooth actions. Once you have the local adapter, you can get a set of BluetoothDevice
        objects representing all paired devices with getBondedDevices(); start device discovery with startDiscovery(); or create a BluetoothServerSocket
        to listen for incoming RFComm connection requests with listenUsingRfcommWithServiceRecord(java.lang.String, java.util.UUID); listen for incoming
        L2CAP Connection-oriented Channels (CoC) connection requests with listenUsingL2capChannel(); or start a scan for Bluetooth LE devices with
        startLeScan(android.bluetooth.BluetoothAdapter.LeScanCallback).

        This class is thread safe.
        */

            try {
                mCommandConnectThread = new BTConnectThread(mAdapter, btDevice,
                        this, HUDConnectivityService.Channel.COMMAND_CHANNEL, UUID_COMMAND.toString(), 1);

                mObjectConnectThread = new BTConnectThread(mAdapter, btDevice,
                        this, OBJECT_CHANNEL, UUID_OBJECT.toString(), 1);

                mFileConnectThread = new BTConnectThread(mAdapter, btDevice,
                        this, HUDConnectivityService.Channel.FILE_CHANNEL, UUID_COMMAND.toString(), 1);

            } catch (Exception e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
        }

        mObjectConnectThread.start();

        mCommandConnectThread.start();

        mFileConnectThread.start();

    }

    void connected(HUDConnectivityService.Channel paramChannel, BluetoothSocket paramBluetoothSocket) {

        /*
        if (BTMfiSessionManager.getInstance(this.mContext).isInUse()) {
            BTMfiSessionManager.getInstance(this.mContext).restart();
        }
        switch (channel) {
            case COMMAND_CHANNEL:
                if (this.mCommandConnectedThread != null) {
                    this.mCommandConnectedThread.cancel();
                    this.mCommandConnectedThread = null;
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.mCommandConnectedThread = new BTConnectedThread(this, socket, COMMAND_CHANNEL);
                this.mCommandConnectedThread.start();
                break;
            case OBJECT_CHANNEL:
                if (this.mObjectConnectedThread != null) {
                    this.mObjectConnectedThread.cancel();
                    this.mObjectConnectedThread = null;
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                this.mObjectConnectedThread = new BTConnectedThread(this, socket, OBJECT_CHANNEL);
                this.mObjectConnectedThread.start();
                break;
            case FILE_CHANNEL:
                if (this.mFileConnectedThread != null) {
                    this.mFileConnectedThread.cancel();
                    this.mFileConnectedThread = null;
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
                this.mFileConnectedThread = new BTConnectedThread(this, socket, FILE_CHANNEL);
                this.mFileConnectedThread.start();
                break;
        }
        */

        switch (paramChannel) {
            case COMMAND_CHANNEL:

                if (mCommandConnectedThread != null)
                {
                    mCommandConnectedThread.cancel();
                    mCommandConnectedThread = null;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mCommandConnectedThread = new BTConnectedThread(this,paramBluetoothSocket, COMMAND_CHANNEL);
                mCommandConnectedThread.start();
                break;
            case OBJECT_CHANNEL:
                if (mObjectConnectedThread != null)
                {
                    mObjectConnectedThread.cancel();
                    mObjectConnectedThread = null;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mObjectConnectedThread = new BTConnectedThread(this,paramBluetoothSocket, OBJECT_CHANNEL);
                mObjectConnectedThread.start();
                break;
            case FILE_CHANNEL:
                if (mFileConnectedThread != null)
                {
                    mFileConnectedThread.cancel();
                    mFileConnectedThread = null;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mFileConnectedThread = new BTConnectedThread(this,paramBluetoothSocket, FILE_CHANNEL);
                mFileConnectedThread.start();
                break;
        }
    }

    void connectionFailed() {
        this.mHandler.obtainMessage(5, "Disonnected").sendToTarget();
        if (ConnectionState.CONNECTED.compareTo(getState(COMMAND_CHANNEL)) != 0 && ConnectionState.CONNECTED.compareTo(getState(OBJECT_CHANNEL)) != 0 && ConnectionState.CONNECTED.compareTo(getState(FILE_CHANNEL)) != 0)
            broadcastStateChanged(this.mContext, 0);
        stop(false);
        start(false);
    }

    void connectionLost() {
        this.mHandler.obtainMessage(5, "Disonnected").sendToTarget();
        //if (ConnectionState.CONNECTED.compareTo(getState(HUDConnectivityService.Channel.COMMAND_CHANNEL)) != 0 && ConnectionState.CONNECTED.compareTo(getState(HUDConnectivityService.Channel.OBJECT_CHANNEL)) != 0 && ConnectionState.CONNECTED.compareTo(getState(HUDConnectivityService.Channel.FILE_CHANNEL)) != 0)
        //    broadcastStateChanged(this.mContext, STATE_DISCONNECTED);
        //JC:15.04.2022 Change to || to test objecz channel
        if (ConnectionState.CONNECTED.compareTo(getState(COMMAND_CHANNEL)) != 0 || ConnectionState.CONNECTED.compareTo(getState(OBJECT_CHANNEL)) != 0 || ConnectionState.CONNECTED.compareTo(getState(FILE_CHANNEL)) != 0)
            broadcastStateChanged(this.mContext, STATE_DISCONNECTED);
        stop(false);
        start(false);
    }

    void connectionSuccessed(HUDConnectivityService.Channel paramChannel, BluetoothDevice paramBluetoothDevice) {
        setState(paramChannel, ConnectionState.CONNECTED);
        //if (ConnectionState.CONNECTED.compareTo(getState(HUDConnectivityService.Channel.COMMAND_CHANNEL)) == 0 && ConnectionState.CONNECTED.compareTo(getState(HUDConnectivityService.Channel.OBJECT_CHANNEL)) == 0 && ConnectionState.CONNECTED.compareTo(getState(HUDConnectivityService.Channel.FILE_CHANNEL)) == 0) {
        //JC:15.04.2022
        //Changed to || to test only object channel
        if (ConnectionState.CONNECTED.compareTo(getState(COMMAND_CHANNEL)) == 0 || ConnectionState.CONNECTED.compareTo(getState(OBJECT_CHANNEL)) == 0 || ConnectionState.CONNECTED.compareTo(getState(FILE_CHANNEL)) == 0) {
            BTProperty.setBTConnectedDeviceType(this.mContext, 0);
            BTProperty.setBTConnectionState(this.mContext, 2);
            BTProperty.setBTConnectedDeviceName(this.mContext, paramBluetoothDevice.getName());
            BTProperty.setBTConnectedDeviceAddress(this.mContext, paramBluetoothDevice.getAddress());
            BTProperty.setLastPairedDeviceAddress(this.mContext, paramBluetoothDevice.getAddress());
            BTProperty.setLastPairedDeviceName(this.mContext, paramBluetoothDevice.getName());
            BTProperty.setLastPairedDeviceType(this.mContext, 0);
            broadcastStateChanged(this.mContext, STATE_CONNECTED);
        }
    }

    public int getConnectionState() {
        return currentState;
    }

    ConnectionState getState(HUDConnectivityService.Channel paramChannel) {

        ConnectionState connectionState = null;
        switch (paramChannel)
        {
            case COMMAND_CHANNEL:
                connectionState = mCommandState;
                break;
            case FILE_CHANNEL:
                connectionState = mFileState;
                break;
            case OBJECT_CHANNEL:
                connectionState = mObjectState;
                break;
        }
        return connectionState;
    }

    public void listeningConnectionLost() {
        this.mHandler.obtainMessage(5, "Disonnected").sendToTarget();
        if (ConnectionState.CONNECTED.compareTo(getState(COMMAND_CHANNEL)) != 0 && ConnectionState.CONNECTED.compareTo(getState(OBJECT_CHANNEL)) != 0 && ConnectionState.CONNECTED.compareTo(getState(FILE_CHANNEL)) != 0)
            broadcastStateChanged(this.mContext, 0);
        stop(true);
        start(true);
    }

    void resetDeviceType() {
        BTProperty.setBTConnectedDeviceType(this.mContext, 0);
        BTProperty.setBTConnectionState(this.mContext, 0);
    }

    public void showBtErrorToastToUser() {
        Toast.makeText(this.mContext, "Open Settings, turn off Bluetooth. Bluetooth service was not able to release required resources.", Toast.LENGTH_LONG).show();
    }

    public void start(boolean paramBoolean) {
        if(mCommandConnectThread!=null) {
            mCommandConnectThread.cancel();
            mCommandConnectThread = null;
        }
        if(mObjectConnectThread!=null) {
            mObjectConnectThread.cancel();
            mObjectConnectThread = null;
        }
        if(mFileConnectThread!=null) {
            mFileConnectThread.cancel();
            mFileConnectThread = null;
        }
        if(mCommandConnectedThread!=null) {
            mCommandConnectedThread.cancel();
            mCommandConnectedThread = null;
        }
        if(mObjectConnectedThread!=null) {
            mObjectConnectedThread.cancel();
            mObjectConnectedThread = null;
        }
        if(mFileConnectedThread!=null) {
            mFileConnectedThread.cancel();
            mFileConnectedThread = null;
        }

        if(paramBoolean) {
            Log.i(TAG, "starting mCommandAcceptThread, mObjectAcceptThread and mFileAcceptThread");
            setState(HUDConnectivityService.Channel.COMMAND_CHANNEL, ConnectionState.LISTEN);
            setState(HUDConnectivityService.Channel.OBJECT_CHANNEL, ConnectionState.LISTEN);
            setState(HUDConnectivityService.Channel.FILE_CHANNEL, ConnectionState.LISTEN);
            if (this.mCommandAcceptThread == null) {
                this.mCommandAcceptThread = new BTAcceptThread(this.mAdapter, this, HUDConnectivityService.Channel.COMMAND_CHANNEL, NAME_COMMAND, UUID_COMMAND);
                this.mCommandAcceptThread.start();
            }
            if (this.mObjectAcceptThread == null) {
                this.mObjectAcceptThread = new BTAcceptThread(this.mAdapter, this, HUDConnectivityService.Channel.OBJECT_CHANNEL, NAME_OBJECT, UUID_OBJECT);
                this.mObjectAcceptThread.start();
            }
            if (this.mFileAcceptThread == null) {
                this.mFileAcceptThread = new BTAcceptThread(this.mAdapter, this, HUDConnectivityService.Channel.FILE_CHANNEL, NAME_FILE, UUID_FILE);
                this.mFileAcceptThread.start();
            }
        }
        //!!!
        //this.mHandler.obtainMessage(6).sendToTarget();
    }

    public void stop(boolean quit) {
        if (this.mCommandConnectThread != null) {
            this.mCommandConnectThread.cancel();
            this.mCommandConnectThread = null;
        }
        if (this.mObjectConnectThread != null) {
            this.mObjectConnectThread.cancel();
            this.mObjectConnectThread = null;
        }
        if (this.mFileConnectThread != null) {
            this.mFileConnectThread.cancel();
            this.mFileConnectThread = null;
        }
        if (this.mCommandConnectedThread != null) {
            this.mCommandConnectedThread.cancel();
            this.mCommandConnectedThread = null;
        }
        if (this.mObjectConnectedThread != null) {
            this.mObjectConnectedThread.cancel();
            this.mObjectConnectedThread = null;
        }
        if (this.mFileConnectedThread != null) {
            this.mFileConnectedThread.cancel();
            this.mFileConnectedThread = null;
        }
        if (quit) {
            Log.i(TAG, "stopping mCommandAcceptThread, mObjectAcceptThread and mFileAcceptThread");
            if (this.mCommandAcceptThread != null) {
                this.mCommandAcceptThread.cancel();
                this.mCommandAcceptThread = null;
            }
            if (this.mObjectAcceptThread != null) {
                this.mObjectAcceptThread.cancel();
                this.mObjectAcceptThread = null;
            }
            if (this.mFileAcceptThread != null) {
                this.mFileAcceptThread.cancel();
                this.mFileAcceptThread = null;
            }
            setState(HUDConnectivityService.Channel.COMMAND_CHANNEL, ConnectionState.NONE);
            setState(HUDConnectivityService.Channel.OBJECT_CHANNEL, ConnectionState.NONE);
            setState(HUDConnectivityService.Channel.FILE_CHANNEL, ConnectionState.NONE);
        } else {
            setState(HUDConnectivityService.Channel.COMMAND_CHANNEL, ConnectionState.LISTEN);
            setState(HUDConnectivityService.Channel.OBJECT_CHANNEL, ConnectionState.LISTEN);
            setState(HUDConnectivityService.Channel.FILE_CHANNEL, ConnectionState.LISTEN);
        }
        BTProperty.setBTConnectionState(this.mContext, 0);
    }

    public boolean write(HUDConnectivityService.Channel paramChannel, byte[] out) {

        int impl = 2;

        if(impl>=2) {

            boolean result;
            result = false;
            if (out != null) {
                if (out.length > 20) {
                    BTConnectedThread r = null;
                    switch (paramChannel) {
                        case COMMAND_CHANNEL:
                            synchronized (this) {
                                r = this.mCommandConnectedThread;
                                break;
                            }
                        case OBJECT_CHANNEL:
                            synchronized (this) {
                                r = this.mObjectConnectedThread;
                                break;
                            }
                        case FILE_CHANNEL:
                            synchronized (this) {
                                r = this.mFileConnectedThread;
                                break;
                            }
                    }

                    //Try to reconnect
                    //This part is not enabled
                    if(false && r==null && m_lstAddress.isEmpty()==false)
                    {
                        Log.i(TAG, "Reconnecting to HUD...");

                        connect(m_lstAddress, m_lstAttempts);

                        switch (paramChannel) {
                            case COMMAND_CHANNEL:
                                synchronized (this) {
                                    r = this.mCommandConnectedThread;
                                    break;
                                }
                            case OBJECT_CHANNEL:
                                synchronized (this) {
                                    r = this.mObjectConnectedThread;
                                    break;
                                }
                            case FILE_CHANNEL:
                                synchronized (this) {
                                    r = this.mFileConnectedThread;
                                    break;
                                }
                        }
                    }

                    if (r != null) {
                        if (out.length > BUFFSIZE) {
                            ByteBuffer.allocate(0);
                            int start = 0;
                            boolean sending = true;
                            int i = 1;
                            while (sending) {
                                if (BUFFSIZE * i < out.length) {
                                    ByteBuffer.wrap(out, start, BUFFSIZE);
                                    r.write(subArray(out, start, BUFFSIZE));
                                    start = BUFFSIZE * i;
                                    i++;
                                } else {
                                    ByteBuffer.wrap(out, start, out.length - start);
                                    r.write(subArray(out, start, out.length - start));
                                    sending = false;
                                }
                                try {
                                    Thread.sleep(100L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            r.write(out);
                        }
                        result = true;
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        Log.w(TAG, "No Bluetooth device connected, skip to send out the data and broadcast disconnected message out.");
                        //!recom3
                        /*
                        if (currentState != 0) {
                            broadcastStateChanged(this.mContext, 0);
                            BTProperty.setBTConnectionState(this.mContext, 0);
                        }
                        */
                    }
                }
            }
            else {
                Log.w(TAG, "Skip to send data since the QueueMessage is null");
            }
            return result;

        }
        else {
            boolean result;
            result = false;

            ByteBuffer bb;

            switch (paramChannel) {
                case COMMAND_CHANNEL:
                    if (mCommandConnectedThread != null)
                        mCommandConnectedThread.write(out);
                    break;
                case OBJECT_CHANNEL:
                    if (mObjectConnectedThread != null)
                        mObjectConnectedThread.write(out);
                    break;
            }

            //!recom3
            return false;
        }
    }

}
