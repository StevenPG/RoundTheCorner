package com.stevenpg.roundthecorner;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Steven on 7/31/2015.
 * This class creates the service that will
 * consistently update the gps coordinates.
 * Alleviating any issues with running background
 * processes in the background.
 */
public class UpdaterService extends IntentService {

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Retrieve data string from incoming Intent
        String data = workIntent.getDataString();

        // Retrieve data
        // create text sender
        // create notification
        // create location services
        // Once everything is working
            // start looping by distance


        // Close activity and service when finished
        Log.d("debugger", "Sending broadcast");
        sendBroadcast(new Intent("UpdateServiceSaysClose"));
        stopSelf();
    }

    public UpdaterService() {
        super("UpdaterService");
    }
}
