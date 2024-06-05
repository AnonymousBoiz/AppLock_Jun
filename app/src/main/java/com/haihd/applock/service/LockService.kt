package com.haihd.applock.service

import android.Manifest
import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.androidhiddencamera.CameraConfig
import com.androidhiddencamera.HiddenCameraService
import com.androidhiddencamera.HiddenCameraUtils
import com.androidhiddencamera.config.CameraFacing
import com.androidhiddencamera.config.CameraFocus
import com.androidhiddencamera.config.CameraImageFormat
import com.androidhiddencamera.config.CameraResolution
import com.haihd.applock.R
import com.haihd.applock.activity.*
import com.haihd.applock.broadcast.NewAppBroadCast
import com.haihd.applock.interfaces.OnHomePressedListener
import com.haihd.applock.key.KeyLock
import com.haihd.applock.utils.NotificationUtil
import com.haihd.applock.viewmodel.AppLockViewModel
import com.haihd.applock.viewmodel.TakePhotoViewModel
import com.haihd.applock.windowmanager.MyWindowLockScreen
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.activity.lock.LockActivity
import com.haihd.applock.item.ItemAppLock
import com.haihd.applock.key.KeyApp
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension


@KoinApiExtension
class LockService : HiddenCameraService() {
    private var isAccessGranted = false
    private var isOverLay = false
    private var activityManager: ActivityManager? = null
    private var sUsageStatsManager: UsageStatsManager? = null
    private var window: MyWindowLockScreen? = null
    private var pageNameShow = ""
    private var isShowLock = false
    private val viewModel by inject<TakePhotoViewModel>()
    private val viewModelApp by inject<AppLockViewModel>()
    private val newAppBroadcastReceiver = NewAppBroadCast()
    private var isUsingCamera = false
    private var isHoming = false
    var isShowLockActivity = false
    private val PKG_SETTING = "com.android.settings"
    private val PKG_APP = "com.haihd.applock"
    private val PKG_INSTALLER = "com.google.android.packageinstaller"

    private var mHomeWatcher: HomeWatcher? = null


    private val onListenerScreenState: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                if (intent.action.equals(Intent.ACTION_SCREEN_OFF) && !isLocking && PreferencesUtils.getBoolean(KeyLock.AFTER_SCREEN_OF, false)) {
                    setUpCamera()
                    isShowLock = true
                    pageNameShow = packageName
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(this@LockService)) {
                            if (PreferencesUtils.getBoolean("APP_LOCK", true)) {
                                window = MyWindowLockScreen(
                                    applicationContext,
                                    packageName
                                )
                            }
                        } else {
                            Toast.makeText(
                                this@LockService,
                                "Pleas allow app permission to lock your app",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        window =
                            MyWindowLockScreen(applicationContext, packageName)
                    }
                    window?.takePhoto = {
                        isUsingCamera = true
                        takePicture()//
                    }
                    window?.stopCamera = {
                        isLocking = false
                        if (!isUsingCamera) {
                            try {
                                stopCamera()
                            } catch (ex: Exception) {
                            }
                        }
                        isUsingCamera = false
                    }

                    Log.d("bbbbbbbbbb", Intent.ACTION_SCREEN_OFF)
                } else if (intent.action.equals(Intent.ACTION_SCREEN_ON)) {
                    Log.d("bbbbbbbbbb", Intent.ACTION_SCREEN_ON);
                }
            }
        }

    }

    public fun registerScreenStateBroadCast() {
        val intentFilter = IntentFilter(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        this.registerReceiver(onListenerScreenState, intentFilter)
    }

    companion object {
        var listMyApp = mutableListOf<ItemAppLock>()
        var showDetailNotify = false
        var isCreate = false
        var isRequestPermission = false
        var isLocking = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // register
        register()
        // state permission
        isAccessGranted = BackgroundManager.isAccessGranted(this@LockService)
        //OverLay
        isOverLay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
        // run check open app locked
        runForever()
        // call back click button home
        mHomeWatcher = HomeWatcher(this)
        mHomeWatcher?.setOnHomePressedListener(object : OnHomePressedListener {
            override fun onHomePressed() {
//                isShowLock = false
                Log.d("Huy", "onHomePressed: ")
                pageNameShow = ""
                isShowLockActivity = false
                isHoming = true
                isLocking = false
                Handler(Looper.getMainLooper()).postDelayed({
                    isHoming = false
                }, 1000)
                window?.clearView()
                window = null
            }

            override fun onHomeLongPressed() {
//                isShowLock = false
                window?.clearView()
                window = null
            }
        })
        mHomeWatcher?.startWatch()
        //

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("nnnnnnnnnnnn", "onCreate: ")

        activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        sUsageStatsManager = this.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.createNotification(
                this,
                getString(R.string.app_name),
                getString(R.string.is_protecting)
            )
        }

        registerScreenStateBroadCast()
    }


    private fun runForever() {

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                // check app lock
                if (!isHoming) {
                    activityManager?.let {
                        val packageName: String = getLauncherTopApp()
                        // check permission true
                        checkForeverPermission()
                        // list app locked
                        val listAppLock = Hawk.get(KeyApp.KEY_APP_LOCK, mutableListOf<String>())
                        listAppLock.add(0, "nothing...")

                        // check pkg
                        if (packageName != PKG_SETTING && packageName != PKG_APP && packageName != PKG_INSTALLER) {
                            isShowLockActivity = false
                        }
                        // fig lock 2 lan
                        Log.d("33333312222","pageNameShow:  $pageNameShow  isLocking:  $isLocking")

                        if (pageNameShow != packageName && !isLocking && packageName.isNotEmpty()) {
                            // check open setting system
                            var isCheckInMyApp = false
                            for (item in listMyApp) {
                                if (item.packageName == packageName) {
                                    isCheckInMyApp = true
                                    isShowLock = false
                                    break
                                }
                            }
                            if (!isCheckInMyApp) {
                                Log.d("33333312222","isCheckInMyApp")
                                isShowLock = true
                            }
                        }
                        for (itemLocked in listAppLock) {
                            Log.e("ccccc", "run: $itemLocked")

                            // check app locked  , start again app ,  allow permission , requesting permission

                            Log.d("33333312222","packageName:  $packageName  itemLocked:  $itemLocked  $isRequestPermission  ${BackgroundManager.checkCreatePassword(this@LockService)}")

                            if ((packageName == itemLocked || (packageName == PKG_APP && !isCreate))
                                && BackgroundManager.checkCreatePassword(this@LockService) && !isRequestPermission
                            ) {

                                if (window == null) {
                                    Log.d("33333312222","isShowLockActivity:  $isShowLockActivity  isShowLock:  $isShowLock")

                                    if ((packageName == PKG_SETTING || packageName == PKG_INSTALLER)
                                        && !isShowLockActivity && !isShowLock
                                    ) {

                                        isShowLockActivity = true
                                        isLocking = true
                                        isShowLock = true
                                        pageNameShow = packageName
                                        launchActivity<LockActivity> {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            putExtra(
                                                KeyLock.PKG_APP, packageName
                                            )
                                        }
                                    } else if (packageName != PKG_SETTING && !isShowLock && packageName != PKG_INSTALLER) {
                                        //setUpCamera
                                        setUpCamera()
                                        isLocking = true
                                        isShowLock = true
                                        pageNameShow = packageName
                                        Log.e(
                                            "hhhhhhhhh",
                                            "checkViewLock: 4${
                                                PreferencesUtils.getString(
                                                    KeyLock.LOCK,
                                                    "sss"
                                                )
                                            }"
                                        )

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if (Settings.canDrawOverlays(this@LockService)) {
                                                Log.e(
                                                    "hhhhhhhhh",
                                                    "checkViewLock: 5${
                                                        PreferencesUtils.getString(
                                                            KeyLock.LOCK,
                                                            "sss"
                                                        )
                                                    }"
                                                )
                                                if (PreferencesUtils.getBoolean("APP_LOCK", true)) {
                                                    window = MyWindowLockScreen(
                                                        applicationContext,
                                                        packageName
                                                    )
                                                }
                                            } else {
                                                Toast.makeText(
                                                    this@LockService,
                                                    "Pleas allow app permission to lock your app",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Log.e(
                                                "hhhhhhhhh",
                                                "checkViewLock: 6${
                                                    PreferencesUtils.getString(
                                                        KeyLock.LOCK,
                                                        "sss"
                                                    )
                                                }"
                                            )

                                            window =
                                                MyWindowLockScreen(applicationContext, packageName)
                                        }
                                        window?.takePhoto = {
                                            isUsingCamera = true
                                            takePicture()//
                                        }
                                        window?.stopCamera = {
                                            isLocking = false
                                            if (!isUsingCamera) {
                                                try {
                                                    stopCamera()
                                                } catch (ex: Exception) {
                                                }
                                            }
                                            isUsingCamera = false
                                        }
                                    }

                                }
                                break
                            }
                        }
                        if (packageName != "" && !isShowLock) {
                            window?.clearView()
                            window = null
                        }
                    }
                }
                handler.postDelayed(this, 300)
            }
        }
        handler.post(runnable)
    }

    private fun getLauncherTopApp(): String {
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        var result = ""
        val event = UsageEvents.Event()
        val usageEvents: UsageEvents = sUsageStatsManager!!.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                result = event.packageName
            }
        }
        if (!TextUtils.isEmpty(result)) {
            return result
        }
        return ""
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.cancelNotification(this)
        }
    }

    override fun onImageCapture(bitmap: Bitmap) {
        if (!isUsingCamera) {
            try {
                stopCamera()
            } catch (ex: Exception) {

            }
        }
        viewModel.saveImage(bitmap, this)
        isUsingCamera = false
    }

    override fun onCameraError(errorCode: Int) {
        Log.d("Huy", "onCameraError: ")
//        TODO("Not yet implemented")
    }

    fun startFromAccessGranted() {
        if (!isAccessGranted) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (Settings.canDrawOverlays(this)&& BackgroundManager.isAccessGranted(this)) {
//                    launchActivity<SplashActivity> {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                } else {
//                    launchActivity<ActivityRequestPermission> {
//                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    }
//                }
//            } else {
//                launchActivity<SplashActivity> {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//            }

            Handler(Looper.getMainLooper()).postDelayed({
                isRequestPermission = false
            }, 300)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun startFromOverLay() {
        if (!isOverLay) {
//            if (Settings.canDrawOverlays(this)&& BackgroundManager.isAccessGranted(this)) {
//                launchActivity<SplashActivity> {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//            } else {
//                launchActivity<ActivityRequestPermission> {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//            }
//            launchActivity<ActivityRequestPermission> {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            }
            Handler(Looper.getMainLooper()).postDelayed({
                isRequestPermission = false
            }, 300)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("nnnnnnnnnnnn", "onDestroy: ")
        //unRegister
        unRegister()
        mHomeWatcher?.stopWatch()
        try {
            this.unregisterReceiver(onListenerScreenState)
        } catch (e: Exception) {
            e.message
        }
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil.cancelNotification(this)
        }
        BackgroundManager.startService( this)
    }

    private fun register() {
        val intent = IntentFilter()
        intent.addAction(Intent.ACTION_PACKAGE_ADDED)
        intent.addAction(Intent.ACTION_PACKAGE_INSTALL)
        intent.addDataScheme("package")
        newAppBroadcastReceiver.listenerNewApp = {
            viewModelApp.loadApp(this, it)
        }
        registerReceiver(newAppBroadcastReceiver, intent)
    }

    private fun unRegister() {
        try {
            unregisterReceiver(newAppBroadcastReceiver)
        } catch (e: Exception) {

        }
    }

    private fun setUpCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {//a
                val cameraConfig = CameraConfig()
                    .getBuilder(this)
                    .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                    .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                    .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                    .setCameraFocus(CameraFocus.AUTO)
                    .build()
                startCamera(cameraConfig)
            }
        }
    }

    private fun checkForeverPermission() {
        //AccessGranted
        isAccessGranted = if (BackgroundManager.isAccessGranted(this@LockService)) {
            startFromAccessGranted()
            true
        } else {
            false
        }
        //OverLay
        isOverLay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                false
            } else {
                startFromOverLay()
                true
            }
        } else {
            true
        }

        isRequestPermission = !isAccessGranted || !isOverLay
    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): LockService = this@LockService
    }

    override fun onBind(intent: Intent): IBinder {
        Log.e("8888888888", "onBind: ")
        return binder
    }
}