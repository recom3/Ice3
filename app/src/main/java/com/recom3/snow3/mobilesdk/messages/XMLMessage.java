package com.recom3.snow3.mobilesdk.messages;

/**
 * Created by Recom3 on 07/04/2022.
 */

import android.util.Log;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLMessage {
    public static final String BT_CONNECT_MESSAGE = "RECON_BT_CONNECTED";

    public static final String BUDDY_INFO_MESSAGE = "RECON_FRIENDS_LOCATION_UPDATE";

    public static final boolean DUMP_MESSAGE_FOR_DEBUG = false;

    public static final String FACEBOOK_INBOX_MESSAGE = "RECON_FACEBOOK_INBOX";

    public static final String JUMP_DATA_MESSAGE = "RECON_JUMP_DATA";

    public static final String JUMP_SNS_MESSAGE = "RECON_JUMP_SNS";

    public static final String LOCATION_RELAY_MESSAGE = "RECON_LOCATION_RELAY";

    public static final String LOCATION_REQUEST_MESSAGE = "RECON_LOCATION_REQUEST";

    public static final String MUSIC_MESSAGE = "RECON_MUSIC_MESSAGE";

    public static final String PHONE_MESSAGE = "RECON_PHONE_MESSAGE";

    public static final String SONG_MESSAGE = "RECON_SONG_MESSAGE";

    static String TAG = "XMLMessage";

    public static final String TRANSFER_REQUEST_MESSAGE = "RECON_TRANSFER_REQUEST";

    public static final String TRANSFER_RESPONSE_MESSAGE = "RECON_TRANSFER_RESPONSE";

    public static final String WEB_REQUEST_MESSAGE = "RECON_WEB_REQUEST";

    public static final String WEB_RESPONSE_MESSAGE = "RECON_WEB_RESPONSE";

    protected static String appendEnding(String paramString) {
        return String.valueOf(paramString) + "</recon>";
    }

    protected static String composeHead(String paramString) {
        return "<recon intent=\"" + paramString + "\">";
    }

    public static String composeSimpleMessage(String paramString) {
        return "<recon intent=\"" + paramString + "\"></recon>";
    }

    public static String composeSimpleMessage(String paramString1, String paramString2, String paramString3) {
        return appendEnding(String.valueOf(composeHead(paramString1)) + String.format("<%s type=\"%s\"/>", new Object[] { paramString2, paramString3 }));
    }

    public static String composeSimpleMessage(String paramString1, String paramString2, ArrayList<BasicNameValuePair> paramArrayList) {
        paramString1 = String.valueOf(composeHead(paramString1)) + String.format("<%s ", new Object[] { paramString2 });
        Iterator<BasicNameValuePair> iterator = paramArrayList.iterator();
        while (true) {
            if (!iterator.hasNext())
                return appendEnding(String.valueOf(paramString1) + String.format("/>", new Object[0]));
            BasicNameValuePair basicNameValuePair = iterator.next();
            paramString1 = String.valueOf(paramString1) + String.format("%s=\"%s\" ", new Object[] { basicNameValuePair.getName(), basicNameValuePair.getValue() });
        }
    }

    public static String composeSimpleMessage(String paramString1, String paramString2, BasicNameValuePair[] paramArrayOfBasicNameValuePair) {
        paramString1 = String.valueOf(composeHead(paramString1)) + String.format("<%s ", new Object[] { paramString2 });
        int i = paramArrayOfBasicNameValuePair.length;
        for (byte b = 0;; b++) {
            if (b >= i)
                return appendEnding(String.valueOf(paramString1) + String.format("/>", new Object[0]));
            BasicNameValuePair basicNameValuePair = paramArrayOfBasicNameValuePair[b];
            paramString1 = String.valueOf(paramString1) + String.format("%s=\"%s\" ", new Object[] { basicNameValuePair.getName(), basicNameValuePair.getValue() });
        }
    }

    public static String composeSimpleMessageElements(String paramString, BasicNameValuePair[] paramArrayOfBasicNameValuePair) {
        paramString = composeHead(paramString);
        int i = paramArrayOfBasicNameValuePair.length;
        for (byte b = 0;; b++) {
            if (b >= i)
                return appendEnding(paramString);
            BasicNameValuePair basicNameValuePair = paramArrayOfBasicNameValuePair[b];
            paramString = String.valueOf(paramString) + String.format("<%s>%s</%s>", new Object[] { basicNameValuePair.getName(), basicNameValuePair.getValue(), basicNameValuePair.getName() });
        }
    }

    public static String getMessageIntent(String paramString) {
        String str1;
        String str2 = "";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            String str = paramString.replace("&", "+");
            StringReader stringReader = new StringReader(str);
            inputSource.setCharacterStream(stringReader);
            str1 = documentBuilder.parse(inputSource).getElementsByTagName("recon").item(0).getAttributes().getNamedItem("intent").getNodeValue();
        } catch (ParserConfigurationException parserConfigurationException) {
            parserConfigurationException.printStackTrace();
            str1 = str2;
        } catch (SAXException sAXException) {
            sAXException.printStackTrace();
            str1 = str2;
        } catch (IOException iOException) {
            iOException.printStackTrace();
            str1 = str2;
        } catch (DOMException dOMException) {
            dOMException.printStackTrace();
            str1 = str2;
        }
        return str1;
    }

    public static HashMap<String, String> parseSimpleMessageElementsToHashMap(String paramString) {
        HashMap<String, String> hashMap = null;
        try {
            InputSource inputSource = new InputSource();
            StringReader stringReader = new StringReader(paramString);
            inputSource.setCharacterStream(stringReader);
            NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon");
            nodeList.item(0).getChildNodes();
            hashMap = new HashMap<String, String>();
            byte b = 0;
            while (true) {
                if (b < nodeList.item(0).getChildNodes().getLength()) {
                    hashMap.put(nodeList.item(0).getChildNodes().item(b).getNodeName(), nodeList.item(0).getChildNodes().item(b).getFirstChild().getNodeValue());
                    b++;
                    continue;
                }
                return (HashMap)hashMap;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            exception = null;
        }
        return hashMap;
    }

    public static Node parseSimpleMessageNode(String paramString) {
        Node node = null;
        try {
            InputSource inputSource = new InputSource();
            StringReader stringReader = new StringReader(paramString);
            inputSource.setCharacterStream(stringReader);
            node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon").item(0).getFirstChild();
        } catch (Exception exception) {
            exception.printStackTrace();
            exception = null;
        }
        return node;
    }

    public static NamedNodeMap parseSimpleMessageNodeMap(String paramString) {
        NamedNodeMap namedNodeMap = null;
        try {
            InputSource inputSource = new InputSource();
            StringReader stringReader = new StringReader(paramString);
            inputSource.setCharacterStream(stringReader);
            namedNodeMap = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon").item(0).getFirstChild().getAttributes();
        } catch (Exception exception) {
            exception.printStackTrace();
            exception = null;
        }
        return namedNodeMap;
    }

    public static String parseSimpleMessageType(String paramString) {
        String str="";
        try {
            InputSource inputSource = new InputSource();
            StringReader stringReader = new StringReader(paramString);
            inputSource.setCharacterStream(stringReader);
            paramString = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon").item(0).getFirstChild().getAttributes().getNamedItem("type").getNodeValue();
        } catch (Exception exception) {
            exception.printStackTrace();
            str = "";
        }
        return str;
    }

    public static Document validate(String paramString1, String paramString2) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            StringBuilder stringBuilder2=null;
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            StringReader stringReader = new StringReader(paramString2);
            inputSource.setCharacterStream(stringReader);
            document = documentBuilder.parse(inputSource);
            if (document.getElementsByTagName("recon").item(0).getAttributes().getNamedItem("intent").getNodeValue().compareTo(paramString1) != 0) {
                String str = TAG;
                stringBuilder2 = new StringBuilder("The XML protocol's intent should be ");
                Log.e(str, stringBuilder2.append(paramString1).toString());
                return null;
            }
            Log.v(TAG, "Has right intent");
            StringBuilder stringBuilder1 = stringBuilder2;
        } catch (Exception exception) {
            Log.e(TAG, "Failed to parse xml", exception);
            exception = null;
        }
        return document;
    }
}

