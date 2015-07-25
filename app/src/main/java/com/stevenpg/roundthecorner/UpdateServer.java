package com.stevenpg.roundthecorner;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Steven on 7/24/2015.
 */
public class UpdateServer extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Do something useful
        for(int i = 0; i < 10; i++) {
            Log.d("debug", "Started?");
        }
        this.stopSelf();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO for communication return IBinder implementation
        return null;
    }
}
