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
    public static final String FIELD_SEPARATOR = "|";
    public static final String NONE = "--";
    public static final String ANDROID_LOCATION_HEADER_STRING =
            "|timestamp|latitude|longitude|accuracy(in meters)|altitude(in meters)|speed(Km/hr)" +
                    "|bearing(in degree)|satellite-in-use" +
                    "|satellite-in-view|pdop|hdop|vdop|" + NEWLINE;
    public static final String NO_FIX = "No Fix Available";
    public static final String GOOGLE_API_HEADER = "|timestamp|latitude|longitude|accuracy(in " +
            "meters)|altitude(in meters)|speed(Km/hr)|bearing(in degree)|" + NEWLINE;

    public static final String SEVEN_DECIMAL_PLACES = "%.7f";
    public static final String TWO_DECIMAL_PLACE = "%.2f";
    public static final int READ_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    public static String LOG_DIRNAME = "logger";
}
