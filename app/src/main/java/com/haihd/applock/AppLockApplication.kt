package com.haihd.applock

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.haihd.applock.activity.splash.SplashActivity
import com.haihd.applock.di.appModule
import com.haihd.applock.service.LockService
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.context.startKoin

class AppLockApplication : Application(), LifecycleObserver {
    private var isForeground = false



    override fun onCreate() {
        super.onCreate()
        mApplication = this
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

    companion object{
        private var mApplication: AppLockApplication? = null
        fun getContext(): Context{
            if (mApplication == null){
                return AppLockApplication()
            }
            return mApplication!!
        }
    }
}