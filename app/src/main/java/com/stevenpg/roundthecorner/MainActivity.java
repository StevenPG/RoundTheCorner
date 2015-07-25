package com.stevenpg.roundthecorner;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    // Button for global access
    Button button;

    // Access to notification from service
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Assign button globally
        button = (Button)findViewById(R.id.StartButton);
    }

    // Validate inputs on button click
    public void ValidateFields(View v){
        this.button.setEnabled(false);

        // Validate inputs, if any fail, short circuit
        boolean quit = false;
        if(ValidateAddress() == false ||
            ValidatePhoneNumber() == false ||
                    ValidateMessage() == false ||
                    ValidateDistance() == false){
            return;
        }
        else{
            // Everything should be good
            StartServiceCloseActivity();
        }
    }

    // Start updating notification
    public void update(){
        // get distance, phone number, address, and message from user
        // Create locationlistener that gets current gps
        // create geocoding and get gps of location
        // create text messenger object
        // loop until distance < enteredDistance in thread
        //      Then send text message to phone number select

        // Thread that updates text in notification
        // Also checks for distance and sends text when within range
        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                int iter = 0;
                while(iter < 50) {
                    builder.setContentText("Distance from Selected Address: " + iter + " mi");
                    notificationManager.notify(0, builder.build());
                    iter++;
                }
            }
        });
        updater.start();
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
        update();

        // Hide application and let thread run the notification updates
        super.onBackPressed();
    }

    public boolean ValidateAddress(){
        EditText addr = (EditText)findViewById(R.id.AddressEdit);
        if("".equals(addr.getText().toString())){
            validationFailed(addr, "Please Enter a Valid Address");
            return false;
        }
        return true;
    }

    public boolean ValidateDistance(){
        EditText distance = (EditText)findViewById(R.id.DistanceEdit);
        String dist = distance.getText().toString();

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
        String phoneNum = phoneNumber.getText().toString();

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
}


