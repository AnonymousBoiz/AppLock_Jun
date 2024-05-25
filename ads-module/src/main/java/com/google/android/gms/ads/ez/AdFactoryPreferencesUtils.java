package com.google.android.gms.ads.ez;

import android.content.Context;
import android.content.SharedPreferences;

public class AdFactoryPreferencesUtils {
    public static final String FILE_NAME = "share_preferences";
    public static SharedPreferences getPreferrences(Context context) {
        return context.getSharedPreferences("Share", 0);
    }

    /**************************
     * Integer
     **************************/
    public static void setTagInt(Context context, String tag, int value) {
        SharedPreferences.Editor localEditor = context.getSharedPreferences(FILE_NAME, 0).edit();
        localEditor.putInt(tag, value);
        localEditor.commit();
    }

    public static int getTagInt(Context context, String tag) {
        return context.getSharedPreferences(FILE_NAME, 0).getInt(tag, 0);
    }

    public static int getTagInt(Context context, String tag, int defaultParam) {
        return context.getSharedPreferences(FILE_NAME, 0).getInt(tag, defaultParam);
    }

    /**************************
     * Long
     **************************/
    public static void setTagLong(Context context, String tag, long value) {
        SharedPreferences.Editor localEditor = context.getSharedPreferences(FILE_NAME, 0).edit();
        localEditor.putLong(tag, value);
        localEditor.commit();
    }

    public static long getTagLong(Context context, String tag) {
        return context.getSharedPreferences(FILE_NAME, 0).getLong(tag, 0);
    }

    public static long getTagLong(Context context, String tag, long defaultParam) {
        return context.getSharedPreferences(FILE_NAME, 0).getLong(tag, defaultParam);
    }
}
