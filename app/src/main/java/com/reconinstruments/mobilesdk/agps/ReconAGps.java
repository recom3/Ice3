package com.reconinstruments.mobilesdk.agps;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import org.w3c.dom.Element;
import android.util.Log;
import android.util.Xml;

import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;

import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Recom3 on 18/07/2023.
 */

public class ReconAGps {

    public static class InvalidUpdatePeriodXml extends Exception {
    }

    public static int getUpdatePeriod(HUDConnectivityMessage hUDConnectivityMessage) throws InvalidUpdatePeriodXml {
        return getUpdatePeriod(new String(hUDConnectivityMessage.data));
    }

    private static int getUpdatePeriod(String str) throws InvalidUpdatePeriodXml {
        try {
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(str));
            Element element = (Element) DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon").item(0);
            return Integer.parseInt(
                    element
                    .getAttribute("value")
            );
        } catch (Exception e) {
            Log.i("ReconAGps", "Invalid location xml");
            throw new InvalidUpdatePeriodXml();
        }
    }

    private static String getLocationData(Location location) {
        XmlSerializer newSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        try {
            newSerializer.setOutput(stringWriter);
            newSerializer.startTag("", "recon");
            newSerializer.attribute("", "intent", "com.reconinstruments.mobilesdk.agps.tohud.LOCATION_UPDATE");
            newSerializer.startTag("", "location");
            newSerializer.attribute("", "lat", new StringBuilder().append(location.getLatitude()).toString());
            newSerializer.attribute("", "lng", new StringBuilder().append(location.getLongitude()).toString());
            if (location.hasAltitude()) {
                newSerializer.attribute("", "alt", new StringBuilder().append(location.getAltitude()).toString());
            }
            if (location.hasSpeed()) {
                newSerializer.attribute("", "spd", new StringBuilder().append(location.getSpeed()).toString());
            }
            newSerializer.attribute("", "utc_time", new StringBuilder().append(location.getTime()).toString());
            newSerializer.endTag("", "location");
            newSerializer.endTag("", "recon");
            newSerializer.endDocument();
            return stringWriter.toString();
        } catch (Exception e) {
            Log.e("ReconAGps", "Error serializing status exchange object");
            throw new RuntimeException(e);
        }
    }

    public static void broadcastLocation(Context context, Location location) {
        String a2 = getLocationData(location);
        Intent intent = new Intent("com.reconinstruments.mobilesdk.hudconnectivity.channel.object");
        HUDConnectivityMessage hUDConnectivityMessage = new HUDConnectivityMessage();
        hUDConnectivityMessage.info = "ReconAGps";
        hUDConnectivityMessage.intentFilter = "com.reconinstruments.mobilesdk.agps.tohud.LOCATION_UPDATE";
        hUDConnectivityMessage.data = a2.getBytes();
        intent.putExtra(HUDConnectivityMessage.TAG, hUDConnectivityMessage.toByteArray());
        context.sendBroadcast(intent);
    }
}
