package com.reconinstruments.os.connectivity.bluetooth;

import android.util.Log;
import android.util.SparseArray;

/**
 * Created by Recom3 on 15/05/2023.
 */

public class HUDBTMessageCollectionManager {

    /* renamed from: a  reason: collision with root package name */
    private static boolean[] f2734a = new boolean[126/*TransportMediator.KEYCODE_MEDIA_PLAY*/];

    /* renamed from: b  reason: collision with root package name */
    private static SparseArray<HUDBTMessage> sparseArrTot;
    private static SparseArray<HUDBTMessage> sparseArr;

    static {
        for (int i = 0; i < 126; i++) {
            f2734a[i] = true;
        }
        sparseArrTot = new SparseArray<>();
        sparseArr = new SparseArray<>();
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00fa  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01c9  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0220  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x0271  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x029f  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x02ad  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x007b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */

    /**
     *
     * @param i                 Thread index
     * @param bArr
     * @param nBytesInBuffer                Number of bytes received in buffer
     * @param excessDataAgent
     * @return
     */
    public static HUDBTMessage a(int i, byte[] bArr, int nBytesInBuffer, ExcessDataAgent excessDataAgent) {
        HUDBTMessage hUDBTMessage;
        HUDBTMessage hUDBTMessageReturn = null;
        int i3 = 0;
        int length = 0;
        int i4;
        boolean isHeaderValid = HUDBTHeaderFactory.checkHeaderIsValid(bArr);
        Log.i("HUDBTMessageCollectionManager", "isHeaderValid" + isHeaderValid);

        if (isHeaderValid) {
            //byte 1 is the message id?
            byte idMsg = HUDBTHeaderFactory.getByte1(bArr);
            //With msg with byte3=1 we do something special
            //It seems byte3=1 come the response
            //---------------------------------------------------------------------------------------------------------
            if (HUDBTHeaderFactory.checkByte3(bArr) == 1) {
                HUDBTMessage hUDBTMessage3 = sparseArr.get(i);
                if (hUDBTMessage3 != null && isHeaderValid) {
                    sparseArr.remove(i);
                    setIn126BoolArrByIndex(hUDBTMessage3.idMsg);
                    //Here we set the message as received?
                }
                hUDBTMessage = sparseArrTot.get(idMsg);
                if (hUDBTMessage == null) {
                    hUDBTMessageReturn = null;
                } else {
                    sparseArrTot.remove(idMsg);
                }
            } else {
                hUDBTMessage = new HUDBTMessage(idMsg);
            }
            //Add to array
            //---------------------------------------------------------------------------------------------------------
            sparseArr.put(i, hUDBTMessage);
            //---------------------------------------------------------------------------------------------------------
            if (bArr != null && (hUDBTMessage.header != null || isHeaderValid)) {
                //!!!
                //if (isHeaderValid) {
                //    i3 = 0;
                //} else {
                    try {
                        hUDBTMessage.header = new byte[32];
                        System.arraycopy(bArr, 0, hUDBTMessage.header, 0, hUDBTMessage.header.length);
                        length = hUDBTMessage.header.length + 0;
                    } catch (Exception e) {
                        e = e;
                        i3 = 0;
                    }

                    try {
                        if (HUDBTHeaderFactory.g(hUDBTMessage.header)) {
                            hUDBTMessage.payload = new byte[HUDBTHeaderFactory.h(hUDBTMessage.header)];
                            hUDBTMessage.nReceivedBytes = 0;
                        }
                        if (HUDBTHeaderFactory.i(hUDBTMessage.header)) {
                            hUDBTMessage.d = new byte[HUDBTHeaderFactory.j(hUDBTMessage.header)];
                            hUDBTMessage.e = 0;
                        }
                        i3 = length;
                        if (nBytesInBuffer <= length) {
                            excessDataAgent.buffer = null;
                        }
                    } catch (Exception e2) {
                        Exception e = e2;
                        i3 = length;
                        e.printStackTrace();
                        int i5 = i3;
                        if (!hUDBTMessage.isPayloadComplete()) {
                        }
                        i4 = i5;
                        if (!hUDBTMessage.isBodyComplete()) {
                        }
                        if (nBytesInBuffer > i4) {
                        }
                        if (!hUDBTMessage.isAllComplete()) {

                        }
                        return hUDBTMessageReturn;
                    }
                    //!!!
                //}
                int i52 = i3;
                //-----------------------------------------------------------------------------------------------------
                if (!hUDBTMessage.isPayloadComplete()) {
                    int bytesAvailableFromPayload = HUDBTMessage.a(nBytesInBuffer, i3, hUDBTMessage.payload.length, hUDBTMessage.nReceivedBytes);

                    System.arraycopy(bArr, i3, hUDBTMessage.payload, hUDBTMessage.nReceivedBytes, bytesAvailableFromPayload);
                    int i6 = i3 + bytesAvailableFromPayload;
                    hUDBTMessage.nReceivedBytes = bytesAvailableFromPayload + hUDBTMessage.nReceivedBytes;
                    i52 = i6;
                    if (nBytesInBuffer <= i6) {
                        excessDataAgent.buffer = null;
                    }
                }
                i4 = i52;
                if (!hUDBTMessage.isBodyComplete()) {
                    int a3 = HUDBTMessage.a(nBytesInBuffer, i52, hUDBTMessage.d.length, hUDBTMessage.e);
                    System.arraycopy(bArr, i52, hUDBTMessage.d, hUDBTMessage.e, a3);
                    int i7 = i52 + a3;
                    hUDBTMessage.e = a3 + hUDBTMessage.e;
                    i4 = i7;
                    if (nBytesInBuffer <= i7) {
                        excessDataAgent.buffer = null;
                    }
                }
                if (nBytesInBuffer > i4) {
                    int i8 = nBytesInBuffer - i4;
                    excessDataAgent.buffer = new byte[i8];
                    System.arraycopy(bArr, i4, excessDataAgent.buffer, 0, i8);
                }
            }
            //Final check
            //---------------------------------------------------------------------------------------------------------
            if (!hUDBTMessage.isAllComplete()) {
                hUDBTMessageReturn = null;
            }
            else if (HUDBTHeaderFactory.isByte3equal1(hUDBTMessage.header)) {
                synchronized (hUDBTMessage) {
                    try {
                        hUDBTMessage.notify();
                    } catch (Throwable th) {
                        HUDBTMessage hUDBTMessage4 = hUDBTMessage;
                        throw th;
                    }
                }
                setIn126BoolArrByIndex(HUDBTHeaderFactory.getByte1(hUDBTMessage.header));
                sparseArr.remove(i);
                hUDBTMessageReturn = null;
            } else {
                sparseArr.remove(i);
                hUDBTMessageReturn = hUDBTMessage;
            }
        //Header is not present
        //-------------------------------------------------------------------------------------------------------------
        } else {
            hUDBTMessage = sparseArr.get(i);
            if (hUDBTMessage == null) {
                if (excessDataAgent.a() > 0) {
                    byte[] bArr2 = new byte[32];
                    int a4 = 32 - excessDataAgent.a();
                    if (a4 <= nBytesInBuffer) {
                        System.arraycopy(excessDataAgent.buffer, 0, bArr2, 0, excessDataAgent.a());
                        System.arraycopy(bArr, 0, bArr2, excessDataAgent.a(), a4);
                        if (HUDBTHeaderFactory.checkHeaderIsValid(bArr2)) {
                            byte[] bArr3 = new byte[excessDataAgent.a() + nBytesInBuffer];
                            System.arraycopy(excessDataAgent.buffer, 0, bArr3, 0, excessDataAgent.a());
                            System.arraycopy(bArr, 0, bArr3, excessDataAgent.a(), nBytesInBuffer);
                            excessDataAgent.buffer = bArr3;
                        }
                    }
                }
                hUDBTMessageReturn = null;
            }
            else
            {
                //Can be data present in excess data agent
                /*
                int i8 = nBytesInBuffer - i4;
                excessDataAgent.buffer = new byte[i8];
                System.arraycopy(bArr, i4, excessDataAgent.buffer, 0, i8);
                */
                int i8 = 0;
                if(excessDataAgent!=null && excessDataAgent.buffer!=null)
                {
                    i8 = excessDataAgent.buffer.length;
                }
            }
            if (bArr != null) {
                if (isHeaderValid) {
                    //If header is not valid this is an error
                }
                int i52 = i3;
                //Payload?- The one coming from liftie only has payload
                if (!hUDBTMessage.isPayloadComplete()) {
                    int bytesAvailableFromPayload = HUDBTMessage.a(nBytesInBuffer, i3, hUDBTMessage.payload.length, hUDBTMessage.nReceivedBytes);

                    System.arraycopy(bArr, i3, hUDBTMessage.payload, hUDBTMessage.nReceivedBytes, bytesAvailableFromPayload);
                    int i6 = i3 + bytesAvailableFromPayload;
                    hUDBTMessage.nReceivedBytes = bytesAvailableFromPayload + hUDBTMessage.nReceivedBytes;
                    i52 = i6;
                    if (nBytesInBuffer <= i6) {
                        excessDataAgent.buffer = null;
                    }
                }
                i4 = i52;
                if (!hUDBTMessage.isBodyComplete()) {
                }
                if (nBytesInBuffer > i4) {
                }
            }
            if (!hUDBTMessage.isAllComplete()) {
                hUDBTMessageReturn = null;
            }
            else {
                sparseArr.remove(i);
                hUDBTMessageReturn = hUDBTMessage;
            }
        }
        return hUDBTMessageReturn;
    }

    private static void setIn126BoolArrByIndex(byte b2) {
        synchronized (f2734a) {
            if (b2 < 126) {
                f2734a[b2] = true;
            }
        }
    }
}
