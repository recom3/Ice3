package com.recom3.snow3.mobilesdk.phonecontrol;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.recom3.snow3.mobilesdk.hudconnectivity.ConnectHelper;
import com.recom3.snow3.mobilesdk.messages.PhoneMessage;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Recom3 on 29/03/2022.
 * Control of Phone functionality
 */

public class PhoneControlService extends Service {

    TelephonyManager telephonyManager;//f2658a
    private boolean e = false;
    private boolean f = false;
    private boolean g = false;


    PhoneStateListener b = new PhoneStateListener(/*this*/) {
        public void onCallStateChanged(int param1Int, String param1String) {
            Log.d("PhoneControlService", "currentState: " +
                    PhoneControlService.this.telephonyManager.getCallState() + " newState: " + param1Int + " number: " + param1String);
            if (param1Int == 2) {
                String str = (new PhoneMessage((Enum)PhoneMessage.Status.STARTED, new String[] { param1String })).a();
                //ConnectHelper.a((Context)PhoneControlService.this.a, str);
            }
            if (param1Int == 0) {
                param1String = (new PhoneMessage((Enum)PhoneMessage.Status.ENDED, new String[] { param1String })).a();
                //ConnectHelper.a((Context)PhoneControlService.this.a, param1String);
            }
        }
    };

    //This is for received SMS
    private BroadcastReceiver h = new BroadcastReceiver() {

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {

            String str = (String) intent.getExtras().get("message");
            PhoneMessage phoneMessage = new PhoneMessage(str);
            if (phoneMessage.mode == PhoneMessage.Type.CONTROL) {

                final PhoneControlService phoneControlService = PhoneControlService.this;
                switch (AnonymousClass8.f2667a[((PhoneMessage.Control) phoneMessage.type).ordinal()]) {
                    case 1:
                        if (phoneControlService.telephonyManager.getCallState() == 1) {
                            if (phoneControlService.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") == -1) {
                                Log.i("PhoneControlService", "MODIFY_PHONE_STATE is PERMISSION_DENIED");
                            }
                            try {
                                TelephonyManager telephonyManager = (TelephonyManager) phoneControlService.getSystemService("phone");
                                Log.i("PhoneControlService", "Get getTeleService...");
                                Method declaredMethod = Class.forName(telephonyManager.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
                                declaredMethod.setAccessible(true);
                                //!recom3
                                //((ITelephony) declaredMethod.invoke(telephonyManager, new Object[0])).answerRingingCall();
                                /*
                                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                                Method m1 = tm.getClass().getDeclaredMethod("getITelephony");
                                m1.setAccessible(true);
                                Object iTelephony = m1.invoke(tm);
                                Method m2 = iTelephony.getClass().getDeclaredMethod("silenceRinger");
                                Method m3 = iTelephony.getClass().getDeclaredMethod("endCall");
                                m2.invoke(iTelephony);
                                m3.invoke(iTelephony);
                                */
                                Method m1 = telephonyManager.getClass().getDeclaredMethod("getITelephony");
                                m1.setAccessible(true);
                                Object iTelephony = m1.invoke(telephonyManager);
                                Method m2 = iTelephony.getClass().getDeclaredMethod("answerRingingCall");
                                m2.invoke(iTelephony);
                                break;
                            } catch (Exception e) {
                                Log.i("PhoneControlService", e.getMessage(), e);
                                Log.i("AutoAnswer", "Error trying to answer using telephony service.  Falling back to headset.");
                                if (Build.VERSION.SDK_INT <= 19) {
                                    //!recom3
                                    //phoneControlService.a();
                                    break;
                                } else {
                                    new Thread(new Runnable() { // from class: com.reconinstruments.mobilesdk.phonecontrol.PhoneControlService.5
                                        @Override // java.lang.Runnable
                                        public void run() {
                                            try {
                                                Runtime.getRuntime().exec("input keyevent " + Integer.toString(79));
                                            } catch (IOException e2) {
                                                Log.i("PhoneControlService", e2.getMessage(), e2);
                                                //!recom3
                                                //PhoneControlService.this.a();
                                            }
                                        }
                                    }).start();
                                    break;
                                }
                            }
                        }
                        break;
                        /*
                    case 3:
                        String str2 = phoneMessage.d;
                        Intent intent2 = new Intent("android.intent.action.CALL");
                        intent2.setData(Uri.parse("tel:" + str2));
                        intent2.addFlags(268435456);
                        phoneControlService.startActivity(intent2);
                        break;
                    case 4:
                        String str3 = phoneMessage.d;
                        String str4 = phoneMessage.g;
                        PendingIntent broadcast = PendingIntent.getBroadcast(phoneControlService, 0, new Intent("SMS_SENT"), 0);
                        PendingIntent broadcast2 = PendingIntent.getBroadcast(phoneControlService, 0, new Intent("SMS_DELIVERED"), 0);
                        phoneControlService.registerReceiver(new BroadcastReceiver() { // from class: com.reconinstruments.mobilesdk.phonecontrol.PhoneControlService.3
                            @Override // android.content.BroadcastReceiver
                            public void onReceive(Context context2, Intent intent3) {
                                Log.i("PhoneControlService", "SMS send result: " + getResultCode());
                                switch (getResultCode()) {
                                    case -1:
                                        Log.i("PhoneControlService", "send ok");
                                        return;
                                    case 0:
                                    default:
                                        return;
                                    case 1:
                                        Log.i("PhoneControlService", "Generic failure");
                                        return;
                                    case 2:
                                        Log.i("PhoneControlService", "Radio off");
                                        return;
                                    case 3:
                                        Log.i("PhoneControlService", "Null PDU");
                                        return;
                                    case 4:
                                        Log.i("PhoneControlService", "No service");
                                        return;
                                }
                            }
                        }, new IntentFilter("SMS_SENT"));
                        phoneControlService.registerReceiver(new BroadcastReceiver() { // from class: com.reconinstruments.mobilesdk.phonecontrol.PhoneControlService.4
                            @Override // android.content.BroadcastReceiver
                            public void onReceive(Context context2, Intent intent3) {
                                Log.i("PhoneControlService", "SMS delivery result: " + getResultCode());
                                switch (getResultCode()) {
                                    case -1:
                                        Log.i("PhoneControlService", "SMS Delivered successfully");
                                        return;
                                    case 0:
                                        Log.i("PhoneControlService", "SMS not delivered");
                                        return;
                                    default:
                                        return;
                                }
                            }
                        }, new IntentFilter("SMS_DELIVERED"));
                        SmsManager.getDefault().sendTextMessage(str3, null, str4, broadcast, broadcast2);
                        break;
                    case 5:
                        TelephonyManager telephonyManager2 = (TelephonyManager) phoneControlService.getSystemService("phone");
                        try {
                            Log.i("PhoneControlService", "Get getTeleService...");
                            Method declaredMethod2 = Class.forName(telephonyManager2.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
                            declaredMethod2.setAccessible(true);
                            ((ITelephony) declaredMethod2.invoke(telephonyManager2, new Object[0])).endCall();
                            break;
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            Log.e("PhoneControlService", "FATAL ERROR: could not connect to telephony subsystem");
                            Log.e("PhoneControlService", "Exception object: " + e2);
                            break;
                        }
                        */
                }

            }
            Log.i("PhoneControlService", "message received: " + str);

        }
    };

    BroadcastReceiver c = new BroadcastReceiver() { // from class: com.reconinstruments.mobilesdk.phonecontrol.PhoneControlService.6
        /* JADX WARN: Code restructure failed: missing block: B:15:0x009f, code lost:
            if (r12.equals("") != false) goto L23;
         */
        /* JADX WARN: Code restructure failed: missing block: B:20:0x00b2, code lost:
            if (r11.equals("") != false) goto L22;
         */
        @Override // android.content.BroadcastReceiver
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void onReceive(Context context, Intent intent) {
            String str;
            Log.i("PhoneControlService", "received message.");
            Log.i("PhoneControlService", "extras: " + intent.getExtras().toString());
            if ((intent.hasExtra("state") ? intent.getStringExtra("state") : null).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String str2 = "";
                if (intent.getExtras() != null) {
                    String stringExtra = intent.getStringExtra("incoming_number");
                    Cursor query = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(stringExtra)), new String[]{"display_name"}, null, null, null);
                    if (query.getCount() > 0) {
                        query.moveToFirst();
                        str2 = stringExtra;
                        str = query.getString(query.getColumnIndexOrThrow("display_name"));
                    } else {
                        str2 = stringExtra;
                        str = "";
                    }
                } else {
                    str = "";
                }
                String str3 = str2 != null ? str2 : "Unknown";
                String str4 = str != null ? str : "Unknown";
                ConnectHelper.a(context, new PhoneMessage(PhoneMessage.Status.RINGING, str3, str4).a());
                Log.i("PhoneControlService", "call from number " + str3 + " name " + str4);
                Log.i("PhoneControlService", "call from " + str3 + " name " + str4);
            }
        }
    };

    static class AnonymousClass8 {

        static final int[] f2667a = new int[PhoneMessage.Control.values().length];

        static {
            try {
                f2667a[PhoneMessage.Control.ANSWER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f2667a[PhoneMessage.Control.REJECT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f2667a[PhoneMessage.Control.START.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f2667a[PhoneMessage.Control.SENDSMS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f2667a[PhoneMessage.Control.END.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public final void a() {
        Intent putExtra = new Intent("android.intent.action.MEDIA_BUTTON").putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(0, 79));
        Intent putExtra2 = new Intent("android.intent.action.MEDIA_BUTTON").putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(1, 79));
        sendOrderedBroadcast(putExtra, "android.permission.CALL_PRIVILEGED");
        sendOrderedBroadcast(putExtra2, "android.permission.CALL_PRIVILEGED");
    }

    //@Nullable
    @Override
    //public IBinder onBind(Intent intent) {
    //    return null;
    //}
    public IBinder onBind(Intent paramIntent) {
        return (IBinder)new Binder();
    }
}
