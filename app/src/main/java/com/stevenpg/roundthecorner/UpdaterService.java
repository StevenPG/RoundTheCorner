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

        // Do work here
        for(int i = 0; i < 400; i++) {
            Log.d("oneoff", "Service is running!");
        }
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UpdaterService(String name) {
        super(name);
    }
}
