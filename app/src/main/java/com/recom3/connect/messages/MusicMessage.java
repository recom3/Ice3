package com.recom3.connect.messages;

import com.recom3.connect.music.MusicDBFrontEnd;
import com.recom3.connect.util.XMLUtils;

import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Recom3 on 27/01/2022.
 */

public class MusicMessage {
    public static final String ATTR_ACTION = "action";

    public static final String ATTR_LOOP = "loop";

    public static final String ATTR_MUTE = "mute";

    public static final String ATTR_PROGRESS = "progress";

    public static final String ATTR_SHUFFLE = "shuffle";

    public static final String ATTR_SONGID = "songId";

    public static final String ATTR_SRCID = "srcId";

    public static final String ATTR_SRCTYPE = "srcType";

    public static final String ATTR_STATE = "state";

    public static final String ATTR_VOLUME = "volume";

    public static String intent = "RECON_MUSIC_MESSAGE";

    public Action action;

    public PlayerInfo info;

    public Type type;

    public MusicMessage(Action paramAction) {
        this.action = paramAction;
        this.type = Type.CONTROL;
    }

    public MusicMessage(Action paramAction, PlayerInfo paramPlayerInfo) {
        this.action = paramAction;
        this.info = paramPlayerInfo;
        this.type = Type.CONTROL;
    }

    public MusicMessage(PlayerInfo paramPlayerInfo, Type paramType) {
        this.info = paramPlayerInfo;
        this.type = paramType;
    }

    public MusicMessage(PlayerInfo paramPlayerInfo, Type paramType, Action paramAction) {
        this.info = paramPlayerInfo;
        this.type = paramType;
        this.action = paramAction;
    }

    public MusicMessage(SongInfo paramSongInfo) {
        this.action = Action.START_SONG;
        this.info = new PlayerInfo(null, paramSongInfo, null, null, Boolean.valueOf(false), null, null);
        this.type = Type.CONTROL;
    }

    public MusicMessage(String paramString) {
        Node node = XMLUtils.parseSimpleMessageNode(paramString);
        if (node.getNodeName().equalsIgnoreCase(Type.CONTROL.name())) {
            this.type = Type.CONTROL;
            NamedNodeMap namedNodeMap = node.getAttributes();
            if (namedNodeMap.getNamedItem("action") != null)
                this.action = Action.valueOf(namedNodeMap.getNamedItem("action").getNodeValue());
        } else if (node.getNodeName().equalsIgnoreCase(Type.STATUS.name())) {
            this.type = Type.STATUS;
        } else {
            throw new IllegalArgumentException();
        }
        this.info = new PlayerInfo(node.getAttributes());
    }

    public String toXML() {
        ArrayList<BasicNameValuePair> arrayList = new ArrayList();
        if (this.action != null)
            arrayList.add(new BasicNameValuePair("action", this.action.name()));
        if (this.info != null) {
            if (this.info.state != null)
                arrayList.add(new BasicNameValuePair("state", this.info.state.name()));
            if (this.info.volume != null)
                arrayList.add(new BasicNameValuePair("volume", "" + this.info.volume));
            if (this.info.progress != null)
                arrayList.add(new BasicNameValuePair("progress", "" + this.info.progress));
            if (this.info.mute != null)
                arrayList.add(new BasicNameValuePair("mute", "" + this.info.mute));
            if (this.info.shuffle != null)
                arrayList.add(new BasicNameValuePair("shuffle", "" + this.info.shuffle));
            if (this.info.loop != null)
                arrayList.add(new BasicNameValuePair("loop", "" + this.info.loop));
            if (this.info.song != null) {
                arrayList.add(new BasicNameValuePair("songId", "" + this.info.song.songId));
                arrayList.add(new BasicNameValuePair("srcType", this.info.song.srcType.name()));
                arrayList.add(new BasicNameValuePair("srcId", "" + this.info.song.srcId));
            }
        }
        return XMLUtils.composeSimpleMessage(intent, this.type.name().toLowerCase(), arrayList);
    }

    public enum Action {
        GET_PLAYER_STATE, NEXT_SONG, PREVIOUS_SONG, START_SONG, TOGGLE_PAUSE, VOLUME_DOWN, VOLUME_UP;

        static {
            //!!!!
            //START_SONG = new Action("START_SONG", 2);
            //PREVIOUS_SONG = new Action("PREVIOUS_SONG", 3);
            //NEXT_SONG = new Action("NEXT_SONG", 4);
            //TOGGLE_PAUSE = new Action("TOGGLE_PAUSE", 5);
            //GET_PLAYER_STATE = new Action("GET_PLAYER_STATE", 6);
            //$VALUES = new Action[] { VOLUME_UP, VOLUME_DOWN, START_SONG, PREVIOUS_SONG, NEXT_SONG, TOGGLE_PAUSE, GET_PLAYER_STATE };
        }
    }

    public static class PlayerInfo {
        public Boolean loop;

        public Boolean mute;

        public Integer progress;

        public Boolean shuffle;

        public MusicMessage.SongInfo song;

        public MusicMessage.PlayerState state;

        public Float volume;

        public PlayerInfo(MusicMessage.PlayerState param1PlayerState, MusicMessage.SongInfo param1SongInfo, Float param1Float, Integer param1Integer, Boolean param1Boolean1, Boolean param1Boolean2, Boolean param1Boolean3) {
            this.state = param1PlayerState;
            this.song = param1SongInfo;
            this.volume = param1Float;
            this.progress = param1Integer;
            this.shuffle = param1Boolean1;
            this.loop = param1Boolean2;
            this.mute = param1Boolean3;
        }

        public PlayerInfo(NamedNodeMap param1NamedNodeMap) {
            if (param1NamedNodeMap.getNamedItem("state") != null)
                this.state = MusicMessage.PlayerState.valueOf(param1NamedNodeMap.getNamedItem("state").getNodeValue());
            if (param1NamedNodeMap.getNamedItem("volume") != null)
                this.volume = Float.valueOf(Float.parseFloat(param1NamedNodeMap.getNamedItem("volume").getNodeValue()));
            if (param1NamedNodeMap.getNamedItem("progress") != null)
                this.progress = Integer.valueOf(Integer.parseInt(param1NamedNodeMap.getNamedItem("progress").getNodeValue()));
            if (param1NamedNodeMap.getNamedItem("mute") != null)
                this.mute = Boolean.valueOf(Boolean.parseBoolean(param1NamedNodeMap.getNamedItem("mute").getNodeValue()));
            if (param1NamedNodeMap.getNamedItem("shuffle") != null)
                this.shuffle = Boolean.valueOf(Boolean.parseBoolean(param1NamedNodeMap.getNamedItem("shuffle").getNodeValue()));
            if (param1NamedNodeMap.getNamedItem("loop") != null)
                this.loop = Boolean.valueOf(Boolean.parseBoolean(param1NamedNodeMap.getNamedItem("loop").getNodeValue()));
            if (param1NamedNodeMap.getNamedItem("songId") != null && param1NamedNodeMap.getNamedItem("srcType") != null && param1NamedNodeMap.getNamedItem("srcId") != null)
                this.song = new MusicMessage.SongInfo(param1NamedNodeMap);
        }

        public void update(PlayerInfo param1PlayerInfo) {
            if (param1PlayerInfo.state != null)
                this.state = param1PlayerInfo.state;
            if (param1PlayerInfo.volume != null)
                this.volume = param1PlayerInfo.volume;
            if (param1PlayerInfo.progress != null)
                this.progress = param1PlayerInfo.progress;
            if (param1PlayerInfo.mute != null)
                this.mute = param1PlayerInfo.mute;
            if (param1PlayerInfo.shuffle != null)
                this.shuffle = param1PlayerInfo.shuffle;
            if (param1PlayerInfo.loop != null)
                this.loop = param1PlayerInfo.loop;
            if (param1PlayerInfo.song != null)
                this.song = param1PlayerInfo.song;
        }
    }

    public enum PlayerState {
        NOT_INIT, PAUSED, PLAYING, STOPPED;

        static {
            //$VALUES = new PlayerState[] { NOT_INIT, PLAYING, PAUSED, STOPPED };
        }
    }

    public static class SongInfo implements Serializable {
        public String songId;

        public String srcId;

        public MusicDBFrontEnd.MusicListType srcType;

        public SongInfo(String param1String1, MusicDBFrontEnd.MusicListType param1MusicListType, String param1String2) {
            this.songId = param1String1;
            this.srcType = param1MusicListType;
            this.srcId = param1String2;
        }

        public SongInfo(NamedNodeMap param1NamedNodeMap) {
            this.songId = param1NamedNodeMap.getNamedItem("songId").getNodeValue();
            this.srcType = MusicDBFrontEnd.MusicListType.valueOf(param1NamedNodeMap.getNamedItem("srcType").getNodeValue());
            this.srcId = param1NamedNodeMap.getNamedItem("srcId").getNodeValue();
        }

        public boolean equals(SongInfo param1SongInfo) {
            return this.songId.equals(param1SongInfo.songId);
        }
    }

    public enum Type {
        CONTROL, STATUS;

        static {

        }
    }
}
