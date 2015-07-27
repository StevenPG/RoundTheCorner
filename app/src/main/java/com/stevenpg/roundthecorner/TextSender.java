package com.stevenpg.roundthecorner;

import android.telephony.SmsManager;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steven on 7/26/2015.
 * This class is a simple wrapper for sending
 * text messages.
 */
public class TextSender {

    List<TextRecipient> textRecipientList;
    private SmsManager smsManager;

    // This constructor is used when there is exactly one TextRecipient
    TextSender(TextRecipient textRecipient){
        this.textRecipientList = new LinkedList<>();
        this.textRecipientList.add(textRecipient);
        this.smsManager = SmsManager.getDefault();
    }

    // This constructor is used when there is more than one TextRecipient
    TextSender(List<TextRecipient> textRecipientList){
        this.textRecipientList = textRecipientList;
    }

    public void sendText(){
        for(TextRecipient textRecipient : this.textRecipientList){
            this.smsManager.sendTextMessage(
                    textRecipient.getPhoneNumber(),null,
                    textRecipient.getMessage(), null, null);
        }
    }

    // Static method to decrease coupling
    // This method is given a list of strings, and returns a list of text recipients with same message
    static public List<TextRecipient> generateRecipientsWithSameMessage(List<String> numbers, String message){
        List<TextRecipient> textRecipientList = new LinkedList<>();

        for(String number : numbers){
            textRecipientList.add(new TextRecipient(number, message));
        }

        return textRecipientList;
    }
}
