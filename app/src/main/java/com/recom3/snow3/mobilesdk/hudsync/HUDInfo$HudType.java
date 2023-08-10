package com.recom3.snow3.mobilesdk.hudsync;

/**
 * Created by Recom3 on 18/03/2022.
 */

public enum HUDInfo$HudType {
    a("HUD"),
    b("Snow2"),
    c("Jet");

    public final String d;

    HUDInfo$HudType(String paramString1) {
        this.d = paramString1;
    }

    public static HUDInfo$HudType a(String paramString) {
        for (HUDInfo$HudType hUDInfo$HudType : values()) {
            if (hUDInfo$HudType.d.equalsIgnoreCase(paramString))
                return hUDInfo$HudType;
        }
        throw new IllegalArgumentException(String.format("The value '%s' does not match any HudType enum", new Object[] { paramString }));
    }
}
