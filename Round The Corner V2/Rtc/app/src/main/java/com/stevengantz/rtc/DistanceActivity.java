package com.stevengantz.rtc;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DistanceActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    CircleOptions circleOptions;
    Circle circle;
    int savedProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get text describing distance
        final TextView textView = (TextView) findViewById(R.id.disttext);

        // Retrieve Button
        Button next = (Button) findViewById(R.id.nextDistanceButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ActivityManager.openMessageActivity(getApplicationContext()));
            }
        });

        // Retrieve Spinner
        final Spinner options = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dist_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        options.setAdapter(adapter);

        // Retrieve seekbar
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(options.getSelectedItem().toString().equals("Meters")){
                    // Meters 15 - 750
                    circle.setRadius(progress*7.35+15);
                    textView.setText(String.valueOf(Math.round(progress*7.35+15)) + " Meters");
                } else if(options.getSelectedItem().toString().equals("Feet")){
                    // Feet 20 - 1000;
                    circle.setRadius((progress*9.8+20) * .3048);
                    textView.setText(String.valueOf(Math.round(progress*9.8+20)) + " Feet");
                } else if(options.getSelectedItem().toString().equals("Kilometers")){
                    // Kilometers 1 - 20
                    circle.setRadius((progress*0.19)*1000);
                    textView.setText(String.valueOf(Math.round(progress*0.19)) + " Kilometers");
                } else if(options.getSelectedItem().toString().equals("Miles")){
                    // Miles 1 - 15
                    circle.setRadius(Math.round(progress*0.14)*1609.34);
                    textView.setText(String.valueOf(Math.round(progress*0.14)) + " Miles");
                } else {
                    assert false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Assign the static value
                if(options.getSelectedItem().toString().equals("Meters")){
                    // Meters 15 - 750
                    savedProgress = (int) Math.round(seekBar.getProgress()*7.35+15);
                    DataHandler.distance = String.valueOf(savedProgress);
                } else if(options.getSelectedItem().toString().equals("Feet")){
                    // Feet 20 - 1000;
                    savedProgress = (int) (Math.round(seekBar.getProgress()*9.8+20) * .3048);
                    DataHandler.distance = String.valueOf(savedProgress);
                } else if(options.getSelectedItem().toString().equals("Kilometers")){
                    // Kilometers 1 - 20
                    savedProgress = (int) (Math.round(seekBar.getProgress()*0.19)*1000);
                    DataHandler.distance = String.valueOf(savedProgress);
                } else if(options.getSelectedItem().toString().equals("Miles")){
                    // Miles 1 - 15
                    savedProgress = (int) (Math.round(seekBar.getProgress()*0.14)*1609.34);
                    DataHandler.distance = String.valueOf(savedProgress);
                } else {
                    assert false;
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng selectedLoc = new LatLng(
                DataHandler.address.getLatitude(),
                DataHandler.address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(selectedLoc).title("Selected Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLoc, 15));

        // Create circle
        circleOptions = new CircleOptions()
                .center(selectedLoc)
                .radius(15); // Meters
        circle = mMap.addCircle(circleOptions);
    }
}
