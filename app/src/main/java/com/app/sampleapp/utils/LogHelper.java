package com.app.sampleapp.utils;

import android.util.Log;

import com.app.sampleapp.BuildConfig;

public class LogHelper {

    public static void log(String TAG, String message){

        if(BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }
}
