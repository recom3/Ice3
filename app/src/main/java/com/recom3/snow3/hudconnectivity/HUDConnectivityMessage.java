package com.recom3.snow3.hudconnectivity;

import android.os.Parcel;
import android.os.Parcelable;

import com.recom3.snow3.mobilesdk.HUDConnectivityService;

import java.nio.ByteBuffer;

/**
 * Created by Recom3 on 25/01/2022.
 */

public class HUDConnectivityMessage implements Parcelable {
    public static final Parcelable.Creator<HUDConnectivityMessage> CREATOR;

    public static String TAG = "HUDConnectivityMessage";

    protected HUDConnectivityService.Channel channel = null;

    public byte[] data;//g?

    public String info;//d?

    public String intentFilter;//e?

    protected int requestKey;

    protected String sender;

    static {
        CREATOR = new Parcelable.Creator<HUDConnectivityMessage>() {
            public HUDConnectivityMessage createFromParcel(Parcel param1Parcel) {
                return new HUDConnectivityMessage(param1Parcel);
            }

            public HUDConnectivityMessage[] newArray(int param1Int) {
                return new HUDConnectivityMessage[param1Int];
            }
        };
    }

    public HUDConnectivityMessage() {}

    public HUDConnectivityMessage(int paramInt, String paramString1, String paramString2, String paramString3, byte[] paramArrayOfbyte) {
        this.requestKey = paramInt;
        this.intentFilter = paramString1;
        this.data = paramArrayOfbyte;
        this.info = paramString3;
        this.sender = paramString2;
    }

    private HUDConnectivityMessage(Parcel paramParcel) {
        readFromParcel(paramParcel);
    }

    public HUDConnectivityMessage(byte[] paramArrayOfbyte) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfbyte, 0, paramArrayOfbyte.length);
        if (paramArrayOfbyte != null && paramArrayOfbyte.length >= 20)
            try {
                int i = byteBuffer.getInt();
                byteBuffer.slice();
                if (i == paramArrayOfbyte.length) {
                    i = byteBuffer.getInt();
                    byteBuffer.slice();
                    int j = byteBuffer.getInt();
                    byteBuffer.slice();
                    if (j < 50331648) {
                        byte[] arrayOfByte = new byte[j];
                        byteBuffer.get(arrayOfByte);
                        String str = new String(arrayOfByte);
                        //this(arrayOfByte);
                        byteBuffer.slice();
                        j = byteBuffer.getInt();
                        byteBuffer.slice();
                        if (j < 50331648) {
                            if (j > 0) {
                                arrayOfByte = new byte[j];
                                byteBuffer.get(arrayOfByte);
                                String str1 = new String(arrayOfByte);
                                //this(arrayOfByte);
                                this.intentFilter = str1;
                                byteBuffer.slice();
                            }
                            j = byteBuffer.getInt();
                            byteBuffer.slice();
                            if (j < 50331648) {
                                if (j > 0) {
                                    byte[] arrayOfByte1 = new byte[j];
                                    byteBuffer.get(arrayOfByte1);
                                    String str1 = new String(arrayOfByte);
                                    //this(arrayOfByte1);
                                    byteBuffer.slice();
                                    this.info = str1;
                                } else {
                                    this.info = "";
                                }
                                arrayOfByte = new byte[byteBuffer.remaining()];
                                byteBuffer.get(arrayOfByte);
                                this.requestKey = i;
                                this.sender = str;
                                this.data = arrayOfByte;
                            }
                        }
                    }
                }
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                indexOutOfBoundsException.printStackTrace();
            }
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

    private void readFromParcel(Parcel paramParcel) {
        this.intentFilter = paramParcel.readString();
        this.data = new byte[paramParcel.readInt()];
        paramParcel.readByteArray(this.data);
        this.sender = paramParcel.readString();
        this.requestKey = paramParcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public HUDConnectivityService.Channel getChannel() {
        return this.channel;
    }

    public byte[] getData() {
        return this.data;
    }

    public String getInfo() {
        return this.info;
    }

    public String getIntentFilter() {
        return this.intentFilter;
    }

    public int getRequestKey() {
        return this.requestKey;
    }

    public String getSender() {
        return this.sender;
    }

    public void setData(byte[] paramArrayOfbyte) {
        this.data = paramArrayOfbyte;
    }

    public void setInfo(String paramString) {
        this.info = paramString;
    }

    public void setIntentFilter(String paramString) {
        this.intentFilter = paramString;
    }

    public void setRequestKey(int paramInt) {
        this.requestKey = paramInt;
    }

    public void setSender(String paramString) {
        this.sender = paramString;
    }

    public byte[] toByteArray() {
        if (this.sender == null)
            this.sender = "";
        if (this.info == null)
            this.info = "";
        if (this.data == null)
            this.data = new byte[0];
        if (this.intentFilter != null) {
            int i = this.sender.length() + 20 + this.intentFilter.length() + this.info.length() + this.data.length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(i);
            byteBuffer.putInt(i);
            byteBuffer.putInt(this.requestKey);
            byteBuffer.putInt(this.sender.length());
            if (this.sender.length() > 0)
                byteBuffer.put(this.sender.getBytes());
            byteBuffer.putInt(this.intentFilter.length());
            if (this.intentFilter.length() > 0)
                byteBuffer.put(this.intentFilter.getBytes());
            byteBuffer.putInt(this.info.length());
            if (this.info.length() > 0)
                byteBuffer.put(this.info.getBytes());
            if (this.data.length >= 0)
                byteBuffer.put(this.data);
            return byteBuffer.array();
        }
        return null;
    }

    public String toString() {
        return "HUDConnectivityMessage [intentFilter=" + this.intentFilter + ", sender=" + this.sender + "]";
    }

    public void writeToParcel(Parcel paramParcel, int paramInt) {
        paramParcel.writeString(this.intentFilter);
        if (this.data != null) {
            paramParcel.writeInt(this.data.length);
            paramParcel.writeByteArray(this.data);
        }
        paramParcel.writeString(this.sender);
        paramParcel.writeInt(this.requestKey);
    }
}
