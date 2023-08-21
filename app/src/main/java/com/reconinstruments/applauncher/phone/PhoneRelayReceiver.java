package com.reconinstruments.applauncher.phone;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.util.Log;

import com.recom3.snow3.AcceptCallActivity;
import com.recom3.snow3.LoginActivity;
import com.recom3.snow3.hudconnectivity.HUDConnectivityMessage;
import com.reconinstruments.modlivemobile.dto.message.PhoneMessage;

public class PhoneRelayReceiver extends BroadcastReceiver {
    public static final String TAG = "CallRelayReceiver";

    public PhoneRelayService mCallRelayreceiveservice;

    Context mContext;

    public PhoneRelayReceiver()
    {

    }

    public PhoneRelayReceiver(Context context)
    {
        this.mContext = context;
    }

    private void doWhenCallEnded(Context paramContext) {
        Log.i("CallRelayReceiver", "Phone call ended");
        this.mCallRelayreceiveservice.alreadyInACall = false;
        if (this.mCallRelayreceiveservice.mBLEServiceConnectionManager.isiOSMode()) {
            Log.i("CallRelayReceiver", "we are in iOS Mode");
            Intent intent1 = new Intent("RECON_IOS_BLUETOOTH_HEADSET_COMMAND");
            intent1.putExtra("command", 4);
            paramContext.sendBroadcast(intent1);
        }
        Intent intent = new Intent("RECON_SS1_HFP_COMMAND");
        intent.putExtra("command", 501);
        paramContext.sendBroadcast(intent);
    }

    private void doWhenCallStarted(Context paramContext) {
        Log.i("CallRelayReceiver", "Phone call Started");
        this.mCallRelayreceiveservice.alreadyInACall = true;
        if (this.mCallRelayreceiveservice.mBLEServiceConnectionManager.isiOSMode()) {
            Log.i("CallRelayReceiver", "we are in iOS Mode");
            Intent intent = new Intent("RECON_IOS_BLUETOOTH_HEADSET_COMMAND");
            intent.putExtra("command", 3);
            paramContext.sendBroadcast(intent);
        }
    }

    private void doWhenRefreshNeeded(Context paramContext) {
        Log.i("CallRelayReceiver", "Refresh needed");
        if (this.mCallRelayreceiveservice.mBLEServiceConnectionManager.isiOSMode()) {
            Log.i("CallRelayReceiver", "we are in iOS Mode");
            Intent intent = new Intent("RECON_IOS_BLUETOOTH_HEADSET_COMMAND");
            intent.putExtra("command", 5);
            paramContext.sendBroadcast(intent);
        }
    }

    public void onReceive(Context paramContext, Intent paramIntent) {
        String str;
        Log.v("CallRelayReceiver", "onReceive");
        if (paramIntent.getAction().equals("HFP_CALL_STATUS_CHANGED")) {
            Log.v("CallRelayReceiver", "HFP_CALL_STATUS_CHANGED");
            str = paramIntent.getStringExtra("event");
            if (str.equals("CALL_STARTED")) {
                Log.v("CallRelayReceiver", "received CALL_STARTED");
                doWhenCallStarted(paramContext);
                return;
            }
            if (str.equals("CALL_ENDED")) {
                Log.v("CallRelayReceiver", "received CALL_ENDED");
                doWhenCallEnded(paramContext);
            }
            return;
        }
        PhoneMessage phoneMessage = null;
        try {
            phoneMessage = new PhoneMessage(paramIntent.getStringExtra("message"));
        }
        catch (Exception ex)
        {
            Log.e("CallRelayReceiver", ex.getMessage());
        }
        if(phoneMessage==null) {
            try {
                byte[] byteArr = paramIntent.getByteArrayExtra("message");

                //HUDConnectivityMessage hudConnectivityMessage = new HUDConnectivityMessage(byteArr);
                //phoneMessage = new PhoneMessage(new String(hudConnectivityMessage.getData()));

                Bundle bundle = paramIntent.getExtras();
                if (bundle != null) {
                    HUDConnectivityMessage hudConnectivityMessage2 = new HUDConnectivityMessage(bundle.getByteArray("message"));
                    String strMsg = new String(hudConnectivityMessage2.getData());
                    phoneMessage = new PhoneMessage(strMsg);
                }

            } catch (Exception ex) {
                Log.e("CallRelayReceiver", ex.getMessage());
            }
        }
        try {
            Log.i("CallRelayReceiver", "control: " + phoneMessage.isControl() + " type: " + phoneMessage.type.name());
            if (!phoneMessage.isControl()) {
                if (phoneMessage.type == PhoneMessage.Status.RINGING) {
                    Log.i("CallRelayReceiver", "CallRelayReceiver received incoming call!");
                    this.mCallRelayreceiveservice.isiOS = false;
                    this.mCallRelayreceiveservice.isHfp = false;
                    this.mCallRelayreceiveservice.gotCall(phoneMessage.name, phoneMessage.number);
                    return;
                }
                if (phoneMessage.type == PhoneMessage.Status.GOTSMS) {
                    Log.i("SMS_RECEIVER", "SMS received by receiver");
                    this.mCallRelayreceiveservice.gotSMS(phoneMessage.name, phoneMessage.number, phoneMessage.body);
                    return;
                }
                if (phoneMessage.type == PhoneMessage.Status.ENDED)
                    doWhenCallEnded(paramContext);
                return;
            }
            if (phoneMessage.type == PhoneMessage.Status.REFRESH_NEEDED)
                doWhenRefreshNeeded(paramContext);
            if (phoneMessage.type == PhoneMessage.Status.REFRESH_NEEDED) {
                doWhenRefreshNeeded(paramContext);
            }
            //When the user hits the answer button code finish here
            if (phoneMessage.type == PhoneMessage.Control.ANSWER) {

                Log.i("CallRelayReceiver", "Answering call");

                //acceptRingCall(paramContext);
                acceptInSvr(paramContext);

                Log.i("CallRelayReceiver", "Ansered call");

                return;
            }
        }
        catch (Exception ex)
        {
            Log.e("CallRelayReceiver", ex.getMessage());
        }
    }

    public void acceptInSvr(Context context)
    {
        final Intent intent=new Intent();
        intent.setAction("com.recom3.ANSWER_CALL");
        //intent.setAction("com.recom3.ANSWERED");
        intent.putExtra("KeyName","code1id");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setComponent(new ComponentName("com.recom3.ice3svr","com.recom3.ice3svr.MyBroadcastReceiver"));
        //intent.setComponent(new ComponentName("com.recom3.ice3svr","com.recom3.ice3svr.MyBroadcastAnswered"));
        context.sendBroadcast(intent);
    }

    public void acceptRingCall(Context context)
    {
        if (Build.VERSION.SDK_INT >= 21) {
            //Intent answerCalintent = new Intent(context, LoginActivity.class);
            //answerCalintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            //        Intent.FLAG_ACTIVITY_CLEAR_TASK  |
            //        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            //context.startActivity(answerCalintent);

            Intent intent = new Intent(context, AcceptCallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
        else
        {
            /*
            if (telephonyService != null) {
                try {
                    telephonyService.answerRingingCall();
                }
                catch (Exception e) {
                    answerPhoneHeadsethook();
                }
            }
            */

            TelecomManager tm = (TelecomManager) mContext
                    .getSystemService(Context.TELECOM_SERVICE);

            if (tm == null) {
                // whether you want to handle this is up to you really
                throw new NullPointerException("tm == null");
            }

            tm.acceptRingingCall();
        }
    }

    /*
Sample trace:

I/LoginAcivity: Incommming call
I/PhoneServiceSDK: size of phone status message: 224
I/PhoneServiceSDK: md5: MD5 Message Digest from AndroidOpenSSL, <initialized>
I/BTConnectivityManager: Putting the message HUDConnectivityMessage [intentFilter=RECON_PHONE_MESSAGE, sender=com.reconinstruments.mobilesdk.phonecontrol.PhoneControlService] into the object queue
I/BTConnector: Taking the message from the object queue: RECON_PHONE_MESSAGE
I/BTObjectConnector: Processing the message QueueMessage [sender=com.reconinstruments.mobilesdk.phonecontrol.PhoneControlService, intentFilter=RECON_PHONE_MESSAGE]
I/BTObjectConnector: Sending the message data: QueueMessage [sender=com.reconinstruments.mobilesdk.phonecontrol.PhoneControlService, intentFilter=RECON_PHONE_MESSAGE]
I/BTConnectivityManager: Putting the message HUDConnectivityMessage [intentFilter=RECON_MUSIC_MESSAGE, sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService] into the object queue
I/BTConnector: Taking the message from the object queue: RECON_MUSIC_MESSAGE
I/BTObjectConnector: Processing the message QueueMessage [sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService, intentFilter=RECON_MUSIC_MESSAGE]
I/BTObjectConnector: Sending the message data: QueueMessage [sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService, intentFilter=RECON_MUSIC_MESSAGE]
I/BTConnectivityManager: Putting the message HUDConnectivityMessage [intentFilter=RECON_MUSIC_MESSAGE, sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService] into the object queue
I/BTConnectivityManager: Putting the message HUDConnectivityMessage [intentFilter=RECON_MUSIC_MESSAGE, sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService] into the object queue
I/BTConnectivityManager: Putting the message HUDConnectivityMessage [intentFilter=RECON_MUSIC_MESSAGE, sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService] into the object queue
W/BTTransportManager: Skip to send data since the QueueMessage is null
W/BTTransportManager: Skip to send data since the QueueMessage is null
I/BTConnectivityManager: Received message from HUD=���������������������
                         HUDService������INTENT_OLD_API_MESSAGE��������<recon intent="CALLER_ID_RESOLUTION"><caller_id>+491624264301</caller_id></recon>��������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
I/BTConnectivityManager: Start receiving new HUDConnectivityMessage data block, total size = 133
I/BTConnectivityManager: Stop receiving data, constructing the HUDConnectivityMessage
I/BTConnectivityManager: Intent filter=INTENT_OLD_API_MESSAGE
I/BTConnectivityManager: HUDConnectivityMessage md5 = 6efd6a0e2401d04a0ebe2819790b88b2
I/SendBroadcastPermission: action:INTENT_OLD_API_MESSAGE, mPermissionType:0
I/BTConnectivityManager: Sent out the broadcast to INTENT_OLD_API_MESSAGE
W/Bundle: Key message expected String but value was a [B.  The default value <null> was returned.
W/Bundle: Attempt to cast generated internal exception:
          java.lang.ClassCastException: byte[] cannot be cast to java.lang.String
              at android.os.BaseBundle.getString(BaseBundle.java:999)
              at android.content.Intent.getStringExtra(Intent.java:6318)
              at com.reconinstruments.applauncher.phone.PhoneRelayReceiver.onReceive(PhoneRelayReceiver.java:88)
              at android.app.LoadedApk$ReceiverDispatcher$Args.run(LoadedApk.java:1222)
              at android.os.Handler.handleCallback(Handler.java:761)
              at android.os.Handler.dispatchMessage(Handler.java:98)
              at android.os.Looper.loop(Looper.java:156)
              at android.app.ActivityThread.main(ActivityThread.java:6577)
              at java.lang.reflect.Method.invoke(Native Method)
              at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:942)
              at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:832)
W/System.err: java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference
W/System.err:     at java.io.StringReader.<init>(StringReader.java:50)
W/System.err:     at com.reconinstruments.modlivemobile.dto.message.XMLMessage.parseSimpleMessageNode(XMLMessage.java:159)
W/System.err:     at com.reconinstruments.modlivemobile.dto.message.PhoneMessage.<init>(PhoneMessage.java:56)
W/System.err:     at com.reconinstruments.applauncher.phone.PhoneRelayReceiver.onReceive(PhoneRelayReceiver.java:88)
W/System.err:     at android.app.LoadedApk$ReceiverDispatcher$Args.run(LoadedApk.java:1222)
W/System.err:     at android.os.Handler.handleCallback(Handler.java:761)
W/System.err:     at android.os.Handler.dispatchMessage(Handler.java:98)
W/System.err:     at android.os.Looper.loop(Looper.java:156)
W/System.err:     at android.app.ActivityThread.main(ActivityThread.java:6577)
W/System.err:     at java.lang.reflect.Method.invoke(Native Method)
W/System.err:     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:942)
W/System.err:     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:832)
E/CallRelayReceiver: Attempt to invoke interface method 'java.lang.String org.w3c.dom.Node.getNodeName()' on a null object reference
E/CallRelayReceiver: No enum constant com.reconinstruments.modlivemobile.dto.message.PhoneMessage.Type.caller_id
E/CallRelayReceiver: Attempt to invoke virtual method 'boolean com.reconinstruments.modlivemobile.dto.message.PhoneMessage.isControl()' on a null object reference
I/BTConnector: Taking the message from the object queue: RECON_MUSIC_MESSAGE
I/BTObjectConnector: Processing the message QueueMessage [sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService, intentFilter=RECON_MUSIC_MESSAGE]
I/BTObjectConnector: Sending the message data: QueueMessage [sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService, intentFilter=RECON_MUSIC_MESSAGE]
I/BTConnector: Taking the message from the object queue: RECON_MUSIC_MESSAGE
I/BTObjectConnector: Processing the message QueueMessage [sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService, intentFilter=RECON_MUSIC_MESSAGE]
I/BTObjectConnector: Sending the message data: QueueMessage [sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService, intentFilter=RECON_MUSIC_MESSAGE]
W/BTTransportManager: Skip to send data since the QueueMessage is null
W/BTTransportManager: Skip to send data since the QueueMessage is null
I/BTConnector: Taking the message from the object queue: RECON_MUSIC_MESSAGE
I/BTObjectConnector: Processing the message QueueMessage [sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService, intentFilter=RECON_MUSIC_MESSAGE]
I/BTObjectConnector: Sending the message data: QueueMessage [sender=com.reconinstruments.mobilesdk.mediaplayer.MediaPlayerService, intentFilter=RECON_MUSIC_MESSAGE]
W/BTTransportManager: Skip to send data since the QueueMessage is null
I/BTConnectivityManager: Received message from HUD=���������������������
                         HUDService������INTENT_OLD_API_MESSAGE��������<recon intent="RECON_PHONE_MESSAGE"><CONTROL type="ANSWER" number="" name="" title="" body="" /></recon>���������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
I/BTConnectivityManager: Start receiving new HUDConnectivityMessage data block, total size = 156
I/BTConnectivityManager: Stop receiving data, constructing the HUDConnectivityMessage
I/BTConnectivityManager: Intent filter=INTENT_OLD_API_MESSAGE
I/BTConnectivityManager: HUDConnectivityMessage md5 = d5dfcac919cdd13a2b490a641810cfb6
I/SendBroadcastPermission: action:INTENT_OLD_API_MESSAGE, mPermissionType:0
I/BTConnectivityManager: Sent out the broadcast to INTENT_OLD_API_MESSAGE
W/Bundle: Key message expected String but value was a [B.  The default value <null> was returned.
W/Bundle: Attempt to cast generated internal exception:
          java.lang.ClassCastException: byte[] cannot be cast to java.lang.String
              at android.os.BaseBundle.getString(BaseBundle.java:999)
              at android.content.Intent.getStringExtra(Intent.java:6318)
              at com.reconinstruments.applauncher.phone.PhoneRelayReceiver.onReceive(PhoneRelayReceiver.java:88)
              at android.app.LoadedApk$ReceiverDispatcher$Args.run(LoadedApk.java:1222)
              at android.os.Handler.handleCallback(Handler.java:761)
              at android.os.Handler.dispatchMessage(Handler.java:98)
              at android.os.Looper.loop(Looper.java:156)
              at android.app.ActivityThread.main(ActivityThread.java:6577)
              at java.lang.reflect.Method.invoke(Native Method)
              at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:942)
              at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:832)
W/System.err: java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference
W/System.err:     at java.io.StringReader.<init>(StringReader.java:50)
W/System.err:     at com.reconinstruments.modlivemobile.dto.message.XMLMessage.parseSimpleMessageNode(XMLMessage.java:159)
W/System.err:     at com.reconinstruments.modlivemobile.dto.message.PhoneMessage.<init>(PhoneMessage.java:56)
W/System.err:     at com.reconinstruments.applauncher.phone.PhoneRelayReceiver.onReceive(PhoneRelayReceiver.java:88)
W/System.err:     at android.app.LoadedApk$ReceiverDispatcher$Args.run(LoadedApk.java:1222)
W/System.err:     at android.os.Handler.handleCallback(Handler.java:761)
W/System.err:     at android.os.Handler.dispatchMessage(Handler.java:98)
W/System.err:     at android.os.Looper.loop(Looper.java:156)
W/System.err:     at android.app.ActivityThread.main(ActivityThread.java:6577)
W/System.err:     at java.lang.reflect.Method.invoke(Native Method)
W/System.err:     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:942)
W/System.err:     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:832)
E/CallRelayReceiver: Attempt to invoke interface method 'java.lang.String org.w3c.dom.Node.getNodeName()' on a null object reference
I/CallRelayReceiver: Answering call
I/SendBroadcastPermission: action:com.recom3.ANSWER_CALL, mPermissionType:0
I/CallRelayReceiver: Ansered call

     */
}
