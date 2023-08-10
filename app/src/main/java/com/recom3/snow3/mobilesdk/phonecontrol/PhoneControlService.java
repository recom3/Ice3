package com.recom3.snow3.mobilesdk.phonecontrol;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.recom3.snow3.mobilesdk.hudconnectivity.ConnectHelper;
import com.recom3.snow3.mobilesdk.messages.PhoneMessage;

/**
 * Created by Recom3 on 29/03/2022.
 */

public class PhoneControlService extends Service {

    TelephonyManager a;

    PhoneStateListener b = new PhoneStateListener(/*this*/) {
        public void onCallStateChanged(int param1Int, String param1String) {
            Log.d("PhoneControlService", "currentState: " +
                    PhoneControlService.this.a.getCallState() + " newState: " + param1Int + " number: " + param1String);
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
    BroadcastReceiver d = new BroadcastReceiver(/*this*/) {
        //https://stackoverflow.com/questions/33517461/smsmessage-createfrompdu-is-deprecated-in-android-api-level-23
        public void onReceive(Context param1Context, Intent param1Intent) {
            // Byte code:
            //   0: ldc 'PhoneControlService'
            //   2: ldc 'received message'
            //   4: invokestatic b : (Ljava/lang/String;Ljava/lang/String;)V
            //   7: aload_2//load a reference onto the stack from local variable 2
            Bundle var_2 = param1Intent.getExtras();
            //   8: invokevirtual getExtras : ()Landroid/os/Bundle;
            //   11: astore_2//store a reference into local variable 2
            //   12: aload_2//load a reference onto the stack from local variable 2
            //   13: ifnull -> 271
            //   16: aload_2
            //   17: ldc 'pdus'

            //   340: new com/reconinstruments/mobilesdk/messages/PhoneMessage
            //   343: dup
            //   344: getstatic com/reconinstruments/mobilesdk/messages/PhoneMessage$Status.e : Lcom/reconinstruments/mobilesdk/messages/PhoneMessage$Status;
            //   347: iconst_4
            //   348: anewarray java/lang/String
            //   351: dup
            //   352: iconst_0
            //   353: aload #8
            //   355: aastore
            //   356: dup
            //   357: iconst_1
            //   358: aload_2
            //   359: aastore
            //   360: dup
            //   361: iconst_2
            //   362: ldc ''
            //   364: aastore
            //   365: dup
            //   366: iconst_3
            //   367: aload #9
            //   369: aastore
            //   370: invokespecial <init> : (Ljava/lang/Enum;[Ljava/lang/String;)V
            //   373: invokevirtual a : ()Ljava/lang/String;
            //   376: invokestatic a : (Landroid/content/Context;Ljava/lang/String;)V
            //   379: return
            //   380: goto -> 183
        }
    };

    //@Nullable
    @Override
    //public IBinder onBind(Intent intent) {
    //    return null;
    //}
    public IBinder onBind(Intent paramIntent) {
        return (IBinder)new Binder();
    }
}
