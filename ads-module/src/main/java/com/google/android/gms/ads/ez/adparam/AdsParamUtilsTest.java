package com.google.android.gms.ads.ez.adparam;

import com.google.android.gms.ads.ez.remote.AppConfigs;
import com.google.android.gms.ads.ez.remote.RemoteKey;


public class AdsParamUtilsTest extends AdUnitFactory {


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
        return "ca-app-pub-3940256099942544/6300978111";
    }

    @Override
    public String getMasterAdsNetwork() {
        return AppConfigs.getString(RemoteKey.MASTER_ADS_NETWORK);
    }

    @Override
    public int getCountShowAds() {
        return 1;
    }

    @Override
    public int getLimitShowAds() {
        return 50;
    }

    @Override
    public long getTimeLastShowAds() {
        return 10;
    }


}
