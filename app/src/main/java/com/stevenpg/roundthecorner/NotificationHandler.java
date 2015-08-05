package com.stevenpg.roundthecorner;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Steven on 7/28/2015.
 * This class abstracts away all of the
 * notification handling normally
 * done in the main activity class.
 */
public class NotificationHandler {

    final int notificationID = 813;

    // Attributes to keep track of notifications
    NotificationManager notificationManager;
    Notification.Builder builder;
    Context context;

    // Build and display the notification
    NotificationHandler(Context context, String BigTitle,
                        String BigText, String BigSummary,
                        String SmallTitle, String SmallText){
        this.context = context;
        this.builder = new Notification.Builder(context);
        this.builder.setStyle(new Notification.BigTextStyle(builder)
                .setBigContentTitle(BigTitle)
                .bigText(BigText)
                .setSummaryText(BigSummary))
                .setContentTitle(SmallTitle)
                .setContentText(SmallText)
                .setPriority(2) // Maximum priority, should send to the top (low:-2 -> 2:high)
                .setSmallIcon(R.mipmap.ic_launcher);
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.notificationManager.notify(notificationID, this.builder.build());
    }

    public void updateNotificationText(String BigTitle,
                                       String BigText, String BigSummary,
                                       String SmallTitle, String SmallText){
        this.builder.setStyle(new Notification.BigTextStyle(builder)
                .setBigContentTitle(BigTitle)
                .bigText(BigText)
                .setSummaryText(BigSummary))
                .setContentTitle(SmallTitle)
                .setContentText(SmallText)
                .setPriority(2) // Maximum priority, should send to the top (low:-2 -> 2:high)
                .setSmallIcon(R.mipmap.ic_launcher);
        this.notificationManager.notify(notificationID, this.builder.build());
    }

    public void closeNotification(){
        this.notificationManager.cancelAll();
    }

}
