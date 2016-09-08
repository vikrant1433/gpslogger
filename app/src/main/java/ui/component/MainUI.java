package ui.component;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.safestreet.logger.R;

/**
 * Created by vikrant on 5/9/16.
 */
public class MainUI {
    private static final String TAG = MainUI.class.getSimpleName();
    protected Activity activity;
    private Spinner travelModeSpinner;
    private EditText weatherConditionET;
    private EditText remarkET;
    private Button startLoggingBtn;
    private Button stopLoggingBtn;
    private Button addRemarkBtn;


    public MainUI(Activity activity) {
        this.activity = activity;
        setupUI();
    }

    private void setupUI() {
        Log.d(TAG, activity.getClass().getSimpleName());
        travelModeSpinner = (Spinner) this.activity.findViewById(R.id.travel_mode_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.activity, R.array
                .travel_mode_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (travelModeSpinner == null) {
            Log.d(TAG, "travelModeSpinner is null");
        } else {
            travelModeSpinner.setAdapter(adapter);
        }

        weatherConditionET = (EditText) this.activity.findViewById(R.id.weather_et);
        remarkET = (EditText) this.activity.findViewById(R.id.remark_et);

        startLoggingBtn = (Button) this.activity.findViewById(R.id.start_logging_btn);
        stopLoggingBtn = (Button) this.activity.findViewById(R.id.stop_logging_btn);
        addRemarkBtn = (Button) this.activity.findViewById(R.id.add_remark_btn);
    }

    public String getTravelMode() {
        return travelModeSpinner.getSelectedItem().toString();
    }

    public String getWeatherCondition() {
        return weatherConditionET.getText().toString();
    }

    public String getRemarkNotes() {
        return remarkET.getText().toString();
    }

}
