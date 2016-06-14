package com.stevenpg.roundthecorner;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Steven on 7/31/2015.
 * This class creates the service that will
 * consistently update the gps coordinates.
 * Alleviating any issues with running background
 * processes in the background.
 * This service implements the necessary interfaces
 * to directly interface with the GoogleApiClient
 * for significantly more accurate location options.
 */
public class UpdaterService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    // Location-based fields
    GoogleApiClient googleApiClient = null;
    LocationRequest locationRequest;
    Location currentLocation;
    boolean connected = false;

    // NotificationHandler
    NotificationHandler notificationHandler;

    @Override
    protected void onHandleIntent(Intent workIntent) {

        // Kick off google services
        buildGoogleApiClient();

        // Retrieve data string from incoming Intent
        ServiceDAO serviceDAO = workIntent.getExtras().getParcelable("DAO");

        // Create text messenger object
        TextSender textSender = new TextSender(new TextRecipient(
                        serviceDAO.phoneNumber, serviceDAO.message));

        // Generate the default notification
        this.notificationHandler = new NotificationHandler(this,
                "Round The Corner", "Distance Remaining: X", "Location Accuracy: X",
                "Distance Remaining: X", "Expand for more details");

        // Grab units field from dao
        String units = serviceDAO.units;

        // Retrieve address location from geo-coding
        Location destination = new Location("Destination");
        destination.setLatitude(Double.parseDouble(serviceDAO.latitude));
        destination.setLongitude(Double.parseDouble(serviceDAO.longitude));

        // Listen for activity to close, if closed, end the service
        BroadcastReceiver broadcastReceiver;
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("debugger", "Received broadcast");
                notificationHandler.closeNotification();
                stopSelf();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("CloseServiceNow"));

        // Busy wait until google client is connected
        while(!connected){
            try{ Thread.sleep(100); }
            catch (InterruptedException e)
            { e.printStackTrace(); }
        }
        createLocationRequest();
        startLocationUpdates();

        double distanceTo = Double.MAX_VALUE;
        double distanceUntilText = Double.parseDouble(serviceDAO.distance);

        while(distanceTo > distanceUntilText){

            // Set new distance
            currentLocation = LocationServices
                    .FusedLocationApi.getLastLocation(googleApiClient);

            // Get data and transform based on units
            distanceTo = currentLocation.distanceTo(destination);
            Float accuracy = currentLocation.getAccuracy();

            // Actually transform based on units
            if("feet".equals(units)){
                distanceTo = distanceTo * 3.28084;
                accuracy = accuracy * 3.28084F;
            }
            if("meters".equals(units)){
                // Do nothing
            }
            if("yards".equals(units)){
                distanceTo = distanceTo * 1.09361;
                accuracy = accuracy * 1.09361F;
            }

            // Update
            notificationHandler.updateNotificationText("Round The Corner",
                    "Distance Remaining: " + Math.round(distanceTo) + " " + units,
                    "Location Accuracy: Within " +
                            Float.toString(accuracy) + " " + units,
                    "Distance: " + Math.round(distanceTo) + " " + units,
                    "Swipe down for more details");

            // Sleep between each update for battery life
            try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
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

    /**
     * GoogleApiClient methods below this point-------
     */

    @Override
    public void onLocationChanged(Location location) {
        // this.currentLocation = location;
        Log.d("debugger", "LocationChanged");
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                this.googleApiClient, this.locationRequest, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        this.connected = true;
        Log.d("debugger", "Connected to GoogleApi");
         Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(this.googleApiClient);
        if(currentLocation != null){
            this.currentLocation = currentLocation;
        }
    }

    protected synchronized void buildGoogleApiClient(){
        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.googleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onDestroy(){
        super.onDestroy();
        notificationHandler.closeNotification();
        sendBroadcast(new Intent("UpdateServiceSaysClose"));
    }
}
