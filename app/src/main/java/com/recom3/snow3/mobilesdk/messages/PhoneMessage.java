package com.recom3.snow3.mobilesdk.messages;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

import java.util.ArrayList;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Recom3 on 29/03/2022.
 */

public class PhoneMessage {

    public static String intent = "RECON_PHONE_MESSAGE";//a(recon engage jar)

    public String body;//g(recon engage jar)

    public Type mode;//b?(recon engage jar)

    public String name;//e(recon engage jar)

    public String number;//d(recon engage jar)

    public String title;//f(recon engage jar)

    public Enum<?> type;//c?(recon engage jar)

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

    //From recon engage jar
    public final String a() {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        try {
            xmlSerializer.setOutput(stringWriter);
            xmlSerializer.startTag("", "recon");
            xmlSerializer.attribute("", "intent", intent);
            xmlSerializer.startTag("", this.mode.name());
            xmlSerializer.attribute("", "type", this.type.name());
            String str;
            if (this.number != null) {
                str =this.number;
            } else {
                str ="";
            }
            xmlSerializer.attribute("", "number", str);
            if (this.name != null) {
                str =this.name;
            } else {
                str ="";
            }
            xmlSerializer.attribute("", "name", str);
            if (this.title != null) {
                //!!!!
                //str =b(this.f);
                str = this.title;
            } else {
                str ="";
            }
            xmlSerializer.attribute("", "title", str);
            if (this.body != null) {
                //!!!!
                //str =b(this.body);
                str = this.body;
            } else {
                str ="";
            }
            xmlSerializer.attribute("", "body", str);
            xmlSerializer.endTag("", this.mode.name());
            xmlSerializer.endTag("", "recon");
            xmlSerializer.endDocument();
            return stringWriter.toString();
        } catch (IOException iOException) {
            //Log.c(h, iOException.getMessage(), iOException);
        } catch (IllegalArgumentException illegalArgumentException) {
            //Log.c(h, illegalArgumentException.getMessage(), illegalArgumentException);
        }
        return "";
    }
}
