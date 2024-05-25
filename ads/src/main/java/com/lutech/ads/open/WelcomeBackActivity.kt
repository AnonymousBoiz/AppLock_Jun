package com.lutech.ads.open;

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.lutech.ads.AdsManager
import com.lutech.ads.open.AppOpenManager
import com.lutech.ads.R

public class WelcomeBackActivity : AppCompatActivity(), com.lutech.ads.open.AppOpenManager.OpenAdsListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_back)
        if (AdsManager.isAppOpenManagerInitialized()){
            AdsManager.appOpenManager.setOpenAdsListener(this)
            Handler(Looper.getMainLooper()).postDelayed({
                AdsManager.appOpenManager.showAdIfAvailable()
            },1000)
        }else{
            finish()
        }
    }

    override fun dismissAds() {
        finish()
    }
}