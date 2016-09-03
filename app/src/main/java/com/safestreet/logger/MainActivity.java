package com.safestreet.logger;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements GpsStatus.NmeaListener,
        GoogleApiClient
                .ConnectionCallbacks, GpsStatus.Listener, GoogleApiClient
                .OnConnectionFailedListener,
        LocationListener {

    // MY_PERMISSIONS_REQUEST_ACCESS_LOCATION can be any random number
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 10;
    private static final int REQUEST_CHECK_SETTINGS = 11;
    private final String TAG = "logger";
    LocationSettingsRequest.Builder builder = null;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private TextView mAnLatitudeText;
    private TextView mAnLongitudeText;
    private boolean mRequestingLocationUpdates = true;
    private LocationManager mLocationManager = null;
    private LocationRequest mLocationRequest = null;
    private Location mCurrentLocation = null;
    private String mLastUpdateTime;
    private TextView mLastUpdateTimeTextView;
    private TextView mAccuracyTextView;
    private TextView mBearingTextView;
    private TextView mAltitudeTextView;
    private TextView mSpeedTextView;
    private TextView mProviderTextView;
    private Float mAccuracy = 0.0f;
    private Float mBearing = 0.0f;
    private double mAltitude = 0.0;
    private Float mSpeed = 0.0f;
    private String mProvider = null;
    private Long mTime = 0L;

    // create Location request for google play services api
    // 1. Set Up a Location Request
    private int UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private int FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private TextView mSatelliteTextView;
    private TextView mDOPTextView;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart(): Connecting to Google API client");
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop(): disconnecting to Google API client");
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState); // Always call the superclass first

        setContentView(R.layout.activity_main);

        // create link between java objects and views
        setupUIReferences();

        // connect to Google API Client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }

        // create Location request for google play services api
        // 1. Set Up a Location Request
        createLocationRequest();

        // 2. get current location settings.
        getCurrentLocationSettings();

        // check if current location settings are satisfied or not
        checkLocationSettings();

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
                                    MainActivity.this,
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

    private void setupUIReferences() {
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_updated_time_tv);
        mLatitudeText = (TextView) findViewById(R.id.lat_tv);
        mLongitudeText = (TextView) findViewById(R.id.long_tv);
        mAccuracyTextView = (TextView) findViewById(R.id.accuracy_tv);
        mSpeedTextView = (TextView) findViewById(R.id.speed_tv);
        mProviderTextView = (TextView) findViewById(R.id.provider_tv);
        mAltitudeTextView = (TextView) findViewById(R.id.altitude_tv);
        mBearingTextView = (TextView) findViewById(R.id.bearing_tv);
        mSatelliteTextView = (TextView) findViewById(R.id.satellite_tv);
        mDOPTextView = (TextView) findViewById(R.id.dop_tv);
        Spinner travelModeSpinner = (Spinner) findViewById(R.id.travel_mode_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array
                .travel_mode_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelModeSpinner.setAdapter(adapter);
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.d(TAG, "onConnected(): successfully connected to Google API client");
        // As of now mRequestingLocationUpdates is just a dummy variable it's value is always true
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "startLocationUpdates(): permission not granted asking for permission");
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d(TAG, "startLocationUpdates(): requesting location updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
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

    @Override
    public void onLocationChanged(Location location) {
//        Log.d(TAG, "onLocationChanged(): getting current location");
        mCurrentLocation = location;
//        Log.d(TAG, "onLocationChanged(): getting current time");
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mAccuracy = location.getAccuracy();
        mSpeed = location.getSpeed();
        mAltitude = location.getAltitude();
        mBearing = location.getBearing();
        mProvider = location.getProvider();
        mTime = location.getTime();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                updateUI();
//            }
//        });
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });
//        updateUI();
    }

    private void updateUI() {

//        Log.d(TAG, "updateUI(): updating lat long and time");
        mLatitudeText.setText(String.valueOf(mLatitude));
        mLongitudeText.setText(String.valueOf(mLongitude));
        mAccuracyTextView.setText(String.valueOf(mAccuracy));
        mAltitudeTextView.setText(String.valueOf(mAltitude));
        mSpeedTextView.setText(String.valueOf(mSpeed));
        mBearingTextView.setText(String.valueOf(mBearing));
        mProviderTextView.setText(String.valueOf(mProvider));
        mLastUpdateTimeTextView.setText(mLastUpdateTime);
    }

    @Override
    public void onNmeaReceived(long timestamp, String nmea) {

        Log.d(TAG, "timestamp: " + timestamp + "" + nmea);
    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

}


