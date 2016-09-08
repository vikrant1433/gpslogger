package app;

import android.app.Application;

/**
 * Created by vikrant on 7/9/16.
 */
public class LoggerApplication extends Application{
    private boolean isLogging = false;
    protected String startLogTime;

    public String getStopLogTime() {
        return stopLogTime;
    }

    public void setStopLogTime(String stopLogTime) {
        this.stopLogTime = stopLogTime;
    }

    public String getStartLogTime() {
        return startLogTime;
    }

    public void setStartLogTime(String startLogTime) {
        this.startLogTime = startLogTime;
    }

    protected String stopLogTime;


    public boolean isLogging() {
        return isLogging;
    }

    public void setLogging(boolean logging) {
        isLogging = logging;
    }
}
