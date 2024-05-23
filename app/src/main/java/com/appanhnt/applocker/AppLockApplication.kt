package com.appanhnt.applocker

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.appanhnt.applocker.activity.splash.SplashActivity
import com.appanhnt.applocker.di.appModule
import com.appanhnt.applocker.service.LockService
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.google.android.gms.ads.ez.EzApplication
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.context.startKoin
import java.util.*

class AppLockApplication : EzApplication(), LifecycleObserver {
    private var isForeground = false
    override fun onCreate() {
        super.onCreate()
        PreferencesUtils.init(this)
        Hawk.init(this).build()
        setupKoin()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this@AppLockApplication)
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@AppLockApplication)
            modules(
                appModule
            )
        }
    }

    @KoinApiExtension
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        isForeground = true
        LockService.isCreate = false
        LockService.showDetailNotify = false
        //App in background
    }

    @KoinApiExtension
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        if (isForeground&& LockService.showDetailNotify) {
            launchActivity<SplashActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            isForeground = false
        }
    }
}