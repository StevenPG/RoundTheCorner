package com.stevenpg.roundthecorner;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

    // Intent service
    Intent intentService;

    // ButtonHandler for global access and clean interaction
    ButtonHandler button;

    // Access to notification
    // TODO DELETE ME WHEN NEW FUNCTIONALITY WORKS
    NotificationHandler notificationHandler;

    // Location services for re-use
    GeoCoderHandler geoCoderHandler;

    // TODO DELETE ME WHEN NEW FUNCTIONALITY WORKS
    LocationManager locationManager;
    LocationListenerHandler locationListenerHandler;

    // Create the text message handler to send messages
    //TODO DELETE ME WHEN NEW FUNCTIONALITY WORKS
    TextSender textSender = null;

    /**
     * Runs when activity starts
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign button globally
        button = new ButtonHandler((Button)findViewById(R.id.StartButton));

        // Check that GPS is on
        isGPSOn();

        // Create location listener that gets current gps
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.locationListenerHandler = new LocationListenerHandler();
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerHandler);
    }

    // Function that kicks off everything on button click
    public void buttonClick(View v ){
        this.button.disable();

        // retrieve all UI elements relevant
        EditText addr = (EditText)findViewById(R.id.AddressEdit);
        EditText distance = (EditText)findViewById(R.id.DistanceEdit);
        EditText phoneNumber = (EditText)findViewById(R.id.PhoneNumberEdit);
        EditText msg = (EditText)findViewById(R.id.MessageEdit);

        // Save into string for validation and transfer to service
        String address = addr.getText().toString();
        String phoneNum = phoneNumber.getText().toString();
        String message = msg.getText().toString();
        String dist = distance.getText().toString();

        Validator validator = new Validator( address,
                phoneNum, dist, message, this);
        if(validator.performValidation() != 0){
            Log.d("debug", "validation failed: " + validator.errorMessage);
            // Print error in field and handle with error message (maybe toast?)
            this.button.enable();
        }
        else{
            // Get a location object from geocode handler
            GeoCoderHandler geoCoderHandler = validator.getGeoCoderHandler();
            Location location = geoCoderHandler.getCoords();

            // Assign all values into data access object
            ServiceDAO serviceDAO = new ServiceDAO(
                    Double.toString(location.getLatitude()),
                    Double.toString(location.getLongitude()),
                    phoneNum,
                    message,
                    dist
            );

            // Set the data and start intent service
            intentService = new Intent(this, UpdaterService.class);
            Bundle b = new Bundle();
            b.putParcelable("DAO", serviceDAO);
            intentService.putExtras(b);
            startService(intentService);

            for(int i = 0; i < 400; i++){
                Log.d("oneoff", "Did service start? Is it running while activity runs?");
            }

            // Temporary debug to test service
            //StartServiceCloseActivity();
        }
    }

    // Start service and close activity
    public void StartServiceCloseActivity(){

        // Create notification
        this.notificationHandler = new NotificationHandler(this, "'Round The Corner Info",
                "Distance from Destination: 0 mi");

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
        this.textSender = new TextSender(new TextRecipient(phoneNumber, message));

        // Texting Elements End ---------------

        // Notification Elements Start-------------------

        // Get distance from user
        EditText distText = (EditText) findViewById(R.id.DistanceEdit);
        final int distance = Integer.parseInt(distText.getText().toString().trim());

        // create geocoding and get gps of location
        final Location selectedLocation = this.geoCoderHandler.getCoords();

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
                    Location cur = locationListenerHandler.getCurrentLocation();
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
                        notificationHandler.updateNotificationText("Attempting to find GPS coverage...");
                    }

                    // Wait 2 seconds between updates
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Set new notification text
                    notificationHandler.updateNotificationText("Distance from Selected Address: \n" +
                            "\" + dist + \" meters");

                } while(dist > distance);

                // send text before shutting everything down
                textSender.sendText();
                mainActivity.finish();
                notificationHandler.closeNotification();
            }
        });
        updater.start();
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


