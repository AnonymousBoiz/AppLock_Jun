package com.appanhnt.applocker.activity.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import com.appanhnt.applocker.activity.locktype.LockTypeActivity
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
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.appanhnt.applocker.activity.language.LanguageActivity
import com.appanhnt.applocker.activity.lock.LockActivity
import com.appanhnt.applocker.activity.welcome.WelcomeActivity
import com.google.android.gms.ads.ez.AdFactoryListener
import com.google.android.gms.ads.ez.LogUtils
import com.google.android.gms.ads.ez.admob.AdmobOpenAdUtils
import com.google.android.gms.ads.ez.utils.TimeShowInter
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel by inject<AppLockViewModel>()
    private val viewModelTheme by inject<ThemeViewModel>()
    private val viewModelVideo by inject<HideVideoModel>()

    @SuppressLint("UseCompatLoadingForDrawables")
    @KoinApiExtension
    override fun initView() {
        // Screen full
        TimeShowInter.setTargetTime(40L)
        setAppActivityFullScreenOver(this)
        //
        openMain()
    }

    override fun initData() {
    }

    @KoinApiExtension
    private fun openMain() {
        if (!BackgroundManager.checkCreatePassword(this)) {
            loadOpenAds(true)
        } else {
            // show lock
            // request permission
            if (!BackgroundManager.isServiceRunning(LockService::class.java, this)) {
                Log.e("nnnnnnnnnnnn", "LockService: ", )

                BackgroundManager.startService(LockService::class.java, this, this)

            }
            loadOpenAds()
        }
    }

    @KoinApiExtension
    private fun loadOpenAds(isStartMain: Boolean = false) {
        AdmobOpenAdUtils.getInstance(this).setAdListener(object : AdFactoryListener() {
            override fun onError() {
                LogUtils.logString(SplashActivity::class.java, "onError")
                AdmobOpenAdUtils.getInstance(this@SplashActivity).showAdIfAvailable(false)
                startAct(isStartMain)
            }

            override fun onLoaded() {
                LogUtils.logString(SplashActivity::class.java, "onLoaded")
                // show ads ngay khi loaded
                AdmobOpenAdUtils.getInstance(this@SplashActivity).showAdIfAvailable(false)
            }

            override fun onDisplay() {
                super.onDisplay()
                LogUtils.logString(SplashActivity::class.java, "onDisplay")
            }

            override fun onDisplayFaild() {
                super.onDisplayFaild()
                LogUtils.logString(SplashActivity::class.java, "onDisplayFaild")
                startAct(isStartMain)
            }

            override fun onClosed() {
                super.onClosed()
                // tam thoi bo viec load lai ads thi dismis
                LogUtils.logString(SplashActivity::class.java, "onClosed")
                startAct(isStartMain)
            }
        }).loadAd()
    }

    @KoinApiExtension
    private fun startAct(isStartMain: Boolean) {
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
        Handler(Looper.getMainLooper()).postDelayed({
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
        }, 500)
    }

    override fun initListener() {
    }

    override fun viewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    @KoinApiExtension
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LockService.isCreate = true
    }


}