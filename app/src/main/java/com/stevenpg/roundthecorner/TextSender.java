package com.stevenpg.roundthecorner;

import android.telephony.SmsManager;

/**
 * Created by Steven on 7/26/2015.
 */
public class TextSender {

    public String phoneNumber;
    public String msg;

    TextSender(String phoneNumber, String msg){
        this.phoneNumber = phoneNumber;
        this.msg = msg;
    }

    public void sendText(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(this.phoneNumber, null, this.msg, null, null);
    }

}
