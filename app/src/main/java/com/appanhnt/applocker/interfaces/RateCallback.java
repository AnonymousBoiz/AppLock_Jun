package com.appanhnt.applocker.interfaces;

public interface RateCallback {

    void onMaybeLater();

    void onSubmit(String review);

    void onRate();

    void starRate(float v);

}
