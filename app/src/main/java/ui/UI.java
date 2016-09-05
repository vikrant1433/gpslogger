package ui;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.safestreet.logger.R;

/**
 * Created by vikrant on 5/9/16.
 */
public class UI {
    Activity activity;
    public UI(Activity activity) {
       this.activity = activity;
    }

    public void initSpinner() {

        Spinner travelModeSpinner = (Spinner) this.activity.findViewById(R.id.travel_mode_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.activity, R.array
                .travel_mode_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelModeSpinner.setAdapter(adapter);
    }
}
