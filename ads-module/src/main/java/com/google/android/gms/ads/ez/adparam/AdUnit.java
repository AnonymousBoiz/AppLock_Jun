package com.google.android.gms.ads.ez.adparam;

public class AdUnit {
    public static final boolean TEST = false;

    public static boolean isTEST() {
        return TEST;
    }

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

    public static String getAdmobRewardedId() {
        String id = AdUnitFactory.getInstance(TEST).getAdmobRewardedId();
        return id == null ? "" : id;
    }

    public static String getAdxInterId() {
        String id = AdUnitFactory.getInstance(TEST).getAdxInterId();
        return id == null ? "" : id;
    }

    public static String getAdxBannerId() {
        String id = AdUnitFactory.getInstance(TEST).getAdxBannerId();
        return id == null ? "" : id;
    }

    public static String getAdxNativeId() {
        String id = AdUnitFactory.getInstance(TEST).getAdxNativeId();
        return id == null ? "" : id;
    }

    public static String getAdxOpenId() {
        String id = AdUnitFactory.getInstance(TEST).getAdxOpenId();
        return id == null ? "" : id;
    }

    public static String getFacebookInterId() {
        String id = AdUnitFactory.getInstance(TEST).getFacebookInterId();
        return id == null ? "" : id;
    }

    public static String getFacebookBannerId() {
        String id = AdUnitFactory.getInstance(TEST).getFacebookBannerId();
        return id == null ? "" : id;
    }

    public static String getFacebookNativeId() {
        String id = AdUnitFactory.getInstance(TEST).getFacebookNativeId();
        return id == null ? "" : id;
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


    public static String getApplovinBannerId() {
        String id = AdUnitFactory.getInstance(TEST).getApplovinBannerId();
        return id == null ? "" : id;
    }

    public static String getApplovinInterId() {
        String id = AdUnitFactory.getInstance(TEST).getApplovinInterId();
        return id == null ? "" : id;
    }

    public static String getApplovinRewardId() {
        String id = AdUnitFactory.getInstance(TEST).getApplovinRewardId();
        return id == null ? "" : id;
    }
}
