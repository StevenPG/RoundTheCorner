package com.stevenpg.roundthecorner;

/**
 * Created by Steven on 7/27/2015.
 * This class will contain attributes
 * and methods to allow for easier
 * handling of data for sending text
 * messages
 */
public class TextRecipient {

    private String phoneNumber;
    private String message;

    public TextRecipient(String phoneNumber, String message){
        this.phoneNumber = phoneNumber;
        this.message = message;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    public String getMessage(){ return this.message; }

}
