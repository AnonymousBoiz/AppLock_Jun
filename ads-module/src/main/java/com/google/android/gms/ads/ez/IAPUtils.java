package com.google.android.gms.ads.ez;

import android.app.Activity;
import android.app.Application;


import java.util.ArrayList;
import java.util.List;

public class IAPUtils {
    private Application application;
    private static IAPUtils INSTANCE;
    public static final String KEY_PREMIUM_ONE_MONTH = "one_month";
    public static final String KEY_PREMIUM_SIX_MONTHS = "six_months";
    public static final String KEY_PREMIUM_ONE_YEAR = "one_year";
    public static final String KEY_PURCHASE_SUCCESS = "purchase_success";


    public boolean isPremium() {
        if (isSubscriptions(KEY_PREMIUM_ONE_MONTH) || isSubscriptions(KEY_PREMIUM_SIX_MONTHS) || isSubscriptions(KEY_PREMIUM_ONE_YEAR)) {
            return true;
        }
        return false;
    }


    public static IAPUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IAPUtils();
        }
        return INSTANCE;
    }


    public void init(Application application) {


    }


    public void getAllSubcriptions() {
        LogUtils.logString(IAPUtils.class, "getAllSubcriptions ");
        List<String> skuListToQuery = new ArrayList<>();
        skuListToQuery.add(KEY_PREMIUM_ONE_MONTH);
        skuListToQuery.add(KEY_PREMIUM_SIX_MONTHS);
        skuListToQuery.add(KEY_PREMIUM_ONE_YEAR);


    }


    public boolean isSubscriptions(String id) {
        return false;
    }

    public void callPurchase(Activity activity, String id) {

    }

    public void callSubcriptions(Activity activity, String id) {


    }


    private void logResponse(int responseCode) {
        switch (responseCode) {
            case SERVICE_TIMEOUT:
                LogUtils.logString(IAPUtils.class, "SERVICE_TIMEOUT");
                break;
            case FEATURE_NOT_SUPPORTED:
                LogUtils.logString(IAPUtils.class, "FEATURE_NOT_SUPPORTED");
                break;
            case SERVICE_DISCONNECTED:
                LogUtils.logString(IAPUtils.class, "SERVICE_DISCONNECTED");
                break;
            case OK:
                LogUtils.logString(IAPUtils.class, "OK");
                break;
            case USER_CANCELED:
                LogUtils.logString(IAPUtils.class, "USER_CANCELED");
                break;
            case SERVICE_UNAVAILABLE:
                LogUtils.logString(IAPUtils.class, "SERVICE_UNAVAILABLE");
                break;
            case BILLING_UNAVAILABLE:
                LogUtils.logString(IAPUtils.class, "BILLING_UNAVAILABLE");
                break;
            case ITEM_UNAVAILABLE:
                LogUtils.logString(IAPUtils.class, "ITEM_UNAVAILABLE");
                break;
            case DEVELOPER_ERROR:
                LogUtils.logString(IAPUtils.class, "DEVELOPER_ERROR");
                break;
            case ERROR:
                LogUtils.logString(IAPUtils.class, "ERROR");
                break;
            case ITEM_ALREADY_OWNED:
                LogUtils.logString(IAPUtils.class, "ITEM_ALREADY_OWNED");
                break;
            case ITEM_NOT_OWNED:
                LogUtils.logString(IAPUtils.class, "ITEM_NOT_OWNED");
                break;
        }
    }

    final int SERVICE_TIMEOUT = -3;
    final int FEATURE_NOT_SUPPORTED = -2;
    final int SERVICE_DISCONNECTED = -1;
    final int OK = 0;
    final int USER_CANCELED = 1;
    final int SERVICE_UNAVAILABLE = 2;
    final int BILLING_UNAVAILABLE = 3;
    final int ITEM_UNAVAILABLE = 4;
    final int DEVELOPER_ERROR = 5;
    final int ERROR = 6;
    final int ITEM_ALREADY_OWNED = 7;
    final int ITEM_NOT_OWNED = 8;
}