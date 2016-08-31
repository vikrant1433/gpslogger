package com.safestreet.logger;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements GoogleApiClient
        .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // 10 can be any number
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 10;
    private static final int REQUEST_CHECK_SETTINGS = 11;
    private final String TAG = "logger";
    LocationSettingsRequest.Builder builder = null;
    //    private Button getLocationBtn;
    //    private TextView latlongTV;
    //    private LocationManager locationManager;
    //    private LocationListener listener;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private TextView mAnLatitudeText;
    private TextView mAnLongitudeText;
    //    private Button getLocBtn;
    private boolean mRequestingLocationUpdates = true;
    // android API location objects
    private LocationManager mLocationManager = null;
    private com.google.android.gms.location.LocationListener mLocationListener = null;
    private LocationRequest mLocationRequest = null;
    private Location mCurrentLocation = null;
    private String mLastUpdateTime;
    private TextView mLastUpdateTimeTextView;

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
        setup();

        // connect to Google API Client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }

//
//        latlongTV = (TextView) findViewById(R.id.latlongTV);
//        getLocationBtn = (Button) findViewById(R.id.getLocationBtn);
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//
//        listener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                String lat = "Latitude: " + location.getLatitude();
//                String lng = "Longitude: " + location.getLongitude();
//
//                latlongTV.append(lat + "\n" + lng);
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//            }
//        };

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
// != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest
// .permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
// Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
//            }
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        } else {
//            configure_button();
//        }
//        locationManager.requestLocationUpdates("gps", 5000, 0, listener);

        // bind showLocation with getLocBtn
//        getLocBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showLocation();
//            }
//        });

        // create Location request for google play services api
        // 1. Set Up a Location Request

        createLocationRequest();

        // 2. get current location settings.
        getCurrentLocationSettings();

        // check if current location settings are satisfied or not
        checkLocationSettings();

        /*
         * GETTING LOCATION INFO USING ANDROID API
         */
        // getting location from  android API
//        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        // define a listener that responds to location updates
//        mLocationListener = new LocationListener() {
//
//            @Override
//            public void onLocationChanged(Location location) {
//                mAnLongitudeText.setText("android API Longitude:" + String.valueOf(location
//                        .getLongitude()));
//                mAnLatitudeText.setText("android API Latitude" + String.valueOf(location
//                        .getLatitude()));
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            ActivityCompat.requestPermissions(
//                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
//                mLocationListener);
        /*
        * END
        * */
    }

    // create Location request for google play services api
    // 1. Set Up a Location Request
    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest(): creating location request for google play services " +
                "location API");
        // Set Up a Location Request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5000);
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

    private void setup() {
//        mAnLatitudeText = (TextView) findViewById(R.id.anlatTV);
//        mAnLongitudeText = (TextView) findViewById(R.id.anlongTV);
        mLatitudeText = (TextView) findViewById(R.id.lat_tv);
        mLongitudeText = (TextView) findViewById(R.id.long_tv);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_updated_time);
//        getLocBtn = (Button) findViewById(R.id.getLocationBtn);
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.d(TAG, "onConnected(): successfully connected to Google API client");
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission
// .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
// .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
// PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            ActivityCompat.requestPermissions(
//                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        } else {
////            showLocation();
//        }
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

///*    @Override //    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
// permissions,
//    @NonNull int[] grantResults) {
//        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_LOCATION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // we can now safely use the API we requested access to
////                showLocation();
//            }
//        } else {
//            // permission was denied or request was cancelled
//        }
//    }*/

    private void showLocation() {

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
// != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest
// .permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "permission not granted");
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            ActivityCompat.requestPermissions(
//                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        Log.d(TAG, "inside showLocation() new ");
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            Log.d(TAG, "mLastLocation is not null");
//            mLatitudeText.setText("Google Play Services API Lat: " + String.valueOf
// (mLastLocation.getLatitude()));
//            mLongitudeText.setText("Google Play Services API Long: " + String.valueOf
// (mLastLocation.getLongitude()));
//        } else {
//            Log.d(TAG, "gps is not on");
////            ActivityCompat.requestPermissions(
////                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
////                            Manifest.permission.ACCESS_FINE_LOCATION},
////                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection to Google API is suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


        Log.d(TAG, "An unresolvable error has occurred and a connection to Google APIs could not " +
                "be established.");
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged(): getting current location");
        mCurrentLocation = location;
        Log.d(TAG, "onLocationChanged(): getting current time");
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {

        Log.d(TAG, "updateUI(): updating lat long and time");
        mLatitudeText.setText("Play Services API Lat: " + String.valueOf
                (mCurrentLocation.getLatitude()));
        mLongitudeText.setText("Play Services API Long: " + String.valueOf
                (mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText(mLastUpdateTime);
    }

//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
// @NonNull int[] grantResults) {
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//            configure_button();
//    }

//    void configure_button() {
//        // first check for permissions
////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission
// .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
// .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
// .PERMISSION_GRANTED) {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
// Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
////                        , 10);
////            }
////            return;
////        }
//        // this code won'latlongTV execute IF permissions are not allowed, because in the line
// above there is return statement.
//        getLocationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //noinspection MissingPermission
//                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
//            }
//        });
//    }
}


