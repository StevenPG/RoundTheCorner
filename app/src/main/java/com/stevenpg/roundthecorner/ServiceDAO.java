package com.stevenpg.roundthecorner;

import android.app.Service;
import android.location.Location;
import android.util.Log;

/**
 * Created by Steven on 7/24/2015.
 */
public class ServiceDAO {
    public Location currentLocation;
    public Location selectedLocation;
    public Integer distance;
    public String message;
    public String phoneNumber;

    ServiceDAO(Location currentLocation, Location selectedLocation, Integer distance, String message, String phoneNumber){
        this.currentLocation = currentLocation;
        this.selectedLocation = selectedLocation;
        this.distance = distance;
        this.message = message;
        this.phoneNumber = phoneNumber;
    }

    ServiceDAO(){
        this.currentLocation = null;
        this.selectedLocation = null;
        this.distance = null;
        this.message = null;
        this.phoneNumber = null;
    }

    public void printDAO(){
        Log.d("debug", "Current Location Lat: " + currentLocation.getLatitude());
        Log.d("debug", "Current Location Long: " + currentLocation.getLongitude());
        Log.d("debug", "Selected Location Lat: " + selectedLocation.getLatitude());
        Log.d("debug", "Selected Location Long: " + selectedLocation.getLongitude());
        Log.d("debug", "Distance: " + distance);
        Log.d("debug", "Message: " + message);
        Log.d("debug", "Phone Number: " + phoneNumber);
    }

    public String serialize(){
        String currentLatLong = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        String selectedLatLong = selectedLocation.getLatitude() + "," + selectedLocation.getLongitude();
        String dist = distance.toString();
        String msg = message;
        String phone = phoneNumber;
        return currentLatLong + "-" +
                selectedLatLong + "-" +
                dist + "-" +
                msg + "-" +
                phone;
    }

    public ServiceDAO buildFromString(String serializedDao){
        String[] data = serializedDao.split("-");
        String [] cur = data[0].split(",");
        String [] sel = data[1].split(",");
        String currLat = cur[0];
        String currLong = cur[1];
        String selLat = sel[0];
        String selLong = sel[1];
        String dist = data[2];
        String msg = data[3];
        String phone = data[4];

        int distance = Integer.parseInt(dist);

        Location currentLocation = new Location("CurrentLocation");
        Location selectedLocation = new Location("SelectedLocation");

        currentLocation.setLatitude(Double.parseDouble(currLat));
        currentLocation.setLongitude(Double.parseDouble(currLong));

        selectedLocation.setLatitude(Double.parseDouble(selLat));
        selectedLocation.setLongitude(Double.parseDouble(selLong));

        ServiceDAO dao = new ServiceDAO(currentLocation, selectedLocation, distance, msg, phone);
        dao.printDAO();

        return dao;
    }
}
