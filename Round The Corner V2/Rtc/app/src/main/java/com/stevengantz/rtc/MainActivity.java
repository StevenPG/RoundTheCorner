package com.stevengantz.rtc;
/**
 * @author Steven Gantz
 * Date: 6/10/2016
 *
 * The main activity is the initial place the
 * user enters the application from. From there,
 * then enter pertinent information until they
 * create their notification.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

/**
 * Main class that initiates the activity and does standard checks.
 * @see AppCompatActivity
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Button held classwide for easy access
     */
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup button
        startButton = (Button) findViewById(R.id.button);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                startActivity(ActivityManager.openAddressActivity(getApplicationContext()));
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        // Once activity is shown, run through the progress bar
        runProgress();
    }

    /**
     * This method handles the progress bar at the start of the application
     */
    protected void runProgress(){
        final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Start the background update thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mProgressBar.incrementProgressBy(25);
                    Thread.sleep(500);
                    mProgressBar.incrementProgressBy(25);
                    Thread.sleep(500);
                    mProgressBar.incrementProgressBy(25);
                    Thread.sleep(500);
                    mProgressBar.incrementProgressBy(25);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                /*
                // Check if network provider is active
                String network = isNetworkOn();
                if(network.equals("MOBILE") || network.equals("WIFI")){
                    mProgressBar.incrementProgressBy(25);
                } else {
                    // Ask user to enable
                }

                // Check if GPS is on
                if(!isGPSOn()){

                }*/
            }
        }).start();

        while(true){
            if(mProgressBar.getProgress() == 100){
                startButton.setEnabled(true);
                return;
            }
        }
    }

    /**
     * Returns whether gps is enabled
     * @return gps status
     */
    protected boolean isGPSOn(){
        return true;
    }

    /**
     * Returns whether the network is enabled
     * @return network provider
     */
    protected String isNetworkOn(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        String getTypeName = null;
        try{
            getTypeName = activeNetwork.getTypeName();
        } catch (NullPointerException npe){
            return "N/A";
        }

        // If no NPE occurred
        return getTypeName;
    }
}
