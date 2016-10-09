package activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.safestreet.logger.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import app.AppState;
import butterknife.ButterKnife;
import logger.AccelerometerLogger;
import logger.LocationLogger;
import model.Phone;
import sensor.Acceleration;
import util.K;
import util.Permission;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int EXTERNAL_STORAGE_READ_WRITE_REQUEST_CODE = 2;
    private final String TAG = MainActivity.class.getSimpleName();
    AppState application;
    //    private AndroidLocationUtil androidLocationUtil;
    //    private  GoogleLocationUtil googleLocationUtil;
    //    private MainUI ui = null;
    LocationManager locationManager;
    private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private WindowManager mWindowManager;
    private Display mDisplay;
    private PowerManager.WakeLock mWakeLock;
    private Sensor mAccelerometer;
    private AccelerometerLogger accelerometerLogger;
    private LocationLogger locationLogger;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {


            final Location newLocation = location;
            // change speed to kmph
            newLocation.setSpeed(location.getSpeed() * 3.6f);

//            Log.d(TAG, "inside onlocation changed " + newLocation.toString());
            if (application.isLogging()) {
                Log.d(TAG, "application.isLogging is true");
                locationLogger.writeLocation(newLocation);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {


        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * when the activity is resumed, we acquire a wake-lock so that the
         * screen stays on, since the user will likely not be fiddling with the
         * screen or buttons.
         */
        mWakeLock.acquire();
        startAccelerometer();
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*
         * When the activity is paused, release our sensor resources and wake locks
         */

        mWakeLock.release();
        stopAccelerometer();
    }

    public void startAccelerometer() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stopAccelerometer() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (application.isLogging()) {
            stopLogging(null);
        }
    }

    @Override
    protected void onStart() {
        //        Log.d(TAG, "onStart: Connecting to Google API client");
        //        googleLocationUtil.connectToGoogleApiClient();
        super.onStart();
    }

    @Override
    protected void onStop() {
        //        Log.d(TAG, "onStop: disconnecting to Google API client");
        //        googleLocationUtil.disconnectToGoogleApiClient();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState); // Always call the superclass first
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        application = (AppState) this.getApplication();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        // [START LOCATION UPDATE]
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
                    .ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, LOCATION_REQUEST_CODE);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, K
                        .MIN_LOCATION_UPADATE_INTERVAL_IN_MILLISECONDS, K
                        .MIN_DISTANCE_LOCATION_UPDATE_IN_METERS,
                locationListener);
        //[END LOCATION UPDATE]


        //        androidLocationUtil = new AndroidLocationUtil(this);
        //        googleLocationUtil = new GoogleLocationUtil(this);

        // initializing ui this includes spinner
        //        if (ui == null) {
        //            ui = new MainUI(this);
        //        }
        //        googleLocationUtil.initGoogleApiClient();
        //        androidLocationUtil.init();


        accelerometerInit();
    }


    private void accelerometerInit() {

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

        // Get an instance of the WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        // Create a bright wake lock
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
                .getName());

    }

    private void startRecording() {

        if (application.isLogging() == false) {
            Toast.makeText(MainActivity.this, "Started writing to file", Toast.LENGTH_SHORT)
                    .show();

            accelerometerLogger = new AccelerometerLogger("acclog#" + Phone.getDeviceName());
            locationLogger = new LocationLogger("gpslogger#" + Phone.getDeviceName());
            locationLogger.startLogging();
            accelerometerLogger.startLogging();

            // it is important to set the logging state to true after setting up logger instances
            application.setLogging(true);


        } else {
            Toast.makeText(MainActivity.this, "Logging is running, First Stop it", Toast
                    .LENGTH_SHORT).show();
        }
    }

    public void startLogging(View view) {
        // checking if we have write permission
        if (Permission.hasReadWritePermission(this)) {
            startRecording();
        } else {
            // if permission is not there ask for it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_READ_WRITE_REQUEST_CODE);
            startRecording();
            //            startLogging(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_READ_WRITE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Write Permission has been granted", Toast
                            .LENGTH_SHORT).show();
                    Log.d(TAG, "Write Permission has been granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(MainActivity.this, "Write Permission is not granted", Toast
                            .LENGTH_SHORT).show();
                    Log.d(TAG, "Write Permission is not granted");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                            .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission
                                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
                                .ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET
                        }, LOCATION_REQUEST_CODE);
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[]
                        // permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the
                        // documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, K
                            .MIN_LOCATION_UPADATE_INTERVAL_IN_MILLISECONDS, K
                            .MIN_DISTANCE_LOCATION_UPDATE_IN_METERS, locationListener);
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void stopLogging(View view) {
        if (application.isLogging()) {
            Toast.makeText(MainActivity.this, "Log has been saved to file in your download " +
                    "directory", Toast.LENGTH_SHORT).show();
            application.setLogging(false);
            application.setStopLogTime(new SimpleDateFormat(K.TIMESTAMP_FORMAT_STRING).format
                    (Calendar.getInstance().getTime()).toString());
            //            androidLocationUtil.stopLogging();
            locationLogger.stopLogging();
            accelerometerLogger.stopLogging();
        } else {
            Toast.makeText(MainActivity.this, "First start the logging", Toast.LENGTH_SHORT).show();
        }
    }


    public void addRemark(View view) {
        // TODO: append remark to last log file
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        if (((AppState) this.getApplication()).isLogging()) {
//            Log.d(TAG, event.values[0] + "");
            accelerometerLogger.writeAcceleration(
                    new Acceleration(event.values[0], event.values[1], event.values[2])
            );
            //            accelerometerLogger.write(new Acceleration(event.values[0], event
            // .values[1], event.values[2]).toString());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}


