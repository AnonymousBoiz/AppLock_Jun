package com.appanhnt.applocker.service

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
import androidx.core.content.ContextCompat.getSystemService
import com.appanhnt.applocker.R
import com.appanhnt.applocker.item.ItemEnterPin
import com.appanhnt.applocker.key.KeyLock
import com.appanhnt.applocker.utils.NotificationLockUtils
import com.appanhnt.applocker.utils.Provider
import com.anhnt.baseproject.utils.PreferencesUtils
import com.appanhnt.applocker.activity.lock.LockActivity
import com.appanhnt.applocker.key.KeyApp
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

    fun startService(
        serviceClass: Class<*>,
        context: Context,
        activity: AppCompatActivity? = null
    ) {
        if (PreferencesUtils.getBoolean("APP_LOCK",true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundServices(serviceClass, context)
            } else context.startService(Intent(context, serviceClass))
        }
        activity?.let {
            Provider.fpManager =
                BiometricAuth.create(activity, useAndroidXBiometricPrompt = false)
        }
    }
    fun stopService(
        serviceClass: Class<*>,
        context: Context,
        activity: AppCompatActivity? = null
    ) {
        context.stopService(Intent(activity,serviceClass))
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startForegroundServices(serviceClass: Class<*>, context: Context) {
        context.startForegroundService(Intent(context, serviceClass))
    }

    fun requestPermission(
        activity: AppCompatActivity,
        isCreate: Boolean = true,
    ) {
        if (!isServiceRunning(LockService::class.java, activity)) {
            Provider.fpManager =
                BiometricAuth.create(activity, useAndroidXBiometricPrompt = false)
            startService(LockService::class.java, activity, activity)
        }
    }

    fun startServiceAndUsageData(
        activity: AppCompatActivity,
        isCreate: Boolean = true,
        isDetailNotify: Boolean = false
    ) {
        if (!isServiceRunning(LockService::class.java, activity)) {
            Provider.fpManager =
                BiometricAuth.create(activity, useAndroidXBiometricPrompt = false)
            startService(LockService::class.java, activity, activity)
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