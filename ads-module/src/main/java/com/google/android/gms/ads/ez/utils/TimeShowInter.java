package com.google.android.gms.ads.ez.utils;

import android.util.Log;

public class TimeShowInter {
    private static Long timeShowBetween = 0L;
    private static Long mTargetTime = 40L;
    public static void setTargetTime(Long targetTime ){
        mTargetTime = targetTime;
    }

    public static void updateTime() {
        timeShowBetween = System.currentTimeMillis();
    }

    public static boolean showInter() {
        Long currentTime = System.currentTimeMillis();
        Log.e("vvvvvvvvvv", "showInter: " + (currentTime - timeShowBetween ) + "   " + mTargetTime);
        return currentTime - timeShowBetween > mTargetTime * 1000L;
    }
}
