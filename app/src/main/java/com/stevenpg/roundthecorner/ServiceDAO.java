package com.stevenpg.roundthecorner;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Steven on 7/31/2015.
 * This class will allow for all data
 * to be passed directly into the service.
 */
public class ServiceDAO implements Parcelable{

    // Complex objects
    public GeoCoderHandler geoCoderHandler;
    public TextSender textSender;

    // Simple types
    public String phoneNumber;
    public String message;
    public String distance;
    public String latitude;
    public String longitude;

    ServiceDAO(String latitude,
               String longitude,
               String phoneNumber,
               String message,
               String distance){
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.distance = distance;

        this.textSender = new TextSender(new TextRecipient(phoneNumber, message));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.message);
        dest.writeString(this.distance);
    }
}
