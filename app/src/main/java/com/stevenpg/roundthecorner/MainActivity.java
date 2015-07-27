package com.stevenpg.roundthecorner;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    // Button for global access
    Button button;

    // Access to notification from service
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    // Location services for re-use
    MyGeoCoder myGeoCoder;
    LocationManager locationManager;
    MyLocationListener myLocationListener;
    Location currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Assign button globally
        button = (Button)findViewById(R.id.StartButton);

        // Check that GPS is on
        isGPSOn();

        // Create locationlistener that gets current gps
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.myLocationListener = new MyLocationListener();
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
    }

    // Validate inputs on button click
    public void ValidateFields(View v){
        this.button.setEnabled(false);

        // Validate inputs, if any fail, short circuit
        if(!ValidateAddress() || !ValidatePhoneNumber() ||
                !ValidateMessage() || !ValidateDistance()){
            return;
        }
        else{
            // Everything should be good
            StartServiceCloseActivity();
        }
    }

    // Start service and close activity
    public void StartServiceCloseActivity(){

        // Create notification here for updating in service
        this.builder = new NotificationCompat.Builder(this)
                .setContentTitle("'Round The Corner Info")
                .setContentText("Distance from Selected Address: 0 mi")
                .setSmallIcon(R.drawable.icon);

        // Actually generate notification
        this.notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        this.notificationManager.notify(0, this.builder.build());

        // Start updating the notification
        update(this);

        // Hide application and let thread run the notification updates
        super.onBackPressed();
    }

    // Start updating notification
    public void update(final MainActivity mainActivity){

        // Texting Elements Start -------------

        // Get phone number from user
        EditText phoneNum = (EditText)findViewById(R.id.PhoneNumberEdit);
        String phoneNumber = phoneNum.getText().toString().trim();

        // Get message from user
        EditText msg = (EditText)findViewById(R.id.MessageEdit);
        String message = msg.getText().toString();

        // create text messenger object
        final TextSender textSender = new TextSender(phoneNumber, message);

        // Texting Elements End ---------------

        // Notification Elements Start-------------------

        // Get distance from user
        EditText distText = (EditText) findViewById(R.id.DistanceEdit);
        final int distance = Integer.parseInt(distText.getText().toString().trim());

        // Get address from user
        EditText addr = (EditText)findViewById(R.id.AddressEdit);
        String address = addr.getText().toString();

        // create geocoding and get gps of location
        final Location selectedLocation = this.myGeoCoder.getCoords();

        // Save current distance between both GPS points
        int distanceBetweenPoints = 0;

        // Notification Elements End -----------------------

        // Thread that updates text in notification
        // Also checks for distance and sends text when within range, application then closed
        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {

                // Set created distance var
                double dist = Double.MAX_VALUE;

                do{
                    // Get Distance)
                    Location cur = myLocationListener.getCurrentLocation();
                    if(cur == null){
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    // Get the new distance each time
                    if(cur != null) {
                        dist = cur.distanceTo(selectedLocation);
                    }
                    else{
                        builder.setContentText("Attempting to find GPS coverage...");
                        notificationManager.notify(0, builder.build());
                    }

                    // Wait 2 seconds between updates
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Set visuals
                    builder.setContentText("Distance from Selected Address: \n" + dist + " meters");
                    notificationManager.notify(0, builder.build());
                } while(dist > distance);

                // send text before shutting everything down
                textSender.sendText();
                mainActivity.finish();
                notificationManager.cancel(0);
            }
        });
        updater.start();
    }

    public boolean ValidateAddress(){
        EditText addr = (EditText)findViewById(R.id.AddressEdit);
        if("".equals(addr.getText().toString())){
            validationFailed(addr, "Please Enter a Valid Address");
            return false;
        }
        // Else, try to Geocode, report failure
        try {
            this.myGeoCoder = new MyGeoCoder(this, addr.getText().toString());
        } catch (IOException e) {
            Log.d("debug", e.getMessage());
            validationFailed(addr, "Error: check Wi-Fi/Mobile Data or Address");
            return false;
        }

        return true;
    }

    public boolean ValidateDistance(){
        EditText distance = (EditText)findViewById(R.id.DistanceEdit);
        String dist = distance.getText().toString().trim();

        if("".equals(dist)){
            validationFailed(distance, "Please Enter a Valid Distance");
            return false;
        }

        for(int i = 0; i < dist.length(); i++){
            if(!Character.isDigit(dist.charAt(i))) {
                validationFailed(distance, "Please Enter a Valid Distance");
                return false;
            }
        }
        return true;
    }

    public boolean ValidatePhoneNumber(){
        EditText phoneNumber = (EditText)findViewById(R.id.PhoneNumberEdit);
        String phoneNum = phoneNumber.getText().toString().trim();

        // Remove all dashes and underscores
        phoneNum = phoneNum.replaceAll("-", "");

        if("".equals(phoneNum)){
            validationFailed(phoneNumber, "Please Enter a Valid Phone Number");
            return false;
        }

        for(int i = 0; i < phoneNum.length(); i++){
            if(!Character.isDigit(phoneNum.charAt(i))) {
                validationFailed(phoneNumber, "Please Enter a Valid Phone Number");
                return false;
            }
        }
        return true;
    }

    public boolean ValidateMessage(){
        EditText msg = (EditText)findViewById(R.id.MessageEdit);
        if("".equals(msg.getText().toString())){
            validationFailed(msg, "Please Enter a Message");
            return false;
        }
        return true;
    }

    // If validation failed
    public void validationFailed(EditText offendingField, String msg){
        offendingField.setText(msg);
        this.button.setEnabled(true);
    }

    // Check if GPS is running
    public void isGPSOn(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // Everything is good, do nothing
            Log.d("debug", "GPS is enabled");
        }
        else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Log.d("debug", "GPS is disabled");
            alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Goto Settings Page To Enable GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }
}


