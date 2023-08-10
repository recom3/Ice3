package com.reconinstruments.os.connectivity.bluetooth;

/**
 * Created by Recom3 on 15/05/2023.
 */

public class HUDBTMessage {

    /* renamed from: a  reason: collision with root package name */
    final byte idMsg;

    /* renamed from: b  reason: collision with root package name */
    byte[] header = null;
    //Payload
    byte[] payload = null;
    //Body
    byte[] d = null;
    int e = 0;
    int nReceivedBytes = 0;

    public HUDBTMessage(byte b2) {
        this.idMsg = b2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int a(int nBytesInBuffer, int nHeaderLen, int nPayloadLen, int nReceivedBytes) {
        int nRemain = nPayloadLen - nReceivedBytes;

        int nReturn = nBytesInBuffer - nHeaderLen;
        if (nRemain < nReturn) {
            nReturn = nRemain;
        }
        return nReturn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isBodyComplete() {
        boolean z = true;
        if (this.d != null) {
            StringBuilder sb = new StringBuilder("isBodyComplete() mBodyCurrentLength:").append(this.e).append(" mBody: ").append(this.d.length);
            if (this.e < this.d.length) {
                z = false;
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isPayloadComplete() {
        boolean z = true;
        if (this.payload != null && this.nReceivedBytes < this.payload.length) {
            z = false;
        }
        return z;
    }

    public final boolean isAllComplete() {
        boolean z;
        try {
            if (this.header == null) {
                z = false;
            } else {
                z = false;
                if (isBodyComplete()) {
                    z = false;
                    if (isPayloadComplete()) {
                        z = true;
                    }
                }
            }
        } catch (Exception e) {
            z = false;
        }
        return z;
    }
}
