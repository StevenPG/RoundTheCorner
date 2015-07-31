package com.stevenpg.roundthecorner;

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

    final int notificationID = 0;

    // Attributes to keep track of notifications
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    Context context;

    // Build and display the notification
    NotificationHandler(Context context, String title, String contentText){
        this.context = context;
        this.builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.icon);
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.notificationManager.notify(notificationID, this.builder.build());
    }

    public void updateNotificationText(String updatedMessage){
        this.builder.setContentText(updatedMessage);
        this.notificationManager.notify(notificationID, this.builder.build());
    }

    public void closeNotification(){
        this.notificationManager.cancel(this.notificationID);
    }

}
