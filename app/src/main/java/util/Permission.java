package util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


/**
 * Created by vikrant on 6/9/16.
 */
public class Permission extends ActivityCompat {
    public static boolean hasLocationPermission(Activity activity) {
        return checkSelfPermission(activity, Manifest.permission
                .ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isStoragePermissionGranted(Activity activity) {
        final String TAG = activity.getClass().getSimpleName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED) {
                Log.d(TAG, "Permission is granted");
                return true;
            } else {
                Log.d(TAG, "Permission is not granted");
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.d(TAG, "Permission is granted sdk version < 23");
            return true;
        }

    }

    public static boolean hasReadWritePermission(Activity activity) {
        return isStoragePermissionGranted(activity) && isExternalStorageWritable();
    }

}
