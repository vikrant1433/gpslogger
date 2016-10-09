package model;

import android.os.Build;

/**
 * Created by vikrant on 6/10/16.
 */

public class Phone {
    public final static String MANUFACTURER = Build.MANUFACTURER;
    public final static String MODEL = Build.MODEL;

    public static String getDeviceName() {
        return MANUFACTURER + " " + MODEL;
    }
}
