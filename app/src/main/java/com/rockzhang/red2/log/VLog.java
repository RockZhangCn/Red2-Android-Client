package com.rockzhang.red2.log;

import android.util.Log;

public class VLog {
    private static String TAG = "Red2-Android";

    public static void debug(String message) {
        Log.v(TAG, getLogWithLine(message));
    }

    public static void info(String message) {
        Log.i(TAG, getLogWithLine(message));
    }

    public static void warning(String message) {
        Log.w(TAG, getLogWithLine(message));
    }

    public static void error(String message) {
        Log.e(TAG, getLogWithLine(message));
    }

    private static String getLogWithLine(String message) {
        int index = 4;
        StringBuffer stringBuffer = new StringBuffer();

        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            String className = stackTrace[index].getFileName();
            String methodName = stackTrace[index].getMethodName();
            int lineNumber = stackTrace[index].getLineNumber();
            methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
            stringBuffer.append("[(").append(className).append(":").append(lineNumber).append(")#").append(methodName).append("] ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalMsg = stringBuffer.toString() + ": " + message;
        return finalMsg;
    }
}
