package com.lutech.ads.extensions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lutech.ads.AdsManager
import com.lutech.ads.utils.Constants

fun Context.isRecordAudioPermissionGranted(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

fun AppCompatActivity.requestAudioPermission() =  ActivityCompat.requestPermissions(
    this,
    arrayOf(Manifest.permission.RECORD_AUDIO),
    Constants.AUDIO_REQUEST_CODE
)

fun Context.isStoragePermissionGranted(): Boolean {
    return if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.TIRAMISU){
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }else{
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

fun AppCompatActivity.requestStoragePermission(){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
            Constants.STORAGE_REQUEST_CODE
        )
    }else{
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.STORAGE_REQUEST_CODE
        )
    }
}

fun Context.isCameraPermissionGranted(): Boolean {
    return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
    else true
}

fun AppCompatActivity.requestCameraPermission(){
    ActivityCompat.requestPermissions(
        this,
        arrayOf(Manifest.permission.CAMERA),
        Constants.CAMERA_REQUEST_CODE
    )
}

fun AppCompatActivity.requestNotificationPermission(){
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),  Constants.NOTIFICATION_REQUEST_CODE)
}

fun Context.isHasFlashLight() = packageManager.hasSystemFeature(
    PackageManager.FEATURE_CAMERA_FLASH) && packageManager.hasSystemFeature(
    PackageManager.FEATURE_CAMERA)

fun Context.isOverlayPermissionGranted(): Boolean =
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) android.provider.Settings.canDrawOverlays(this) else true

fun Context.isNotificationPermissionGranted(): Boolean =
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
        true
    }else{
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

fun AppCompatActivity.isOverlayPermissionGranted(): Boolean{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(this)
    } else {
        true
    }
}

fun AppCompatActivity.requestOverlayPermission(){
    val mHandler: Handler = Handler(Looper.getMainLooper())

    val checkOverlaySetting: Runnable = object : Runnable {
        override fun run() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return
            }
            if (android.provider.Settings.canDrawOverlays(this@requestOverlayPermission)) {
                AdsManager.isUserOutApp = false
                finishActivity(Constants.OVERLAY_REQUEST_CODE)
                return
            }
            mHandler.postDelayed(this, 100)
        }
    }

    try {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, Constants.OVERLAY_REQUEST_CODE)
        mHandler.postDelayed(checkOverlaySetting, 100)
    } catch (e: Exception) {
        e.printStackTrace()
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        startActivityForResult(intent, Constants.OVERLAY_REQUEST_CODE)
        mHandler.postDelayed(checkOverlaySetting, 100)
    }
}