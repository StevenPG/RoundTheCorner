package com.stevenpg.roundthecorner;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Steven on 7/31/2015.
 * This class creates the service that will
 * consistently update the gps coordinates.
 * Alleviating any issues with running background
 * processes in the background.
 */
public class UpdaterService extends IntentService {

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Retrieve data string from incoming Intent
        ServiceDAO serviceDAO = workIntent.getExtras().getParcelable("DAO");

        // Create text messenger object
        TextSender textSender = new TextSender(new TextRecipient(
                        serviceDAO.phoneNumber, serviceDAO.message));

        // Generate the default notification
        NotificationHandler notificationHandler = new NotificationHandler(this,
                "'Round The Corner Info", "Searching for GPS signal...");

        // Retrieve address location from geo-coding
        Location destination = new Location("Dest");
        destination.setLatitude(Double.parseDouble(serviceDAO.latitude));
        destination.setLongitude(Double.parseDouble(serviceDAO.longitude));

        // Create location services that poll device for gps coordinates
        LocationListenerHandler locationListenerHandler =
                new LocationListenerHandler();

        // Listen for updates
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListenerHandler);

        double distanceTo = Double.MAX_VALUE;
        double distanceUntilText = Double.parseDouble(serviceDAO.distance);

        while(distanceTo > distanceUntilText){

            // Get current location
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            notificationHandler.updateNotificationText(distanceTo + " meters remaining");

            // Set new distance
            distanceTo = currentLocation.distanceTo(destination);

            // Sleep between each update for battery life
            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        // Once within distance, send the text message
        textSender.sendText();

        // Close activity, notification, and service when finished
        Log.d("debugger", "Sending broadcast");
        notificationHandler.closeNotification();
        sendBroadcast(new Intent("UpdateServiceSaysClose"));
        stopSelf();
    }

    // Default constructor needed for parcelable
    public UpdaterService() {
        super("UpdaterService");
    }
}
