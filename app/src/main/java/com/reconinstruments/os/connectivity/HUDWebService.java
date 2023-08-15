package com.reconinstruments.os.connectivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by Recom3 on 15/05/2023.
 */

public class HUDWebService extends Service {

    private static final String a = HUDWebService.class.getName();
    private static final String f2143a = HUDWebService.class.getName();

    //private HUDStateUpdateListener b;
    private HUDStateUpdateListener hudStateUpdateListener;

    //private HUDConnectivityManager c;
    private HUDConnectivityManager hudConnectivityManager;

    private final IHUDConnectivity d = new IHUDConnectivity() {
        @Override
        public void onConnectionStateChanged(ConnectionState paramConnectionState) {

        }

        @Override
        public void onNetworkEvent(NetworkEvent paramNetworkEvent, boolean paramBoolean) {

        }

        @Override
        public void onDeviceName(String paramString) {

        }

        public final void a(IHUDConnectivity.ConnectionState param1ConnectionState) {
            Log.d(HUDWebService.this.a, "onConnectionStateChanged(): " + param1ConnectionState);
        }

        public final void a(IHUDConnectivity.NetworkEvent param1NetworkEvent, boolean param1Boolean) {
            Log.d(HUDWebService.this.a, "onNetworkEvent(): " + param1NetworkEvent + ", hasNetworkAccess: " + param1Boolean);
        }

        public final void a(String param1String) {
            Log.d(HUDWebService.this.a, "onDeviceName(): " + param1String);
        }
    };

    /* renamed from: com.reconinstruments.jetandroid.services.HUDWebService$3  reason: invalid class name */
    /* loaded from: recon_engage_jar.jar:com/reconinstruments/jetandroid/services/HUDWebService$3.class */
    public static /* synthetic */ class AnonymousClass3 {

        /* renamed from: a  reason: collision with root package name */
        static final /* synthetic */ int[] f2147a = new int[HUDStateUpdateListener.HUD_STATE.values().length];

        static {
            try {
                f2147a[HUDStateUpdateListener.HUD_STATE.CONNECTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f2147a[HUDStateUpdateListener.HUD_STATE.CONNECTING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f2147a[HUDStateUpdateListener.HUD_STATE.DISCONNECTED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public HUDWebService() {

        /*
        hudStateUpdateListener = new HUDStateUpdateListener() {
            @Override
            public void a(HUD_STATE paramHUDStateUpdateListener$HUD_STATE) {

            }
        };
        */

        /*
        super.onCreate();
        Log.d(f2143a, "onCreate()");
        this.hudConnectivityManager = new HUDConnectivityManager();
        this.hudStateUpdateListener = new HUDStateUpdateListener() { // from class: com.reconinstruments.jetandroid.services.HUDWebService.1
            @Override // com.reconinstruments.mobilesdk.hudconnectivity.HUDStateUpdateListener
            public final void a(HUDStateUpdateListener.HUD_STATE hud_state) {
                HUDWebService.a(HUDWebService.this, hud_state);
            }
        };
        this.hudStateUpdateListener.a(this);
        */
    }

    /**
     * This is the responding to the event in update listener
     * @param hud_state
     */
    static /* synthetic */ void a(HUDWebService hUDWebService, HUDStateUpdateListener.HUD_STATE hud_state) {
        String tag = a;
        Log.i(tag, "updateState" + hud_state);
        switch (AnonymousClass3.f2147a[hud_state.ordinal()]) {
            case 1:
                try {
                    HUDConnectivityManager hUDConnectivityManager = hUDWebService.hudConnectivityManager;
                    hUDConnectivityManager.connectivityHandler = new HUDConnectivityManager.ConnectivityHandler(hUDConnectivityManager, (byte) 0);
                    hUDConnectivityManager.hudConnectivityConnection = new HUDConnectivityPhoneConnection(hUDWebService, hUDConnectivityManager);
                    hUDConnectivityManager.hudConnectivityConnection.startListening();
                    HUDConnectivityManager hUDConnectivityManager2 = hUDWebService.hudConnectivityManager;
                    IHUDConnectivity iHUDConnectivity = hUDWebService.d;
                    if (hUDConnectivityManager2.connectivityHandler != null) {
                        if (iHUDConnectivity == null) {
                            throw new IllegalArgumentException("Interface IHUDConnectivity you registered is null");
                        }
                        if (hUDConnectivityManager2.connectivityHandler.b(iHUDConnectivity)) {
                            iHUDConnectivity.onDeviceName(hUDConnectivityManager2.c);
                            iHUDConnectivity.onConnectionStateChanged(hUDConnectivityManager2.d);
                            iHUDConnectivity.onNetworkEvent(hUDConnectivityManager2.e ? IHUDConnectivity.NetworkEvent.LOCAL_WEB_GAINED : IHUDConnectivity.NetworkEvent.LOCAL_WEB_LOST, hUDConnectivityManager2.hasNetworkAccess());
                            iHUDConnectivity.onNetworkEvent(hUDConnectivityManager2.f ? IHUDConnectivity.NetworkEvent.REMOTE_WEB_GAINED : IHUDConnectivity.NetworkEvent.REMOTE_WEB_LOST, hUDConnectivityManager2.hasNetworkAccess());
                            return;
                        }
                        return;
                    }
                    return;
                } catch (Exception e) {
                    Log.d(f2143a, "Failed to init HUDConnectivityManager", e);
                    return;
                }
            case 2:
            default:
                return;
            case 3:
                hUDWebService.b();
                return;
        }
    }

    /**
     * Destroy called code?
     */
    private void b() {
        WeakReference<IHUDConnectivity> a2;
        try {
            this.hudConnectivityManager.f2715b.stopListening();
        } catch (Exception e) {
            Log.i(f2143a, "Failed to stop HUDConnectivityManager", e);
        }
        HUDConnectivityManager hUDConnectivityManager = this.hudConnectivityManager;
        IHUDConnectivity iHUDConnectivity = this.d;
        if (iHUDConnectivity == null) {
            throw new IllegalArgumentException("Interface IHUDConnectivity you unregistered is null");
        }
        HUDConnectivityManager.ConnectivityHandler connectivityHandler = hUDConnectivityManager.connectivityHandler;
        if (iHUDConnectivity == null || (a2 = connectivityHandler.a(iHUDConnectivity)) == null) {
            return;
        }
        synchronized (connectivityHandler.f2717a) {
            connectivityHandler.f2717a.remove(a2);
        }
    }

    public IBinder onBind(Intent paramIntent) {

        return this.mBinder;
    }

    public void onCreate() {
        super.onCreate();
        Log.i(f2143a, "onCreate()");
        this.hudConnectivityManager = new HUDConnectivityManager();
        this.hudStateUpdateListener = new HUDStateUpdateListener() { // from class: com.reconinstruments.jetandroid.services.HUDWebService.1
            @Override // com.reconinstruments.mobilesdk.hudconnectivity.HUDStateUpdateListener
            public final void a(HUDStateUpdateListener.HUD_STATE hud_state) {
                HUDWebService.a(HUDWebService.this, hud_state);
            }
        };
        this.hudStateUpdateListener.a(this);

        //JC: 15.05.2023
        boolean bForce = true;
        if(bForce)
        {
            connect();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(a, "onDestroy()");
        unregisterReceiver((BroadcastReceiver)this.hudStateUpdateListener);
        b();
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        Log.d(a, "onStartCommand()");
        //return 1;
        return START_STICKY;
    }

    private final IBinder mBinder = (IBinder)new LocalBinder();

    public class LocalBinder extends Binder {
        public HUDWebService getService() {
            return HUDWebService.this;
        }
    }

    public void connect()
    {
        try {
            HUDConnectivityManager hUDConnectivityManager = this.hudConnectivityManager;
            hUDConnectivityManager.connectivityHandler = new HUDConnectivityManager.ConnectivityHandler(hUDConnectivityManager, (byte) 0);
            hUDConnectivityManager.hudConnectivityConnection = new HUDConnectivityPhoneConnection(this, hUDConnectivityManager);
            hUDConnectivityManager.hudConnectivityConnection.startListening();
            HUDConnectivityManager hUDConnectivityManager2 = this.hudConnectivityManager;
            IHUDConnectivity iHUDConnectivity = this.d;
            if (hUDConnectivityManager2.connectivityHandler != null) {
                if (iHUDConnectivity == null) {
                    throw new IllegalArgumentException("Interface IHUDConnectivity you registered is null");
                }
                if (hUDConnectivityManager2.connectivityHandler.b(iHUDConnectivity)) {
                    iHUDConnectivity.onDeviceName(hUDConnectivityManager2.c);
                    iHUDConnectivity.onConnectionStateChanged(hUDConnectivityManager2.d);
                    iHUDConnectivity.onNetworkEvent(hUDConnectivityManager2.e ? IHUDConnectivity.NetworkEvent.LOCAL_WEB_GAINED : IHUDConnectivity.NetworkEvent.LOCAL_WEB_LOST, hUDConnectivityManager2.hasNetworkAccess());
                    iHUDConnectivity.onNetworkEvent(hUDConnectivityManager2.f ? IHUDConnectivity.NetworkEvent.REMOTE_WEB_GAINED : IHUDConnectivity.NetworkEvent.REMOTE_WEB_LOST, hUDConnectivityManager2.hasNetworkAccess());
                    return;
                }
                return;
            }
            return;
        } catch (Exception e) {
            Log.d(f2143a, "Failed to init HUDConnectivityManager", e);
            return;
        }
    }

    /**
     * JC: 16.05.2023
     * Connect all the threads
     * @param paramString       uuid? (viene del PairingTabFragment)
     * @param paramInt          number of connection attemps?
     */
    public void connect(String paramString, int paramInt) {
        hudConnectivityManager.connect(paramString, paramInt);
    }
}
