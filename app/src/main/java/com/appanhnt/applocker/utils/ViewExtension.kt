package com.appanhnt.applocker.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import com.appanhnt.applocker.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

fun View.disableFocus() {
    this.isFocusableInTouchMode = false
    this.isFocusable = false
}

fun View.enableFocus() {
    this.isFocusableInTouchMode = true
    this.isFocusable = true
}

fun View.showKeyboard(context: Context) {
    this.requestFocus()
    val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}


fun View.hide() {
    this.isVisible = false
}

fun View.show() {
    this.isVisible = true
}

fun Context.logEvent(eventName: String) {
    try {
        Log.d("android_log", "logEvent: $eventName")
        val bundle = Bundle()
        bundle.putString("EVENT", eventName)
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mFirebaseAnalytics.logEvent(eventName, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showToastDebug(msg: String) {
    if (BuildConfig.DEBUG) {
        Log.d("android_log", ": $msg");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun Context.log(msg: String) {
    Log.d("android_log", msg);
}


fun Context.setUserProperty(key: String) {
    try {
        val mFirebaseAnalytics = this.let { FirebaseAnalytics.getInstance(it) }
        mFirebaseAnalytics.setUserProperty(key, key)
//        showToastDebug(key)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
