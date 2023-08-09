package com.recom3.connect.messages;

import android.location.Location;
import android.util.Log;

import com.recom3.connect.util.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Recom3 on 06/07/2022.
 */

public class BuddyInfoMessage {
    static final String FRIEND_ATTR_ID = "id";

    static final String FRIEND_ATTR_LAT = "lat";

    static final String FRIEND_ATTR_LNG = "lng";

    static final String FRIEND_ATTR_LOCATION_TIME = "loc_time";

    static final String FRIEND_ATTR_NAME = "name";

    static final String FRIEND_NODE_NAME = "friend";

    static final String TAG = "BuddyInfoMessage";

    static String intent = "RECON_FRIENDS_LOCATION_UPDATE";

    public String compose(ArrayList<BuddyInfo> paramArrayList) {
        String str2 = XMLUtils.composeHead(intent);
        Iterator<BuddyInfo> iterator = paramArrayList.iterator();
        String str1;
        for (str1 = str2; iterator.hasNext(); ) {
            BuddyInfo buddyInfo = iterator.next();
            str1 = str1 + String.format("<%s %s=\"%d\" %s=\"%s\" %s=\"%f\" %s=\"%f\" %s=\"%d\" />\n", new Object[] {
                    "friend", "id", Integer.valueOf(buddyInfo.localId), "name", buddyInfo.name, "lat", Double.valueOf(buddyInfo.location.getLatitude()), "lng", Double.valueOf(buddyInfo.location.getLongitude()), "loc_time",
                    Long.valueOf(buddyInfo.location.getTime()) });
        }
        return XMLUtils.appendEnding(str1);
    }

    public List<BuddyInfo> parse(String paramString) {
        Document document = XMLUtils.validate(intent, paramString);
        if (document == null)
            return null;
        Node node = document.getElementsByTagName("recon").item(0);
        try {
            Node node1 = node.getFirstChild();
            ArrayList<BuddyInfo> arrayList = new ArrayList();
            while (true) {
                String str;
                ArrayList<BuddyInfo> arrayList1 = arrayList;
                if (node1 != null) {
                    if (node1.getNodeType() == 1 && node1.getNodeName().compareToIgnoreCase("friend") == 0) {
                        NamedNodeMap namedNodeMap = node1.getAttributes();
                        int i = Integer.parseInt(namedNodeMap.getNamedItem("id").getNodeValue());
                        str = namedNodeMap.getNamedItem("name").getNodeValue();
                        double d1 = Double.parseDouble(namedNodeMap.getNamedItem("lat").getNodeValue());
                        double d2 = Double.parseDouble(namedNodeMap.getNamedItem("lng").getNodeValue());
                        long l = Long.parseLong(namedNodeMap.getNamedItem("loc_time").getNodeValue());
                        BuddyInfo buddyInfo = new BuddyInfo(i, str, d1, d2, l);
                        arrayList.add(buddyInfo);
                    }
                    node1 = node1.getNextSibling();
                    continue;
                }
                return (List<BuddyInfo>)arrayList1;
            }
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
            Log.e("BuddyInfoMessage", numberFormatException.getMessage());
            ArrayList arrayList = new ArrayList();
        } catch (NullPointerException nullPointerException) {
            nullPointerException.printStackTrace();
            Log.e("BuddyInfoMessage", nullPointerException.getMessage());
        }
        return (List<BuddyInfo>)null;
    }

    public static class BuddyInfo {
        public String email;

        public int localId;

        public Location location;

        public String name;

        public BuddyInfo(int param1Int, String param1String, double param1Double1, double param1Double2, long param1Long) {
            this.localId = param1Int;
            this.name = param1String;
            this.location = new Location("");
            this.location.setLatitude(param1Double1);
            this.location.setLongitude(param1Double2);
            this.location.setTime(param1Long);
        }
    }
}

