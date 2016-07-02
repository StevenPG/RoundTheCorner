package com.stevengantz.rtc;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class UpdateService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    NotificationManager notificationManager;
    Notification notification;
    Location lastLocation;
    boolean cont = true;

    // Location Handling
    GoogleApiClient client;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v("service", "Service Started...");
        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        client.connect();

        if (notificationManager == null) {
            notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
        }
        // Build notification
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(
                        "Round The Corner"
                )
                .bigText(
                        "Distance Remaining: ..."
                )
                .setSummaryText(
                        "Location Accuracy: ..."
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

        while (cont) {
            try {
                Thread.sleep(1000);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.v("permissions", "Did not accept permissions...");
                    return;
                }
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
                if(lastLocation != null){
                    Log.v("location",
                            String.valueOf(lastLocation.getLatitude()) +
                            String.valueOf(lastLocation.getLongitude())
                    );
                    float totalDistance = getDistance(lastLocation);

                    // Update notification
                    NotificationCompat.BigTextStyle newBigTextStyle = new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(
                                    "Round The Corner"
                            )
                            .bigText(
                                    "Distance Remaining: " + (int) totalDistance + " " +
                                            DataHandler.units
                            )
                            .setSummaryText(
                                    "Location Accuracy: " + lastLocation.getAccuracy() + " Meters"
                            );
                    updateNotification(
                            (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.common_ic_googleplayservices)
                                    .setAutoCancel(false)
                                    .setOngoing(true)
                                    .setPriority(Notification.PRIORITY_MAX)
                                    .setStyle(newBigTextStyle)
                    );

                    // Check if within bounds, if so, send message.
                    Log.v("distCheck", "Total Distance: " + totalDistance + " in " + DataHandler.units);
                    Log.v("distCheck", "Distance from Location: " + DataHandler.distance);
                    if(totalDistance*1609.34 < Integer.parseInt(DataHandler.distance)){
                        SmsManager smsManager = SmsManager.getDefault();
                        Log.v("msg", "Sending Text Message...");
                        if(DataHandler.msg == null || DataHandler.msg.equals("")){
                            DataHandler.msg = "Failed to retrieve UserMsg";
                        } else {
                            Log.v("msg", DataHandler.msg);
                        }
                        smsManager.sendTextMessage(
                                DataHandler.phoneNumber,
                                "",
                                DataHandler.msg,
                                null, null);
                        stopSelf();
                    }

                } else {
                    Log.v("location", "Couldn't retrieve location...");
                    Toast.makeText(getApplicationContext(),
                            "Couldn't get location... Please turn on Location",
                            Toast.LENGTH_LONG).show();
                    cont = false;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void updateNotification(NotificationCompat.Builder builder) {
        notificationManager.notify(0,
                builder.build());
    }

    protected float getDistance(Location currentLocation){
        float distance = currentLocation.distanceTo(
                DataHandler.enteredLocation);
        if(DataHandler.units.equals("Meters")){
            return distance;
        } else if(DataHandler.units.equals("Feet")){
            return Math.round(distance * 3.28084F);
        }else if(DataHandler.units.equals("Kilometers")){
            return Math.round(distance * 1000);
        }else if(DataHandler.units.equals("Miles")){
            return Math.round(distance * 0.000621371);
        } else {
            assert false;
            return 0;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(0);
        client.disconnect();
        Log.v("service", "Service Destroyed...");
    }

    // Android location methods that must be overridden
    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
