package com.stevengantz.rtc;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

/**
 * @author Steven Gantz
 * Date: 6/11/2016
 *
 * Activity Manager contains static
 * methods that open activities directly.
 */
public class ActivityManager {

    public static Intent openAddressActivity(Context context){
        return new Intent(context, AddressActivity.class);
    }

    public static Intent openContactActivity(Context context){
        return new Intent(context, ContactActivity.class);
    }

    public static Intent openDistanceActivity(Context context){
        return new Intent(context, DistanceActivity.class);
    }

    public static Intent openMessageActivity(Context context){
        return new Intent(context, MessageActivity.class);
    }
}
