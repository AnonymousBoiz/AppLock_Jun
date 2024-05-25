package com.lutech.ads.billing;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PurchasesUpdatedListener;

public class BillingClientSetup {
    private static BillingClient instance;

    private static final String key = "Upgraded";

    public static final String ITEM_BUY_SUB_WEEK = "filerecovery_sub_week";
    public static final String ITEM_BUY_SUB_MONTH = "filerecovery_sub_month";
    public static final String ITEM_BUY_SUB_YEAR = "filerecovery_sub_year";

    private static final String NAME_APP = "NAME_APP";

    public static BillingClient getInstance(Context context, PurchasesUpdatedListener listener) {
        return instance == null ? setupBillingClient(context, listener) : instance;
    }

    private static BillingClient setupBillingClient(Context context, PurchasesUpdatedListener listener) {
        BillingClient billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(listener)
                .build();
        return billingClient;
    }

    public static boolean isUpgraded(Context context) {
//        if (BuildConfig.DEBUG) return true;
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_APP, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong("Time", 0);
        long timeCurrent = System.currentTimeMillis()/1000;
        if(timeCurrent<=time) {
            return true;
        }
        return false;

    }

    public static void updateTimeUpgrade(Context context, long time) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME_APP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("Time", time);
        editor.apply();
    }
}
