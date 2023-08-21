package com.reconinstruments.connect.messages;

import com.recom3.connect.util.FileUtils;
import com.recom3.connect.util.XMLUtils;
import com.recom3.snow3.mobilesdk.messages.XMLMessage;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by Chus on 20/08/2023.
 */

public class TransferRequestMessage {
    static final String ACTION_ATTR_DEST_FILE = "destFile";
    static final String ACTION_ATTR_DEST_PATH = "destPath";
    static final String ACTION_ATTR_FILE_NAME = "file";
    static final String ACTION_ATTR_PATH_TYPE = "path";
    static final String ACTION_ATTR_TYPE = "type";
    static final String ACTION_NODE_NAME = "action";
    static final String TAG = "TransferRequestMessage";
    static String intent = XMLMessage.TRANSFER_REQUEST_MESSAGE;

    /* loaded from: classes.dex */
    public enum RequestType {
        NONE,
        DIR_REQUEST,
        FILE_REQUEST,
        FILE_CHECK,
        FILE_PUSH,
        FILE_DELETE,
        TEST
    }

    /* loaded from: classes.dex */
    public static class RequestBundle {
        public FileUtils.FilePath destPath;
        public FileUtils.FilePath filePath;
        public RequestType type;

        public RequestBundle(RequestType type) {
            this.type = type;
        }

        public RequestBundle(RequestType type, FileUtils.FilePath filePath) {
            this.type = type;
            this.filePath = filePath;
        }

        public RequestBundle(RequestType type, FileUtils.FilePath filePath, FileUtils.FilePath destPath) {
            this.type = type;
            this.filePath = filePath;
            this.destPath = destPath;
        }
    }

    public static String requestName(RequestType type) {
        switch (type) {
            case DIR_REQUEST:
                return "get-dir";
            case FILE_REQUEST:
                return "get-file";
            case FILE_CHECK:
                return "check";
            case FILE_PUSH:
                return "push-file";
            case FILE_DELETE:
                return "delete-file";
            case TEST:
                return "test";
            default:
                return "";
        }
    }

    public static RequestType requestType(String type) {
        if (type.equalsIgnoreCase("get-dir")) {
            return RequestType.DIR_REQUEST;
        }
        if (type.equalsIgnoreCase("get-file")) {
            return RequestType.FILE_REQUEST;
        }
        if (type.equalsIgnoreCase("check")) {
            return RequestType.FILE_CHECK;
        }
        if (type.equalsIgnoreCase("push-file")) {
            return RequestType.FILE_PUSH;
        }
        if (type.equalsIgnoreCase("delete-file")) {
            return RequestType.FILE_DELETE;
        }
        return RequestType.NONE;
    }

    public static String compose(RequestBundle bundle) {
        RequestType type = bundle.type;
        if (type == RequestType.DIR_REQUEST) {
            String message = XMLUtils.composeSimpleMessage(intent, "action", new BasicNameValuePair[]{new BasicNameValuePair(ACTION_ATTR_TYPE, requestName(type))});
            return message;
        }
        BasicNameValuePair[] pairs = new BasicNameValuePair[(bundle.destPath != null ? 2 : 0) + 3];
        pairs[0] = new BasicNameValuePair(ACTION_ATTR_TYPE, requestName(type));
        pairs[1] = new BasicNameValuePair(ACTION_ATTR_FILE_NAME, bundle.filePath.path);
        pairs[2] = new BasicNameValuePair(ACTION_ATTR_PATH_TYPE, bundle.filePath.type.name());
        if (bundle.destPath != null) {
            pairs[3] = new BasicNameValuePair(ACTION_ATTR_DEST_FILE, bundle.destPath.path);
            pairs[4] = new BasicNameValuePair(ACTION_ATTR_DEST_PATH, bundle.destPath.type.name());
        }
        String message2 = XMLUtils.composeSimpleMessage(intent, "action", pairs);
        return message2;
    }

    public static RequestBundle parseRequest(String message) {
        Document doc = XMLUtils.validate(intent, message);
        if (doc == null) {
            return null;
        }
        RequestBundle reqBundle = new RequestBundle(RequestType.NONE, null, null);
        try {
            NodeList nodes = doc.getElementsByTagName("recon");
            Node rootNode = nodes.item(0);
            Node firstChild = rootNode.getFirstChild();
            if (firstChild.getNodeType() == 1 && firstChild.getNodeName().compareToIgnoreCase("action") == 0) {
                NamedNodeMap actionAttr = firstChild.getAttributes();
                Node nActionType = actionAttr.getNamedItem(ACTION_ATTR_TYPE);
                String actionType = nActionType.getNodeValue();
                reqBundle.type = requestType(actionType);
                if (reqBundle.type != RequestType.DIR_REQUEST) {
                    Node nFileName = actionAttr.getNamedItem(ACTION_ATTR_FILE_NAME);
                    Node nPathType = actionAttr.getNamedItem(ACTION_ATTR_PATH_TYPE);
                    String fileName = nFileName.getNodeValue();
                    FileUtils.FilePath.PathType pathType = FileUtils.FilePath.PathType.valueOf(nPathType.getNodeValue());
                    reqBundle.filePath = new FileUtils.FilePath(fileName, pathType);
                    Node nDestFile = actionAttr.getNamedItem(ACTION_ATTR_DEST_FILE);
                    Node nDestPath = actionAttr.getNamedItem(ACTION_ATTR_DEST_PATH);
                    if (nDestFile != null && nDestPath != null) {
                        String destFileName = nDestFile.getNodeValue();
                        FileUtils.FilePath.PathType destPathType = FileUtils.FilePath.PathType.valueOf(nDestPath.getNodeValue());
                        reqBundle.destPath = new FileUtils.FilePath(destFileName, destPathType);
                    } else {
                        reqBundle.destPath = reqBundle.filePath;
                    }
                }
            }
            return reqBundle;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return reqBundle;
        } catch (NumberFormatException e2) {
            e2.printStackTrace();
            return reqBundle;
        }
    }
}
