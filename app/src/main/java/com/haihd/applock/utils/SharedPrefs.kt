package com.haihd.applock.utils

import android.content.Context
import android.content.SharedPreferences
import com.haihd.applock.key.KeyApp.COUNT_RATE
import com.haihd.applock.key.KeyApp.IS_RATE
import com.google.gson.Gson

object SharedPrefs {
    private val PREFS_NAME = "PREFS_NAME_APP_LOCK"

    fun getDefaultSharedPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getString(context: Context, key: String): String {
        return getDefaultSharedPref(context).getString(key, "") ?: ""
    }

    fun getString(context: Context, key: String, default: String): String {
        return getDefaultSharedPref(context).getString(key, default) ?: default
    }

    fun getBoolean(context: Context, key: String): Boolean {
        return getDefaultSharedPref(context).getBoolean(key, false)
    }

    fun getLong(context: Context, key: String): Long {
        return getDefaultSharedPref(context).getLong(key, 0L)
    }

    fun getInt(context: Context, key: String): Int {
        return getDefaultSharedPref(context).getInt(key, 0)
    }

    fun <T> put(context: Context, key: String, data: T) {
        val editor: SharedPreferences.Editor = getDefaultSharedPref(context).edit()
        if (data is String) {
            editor.putString(key, data as String)
        } else if (data is Boolean) {
            editor.putBoolean(key, (data as Boolean))
        } else if (data is Float) {
            editor.putFloat(key, (data as Float))
        } else if (data is Int) {
            editor.putInt(key, (data as Int))
        } else if (data is Long) {
            editor.putLong(key, (data as Long))
        } else {
            editor.putString(key, Gson().toJson(data))
        }
        editor.apply()
    }

    fun clear(context: Context) {
        getDefaultSharedPref(context).edit().clear().apply()
    }

//    fun setLanguageConfig(context: Context) {
//        getDefaultSharedPref(context).edit().putBoolean(Constants.CONFIG_BT_BACK, true).apply()
//    }

    fun isRated(context: Context): Boolean {
        return getDefaultSharedPref(context).getBoolean(IS_RATE, false)
    }

    fun setRated(context: Context) {
        getDefaultSharedPref(context).edit().putBoolean(IS_RATE, true).apply()
    }

    fun getCountRate(context: Context): Int {
        return getDefaultSharedPref(context).getInt(COUNT_RATE, 0)
    }

    fun increaseCountRate(context: Context) {
        getDefaultSharedPref(context).edit().putInt(COUNT_RATE, getCountRate(context) + 1).apply()
    }

}