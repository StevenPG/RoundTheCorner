package com.stevengantz.rtc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        EditText phone = (EditText) findViewById(R.id.PhoneText);
        EditText addr = (EditText) findViewById(R.id.AddrText);
        EditText dist = (EditText) findViewById(R.id.DistText);
        phone.setText(DataHandler.phoneNumber);
        addr.setText(DataHandler.address.getAddressLine(0));
        dist.setText(DataHandler.distance + " Meters");

        Button startNotif = (Button) findViewById(R.id.startButton);
        startNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start notification service
                //hide activity
            }
        });
    }
}
