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

        // Create text messager object
        TextSender textSender = new TextSender(new TextRecipient(
                        serviceDAO.phoneNumber, serviceDAO.message));

        // Generate the default notification
        NotificationHandler notificationHandler = new NotificationHandler(this,
                "'Round The Corner Info", "Searching for GPS signal...");

        // Retrieve address location from geocoding
        Location destination = new Location("Dest");
        destination.setLatitude(Double.parseDouble(serviceDAO.latitude));
        destination.setLongitude(Double.parseDouble(serviceDAO.longitude));

        // Create location services that poll device for gps coordinates
        LocationListenerHandler locationListenerHandler =
                new LocationListenerHandler();

        // Listen for updates
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                20, 5, locationListenerHandler);

        Location currentLocation = locationListenerHandler.getCurrentLocation();

        double distanceTo = currentLocation.distanceTo(destination);
        double distanceUntilText = Double.parseDouble(serviceDAO.distance);

        while(distanceTo > distanceUntilText){
            Log.d("debugger", "Not yet within distance: " + distanceTo + " " + distanceUntilText);
            currentLocation = locationListenerHandler.getCurrentLocation();
            Log.d("debugger", "GPS: " + currentLocation.getLatitude() +
                    "," + currentLocation.getLongitude());
        }

        Log.d("debugger", "Within distance, sending text");

        // Close activity, notifiation, and service when finished
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
