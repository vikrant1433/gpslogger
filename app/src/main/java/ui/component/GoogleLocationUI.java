package ui.component;

import android.app.Activity;

import util.GoogleLocationUtil;

/**
 * Created by vikrant on 7/9/16.
 */
public class GoogleLocationUI extends MainUI{

    Activity activity;
    private GoogleLocationUtil googleLocationUtil;

    public GoogleLocationUI(Activity activity, GoogleLocationUtil googleLocationUtil) {
        super(activity);
        this.activity = activity;
        this.googleLocationUtil = googleLocationUtil;
    }
}
