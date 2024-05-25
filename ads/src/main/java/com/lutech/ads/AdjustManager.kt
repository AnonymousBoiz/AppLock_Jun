package com.lutech.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel

object AdjustManager {

    fun init(myApplication: Application,appToken: String){
        //adjust
        val appToken = appToken
        val environment = if(BuildConfig.DEBUG){
            AdjustConfig.ENVIRONMENT_SANDBOX
        }else{
            AdjustConfig.ENVIRONMENT_PRODUCTION
        }
        val config = AdjustConfig(myApplication, appToken, environment)
        config.setLogLevel(LogLevel.VERBOSE)
        config.setPreinstallTrackingEnabled(true)
        Adjust.onCreate(config)

        myApplication.registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())
    }

    class AdjustLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {

        }

        override fun onActivityStarted(p0: Activity) {

        }

        override fun onActivityResumed(p0: Activity) {
            Adjust.onResume();
        }

        override fun onActivityPaused(p0: Activity) {
            Adjust.onPause();
        }

        override fun onActivityStopped(p0: Activity) {

        }

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

        }

        override fun onActivityDestroyed(p0: Activity) {

        }
    }
}