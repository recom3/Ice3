package com.recom3.connect.util;

import android.util.Log;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class XMLUtils {
    static String TAG = "XMLUtils";

    public static String appendEnding(String paramString) {
        return paramString + "</recon>";
    }

    public static String composeHead(String paramString) {
        return "<recon intent=\"" + paramString + "\">";
    }

    public static String composeSimpleMessage(String paramString) {
        return "<recon intent=\"" + paramString + "\"></recon>";
    }

    public static String composeSimpleMessage(String paramString1, String paramString2, String paramString3) {
        paramString1 = composeHead(paramString1);
        return appendEnding(paramString1 + String.format("<%s type=\"%s\"/>", new Object[] { paramString2, paramString3 }));
    }

    public static String composeSimpleMessage(String paramString1, String paramString2, ArrayList<BasicNameValuePair> paramArrayList) {
        paramString1 = composeHead(paramString1);
        paramString1 = paramString1 + String.format("<%s ", new Object[] { paramString2 });
        for (BasicNameValuePair basicNameValuePair : paramArrayList) {
            paramString1 = paramString1 + String.format("%s=\"%s\" ", new Object[] { basicNameValuePair.getName(), basicNameValuePair.getValue() });
        }
        return appendEnding(paramString1 + String.format("/>", new Object[0]));
    }

    public static String composeSimpleMessage(String paramString1, String paramString2, BasicNameValuePair[] paramArrayOfBasicNameValuePair) {
        paramString1 = composeHead(paramString1);
        paramString1 = paramString1 + String.format("<%s ", new Object[] { paramString2 });
        int i = paramArrayOfBasicNameValuePair.length;
        for (byte b = 0; b < i; b++) {
            BasicNameValuePair basicNameValuePair = paramArrayOfBasicNameValuePair[b];
            paramString1 = paramString1 + String.format("%s=\"%s\" ", new Object[] { basicNameValuePair.getName(), basicNameValuePair.getValue() });
        }
        return appendEnding(paramString1 + String.format("/>", new Object[0]));
    }

    public static String composeSimpleMessageElements(String paramString, BasicNameValuePair[] paramArrayOfBasicNameValuePair) {
        paramString = composeHead(paramString);
        int i = paramArrayOfBasicNameValuePair.length;
        for (byte b = 0; b < i; b++) {
            BasicNameValuePair basicNameValuePair = paramArrayOfBasicNameValuePair[b];
            paramString = paramString + String.format("<%s>%s</%s>", new Object[] { basicNameValuePair.getName(), basicNameValuePair.getValue(), basicNameValuePair.getName() });
        }
        return appendEnding(paramString);
    }

    public static String getMessageIntent(String paramString) {
        String str1;
        String str2 = "";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            //this();
            paramString = paramString.replace("&", "+");
            StringReader stringReader = new StringReader(paramString);
            //this(paramString);
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

    public static String getNodeValue(NamedNodeMap paramNamedNodeMap, String paramString) {
        Node node = paramNamedNodeMap.getNamedItem(paramString);
        return (node != null) ? node.getNodeValue() : null;
    }

    public static HashMap<String, String> parseSimpleMessageElementsToHashMap(String paramString) {
        try {
            InputSource inputSource = new InputSource();
            //this();
            StringReader stringReader = new StringReader(paramString);
            //this(paramString);
            inputSource.setCharacterStream(stringReader);
            NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon");
            nodeList.item(0).getChildNodes();
            //HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
            //!!!! used HashMap<String, String> instead of HashMap<Object, Object>
            HashMap<String, String> hashMap = new HashMap<String, String>();
            //this();
            byte b = 0;
            while (true) {
                //HashMap<Object, Object> hashMap1 = hashMap;
                HashMap<String, String> hashMap1 = hashMap;
                if (b < nodeList.item(0).getChildNodes().getLength()) {
                    hashMap.put(nodeList.item(0).getChildNodes().item(b).getNodeName(), nodeList.item(0).getChildNodes().item(b).getFirstChild().getNodeValue());
                    b++;
                    continue;
                }
                break;
            }
            return  hashMap;
        } catch (Exception exception) {
            exception.printStackTrace();
            exception = null;
            return null;
        }
        //return (HashMap<String, String>)exception;
    }

    public static Node parseSimpleMessageNode(String paramString) {
        try {
            InputSource inputSource = new InputSource();
            //this();
            StringReader stringReader = new StringReader(paramString);
            //this(paramString);
            inputSource.setCharacterStream(stringReader);
            Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon").item(0).getFirstChild();
            return node;
        } catch (Exception exception) {
            exception.printStackTrace();
            exception = null;
            return (Node)exception;
        }
        //return (Node)exception;
    }

    public static NamedNodeMap parseSimpleMessageNodeMap(String paramString) {
        try {
            InputSource inputSource = new InputSource();
            //this();
            StringReader stringReader = new StringReader(paramString);
            //this(paramString);
            inputSource.setCharacterStream(stringReader);
            NamedNodeMap namedNodeMap = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon").item(0).getFirstChild().getAttributes();
            return  namedNodeMap;
        } catch (Exception exception) {
            exception.printStackTrace();
            exception = null;
            return (NamedNodeMap)exception;
        }
        //return (NamedNodeMap)exception;
    }

    public static String parseSimpleMessageType(String paramString) {
        String str;
        try {
            InputSource inputSource = new InputSource();
            //this();
            StringReader stringReader = new StringReader(paramString);
            //this(paramString);
            inputSource.setCharacterStream(stringReader);
            str = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getElementsByTagName("recon").item(0).getFirstChild().getAttributes().getNamedItem("type").getNodeValue();
        } catch (Exception exception) {
            exception.printStackTrace();
            str = "";
        }
        return str;
    }

    public static Document validate(String paramString1, String paramString2) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            StringBuilder stringBuilder2;
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            //this();
            StringReader stringReader = new StringReader(paramString2);
            //this(paramString2);
            inputSource.setCharacterStream(stringReader);
            Document document = documentBuilder.parse(inputSource);
            if (document.getElementsByTagName("recon").item(0).getAttributes().getNamedItem("intent").getNodeValue().compareTo(paramString1) != 0) {
                String str = TAG;
                stringBuilder2 = new StringBuilder();
                //this();
                Log.e(str, stringBuilder2.append("The XML protocol's intent should be ").append(paramString1).toString());
                return null;
            }
            Log.v(TAG, "Has right intent");
            //!!!! check if stringBuilder2 maybe is not initiallized
            //StringBuilder stringBuilder1 = stringBuilder2;
            return document;
        } catch (Exception exception) {
            Log.e(TAG, "Failed to parse xml", exception);
            exception = null;
            return (Document)exception;
        }
        //return (Document)exception;
    }
}
