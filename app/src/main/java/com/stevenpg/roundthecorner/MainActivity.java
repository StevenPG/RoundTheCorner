package com.stevenpg.roundthecorner;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    // Save context for necessary areas
    Context context = this;

    // DAO for holding data to give to service
    ServiceDAO data;

    // Button for global access
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.StartButton);
    }

    // Fill dao with dummy data
    // get service working
    // get notifications working
    // then retrieve data

    // Validate inputs on button click
    public void ValidateFields(View v){
        this.button.setEnabled(false);

        // Validate inputs
        boolean quit = false;
        if(ValidateAddress() == false ||
            ValidatePhoneNumber() == false ||
                    ValidateMessage() == false ||
                    ValidateDistance() == false){
            return;
        }
        else{
            // Everything should be good

            // Fill DAO with dummy data after validation
            Location currentLocation = new Location("CurrentLocation");
            Location selectedLocation = new Location("SelectedLocation");
            Integer distance = new Integer(50);
            String message = "Hello, World!";
            String phoneNumber = "123456789";
            data = new ServiceDAO(currentLocation, selectedLocation, distance, message, phoneNumber);
            data.printDAO();

            StartServiceCloseActivity(data);
        }
    }

    // Start service and close activity
    public void StartServiceCloseActivity(ServiceDAO dao){
        Intent intent = new Intent(context, UpdateServer.class);
        intent.putExtra("DAO", dao.serialize());
        this.startService(intent);
        finish();
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


