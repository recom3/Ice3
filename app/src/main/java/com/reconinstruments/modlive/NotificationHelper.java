package com.reconinstruments.modlive;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.reconinstruments.connect.util.NotificationUtils;

/**
 * Created by recom3 on 21/08/2023.
 */

public class NotificationHelper {
    private static final int CONNECTION_NOTIFICATION_ID = 1;
    private static final String TAG = "NotificationHelper";

    public static void notifyConnected(Service service) {
        Notification notification = notifyPhoneConnected(service);
        service.startForeground(1, notification);
    }

    public static void clearNotifications(Service service) {
        NotificationUtils.getMgr(service).cancel(1);
        service.stopForeground(true);
    }

    private static Notification notifyPhoneConnected(Service context) {
        Log.i(TAG, "showing Phone connected notification");
        long when = System.currentTimeMillis();
        int icon = context.getResources().getIdentifier("sp_connectivity", "drawable", "com.reconinstruments.applauncher");
        Notification notification = new Notification(icon, "Connected", when);
        notification.flags = 2;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        //https://stackoverflow.com/questions/32345768/cannot-resolve-method-setlatesteventinfo
        //!recom3
        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(false);
        //builder.setTicker("this is ticker text");
        builder.setContentTitle("Smart Phone Connected");
        builder.setContentText("Smart Phone connection established!");
        //builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(contentIntent);
        builder.setOngoing(true);
        //builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        builder.build();
        //notification.setLatestEventInfo(context, "Smart Phone Connected", "Smart Phone connection established!", contentIntent);
        return notification;
    }
}