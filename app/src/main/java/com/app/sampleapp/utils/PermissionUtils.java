package com.app.sampleapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.app.sampleapp.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class PermissionUtils {

    public static int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;

    /*
     * Check if version is marshmallow and above.
     * Used in deciding to ask runtime permission
     * */

    private static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    private static boolean shouldAskPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            int permissionResult = ActivityCompat.checkSelfPermission(context, permission);
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    public static void checkPermission(Context context, String permission, PermissionAskListener listener) {
        /*
         * If permission is not granted
         * */
        if (shouldAskPermission(context, permission)) {


            /*
             * If permission denied previously
             * */
            if (((Activity) context).shouldShowRequestPermissionRationale(permission)) {
                listener.onPermissionPreviouslyDenied();

            } else {

                if (PermissionUtils.isFirstTimeAskingPermission(context, permission)) {
                    firstTimeAskingPermission(context, permission, false);
                    listener.onPermissionAsk();
                }
                /*
                 * Permission denied or first time requested
                 * */
                else {

                    listener.onPermissionDisabled();
                }

            }
        } else {
            listener.onPermissionGranted();
        }
    }

    public static String[] checkPermissionsHasGranted(Context context, String[] permissions) {
        ArrayList<String> premissionsNeedGrantAccess = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (shouldAskPermission(context, permissions[i])) {
                premissionsNeedGrantAccess.add(permissions[i]);
            }
        }
        return premissionsNeedGrantAccess.toArray(new String[]{});
    }

    /*
     * Callback on various cases on checking permission
     *
     * 1.  Below M, runtime permission not needed. In that case onPermissionGranted() would be called.
     *     If permission is already granted, onPermissionGranted() would be called.
     *
     * 2.  Above M, if the permission is being asked first time onPermissionAsk() would be called.
     *
     * 3.  Above M, if the permission is previously asked but not granted, onPermissionPreviouslyDenied()
     *     would be called.
     *
     * 4.  Above M, if the permission is checkbox_unselected by device policy or the user checked "Never ask again"
     *     check box on previous request permission, onPermissionDisabled() would be called.
     * */
    public interface PermissionAskListener {
        /*
         * Callback to ask permission
         * */
        void onPermissionAsk();

        /*
         * Callback on permission denied
         * */
        void onPermissionPreviouslyDenied();

        /*
         * Callback on permission "Never show again" checked and denied
         * */
        void onPermissionDisabled();

        /*
         * Callback on permission granted
         * */
        void onPermissionGranted();

    }
    private static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime){
        SharedPreferences sharedPreference = context.getSharedPreferences("permission", MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }
    private static boolean isFirstTimeAskingPermission(Context context, String permission){
        return context.getSharedPreferences("permission", MODE_PRIVATE).getBoolean(permission, true);
    }

    public static void showPermissionDialog(String msg, final boolean disabled, final Activity context){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!disabled) {
//                  checkPermissions();
                    ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE_REQUEST_CODE);

                }
                else
                {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                context.finish();
            }
        });
        builder.create();
        builder.show();

    }

}
