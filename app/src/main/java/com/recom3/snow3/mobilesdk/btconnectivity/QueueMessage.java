package com.recom3.snow3.mobilesdk.btconnectivity;

import android.util.Log;

import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Recom3 on 25/01/2022.
 */

public class QueueMessage extends HUDConnectivityMessage
{
    private IBTConnectorCallBack btConnectorCallBack;

    public QueueMessage(HUDConnectivityMessage paramHUDConnectivityMessage) {
        this.requestKey = paramHUDConnectivityMessage.getRequestKey();
        this.sender = paramHUDConnectivityMessage.getSender();
        this.intentFilter = paramHUDConnectivityMessage.getIntentFilter();
        this.info = paramHUDConnectivityMessage.getInfo();
        this.data = paramHUDConnectivityMessage.getData();
    }

    public QueueMessage(HUDConnectivityMessage paramHUDConnectivityMessage, HUDConnectivityService.Channel paramChannel) {
        this.requestKey = paramHUDConnectivityMessage.getRequestKey();
        this.sender = paramHUDConnectivityMessage.getSender();
        this.intentFilter = paramHUDConnectivityMessage.getIntentFilter();
        this.info = paramHUDConnectivityMessage.getInfo();
        if (paramChannel.equals(HUDConnectivityService.Channel.FILE_CHANNEL)) {
            //this.data = compress(getFileFromPointer(paramHUDConnectivityMessage.getData()));
            this.data = compress(paramHUDConnectivityMessage.getData());
        } else {
            this.data = paramHUDConnectivityMessage.getData();
        }
        this.channel = paramChannel;
    }

    private byte[] compress(byte[] paramArrayOfbyte) {
        Log.i(TAG, "compressing file to be sent");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] paramArrayOfbyteResult;
        try {
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(paramArrayOfbyte);
            gZIPOutputStream.finish();
            byteArrayOutputStream.close();
            byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
            gZIPOutputStream.close();
            Log.i(TAG, "binary length: " + paramArrayOfbyte.length);
            Log.i(TAG, "gzipped length: " + arrayOfByte.length);
            paramArrayOfbyteResult = arrayOfByte;
        } catch (IOException iOException) {
            Log.i(TAG, "error gzipping data stream", iOException);
            paramArrayOfbyteResult = null;
        }
        return paramArrayOfbyteResult;
    }

    private byte[] getFileFromPointer(byte[] paramArrayOfbyte) {
        if (paramArrayOfbyte != null) {
            String str = new String(paramArrayOfbyte);
            paramArrayOfbyte = null;
            File file = new File(str);
            if (file.exists()) {
                try {
                    byte[] arrayOfByte = readFile(file);
                    paramArrayOfbyte = arrayOfByte;
                } catch (IOException iOException) {
                    Log.w(TAG, "ioexception: " + iOException);
                    iOException.printStackTrace();
                }
                return paramArrayOfbyte;
            }
            Log.e(TAG, "Passed pointer to file that does not exist!");
            return paramArrayOfbyte;
        }
        return null;
    }

    private byte[] readFile(File paramFile) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(paramFile, "r");
        int i;
        try {
            long l = randomAccessFile.length();
            i = (int)l;
        } finally {
            randomAccessFile.close();
        }
        byte[] arrayOfByte = new byte[i];
        randomAccessFile.readFully(arrayOfByte);
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        Log.i(str, stringBuilder.append("raw file: ").append(arrayOfByte.length).toString());
        randomAccessFile.close();
        return arrayOfByte;
    }

    public IBTConnectorCallBack getHUDConnectivityCallBack() {
        return this.btConnectorCallBack;
    }

    public void setHUDConnectivityCallBack(IBTConnectorCallBack paramIBTConnectorCallBack) {
        this.btConnectorCallBack = paramIBTConnectorCallBack;
    }

    public HUDConnectivityMessage toHUDConnectivityMessage() {
        HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
        hUDConnectivityMessage.setIntentFilter(this.intentFilter);
        hUDConnectivityMessage.setRequestKey(this.requestKey);
        hUDConnectivityMessage.setSender(this.sender);
        hUDConnectivityMessage.setInfo(this.info);
        hUDConnectivityMessage.setData(this.data);
        return hUDConnectivityMessage;
    }

    public String toString() {
        return "QueueMessage [sender=" + this.sender + ", intentFilter=" + this.intentFilter + "]";
    }

}
