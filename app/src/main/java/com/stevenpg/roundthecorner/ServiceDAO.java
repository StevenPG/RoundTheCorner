package com.stevenpg.roundthecorner;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Steven on 7/31/2015.
 * This class will allow for all data
 * to be passed directly into the service.
 */
public class ServiceDAO implements Parcelable{

    // Simple types
    public String latitude;
    public String longitude;
    public String phoneNumber;
    public String message;
    public String distance;
    public String units;

    ServiceDAO(String latitude,
               String longitude,
               String phoneNumber,
               String message,
               String distance, String units){
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.distance = distance;
        this.units = units;
    }

    public static ServiceDAO readFromParcel(Parcel in){
        String latitude = in.readString();
        String longitude = in.readString();
        String phoneNumber = in.readString();
        String message = in.readString();
        String distance = in.readString();
        String units = in.readString();
        return new ServiceDAO(latitude, longitude,
                phoneNumber, message, distance, units);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.message);
        dest.writeString(this.distance);
        dest.writeString(this.units);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Required field because dev forgot Java doesn't have multiple inheritance
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel source) {
            return readFromParcel(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[0];
        }
    };
}
