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
        Log.d("CallRelayReceiver", "Phone call ended");
        this.mCallRelayreceiveservice.alreadyInACall = false;
        if (this.mCallRelayreceiveservice.mBLEServiceConnectionManager.isiOSMode()) {
            Log.d("CallRelayReceiver", "we are in iOS Mode");
            Intent intent1 = new Intent("RECON_IOS_BLUETOOTH_HEADSET_COMMAND");
            intent1.putExtra("command", 4);
            paramContext.sendBroadcast(intent1);
        }
        Intent intent = new Intent("RECON_SS1_HFP_COMMAND");
        intent.putExtra("command", 501);
        paramContext.sendBroadcast(intent);
    }

    private void doWhenCallStarted(Context paramContext) {
        Log.d("CallRelayReceiver", "Phone call Started");
        this.mCallRelayreceiveservice.alreadyInACall = true;
        if (this.mCallRelayreceiveservice.mBLEServiceConnectionManager.isiOSMode()) {
            Log.d("CallRelayReceiver", "we are in iOS Mode");
            Intent intent = new Intent("RECON_IOS_BLUETOOTH_HEADSET_COMMAND");
            intent.putExtra("command", 3);
            paramContext.sendBroadcast(intent);
        }
    }

    private void doWhenRefreshNeeded(Context paramContext) {
        Log.d("CallRelayReceiver", "Refresh needed");
        if (this.mCallRelayreceiveservice.mBLEServiceConnectionManager.isiOSMode()) {
            Log.d("CallRelayReceiver", "we are in iOS Mode");
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
            Log.d("CallRelayReceiver", "control: " + phoneMessage.isControl() + " type: " + phoneMessage.type.name());
            if (!phoneMessage.isControl()) {
                if (phoneMessage.type == PhoneMessage.Status.RINGING) {
                    Log.d("CallRelayReceiver", "CallRelayReceiver received incoming call!");
                    this.mCallRelayreceiveservice.isiOS = false;
                    this.mCallRelayreceiveservice.isHfp = false;
                    this.mCallRelayreceiveservice.gotCall(phoneMessage.name, phoneMessage.number);
                    return;
                }
                if (phoneMessage.type == PhoneMessage.Status.GOTSMS) {
                    Log.d("SMS_RECEIVER", "SMS received by receiver");
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
}
