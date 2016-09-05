package util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.safestreet.logger.R;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by vikrant on 4/9/16.
 */
public class AndroidLocationUtil {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 10;
    // update interval of location is set to 1 second
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final float MIN_DISTANCE = 0;
    private static final String TAG = AndroidLocationUtil.class.getSimpleName();
    // ui fields
    private TextView mLastUpdateTimeTextView;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private TextView mAccuracyTextView;
    private TextView mBearingTextView;
    private TextView mAltitudeTextView;
    private TextView mSpeedTextView;
    private TextView mProviderTextView;
    private TextView mSatelliteTextView;
    private TextView mDOPTextView;
    //
    private Activity activity;
    private LocationManager mLocationManager;
    // location fields
    private Float mAccuracy = 0.0f;
    private Float mBearing = 0.0f;
    private double mAltitude = 0.0;
    private String mProvider = "None";
    private String mLastUpdateTime;
    private float mSpeed = 0.0f;
    private long mTime = 0;
    private double mLatitude = 0.0;
    private double mLongitude = 0.0;
    private int mSatelliteInUse = 0;
    private int mSatelliteInView = 0;
    private double mDOP = 0.0;
    private double mHDOP = 0.0;
    private double mVDOP = 0.0;

    private GpsStatus.NmeaListener nmeaListener = new GpsStatus.NmeaListener() {
        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            Log.d(TAG, "timstamp: " + timestamp + " nmea " + nmea);
        }
    };
    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            Log.d(TAG, "onGpsStatusChanged: event " + event);
        }
    };
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            // Log.d(TAG, "onLocationChanged(): getting current time");
            setLocation(location);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: provider=" + provider + " status " + status + " " +
                    "satellitesInUse" + extras.getInt("satellites"));

        }

        @Override
        public void onProviderEnabled(String provider) {

            Log.d(TAG, "onProviderEnabled: provider=" +provider );
        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            AndroidLocationUtil.this.activity.startActivity(intent);

        }
    };

    public AndroidLocationUtil(Activity activity) {
        this.activity = activity;
    }

    private void updateUI() {

        mLatitudeText.setText(String.valueOf(mLatitude));
        mLongitudeText.setText(String.valueOf(mLongitude));
        mAccuracyTextView.setText(String.valueOf(mAccuracy));
        mAltitudeTextView.setText(String.valueOf(mAltitude));
        mSpeedTextView.setText(String.valueOf(mSpeed));
        mBearingTextView.setText(String.valueOf(mBearing));
        mProviderTextView.setText(String.valueOf(mProvider));
        mLastUpdateTimeTextView.setText(mLastUpdateTime);
        mSatelliteTextView.setText(mSatelliteInUse + "/" + mSatelliteInView);
        mDOPTextView.setText(mDOP + "/" + mVDOP + "/" + mHDOP);
    }

    public void init() {
        mLocationManager = (LocationManager) this.activity.getSystemService(Context
                .LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission
                .ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this
                        .activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(
                    this.activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
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

        setUpUIReferences();
    }

    private void setUpUIReferences() {

        mLastUpdateTimeTextView = (TextView) this.activity.findViewById(R.id
                .a_last_updated_time_tv);
        mLatitudeText = (TextView) this.activity.findViewById(R.id.a_lat_tv);
        mLongitudeText = (TextView) this.activity.findViewById(R.id.a_long_tv);
        mAccuracyTextView = (TextView) this.activity.findViewById(R.id.a_accuracy_tv);
        mSpeedTextView = (TextView) this.activity.findViewById(R.id.a_speed_tv);
        mProviderTextView = (TextView) this.activity.findViewById(R.id.a_provider_tv);
        mAltitudeTextView = (TextView) this.activity.findViewById(R.id.a_altitude_tv);
        mBearingTextView = (TextView) this.activity.findViewById(R.id.a_bearing_tv);
        mSatelliteTextView = (TextView) this.activity.findViewById(R.id.a_satellite_tv);
        mDOPTextView = (TextView) this.activity.findViewById(R.id.a_dop_tv);
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
        mTime = location.getTime();
    }
}
