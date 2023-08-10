package com.reconinstruments.os.connectivity.http;

/**
 * Created by Recom3 on 15/05/2023.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.reconinstruments.os.connectivity.IHUDConnectivity;
import com.reconinstruments.os.connectivity.bluetooth.HUDBTBaseService;
import com.reconinstruments.os.connectivity.bluetooth.HUDBTHeaderFactory;
import com.reconinstruments.os.connectivity.bluetooth.IHUDBTConsumer;
import com.reconinstruments.os.connectivity.bluetooth.IHUDBTService;

/**
 * Class to send http request via BT
 * Is it using SPP class: HUDSPPService
 * and HUDConnectivityPhoneConnection? That seem containing the
 * HUDSPPService
 * But HUDConnectivityPhoneConnection is contained in HUDWebService
 * This las one contain HUDConnectivityManager that is using to send the messages
 *
 * HUDWebService is declared in the manifest
 * Uses: URLConnectionHUDAdaptor
 */
public class HUDHttpBTConnection implements IHUDBTConsumer {
    //public static IHUDBTService b = null;
    public static IHUDBTService ihudbtService = null;

    private static IHUDConnectivity e = null;

    //True when network connected?
    private static boolean f = false;

    private static boolean g = false;

    public Context context = null;

    public BroadcastReceiver c = new BroadcastReceiver() {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            try {
                HUDHttpBTConnection.this.a(context);
                //} catch (InterruptedException e2) {
            } catch (Exception e2) {
            }
        }
    };

    private final String TAG = getClass().getSimpleName();

    private boolean h = true;

    public HUDHttpBTConnection(Context paramContext, IHUDConnectivity paramIHUDConnectivity) {
        if (paramIHUDConnectivity == null)
            throw new NullPointerException("HUDHttpBTConnection hudConnectivity can't be null");
        this.h = false;
        this.context = paramContext;
        e = paramIHUDConnectivity;
    }

    /**
     * This method is used to send and array of bytes
     * Possiby this method is invoqued when getting an http request from a message coming from the Goggles?
     * @param paramArrayOfbyte
     * @return
     */
    private boolean sendBytes(byte[] paramArrayOfbyte) {
        boolean bool = true;
        try {
            IHUDConnectivity.NetworkEvent networkEvent = null;
            //Byte 3 not usually to 2
            //Possibly 3 is used in some kind of ack
            if (HUDBTHeaderFactory.checkByte3(paramArrayOfbyte) == 3) {
                if (HUDBTHeaderFactory.e(paramArrayOfbyte) == 2) {
                    boolean bool1 = bool;
                    if (this.h) {
                        boolean bool2;
                        if (HUDBTHeaderFactory.f(paramArrayOfbyte) == 1) {
                            bool2 = true;
                        } else {
                            bool2 = false;
                        }
                        bool1 = bool;
                        if (bool2 != g) {
                            StringBuilder stringBuilder = new StringBuilder("Network state went from ");
                            stringBuilder.append(g).append(" to ").append(bool2);
                            g = bool2;
                            IHUDConnectivity iHUDConnectivity = e;
                            if (g) {
                                networkEvent = IHUDConnectivity.NetworkEvent.REMOTE_WEB_GAINED;
                            } else {
                                networkEvent = IHUDConnectivity.NetworkEvent.REMOTE_WEB_LOST;
                            }
                            //HUDSPPService implements IHUDConnectivity
                            //iHUDConnectivity.a(networkEvent, a());
                            iHUDConnectivity.onNetworkEvent(networkEvent, HUDHttpBTConnection.this.a());
                            bool1 = bool;
                        }
                    }
                    return bool1;
                }
                return false;
            }
            //!!!
            //if (HUDBTHeaderFactory.c((byte[])networkEvent) == 2) {
            //Byte 3 not usually to 2
            if (HUDBTHeaderFactory.checkByte3((byte[])paramArrayOfbyte) == 2) {
                if (HUDBTHeaderFactory.e((byte[])paramArrayOfbyte) == 1) {
                    boolean bool1 = bool;
                    if (!this.h) {
                        a(HUDBTHeaderFactory.getByte1((byte[])paramArrayOfbyte));
                        bool1 = bool;
                    }
                    return bool1;
                }
                return false;
            }
        } catch (Exception exception) {
            return false;
        }
        return false;
    }

    /**
     * Why using a paramByte, because there is a
     * method with byte array, but this a method with byte array uses this
     * @param b2
     */
    private void a(byte b2) {
        synchronized (this) {
            if (ihudbtService != null && !this.h && ihudbtService.getConnectionState() == IHUDConnectivity.ConnectionState.CONNECTED) {
                try {
                    synchronized (ihudbtService) {
                        sendHttpResponseToBT(HUDBTHeaderFactory.a(f, b2), null, null);
                    }
                } catch (Exception e2) {
                }
            }
        }
    }

    public static boolean a() {
        return (f || g);
    }

    private void b() {
        synchronized (this) {
            if (ihudbtService != null && !this.h && ihudbtService.getConnectionState() == IHUDConnectivity.ConnectionState.CONNECTED) {
                try {
                    synchronized (ihudbtService) {
                        sendHttpResponseToBT(HUDBTHeaderFactory.constructHeader(f), null, null);
                    }
                } catch (Exception e2) {
                }
            }
        }
    }

    /**
     * This is the sending function. Start analysis with this one
     * @param header
     * @param payload
     * @param body
     */
    private void sendHttpResponseToBT(byte[] header, byte[] payload, byte[] body) throws Exception {
        if (header != null) {
            new StringBuilder("header size:").append(header.length);
        }
        if (payload != null) {
            new StringBuilder("payload size:").append(payload.length);
        }
        if (body != null) {
            new StringBuilder("body size:").append(body.length);
        }
        if (ihudbtService == null) {
            throw new Exception("sendData: HUDBTService is null");
        }

        //This is the point where sends data
        HUDBTBaseService.OutputStreamContainer outputStreamContainer=ihudbtService.getOutputStreamCont();

        if (outputStreamContainer == null) {
            throw new Exception(this.TAG + ":Couldn't obtain a new OutputSreamContainer");
        }

        try {
            if (header != null) {
                try {
                    if (header.length > 0) {
                        ihudbtService.a(outputStreamContainer, header);
                    }
                } catch (Exception e2) {
                    throw e2;
                }
            }
            if (payload != null && payload.length > 0) {
                ihudbtService.a(outputStreamContainer, payload);
            }
            if (body != null && body.length > 0) {
                ihudbtService.a(outputStreamContainer, body);
            }
            ihudbtService.a(outputStreamContainer);
        } catch (Throwable th) {
            ihudbtService.a(outputStreamContainer);
            throw th;
        }

    }

    /**
     * This is calling b(,,)
     * @param header
     * @param bArr2
     * @param bArr3
     * @return
     */
    private boolean callHttp(byte[] header, byte[] bArr2, byte[] bArr3) {
        boolean z;
        int lB = 0;
        if (HUDBTHeaderFactory.checkByte3(header) == 2) {
            try {
                HUDHttpRequest hUDHttpRequest = new HUDHttpRequest(bArr2);
                hUDHttpRequest.a(bArr3);
                //Post?
                if (HUDBTHeaderFactory.checkByte3(header) == 3) {
                    URLConnectionHUDAdaptor.a(hUDHttpRequest);
                    z = true;
                } else {
                    byte b2 = HUDBTHeaderFactory.getByte1(header);
                    HUDHttpResponse hudHttpResponse = URLConnectionHUDAdaptor.a(hUDHttpRequest);
                    z = true;
                    if (!this.h) {
                        z = true;
                        if (ihudbtService != null) {
                            byte[] bytes = hudHttpResponse.toString().getBytes();
                            int length = bytes.length;
                            if (hudHttpResponse.a()) {
                                //Lenght of the htt response
                                lB = hudHttpResponse.b.length;
                            }
                            //payload, body
                            byte[] a3 = HUDBTHeaderFactory.adquireHeader(length, lB);
                            HUDBTHeaderFactory.a(a3, b2);

                            //Send header, the response and body
                            sendHttpResponseToBT(a3, bytes, hudHttpResponse.b);
                            z = true;
                        }
                    }
                }
            } catch (Exception e2) {
                z = true;
                if (HUDBTHeaderFactory.checkByte3(header) == 2) {
                    z = true;
                    if (ihudbtService != null) {
                        try {
                            sendHttpResponseToBT(HUDBTHeaderFactory.a(), null, null);
                            z = true;
                        } catch (Exception e3) {
                            z = true;
                        }
                    }
                }
            }
        } else {
            z = true;
            if (HUDBTHeaderFactory.checkByte3(header) != 1) {
                z = false;
            }
        }
        return z;
    }

    /**
     * New function to try to implement sendWebRequest
     * @param hUDHttpRequest
     * @return
     */
    public boolean callHttp(HUDHttpRequest hUDHttpRequest) {
        boolean z;
        int i = 0;
        //if (HUDBTHeaderFactory.c(bArr) == 2) {
        try {

            //hUDHttpRequest.a(bArr3);
            //if (HUDBTHeaderFactory.c(bArr) == 3) {
            //    URLConnectionHUDAdaptor.a(hUDHttpRequest);
            //    z = true;
            //} else {
            byte[] header = new byte[0];
            byte b2 = HUDBTHeaderFactory.getByte1(header);
            //HUDHttpResponse a2 = URLConnectionHUDAdaptor.a(hUDHttpRequest);
            z = true;
            if (!this.h) {
                z = true;
                if (ihudbtService != null) {
                    byte[] bytes = hUDHttpRequest.toString().getBytes();
                    int length = bytes.length;
                    if (hUDHttpRequest.a()) {
                        i = hUDHttpRequest.b.length;
                    }
                    byte[] a3 = HUDBTHeaderFactory.adquireHeader2(length, i);
                    HUDBTHeaderFactory.a(a3, b2);
                    //sendHttpResponseToBT(a3, bytes, a2.b);
                    //sendHttpResponseToBT(null, bytes, null);
                    sendHttpResponseToBT(a3, bytes, null);
                    z = true;
                }
            }
            //}
        } catch (Exception e2) {
            z = true;
            //if (HUDBTHeaderFactory.c(bArr) == 2) {
            z = true;
            if (ihudbtService != null) {
                try {
                    sendHttpResponseToBT(HUDBTHeaderFactory.a(), null, null);
                    z = true;
                } catch (Exception e3) {
                    z = true;
                }
            }
            //}
        }
        //}
        //} else {
        //    z = true;
        //    if (HUDBTHeaderFactory.c(bArr) != 1) {
        //        z = false;
        //    }
        //}
        return z;

    }

    public final void a(Context paramContext) {
        boolean bool1;
        boolean bool = f;
        //NetworkInfo networkInfo = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
        //!!!
        NetworkInfo networkInfo = ((ConnectivityManager)paramContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            bool1 = true;
        } else {
            bool1 = false;
        }
        f = bool1;
        if (bool != f && e != null) {
            IHUDConnectivity.NetworkEvent networkEvent;
            IHUDConnectivity iHUDConnectivity = e;
            if (f) {
                networkEvent = IHUDConnectivity.NetworkEvent.LOCAL_WEB_GAINED;
            } else {
                networkEvent = IHUDConnectivity.NetworkEvent.LOCAL_WEB_LOST;
            }
            //!!!
            //iHUDConnectivity.a(networkEvent, a());
            iHUDConnectivity.onNetworkEvent(networkEvent, a());
            b();
        }
    }

    /**
     * This is the public function that is calling c
     * @param header
     * @param payload
     * @param body
     * @return
     */
    public final boolean sendHttpRequest(byte[] header, byte[] payload, byte[] body) {
        boolean bool1 = false;
        if (header == null)
            return bool1;
        //This is some kind of ack?
        if (HUDBTHeaderFactory.checkByte4(header) == 3)
            return sendBytes(header);
        boolean bool2 = bool1;
        if (HUDBTHeaderFactory.checkByte4(header) == 2) {
            bool2 = bool1;
            if (payload.length > 0)
                bool2 = callHttp(header, payload, body);
        }
        return bool2;
    }
}
