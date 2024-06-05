package com.haihd.applock.activity.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.BuildConfig
import com.haihd.applock.R
import com.haihd.applock.activity.language.LanguageActivity
import com.haihd.applock.activity.lock.LockActivity
import com.haihd.applock.activity.locktype.LockTypeActivity
import com.haihd.applock.activity.welcome.WelcomeActivity
import com.haihd.applock.databinding.ActivitySplashBinding
import com.haihd.applock.item.ItemVideoHide
import com.haihd.applock.key.KeyApp
import com.haihd.applock.key.KeyLock
import com.haihd.applock.key.Vault
import com.haihd.applock.service.BackgroundManager
import com.haihd.applock.service.LockService
import com.haihd.applock.utils.NotificationLockUtils
import com.haihd.applock.viewmodel.AppLockViewModel
import com.haihd.applock.viewmodel.HideVideoModel
import com.haihd.applock.viewmodel.ThemeViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.haihd.applock.utils.RemoteData
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
                    loadDataRemoteConfig()
                    showUpdateVersionDialog(BuildConfig.VERSION_CODE) { showAds() }
                }
        }
    }

    private fun loadDataRemoteConfig(){
        RemoteData.IsStartForegroundService = mFirebaseRemoteConfig.getBoolean("is_start_foreground_service")
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
                BackgroundManager.startService(this, this)
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
            BackgroundManager.startService(this, this)
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