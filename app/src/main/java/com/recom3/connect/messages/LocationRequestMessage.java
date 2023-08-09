package com.recom3.connect.messages;

import com.recom3.connect.util.XMLUtils;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.NamedNodeMap;

import java.util.Locale;

/**
 * Created by Recom3 on 05/07/2022.
 */

public class LocationRequestMessage {
    static final String TAG = "DeviceInfoMessage";

    static String intent = "RECON_LOCATION_REQUEST";

    public static String compose(LocationRequest paramLocationRequest) {
        boolean bool;
        if (paramLocationRequest.delay != 0) {
            bool = true;
        } else {
            bool = true;
        }
        //BasicNameValuePair[] arrayOfBasicNameValuePair = new BasicNameValuePair[bool];
        BasicNameValuePair[] arrayOfBasicNameValuePair = new BasicNameValuePair[1];
        arrayOfBasicNameValuePair[0] = new BasicNameValuePair("type", paramLocationRequest.action.name());
        if (paramLocationRequest.delay != 0)
            arrayOfBasicNameValuePair[1] = new BasicNameValuePair("delay", Integer.toString(paramLocationRequest.delay));
        return XMLUtils.composeSimpleMessage("RECON_LOCATION_REQUEST", "action", arrayOfBasicNameValuePair);
    }

    public static LocationRequest parse(String paramString) {
        NamedNodeMap namedNodeMap = XMLUtils.parseSimpleMessageNodeMap(paramString);
        LocationCommand locationCommand = LocationCommand.valueOf(namedNodeMap.getNamedItem("type").getNodeValue().toUpperCase(Locale.CANADA));
        int i = 0;
        if (namedNodeMap.getNamedItem("delay") != null)
            i = Integer.parseInt(namedNodeMap.getNamedItem("delay").getNodeValue());
        return new LocationRequest(locationCommand, i);
    }

    public enum LocationCommand {
        DISABLE, DISABLEBUDDIES, ENABLE, ENABLEBUDDIES;

        static {
            //!!!
            //DISABLEBUDDIES = new LocationCommand("DISABLEBUDDIES", 3);
            //$VALUES = new LocationCommand[] { ENABLE, DISABLE, ENABLEBUDDIES, DISABLEBUDDIES };
        }
    }

    public static class LocationRequest {
        public LocationRequestMessage.LocationCommand action;

        public int delay;

        public LocationRequest(LocationRequestMessage.LocationCommand param1LocationCommand) {
            this.action = param1LocationCommand;
            this.delay = 0;
        }

        public LocationRequest(LocationRequestMessage.LocationCommand param1LocationCommand, int param1Int) {
            this.action = param1LocationCommand;
            this.delay = param1Int;
        }
    }
}

