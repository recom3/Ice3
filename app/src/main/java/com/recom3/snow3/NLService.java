package com.recom3.snow3;

import android.app.Notification;
import android.app.PendingIntent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by Recom3 on 28/05/2022.
 * First answer here
 * https://stackoverflow.com/questions/41542245/programmatically-accept-call-in-nougat
 */

public class NLService extends NotificationListenerService {

    public static final String TAG = "snow3_log";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            if (sbn.getNotification().actions != null) {
                for (Notification.Action action : sbn.getNotification().actions)
                {
                    Log.e(TAG, "" + action.title);
                    if (action.title.toString().equalsIgnoreCase("Answer")) {
                        Log.e(TAG, "" + true);
                        PendingIntent intent = action.actionIntent;

                        try {
                            intent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}
