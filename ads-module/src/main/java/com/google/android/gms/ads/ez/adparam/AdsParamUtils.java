package com.google.android.gms.ads.ez.adparam;


import com.google.android.gms.ads.ez.remote.AppConfigs;
import com.google.android.gms.ads.ez.remote.RemoteKey;

public class AdsParamUtils extends AdUnitFactory {

    @Override
    public String getAdmobInterId() {
        return "ca-app-pub-6567371235509783/8866971116";
    }

    @Override
    public String getAdmobNativeId() {
        return "";
    }

    @Override
    public String getAdmobOpenId() {
        return "ca-app-pub-6567371235509783/5119297794";
    }

    @Override
    public String getAdmobBannerId() {
        return AppConfigs.getString(RemoteKey.ADMOB_BANNER_ID);
    }

    @Override
    public String getMasterAdsNetwork() {
        return AppConfigs.getString(RemoteKey.MASTER_ADS_NETWORK);
    }

    @Override
    public int getCountShowAds() {
        return AppConfigs.getInt(RemoteKey.COUNT_SHOW_ADS);
    }

    @Override
    public int getLimitShowAds() {
        return AppConfigs.getInt(RemoteKey.MAX_SHOW_DAY);
    }

    @Override
    public long getTimeLastShowAds() {
        return AppConfigs.getInt(RemoteKey.TIME_SHOW_ADS);
    }


}
