package com.recom3.connect.messages;

import android.location.Location;

import com.recom3.connect.util.XMLUtils;

import org.apache.http.message.BasicNameValuePair;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Recom3 on 05/07/2022.
 */

public class LocationMessage {
    static final String TAG = "DeviceInfoMessage";

    static String intent = "RECON_LOCATION_RELAY";

    public static String compose(Location paramLocation) {
        return XMLUtils.composeSimpleMessageElements(intent, new BasicNameValuePair[] { new BasicNameValuePair("longtitude", "" + paramLocation.getLongitude()), new BasicNameValuePair("lattitude", "" + paramLocation.getLatitude()), new BasicNameValuePair("accuracy", "" + paramLocation.getAccuracy()) });
    }

    public static Location parse(String paramString) {
        HashMap hashMap = XMLUtils.parseSimpleMessageElementsToHashMap(paramString);
        double d1 = Location.convert((String)hashMap.get("lattitude"));
        double d2 = Location.convert((String)hashMap.get("longtitude"));
        Location location = new Location("MOD Live");
        location.setLatitude(d1);
        location.setLongitude(d2);
        if ((String)hashMap.get("accuracy") != null)
            location.setAccuracy(Float.parseFloat((String)hashMap.get("accuracy")));
        location.setTime((new Date()).getTime());
        return location;
    }
}
