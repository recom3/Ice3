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
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: aload_1
        //   3: arraylength
        //   4: sipush #25600
        //   7: if_icmple -> 177
        //   10: iconst_0
        //   11: istore_2
        //   12: iconst_1
        //   13: istore_3
        //   14: iconst_1
        //   15: istore #4
        //   17: iload_3
        //   18: ifeq -> 197
        //   21: sipush #25600
        //   24: iload #4
        //   26: imul
        //   27: aload_1
        //   28: arraylength
        //   29: if_icmpge -> 118
        //   32: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.connectingRoute : I
        //   35: iconst_2
        //   36: if_icmpeq -> 94
        //   39: aload_0
        //   40: iconst_1
        //   41: aload_0
        //   42: getfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   45: invokevirtual getFileSessionId : ()I
        //   48: aload_0
        //   49: aload_1
        //   50: iload_2
        //   51: sipush #25600
        //   54: invokespecial subArray : ([BII)[B
        //   57: invokevirtual sendSessionData : (ZI[B)V
        //   60: sipush #25600
        //   63: iload #4
        //   65: imul
        //   66: istore_2
        //   67: iinc #4, 1
        //   70: ldc2_w 100
        //   73: invokestatic sleep : (J)V
        //   76: goto -> 17
        //   79: astore #5
        //   81: aload #5
        //   83: invokevirtual printStackTrace : ()V
        //   86: goto -> 17
        //   89: astore_1
        //   90: aload_0
        //   91: monitorexit
        //   92: aload_1
        //   93: athrow
        //   94: aload_0
        //   95: iconst_1
        //   96: aload_0
        //   97: getfield serverEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback;
        //   100: invokevirtual getFileSessionId : ()I
        //   103: aload_0
        //   104: aload_1
        //   105: iload_2
        //   106: sipush #25600
        //   109: invokespecial subArray : ([BII)[B
        //   112: invokevirtual sendSessionData : (ZI[B)V
        //   115: goto -> 60
        //   118: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.connectingRoute : I
        //   121: iconst_2
        //   122: if_icmpeq -> 152
        //   125: aload_0
        //   126: iconst_1
        //   127: aload_0
        //   128: getfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   131: invokevirtual getFileSessionId : ()I
        //   134: aload_0
        //   135: aload_1
        //   136: iload_2
        //   137: aload_1
        //   138: arraylength
        //   139: iload_2
        //   140: isub
        //   141: invokespecial subArray : ([BII)[B
        //   144: invokevirtual sendSessionData : (ZI[B)V
        //   147: iconst_0
        //   148: istore_3
        //   149: goto -> 70
        //   152: aload_0
        //   153: iconst_1
        //   154: aload_0
        //   155: getfield serverEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback;
        //   158: invokevirtual getFileSessionId : ()I
        //   161: aload_0
        //   162: aload_1
        //   163: iload_2
        //   164: aload_1
        //   165: arraylength
        //   166: iload_2
        //   167: isub
        //   168: invokespecial subArray : ([BII)[B
        //   171: invokevirtual sendSessionData : (ZI[B)V
        //   174: goto -> 147
        //   177: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.connectingRoute : I
        //   180: iconst_2
        //   181: if_icmpeq -> 200
        //   184: aload_0
        //   185: iconst_1
        //   186: aload_0
        //   187: getfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   190: invokevirtual getFileSessionId : ()I
        //   193: aload_1
        //   194: invokevirtual sendSessionData : (ZI[B)V
        //   197: aload_0
        //   198: monitorexit
        //   199: return
        //   200: aload_0
        //   201: iconst_1
        //   202: aload_0
        //   203: getfield serverEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback;
        //   206: invokevirtual getFileSessionId : ()I
        //   209: aload_1
        //   210: invokevirtual sendSessionData : (ZI[B)V
        //   213: goto -> 197
        // Exception table:
        //   from	to	target	type
        //   2	10	89	finally
        //   21	60	89	finally
        //   70	76	79	java/lang/InterruptedException
        //   70	76	89	finally
        //   81	86	89	finally
        //   94	115	89	finally
        //   118	147	89	finally
        //   152	174	89	finally
        //   177	197	89	finally
        //   200	213	89	finally
    }

    private void sendHUDConnectivityMessage(byte[] paramArrayOfbyte, boolean paramBoolean) {
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: new com/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityMessage
        //   5: astore_3
        //   6: aload_3
        //   7: aload_1
        //   8: invokespecial <init> : ([B)V
        //   11: aload_3
        //   12: ifnull -> 255
        //   15: aload_3
        //   16: invokevirtual getIntentFilter : ()Ljava/lang/String;
        //   19: ifnull -> 255
        //   22: new java/lang/StringBuilder
        //   25: astore_1
        //   26: aload_1
        //   27: invokespecial <init> : ()V
        //   30: ldc 'BTMfiSessionManager'
        //   32: aload_1
        //   33: ldc_w 'md5(cMsg.getData(),0) = '
        //   36: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   39: aload_3
        //   40: invokevirtual getData : ()[B
        //   43: iconst_0
        //   44: invokestatic md5 : ([BI)Ljava/lang/String;
        //   47: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   50: invokevirtual toString : ()Ljava/lang/String;
        //   53: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   56: pop
        //   57: getstatic com/reconinstruments/mobilesdk/hudconnectivity/Constants.showToast : Z
        //   60: ifeq -> 107
        //   63: aload_0
        //   64: getfield mContext : Landroid/content/Context;
        //   67: invokevirtual getApplicationContext : ()Landroid/content/Context;
        //   70: astore_1
        //   71: new java/lang/StringBuilder
        //   74: astore #4
        //   76: aload #4
        //   78: invokespecial <init> : ()V
        //   81: aload_1
        //   82: aload #4
        //   84: ldc_w 'Received the message '
        //   87: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   90: aload_3
        //   91: invokevirtual toString : ()Ljava/lang/String;
        //   94: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   97: invokevirtual toString : ()Ljava/lang/String;
        //   100: iconst_1
        //   101: invokestatic makeText : (Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;
        //   104: invokevirtual show : ()V
        //   107: new android/content/Intent
        //   110: astore_1
        //   111: aload_1
        //   112: aload_3
        //   113: invokevirtual getIntentFilter : ()Ljava/lang/String;
        //   116: invokespecial <init> : (Ljava/lang/String;)V
        //   119: iload_2
        //   120: ifeq -> 151
        //   123: ldc 'BTMfiSessionManager'
        //   125: ldc_w 'Changing file for a pointer in HUD message'
        //   128: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   131: pop
        //   132: aload_3
        //   133: aload_0
        //   134: aload_0
        //   135: aload_3
        //   136: invokevirtual getData : ()[B
        //   139: invokespecial uncompress : ([B)[B
        //   142: invokevirtual writeFile : ([B)Ljava/lang/String;
        //   145: invokevirtual getBytes : ()[B
        //   148: invokevirtual setData : ([B)V
        //   151: aload_1
        //   152: ldc_w 'message'
        //   155: aload_3
        //   156: invokevirtual toByteArray : ()[B
        //   159: invokevirtual putExtra : (Ljava/lang/String;[B)Landroid/content/Intent;
        //   162: pop
        //   163: aload_0
        //   164: getfield mContext : Landroid/content/Context;
        //   167: aload_1
        //   168: invokevirtual sendBroadcast : (Landroid/content/Intent;)V
        //   171: new java/lang/StringBuilder
        //   174: astore_1
        //   175: aload_1
        //   176: invokespecial <init> : ()V
        //   179: ldc 'BTMfiSessionManager'
        //   181: aload_1
        //   182: ldc_w 'Sent out the broadcast to '
        //   185: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   188: aload_3
        //   189: invokevirtual getIntentFilter : ()Ljava/lang/String;
        //   192: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   195: invokevirtual toString : ()Ljava/lang/String;
        //   198: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   201: pop
        //   202: getstatic com/reconinstruments/mobilesdk/hudconnectivity/Constants.showToast : Z
        //   205: ifeq -> 252
        //   208: aload_0
        //   209: getfield mContext : Landroid/content/Context;
        //   212: invokevirtual getApplicationContext : ()Landroid/content/Context;
        //   215: astore_1
        //   216: new java/lang/StringBuilder
        //   219: astore #4
        //   221: aload #4
        //   223: invokespecial <init> : ()V
        //   226: aload_1
        //   227: aload #4
        //   229: ldc_w 'Sent out the broadcast to '
        //   232: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   235: aload_3
        //   236: invokevirtual getIntentFilter : ()Ljava/lang/String;
        //   239: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   242: invokevirtual toString : ()Ljava/lang/String;
        //   245: iconst_1
        //   246: invokestatic makeText : (Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;
        //   249: invokevirtual show : ()V
        //   252: aload_0
        //   253: monitorexit
        //   254: return
        //   255: new java/lang/StringBuilder
        //   258: astore_1
        //   259: aload_1
        //   260: invokespecial <init> : ()V
        //   263: ldc 'BTMfiSessionManager'
        //   265: aload_1
        //   266: ldc_w 'Received the message '
        //   269: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   272: aload_3
        //   273: invokevirtual toString : ()Ljava/lang/String;
        //   276: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   279: invokevirtual toString : ()Ljava/lang/String;
        //   282: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   285: pop
        //   286: goto -> 252
        //   289: astore_1
        //   290: aload_0
        //   291: monitorexit
        //   292: aload_1
        //   293: athrow
        // Exception table:
        //   from	to	target	type
        //   2	11	289	finally
        //   15	107	289	finally
        //   107	119	289	finally
        //   123	151	289	finally
        //   151	252	289	finally
        //   255	286	289	finally
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
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: ldc 'BTMfiSessionManager'
        //   4: ldc_w 'cleanup'
        //   7: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   10: pop
        //   11: aload_0
        //   12: invokevirtual closeServer : ()V
        //   15: aload_0
        //   16: getfield serialPortClientManager : Lcom/stonestreetone/bluetopiapm/SPPM$SerialPortClientManager;
        //   19: ifnull -> 43
        //   22: ldc 'BTMfiSessionManager'
        //   24: ldc_w 'serialPortServerManager :disconnectRemoteDevice'
        //   27: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   30: pop
        //   31: aload_0
        //   32: getfield serialPortClientManager : Lcom/stonestreetone/bluetopiapm/SPPM$SerialPortClientManager;
        //   35: invokevirtual dispose : ()V
        //   38: aload_0
        //   39: aconst_null
        //   40: putfield serialPortClientManager : Lcom/stonestreetone/bluetopiapm/SPPM$SerialPortClientManager;
        //   43: aload_0
        //   44: iconst_0
        //   45: invokevirtual setInUse : (Z)V
        //   48: aload_0
        //   49: monitorexit
        //   50: return
        //   51: astore_1
        //   52: aload_0
        //   53: monitorexit
        //   54: aload_1
        //   55: athrow
        // Exception table:
        //   from	to	target	type
        //   2	43	51	finally
        //   43	48	51	finally
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
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: aload_0
        //   3: aload_1
        //   4: putfield mBTConnectivityManager : Lcom/reconinstruments/mobilesdk/btconnectivity/BTConnectivityManager;
        //   7: aload_0
        //   8: invokevirtual start : ()V
        //   11: aload_0
        //   12: monitorexit
        //   13: return
        //   14: astore_1
        //   15: aload_0
        //   16: monitorexit
        //   17: aload_1
        //   18: athrow
        // Exception table:
        //   from	to	target	type
        //   2	11	14	finally
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
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: iload_1
        //   3: ifne -> 15
        //   6: aload_0
        //   7: aload_2
        //   8: iconst_0
        //   9: invokespecial sendHUDConnectivityMessage : ([BZ)V
        //   12: aload_0
        //   13: monitorexit
        //   14: return
        //   15: aload_0
        //   16: getfield receivingBuff : Ljava/nio/ByteBuffer;
        //   19: invokevirtual remaining : ()I
        //   22: istore #4
        //   24: iload #4
        //   26: istore #5
        //   28: iload #4
        //   30: ifne -> 93
        //   33: aload_0
        //   34: iconst_0
        //   35: putfield totalReceived : I
        //   38: aload_0
        //   39: iconst_0
        //   40: putfield dataReceived : I
        //   43: aload_0
        //   44: iconst_0
        //   45: invokestatic allocate : (I)Ljava/nio/ByteBuffer;
        //   48: putfield receivingBuff : Ljava/nio/ByteBuffer;
        //   51: iload_3
        //   52: bipush #16
        //   54: if_icmplt -> 12
        //   57: aload_0
        //   58: aload_0
        //   59: aload_2
        //   60: invokespecial byteArrayToInt : ([B)I
        //   63: putfield totalReceived : I
        //   66: aload_0
        //   67: getfield totalReceived : I
        //   70: ifle -> 12
        //   73: aload_0
        //   74: aload_0
        //   75: getfield totalReceived : I
        //   78: invokestatic allocate : (I)Ljava/nio/ByteBuffer;
        //   81: putfield receivingBuff : Ljava/nio/ByteBuffer;
        //   84: aload_0
        //   85: getfield receivingBuff : Ljava/nio/ByteBuffer;
        //   88: invokevirtual remaining : ()I
        //   91: istore #5
        //   93: iload #5
        //   95: iload_3
        //   96: if_icmpge -> 187
        //   99: aload_0
        //   100: getfield receivingBuff : Ljava/nio/ByteBuffer;
        //   103: aload_2
        //   104: iconst_0
        //   105: iload #5
        //   107: invokevirtual put : ([BII)Ljava/nio/ByteBuffer;
        //   110: pop
        //   111: aload_0
        //   112: aload_0
        //   113: getfield dataReceived : I
        //   116: iload #5
        //   118: iadd
        //   119: putfield dataReceived : I
        //   122: aload_0
        //   123: getfield receivingBuff : Ljava/nio/ByteBuffer;
        //   126: invokevirtual remaining : ()I
        //   129: ifne -> 12
        //   132: aload_0
        //   133: getfield receivingBuff : Ljava/nio/ByteBuffer;
        //   136: invokevirtual array : ()[B
        //   139: invokevirtual clone : ()Ljava/lang/Object;
        //   142: checkcast [B
        //   145: astore_2
        //   146: ldc 'BTMfiSessionManager'
        //   148: ldc_w 'Stop receiving data, constructing the HUDConnectivityMessage'
        //   151: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   154: pop
        //   155: aload_0
        //   156: aload_2
        //   157: iload_1
        //   158: invokespecial sendHUDConnectivityMessage : ([BZ)V
        //   161: aload_0
        //   162: iconst_0
        //   163: putfield totalReceived : I
        //   166: aload_0
        //   167: iconst_0
        //   168: putfield dataReceived : I
        //   171: aload_0
        //   172: iconst_0
        //   173: invokestatic allocate : (I)Ljava/nio/ByteBuffer;
        //   176: putfield receivingBuff : Ljava/nio/ByteBuffer;
        //   179: goto -> 12
        //   182: astore_2
        //   183: aload_0
        //   184: monitorexit
        //   185: aload_2
        //   186: athrow
        //   187: aload_0
        //   188: getfield receivingBuff : Ljava/nio/ByteBuffer;
        //   191: aload_2
        //   192: iconst_0
        //   193: iload_3
        //   194: invokevirtual put : ([BII)Ljava/nio/ByteBuffer;
        //   197: pop
        //   198: aload_0
        //   199: aload_0
        //   200: getfield dataReceived : I
        //   203: iload_3
        //   204: iadd
        //   205: putfield dataReceived : I
        //   208: goto -> 122
        // Exception table:
        //   from	to	target	type
        //   6	12	182	finally
        //   15	24	182	finally
        //   33	51	182	finally
        //   57	93	182	finally
        //   99	122	182	finally
        //   122	179	182	finally
        //   187	208	182	finally
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
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: aload_0
        //   3: iconst_1
        //   4: sipush #5000
        //   7: invokevirtual disconnectRemoteDevice : (ZI)V
        //   10: aload_0
        //   11: iconst_0
        //   12: invokevirtual setInUse : (Z)V
        //   15: aload_0
        //   16: monitorexit
        //   17: return
        //   18: astore_1
        //   19: aload_0
        //   20: monitorexit
        //   21: aload_1
        //   22: athrow
        // Exception table:
        //   from	to	target	type
        //   2	15	18	finally
    }

    public void retryOneMoreTime() {
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: aload_0
        //   3: aload_0
        //   4: getfield sendingPos : I
        //   7: sipush #25600
        //   10: isub
        //   11: putfield sendingPos : I
        //   14: aload_0
        //   15: getfield sendingPos : I
        //   18: ifge -> 26
        //   21: aload_0
        //   22: iconst_0
        //   23: putfield sendingPos : I
        //   26: aload_0
        //   27: monitorexit
        //   28: return
        //   29: astore_1
        //   30: aload_0
        //   31: monitorexit
        //   32: aload_1
        //   33: athrow
        // Exception table:
        //   from	to	target	type
        //   2	26	29	finally
    }

    public void sendFileData() {
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: aload_0
        //   3: getfield sendingBuff : [B
        //   6: arraylength
        //   7: sipush #25600
        //   10: if_icmple -> 628
        //   13: aload_0
        //   14: getfield sendingPos : I
        //   17: sipush #25600
        //   20: iadd
        //   21: aload_0
        //   22: getfield sendingBuff : [B
        //   25: arraylength
        //   26: if_icmpgt -> 281
        //   29: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.connectingRoute : I
        //   32: iconst_2
        //   33: if_icmpeq -> 165
        //   36: aload_0
        //   37: iconst_1
        //   38: aload_0
        //   39: getfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   42: invokevirtual getFileSessionId : ()I
        //   45: aload_0
        //   46: aload_0
        //   47: getfield sendingBuff : [B
        //   50: aload_0
        //   51: getfield sendingPos : I
        //   54: sipush #25600
        //   57: invokespecial subArray : ([BII)[B
        //   60: invokevirtual sendSessionData : (ZI[B)V
        //   63: ldc 'BTMfiSessionManager'
        //   65: ldc_w 'sent out message.size = 25600'
        //   68: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   71: pop
        //   72: new java/lang/StringBuilder
        //   75: astore_1
        //   76: aload_1
        //   77: invokespecial <init> : ()V
        //   80: ldc 'BTMfiSessionManager'
        //   82: aload_1
        //   83: ldc_w 'sendingPos = '
        //   86: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   89: aload_0
        //   90: getfield sendingPos : I
        //   93: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   96: invokevirtual toString : ()Ljava/lang/String;
        //   99: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   102: pop
        //   103: aload_0
        //   104: aload_0
        //   105: getfield packetNo : I
        //   108: iconst_1
        //   109: iadd
        //   110: putfield packetNo : I
        //   113: new java/lang/StringBuilder
        //   116: astore_1
        //   117: aload_1
        //   118: invokespecial <init> : ()V
        //   121: ldc 'BTMfiSessionManager'
        //   123: aload_1
        //   124: ldc_w 'packetNo = '
        //   127: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   130: aload_0
        //   131: getfield packetNo : I
        //   134: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   137: invokevirtual toString : ()Ljava/lang/String;
        //   140: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   143: pop
        //   144: aload_0
        //   145: aload_0
        //   146: getfield sendingPos : I
        //   149: sipush #25600
        //   152: iadd
        //   153: putfield sendingPos : I
        //   156: ldc2_w 100
        //   159: invokestatic sleep : (J)V
        //   162: aload_0
        //   163: monitorexit
        //   164: return
        //   165: aload_0
        //   166: iconst_1
        //   167: aload_0
        //   168: getfield serverEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback;
        //   171: invokevirtual getFileSessionId : ()I
        //   174: aload_0
        //   175: aload_0
        //   176: getfield sendingBuff : [B
        //   179: aload_0
        //   180: getfield sendingPos : I
        //   183: sipush #25600
        //   186: invokespecial subArray : ([BII)[B
        //   189: invokevirtual sendSessionData : (ZI[B)V
        //   192: ldc 'BTMfiSessionManager'
        //   194: ldc_w 'sent out message.size = 25600'
        //   197: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   200: pop
        //   201: new java/lang/StringBuilder
        //   204: astore_1
        //   205: aload_1
        //   206: invokespecial <init> : ()V
        //   209: ldc 'BTMfiSessionManager'
        //   211: aload_1
        //   212: ldc_w 'sendingPos = '
        //   215: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   218: aload_0
        //   219: getfield sendingPos : I
        //   222: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   225: invokevirtual toString : ()Ljava/lang/String;
        //   228: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   231: pop
        //   232: aload_0
        //   233: aload_0
        //   234: getfield packetNo : I
        //   237: iconst_1
        //   238: iadd
        //   239: putfield packetNo : I
        //   242: new java/lang/StringBuilder
        //   245: astore_1
        //   246: aload_1
        //   247: invokespecial <init> : ()V
        //   250: ldc 'BTMfiSessionManager'
        //   252: aload_1
        //   253: ldc_w 'packetNo = '
        //   256: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   259: aload_0
        //   260: getfield packetNo : I
        //   263: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   266: invokevirtual toString : ()Ljava/lang/String;
        //   269: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   272: pop
        //   273: goto -> 144
        //   276: astore_1
        //   277: aload_0
        //   278: monitorexit
        //   279: aload_1
        //   280: athrow
        //   281: aload_0
        //   282: getfield sendingBuff : [B
        //   285: arraylength
        //   286: aload_0
        //   287: getfield sendingPos : I
        //   290: isub
        //   291: ifle -> 454
        //   294: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.connectingRoute : I
        //   297: iconst_2
        //   298: if_icmpeq -> 474
        //   301: aload_0
        //   302: iconst_1
        //   303: aload_0
        //   304: getfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   307: invokevirtual getFileSessionId : ()I
        //   310: aload_0
        //   311: aload_0
        //   312: getfield sendingBuff : [B
        //   315: aload_0
        //   316: getfield sendingPos : I
        //   319: aload_0
        //   320: getfield sendingBuff : [B
        //   323: arraylength
        //   324: aload_0
        //   325: getfield sendingPos : I
        //   328: isub
        //   329: invokespecial subArray : ([BII)[B
        //   332: invokevirtual sendSessionData : (ZI[B)V
        //   335: new java/lang/StringBuilder
        //   338: astore_1
        //   339: aload_1
        //   340: invokespecial <init> : ()V
        //   343: ldc 'BTMfiSessionManager'
        //   345: aload_1
        //   346: ldc_w 'sent out message.size = '
        //   349: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   352: aload_0
        //   353: getfield sendingBuff : [B
        //   356: arraylength
        //   357: aload_0
        //   358: getfield sendingPos : I
        //   361: isub
        //   362: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   365: invokevirtual toString : ()Ljava/lang/String;
        //   368: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   371: pop
        //   372: new java/lang/StringBuilder
        //   375: astore_1
        //   376: aload_1
        //   377: invokespecial <init> : ()V
        //   380: ldc 'BTMfiSessionManager'
        //   382: aload_1
        //   383: ldc_w 'sendingPos = '
        //   386: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   389: aload_0
        //   390: getfield sendingPos : I
        //   393: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   396: invokevirtual toString : ()Ljava/lang/String;
        //   399: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   402: pop
        //   403: aload_0
        //   404: aload_0
        //   405: getfield packetNo : I
        //   408: iconst_1
        //   409: iadd
        //   410: putfield packetNo : I
        //   413: new java/lang/StringBuilder
        //   416: astore_1
        //   417: aload_1
        //   418: invokespecial <init> : ()V
        //   421: ldc 'BTMfiSessionManager'
        //   423: aload_1
        //   424: ldc_w 'packetNo = '
        //   427: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   430: aload_0
        //   431: getfield packetNo : I
        //   434: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   437: invokevirtual toString : ()Ljava/lang/String;
        //   440: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   443: pop
        //   444: ldc2_w 100
        //   447: invokestatic sleep : (J)V
        //   450: iconst_1
        //   451: putstatic com/reconinstruments/mobilesdk/btconnectivity/BTConnector.fileTransferDone : Z
        //   454: aload_0
        //   455: iconst_0
        //   456: newarray byte
        //   458: putfield sendingBuff : [B
        //   461: aload_0
        //   462: iconst_0
        //   463: putfield sendingPos : I
        //   466: aload_0
        //   467: iconst_0
        //   468: putfield sendingPacketId : I
        //   471: goto -> 156
        //   474: aload_0
        //   475: iconst_1
        //   476: aload_0
        //   477: getfield serverEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback;
        //   480: invokevirtual getFileSessionId : ()I
        //   483: aload_0
        //   484: aload_0
        //   485: getfield sendingBuff : [B
        //   488: aload_0
        //   489: getfield sendingPos : I
        //   492: aload_0
        //   493: getfield sendingBuff : [B
        //   496: arraylength
        //   497: aload_0
        //   498: getfield sendingPos : I
        //   501: isub
        //   502: invokespecial subArray : ([BII)[B
        //   505: invokevirtual sendSessionData : (ZI[B)V
        //   508: new java/lang/StringBuilder
        //   511: astore_1
        //   512: aload_1
        //   513: invokespecial <init> : ()V
        //   516: ldc 'BTMfiSessionManager'
        //   518: aload_1
        //   519: ldc_w 'sent out message.size = '
        //   522: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   525: aload_0
        //   526: getfield sendingBuff : [B
        //   529: arraylength
        //   530: aload_0
        //   531: getfield sendingPos : I
        //   534: isub
        //   535: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   538: invokevirtual toString : ()Ljava/lang/String;
        //   541: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   544: pop
        //   545: new java/lang/StringBuilder
        //   548: astore_1
        //   549: aload_1
        //   550: invokespecial <init> : ()V
        //   553: ldc 'BTMfiSessionManager'
        //   555: aload_1
        //   556: ldc_w 'sendingPos = '
        //   559: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   562: aload_0
        //   563: getfield sendingPos : I
        //   566: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   569: invokevirtual toString : ()Ljava/lang/String;
        //   572: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   575: pop
        //   576: aload_0
        //   577: aload_0
        //   578: getfield packetNo : I
        //   581: iconst_1
        //   582: iadd
        //   583: putfield packetNo : I
        //   586: new java/lang/StringBuilder
        //   589: astore_1
        //   590: aload_1
        //   591: invokespecial <init> : ()V
        //   594: ldc 'BTMfiSessionManager'
        //   596: aload_1
        //   597: ldc_w 'packetNo = '
        //   600: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   603: aload_0
        //   604: getfield packetNo : I
        //   607: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   610: invokevirtual toString : ()Ljava/lang/String;
        //   613: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   616: pop
        //   617: goto -> 444
        //   620: astore_1
        //   621: aload_1
        //   622: invokevirtual printStackTrace : ()V
        //   625: goto -> 162
        //   628: aload_0
        //   629: getfield sendingBuff : [B
        //   632: arraylength
        //   633: ifle -> 773
        //   636: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.connectingRoute : I
        //   639: iconst_2
        //   640: if_icmpeq -> 793
        //   643: aload_0
        //   644: iconst_1
        //   645: aload_0
        //   646: getfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   649: invokevirtual getFileSessionId : ()I
        //   652: aload_0
        //   653: getfield sendingBuff : [B
        //   656: invokevirtual sendSessionData : (ZI[B)V
        //   659: new java/lang/StringBuilder
        //   662: astore_1
        //   663: aload_1
        //   664: invokespecial <init> : ()V
        //   667: ldc 'BTMfiSessionManager'
        //   669: aload_1
        //   670: ldc_w 'sent out message.size = '
        //   673: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   676: aload_0
        //   677: getfield sendingBuff : [B
        //   680: arraylength
        //   681: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   684: invokevirtual toString : ()Ljava/lang/String;
        //   687: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   690: pop
        //   691: new java/lang/StringBuilder
        //   694: astore_1
        //   695: aload_1
        //   696: invokespecial <init> : ()V
        //   699: ldc 'BTMfiSessionManager'
        //   701: aload_1
        //   702: ldc_w 'sendingPos = '
        //   705: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   708: aload_0
        //   709: getfield sendingPos : I
        //   712: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   715: invokevirtual toString : ()Ljava/lang/String;
        //   718: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   721: pop
        //   722: aload_0
        //   723: aload_0
        //   724: getfield packetNo : I
        //   727: iconst_1
        //   728: iadd
        //   729: putfield packetNo : I
        //   732: new java/lang/StringBuilder
        //   735: astore_1
        //   736: aload_1
        //   737: invokespecial <init> : ()V
        //   740: ldc 'BTMfiSessionManager'
        //   742: aload_1
        //   743: ldc_w 'packetNo = '
        //   746: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   749: aload_0
        //   750: getfield packetNo : I
        //   753: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   756: invokevirtual toString : ()Ljava/lang/String;
        //   759: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   762: pop
        //   763: ldc2_w 100
        //   766: invokestatic sleep : (J)V
        //   769: iconst_1
        //   770: putstatic com/reconinstruments/mobilesdk/btconnectivity/BTConnector.fileTransferDone : Z
        //   773: aload_0
        //   774: iconst_0
        //   775: newarray byte
        //   777: putfield sendingBuff : [B
        //   780: aload_0
        //   781: iconst_0
        //   782: putfield sendingPos : I
        //   785: aload_0
        //   786: iconst_0
        //   787: putfield sendingPacketId : I
        //   790: goto -> 162
        //   793: aload_0
        //   794: iconst_1
        //   795: aload_0
        //   796: getfield serverEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback;
        //   799: invokevirtual getFileSessionId : ()I
        //   802: aload_0
        //   803: getfield sendingBuff : [B
        //   806: invokevirtual sendSessionData : (ZI[B)V
        //   809: new java/lang/StringBuilder
        //   812: astore_1
        //   813: aload_1
        //   814: invokespecial <init> : ()V
        //   817: ldc 'BTMfiSessionManager'
        //   819: aload_1
        //   820: ldc_w 'sent out message.size = '
        //   823: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   826: aload_0
        //   827: getfield sendingBuff : [B
        //   830: arraylength
        //   831: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   834: invokevirtual toString : ()Ljava/lang/String;
        //   837: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   840: pop
        //   841: new java/lang/StringBuilder
        //   844: astore_1
        //   845: aload_1
        //   846: invokespecial <init> : ()V
        //   849: ldc 'BTMfiSessionManager'
        //   851: aload_1
        //   852: ldc_w 'sendingPos = '
        //   855: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   858: aload_0
        //   859: getfield sendingPos : I
        //   862: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   865: invokevirtual toString : ()Ljava/lang/String;
        //   868: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   871: pop
        //   872: aload_0
        //   873: aload_0
        //   874: getfield packetNo : I
        //   877: iconst_1
        //   878: iadd
        //   879: putfield packetNo : I
        //   882: new java/lang/StringBuilder
        //   885: astore_1
        //   886: aload_1
        //   887: invokespecial <init> : ()V
        //   890: ldc 'BTMfiSessionManager'
        //   892: aload_1
        //   893: ldc_w 'packetNo = '
        //   896: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   899: aload_0
        //   900: getfield packetNo : I
        //   903: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   906: invokevirtual toString : ()Ljava/lang/String;
        //   909: invokestatic d : (Ljava/lang/String;Ljava/lang/String;)I
        //   912: pop
        //   913: goto -> 763
        // Exception table:
        //   from	to	target	type
        //   2	144	276	finally
        //   144	156	276	finally
        //   156	162	620	java/lang/InterruptedException
        //   156	162	276	finally
        //   165	273	276	finally
        //   281	444	276	finally
        //   444	454	276	finally
        //   454	471	276	finally
        //   474	617	276	finally
        //   621	625	276	finally
        //   628	763	276	finally
        //   763	773	276	finally
        //   773	790	276	finally
        //   793	913	276	finally
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
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: iload_2
        //   3: ifle -> 24
        //   6: new com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager$11
        //   9: astore #4
        //   11: aload #4
        //   13: aload_0
        //   14: aload_3
        //   15: iload_2
        //   16: invokespecial <init> : (Lcom/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager;[BI)V
        //   19: aload #4
        //   21: invokevirtual start : ()V
        //   24: aload_0
        //   25: monitorexit
        //   26: return
        //   27: astore_3
        //   28: aload_0
        //   29: monitorexit
        //   30: aload_3
        //   31: athrow
        // Exception table:
        //   from	to	target	type
        //   6	24	27	finally
    }

    public boolean sendSessionData(HUDConnectivityService.Channel paramChannel, byte[] paramArrayOfbyte) {
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: aload_2
        //   3: ifnull -> 519
        //   6: getstatic com/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel.COMMAND_CHANNEL : Lcom/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel;
        //   9: aload_1
        //   10: invokevirtual compareTo : (Ljava/lang/Enum;)I
        //   13: ifne -> 110
        //   16: new java/lang/StringBuilder
        //   19: astore_1
        //   20: aload_1
        //   21: invokespecial <init> : ()V
        //   24: ldc 'BTMfiSessionManager'
        //   26: aload_1
        //   27: ldc_w 'Received data sending request from COMMAND_CHANNEL, size = '
        //   30: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   33: aload_2
        //   34: arraylength
        //   35: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   38: invokevirtual toString : ()Ljava/lang/String;
        //   41: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   44: pop
        //   45: aload_2
        //   46: arraylength
        //   47: sipush #25600
        //   50: if_icmpgt -> 98
        //   53: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.connectingRoute : I
        //   56: iconst_2
        //   57: if_icmpeq -> 77
        //   60: aload_0
        //   61: iconst_1
        //   62: aload_0
        //   63: getfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   66: invokevirtual getCommandSessionId : ()I
        //   69: aload_2
        //   70: invokevirtual sendSessionData : (ZI[B)V
        //   73: aload_0
        //   74: monitorexit
        //   75: iconst_1
        //   76: ireturn
        //   77: aload_0
        //   78: iconst_1
        //   79: aload_0
        //   80: getfield serverEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback;
        //   83: invokevirtual getCommandSessionId : ()I
        //   86: aload_2
        //   87: invokevirtual sendSessionData : (ZI[B)V
        //   90: goto -> 73
        //   93: astore_1
        //   94: aload_0
        //   95: monitorexit
        //   96: aload_1
        //   97: athrow
        //   98: ldc 'BTMfiSessionManager'
        //   100: ldc_w 'The message size is larger than 25k. Rejected.'
        //   103: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
        //   106: pop
        //   107: goto -> 73
        //   110: getstatic com/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel.OBJECT_CHANNEL : Lcom/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel;
        //   113: aload_1
        //   114: invokevirtual compareTo : (Ljava/lang/Enum;)I
        //   117: ifne -> 208
        //   120: aload_2
        //   121: arraylength
        //   122: sipush #25600
        //   125: if_icmpgt -> 196
        //   128: new java/lang/StringBuilder
        //   131: astore_1
        //   132: aload_1
        //   133: invokespecial <init> : ()V
        //   136: ldc 'BTMfiSessionManager'
        //   138: aload_1
        //   139: ldc_w 'Received data sending request from OBJECT_CHANNEL, size = '
        //   142: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   145: aload_2
        //   146: arraylength
        //   147: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   150: invokevirtual toString : ()Ljava/lang/String;
        //   153: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   156: pop
        //   157: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.connectingRoute : I
        //   160: iconst_2
        //   161: if_icmpeq -> 180
        //   164: aload_0
        //   165: iconst_1
        //   166: aload_0
        //   167: getfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   170: invokevirtual getObjectSessionId : ()I
        //   173: aload_2
        //   174: invokevirtual sendSessionData : (ZI[B)V
        //   177: goto -> 73
        //   180: aload_0
        //   181: iconst_1
        //   182: aload_0
        //   183: getfield serverEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback;
        //   186: invokevirtual getObjectSessionId : ()I
        //   189: aload_2
        //   190: invokevirtual sendSessionData : (ZI[B)V
        //   193: goto -> 73
        //   196: ldc 'BTMfiSessionManager'
        //   198: ldc_w 'The message size is larger than 25k. Rejected.'
        //   201: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
        //   204: pop
        //   205: goto -> 73
        //   208: getstatic com/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel.FILE_CHANNEL : Lcom/reconinstruments/mobilesdk/hudconnectivity/HUDConnectivityService$Channel;
        //   211: aload_1
        //   212: invokevirtual compareTo : (Ljava/lang/Enum;)I
        //   215: ifne -> 507
        //   218: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.connectingRoute : I
        //   221: iconst_2
        //   222: if_icmpeq -> 365
        //   225: new java/lang/StringBuilder
        //   228: astore_1
        //   229: aload_1
        //   230: invokespecial <init> : ()V
        //   233: ldc 'BTMfiSessionManager'
        //   235: aload_1
        //   236: ldc_w 'Received data sending request from FILE_CHANNEL, size = '
        //   239: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   242: aload_2
        //   243: arraylength
        //   244: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   247: invokevirtual toString : ()Ljava/lang/String;
        //   250: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   253: pop
        //   254: aload_0
        //   255: aload_2
        //   256: putfield sendingBuff : [B
        //   259: aload_0
        //   260: iconst_0
        //   261: putfield sendingPos : I
        //   264: aload_0
        //   265: iconst_0
        //   266: putfield sendingPacketId : I
        //   269: aload_0
        //   270: iconst_0
        //   271: putfield packetNo : I
        //   274: aload_2
        //   275: arraylength
        //   276: sipush #25600
        //   279: irem
        //   280: ifne -> 327
        //   283: new java/lang/StringBuilder
        //   286: astore_1
        //   287: aload_1
        //   288: invokespecial <init> : ()V
        //   291: ldc 'BTMfiSessionManager'
        //   293: aload_1
        //   294: ldc_w 'total packets should be sent: '
        //   297: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   300: aload_2
        //   301: arraylength
        //   302: sipush #25600
        //   305: idiv
        //   306: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   309: invokevirtual toString : ()Ljava/lang/String;
        //   312: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   315: pop
        //   316: iconst_0
        //   317: putstatic com/reconinstruments/mobilesdk/btconnectivity/BTConnector.fileTransferDone : Z
        //   320: aload_0
        //   321: invokevirtual sendFileData : ()V
        //   324: goto -> 73
        //   327: new java/lang/StringBuilder
        //   330: astore_1
        //   331: aload_1
        //   332: invokespecial <init> : ()V
        //   335: ldc 'BTMfiSessionManager'
        //   337: aload_1
        //   338: ldc_w 'total packets should be sent: '
        //   341: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   344: aload_2
        //   345: arraylength
        //   346: sipush #25600
        //   349: idiv
        //   350: iconst_1
        //   351: iadd
        //   352: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   355: invokevirtual toString : ()Ljava/lang/String;
        //   358: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   361: pop
        //   362: goto -> 316
        //   365: new java/lang/StringBuilder
        //   368: astore_1
        //   369: aload_1
        //   370: invokespecial <init> : ()V
        //   373: ldc 'BTMfiSessionManager'
        //   375: aload_1
        //   376: ldc_w 'Received data sending request from FILE_CHANNEL, size = '
        //   379: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   382: aload_2
        //   383: arraylength
        //   384: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   387: invokevirtual toString : ()Ljava/lang/String;
        //   390: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   393: pop
        //   394: aload_0
        //   395: aload_2
        //   396: putfield sendingBuff : [B
        //   399: aload_0
        //   400: iconst_0
        //   401: putfield sendingPos : I
        //   404: aload_0
        //   405: iconst_0
        //   406: putfield sendingPacketId : I
        //   409: aload_0
        //   410: iconst_0
        //   411: putfield packetNo : I
        //   414: aload_2
        //   415: arraylength
        //   416: sipush #25600
        //   419: irem
        //   420: ifne -> 467
        //   423: new java/lang/StringBuilder
        //   426: astore_1
        //   427: aload_1
        //   428: invokespecial <init> : ()V
        //   431: ldc 'BTMfiSessionManager'
        //   433: aload_1
        //   434: ldc_w 'total packets should be sent: '
        //   437: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   440: aload_2
        //   441: arraylength
        //   442: sipush #25600
        //   445: idiv
        //   446: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   449: invokevirtual toString : ()Ljava/lang/String;
        //   452: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   455: pop
        //   456: iconst_0
        //   457: putstatic com/reconinstruments/mobilesdk/btconnectivity/BTConnector.fileTransferDone : Z
        //   460: aload_0
        //   461: invokevirtual sendFileData : ()V
        //   464: goto -> 73
        //   467: new java/lang/StringBuilder
        //   470: astore_1
        //   471: aload_1
        //   472: invokespecial <init> : ()V
        //   475: ldc 'BTMfiSessionManager'
        //   477: aload_1
        //   478: ldc_w 'total packets should be sent: '
        //   481: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   484: aload_2
        //   485: arraylength
        //   486: sipush #25600
        //   489: idiv
        //   490: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   493: iconst_1
        //   494: invokevirtual append : (I)Ljava/lang/StringBuilder;
        //   497: invokevirtual toString : ()Ljava/lang/String;
        //   500: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   503: pop
        //   504: goto -> 456
        //   507: ldc 'BTMfiSessionManager'
        //   509: ldc_w 'There is no any channel to send session data'
        //   512: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
        //   515: pop
        //   516: goto -> 73
        //   519: ldc 'BTMfiSessionManager'
        //   521: ldc_w 'Skip to send session data to iOS since the message is null'
        //   524: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
        //   527: pop
        //   528: goto -> 73
        // Exception table:
        //   from	to	target	type
        //   6	73	93	finally
        //   77	90	93	finally
        //   98	107	93	finally
        //   110	177	93	finally
        //   180	193	93	finally
        //   196	205	93	finally
        //   208	316	93	finally
        //   316	324	93	finally
        //   327	362	93	finally
        //   365	456	93	finally
        //   456	464	93	finally
        //   467	504	93	finally
        //   507	516	93	finally
        //   519	528	93	finally
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
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: new com/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback
        //   5: astore_1
        //   6: aload_1
        //   7: aload_0
        //   8: getfield mContext : Landroid/content/Context;
        //   11: aload_0
        //   12: invokespecial <init> : (Landroid/content/Context;Lcom/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager;)V
        //   15: aload_0
        //   16: aload_1
        //   17: putfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   20: new com/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback
        //   23: astore_1
        //   24: aload_1
        //   25: aload_0
        //   26: getfield mContext : Landroid/content/Context;
        //   29: aload_0
        //   30: invokespecial <init> : (Landroid/content/Context;Lcom/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager;)V
        //   33: aload_0
        //   34: aload_1
        //   35: putfield serverEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiServerEventCallback;
        //   38: new com/stonestreetone/bluetopiapm/SPPM$SerialPortClientManager
        //   41: astore_1
        //   42: aload_1
        //   43: aload_0
        //   44: getfield clientEventCallback : Lcom/reconinstruments/mobilesdk/btmfi/BTMfiClientEventCallback;
        //   47: invokespecial <init> : (Lcom/stonestreetone/bluetopiapm/SPPM$SerialPortClientManager$ClientEventCallback;)V
        //   50: aload_0
        //   51: aload_1
        //   52: putfield serialPortClientManager : Lcom/stonestreetone/bluetopiapm/SPPM$SerialPortClientManager;
        //   55: aload_0
        //   56: iconst_5
        //   57: aconst_null
        //   58: aconst_null
        //   59: invokevirtual openServer : (I[Ljava/util/UUID;Ljava/lang/String;)V
        //   62: aload_0
        //   63: invokevirtual configureMFiSettings : ()V
        //   66: getstatic com/reconinstruments/mobilesdk/btmfi/BTMfiSessionManager.testingPurpose : Z
        //   69: ifeq -> 87
        //   72: ldc 'BTMfiSessionManager'
        //   74: ldc_w 'Connect to QA iPhone for testing purpose'
        //   77: invokestatic i : (Ljava/lang/String;Ljava/lang/String;)I
        //   80: pop
        //   81: aload_0
        //   82: iconst_1
        //   83: iconst_0
        //   84: invokevirtual connectRemoteDevice : (IZ)V
        //   87: aload_0
        //   88: monitorexit
        //   89: return
        //   90: astore_1
        //   91: aload_1
        //   92: invokevirtual printStackTrace : ()V
        //   95: ldc 'BTMfiSessionManager'
        //   97: ldc_w 'ERROR: Could not connect to the BluetopiaPM service.'
        //   100: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
        //   103: pop
        //   104: goto -> 55
        //   107: astore_1
        //   108: aload_0
        //   109: monitorexit
        //   110: aload_1
        //   111: athrow
        // Exception table:
        //   from	to	target	type
        //   2	38	107	finally
        //   38	55	90	java/lang/Exception
        //   38	55	107	finally
        //   55	87	107	finally
        //   91	104	107	finally
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
