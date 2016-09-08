package util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import app.LoggerApplication;
import io.LogWriter;
import logger.nmea.NmeaSentence;
import math.UnitConverter;
import ui.component.AndroidLocationUI;

/**
 * Created by vikrant on 4/9/16.
 */
public class AndroidLocationUtil {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 10;
    // update interval of location is set to 1 second
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final float MIN_DISTANCE = 0;
    private static final String TAG = AndroidLocationUtil.class.getSimpleName();
    // location fields
    protected Float mAccuracy = 0.0f;
    protected Float mBearing = 0.0f;
    protected double mAltitude = 0.0;
    protected String mLastUpdateTime;
    protected float mSpeed = 0.0f;
    protected String mTime = "-1";
    protected double mLatitude = 0.0;
    protected double mLongitude = 0.0;
    protected int mSatelliteInUse = 0;
    protected int mSatelliteInView = 0;
    /**
     * HDOP – horizontal dilution of precision
     * VDOP – vertical dilution of precision
     * PDOP – position (3D) dilution of precision
     */
    protected String mPDOP;
    protected String mHDOP;
    protected String mVDOP;
    protected String mProvider = K.NONE;

    private Activity activity;
    private LocationManager mLocationManager;
    private AndroidLocationUI androidLocationUI;
    private NmeaSentence nmea = new NmeaSentence("");
    private String mGpsMode = K.NO_FIX;
    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            //                        Log.d(TAG, "onGpsStatusChanged: event " + event);
            GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
            if (gpsStatus != null) {
                mSatelliteInUse = 0;
                mSatelliteInView = 0;
                Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                Iterator<GpsSatellite> sat = satellites.iterator();
                while (sat.hasNext()) {
                    GpsSatellite satellite = sat.next();
                    if (satellite.usedInFix())
                        mSatelliteInUse++;
                    mSatelliteInView++;
                }
            }
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    mGpsMode = "Searching";
                    //                    Toast.makeText(activity, "GPS_SEARCHING", Toast
                    // .LENGTH_SHORT).show(); System.out.println("TAG - GPS searching: ");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    //                    System.out.println("TAG - GPS Stopped");
                    mGpsMode = "Stopped";
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                /*
                 * GPS_EVENT_FIRST_FIX Event is called when GPS is locked
                 */
                    //                    Toast.makeText(activity, "GPS_LOCKED", Toast
                    // .LENGTH_SHORT).show();
                    /*
                     * Removing the GPS status listener once GPS is locked
                     */
                    //                    mLocationManager.removeGpsStatusListener
                    // (gpsStatusListener);
                    mGpsMode = "Fixed";

                    break;
                //                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                //                    mGpsMode = K.NO_FIX;
                //                    //                    System.out.println("TAG -
                // GPS_EVENT_SATELLITE_STATUS");
                //                    break;
            }
            if (gpsStatus != null) {
                androidLocationUI.updateGpsStatusUI();
                androidLocationUI.updateSatelliteUI();
            }

        }
    };
    private GpsStatus.NmeaListener nmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmeaSentence) {

            if (Strings.isNullOrEmpty(nmeaSentence)) {
                return;
            }
            nmea.setNmeaSentence(nmeaSentence);
            if (nmea.isLocationSentence()) {
                mPDOP = nmea.getLatestPdop();
                mHDOP = nmea.getLatestHdop();
                mVDOP = nmea.getLatestVdop();
                androidLocationUI.updateNemaUI();
            }

        }
    };
    private LogWriter logWriter;
    private LoggerApplication application;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            // Log.d(TAG, "onLocationChanged(): getting current time");
            setLocation(location);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            mTime = new SimpleDateFormat(K.TIMESTAMP_FORMAT_STRING).format(Calendar
                    .getInstance().getTime()).toString();
            androidLocationUI.updateUI();
            if (application.isLogging()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveData();
                    }
                }).start();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

            Log.d(TAG, "onProviderEnabled: provider=" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            AndroidLocationUtil.this.activity.startActivity(intent);

        }
    };
    private String logStartTime;

    public AndroidLocationUtil(Activity activity) {
        this.activity = activity;
        this.androidLocationUI = new AndroidLocationUI(activity, this);
        application = (LoggerApplication) this.activity.getApplication();

    }

    public String getVDOP() {
        return mVDOP;
    }

    public String getHDOP() {
        return mHDOP;
    }

    public String getPDOP() {
        return mPDOP;
    }

    public int getSatelliteInView() {
        return mSatelliteInView;
    }

    public int getSatelliteInUse() {
        return mSatelliteInUse;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getLatitude() {
        return String.format(K.SEVEN_DECIMAL_PLACES, mLatitude);
    }

    public String getTime() {
        return mTime;
    }

    public String getSpeed() {
        return String.format(K.TWO_DECIMAL_PLACE, UnitConverter.fromMpsToKmph(mSpeed));
    }

    public String getLastUpdateTime() {
        return mLastUpdateTime;
    }

    public String getProvider() {
        return mProvider;
    }

    public double getAltitude() {
        return mAltitude;
    }

    public Float getBearing() {
        return mBearing;
    }

    public Float getAccuracy() {
        return mAccuracy;
    }

    synchronized private void saveData() {
        logWriter.write(K.FIELD_SEPARATOR +
                getTime() + K.FIELD_SEPARATOR +
                getLatitude() + K.FIELD_SEPARATOR +
                getLongitude() + K.FIELD_SEPARATOR +
                getAccuracy() + K.FIELD_SEPARATOR +
                getAltitude() + K.FIELD_SEPARATOR +
                getSpeed() + K.FIELD_SEPARATOR +
                getBearing() + K.FIELD_SEPARATOR +
                getSatelliteInUse() + K.FIELD_SEPARATOR +
                getSatelliteInView() + K.FIELD_SEPARATOR +
                getPDOP() + K.FIELD_SEPARATOR +
                getHDOP() + K.FIELD_SEPARATOR +
                getVDOP() + K.FIELD_SEPARATOR + K.NEWLINE
        );
    }

    public void init() {

        // getting location instance of location manager
        mLocationManager = (LocationManager) this.activity.getSystemService(Context
                .LOCATION_SERVICE);
        // checking run time location access permissions only for API version >= 23 (Marshmallow)
        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission
                .ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // if permission is already granted request for permissions
            //            ActivityCompat.requestPermissions(
            //                    this.activity, new String[]{Manifest.permission
            // .ACCESS_FINE_LOCATION},
            //                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                UPDATE_INTERVAL_IN_MILLISECONDS, MIN_DISTANCE, locationListener);
        mLocationManager.addGpsStatusListener(gpsStatusListener);
        mLocationManager.addNmeaListener(nmeaListener);
    }

    public void startLogging() {
        Log.d(TAG, "Logging started");
        // checking for storage permission
        logWriter = new LogWriter(activity, androidLocationUI, K.LOG_FILE_NAME_PREFIX);
        logWriter.write(K.GOOGLE_API_HEADER);
        // log file name gpslog-current-date-time.txt
    }


    public void stopLogging() {
        logWriter.writeLogTemplate();
        logWriter.close();
    }

    private void setLocation(Location location) {
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mAccuracy = location.getAccuracy();
        mSpeed = location.getSpeed();
        mAltitude = location.getAltitude();
        mBearing = location.getBearing();
        mProvider = location.getProvider();
    }

    public String getGpsMode() {
        return mGpsMode;
    }
}
