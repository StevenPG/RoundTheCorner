package com.stevenpg.roundthecorner;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Steven on 7/26/2015.
 * This class will condense getting the current
 * location into just a simple method call for
 * the main activity.
 */
public class LocationListenerHandler implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        Log.d("debugger", location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // called when status changes
    }

    @Override
    public void onProviderEnabled(String provider) {
        // called when the GPS provider is turned on (user turning on the GPS on the phone)
    }

    @Override
    public void onProviderDisabled(String provider) {
        // called when the status of the GPS provider changes
    }
}
