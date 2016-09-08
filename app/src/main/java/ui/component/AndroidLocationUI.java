package ui.component;

import android.app.Activity;
import android.widget.TextView;

import com.safestreet.logger.R;

import util.AndroidLocationUtil;

/**
 * Created by vikrant on 6/9/16.
 */
public class AndroidLocationUI extends MainUI {
    private static final String TAG = AndroidLocationUI.class.getSimpleName();
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected TextView mAccuracyTextView;
    protected TextView mBearingTextView;
    protected TextView mAltitudeTextView;
    protected TextView mSpeedTextView;
    protected TextView mProviderTextView;
    protected TextView mPDOPTextView;
    protected TextView mHDOPTextView;
    protected TextView mVDOPTextView;
    protected TextView mSatelliteInUseTextView;
    protected TextView mSatelliteInViewTextView;
    protected TextView mGpsModeTextView;
    private AndroidLocationUtil androidLocationUtil;

    public AndroidLocationUI(Activity activity, AndroidLocationUtil androidLocationUtil) {
        super(activity);
        this.androidLocationUtil = androidLocationUtil;
        setUpUIReferences();

    }

    public void updateNemaUI() {
        //        String gpsMode = androidLocationUtil.getGpsMode();
        //
        //        if (isChanged(gpsMode, mGpsModeTextView.getText().toString())) {
        //            mGpsModeTextView.setText(gpsMode);
        //        }

        //        String satInUse = androidLocationUtil.getSatelliteInUse();
        //        String satInView = androidLocationUtil.getSatelliteInView();

        //        if (satInUse.isEmpty()) satInUse = "--";
        //        if (satInView.isEmpty()) satInView = "--";

        //        if (isChanged(satInUse, mSatelliteInUseTextView.getText().toString())) {
        //            mSatelliteInUseTextView.setText(satInUse);
        //        }

        //        if (isChanged(satInView, mSatelliteInViewTextView.getText().toString())) {
        //            mSatelliteInViewTextView.setText(satInView);
        //        }

        String pdop = androidLocationUtil.getPDOP();
        String hdop = androidLocationUtil.getHDOP();
        String vdop = androidLocationUtil.getVDOP();

        pdop = pdop.isEmpty() ? "--" : pdop;
        hdop = hdop.isEmpty() ? "--" : hdop;
        vdop = vdop.isEmpty() ? "--" : vdop;

        if (isChanged(pdop, mPDOPTextView.getText().toString())) {
            mPDOPTextView.setText(pdop);
        }

        if (isChanged(hdop, mHDOPTextView.getText().toString())) {
            mHDOPTextView.setText(hdop);
        }
        if (isChanged(vdop, mVDOPTextView.getText().toString())) {
            mVDOPTextView.setText(vdop);
        }
    }

    private boolean isChanged(String latest, String old) {
        return !latest.equalsIgnoreCase("--") && !latest.equalsIgnoreCase(old);
    }

    public void setUpUIReferences() {

        mLastUpdateTimeTextView = (TextView) this.activity.findViewById(R.id
                .a_last_updated_time_tv);
        mLatitudeText = (TextView) this.activity.findViewById(R.id.a_lat_tv);
        mLongitudeText = (TextView) this.activity.findViewById(R.id.a_long_tv);
        mAccuracyTextView = (TextView) this.activity.findViewById(R.id.a_accuracy_tv);
        mSpeedTextView = (TextView) this.activity.findViewById(R.id.a_speed_tv);
        mProviderTextView = (TextView) this.activity.findViewById(R.id.a_provider_tv);
        mAltitudeTextView = (TextView) this.activity.findViewById(R.id.a_altitude_tv);
        mBearingTextView = (TextView) this.activity.findViewById(R.id.a_bearing_tv);
        mSatelliteInUseTextView = (TextView) this.activity.findViewById(R.id.a_satellite_in_use_tv);
        mSatelliteInViewTextView = (TextView) this.activity.findViewById(R.id
                .a_satellite_in_view_tv);

        mPDOPTextView = (TextView) this.activity.findViewById(R.id.a_pdop_tv);
        mHDOPTextView = (TextView) this.activity.findViewById(R.id.a_hdop_tv);
        mVDOPTextView = (TextView) this.activity.findViewById(R.id.a_vdop_tv);
        mGpsModeTextView = (TextView) this.activity.findViewById(R.id.gps_mode_tv);

    }

    public void updateUI() {

        mLatitudeText.setText(String.valueOf(androidLocationUtil.getLatitude()));
        mLongitudeText.setText(String.valueOf(androidLocationUtil.getLongitude()));
        mAccuracyTextView.setText(String.valueOf(androidLocationUtil.getAccuracy()));
        mAltitudeTextView.setText(String.valueOf(androidLocationUtil.getAltitude()));
        mSpeedTextView.setText(String.valueOf(androidLocationUtil.getSpeed()));
        mBearingTextView.setText(String.valueOf(androidLocationUtil.getBearing()));
        mProviderTextView.setText(String.valueOf(androidLocationUtil.getProvider()));
        mLastUpdateTimeTextView.setText(androidLocationUtil.getLastUpdateTime());
        mSatelliteInUseTextView.setText(String.valueOf(androidLocationUtil.getSatelliteInUse()));
        mSatelliteInViewTextView.setText(String.valueOf(androidLocationUtil.getSatelliteInView()));
    }

    public void updateGpsStatusUI() {
        if (isChanged(androidLocationUtil.getGpsMode(), mGpsModeTextView.getText().toString()))
            mGpsModeTextView.setText(androidLocationUtil.getGpsMode());
    }

    public void updateSatelliteUI() {
        if (isChanged(String.valueOf(androidLocationUtil.getSatelliteInUse()),
                mSatelliteInUseTextView.getText().toString())) {
            mSatelliteInUseTextView.setText(String.valueOf(androidLocationUtil.getSatelliteInUse()));
        }

        if (isChanged(String.valueOf(androidLocationUtil.getSatelliteInView()),
                mSatelliteInViewTextView.getText().toString())) {
            mSatelliteInViewTextView.setText(String.valueOf(androidLocationUtil.getSatelliteInView()));
        }
    }
}
