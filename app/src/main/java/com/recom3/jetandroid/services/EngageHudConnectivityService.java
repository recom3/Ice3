package com.recom3.jetandroid.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.recom3.jetandroid.models.Profile;
import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.recom3.snow3.mobilesdk.HUDConnectivityService;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDConnectivityService$Channel;
import com.recom3.snow3.mobilesdk.hudconnectivity.HUDStateUpdateListener;
import com.recom3.snow3.mobilesdk.hudsync.HUDInfo;
import com.recom3.snow3.mobilesdk.hudsync.HUDInfo$HudType;
import com.reconinstruments.mobilesdk.agps.AGpsModule;

import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Recom3 on 17/03/2022.
 */

public class EngageHudConnectivityService extends HUDConnectivityService {

    private static final String TAG = EngageHudConnectivityService.class.getSimpleName();//g

    private static HUDInfo o;

    //@a
    //Profile a;

    //@a
    //ServiceManager b;

    //@a
    //EngageNotificationManager c;

    //@a
    //HUDConfigManager d;

    //@a
    //EngageAnalytics e;

    boolean f;
    private AGpsModule aGpsModule;//i

    private boolean h = false;

    private ServiceConnection q = new ServiceConnection(/*this*/) {
        public void onServiceConnected(ComponentName param1ComponentName, IBinder param1IBinder) {
            //Log.b(EngageHudConnectivityService.b(), "Phone control service connected");
            //this.a.f = true;
        }

        public void onServiceDisconnected(ComponentName param1ComponentName) {
            //Log.i(EngageHudConnectivityService.b(), "Phone control service disconnected");
            //this.a.f = false;
        }
    };

    private HUDStateUpdateListener r = new HUDStateUpdateListener(/*this*/) {
        public final void onHUDStateUpdate(HUDStateUpdateListener.HUD_STATE param1HUD_STATE) {
            switch (//EngageHudConnectivityService.null.a[param1HUD_STATE.ordinal()]
                    param1HUD_STATE.ordinal()
                    ) {
                default:
                    return;
                case 1:
                    Log.i(TAG, "disconnected");
                    //EngageHudConnectivityService.a(this.a);
                    //if (EngageHudConnectivityService.b(this.a) > 0L)
                    //    this.a.e.a((EngageAnalyticsEvents.EVENT)EngageAnalyticsEvents.HUD.e, (System.currentTimeMillis() - EngageHudConnectivityService.b(this.a)) / 1000L);
                    //EngageHudConnectivityService.a(this.a, 0L);
                case 2:
                    Log.i(TAG, "connecting");
                    //EngageHudConnectivityService.a(this.a, 0L);
                case 3:
                    break;
            }
            //Log.i(EngageHudConnectivityService.b(), "connected");
            //EngageHudConnectivityService.a(this.a, System.currentTimeMillis());
            //EngageHudConnectivityService.c(this.a);
        }
    };

    private final BroadcastReceiver s = new BroadcastReceiver(/*this*/) {
        public void onReceive(Context param1Context, Intent param1Intent) {
            HUDConnectivityMessage hUDConnectivityMessage = null;
            String str;
            if (param1Intent.getAction().equals("RECON_SMARTPHONE_CONNECTION_MESSAGE")) {
                Bundle bundle = param1Intent.getExtras();
                if (bundle != null) {
                    String str1 = bundle.getString("message");
                    if (str1 != null) {
                        //Log.i(EngageHudConnectivityService.b(), "mMODLiveReceiver receieved a message: " + str1);
                        //hUDConnectivityMessage = new HUDConnectivityMessage();
                        //public HUDConnectivityMessage(int paramInt, String paramString1, String paramString2, String paramString3, byte[] paramArrayOfbyte)
                        hUDConnectivityMessage = new HUDConnectivityMessage(0, EngageHudConnectivityService.b(""),
                                "INTENT_OLD_API_MESSAGE", "", str1.getBytes());
                        //hUDConnectivityMessage.e = "INTENT_OLD_API_MESSAGE";
                        //hUDConnectivityMessage.d = EngageHudConnectivityService.b();
                        //hUDConnectivityMessage.c = 0;
                        //hUDConnectivityMessage.g = str1.getBytes();
                        //Log.i(EngageHudConnectivityService.b(), "mMODLiveReceiver converting this message into HUDConnectivityMessage: " + hUDConnectivityMessage.toString());
                        EngageHudConnectivityService.this.a(hUDConnectivityMessage,
                                //!!!
                                //HUDConnectivityService.Channel.b
                                HUDConnectivityService$Channel.b
                        );
                    }
                }
                return;
            }
            //!!!
            //if (hUDConnectivityMessage.getAction().equals("INTENT_OLD_API_MESSAGE")) {
            if (hUDConnectivityMessage.getInfo().equals("INTENT_OLD_API_MESSAGE")) {
                Log.i(EngageHudConnectivityService.b(""), "mMODLiveReceiver receieved INTENT_OLD_API_MESSAGE");
                //!!!
                //str = EngageHudConnectivityService.a((Intent)hUDConnectivityMessage);
                str = hUDConnectivityMessage.getInfo();
                if (str != null) {
                    //!!!
                    //Intent intent = new Intent(EngageHudConnectivityService.a(str));
                    Intent intent = new Intent(str);
                    Bundle bundle = new Bundle();
                    bundle.putString("message", str);
                    intent.putExtras(bundle);
                    EngageHudConnectivityService.this.sendBroadcast(intent);
                    //Log.b(EngageHudConnectivityService.b(), "mMODLiveReceiver sent out the message: " + str);
                }
                return;
            }
            if (param1Intent.getAction().equals("AFTER_CONNECT")) {
                Log.i(EngageHudConnectivityService.b(""), "mMODLiveReceiver receieved INTENT_AFTER_CONNECT");
                //!!!
                //str = EngageHudConnectivityService.a((Intent)str);
                str = hUDConnectivityMessage.getInfo();
                if (str != null) {
                    HUDInfo hUDInfo = new HUDInfo();
                    //hUDInfo.a = HUDInfo.HudType.a(HUDInfo.a(str, "hud_type", "none"));
                    hUDInfo.a = HUDInfo$HudType.a(HUDInfo.a(str, "hud_type", "none"));
                    hUDInfo.b = HUDInfo.a(str, "firmware_version", "unknown");
                    hUDInfo.c = HUDInfo.a(str, "serial_number", "");

                    //EngageHudConnectivityService.a(this.a, hUDInfo);
                    EngageHudConnectivityService.this.a(
                            //!!!
                            //this.a,
                            hUDConnectivityMessage,
                            HUDConnectivityService$Channel.b);
                }
                return;
            }
            if (param1Intent.getAction().equals("com.reconinstruments.SPORTS_ACTIVITY")) {
                //Log.b(EngageHudConnectivityService.b(), "mMODLiveReceiver receieved INTENT_SPORTS_ACTIVITY");
                //String str1 = EngageHudConnectivityService.a((Intent)str);
                //if (str1 != null) {
                //ActivityInfo activityInfo = new ActivityInfo();
                //int i = ActivityInfo.a(str1, "sports_activity_type");
                //activityInfo.c = (Trip.SportId)ActivityInfo.b.get(i);
                //i = ActivityInfo.a(str1, "sports_activity_state");
                //activityInfo.d = (ActivityInfo.ActivityStatus) ActivityInfo.a.get(i);
                //EngageHudConnectivityService.a(this.a, activityInfo);
                //}
            }
        }
    };

    public static HUDInfo a() {
        return o;
    }

    private static void a(boolean paramBoolean) {
        /*
        if (AuthenticationManager.d()) {
            UserEdit userEdit = new UserEdit();
            userEdit.a.c(paramBoolean);
            AuthenticationManager.a(userEdit, null);
        }
        */
    }

    private static String b(String paramString) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            //this();
            StringReader stringReader = new StringReader(paramString);
            //this(paramString);
            inputSource.setCharacterStream(stringReader);
            paramString = documentBuilder.parse(inputSource).getElementsByTagName("recon").item(0).getAttributes().getNamedItem("intent").getNodeValue();
        } catch (Exception exception) {
            Log.e(TAG, "Failed to parse xml", exception);
            exception = null;
        }
        //return (String)exception;
        return paramString;
    }

    public void onCreate() {
        Log.i(TAG, "onCreate()");
        super.onCreate();

        //!!!
        //((BaseApp) getApplication()).a(this);

        this.aGpsModule = new AGpsModule(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.reconinstruments.mobilesdk.agps.tophone.SET_AGPS_LOCATION_UPDATE_PERIOD");
        intentFilter.addAction("RECON_LOCATION_RELAY");
        AGpsModule.context.registerReceiver(aGpsModule.broadcastReceiver, intentFilter);
        Log.i(AGpsModule.TAG, "start listening");

    }

    @Override // com.reconinstruments.mobilesdk.hudconnectivity.HUDConnectivityService, android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.i(TAG, "onStartCommand()");
        Log.i(TAG, "onStartCommand()");
        //!!!recom3
        /*
        String a2 = Profile.a();
        if (a2 != null && !this.h && BTUtil.a(a2)) {
            Log.b(g, "Attempting to reconnect to device: " + a2);
            a(HUDConnectivityService.DeviceType.ANDROID, a2, 1);
        }
        */
        return super.onStartCommand(intent, i, i2);
    }


    public class LocalBinder extends Binder {
        public EngageHudConnectivityService getService() {
            return EngageHudConnectivityService.this;
        }
    }
}