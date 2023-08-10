package com.reconinstruments.os.connectivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Recom3 on 15/05/2023.
 */

public class HUDConnectivityManager implements IHUDConnectivity {
    private static final String TAG = "HUDConnectivityManager";

    //public HUDConnectivityManager.ConnectivityHandler a = null;
    public HUDConnectivityManager.ConnectivityHandler connectivityHandler = null;

    //public IHUDConnectivityConnection b = null;
    public com.reconinstruments.os.connectivity.IHUDConnectivityConnection hudConnectivityConnection = null;

    public String c = "";
    public IHUDConnectivityConnection f2715b = null;

    //public IHUDConnectivity$ConnectionState d = IHUDConnectivity$ConnectionState.e;
    public IHUDConnectivity.ConnectionState connectionState = IHUDConnectivity.ConnectionState.DISCONNECTED;
    public IHUDConnectivity.ConnectionState d = IHUDConnectivity.ConnectionState.DISCONNECTED;

    public boolean e = false;

    public boolean f = false;

    private boolean g = false;

    /* renamed from: com.reconinstruments.os.connectivity.HUDConnectivityManager$1  reason: invalid class name */
    /* loaded from: recon_engage_jar.jar:com/reconinstruments/os/connectivity/HUDConnectivityManager$1.class */
    /* synthetic */ class AnonymousClass1 {

        /* renamed from: a  reason: collision with root package name */
        /*static*/ final /* synthetic */ int[] f2716a = new int[IHUDConnectivity.NetworkEvent.values().length];

        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:13:0x003a -> B:23:0x002a). Please submit an issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:15:0x003e -> B:21:0x001f). Please submit an issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:17:0x0042 -> B:19:0x0014). Please submit an issue!!! */
        /*
        static {
            try {
                connectivityHandler[IHUDConnectivity.NetworkEvent.LOCAL_WEB_GAINED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                connectivityHandler[IHUDConnectivity.NetworkEvent.LOCAL_WEB_LOST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                connectivityHandler[IHUDConnectivity.NetworkEvent.REMOTE_WEB_GAINED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                connectivityHandler[IHUDConnectivity.NetworkEvent.REMOTE_WEB_LOST.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
        */
    }

    public HUDConnectivityManager() {

    }

    @Override
    public void onConnectionStateChanged(ConnectionState paramConnectionState) {

    }

    @Override
    public void onNetworkEvent(NetworkEvent paramNetworkEvent, boolean hasNetworkAccess) {
        /*
        int i = 1;
        new StringBuilder("onNetworkEvent:").append(networkEvent).append(" hasNetworkAccess:").append(hasNetworkAccess);
        switch (AnonymousClass1.f2716a[networkEvent.ordinal()]) {
            case 1:
                this.e = true;
                break;
            case 2:
                this.e = false;
                break;
            case 3:
                this.f = true;
                break;
            case 4:
                this.f = false;
                break;
        }
        ConnectivityHandler connectivityHandler = this.connectivityHandler;
        int ordinal = networkEvent.ordinal();
        if (!a()) {
            i = 0;
        }
        connectivityHandler.obtainMessage(2, ordinal, i).sendToTarget();
        */
    }

    @Override
    public void onDeviceName(String paramString) {
        this.c = paramString;
        Message obtainMessage = this.connectivityHandler.obtainMessage(4);
        Bundle bundle = new Bundle();
        bundle.putString("device_name", paramString);
        obtainMessage.setData(bundle);
        this.connectivityHandler.sendMessage(obtainMessage);
    }

    public enum CHANNEL {
        COMMAND_CHANNEL, FILE_CHANNEL, OBJECT_CHANNEL;
    }

    public final boolean hasNetworkAccess() {
        boolean z;
        try {
            z = this.f2715b.hasNetworkAccess();
            //} catch (RemoteException e) {
        } catch (Exception e) {
            z = false;
        }
        return z;
    }

    public final void a(IHUDConnectivity.ConnectionState paramIHUDConnectivity$ConnectionState) {
        boolean bool;
        (new StringBuilder("onConnectionStateChanged:")).append(paramIHUDConnectivity$ConnectionState);
        this.connectionState = paramIHUDConnectivity$ConnectionState;
        if (paramIHUDConnectivity$ConnectionState == ConnectionState.CONNECTED) {
            bool = true;
        } else {
            bool = false;
        }
        this.g = bool;
        this.connectivityHandler.obtainMessage(1, paramIHUDConnectivity$ConnectionState.ordinal(), -1).sendToTarget();
    }

    public final void sendWebRequest(com.reconinstruments.os.connectivity.http.HUDHttpRequest hudHttpRequest) {
        //This implementation seems not OK
        /*
        String paramString = "";
        try {
            paramString = new String(hudHttpRequest.b, "UTF-8");
        }
        catch (Exception ex)
        {
            Log.e("sendWebRequest", ex.getMessage());
        }
        this.c = paramString;
        Message message = this.connectivityHandler.obtainMessage(4);
        Bundle bundle = new Bundle();
        bundle.putString("device_name", paramString);
        message.setData(bundle);
        this.connectivityHandler.sendMessage(message);
        */
        //Better use something like this:

        hudConnectivityConnection.sendWebRequest(hudHttpRequest);
    }

    public static class ConnectivityHandler extends Handler {

        public ArrayList<WeakReference<IHUDConnectivity>> a = new ArrayList<WeakReference<IHUDConnectivity>>();

        public ConnectivityHandler(HUDConnectivityManager paramHUDConnectivityManager) {}

        /* renamed from: a  reason: collision with root package name */
        public ArrayList<WeakReference<IHUDConnectivity>> f2717a;

        private ConnectivityHandler() {
            this.f2717a = new ArrayList<>();
        }

        public /* synthetic */ ConnectivityHandler(HUDConnectivityManager hUDConnectivityManager, byte b2) {
            this();
        }

        public final WeakReference<IHUDConnectivity> a(IHUDConnectivity iHUDConnectivity) {
            WeakReference<IHUDConnectivity> weakReference;
            synchronized (this.f2717a) {
                int i = 0;
                while (true) {
                    if (i >= this.f2717a.size()) {
                        weakReference = null;
                        break;
                    } else if (this.f2717a.get(i).get() == iHUDConnectivity) {
                        weakReference = this.f2717a.get(i);
                        break;
                    } else {
                        i++;
                    }
                }
            }
            return weakReference;

        }

        public final boolean b(IHUDConnectivity iHUDConnectivity) {
            /*
            boolean bool = false;
            if (paramIHUDConnectivity != null && a(paramIHUDConnectivity) == null) {
                ArrayList<WeakReference<IHUDConnectivity>> arrayList = this.a;
                //monitor enter ClassFileLocalVariableReferenceExpression{type=ObjectType{java/util/ArrayList<ObjectType{java/lang/ref/WeakReference<ObjectType{com/reconinstruments/os/connectivity/IHUDConnectivity}>}>}, name=null}
                try {
                    ArrayList<WeakReference<IHUDConnectivity>> arrayList1 = this.a;
                    WeakReference<IHUDConnectivity> weakReference = new WeakReference((IHUDConnectivity)paramIHUDConnectivity);
                    arrayList1.add(weakReference);
                //monitor exit ClassFileLocalVariableReferenceExpression{type=ObjectType{java/util/ArrayList<ObjectType{java/lang/ref/WeakReference<ObjectType{com/reconinstruments/os/connectivity/IHUDConnectivity}>}>}, name=null}
                    bool = true;
                } finally {}
            }
            return bool;
            */
            boolean z = false;
            if (iHUDConnectivity != null && a(iHUDConnectivity) == null) {
                synchronized (this.f2717a) {
                    this.f2717a.add(new WeakReference<>(iHUDConnectivity));
                }
                z = true;
            }
            return z;
        }

        public void handleMessage(Message message) {
            if (this.f2717a.size() == 0) {
                return;
            }
            switch (message.what) {
                case 1:
                    new StringBuilder("MESSAGE_BT_STATE_CHANGE: ").append(message.arg1);
                    synchronized (this.f2717a) {
                        int i = 0;
                        while (i < this.f2717a.size()) {
                            IHUDConnectivity iHUDConnectivity = this.f2717a.get(i).get();
                            if (iHUDConnectivity != null) {
                                iHUDConnectivity.onConnectionStateChanged(IHUDConnectivity.ConnectionState.values()[message.arg1]);
                            } else {
                                this.f2717a.remove(i);
                                i--;
                            }
                            i++;
                        }
                    }
                    return;
                case 2:
                    new StringBuilder("MESSAGE_NETWORK_EVENT: ").append(message.arg1);
                    synchronized (this.f2717a) {
                        int i2 = 0;
                        while (i2 < this.f2717a.size()) {
                            IHUDConnectivity iHUDConnectivity2 = this.f2717a.get(i2).get();
                            if (iHUDConnectivity2 != null) {
                                iHUDConnectivity2.onNetworkEvent(IHUDConnectivity.NetworkEvent.values()[message.arg1], message.arg2 == 1);
                            } else {
                                this.f2717a.remove(i2);
                                i2--;
                            }
                            i2++;
                        }
                    }
                    return;
                case 3:
                default:
                    return;
                case 4:
                    new StringBuilder("MESSAGE_DEVICE_NAME: ").append(message.getData().getString("device_name"));
                    synchronized (this.f2717a) {
                        int i3 = 0;
                        while (i3 < this.f2717a.size()) {
                            IHUDConnectivity iHUDConnectivity3 = this.f2717a.get(i3).get();
                            if (iHUDConnectivity3 != null) {
                                iHUDConnectivity3.onDeviceName(message.getData().getString("device_name"));
                            } else {
                                this.f2717a.remove(i3);
                                i3--;
                            }
                            i3++;
                        }
                    }
                    return;
            }
        }
    }

    /**
     * JC: 16.05.2023
     * Connect all the threads
     * @param paramString       uuid? (viene del PairingTabFragment)
     * @param paramInt          number of connection attemps?
     */
    public void connect(String paramString, int paramInt) {
        hudConnectivityConnection.connect(paramString, paramInt);
    }
}
