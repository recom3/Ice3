package com.reconinstruments.modlivemobile.dto.message;

import java.util.ArrayList;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class PhoneMessage {
    public static String intent = "RECON_PHONE_MESSAGE";

    public String body;

    public Type mode;

    public String name;

    public String number;

    public String title;

    public Enum<?> type;

    public PhoneMessage(Enum<?> paramEnum, String... paramVarArgs) {
        if (paramEnum.getClass() == Control.class) {
            this.mode = Type.CONTROL;
        } else {
            this.mode = Type.STATUS;
        }
        this.type = paramEnum;
        byte b = 0;
        while (true) {
            if (b >= paramVarArgs.length)
                return;
            switch (b) {
                case 0:
                    this.number = paramVarArgs[b];
                    b++;
                    break;
                case 1:
                    this.name = paramVarArgs[b];
                    b++;
                    break;
                case 2:
                    this.title = paramVarArgs[b];
                    b++;
                    break;
                case 3:
                    this.body = paramVarArgs[b];
                    b++;
                    break;
            }
        }
    }

    public PhoneMessage(String paramString) {
        Node node = XMLMessage.parseSimpleMessageNode(paramString);
        this.mode = Type.valueOf(node.getNodeName());
        NamedNodeMap namedNodeMap = node.getAttributes();
        if (this.mode == Type.CONTROL) {
            if (namedNodeMap.getNamedItem("type") != null)
                this.type = Control.valueOf(namedNodeMap.getNamedItem("type").getNodeValue());
        } else if (this.mode == Type.STATUS && namedNodeMap.getNamedItem("type") != null) {
            this.type = Status.valueOf(namedNodeMap.getNamedItem("type").getNodeValue());
        }
        if (namedNodeMap.getNamedItem("number") != null)
            this.number = namedNodeMap.getNamedItem("number").getNodeValue();
        if (namedNodeMap.getNamedItem("name") != null)
            this.name = namedNodeMap.getNamedItem("name").getNodeValue();
        if (namedNodeMap.getNamedItem("title") != null)
            this.title = namedNodeMap.getNamedItem("title").getNodeValue();
        if (namedNodeMap.getNamedItem("body") != null)
            this.body = namedNodeMap.getNamedItem("body").getNodeValue();
    }

    public boolean isControl() {
        return (this.mode == Type.CONTROL);
    }

    public String toXML() {
        ArrayList<BasicNameValuePair> arrayList = new ArrayList();
        arrayList.add(new BasicNameValuePair("type", this.type.name()));
        if (this.number != null)
            arrayList.add(new BasicNameValuePair("number", this.number));
        if (this.name != null)
            arrayList.add(new BasicNameValuePair("name", this.name));
        if (this.title != null)
            arrayList.add(new BasicNameValuePair("title", this.title));
        if (this.body != null)
            arrayList.add(new BasicNameValuePair("body", this.body));
        return XMLMessage.composeSimpleMessage(intent, this.mode.name(), arrayList);
    }

    public enum Control {
        ANSWER, END, REJECT, SENDSMS, START;

        static {
            //ENUM$VALUES = new Control[] { ANSWER, REJECT, START, END, SENDSMS };
        }
    }

    public enum Status {
        RINGING, STARTED, ENDED, GOTSMS, OFFHOOK, REFRESH_NEEDED;

        static {
            //GOTSMS = new Status("GOTSMS", 4);
            //REFRESH_NEEDED = new Status("REFRESH_NEEDED", 5);
            //ENUM$VALUES = new Status[] { RINGING, STARTED, ENDED, OFFHOOK, GOTSMS, REFRESH_NEEDED };
        }
    }

    public enum Type {
        CONTROL, STATUS;

        static {

        }
    }
}
