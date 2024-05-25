package com.lutech.ads.utils

import android.app.Activity
import android.content.Context
import androidx.core.content.edit

object SharePref {
//    fun setEnableAds(context: Context){
//        val sharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
//        sharedPreferences.edit {
//            putBoolean(Constants.ENABLE_ADS, false)
//        }
//    }
//
//    fun isEnableAds(context: Context): Boolean{
//        val sharedPreferences = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
//       return sharedPreferences.getBoolean(Constants.ENABLE_ADS, true)
//    }

    fun setIsRating(context: Context) {
        val sharePef = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putBoolean(Constants.KEY_IS_RATING, true)
        editor.apply()
    }

    fun isRating(context: Context): Boolean {
        val sharePef = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        return sharePef.getBoolean(Constants.KEY_IS_RATING, false)
    }

    fun isGetLanguage(context: Context): Boolean {
        val sharePef = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        return sharePef.getBoolean(Constants.IS_SET_LANG, false)
    }

    fun getIOSCountryData(context: Context): String {
        val sharePef = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        return sharePef.getString(Constants.KEY_LANG, "en").toString()
    }

    fun isSetLanguage(context: Context) {
        val sharePef = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putBoolean(Constants.IS_SET_LANG, true)
        editor.apply()
    }

    fun setIOSCountryData(lang: String, context: Context) {
        val sharePef = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        val editor = sharePef.edit()
        editor.putString(Constants.KEY_LANG, lang)
        editor.apply()
    }
}