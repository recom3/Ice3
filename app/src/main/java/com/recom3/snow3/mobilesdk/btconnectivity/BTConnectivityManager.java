package com.recom3.snow3.mobilesdk.btconnectivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.btttransport.BTTransportManager;
import com.recom3.snow3.mobilesdk.hudconnectivity.Constants;

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
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPInputStream;

/**
 * Created by Recom3 on 25/01/2022.
 * Main class for bluetooth communication
 * This class is not holding the communication threads themselves.
 * The threads are in BTransportManager
 */

public class BTConnectivityManager {
    public static final String DEVICE_NAME = "device_name";

    public static final int MESSAGE_DEVICE_NAME = 4;

    public static final int MESSAGE_READ = 2;

    public static final int MESSAGE_READ_FILE = 7;

    public static final int MESSAGE_RESET = 6;

    public static final int MESSAGE_STATE_CHANGE = 1;

    public static final int MESSAGE_TOAST = 5;

    public static final int MESSAGE_WRITE = 3;

    private static final String TAG = "BTConnectivityManager";

    public static final String TOAST = "toast";

    static BlockingQueue<QueueMessage> mCommandQueue = new ArrayBlockingQueue<QueueMessage>(1024);

    static BlockingQueue<QueueMessage> mFileQueue = new ArrayBlockingQueue<QueueMessage>(1024);

    static BlockingQueue<QueueMessage> mObjectQueue = new ArrayBlockingQueue<QueueMessage>(1024);

    private int dataReceived = 0;

    private BTTransportManager mBTTransportManager = null;

    private BTConnector mBtCommandConnector = null;

    private BTConnector mBtFileConnector;

    private BTConnector mBtObjectConnector;

    private Context mContext;

    private final Handler mTransportHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message param1Message) {
            //Log.i("BTConnectivityManager", "Received message from HUD");
            BTTransportManager.ConnectionState connectionState;
            switch (param1Message.what) {
                default:
                    return;
                case 1:
                    connectionState = (BTTransportManager.ConnectionState)param1Message.obj;
                case 5:
                    if (Constants.showToast)
                        Toast.makeText(BTConnectivityManager.this.mContext.getApplicationContext(),
                                //(String)((Message)connectionState).obj,
                                (String)param1Message.obj,
                                Toast.LENGTH_LONG).show();
                    //Object and command
                case 2: {
                    String str="";
                    StringBuilder sb = new StringBuilder();

                    if(param1Message!=null && param1Message.obj!=null) {
                        //str = param1Message.obj.toString();
                        try {
                            if (param1Message.obj.getClass() == byte[].class) {
                                byte[] byteArr = (byte[]) param1Message.obj;
                                str = new String(byteArr);
                            }
                            else
                            {
                                str = param1Message.obj.toString();
                            }
                        }
                        catch (Exception ex)
                        {

                        }
                    }
                    sb.append("Received message from HUD=");
                    sb.append(str);
                    Log.i("BTConnectivityManager", sb.toString());
                    //java.lang.ClassCastException: java.lang.String cannot be cast to byte[]

                    //Problem with Exception?
/*
                    (new BTConnectivityManager.ReceiveDataTask()).execute(//(Object[])
                            new BTConnectivityManager.ReceiveDataParams[]{
                                    new BTConnectivityManager.ReceiveDataParams((byte[]) (
                                            //(Message)connectionState).obj,
                                            param1Message.obj),
                                            //((Message)connectionState).arg1,
                                            param1Message.arg1,
                                            false)});
*/
                    if (param1Message.obj.getClass() == byte[].class) {
                        BTConnectivityManager.this.receiveData2((byte[]) param1Message.obj, param1Message.arg1, false);
                    }
                    }
                    //File
                case 7:
                    break;
            }
            /*
            BTConnectivityManager.ReceiveDataParams receiveDataParams = new BTConnectivityManager.ReceiveDataParams((byte[])(
                    //(Message)connectionState).obj,
                    param1Message.obj),
                    //((Message)connectionState).arg1,
                    param1Message.arg1,
                    true);

            BTConnectivityManager.ReceiveDataParams[] arrReceiveDataParams =
                    new BTConnectivityManager.ReceiveDataParams[] {receiveDataParams};

            (new BTConnectivityManager.ReceiveDataTask()).execute(arrReceiveDataParams);
            */

            /*
            (new BTConnectivityManager.ReceiveDataTask()).execute(//(Object[])
                    new BTConnectivityManager.ReceiveDataParams[] {
                    new BTConnectivityManager.ReceiveDataParams((byte[])(
                            //(Message)connectionState).obj,
                            param1Message.obj),
                            //((Message)connectionState).arg1,
                            param1Message.arg1,
                            true) });
            */
        }
    };

    private ByteArrayOutputStream osBuffer;

    private boolean receiving = false;

    private ByteBuffer receivingBuff = ByteBuffer.allocate(0);

    private int totalReceived = 0;

    static {
        mFileQueue = new ArrayBlockingQueue<QueueMessage>(1024);
    }

    public BTConnectivityManager(Context paramContext) {
        this.mContext = paramContext;
        start();
    }

    private int byteArrayToInt(byte[] paramArrayOfbyte) {
        int i = ByteBuffer.wrap((byte[])paramArrayOfbyte.clone(), 0, 4).getInt();
        if (i >= 50331648)
            i = 0;
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

    public static String md5(byte[] paramArrayOfbyte, int paramInt) {
        String str;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(paramArrayOfbyte, paramInt, paramArrayOfbyte.length - paramInt);
            byte[] arrayOfByte = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (paramInt = 0; paramInt < arrayOfByte.length; paramInt++) {
                stringBuffer.append(String.format("%02x", new Object[] { Integer.valueOf(arrayOfByte[paramInt] & 0xFF) }));
            }
            str = stringBuffer.toString();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
            str = "";
        }
        return str;
    }

    private String performChecksum(byte[] paramArrayOfbyte) {
        return md5(paramArrayOfbyte, 0);
    }

    /*
    From: HUDService(Glasses software)>BTConnectivityManager>sendHUDConnectivityMessage(byte[] paramArrayOfbyte, boolean paramBoolean)
    Pasted here as reference
     */
    private void sendHUDConnectivityMessage_HUDService(byte[] readBuf, boolean file) {
        this.totalReceived = 0;
        this.dataReceived = 0;
        this.receivingBuff = ByteBuffer.allocate(0);
        Log.i(TAG, "Stop receiving data, constructing the HUDConnectivityMessage");
        HUDConnectivityMessage cMsg = new HUDConnectivityMessage(readBuf);
        if (cMsg != null && cMsg.getIntentFilter() != null) {
            Log.i(TAG, "HUDConnectivityMessage md5 = " + md5(cMsg.getData(), 0));
            if (Constants.showToast) {
                Toast.makeText(this.mContext.getApplicationContext(), "Received the message " + cMsg.toString(), 1).show();
            }
            Intent i = new Intent(cMsg.getIntentFilter());
            if (file) {
                Log.i(TAG, "Changing file for a pointer in HUD message");
                String filePath = writeFile(uncompress(cMsg.getData()));
                cMsg.setData(filePath.getBytes());
            }
            i.putExtra("message", cMsg.toByteArray());
            this.mContext.sendBroadcast(i);
            Log.i(TAG, "Sent out the broadcast to " + cMsg.getIntentFilter());
            if (Constants.showToast) {
                Toast.makeText(this.mContext.getApplicationContext(), "Sent out the broadcast to " + cMsg.getIntentFilter(), 1).show();
            }
        } else if (cMsg != null) {
            Log.i(TAG, "Received the message " + cMsg.toString());
        } else {
            Log.w(TAG, "Can't construct a HUDConnectivityMessage");
        }
    }

    /**
     * This is similar to HUDService(Glasses software)>BTConnectivityManager>sendHUDConnectivityMessage(byte[] paramArrayOfbyte, boolean paramBoolean)
     * (code pasted above)
     * @param paramArrayOfbyte
     * @param paramInt              len?
     * @param paramBoolean          is file data?
     */
    private void receiveData(byte[] paramArrayOfbyte, int paramInt, boolean paramBoolean) {

        int remaining = receivingBuff.remaining();//#4=remaining

        if (remaining == 0) {
            //If we have at least 20 bytes return
            if (paramInt < 20) {

                return;
            }

            totalReceived = byteArrayToInt(paramArrayOfbyte);

            if (totalReceived <= 0) {
                return;
            }

            receivingBuff = ByteBuffer.allocate(totalReceived);
            remaining = receivingBuff.remaining();// #5=remaining

            StringBuilder sb = new StringBuilder();
            sb.append("HUDConnectivityMessage data block, total size = ");
            sb.append(this.totalReceived);
            Log.i("BTConnectivityManager", sb.toString());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Start receiving new HUDConnectivityMessage data block, total size = ");
        sb.append(this.totalReceived);
        Log.i("BTConnectivityManager", sb.toString());

        //   98: iload #5
        //   100: iload_2
        //   101: if_icmpge -> 210

        if (remaining >= paramInt) {

            this.receivingBuff.put(paramArrayOfbyte, 0, paramInt);

            this.dataReceived += paramInt;
        } else {

            this.receivingBuff.put(paramArrayOfbyte, 0, paramInt);

            dataReceived += remaining;
        }

        //Warning
        sb = new StringBuilder();

        sb.append("dataReceived= ");
        sb.append(this.dataReceived);
        Log.i("BTConnectivityManager", sb.toString());


        if(this.receivingBuff.remaining()!=0)
        {
            return;
        }

        //If boolean true is file data
        sendHUDConnectivityMessage(this.receivingBuff.array(), paramBoolean);

        //   179: aload_0
        //   180: iconst_0
        //   181: putfield receiving : Z

        this.receiving = false;

        //   184: aload_0
        //   185: iconst_0
        //   186: putfield totalReceived : I

        this.totalReceived = 0;

        //   189: aload_0
        //   190: iconst_0
        //   191: putfield dataReceived : I

        this.dataReceived = 0;

        //   194: aload_0
        //   195: iconst_0
        //   196: invokestatic allocate : (I)Ljava/nio/ByteBuffer;
        //   199: putfield receivingBuff : Ljava/nio/ByteBuffer;

        this.receivingBuff = ByteBuffer.allocate(0);

        //   202: goto -> 26

        return;

    }

    private void resetBuffer() {
        Log.i("BTConnectivityManager", "Reset the receiving data buffer");
        this.receiving = false;
        this.totalReceived = 0;
        this.dataReceived = 0;
        this.receivingBuff = ByteBuffer.allocate(0);
    }

    private void sendHUDConnectivityMessage(byte[] paramArrayOfbyte, boolean paramBoolean) {

        this.totalReceived = 0;
        this.dataReceived = 0;
        this.receivingBuff = ByteBuffer.allocate(0);
        Log.i("BTConnectivityManager", "Stop receiving data, constructing the HUDConnectivityMessage");

        HUDConnectivityMessage hudConnectivityMessage = new HUDConnectivityMessage(paramArrayOfbyte);

        if(hudConnectivityMessage!=null) {

            String intentFilter = hudConnectivityMessage.getIntentFilter();

            if(intentFilter!=null) {

                Log.i("BTConnectivityManager", "Intent filter=" + intentFilter);

                StringBuilder sb = new StringBuilder();
                sb.append("HUDConnectivityMessage md5 = ");
                sb.append(md5(hudConnectivityMessage.getData(), 0));
                Log.i("BTConnectivityManager", sb.toString());

                if(false) {
                    //Show toast

                    Toast.makeText(this.mContext.getApplicationContext(), "Received the message " + hudConnectivityMessage.toString(), 1).show();
                }

                Intent intent = new Intent(hudConnectivityMessage.getIntentFilter());

                if(paramBoolean) {

                    Log.i("BTConnectivityManager", "Changing file for a pointer in HUD message");
                    String pointer = writeFile(uncompress(hudConnectivityMessage.getData()));
                    hudConnectivityMessage.setData(pointer.getBytes());
                }
                intent.putExtra("message", hudConnectivityMessage.toByteArray());

                mContext.sendBroadcast(intent);

                sb = new StringBuilder();

                sb.append("Sent out the broadcast to ");

                sb.append(hudConnectivityMessage.getIntentFilter());

                Log.i("BTConnectivityManager", sb.toString());

                if (Constants.showToast) {
                    Toast.makeText(this.mContext.getApplicationContext(), "Sent out the broadcast to " + hudConnectivityMessage.getIntentFilter(), 1).show();
                }

            return;
        }}

        else if(hudConnectivityMessage!=null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Received the message ");
            sb.append(hudConnectivityMessage.toString());
            Log.i("BTConnectivityManager", sb.toString());
        }
        else {
            Log.i("BTConnectivityManager", "Can't construct a HUDConnectivityMessage");
        }

    }

    private void setupCallBack(final QueueMessage qMsg) {
        qMsg.setHUDConnectivityCallBack(new IBTConnectorCallBack() {
            public void onCompleted(boolean param1Boolean) {
                Intent intent = new Intent(qMsg.getSender());
                intent.putExtra("result", param1Boolean);

                if (qMsg.getChannel().equals(HUDConnectivityService.Channel.FILE_CHANNEL))
                    qMsg.setData(new byte[0]);

                intent.putExtra(HUDConnectivityMessage.TAG, (Parcelable)qMsg.toHUDConnectivityMessage());
                BTConnectivityManager.this.mContext.sendBroadcast(intent);
                Log.i("BTConnectivityManager", "HUDConnectivityCallBack broadcast " + qMsg.toHUDConnectivityMessage().toString() + " with result " + param1Boolean);
            }
        });
    }

    private byte[] uncompress(byte[] paramArrayOfbyte) {
        GZIPInputStream gZIPInputStream3 = null;
        Log.i("BTConnectivityManager", "uncompressing file byte array");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPInputStream gZIPInputStream1 = null;
        GZIPInputStream gZIPInputStream2 = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
            try {
                gZIPInputStream2 = new GZIPInputStream(byteArrayInputStream);
                int i = 0;
                while (i != -1) {
                    try {
                        int j = gZIPInputStream2.read();
                        i = j;
                        if (j != -1) {
                            byteArrayOutputStream.write(j);
                            i = j;
                        }
                    } catch (IOException ex) {
                        gZIPInputStream1 = gZIPInputStream2;
                        continue;
                    }
                }
                byteArrayInputStream.close();
                gZIPInputStream2.close();
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
                return byteArrayOutputStream.toByteArray();
            } catch (IOException ex) {}
        } catch (Exception iOException) {
            gZIPInputStream3 = gZIPInputStream2;
            Log.w("BTConnectivityManager", "ioexception caught when parsing zipped array");
            Log.e("BTConnectivityManager", "exception:" + iOException);
        }
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

    public void connect(String paramString, int paramInt) {
        this.mBTTransportManager.connect(paramString, paramInt);
    }

    public void disConnect() {
        if (this.mBTTransportManager != null)
            this.mBTTransportManager.stop(false);
    }

    public void disconnectThreads() {
        if (this.mBTTransportManager != null)
            this.mBTTransportManager.stop(false);
    }

    public BTTransportManager getBTTransportManager() {
        return this.mBTTransportManager;
    }

    public int getConnectionState() {
        return this.mBTTransportManager.getConnectionState();
    }

    public void push(HUDConnectivityMessage paramHUDConnectivityMessage, HUDConnectivityService.Channel paramChannel) {
        QueueMessage queueMessage = new QueueMessage(paramHUDConnectivityMessage, paramChannel);
        setupCallBack(queueMessage);
        try {
            StringBuilder stringBuilder1;
            switch (paramChannel) {
                default:
                    return;
                case COMMAND_CHANNEL:
                    stringBuilder1 = new StringBuilder();
                    Log.i("BTConnectivityManager", stringBuilder1.append("Putting the message ").append(paramHUDConnectivityMessage.toString()).append(" into the command queue").toString());
                    if (Constants.showToast) {
                        Context context = this.mContext.getApplicationContext();
                        StringBuilder stringBuilder2 = new StringBuilder();
                        Toast.makeText(context, stringBuilder2.append("Putting the message ").append(paramHUDConnectivityMessage.toString()).append(" into the command queue").toString(), Toast.LENGTH_LONG).show();
                    }
                    mCommandQueue.put(queueMessage);
                    break;
                case OBJECT_CHANNEL:
                    stringBuilder1 = new StringBuilder();
                    Log.i("BTConnectivityManager", stringBuilder1.append("Putting the message ").append(paramHUDConnectivityMessage.toString()).append(" into the object queue").toString());
                    if (Constants.showToast) {
                        Context context = this.mContext.getApplicationContext();
                        StringBuilder stringBuilder2 = new StringBuilder();
                        Toast.makeText(context, stringBuilder2.append("Putting the message ").append(paramHUDConnectivityMessage.toString()).append(" into the object queue").toString(), Toast.LENGTH_LONG).show();
                    }
                    mObjectQueue.put(queueMessage);
                    break;
                case FILE_CHANNEL:
                    stringBuilder1 = new StringBuilder();
                    Log.i("BTConnectivityManager", stringBuilder1.append("Putting the message ").append(paramHUDConnectivityMessage.toString()).append(" into the file queue").toString());
                    mFileQueue.put(queueMessage);
                    break;
            }
        } catch (InterruptedException interruptedException) {
            Log.w("BTConnectivityManager", "Stop putting the message from the queue...");
            interruptedException.printStackTrace();
            //}
            StringBuilder stringBuilder = new StringBuilder();
            Log.i("BTConnectivityManager", stringBuilder.append("Putting the message ").append(interruptedException.toString()).append(" into the file queue").toString());
            if (Constants.showToast) {
                Context context = this.mContext.getApplicationContext();
                StringBuilder stringBuilder1 = new StringBuilder();
                Toast.makeText(context, stringBuilder1.append("Putting the message ").append(interruptedException.toString()).append(" into the file queue").toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Starts all the threads is called from constructor
     * Triggers accept thread
     */
    public void start() {

        Log.i("BTConnectivityManager", "'Starting Bluetooth transport manager...'");

        mBTTransportManager = new BTTransportManager(mContext, mTransportHandler);

        if(mBTTransportManager!=null)
        {
            //!recom3
            //this.mBTTransportManager.stop(true);
            //this.mBTTransportManager.start(true);
        }

        Log.i(TAG, "Restarting all of the Bluetooth connectors...");

        if(mBtCommandConnector==null)
        {
            mBtCommandConnector = new BTCommandConnector(mContext, mBTTransportManager);
            mBtCommandConnector.start();;
        }
        else
        {
            mBtCommandConnector.cancel();
        }

        if(mBtObjectConnector==null)
        {
            mBtObjectConnector = new BTObjectConnector(mContext, mBTTransportManager);
            mBtObjectConnector.start();
        }
        else
        {
            mBtObjectConnector.cancel();
        }

        if(mBtFileConnector==null)
        {
            mBtFileConnector = new BTFileConnector(mContext, mBTTransportManager);
            mBtFileConnector.start();
        }
        else
        {
            mBtFileConnector.cancel();
        }

        //!recom3
        //this.mBtCommandConnector.start();
        //this.mBtObjectConnector.start();
        //this.mBtFileConnector.start();
    }

    public void stop() {

        Log.i(TAG, "Shutdown Bluetooth TransportManager...");
        if(mBTTransportManager!=null)
        {
            mBTTransportManager.stop(true);
        }
        if(mBtCommandConnector!=null)
        {
            mBtCommandConnector.cancel();
            mBtCommandConnector = null;
        }
        //!recom3
        /*
        if (this.mBtObjectConnector != null) {
            this.mBtObjectConnector.cancel();
            this.mBtObjectConnector = null;
        }
        if (this.mBtFileConnector != null) {
            this.mBtFileConnector.cancel();
            this.mBtFileConnector = null;
        }
        */
    }

    public String writeFile(byte[] paramArrayOfbyte) {
        String str1;
        String str2 = performChecksum(paramArrayOfbyte) + ".tmp";
        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp/");
        if (!file2.mkdirs())
            Log.i("BTConnectivityManager", "Parent directories were not created. Possibly since they already exist.");
        String str3 = file2.getAbsolutePath() + "/" + str2;
        Log.i("BTConnectivityManager", "temporary path: " + str3);
        File file1 = new File(str3);
        if (file1.exists())
            file1.delete();
        File file3 = new File(str3);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file3);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            try {
                bufferedOutputStream.write(paramArrayOfbyte);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                str1 = file3.getAbsolutePath();
            } catch (IOException iOException) {
                Log.w("BTConnectivityManager", "caught exception closing file : " + iOException);
                iOException.printStackTrace();
                str1 = "";
            }
        } catch (FileNotFoundException fileNotFoundException) {
            Log.w("BTConnectivityManager", "caught exception opening buffer: " + fileNotFoundException);
            fileNotFoundException.printStackTrace();
            str1 = "";
        }
        return str1;
    }

    private class ReceiveDataParams {
        boolean isFile;

        int len;

        byte[] readBuf;

        public ReceiveDataParams(byte[] param1ArrayOfbyte, int param1Int, boolean param1Boolean) {
            this.readBuf = param1ArrayOfbyte;
            this.len = param1Int;
            this.isFile = param1Boolean;
        }
    }

    private class ReceiveDataTask extends AsyncTask<ReceiveDataParams, Void, Void> {
        private ReceiveDataTask() {}

        protected Void doInBackground(BTConnectivityManager.ReceiveDataParams... param1VarArgs) {
            BTConnectivityManager.ReceiveDataParams receiveDataParams = param1VarArgs[0];
            BTConnectivityManager.this.receiveData(receiveDataParams.readBuf, receiveDataParams.len, receiveDataParams.isFile);
            return null;
        }
    }

    /**
     * From HUDService of HUD apk
     * @param paramArrayOfbyte
     * @param paramInt
     */
    private void receiveData(byte[] paramArrayOfbyte, int paramInt) {
        int i = this.receivingBuff.remaining();
        int j = i;
        if (i == 0) {
            if (paramInt < 20)
                return;
            this.totalReceived = byteArrayToInt(paramArrayOfbyte);
            if (this.totalReceived > 0) {
                this.receivingBuff = ByteBuffer.allocate(this.totalReceived);
                j = this.receivingBuff.remaining();
                Log.i("BTConnectivityManager", "Start receiving new HUDConnectivityMessage data block, total size = " + this.totalReceived);
            } else {
                return;
            }
        }
        if (j < paramInt) {
            this.receivingBuff.put(paramArrayOfbyte, 0, j);
            this.dataReceived += j;
        } else {
            this.receivingBuff.put(paramArrayOfbyte, 0, paramInt);
            this.dataReceived += paramInt;
        }
        Log.i("BTConnectivityManager", "dataReceived= " + this.dataReceived);
        if (this.receivingBuff.remaining() == 0) {
            sendHUDConnectivityMessage(this.receivingBuff.array(), false);
            this.receiving = false;
            this.totalReceived = 0;
            this.dataReceived = 0;
            this.receivingBuff = ByteBuffer.allocate(0);
        }
    }

    /**
     * From HUDService of HUD apk
     * @param paramArrayOfbyte
     * @param paramInt
     * @param paramBoolean
     */
    private void receiveData2(byte[] paramArrayOfbyte, int paramInt, boolean paramBoolean) {
        int i = this.receivingBuff.remaining();
        int j = i;
        if (i == 0) {
            if (paramInt < 20)
                return;
            this.totalReceived = byteArrayToInt(paramArrayOfbyte);
            if (this.totalReceived > 0) {
                this.receivingBuff = ByteBuffer.allocate(this.totalReceived);
                j = this.receivingBuff.remaining();
                Log.i("BTConnectivityManager", "Start receiving new HUDConnectivityMessage data block, total size = " + this.totalReceived);
            } else {
                return;
            }
        }
        if (j < paramInt) {
            this.receivingBuff.put(paramArrayOfbyte, 0, j);
            this.dataReceived += j;
        } else {
            this.receivingBuff.put(paramArrayOfbyte, 0, paramInt);
            this.dataReceived += paramInt;
        }
        Log.i("BTConnectivityManager", "dataReceived= " + this.dataReceived);
        if (this.receivingBuff.remaining() == 0) {

            sendHUDConnectivityMessage(this.receivingBuff.array(), paramBoolean);

            this.receiving = false;
            this.totalReceived = 0;
            this.dataReceived = 0;
            this.receivingBuff = ByteBuffer.allocate(0);
        }
    }
}
