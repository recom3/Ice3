package com.recom3.connect.music;

/**
 * Created by Recom3 on 27/01/2022.
 */

public abstract class ReconMediaData {
    public String id;

    public String name;

    ReconMediaData(String paramString1, String paramString2) {
        this.id = paramString1;
        this.name = paramString2;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(String paramString) {
        this.id = paramString;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public static class ReconAlbum extends ReconMediaData {
        public String artistId;

        public String artistName;

        public String pos;

        public ReconAlbum(String param1String1, String param1String2) {
            super(param1String1, param1String2);
        }

        public ReconAlbum(String param1String1, String param1String2, String param1String3, String param1String4) {
            super(param1String1, param1String4);
            this.artistId = param1String2;
            this.artistName = param1String3;
        }
    }

    public static class ReconArtist extends ReconMediaData {
        public ReconArtist(String param1String1, String param1String2) {
            super(param1String1, param1String2);
        }
    }

    public static class ReconPlaylist extends ReconMediaData {
        public ReconPlaylist(String param1String1, String param1String2) {
            super(param1String1, param1String2);
        }
    }

    public static class ReconSong extends ReconMediaData {
        public String album;

        public String artist;

        public String data;

        public long duration;

        public String title;

        public ReconSong(String param1String1, String param1String2, String param1String3, String param1String4, long param1Long) {
            super(param1String1, param1String2 + " - " + param1String4);
            this.artist = param1String2;
            this.title = param1String4;
            this.album = param1String3;
            this.duration = param1Long;
        }
    }
}
