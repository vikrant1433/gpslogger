package logger;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import util.K;

/**
 * Created by vikrant on 7/9/16.
 */
public class LogWriter {
    private static final String TAG = LogWriter.class.getSimpleName();
    protected String startLogTime;
    protected String stopLogTime;
    protected String header = "";
    protected BufferedWriter bufferedWriter;
    protected File logFile;
    protected File loggerDirectory;
    protected String fileName;
    protected String fileNamePrefix;

    public LogWriter(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void startLogging() {
        setStartLogTime("" + System.currentTimeMillis());
        this.fileName = this.fileNamePrefix + K.FILE_NAME_SEPARATOR + new SimpleDateFormat
                // // TODO: 8/10/16 adding # to separate year and time can be a bug check it
                ("yyyy-MM-dd" + K.FILE_NAME_SEPARATOR + "HH:mm:ss").format(Calendar.getInstance()
                .getTime()) + K.LOG_FILE_NAME_EXTENSION;
        createFile();
        write("start time: " + getStartLogTime() + K.NEWLINE);
        writeHeader();
    }

    protected void writeHeader() {
        write(header + K.NEWLINE);
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
    public void stopLogging() {
        setStopLogTime("" + System.currentTimeMillis());
        write("stop time: " + getStopLogTime() + K.NEWLINE);
        close();
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

        //        String header = "Logging started at " + application.getStartLogTime() + K
        // .NEWLINE + "Logging stopped at " + application.getStopLogTime() + K.NEWLINE
        //                + "Weather condition: " + ui.getWeatherCondition() + K.NEWLINE
        //                + "Travel Mode: " + ui.getTravelMode() + K.NEWLINE
        //                + "Phone manufacturer: " + phoneManufacturer + K.NEWLINE
        //                + "Phone model: " + phoneModel + K.NEWLINE
        //                + "Android Version : " + androidVersion + K.NEWLINE
        //                + "Remark: " + ui.getRemarkNotes() + K.NEWLINE;
        //        try {
        //            bufferedWriter.write(header);
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
    }

}

