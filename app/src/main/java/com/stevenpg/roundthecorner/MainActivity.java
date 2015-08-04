package com.stevenpg.roundthecorner;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

    // Intent service
    Intent intentService;

    // Filenames where data is stored for load and save
    String ADDRESSFILE = "address_pref";
    String NUMBERFILE = "number_pref";
    String MESSAGEFILE = "message_pref";

    // ButtonHandler for global access and clean interaction
    ButtonHandler button;

    // Broadcaster for use in other methods
    BroadcastReceiver broadcastReceiver;

    // Spinner to grab from multiple places
    Spinner spinner;

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

        // Check that one network provider is on
        isNetworkOn();

        // Create spinner
        this.spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Create the broadcast receiver and define properties
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("debugger", "Received broadcast");
                finish();
                unregisterReceiver(broadcastReceiver);
            }
        };

        // Register the receiver with this activity so the service can command it
        registerReceiver(broadcastReceiver, new IntentFilter("UpdateServiceSaysClose"));
    }

    // Function that kicks off everything on button click
    public void buttonClick(View v ){
        this.button.disable();

        // Retrieve spinner value to set units for notification
        String units = spinner.getSelectedItem().toString();

        // retrieve all UI elements relevant
        EditText addressTxt = (EditText)findViewById(R.id.AddressEdit);
        EditText distance = (EditText)findViewById(R.id.DistanceEdit);
        EditText phoneNumber = (EditText)findViewById(R.id.PhoneNumberEdit);
        EditText msg = (EditText)findViewById(R.id.MessageEdit);

        // Save into string for validation and transfer to service
        String address = addressTxt.getText().toString();
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
            // Guarantee Start is disabled
            this.button.disable();
            this.button.updateText("Start New Notification");

            // Get a location object from geocode handler
            GeoCoderHandler geoCoderHandler = validator.getGeoCoderHandler();
            Location location = geoCoderHandler.getCoords();

            // Assign all values into data access object
            ServiceDAO serviceDAO = new ServiceDAO(
                    Double.toString(location.getLatitude()),
                    Double.toString(location.getLongitude()),
                    phoneNum,
                    message,
                    dist,
                    units
            );

            // Set the data and start intent service
            intentService = new Intent(this, UpdaterService.class);
            Bundle b = new Bundle();
            b.putParcelable("DAO", serviceDAO);
            intentService.putExtras(b);
            startService(intentService);

            // Hide activity and start service
            Log.d("debugger", "Hiding activity");
        }
    }

    // Control which buttons are available, LOAD by default
    public void enableSave(View v){
        Switch saveSwitch = (Switch) findViewById(R.id.switch1);
        Button address = (Button) findViewById(R.id.AddressSaveButton);
        Button phone = (Button) findViewById(R.id.PhoneSaveButton);
        Button message = (Button) findViewById(R.id.MessageSaveButton);
        if(saveSwitch.isChecked()){
            // Save is enabled
            address.setEnabled(true);
            phone.setEnabled(true);
            message.setEnabled(true);
        }
        else{
            // Disable saves
            address.setEnabled(false);
            phone.setEnabled(false);
            message.setEnabled(false);
        }
    }

    // Control each set of load and save buttons that have only 1 saved state
    public void loadAddress(View v){
        EditText address = (EditText)findViewById(R.id.AddressEdit);
        loadString(ADDRESSFILE, address);
    }
    public void loadPhoneNumber(View v){
        EditText phone = (EditText)findViewById(R.id.PhoneNumberEdit);
        loadString(NUMBERFILE, phone);
    }
    public void loadMessage(View v){
        EditText message = (EditText)findViewById(R.id.MessageEdit);
        loadString(MESSAGEFILE, message);
    }
    public void saveAddress(View v){
        EditText address = (EditText)findViewById(R.id.AddressEdit);
        String addressText = address.getText().toString();
        saveString(ADDRESSFILE, addressText);
    }
    public void savePhoneNumber(View v){
        EditText phone = (EditText)findViewById(R.id.PhoneNumberEdit);
        String phoneText = phone.getText().toString();
        saveString(NUMBERFILE, phoneText);
    }
    public void saveMessage(View v){
        EditText message = (EditText)findViewById(R.id.MessageEdit);
        String msgText = message.getText().toString();
        saveString(MESSAGEFILE, msgText);
    }

    // Just edit the text directly through the loading
    public void loadString(String fileName, EditText editText){
        try {
            FileInputStream fis = openFileInput(fileName);
            byte fileContents[] = new byte[500];
            fis.read(fileContents);
            String text = new String(fileContents);
            editText.setText(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    public void saveString(String fileName, String string){
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        catch (IOException io){ io.printStackTrace(); }
    }

    // End set of load and save buttons

    @Override
    protected void onDestroy(){
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e){
            Log.d("debugger", "Destroyed");
        }
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

    // Test if Wi-Fi or Mobile data are on for GeoCoding
    public void isNetworkOn(){

        // Test Wi-Fi
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(!wifiInfo.isConnected() && !networkInfo.isConnected()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            Log.d("debug", "GPS is disabled");
            alertDialogBuilder.setMessage("Internet Access Not Found. Please enable Wi-Fi or Mobile Data.")
                    .setCancelable(false)
                    .setPositiveButton("Enable Wi-Fi",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent callGPSSettingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            })
                    .setNegativeButton("Enable Mobile Data",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent callMobileDataIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                                    startActivity(callMobileDataIntent);
                                }
                            })
                    .setIcon(android.R.drawable.ic_dialog_alert);
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
        // Otherwise one must be enabled
    }
}


