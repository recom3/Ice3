package com.recom3.connect.messages;

import com.recom3.connect.music.ReconMediaData;
import com.recom3.connect.util.XMLUtils;
import com.recom3.snow3.mobilesdk.messages.XMLMessage;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by recom3 on 09/08/2023.
 */

public class SongMessage {
    public static final String ATTR_ALBUM = "album";
    public static final String ATTR_ARTIST = "artist";
    public static final String ATTR_SONG = "song";
    public static final String ATTR_TITLE = "title";
    public static String intent = XMLMessage.SONG_MESSAGE;
    public ReconMediaData.ReconSong song;

    public static ReconMediaData.ReconSong getSong(String xml) {
        NamedNodeMap map = XMLUtils.parseSimpleMessageNodeMap(xml);
        String title = getNodeValue(map, "title");
        String artist = getNodeValue(map, ATTR_ARTIST);
        String album = getNodeValue(map, ATTR_ALBUM);
        return new ReconMediaData.ReconSong("0", artist, album, title, 0L);
    }

    public static String getNodeValue(NamedNodeMap map, String attr) {
        Node node = map.getNamedItem(attr);
        return node != null ? node.getNodeValue() : "";
    }
}