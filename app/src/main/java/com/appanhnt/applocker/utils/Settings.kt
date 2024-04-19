package com.appanhnt.applocker.utils


import android.content.SharedPreferences
import com.appanhnt.applocker.key.Icon.ICON
import com.google.android.gms.ads.ez.EzAdControl.getContext
import kotlin.jvm.internal.Intrinsics


class Settings private constructor() {
    //    var isHideTrack: Boolean
//        get() = sharedPreferences.getBoolean(Constants.HIDE_TRACK, false)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.HIDE_TRACK, z)
//            editor.apply()
//        }
//    val patternLineColor: Int
//        get() = ContextCompat.getColor(
//            MyApplication.Companion.getContext(),
//            if (isHideTrack) R.color.transparent else R.color.white
//        )
//    var isRandomKeyboard: Boolean
//        get() = sharedPreferences.getBoolean(Constants.RANDOM_KEYBOARD, false)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.RANDOM_KEYBOARD, z)
//            editor.apply()
//        }
//    var isFingerLock: Boolean
//        get() = sharedPreferences.getBoolean(Constants.FINGER_LOCK, false)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.FINGER_LOCK, z)
//            editor.apply()
//        }
//    var isAppLock: Boolean
//        get() = sharedPreferences.getBoolean(Constants.APP_LOCK, true)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.APP_LOCK, z)
//            editor.apply()
//        }
//    var isVibration: Boolean
//        get() = sharedPreferences.getBoolean(Constants.VIBRATE, false)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.VIBRATE, z)
//            editor.apply()
//        }
//    var isUninstallProtection: Boolean
//        get() = sharedPreferences.getBoolean(Constants.UNINSTALL_PROTECTION, false)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.UNINSTALL_PROTECTION, z)
//            editor.apply()
//        }
//    var isLockNewApp: Boolean
//        get() = sharedPreferences.getBoolean(Constants.LOCK_NEW_APP, true)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.LOCK_NEW_APP, z)
//            editor.apply()
//        }
//    var relockType: Int
//        get() = sharedPreferences.getInt(Constants.RELOCK_TYPE, 1)
//        set(i) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putInt(Constants.RELOCK_TYPE, i)
//            editor.apply()
//        }
//    val relockTypeText: Int
//        get() {
//            val relockType = relockType
//            return if (relockType != 1) if (relockType != 2) R.string.txt_custom_time else R.string.txt_after_screen_off else R.string.txt_after_exit
//        }
//    var lockType: String?
//        get() = sharedPreferences.getString(Constants.LOCK_TYPE, "").toString()
//        set(value) {
//            Intrinsics.checkNotNullParameter(value, "value")
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putString(Constants.LOCK_TYPE, value)
//            editor.apply()
//        }
//    val isPatternLock: Boolean
//        get() = Intrinsics.areEqual(lockType, Constants.PATTERN)
//    val isPINLock: Boolean
//        get() = Intrinsics.areEqual(lockType, Constants.PIN_CODE)
//    var patternCode: String?
//        get() = sharedPreferences.getString(Constants.PATTERN_CODE, "")
//        set(str) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putString(Constants.PATTERN_CODE, str)
//            editor.apply()
//        }
//    var password: String?
//        get() = sharedPreferences.getString(Constants.PASSWORD, "")
//        set(str) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putString(Constants.PASSWORD, str)
//            editor.apply()
//        }
//    val passwordTypeText: Int
//        get() = if (Intrinsics.areEqual(
//                lockType,
//                Constants.PATTERN
//            )
//        ) R.string.txt_pattern else R.string.txt_pin
//    var isIntruderSelfie: Boolean
//        get() = sharedPreferences.getBoolean(Constants.INTRUDER_SELFIE, false)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.INTRUDER_SELFIE, z)
//            editor.apply()
//        }
//    var isDeviceAdminApp: Boolean
//        get() = sharedPreferences.getBoolean(Constants.DEVICE_ADMIN, false)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.DEVICE_ADMIN, z)
//            editor.apply()
//        }
//    var maxWrongTimes: Int
//        get() = sharedPreferences.getInt(Constants.MAX_WRONG_TIMES, 3)
//        set(i) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            var z = false
//            if (1 <= i && i < 7) {
//                z = true
//            }
//            if (z) {
//                editor.putInt(Constants.MAX_WRONG_TIMES, i)
//            }
//            editor.apply()
//        }
    var iconAppId: Int
        get() = sharedPreferences!!.getInt(ICON, 0)
        set(i) {
            val sharedPreferences2 = sharedPreferences
            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
            val editor = sharedPreferences2?.edit()
            Intrinsics.checkNotNullExpressionValue(editor, "editor")
            editor?.putInt(ICON, i)
            editor?.apply()
        }
//    var appNameId: Int
//        get() = sharedPreferences.getInt("AppLock", R.string.app_name)
//        set(i) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putInt("AppLock", i)
//            editor.apply()
//        }
//    var isTakenIntruderSelfie: Boolean
//        get() = sharedPreferences.getBoolean(Constants.INTRUDER_SELFIE_TAKEN, false)
//        set(z) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putBoolean(Constants.INTRUDER_SELFIE_TAKEN, z)
//            editor.apply()
//        }
//    var timeWarning: Long
//        get() = sharedPreferences.getLong(Constants.TIME_WARNING, 0L)
//        set(j) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putLong(Constants.TIME_WARNING, j)
//            editor.apply()
//        }
//    var ratingExitTimes: Long
//        get() = sharedPreferences.getLong(Constants.RATING_EXIT, 1L)
//        set(j) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putLong(Constants.RATING_EXIT, j)
//            editor.apply()
//        }
//    var currentThemeId: Int
//        get() = sharedPreferences.getInt(Constants.CURRENT_THEME, 3)
//        set(i) {
//            val sharedPreferences2 = sharedPreferences
//            Intrinsics.checkNotNullExpressionValue(sharedPreferences2, "sharedPreferences")
//            val editor = sharedPreferences2.edit()
//            Intrinsics.checkNotNullExpressionValue(editor, "editor")
//            editor.putInt(Constants.CURRENT_THEME, i)
//            editor.apply()
//        }

    companion object {
        val INSTANCE = Settings()
//        private var sharedPreferences: SharedPreferences? = null
        private val sharedPreferences: SharedPreferences =
            getContext().getSharedPreferences("AppLock", 0)
    }
}