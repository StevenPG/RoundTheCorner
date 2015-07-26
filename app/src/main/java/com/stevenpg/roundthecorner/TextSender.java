package com.stevenpg.roundthecorner;

import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by Steven on 7/26/2015.
 * This class is a simple wrapper for sending
 * text messages.
 */
public class TextSender {

    public String phoneNumber;
    public String msg;
    private SmsManager smsManager;

    TextSender(String phoneNumber, String msg){
        this.phoneNumber = phoneNumber;
        this.msg = msg;
        this.smsManager = SmsManager.getDefault();
    }

    public void sendText(){
        this.smsManager.sendTextMessage(this.phoneNumber, null, this.msg, null, null);
    }

    // DELETE ME
    public void debugSend(){
        Log.d("debug", "If this is seen, delete this method!!!");
        this.smsManager.sendTextMessage("6109456265", null, "Testing, 1, 2, 3", null, null);
    }

}
