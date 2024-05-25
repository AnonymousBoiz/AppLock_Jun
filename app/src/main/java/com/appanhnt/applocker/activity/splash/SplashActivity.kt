package com.appanhnt.applocker.activity.splash

import android.R.attr.gravity
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.appanhnt.applocker.BuildConfig
import com.appanhnt.applocker.R
import com.appanhnt.applocker.activity.language.LanguageActivity
import com.appanhnt.applocker.activity.lock.LockActivity
import com.appanhnt.applocker.activity.locktype.LockTypeActivity
import com.appanhnt.applocker.activity.welcome.WelcomeActivity
import com.appanhnt.applocker.databinding.ActivitySplashBinding
import com.appanhnt.applocker.item.ItemVideoHide
import com.appanhnt.applocker.key.KeyApp
import com.appanhnt.applocker.key.KeyLock
import com.appanhnt.applocker.key.Vault
import com.appanhnt.applocker.service.BackgroundManager
import com.appanhnt.applocker.service.LockService
import com.appanhnt.applocker.utils.NotificationLockUtils
import com.appanhnt.applocker.viewmodel.AppLockViewModel
import com.appanhnt.applocker.viewmodel.HideVideoModel
import com.appanhnt.applocker.viewmodel.ThemeViewModel
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.lutech.ads.AdsManager
import com.lutech.ads.extensions.showUpdateVersionDialog
import com.lutech.ads.interstitial.InterstitialAdsManager
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension


class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    private val viewModel by inject<AppLockViewModel>()
    private val viewModelTheme by inject<ThemeViewModel>()
    private val viewModelVideo by inject<HideVideoModel>()

    override fun viewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    @KoinApiExtension
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LockService.isCreate = true

//        AsyncTask.execute {
//            try {
//                val adInfo =
//                    AdvertisingIdClient.getAdvertisingIdInfo(
//                        this@SplashActivity
//                    )
//                val myId = if (adInfo != null) adInfo.id else null
//
//                Log.d("666669999999", myId!!)
//            } catch (e: Exception) {
//            }
//        }
    }

    @KoinApiExtension
    override fun initView() {
        setAppActivityFullScreenOver(this)

        AdsManager.requestConsentForm(this, application) {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build()
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this@SplashActivity) {
                    AdsManager.loadAdsRemoteConfig()
//                    loadDataRemoteConfig()
                    showUpdateVersionDialog(BuildConfig.VERSION_CODE) { showAds() }
                }
        }
    }

    private fun showAds() {
        Handler(Looper.getMainLooper()).postDelayed({
            InterstitialAdsManager.showSplashAds(this@SplashActivity, this@SplashActivity)
        }, 1000)
    }

    @OptIn(KoinApiExtension::class)
    private fun gotoNextScreen(){
        if (!BackgroundManager.checkCreatePassword(this)) {
            startScreen(true)
        } else {
            // show lock
            // request permission
            if (!BackgroundManager.isServiceRunning(LockService::class.java, this)) {
                Log.e("nnnnnnnnnnnn", "LockService: ")

                BackgroundManager.startService(LockService::class.java, this, this)

            }
            startScreen()
        }
    }

    override fun initData() {
    }

    @OptIn(KoinApiExtension::class)
    private fun startScreen(isStartMain: Boolean = false) {
//        val listImage = Hawk.get(Vault.KEY_FILE_NAME_IMAGE, mutableListOf<ItemImageHide>())
        val listVideo = Hawk.get(Vault.KEY_FILE_NAME_VIDEO, mutableListOf<ItemVideoHide>())
        //
        //load app
        viewModel.loadApp(this)
        viewModelTheme.getBackground(this)
        viewModelTheme.getListTheme(this)
//        if (!listImage.isNullOrEmpty()) {
//            viewModelImage.decryptListImage(listImage)
//        }
        if (!listVideo.isNullOrEmpty()) {
            viewModelVideo.decryptListVideo(listVideo, this)
        }
//        launchActivity<ConcernedApp> { }
        if (!BackgroundManager.isServiceRunning(LockService::class.java, this)) {
            BackgroundManager.startService(LockService::class.java, this, this)
        }
        if (isStartMain) {
            if (PreferencesUtils.getBoolean(KeyApp.IS_CHOOSE_LANGUAGE, false)) {
                if (PreferencesUtils.getBoolean(KeyApp.CREATED_PASSWORD, false)){
                    launchActivity<WelcomeActivity> { }
                }else{
                    launchActivity<LockTypeActivity> { }
                }
            } else {
                launchActivity<LanguageActivity> { }
            }
        } else {
            if (PreferencesUtils.getBoolean(KeyApp.CREATED_PASSWORD, false)) {
                launchActivity<LockActivity> {
                    putExtra(KeyApp.LOCK_MY_APP, true)
                    putExtra(NotificationLockUtils.DETAIL, LockService.showDetailNotify)
                    putExtra(
                        KeyLock.PKG_APP, "com.appanhnt.applocker"
                    )
                }
            }else{
                launchActivity<LockTypeActivity> { }
            }
        }
        finish()
    }

    override fun initListener() {
    }

    override fun onAdDismissed() {
        gotoNextScreen()
    }

    override fun onWaitAds() {

    }
}