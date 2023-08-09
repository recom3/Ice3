package com.recom3.connect.messages;

import android.os.Bundle;

import com.recom3.connect.util.FileUtils;
import com.recom3.connect.util.XMLUtils;
import com.recom3.snow3.mobilesdk.utils.DateUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class TransferResponseMessage {
    static final String CHECK_NODE = "check";

    static final String NODE_ATTR_DATE = "date";

    static final String NODE_ATTR_FILE = "file";

    static final String NODE_ATTR_LAT = "latitude";

    static final String NODE_ATTR_LENGTH = "length";

    static final String NODE_ATTR_LONG = "longitude";

    static final String NODE_ATTR_NAME = "name";

    static final String NODE_ATTR_PATH_TYPE = "path";

    static final String NODE_ATTR_SIZE = "size";

    static final String NODE_ATTR_SUM = "sum";

    static final String SENDING_NODE = "sending-file";

    static final String TAG = "TransferResponseMessage";

    static final String TRIP_NODE = "trip";

    static String intent = "RECON_TRANSFER_RESPONSE";

    //!!!!: review all this code
    public static String compose(ResponseBundle paramResponseBundle) {
        String str2;
        FileInfo fileInfo = null;
        Bundle bundle1;
        FileInfo[] arrayOfFileInfo;
        int i;
        int j;
        String str6;
        String str3 = XMLUtils.composeHead(intent);
        switch (paramResponseBundle.type) {
            default:
                str2 = str3;
                return XMLUtils.appendEnding(str2);
            case DIR:
                //!!!!
                //arrayOfFileInfo = (FileInfo[])((ResponseBundle)str2).data;
                arrayOfFileInfo = (FileInfo[])((ResponseBundle)paramResponseBundle).data;
                i = arrayOfFileInfo.length;
                j = 0;
                while (true) {
                    str2 = str3;
                    if (j < i) {
                        fileInfo = arrayOfFileInfo[j];
                        str3 = str3 + String.format("<%s %s=\"%s\" %s=\"%s\" %s=\"%s\"/>", new Object[] { "trip", "name", fileInfo.fileName, "date", DateUtils.fileDateToString(fileInfo.date), "length", Integer.valueOf(fileInfo.numRecords) });
                        j++;
                        continue;
                    }
                    //return XMLUtils.appendEnding((String)fileInfo);
                    return XMLUtils.appendEnding((String)fileInfo.toString());
                }
            case FILE:
                //bundle1 = (Bundle)((ResponseBundle)fileInfo).data;
                bundle1 = (Bundle)((ResponseBundle)paramResponseBundle).data;
                str6 = bundle1.getString("file");
                String str4 = bundle1.getString("path");
                j = bundle1.getInt("size");
                String str1 = str3 + String.format("<%s %s=\"%s\" %s=\"%s\" %s=\"%s\"/>", new Object[] { "sending-file", "file", str6, "path", str4, "size", Integer.valueOf(j) });
                return XMLUtils.appendEnding(str1);
            case CHECK:
                break;
        }
        //!!!!
        //Bundle bundle2 = (Bundle)((ResponseBundle)str1).data;
        Bundle bundle2 = (Bundle)((ResponseBundle)paramResponseBundle).data;
        String str1 = bundle2.getString("file");
        String str4 = bundle2.getString("path");
        String str5 = bundle2.getString("sum");
        str1 = str3 + String.format("<%s %s=\"%s\" %s=\"%s\" %s=\"%s\"/>", new Object[] { "check", "file", str1, "path", str4, "sum", str5 });
        return XMLUtils.appendEnding(str1);
    }

    public static ResponseBundle parseResponse(String paramString) {
        Document doc = XMLUtils.validate(intent, paramString);
        if (doc == null)
            return null;
        try {
            ResponseBundle responseBundle = null;
            Node node2 = doc.getElementsByTagName("recon").item(0);
            Node node1 = node2.getFirstChild();
            if (!node2.hasChildNodes()) {
                responseBundle = new ResponseBundle(TransferResponse.DIR, new FileInfo[0]);
                //this(TransferResponse.DIR, new FileInfo[0]);
                return responseBundle;
            }
            //!!!!
            //if (responseBundle.getNodeType() == 1 && responseBundle.getNodeName().compareToIgnoreCase("trip") == 0) {
            if (doc.getNodeType() == 1 && doc.getNodeName().compareToIgnoreCase("trip") == 0) {
                NodeList nodeList = node2.getChildNodes();
                FileInfo[] arrayOfFileInfo = new FileInfo[nodeList.getLength()];
                for (byte b = 0; b < nodeList.getLength(); b++) {
                    NamedNodeMap namedNodeMap = nodeList.item(b).getAttributes();
                    arrayOfFileInfo[b] = new FileInfo(namedNodeMap.getNamedItem("name").getNodeValue(), DateUtils.fileDateStringToDate(namedNodeMap.getNamedItem("date").getNodeValue()), Integer.parseInt(namedNodeMap.getNamedItem("length").getNodeValue()));
                }
                return new ResponseBundle(TransferResponse.DIR, arrayOfFileInfo);
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return null;
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return null;
        }
        if (doc.getNodeType() == 1 && doc.getNodeName().compareToIgnoreCase("sending-file") == 0) {
            FileUtils.FilePath filePath;
            NamedNodeMap namedNodeMap = doc.getAttributes();
            Node node3 = namedNodeMap.getNamedItem("file");
            Node node1 = namedNodeMap.getNamedItem("path");
            Node node2 = namedNodeMap.getNamedItem("size");
            if (node3 != null && node1 != null) {
                FileUtils.FilePath.PathType pathType = FileUtils.FilePath.PathType.valueOf(node1.getNodeValue());
                filePath = new FileUtils.FilePath(node3.getNodeValue(), pathType);
                //this(node3.getNodeValue(), pathType);
            } else {
                filePath = new FileUtils.FilePath("", FileUtils.FilePath.PathType.ROOT);
            }
            return new ResponseBundle(TransferResponse.FILE, filePath, Integer.parseInt(node2.getNodeValue()));
        }
        if (doc.getNodeType() == 1 && doc.getNodeName().compareToIgnoreCase("check") == 0) {
            FileUtils.FilePath.PathType pathType;
            NamedNodeMap namedNodeMap = doc.getAttributes();
            Node node2 = namedNodeMap.getNamedItem("file");
            Node node1 = namedNodeMap.getNamedItem("path");
            Node node3 = namedNodeMap.getNamedItem("sum");
            if (node1 != null) {
                pathType = FileUtils.FilePath.PathType.valueOf(node1.getNodeValue());
            } else {
                pathType = FileUtils.FilePath.PathType.ROOT;
            }
            FileUtils.FilePath filePath = new FileUtils.FilePath(node2.getNodeValue(), pathType);
            //this(node2.getNodeValue(), pathType);
            return new ResponseBundle(TransferResponse.CHECK, filePath, node3.getNodeValue());
        }
        return null;
    }

    public static class FileInfo {
        public long date;

        public String fileName;

        public int numRecords;

        public FileInfo(String param1String, long param1Long, int param1Int) {
            this.fileName = param1String;
            this.date = param1Long;
            this.numRecords = param1Int;
        }
    }

    public static class ResponseBundle {
        public Object data;

        public TransferResponseMessage.TransferResponse type = TransferResponseMessage.TransferResponse.NONE;

        public ResponseBundle(TransferResponseMessage.TransferResponse param1TransferResponse, FileUtils.FilePath param1FilePath, int param1Int) {
            this.type = param1TransferResponse;
            this.data = new Bundle();
            ((Bundle)this.data).putInt("size", param1Int);
            if (param1FilePath != null) {
                ((Bundle)this.data).putString("file", param1FilePath.path);
                ((Bundle)this.data).putString("path", param1FilePath.type.name());
            }
        }

        public ResponseBundle(TransferResponseMessage.TransferResponse param1TransferResponse, FileUtils.FilePath param1FilePath, String param1String) {
            this.type = param1TransferResponse;
            this.data = new Bundle();
            ((Bundle)this.data).putString("sum", param1String);
            ((Bundle)this.data).putString("file", param1FilePath.path);
            ((Bundle)this.data).putString("path", param1FilePath.type.name());
        }

        public ResponseBundle(TransferResponseMessage.TransferResponse param1TransferResponse, TransferResponseMessage.FileInfo[] param1ArrayOfFileInfo) {
            this.type = param1TransferResponse;
            this.data = param1ArrayOfFileInfo;
        }

        public FileUtils.FilePath getPath() {
            return new FileUtils.FilePath(((Bundle)this.data).getString("file"), FileUtils.FilePath.PathType.valueOf(((Bundle)this.data).getString("path")));
        }

        public int getSize() {
            return ((Bundle)this.data).getInt("size");
        }

        public String getSum() {
            return ((Bundle)this.data).getString("sum");
        }
    }

    public enum TransferResponse {
        CHECK, DIR, FILE, NONE;

        static {
            //!!!!
            //CHECK = new TransferResponse("CHECK", 3);
            //$VALUES = new TransferResponse[] { NONE, DIR, FILE, CHECK };
        }
    }
}
