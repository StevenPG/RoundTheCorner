package com.stevenpg.roundthecorner;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Steven on 7/26/2015.
 * This class will wrap the standard
 * geocoder library and easily return
 * the gps coordinates of a location
 * using only the constructor.
 */
public class MyGeoCoder {

    protected Location selectedLocation;

    // Constructor
    // Throws an IOException is the geocoder fails in any way
    MyGeoCoder(Context context, String location) throws IOException{
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList = geocoder.getFromLocationName(location, 1);
        if(addressList == null || addressList.size() == 0){
            throw new IOException("Something went wrong while GeoCoding...");
        }
        else {
            Address address = addressList.get(0);
            selectedLocation = new Location("selectedLocation");
            selectedLocation.setLatitude(address.getLatitude());
            selectedLocation.setLongitude(address.getLongitude());
        }
    }

    public Location getCoords(){
        return this.selectedLocation;
    }
}
