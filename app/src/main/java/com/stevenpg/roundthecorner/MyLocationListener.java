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
public class MyLocationListener implements LocationListener {

    private Location currentLocation;

    @Override
    public void onLocationChanged(Location location) {
        Log.d("debug", location.getLatitude() + ", " + location.getLongitude());
        currentLocation = new Location("CurrentLocation");
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());
    }

    public Location getCurrentLocation(){
        return currentLocation;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}