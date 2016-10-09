package logger;

import android.location.Location;
import android.util.Log;

import util.K;

/**
 * Created by vikrant on 7/10/16.
 */

public class LocationLogger extends LogWriter {
    static final String TAG = LocationLogger.class.getSimpleName();
    public LocationLogger(String fileNamePrefix) {
        super(fileNamePrefix);
        setHeader(K.LOCATION_LOGGER_DEFAULT_HEADER);
    }

    public void writeLocation(Location location) {
        Log.d(TAG, "writing location to file " + location.toString());
        write(System.currentTimeMillis() + K.FIELD_SEPARATOR +
                location.getLatitude() + K.FIELD_SEPARATOR +
                location.getLongitude() + K.FIELD_SEPARATOR +
                location.getAccuracy() + K.FIELD_SEPARATOR +
                location.getSpeed() + K.FIELD_SEPARATOR +
                location.getBearing() + K.NEWLINE
        );
    }
}
