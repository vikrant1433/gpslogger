package activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.safestreet.logger.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import app.LoggerApplication;
import ui.component.MainUI;
import util.AndroidLocationUtil;
import util.GoogleLocationUtil;
import util.K;
import util.Permission;


public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    LoggerApplication application;
    private AndroidLocationUtil androidLocationUtil;
    private GoogleLocationUtil googleLocationUtil;
    private MainUI ui = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (application.isLogging()) {
            stopLogging(null);
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: Connecting to Google API client");
        googleLocationUtil.connectToGoogleApiClient();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: disconnecting to Google API client");
        googleLocationUtil.disconnectToGoogleApiClient();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState); // Always call the superclass first
        setContentView(R.layout.activity_main);

        application = (LoggerApplication) this.getApplication();
        androidLocationUtil = new AndroidLocationUtil(this);
        googleLocationUtil = new GoogleLocationUtil(this);

        // initializing ui this includes spinner
        if (ui == null) {
            ui = new MainUI(this);
        }
        googleLocationUtil.initGoogleApiClient();
        androidLocationUtil.init();
    }

    public void startLogging(View view) {
        if (Permission.hasReadWritePermission(this)) {

            if (application.isLogging() == false) {
                application.setLogging(true);
                Toast.makeText(MainActivity.this, "Started writing to file", Toast.LENGTH_SHORT)
                        .show();
                application.setStartLogTime(new SimpleDateFormat(K.TIMESTAMP_FORMAT_STRING)
                        .format(Calendar.getInstance().getTime()).toString());
                androidLocationUtil.startLogging();
                googleLocationUtil.startLogging();
            } else {
                Toast.makeText(MainActivity.this, "Logging is running, First Stop it", Toast
                        .LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, K
                    .READ_WRITE_EXTERNAL_STORAGE_PERMISSION);
//            startLogging(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case K.READ_WRITE_EXTERNAL_STORAGE_PERMISSION: {
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
            androidLocationUtil.stopLogging();
            googleLocationUtil.stopLogging();
        } else {
            Toast.makeText(MainActivity.this, "First start the logging", Toast.LENGTH_SHORT).show();
        }
    }


    public void addRemark(View view) {
        // TODO: append remark to last log file
    }
}


