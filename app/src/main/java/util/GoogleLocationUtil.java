package util;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.safestreet.logger.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.LoggerApplication;
import io.LogWriter;
import math.UnitConverter;
import ui.component.GoogleLocationUI;

/**
 * Created by vikrant on 4/9/16.
 */
public class GoogleLocationUtil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient
        .OnConnectionFailedListener, LocationListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 10;
    private static final int REQUEST_CHECK_SETTINGS = 11;
    private static final int MIN_DISTANCE = 0;
    private final String TAG = GoogleLocationUtil.class.getSimpleName();
    private final LoggerApplication application;
    private final GoogleLocationUI googleLocationUI;
    private Location location;
    private int UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private String mLastUpdateTime;
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
    private GoogleApiClient mGoogleApiClient = null;
    private LocationRequest mLocationRequest = null;
    private LocationSettingsRequest.Builder builder = null;
    private boolean mRequestingLocationUpdates = true;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private double mAltitude = 0.0;
    private Float mAccuracy = 0.0f;
    private Float mBearing = 0.0f;
    private Float mSpeed = 0.0f;
    private String mProvider = null;
    private String mTime = "N/A";
    private Activity activity;
    private LogWriter logWriter;

    public GoogleLocationUtil(Activity activity) {
        this.activity = activity;
        this.googleLocationUI = new GoogleLocationUI(activity, this);
        application = (LoggerApplication) this.activity.getApplication();
    }

    public String getLatitude() {
        //        return String.format("%.") mLatitude;
        return String.format("%.6f", mLatitude);
    }

    public String getLongitude() {
        return String.format("%.6f", mLongitude);
    }

    public double getmAltitude() {
        return mAltitude;
    }

    public Float getmAccuracy() {
        return mAccuracy;
    }

    public Float getBearing() {
        return mBearing;
    }

    public String getSpeed() {
        return String.format("%.2f", UnitConverter.fromMpsToKmph(mSpeed));
    }

    public void setUpUI() {
        Log.d(TAG, "activity name: " + activity.getClass().getSimpleName());

        mLastUpdateTimeTextView = (TextView) this.activity.findViewById(R.id.last_updated_time_tv);
        mLatitudeText = (TextView) this.activity.findViewById(R.id.lat_tv);
        mLongitudeText = (TextView) this.activity.findViewById(R.id.long_tv);
        mAccuracyTextView = (TextView) this.activity.findViewById(R.id.accuracy_tv);
        mSpeedTextView = (TextView) this.activity.findViewById(R.id.speed_tv);
        mProviderTextView = (TextView) this.activity.findViewById(R.id.provider_tv);
        mAltitudeTextView = (TextView) this.activity.findViewById(R.id.altitude_tv);
        mBearingTextView = (TextView) this.activity.findViewById(R.id.bearing_tv);
        mSatelliteTextView = (TextView) this.activity.findViewById(R.id.satellite_tv);
        mDOPTextView = (TextView) this.activity.findViewById(R.id.dop_tv);
    }

    private void updateUI() {
        if (mLongitudeText == null) {
            Log.d(TAG, "updateUI: mLogitudeText is null");
            setUpUI();
        }
        mLongitudeText.setText(getLongitude());
        mLatitudeText.setText(getLatitude());
        mAccuracyTextView.setText(String.valueOf(mAccuracy));
        mAltitudeTextView.setText(String.valueOf(mAltitude));
        mSpeedTextView.setText(getSpeed());
        mBearingTextView.setText(String.valueOf(mBearing));
        mProviderTextView.setText(String.valueOf(mProvider));
        mLastUpdateTimeTextView.setText(mLastUpdateTime);

    }

    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest(): creating location request for google play services " +
                "location API");
        // Set Up a Location Request
        mLocationRequest = new LocationRequest();
        // Set the location update interval to 1000 ms  = 1 second
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Set the fasted location update interval to 1000 ms  = 1 second
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // 2. get current location settings.
    private void getCurrentLocationSettings() {
        // Get Current Location Settings
        Log.d(TAG, "getCurrentLocationSettings(): Get Current Location Settings");
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
    }

    // check if current location settings are satisfied or not
    private void checkLocationSettings() {
        // Next check whether the current location settings are satisfied:
        Log.d(TAG, "checkLocationSettings(): check whether the current location settings are " +
                "satisfied");
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        // check the location settings by looking at the status code from the LocationSettingsResult
        // object
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStates = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        //                 ...
                        Log.d(TAG, "All location settings are satisfied. The client can " +
                                "initialize location requests here.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "Location settings are not satisfied");
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // To prompt the user for permission to modify the location settings
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            Log.d(TAG, "To prompt the user for permission to modify the location " +
                                    "settings");
                            status.startResolutionForResult(
                                    GoogleLocationUtil.this.activity,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.d(TAG, "Error occured while prompting user to modify the location" +
                                    "settings");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        //                 ...
                        Log.d(TAG, "Location settings are not satisfied");
                        break;
                }
            }
        });


    }

    @Override
    public void onLocationChanged(Location location) {
        //        Log.d(TAG, "onLocationChanged(): getting current location");
        setLocation(location);
        updateUI();

        //                Log.d(TAG, "onLocationChanged(): getting current time");
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mTime = new SimpleDateFormat(K.TIMESTAMP_FORMAT_STRING).format(Calendar
                .getInstance().getTime()).toString();
        if (application.isLogging()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveDate();
                }
            }).start();
        }
    }

    synchronized private void saveDate() {

        logWriter.write(K.FIELD_SEPARATOR +
                getTime() + K.FIELD_SEPARATOR +
                mLatitude + K.FIELD_SEPARATOR +
                mLongitude + K.FIELD_SEPARATOR +
                mAccuracy + K.FIELD_SEPARATOR +
                mAltitude + K.FIELD_SEPARATOR +
                getSpeed() + K.FIELD_SEPARATOR +
                getBearing() + K.FIELD_SEPARATOR + K.NEWLINE
        );

    }

    private void startLocationUpdates() {

        Log.d(TAG, "startLocationUpdates(): requesting location updates");
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission
                .ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void setLocation(Location location) {

        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mAccuracy = location.getAccuracy();
        mSpeed = location.getSpeed();
        mAltitude = location.getAltitude();
        mBearing = location.getBearing();
        mProvider = location.getProvider();
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.d(TAG, "onConnected(): successfully connected to Google API client");
        // As of now mRequestingLocationUpdates is just a dummy variable it's value is always true
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call
        // connectToGoogleApiClient() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
        Log.i(TAG, "Connection to Google API is suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult
                .getErrorCode());
        Log.d(TAG, "An unresolvable error has occurred and a connection to Google APIs could not " +
                "be established.");
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

    }

    public void initGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }
        createLocationRequest();
        getCurrentLocationSettings();
        checkLocationSettings();
    }

    public void connectToGoogleApiClient() {
        mGoogleApiClient.connect();
    }

    public void disconnectToGoogleApiClient() {
        mGoogleApiClient.disconnect();
    }

    public void stopLogging() {
        logWriter.writeLogTemplate();
        logWriter.close();
    }

    public void startLogging() {
        // checking for storage permission
        logWriter = new LogWriter(activity, googleLocationUI, "play-store-api-gpslog");
        logWriter.write(K.GOOGLE_API_HEADER);
        // it is important to give a different prefix name to log file else it will over
        // write the  any existing file with same prefix + timestamp
        // log file name gpslog-current-date-time.txt
    }

    public String getTime() {
        return mTime;
    }
}
