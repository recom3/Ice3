package com.recom3.snow3.model;

import com.recom3.snow3.mobilesdk.hudconnectivity.HUDStateUpdateListener;

/**
 * Created by Recom3 on 10/07/2022.
 */

public class PairingConnectionState {
    private PairingConnectionStateEnum mPairingConnectionStateEnum;

    public PairingConnectionState(PairingConnectionStateEnum paramPairingConnectionStateEnum) {
        this.mPairingConnectionStateEnum = paramPairingConnectionStateEnum;
    }

    public PairingConnectionState(HUDStateUpdateListener.HUD_STATE paramHUD_STATE) {
        switch (paramHUD_STATE) {
            default:
                this.mPairingConnectionStateEnum = PairingConnectionStateEnum.CONNECTED;
                return;
            case DISCONNECTED:
                this.mPairingConnectionStateEnum = PairingConnectionStateEnum.DISCONNECTED;
                return;
            case CONNECTING:
                this.mPairingConnectionStateEnum = PairingConnectionStateEnum.CONNECTING;
                return;
        }
    }

    public boolean equals(Object paramObject) {
        boolean bool = true;
        if (this != paramObject) {
            if (paramObject == null)
                return false;
            if (getClass() != paramObject.getClass())
                return false;
            paramObject = paramObject;
            if (this.mPairingConnectionStateEnum != ((PairingConnectionState)paramObject).mPairingConnectionStateEnum)
                bool = false;
        }
        return bool;
    }

    public boolean isConnected() {
        return (this.mPairingConnectionStateEnum == PairingConnectionStateEnum.CONNECTED);
    }

    public boolean isConnecting() {
        return (this.mPairingConnectionStateEnum == PairingConnectionStateEnum.CONNECTING);
    }

    public boolean isDisconnected() {
        return (this.mPairingConnectionStateEnum == PairingConnectionStateEnum.DISCONNECTED);
    }
}

