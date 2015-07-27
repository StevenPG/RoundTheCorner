package com.stevenpg.roundthecorner;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Steven on 7/27/2015.
 * This class encapsulates the process
 * of validating into a method call.
 * Cleaning up MainActivity nicely.
 */
public class Validator {

    // Standard fields
    private String address;
    private String phoneNumber;
    private String distance;
    private String message;
    private Context context;

    // GeoCoderHandler for post-validation use
    private GeoCoderHandler geoCoderHandler;

    // Constructor just saves necessary data. Actual validation is called manually
    public Validator(String address, String phoneNumber, String distance, String message, Context context){
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.distance = distance;
        this.message = message;
        this.context = context;
    }

    /**
     * Perform validation, return failure message based on error code
     * Retrieve GeoCoderHandler after validation passes
     * Error Codes:
     * 1 - Message contained an empty string
     * 2 - Address contained an empty string
     * 3 - GeoCoding threw an IOException
     * 4 - Phone Number contained an empty string
     * 5 - Distance contained an empty string
     */
    public int performValidation(){

        int res = isAddressOK();
        if(res != 0){ return res; }

        res = isMessageOK();
        if(res != 0){ return res; }

        res = isPhoneNumberOK();
        if(res != 0){ return res; }

        res = isDistanceOK();
        if(res != 0) { return res; }

        // If all validations passed
        return 0;
    }

    public int isAddressOK(){
        // Test for empty string
        if("".equals(this.address)){
            return 2;
        }

        // Attempt to GeoCode
        try{
            this.geoCoderHandler = new GeoCoderHandler(this.context, this.address);
        } catch (IOException e) {
            Log.d("debug", "IOException occurred during GeoCoding: " + e.getMessage());
            return 3;
        }

        // If nothing failed
        return 0;
    }

    public int isPhoneNumberOK(){
        // Test for empty string
        if("".equals(this.phoneNumber)){
            return 4;
        }

        // If nothing failed
        return 0;
    }

    public int isDistanceOK(){
        // Test for empty string\
        if("".equals(this.distance)){
            return 5;
        }

        // If nothing failed
        return 0;
    }

    public int isMessageOK(){
        // Test for empty string
        if("".equals(this.message))
            return 1;
        else
            return 0;
    }

    public GeoCoderHandler getGeoCoderHandler(){
        return this.geoCoderHandler;
    }
}
