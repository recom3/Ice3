package com.reconinstruments.connect.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by recom3 on 21/08/2023.
 */

public class NotificationUtils {
    private static final String TAG = "BTNotification";

    public static NotificationManager getMgr(Context context) {
        return (NotificationManager) context.getSystemService("notification");
    }

    public static void showOneTimeNotification(Context context, int drawable, String title, String message, int id, Intent notificationIntent) {
        Log.i(TAG, "showing notification");
        NotificationManager notificationManager = getMgr(context);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(drawable, title, when);
        notification.flags = 16;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        //https://stackoverflow.com/questions/32345768/cannot-resolve-method-setlatesteventinfo
        //!recom3
        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(false);
        //builder.setTicker("this is ticker text");
        builder.setContentTitle(title);
        builder.setContentText(message);
        //builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(contentIntent);
        builder.setOngoing(true);
        //builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        builder.build();

        notification = builder.getNotification();
        //notification.setLatestEventInfo(context, title, message, contentIntent);

        notificationManager.notify(id, notification);
    }

    public static void cancel(Context context, int id) {
        getMgr(context).cancel(id);
    }
}