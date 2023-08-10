package com.recom3.snow3.model;

/**
 * Created by Recom3 on 10/07/2022.
 */

public enum PairingConnectionStateEnum {
    CONNECTED(1),
    CONNECTING(2),
    DISCONNECTED(0);

    private final int value;

    static {
        /*
        CONNECTING = new PairingConnectionStateEnum("CONNECTING", 1, 1);
        CONNECTED = new PairingConnectionStateEnum("CONNECTED", 2, 2);
        ENUM$VALUES = new PairingConnectionStateEnum[] { DISCONNECTED, CONNECTING, CONNECTED };
        */
    }

    PairingConnectionStateEnum(int paramInt1) {
        this.value = paramInt1;
    }

    public int getValue() {
        return this.value;
    }
}
