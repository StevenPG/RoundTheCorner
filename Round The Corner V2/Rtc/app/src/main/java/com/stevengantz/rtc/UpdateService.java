package com.stevengantz.rtc;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class UpdateService extends IntentService {

    NotificationManager notificationManager;
    Notification notification;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("service", "Service Started...");
        if(notificationManager == null) {
            notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
        }
        // Build notification
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(
                        "Round The Corner"
                )
                .bigText(
                        "Distance Remaining: X"
                )
                .setSummaryText(
                        "Location Accuracy: X"
                );
        notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_ic_googleplayservices)
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(bigTextStyle)
                .build();

        // Show notification
        notificationManager.notify(0, notification);

        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        notificationManager.cancel(0);
        Log.v("service", "Service Destroyed...");
    }
}
