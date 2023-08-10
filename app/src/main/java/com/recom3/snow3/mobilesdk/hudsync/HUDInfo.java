package com.recom3.snow3.mobilesdk.hudsync;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Recom3 on 18/03/2022.
 */

public class HUDInfo implements Parcelable {
    public static final Parcelable.Creator<HUDInfo> CREATOR;

    private static final String d = HUDInfo.class.getName();

    public HUDInfo$HudType a = HUDInfo$HudType.a;

    public String b;

    public String c;

    static {
        CREATOR = new Parcelable.Creator<HUDInfo>() {

            @Override
            public HUDInfo createFromParcel(Parcel source) {
                return null;
            }

            @Override
            public HUDInfo[] newArray(int size) {
                return new HUDInfo[0];
            }
        };
    }

    public HUDInfo() {}

    public HUDInfo(Parcel paramParcel) {
        this.a = (HUDInfo$HudType)paramParcel.readSerializable();
        this.b = paramParcel.readString();
        this.c = paramParcel.readString();
    }

    public static String a(String paramString1, String paramString2, String paramString3) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            //this();
            StringReader stringReader = new StringReader(paramString1);
            //this(paramString1);
            inputSource.setCharacterStream(stringReader);
            paramString1 = documentBuilder.parse(inputSource).getElementsByTagName(paramString2).item(0).getTextContent();
            paramString3 = paramString1;
        } catch (Exception exception) {
            Log.e(d, "Failed to parse xml", exception);
        }
        return paramString3;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return this.a.toString() + " " + this.b;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt) {
        paramParcel.writeSerializable(this.a);
        paramParcel.writeString(this.b);
        paramParcel.writeString(this.c);
    }
}

