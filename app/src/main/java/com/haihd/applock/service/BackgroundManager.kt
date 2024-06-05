package com.haihd.applock.service

import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.haihd.applock.R
import com.haihd.applock.item.ItemEnterPin
import com.haihd.applock.key.KeyLock
import com.haihd.applock.utils.NotificationLockUtils
import com.haihd.applock.utils.Provider
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.activity.lock.LockActivity
import com.haihd.applock.key.KeyApp
import com.haihd.applock.utils.RemoteData
import com.lutech.ads.extensions.isNotificationPermissionGranted
import com.lutech.ads.extensions.isOverlayPermissionGranted
import com.orhanobut.hawk.Hawk
import com.tailoredapps.biometricauth.BiometricAuth
import org.koin.core.component.KoinApiExtension


object BackgroundManager {
    const val REQUEST_CODE_OVERLAY = 1
    const val REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 4327

    fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    @OptIn(KoinApiExtension::class)
    fun startLockService(context: Context, activity: AppCompatActivity? = null) {
        val intent = Intent(activity, LockService::class.java)
        try {
            if(PreferencesUtils.getBoolean("APP_LOCK",true) && context.isOverlayPermissionGranted()  && context.isNotificationPermissionGranted()){
                if (isServiceRunning(LockService::class.java, context)) {
                    context.stopService(intent)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && RemoteData.IsStartForegroundService) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }

                activity?.let {
                    Provider.fpManager =
                        BiometricAuth.create(activity, useAndroidXBiometricPrompt = false)
                }
            }
        } catch (ignored: IllegalStateException) {
            runCatching {
                ContextCompat.startForegroundService(context, intent)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun startService(
        context: Context,
        activity: AppCompatActivity? = null,
        serviceClass: Class<*> = LockService::class.java,
    ) {
        val intent = Intent(context, serviceClass)
        try {
            if(PreferencesUtils.getBoolean("APP_LOCK",true) && context.isNotificationPermissionGranted()){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && RemoteData.IsStartForegroundService) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
            activity?.let {
                Provider.fpManager =
                    BiometricAuth.create(activity, useAndroidXBiometricPrompt = false)
            }
        } catch (ignored: IllegalStateException) {
            runCatching {
                ContextCompat.startForegroundService(context, intent)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }
    @OptIn(KoinApiExtension::class)
    fun stopService(
        context: Context,
        serviceClass: Class<*> = LockService::class.java,
    ) {
        context.stopService(Intent(context,serviceClass))
    }

    fun startServiceAndUsageData(
        activity: AppCompatActivity,
        isCreate: Boolean = true,
        isDetailNotify: Boolean = false
    ) {
        if (!isServiceRunning(LockService::class.java, activity)) {
            Provider.fpManager =
                BiometricAuth.create(activity, useAndroidXBiometricPrompt = false)
            startService(activity, activity)
        } else if (!isAccessGranted(activity)) {
            openUsageStats(activity)
        }
        if (!isCreate) {
            val intent = Intent(activity, LockActivity::class.java)
            intent.putExtra(KeyApp.LOCK_MY_APP, true)
            intent.putExtra(NotificationLockUtils.DETAIL, isDetailNotify)
            intent.putExtra(
                KeyLock.PKG_APP, "com.appanhnt.applocker"
            )
            activity.startActivity(intent)
            activity.finish()
        }
    }

    fun checkPermission(context: Context): Boolean {
        if (!isAccessGranted(context)) {
            return false
        }

        if (!Settings.canDrawOverlays(context)) {
            return false
        }

        if (!context.isNotificationPermissionGranted()) return false
        return isIgnoringBatteryOptimizations(context)
    }

    @KoinApiExtension
    fun openUsageStats(context: Context) {
        LockService.isRequestPermission = true
        val intent = Intent("android.settings.USAGE_ACCESS_SETTINGS")
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("package:" + context.packageName)
        try {
            context.startActivity(intent)
        } catch (unused: Exception) {
            intent.data = Uri.fromParts("package", context.packageName, null)
            try {
                context.startActivity(intent)
            } catch (unused2: Exception) {
                try {
                    intent.data = null
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.can_not_navigate_there),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun isAccessGranted(context: Context): Boolean {
        return try {
            val packageManager: PackageManager = context.packageManager
            val applicationInfo: ApplicationInfo =
                packageManager.getApplicationInfo(context.packageName, 0)
            val appOpsManager: AppOpsManager =
                context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            var mode = 0
            mode = appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid,
                applicationInfo.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun checkCreatePassword(context: Context): Boolean {
        Log.e(
            "hhhhhhhhh",
            "savePassLock: ${
                Hawk.get(
                    KeyLock.KEY_PATTERN,
                    (mutableListOf<Int>())
                )
            } ${PreferencesUtils.getString(KeyLock.LOCK, context.getString(R.string.pattern))},"
        )
        when (PreferencesUtils.getString(KeyLock.LOCK, context.getString(R.string.pattern))) {
            context.getString(R.string.pattern) -> {
                Log.e(
                    "hhhhhhhhh",
                    "savePassLock:333 ${Hawk.get(KeyLock.KEY_PATTERN, (mutableListOf<Int>()))}",
                )
                if (!Hawk.get(KeyLock.KEY_PATTERN, (mutableListOf<Int>())).isNullOrEmpty()) {
                    return true
                }
            }

            context.getString(R.string.pin) -> {
                if (!Hawk.get(KeyLock.KEY_PIN, mutableListOf<ItemEnterPin>()).isNullOrEmpty()) {
                    Log.e(
                        "hhhhhhhhh",
                        "savePassLock: ${Hawk.get(KeyLock.KEY_PIN, mutableListOf<ItemEnterPin>())}",
                    )

                    return true
                }
            }

            else -> {
                Log.e(
                    "hhhhhhhhh",
                    "savePassLock:444 ${Hawk.get(KeyLock.KEY_PATTERN, (mutableListOf<Int>()))}",
                )
                if (!Hawk.get(KeyLock.KEY_PATTERN, (mutableListOf<Int>())).isNullOrEmpty()) {
                    return true
                }
            }
        }
        return false
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val packageName: String = context.packageName
        val powerManager = getSystemService(context, PowerManager::class.java)
        return powerManager?.isIgnoringBatteryOptimizations(packageName) ?: false
    }
}