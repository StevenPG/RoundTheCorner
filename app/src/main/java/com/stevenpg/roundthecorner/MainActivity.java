package com.stevenpg.roundthecorner;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

        // Create the broadcast receiver and define properties
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("debugger", "Received broadcast");
                finish();
                unregisterReceiver(this);
                System.exit(0);
            }
        };

        // Register the receiver with this activity so the service can command it
        registerReceiver(broadcastReceiver, new IntentFilter("UpdateServiceSaysClose"));
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

            // Hide activity and start service
            super.onBackPressed();
            Log.d("debugger", "Hiding activity");
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
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
                    .setPositiveButton("Go To Settings Page To Enable GPS",
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


