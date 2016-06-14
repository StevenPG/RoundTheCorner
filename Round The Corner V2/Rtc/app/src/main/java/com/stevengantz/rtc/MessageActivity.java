package com.stevengantz.rtc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageActivity extends AppCompatActivity {

    Intent intentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        EditText phone = (EditText) findViewById(R.id.PhoneText);
        EditText addr = (EditText) findViewById(R.id.AddrText);
        EditText dist = (EditText) findViewById(R.id.DistText);

        // If they are null, something is wrong with the view
        if (phone == null) throw new AssertionError();
        if (addr == null) throw new AssertionError();
        if (dist == null) throw new AssertionError();

        phone.setText(DataHandler.phoneNumber);
        addr.setText(DataHandler.address.getAddressLine(0));
        dist.setText(DataHandler.distance + " Meters");

        final Button stopServButton = (Button) findViewById(R.id.stopServiceButton);
        final Button startNotif = (Button) findViewById(R.id.startButton);

        // If they are null, something is wrong with the view
        if (stopServButton == null) throw new AssertionError();
        if (startNotif == null) throw new AssertionError();

        intentService = new Intent(this, UpdateService.class);

        stopServButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove ability to cancel service
                stopServButton.setEnabled(false);

                // Stop the service completely
                stopService(intentService);

                // Let user create notification
                startNotif.setEnabled(true);
            }
        });
        startNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable the button while the notification exists
                startNotif.setEnabled(false);

                // Start notification service
                startService(intentService);

                // Enable the stop service button
                stopServButton.setEnabled(true);

                // Hide the activity
                moveTaskToBack(true);
            }
        });
    }
}
