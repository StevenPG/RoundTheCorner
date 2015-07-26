package com.stevenpg.roundthecorner;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Text;


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
    public void update(final MainActivity mainActivity){

        // Texting Elements Start -------------

        // Get phone number from user
        EditText phoneNum = (EditText)findViewById(R.id.PhoneNumberEdit);
        String phoneNumber = phoneNum.getText().toString();

        // Get message from user
        EditText msg = (EditText)findViewById(R.id.MessageEdit);
        String message = msg.getText().toString();

        // create text messenger object
        TextSender textSender = new TextSender(phoneNumber, message);

        // Texting Elements End ---------------

        // Notification Elements Start-------------------

        // Get distance from user
        EditText distText = (EditText) findViewById(R.id.DistanceEdit);
        int distance = Integer.parseInt(distText.getText().toString());

        // Get address from user
        EditText addr = (EditText)findViewById(R.id.AddressEdit);
        String address = addr.getText().toString();
        // create geocoding and get gps of location

        // Create locationlistener that gets current gps

        // Save current distance between both GPS points
        int distanceBetweenPoints = 0;

        // Notification Elements End -----------------------


        // Thread that updates text in notification
        // Also checks for distance and sends text when within range, application then closed
        Thread updater = new Thread(new Runnable() {
            @Override
            public void run() {
                int iter = 0;
                // Loop until distance < distanceEntered
                while(iter < 50) {
                    builder.setContentText("Distance from Selected Address: " + iter + " mi");
                    notificationManager.notify(0, builder.build());
                    iter++;
                }
                // send text before shutting everything down
                mainActivity.finish();
                notificationManager.cancel(0);
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
        update(this);

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


