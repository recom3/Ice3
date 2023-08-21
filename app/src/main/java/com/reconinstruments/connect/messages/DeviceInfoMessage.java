package com.reconinstruments.connect.messages;

import com.recom3.connect.util.XMLUtils;
import com.recom3.snow3.mobilesdk.messages.XMLMessage;
import com.reconinstruments.connect.apps.DeviceInfo;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Chus on 20/08/2023.
 */

public class DeviceInfoMessage {
    private static final String DEVICE_ATTR_APP = "application";
    private static final String DEVICE_ATTR_SERIAL = "serialNumber";
    private static final String DEVICE_ATTR_VCODE = "versionCode";
    private static final String DEVICE_NODE_NAME = "device";
    static final String TAG = "DeviceInfoMessage";
    public static String intent = XMLMessage.BT_CONNECT_MESSAGE;

    public static String getNodeValue(NamedNodeMap map, String attr) {
        Node node = map.getNamedItem(attr);
        return node != null ? node.getNodeValue() : "";
    }

    public static DeviceInfo parse(String message) {
        NamedNodeMap map = XMLUtils.parseSimpleMessageNodeMap(message);
        String application = getNodeValue(map, DEVICE_ATTR_APP);
        String versionCode = getNodeValue(map, "versionCode");
        String serialNumber = getNodeValue(map, "serialNumber");
        int version = Integer.parseInt(versionCode);
        if (serialNumber.length() == 0 || application.equals("com.reconinstruments.hqmobile")) {
            DeviceInfo deviceInfo = new DeviceInfo(DeviceInfo.DeviceType.ANDROID, null, Integer.valueOf(version), null);
            return deviceInfo;
        }
        DeviceInfo deviceInfo2 = new DeviceInfo(DeviceInfo.DeviceType.MODLIVE, null, Integer.valueOf(version), serialNumber);
        return deviceInfo2;
    }

    public static String compose(String... attr) {
        BasicNameValuePair[] pairs = new BasicNameValuePair[attr.length];
        pairs[0] = new BasicNameValuePair(DEVICE_ATTR_APP, attr[0]);
        pairs[1] = new BasicNameValuePair("versionCode", attr[1]);
        if (attr.length == 3) {
            pairs[2] = new BasicNameValuePair("versionCode", attr[2]);
        }
        String message = XMLUtils.composeSimpleMessage(intent, DEVICE_NODE_NAME, pairs);
        return message;
    }
}
