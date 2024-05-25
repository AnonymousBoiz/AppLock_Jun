package com.google.android.gms.ads.ez.adparam;

public class AdUnit {
    public static final boolean TEST = true;

    public static String getAdmobInterId() {
        String id = AdUnitFactory.getInstance(TEST).getAdmobInterId();
        return "ca-app-pub-6567371235509783/8866971116";
    }

    public static String getAdmobNativeId() {
        String id = AdUnitFactory.getInstance(TEST).getAdmobNativeId();
        return id == null ? "" : id;
    }

    public static String getAdmobBannerId() {
        String id = AdUnitFactory.getInstance(TEST).getAdmobBannerId();
        return id == null ? "" : id;
    }

    public static String getAdmobOpenId() {
        String id = AdUnitFactory.getInstance(TEST).getAdmobOpenId();
        return "ca-app-pub-6567371235509783/5119297794";
    }

    public static int getCountShowAds() {
        return AdUnitFactory.getInstance(TEST).getCountShowAds();
    }

    public static int getLimitShowAds() {
        return AdUnitFactory.getInstance(TEST).getLimitShowAds();
    }

    public static long getTimeLastShowAds() {
        return AdUnitFactory.getInstance(TEST).getTimeLastShowAds();
    }


    public static String getMasterAdsNetwork() {
        String id = AdUnitFactory.getInstance(TEST).getMasterAdsNetwork();
        return id == null ? "" : id;
    }
}
