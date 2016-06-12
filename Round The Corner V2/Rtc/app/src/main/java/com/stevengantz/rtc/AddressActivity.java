package com.stevengantz.rtc;

import android.content.Context;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.location.Address;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class AddressActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText addressBar;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Add button to disable/enable
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ActivityManager.openContactActivity(getApplicationContext()));
            }
        });

        // Retrieve the switch that switches between satellite and regular
        Switch satSwitch = (Switch) findViewById(R.id.switch1);
        if(satSwitch != null){
            satSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                }
            });
        }

        // Add changes that occur when enter is pressed in text field
        addressBar = (EditText) findViewById(R.id.editText);
        addressBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Toast.makeText(AddressActivity.this, "Entered " + v.getText().toString(), Toast.LENGTH_LONG).show();

                // Close keyboard
                if(getCurrentFocus() != null){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                // Pan to that location if it exists
                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> address;
                try {
                    address = geocoder.getFromLocationName(v.getText().toString(), 1);
                    if(address.size() > 0){
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition(
                                        new LatLng(address.get(0).getLatitude(),
                                                address.get(0).getLongitude()), 15, 0, 0)));
                        DataHandler.address = address.get(0);
                        // Enable next button
                        next.setEnabled(true);
                    } else {
                        throw new IOException("Error finding address");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error finding address...", Toast.LENGTH_LONG).show();
                    return true;
                }

                return true;
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
        LatLng philly = new LatLng(39.9526, -75.1652);
        mMap.addMarker(new MarkerOptions().position(philly).title("Marker in Philidelphia"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(philly));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(philly, 10));
    }
}
