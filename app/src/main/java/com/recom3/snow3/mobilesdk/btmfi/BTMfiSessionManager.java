package com.recom3.snow3.mobilesdk.btmfi;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.recom3.snow3.mobilesdk.btconnectivity.BTConnectivityManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

/**
 * Created by Recom3 on 25/01/2022.
 * Reference material here>
 * https://www.tutorialspoint.com/android/android_bluetooth.htm
 * https://developer.android.com/guide/topics/connectivity/bluetooth
 * https://developer.android.com/guide/topics/connectivity/bluetooth/connect-bluetooth-devices
 *
 */

public class BTMfiSessionManager {
    private static final int BUFFSIZE = 2060;

    private static final String TAG = "BTMfiSessionManager";

    private static final int WRITTINGBUFFSIZE = 25600;

    static int connectingRoute;

    private static BTMfiSessionManager instance;

    static boolean testingPurpose = false;
/*
    private BTMfiClientEventCallback clientEventCallback;
*/
    private int dataReceived = 0;

    private boolean inUse = false;

    private String lastMfiAddress = "9C:20:7B:1C:DA:69";

    private BTConnectivityManager mBTConnectivityManager;

    private Context mContext;
/*
    private int packetNo = 0;

    private ByteBuffer receivingBuff = ByteBuffer.allocate(0);

    private byte[] sendingBuff = new byte[0];

    private int sendingPacketId = 0;

    private int sendingPos = 0;

    private SPPM.SerialPortClientManager serialPortClientManager;

    private SPPM.SerialPortServerManager serialPortServerManager;

    private BTMfiServerEventCallback serverEventCallback;

    boolean testFakeSessionResult = false;

    private int totalReceived = 0;

    static {
        connectingRoute = 0;
    }
*/
    private BTMfiSessionManager(Context paramContext) {
        this.mContext = paramContext;
    }
/*
    private int byteArrayToInt(byte[] paramArrayOfbyte) {
        int i = ByteBuffer.wrap(subArray(paramArrayOfbyte, 0, 4)).getInt();
        Log.i("BTMfiSessionManager", "Start receiving new HUDConnectivityMessage data block, total size = " + i);
        if (i >= 50331648 || i < 0) {
            Log.w("BTMfiSessionManager", "skip this data packet, totalReceived is too large to deal with, totalReceived = " + i);
            Log.e("BTMfiSessionManager", "The bad packet hex content: " + bytesToHex(paramArrayOfbyte));
            Log.e("BTMfiSessionManager", "The bad packet content: " + new String(paramArrayOfbyte));
            reset();
            reattemptConnection();
            i = 0;
        }
        return i;
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

    public static BTMfiSessionManager getInstance(Context paramContext) {
        if (instance == null)
            instance = new BTMfiSessionManager(paramContext);
        return instance;
    }

    private BluetoothAddress getRemoteDeviceAddress() {
        BluetoothAddress bluetoothAddress1 = null;
        BluetoothAddress bluetoothAddress2 = bluetoothAddress1;
        if (this.lastMfiAddress != null)
            try {
                bluetoothAddress2 = new BluetoothAddress();
                this(this.lastMfiAddress);
            } catch (IllegalArgumentException illegalArgumentException) {
                illegalArgumentException.printStackTrace();
                bluetoothAddress2 = bluetoothAddress1;
            }
        return bluetoothAddress2;
    }

    public static String md5(byte[] paramArrayOfbyte, int paramInt) {
        String str;
        if (paramArrayOfbyte == null) {
            Log.w("BTMfiSessionManager", "array is null");
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(paramArrayOfbyte, paramInt, paramArrayOfbyte.length - paramInt);
            byte[] arrayOfByte = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            this();
            for (paramInt = 0; paramInt < arrayOfByte.length; paramInt++)
                stringBuffer.append(Integer.toHexString(arrayOfByte[paramInt] & 0xFF));
            str = stringBuffer.toString();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
            str = "";
        }
        return str;
    }

    private void notifyCallbackWithSessionResult(SPPM paramSPPM, int paramInt1, int paramInt2) {
        boolean bool = false;
        if (this.clientEventCallback.getCommandSessionId() == paramInt1) {
            bool = true;
        } else if (this.clientEventCallback.getObjectSessionId() == paramInt1) {
            bool = true;
        } else if (this.clientEventCallback.getFileSessionId() == paramInt1) {
            bool = true;
        }
        if (bool) {
            Log.d("BTMfiSessionManager", "notifying client callback");
            this.clientEventCallback.setSessionResult(paramInt1, paramInt2);
            return;
        }
        Log.d("BTMfiSessionManager", "notifying server callback");
        this.serverEventCallback.setSessionResult(paramInt1, paramInt2);
    }

    private String performChecksum(byte[] paramArrayOfbyte) {
        return md5(paramArrayOfbyte, 0);
    }

    private void sendFileData(byte[] paramArrayOfbyte) {

    }

    private void sendHUDConnectivityMessage(byte[] paramArrayOfbyte, boolean paramBoolean) {

    }

    private void showToast(final String message) {
        if (Constants.showToast)
            (new Handler(Looper.getMainLooper())).post(new Runnable() {
                public void run() {
                    Toast.makeText(BTMfiSessionManager.this.mContext, message, 1).show();
                }
            });
    }

    private byte[] subArray(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
        byte[] arrayOfByte = new byte[paramInt2];
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
            arrayOfByte[i - paramInt1] = (byte)paramArrayOfbyte[i];
        return arrayOfByte;
    }

    private byte[] uncompress(byte[] paramArrayOfbyte) {
        GZIPInputStream gZIPInputStream3;
        Log.d("BTMfiSessionManager", "uncompressing file byte array");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPInputStream gZIPInputStream1 = null;
        GZIPInputStream gZIPInputStream2 = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream();
            this(paramArrayOfbyte);
            try {
                gZIPInputStream2 = new GZIPInputStream();
                this(byteArrayInputStream);
                int i = 0;
                while (i != -1) {
                    try {
                        int j = gZIPInputStream2.read();
                        i = j;
                        if (j != -1) {
                            byteArrayOutputStream.write(j);
                            i = j;
                        }
                    } catch (IOException null) {
                        gZIPInputStream1 = gZIPInputStream2;
                        continue;
                    }
                }
                byteArrayInputStream.close();
                gZIPInputStream2.close();
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
                return byteArrayOutputStream.toByteArray();
            } catch (IOException null) {}
        } catch (IOException iOException) {
            gZIPInputStream3 = gZIPInputStream2;
        }
        Log.w("BTMfiSessionManager", "ioexception caught when parsing zipped array");
        Log.e("BTMfiSessionManager", "exception:" + iOException);
        if (byteArrayOutputStream != null)
            try {
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
            } catch (IOException iOException1) {
                iOException1.printStackTrace();
            }
        if (gZIPInputStream1 != null)
            try {
                gZIPInputStream1.close();
            } catch (IOException iOException1) {
                iOException1.printStackTrace();
            }
        byte[] arrayOfByte = paramArrayOfbyte;
        if (gZIPInputStream3 != null)
            try {
                gZIPInputStream3.close();
                arrayOfByte = paramArrayOfbyte;
            } catch (IOException iOException1) {
                iOException1.printStackTrace();
                arrayOfByte = paramArrayOfbyte;
            }
        return arrayOfByte;
    }

    private boolean writeNewDbFile(byte[] paramArrayOfbyte, String paramString) {
        boolean bool1 = false;
        if (paramString.endsWith(".txt")) {
            if (!(new File(paramString.replace("imcomingMessage.txt", ""))).mkdirs())
                Log.d("BTMfiSessionManager", "Parent directories were not created. Possibly since they already exist.");
            File file2 = new File(paramString);
            boolean bool = false;
            if (file2.exists())
                bool = file2.delete();
            if (bool) {
                Log.d("BTMfiSessionManager", "File was succesfully deleted");
            } else {
                Log.d("BTMfiSessionManager", "no file found to delete");
            }
            File file1 = new File(paramString);
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream();
                FileOutputStream fileOutputStream = new FileOutputStream();
                this(file1);
                this(fileOutputStream);
                try {
                    bufferedOutputStream.write(paramArrayOfbyte);
                    try {
                        bufferedOutputStream.flush();
                        try {
                            bufferedOutputStream.close();
                            bool = true;
                        } catch (IOException iOException) {
                            Log.w("BTMfiSessionManager", "caught exception closing file : " + iOException);
                            iOException.printStackTrace();
                            bool = bool1;
                        }
                    } catch (IOException iOException) {
                        Log.w("BTMfiSessionManager", "caught exception flushing buffer: " + iOException);
                        iOException.printStackTrace();
                        bool = bool1;
                    }
                } catch (IOException iOException) {
                    Log.w("BTMfiSessionManager", "caught exception writing to file: " + iOException);
                    iOException.printStackTrace();
                    bool = bool1;
                }
            } catch (FileNotFoundException fileNotFoundException) {
                Log.w("BTMfiSessionManager", "caught exception opening buffer: " + fileNotFoundException);
                fileNotFoundException.printStackTrace();
                bool = bool1;
            }
            return bool;
        }
        boolean bool2 = true;
    }

    public void cancelPacket(boolean paramBoolean, final int packetID) {
        Log.d("BTMfiSessionManager", "cancelPacket");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    serialPortServerManager = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager != null) {
                    int i = serialPortServerManager.cancelPacket(packetID);
                    Log.d("BTMfiSessionManager", "cancelPacket() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "cancelPacket() result: The selected Manager is not initialized");
            }
        }).start();
    }

    public final void cleanup() {

    }

    public void closeServer() {
        Log.d("BTMfiSessionManager", "closeServer");
        (new Thread() {
            public void run() {
                if (BTMfiSessionManager.this.serialPortServerManager != null) {
                    BTMfiSessionManager.this.serialPortServerManager.dispose();
                    BTMfiSessionManager.access$102(BTMfiSessionManager.this, null);
                    Log.d("BTMfiSessionManager", "Close Server result: Success");
                    return;
                }
                Log.w("BTMfiSessionManager", "Close Server result: No server is currently active.");
            }
        }).start();
    }

    public void configureMFiSettings() {
        (new Thread() {
            public void run() {
                if (BTMfiSessionManager.this.serialPortClientManager != null) {
                    int i = BTMfiSessionManager.this.serialPortClientManager.configureMFiSettings(2060, 800, null, new SPPM.MFiAccessoryInfo(512L, BluetoothAdapter.getDefaultAdapter().getName(), 65536, 65536, "Recon Instruments", "Jet", "40984E5CCE15", 7), new SPPM.MFiProtocol[] { new SPPM.MFiProtocol("com.reconinstruments.command", SPPM.MFiProtocolMatchAction.NONE), new SPPM.MFiProtocol("com.reconinstruments.object", SPPM.MFiProtocolMatchAction.NONE), new SPPM.MFiProtocol("com.reconinstruments.file", SPPM.MFiProtocolMatchAction.NONE) }"12345ABCDE", null, "en", null);
                    Log.d("BTMfiSessionManager", "configureMFiSettings() client result: " + i);
                    BTMfiSessionManager.this.showToast("configureMFiSettings() result: " + i);
                }
                SystemClock.sleep(100L);
                if (BTMfiSessionManager.this.serialPortServerManager != null) {
                    int i = BTMfiSessionManager.this.serialPortServerManager.configureMFiSettings(2060, 800, null, new SPPM.MFiAccessoryInfo(512L, BluetoothAdapter.getDefaultAdapter().getName(), 65536, 65536, "Recon Instruments", "Jet", "40984E5CCE15", 7), new SPPM.MFiProtocol[] { new SPPM.MFiProtocol("com.reconinstruments.command", SPPM.MFiProtocolMatchAction.NONE), new SPPM.MFiProtocol("com.reconinstruments.object", SPPM.MFiProtocolMatchAction.NONE), new SPPM.MFiProtocol("com.reconinstruments.file", SPPM.MFiProtocolMatchAction.NONE) }"12345ABCDE", null, "en", null);
                    Log.d("BTMfiSessionManager", "configureMFiSettings() server result: " + i);
                    BTMfiSessionManager.this.showToast("configureMFiSettings() result: " + i);
                }
            }
        }).start();
    }

    public void connectRemoteDevice(final int portNumber, final boolean waitForConnection) {
        Log.d("BTMfiSessionManager", "connectRemoteDevice");
        (new Thread() {
            public void run() {
                EnumSet<SPPM.SerialPortClientManager.ConnectionFlags> enumSet = EnumSet.noneOf(SPPM.SerialPortClientManager.ConnectionFlags.class);
                enumSet.add(SPPM.SerialPortClientManager.ConnectionFlags.REQUIRE_AUTHENTICATION);
                enumSet.add(SPPM.SerialPortClientManager.ConnectionFlags.REQUIRE_ENCRYPTION);
                enumSet.add(SPPM.SerialPortClientManager.ConnectionFlags.MFI_REQUIRED);
                if (BTMfiSessionManager.this.serialPortClientManager != null) {
                    BluetoothAddress bluetoothAddress = BTMfiSessionManager.this.getRemoteDeviceAddress();
                    if (bluetoothAddress == null) {
                        Log.d("BTMfiSessionManager", "ERROR: Bluetooth address is not formatted correctly.");
                        return;
                    }
                    int i = BTMfiSessionManager.this.serialPortClientManager.connectRemoteDevice(bluetoothAddress, portNumber, enumSet, waitForConnection);
                    if (i == 0) {
                        BTMfiSessionManager.this.setInUse(true);
                        BTProperty.setBTConnectedDeviceType(BTMfiSessionManager.this.mContext, 1);
                        BTProperty.setBTConnectionState(BTMfiSessionManager.this.mContext, 2);
                        BTProperty.setBTConnectedDeviceName(BTMfiSessionManager.this.mContext, BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothAddress.toString()).getName());
                        BTProperty.setBTConnectedDeviceAddress(BTMfiSessionManager.this.mContext, bluetoothAddress.toString());
                        BTProperty.setLastPairedDeviceAddress(BTMfiSessionManager.this.mContext, bluetoothAddress.toString());
                        BTProperty.setLastPairedDeviceName(BTMfiSessionManager.this.mContext, BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothAddress.toString()).getName());
                        BTProperty.setLastPairedDeviceType(BTMfiSessionManager.this.mContext, 1);
                    } else {
                        BTMfiSessionManager.this.setInUse(false);
                    }
                    Log.d("BTMfiSessionManager", "connectRemoteDevice() result: " + i);
                    BTMfiSessionManager.this.showToast("connectRemoteDevice() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "connectRemoteDevice() result: The Client Manager is not initialized");
            }
        }).start();
    }

    public void connectionRequestResponse(final boolean accept) {
        Log.d("BTMfiSessionManager", "connectionRequestResponse");
        (new Thread() {
            public void run() {
                if (BTMfiSessionManager.this.serialPortServerManager != null) {
                    int i = BTMfiSessionManager.this.serialPortServerManager.connectionRequestResponse(accept);
                    Log.d("BTMfiSessionManager", "connectionRequestResponse() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "connectionRequestResponse() result: There is not active server port");
            }
        }).start();
    }

    public void disconnectRemoteDevice(boolean paramBoolean, final int flushTimeout) {
        Log.d("BTMfiSessionManager", "disconnectRemoteDevice");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    Log.d("BTMfiSessionManager", "serialPortClientManager disconnectRemoteDevice()");
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    Log.d("BTMfiSessionManager", "serialPortServerManager disconnectRemoteDevice()");
                    serialPortServerManager = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager != null) {
                    BTTransportManager.broadcastStateChanged(BTMfiSessionManager.this.mContext, 0);
                    BTMfiSessionManager.this.reset();
                    BTMfiSessionManager.connectingRoute = 0;
                    BTMfiSessionManager.this.setInUse(false);
                    BTProperty.setBTConnectionState(BTMfiSessionManager.this.mContext, 0);
                    BTProperty.setBTConnectedDeviceType(BTMfiSessionManager.this.mContext, 0);
                    BTMfiSessionManager.this.serverEventCallback.setDefaultValuesForSessions();
                    BTMfiSessionManager.this.clientEventCallback.setDefaultValuesForSessions();
                    SystemClock.sleep(500L);
                    int i = BTMfiSessionManager.this.serialPortClientManager.disconnectRemoteDevice(flushTimeout);
                    Log.d("BTMfiSessionManager", "disconnectRemoteDevice() result: " + i);
                    i = BTMfiSessionManager.this.serialPortServerManager.disconnectRemoteDevice(flushTimeout);
                    Log.d("BTMfiSessionManager", "disconnectRemoteDevice() result: " + i);
                    BTMfiSessionManager.this.showToast("disconnectRemoteDevice() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "disconnectRemoteDevice() result: The selected Manager is not initialized");
            }
        }).start();
    }

    public BTConnectivityManager getBTConnectivityManager() {
        return this.mBTConnectivityManager;
    }

    public String getLastMfiAddress() {
        return this.lastMfiAddress;
    }

    public int getSendingPacketId() {
        return this.sendingPacketId;
    }

    public final void init(BTConnectivityManager paramBTConnectivityManager) {

    }

    public boolean isInUse() {
        return this.inUse;
    }

    public void openServer(int paramInt, UUID[] paramArrayOfUUID, String paramString) {
        (new Thread() {
            public void run() {
                EnumSet<SPPM.SerialPortServerManager.IncomingConnectionFlags> enumSet = EnumSet.noneOf(SPPM.SerialPortServerManager.IncomingConnectionFlags.class);
                enumSet.add(SPPM.SerialPortServerManager.IncomingConnectionFlags.REQUIRE_AUTHENTICATION);
                enumSet.add(SPPM.SerialPortServerManager.IncomingConnectionFlags.REQUIRE_ENCRYPTION);
                enumSet.add(SPPM.SerialPortServerManager.IncomingConnectionFlags.MFI_REQUIRED);
                if (BTMfiSessionManager.this.serialPortServerManager == null) {
                    try {
                        BTMfiSessionManager bTMfiSessionManager = BTMfiSessionManager.this;
                        SPPM.SerialPortServerManager serialPortServerManager = new SPPM.SerialPortServerManager();
                        this(BTMfiSessionManager.this.serverEventCallback, enumSet);
                        BTMfiSessionManager.access$102(bTMfiSessionManager, serialPortServerManager);
                        Log.d("BTMfiSessionManager", "Open Server result: Success");
                    } catch (ServerNotReachableException serverNotReachableException) {
                        serverNotReachableException.printStackTrace();
                        Log.w("BTMfiSessionManager", "Open Server result: Unable to communicate with Platform Manager service");
                    } catch (BluetopiaPMException bluetopiaPMException) {
                        bluetopiaPMException.printStackTrace();
                        Log.w("BTMfiSessionManager", "Open Server result: Unable to register server port (already in use?)");
                    }
                    return;
                }
                Log.w("BTMfiSessionManager", "Open Server result: The sample already has an active SPP server manager");
            }
        }).start();
    }

    public void openSessionRequestResponse(boolean paramBoolean1, final int sessionID, final boolean accept) {
        Log.d("BTMfiSessionManager", "openSessionRequestResponse");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    serialPortServerManager = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager != null) {
                    int i = serialPortServerManager.openSessionRequestResponse(sessionID, accept);
                    BTMfiSessionManager.this.notifyCallbackWithSessionResult((SPPM)serialPortServerManager, sessionID, i);
                    Log.d("BTMfiSessionManager", "openSessionRequestResponse() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "openSessionRequestResponse() result: The selected Manager is not initialized");
            }
        }).start();
    }

    public void queryConnectionType(boolean paramBoolean) {
        Log.d("BTMfiSessionManager", "queryConnectionType");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    serialPortServerManager = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager != null) {
                    SPPM.ConnectionType connectionType = serialPortServerManager.queryConnectionType();
                    if (connectionType != null) {
                        switch (connectionType) {
                            default:
                                return;
                            case SPP:
                                Log.d("BTMfiSessionManager", "queryConnectionType() result: SPP");
                            case MFI:
                                break;
                        }
                        Log.d("BTMfiSessionManager", "queryConnectionType() result: MFi");
                    }
                    Log.w("BTMfiSessionManager", "queryConnectionType() result: Not currently connected");
                }
                Log.w("BTMfiSessionManager", "queryConnectionType() result: The selected Manager is not initialized");
            }
        }).start();
    }

    public void queryRemoteDeviceServices(String paramString, EnumSet<SPPM.PortStatus> paramEnumSet, boolean paramBoolean, int paramInt) {
        Log.d("BTMfiSessionManager", "queryRemoteDeviceServices");
        (new Thread() {
            public void run() {
                BluetoothAddress bluetoothAddress;
                if (BTMfiSessionManager.this.serialPortClientManager != null) {
                    bluetoothAddress = BTMfiSessionManager.this.getRemoteDeviceAddress();
                    if (bluetoothAddress == null) {
                        Log.w("BTMfiSessionManager", "ERROR: Bluetooth address is not formatted correctly.");
                        return;
                    }
                } else {
                    return;
                }
                SPPM.ServiceRecordInformation[] arrayOfServiceRecordInformation = BTMfiSessionManager.this.serialPortClientManager.queryRemoteDeviceServices(bluetoothAddress);
                if (arrayOfServiceRecordInformation != null) {
                    Log.d("BTMfiSessionManager", "queryRemoteDeviceServices():");
                    int i = arrayOfServiceRecordInformation.length;
                    byte b = 0;
                    while (true) {
                        if (b < i) {
                            SPPM.ServiceRecordInformation serviceRecordInformation = arrayOfServiceRecordInformation[b];
                            Log.d("BTMfiSessionManager", "Service Record Handle     : " + serviceRecordInformation.serviceRecordHandle);
                            Log.d("BTMfiSessionManager", "Service Class             : " + serviceRecordInformation.serviceClassID.toString());
                            Log.d("BTMfiSessionManager", "Service Name              : " + serviceRecordInformation.serviceName);
                            Log.d("BTMfiSessionManager", "Service RFCOMM Port Number: " + serviceRecordInformation.rfcommPortNumber);
                            b++;
                            continue;
                        }
                        return;
                    }
                }
                Log.w("BTMfiSessionManager", "queryRemoteDeviceServices(): returned null");
            }
        }).start();
    }

    public void readData(boolean paramBoolean) {
        Log.d("BTMfiSessionManager", "readData");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager2;
                SPPM.SerialPortServerManager serialPortServerManager1 = null;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    serialPortServerManager2 = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager2 != null) {
                    String str;
                    byte[] arrayOfByte = new byte[1024];
                    int i = serialPortServerManager2.readData(arrayOfByte, 0);
                    Log.d("BTMfiSessionManager", "readData() result: " + i);
                    BTMfiSessionManager.this.showToast("readData() result: " + i);
                    byte b = 0;
                    serialPortServerManager2 = serialPortServerManager1;
                    while (b < i) {
                        str = serialPortServerManager2 + (char)arrayOfByte[b];
                        b++;
                    }
                    if (str != null)
                        Log.d("BTMfiSessionManager", str);
                    return;
                }
                Log.w("BTMfiSessionManager", "readData() result: The selected Manager is not initialized");
            }
        }).start();
    }

    void reattemptConnection() {
        disconnectRemoteDevice(false, 5000);
        try {
            Log.w("BTMfiSessionManager", "Try to connect to iOS after 1s");
            Thread.sleep(1000L);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        connectRemoteDevice(1, false);
    }

    public void receiveData(boolean paramBoolean, byte[] paramArrayOfbyte, int paramInt) {

    }

    public void reset() {
        Log.i("BTMfiSessionManager", "reset totalReceived, dataReceived and receivingBuff to 0");
        this.totalReceived = 0;
        this.dataReceived = 0;
        this.receivingBuff = ByteBuffer.allocate(0);
        this.sendingBuff = new byte[0];
        this.sendingPos = 0;
        this.sendingPacketId = 0;
        this.packetNo = 0;
    }

    public final void restart() {

    }

    public void retryOneMoreTime() {

    }

    public void sendFileData() {

    }

    public void sendLineStatus(boolean paramBoolean, final EnumSet<SPPM.LineStatus> lineStatus) {
        Log.d("BTMfiSessionManager", "sendLineStatus");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    serialPortServerManager = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager != null) {
                    int i = serialPortServerManager.sendLineStatus(lineStatus);
                    Log.d("BTMfiSessionManager", "sendLineStatus() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "sendLineStatus() result: The selected Manager is not initialized");
            }
        }).start();
    }

    public void sendNonSessionData(boolean paramBoolean, final int lingoID, final int commandID, final int transactionID) {
        Log.d("BTMfiSessionManager", "sendNonSessionData");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    serialPortServerManager = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager != null) {
                    int i = serialPortServerManager.sendNonSessionData(lingoID, commandID, transactionID, "This is a non-session data test message".getBytes());
                    Log.d("BTMfiSessionManager", "sendNonSessionData() result: " + i);
                    BTMfiSessionManager.this.showToast("sendNonSessionData() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "sendNonSessionData() result: The selected Manager is not initialized");
            }
        }).start();
    }

    public void sendPortStatus(boolean paramBoolean1, final EnumSet<SPPM.PortStatus> portStatus, final boolean breakSignal, final int breakTimeout) {
        Log.d("BTMfiSessionManager", "sendPortStatus");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    serialPortServerManager = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager != null) {
                    int i = serialPortServerManager.sendPortStatus(portStatus, breakSignal, breakTimeout);
                    Log.d("BTMfiSessionManager", "sendPortStatus() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "sendPortStatus() result: The selected Manager is not initialized");
            }
        }).start();
    }

    public void sendSessionData(boolean paramBoolean, int paramInt, byte[] paramArrayOfbyte) {

    }

    public boolean sendSessionData(HUDConnectivityService.Channel paramChannel, byte[] paramArrayOfbyte) {

    }

    public void setInUse(boolean paramBoolean) {
        this.inUse = paramBoolean;
    }

    public void setLastMfiAddress(String paramString) {
        Log.i("BTMfiSessionManager", "Set Last MfiAddress with " + paramString);
        this.lastMfiAddress = paramString;
    }

    public void setSendingPacketId(int paramInt) {
        this.sendingPacketId = paramInt;
    }

    public final void start() {

    }

    public void writeData(boolean paramBoolean) {
        Log.d("BTMfiSessionManager", "writeData");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    serialPortServerManager = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager != null) {
                    int i = serialPortServerManager.writeData("This is an SPP data test message".getBytes(), 5000);
                    Log.d("BTMfiSessionManager", "writeData() result: " + i);
                    BTMfiSessionManager.this.showToast("writeData() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "writeData() result: The selected Manager is not initialized");
            }
        }).start();
    }

    public void writeDataArbitrary(boolean paramBoolean, final String arbitraryDataString, final int writeTimeout) {
        Log.d("BTMfiSessionManager", "writeDataArbitrary");
        (new Thread() {
            public void run() {
                SPPM.SerialPortServerManager serialPortServerManager;
                if (BTMfiSessionManager.connectingRoute != 2) {
                    SPPM.SerialPortClientManager serialPortClientManager = BTMfiSessionManager.this.serialPortClientManager;
                } else {
                    serialPortServerManager = BTMfiSessionManager.this.serialPortServerManager;
                }
                if (serialPortServerManager != null) {
                    int i = serialPortServerManager.writeData(arbitraryDataString.getBytes(), writeTimeout);
                    Log.d("BTMfiSessionManager", "writeData() result: " + i);
                    return;
                }
                Log.w("BTMfiSessionManager", "writeData() result: The selected Manager is not initialized");
            }
        }).start();
    }

    public String writeFile(byte[] paramArrayOfbyte) {
        String str1;
        String str2 = performChecksum(paramArrayOfbyte) + ".tmp";
        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp/");
        if (!file2.mkdirs())
            Log.d("BTMfiSessionManager", "Parent directories were not created. Possibly since they already exist.");
        str2 = file2.getAbsolutePath() + "/" + str2;
        Log.d("BTMfiSessionManager", "temporary path: " + str2);
        file2 = new File(str2);
        boolean bool = false;
        if (file2.exists())
            bool = file2.delete();
        if (bool) {
            Log.d("BTMfiSessionManager", "file was succesfully deleted");
        } else {
            Log.d("BTMfiSessionManager", "no file found to delete");
        }
        File file1 = new File(str2);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            //FileOutputStream fileOutputStream = new FileOutputStream(file1);
            //this(file1);
            //this(fileOutputStream);
            try {
                bufferedOutputStream.write(paramArrayOfbyte);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                str1 = file1.getAbsolutePath();
            } catch (IOException iOException) {
                Log.w("BTMfiSessionManager", "caught exception closing file : " + iOException);
                iOException.printStackTrace();
                str1 = "";
            }
        } catch (FileNotFoundException fileNotFoundException) {
            Log.w("BTMfiSessionManager", "caught exception opening buffer: " + fileNotFoundException);
            fileNotFoundException.printStackTrace();
            str1 = "";
        }
        return str1;
    }
    */
}
