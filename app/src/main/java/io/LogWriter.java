package io;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import app.LoggerApplication;
import ui.component.MainUI;
import util.K;

/**
 * Created by vikrant on 7/9/16.
 */
public class LogWriter {
    private static final String TAG = LogWriter.class.getSimpleName();
    private final LoggerApplication application;
    MainUI ui = null;
    BufferedWriter bufferedWriter;
    private File logFile;
    private File loggerDirectory;
    private Activity activity;
    private String fileName;
    private String fileNamePrefix;


    public LogWriter(Activity activity, MainUI ui, String fileNamePrefix) {
        this.ui = ui;
        this.activity = activity;
        application = (LoggerApplication) activity.getApplication();
        this.fileName = fileNamePrefix + "-" + new SimpleDateFormat
                ("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()) + K
                .LOG_FILE_NAME_EXTENSION;
        createFile();
    }

    private void createFile() {
        Log.d(TAG, "In createFile: creating files/ directory in download folder");
        loggerDirectory = getDownloadStorageDir(K.LOG_DIRNAME);
        logFile = new File(loggerDirectory, fileName);
        try {
            logFile.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (logFile != null) {
                logFile.createNewFile();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getDownloadStorageDir(String filename) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), filename);
        if (!file.mkdirs()) {
            Log.d(TAG, "Directory not created, directory may already exists");
        } else {
            Log.d(TAG, "Creating logger directory");
        }
        return file;
    }

    public void write(String s) {
        try {
            bufferedWriter.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLogTemplate() {
        String phoneManufacturer = Build.MANUFACTURER;
        String phoneModel = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;

        String header = "Logging started at " + application.getStartLogTime() + K.NEWLINE
                + "Logging stopped at " + application.getStopLogTime() + K.NEWLINE
                + "Weather condition: " + ui.getWeatherCondition() + K.NEWLINE
                + "Travel Mode: " + ui.getTravelMode() + K.NEWLINE
                + "Phone manufacturer: " + phoneManufacturer + K.NEWLINE
                + "Phone model: " + phoneModel + K.NEWLINE
                + "Android Version : " + androidVersion + K.NEWLINE
                + "Remark: " + ui.getRemarkNotes() + K.NEWLINE;

        try {
            bufferedWriter.write(header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

