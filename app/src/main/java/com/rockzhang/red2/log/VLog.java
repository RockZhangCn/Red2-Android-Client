package com.rockzhang.red2.log;

import android.util.Log;

public class VLog {
    private static String TAG = "Red2-Android";

    public static void debug(String message) {
        Log.v(TAG, message);
    }

    public static void info(String message) {
        Log.i(TAG, message);
    }

    public static void warning(String message) {
        Log.w(TAG, message);
    }

    public static void error(String message) {
        Log.e(TAG, message);
    }
}
