package com.safestreet.logger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ui.UI;
import util.AndroidLocationUtil;
import util.GoogleLocationUtil;


public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private AndroidLocationUtil androidLocationUtil;
    private GoogleLocationUtil googleLocationUtil;
    private UI ui = new UI(this);

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
        androidLocationUtil = new AndroidLocationUtil(this);
        googleLocationUtil = new GoogleLocationUtil(this);
        setContentView(R.layout.activity_main);

        // initializing spinner
        ui.initSpinner();
        googleLocationUtil.initGoogleApiClient();
        androidLocationUtil.init();
    }

}


