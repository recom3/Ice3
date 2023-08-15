package com.recom3.snow3.mobilesdk.phonecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.recom3.snow3.LoginActivity;
import com.recom3.snow3.MainActivityTest;

public class PhoneReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("", "Received call");

        String stateStr =
                intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        //val number =
        //        intent.extras!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)

        int state = 0;

        if (stateStr.compareToIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)==0) {
            state = TelephonyManager.CALL_STATE_RINGING;
            MainActivityTest.incommingCall();
        }
    }
}