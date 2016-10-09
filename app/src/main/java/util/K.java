package util;

/**
 * Created by vikrant on 7/9/16.
 * This class includes all the constants used in the app
 */
public class K {
    public final static String NEWLINE = System.getProperty("line.separator");
    public static final String TIMESTAMP_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    public static final String LOG_FILE_NAME_PREFIX = "gpslog";
    public static final String LOG_FILE_NAME_EXTENSION = ".txt";
    public static final String FIELD_SEPARATOR = ",";
    public static final String NONE = "--";
    public static final String ANDROID_LOCATION_HEADER_STRING = new Header().addHeader
            ("timestamp").getHeader();
    //            "timestamp" + FIELD_SEPARATOR +
    //                    "latitude" + FIELD_SEPARATOR +
    //                    "longitude" + FIELD_SEPARATOR +
    //                    "accuracy(in meters)" + FIELD_SEPARATOR +
    //                    "altitude(in meters)" + FIELD_SEPARATOR +
    //                    "speed(Km/hr)" + FIELD_SEPARATOR +
    //                    "bearing(in degree)" + FIELD_SEPARATOR +
    //                    "satellite-in-use" + FIELD_SEPARATOR +
    //                    "satellite-in-view" + FIELD_SEPARATOR +
    //                    "pdop" + FIELD_SEPARATOR +
    //                    "hdop" + FIELD_SEPARATOR +
    //                    "vdop" + FIELD_SEPARATOR + NEWLINE;
    public static final String NO_FIX = "No Fix Available";
    public static final String GOOGLE_API_HEADER =
            "timestamp" + FIELD_SEPARATOR + "latitude" + FIELD_SEPARATOR + "longitude" +
                    FIELD_SEPARATOR + "accuracy(in meters)" + FIELD_SEPARATOR + "altitude(in " +
                    "meters)" + FIELD_SEPARATOR + "speed(Km/hr)" + FIELD_SEPARATOR + "bearing(in " +
                    "degree)" + FIELD_SEPARATOR + "" + NEWLINE;
    public static final String SEVEN_DECIMAL_PLACES = "%.7f";
    public static final String TWO_DECIMAL_PLACE = "%.2f";
    public static final int EXTERNAL_STORAGE_READ_WRITE_REQUEST_CODE = 1;
    public static final String LOCATION_LOGGER_DEFAULT_HEADER = new Header()
            .addHeader("timestamp")
            .addHeader("lat")
            .addHeader("long")
            .addHeader("accuracy (in meters)")
            .addHeader("speed in kmph")
            .addHeader("brearing (in deg)")
            .getHeader();
    public static final String FILE_NAME_SEPARATOR = "#";
    public static final long MIN_LOCATION_UPADATE_INTERVAL_IN_MILLISECONDS = 20;
    public static final float MIN_DISTANCE_LOCATION_UPDATE_IN_METERS = 0.0f;
    public static String LOG_DIRNAME = "logger";
    public static String ACCELERATION_LOGGER_DEFAULT_HEADER = new Header()
            .addHeader("timestamp")
            .addHeader("ax")
            .addHeader("ay")
            .addHeader("az")
            .getHeader();
}
