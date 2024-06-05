package com.haihd.applock.windowmanager

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.haihd.applock.R
import com.haihd.applock.databinding.LayoutDialogFingerprintBinding
import com.haihd.applock.databinding.LayoutLockAppBinding
import com.haihd.applock.dialog.DialogFingerprint
import com.haihd.applock.dialog.DialogQuestion
import com.haihd.applock.key.IntrusionAlert
import com.haihd.applock.key.KeyLock
import com.haihd.applock.key.KeyQuestion
import com.haihd.applock.key.KeyTheme
import com.haihd.applock.utils.Provider
import com.haihd.applock.utils.ThemeUtils
import com.haihd.applock.utils.VibrationUtil
import com.haihd.applock.viewmodel.ThemeViewModel
import com.haihd.applock.widget.MyGroupView
import com.anhnt.baseproject.utils.PreferencesUtils
import com.orhanobut.hawk.Hawk
import com.reginald.patternlockview.PatternLockView
import com.tailoredapps.biometricauth.BiometricAuthenticationCancelledException
import com.tailoredapps.biometricauth.BiometricAuthenticationException
import com.tailoredapps.biometricauth.delegate.androidxlegacy.AndroidXBiometricAuth
import com.tailoredapps.biometricauth.delegate.pie.PieBiometricAuth
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

@KoinApiExtension
open class MyWindowLockScreen(var context: Context, var pkg: String?) : KoinComponent,
    AppCompatActivity() {
    private lateinit var binding: LayoutLockAppBinding
    private lateinit var windowManager: WindowManager
    private var dialogQuestion: DialogQuestion? = null
    private var dialogFinger: DialogFingerprint? = null
    private val viewThemeModel by inject<ThemeViewModel>()
    var takePhoto: (() -> Unit)? = null
    var stopCamera: (() -> Unit)? = null

    init {
        initView()
        initListener()
        initData()
    }

    private fun initData() {
        // theme set up background
        if (PreferencesUtils.getString(
                KeyTheme.KEY_BACKGROUND,
                "default"
            ) != KeyTheme.BG_CUSTOM
        ) {
            binding.themeBackground.setImageDrawable(ThemeUtils.themeBackground(context))
        } else {
            viewThemeModel.customBackground.observeForever {
                Glide.with(context).load(it).apply(
                    RequestOptions().override(
                        binding.themeBackground.width,
                        binding.themeBackground.height
                    )
                ).into(binding.themeBackground)
            }
        }
    }

    private fun initView() {
        Log.e(
            "hhhhhhhhh",
            "checkViewLock:22 ${
                PreferencesUtils.getString(
                    KeyLock.LOCK,
                    context.getString(R.string.pattern)
                )
            }",
        )

        //
        setLocale(PreferencesUtils.getString("KEY_LANGUAGE", "en"))
        val myGroup = MyGroupView(context)
        windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        binding = LayoutLockAppBinding.inflate(LayoutInflater.from(context), myGroup, false)
        // set up view
        Log.e("ttttttttttt", "initView: ${PreferencesUtils.getBoolean("APP_LOCK", true)}")
        checkViewLock()
//        //set up theme
        binding.textLock.setTextColor(ThemeUtils.getLineColorText())

        windowManager.addView(binding.root, setupLayout())
        //
        //load ads
//        loadAds()
        // finger print
        if (PreferencesUtils.getBoolean(KeyLock.LOCK_FINGER, false)) {
            binding.icFinger.visibility = View.VISIBLE
            //dialog custom
            binding.icFinger.setOnLongClickListener {
                showDialogFingerDevice()
                true
            }

            //

        } else {
            binding.icFinger.visibility = View.GONE

        }
        //
        //load icon
        if (!pkg.isNullOrEmpty()) {
            try {
                val iconApp = context.packageManager.getApplicationIcon(pkg!!)
                Glide.with(context).load(iconApp).into(binding.icIconApp)
            } catch (ex: PackageManager.NameNotFoundException) {
            }
        }
    }

    private fun showDialogFingerDevice() {
        // dialog he thong neu co
        Provider.fpManager?.let {
            if (!it.hasFingerprintHardware) {
                //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
            } else if (!it.hasFingerprintsEnrolled) {
                //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
            } else {
                it.authenticate(
                    title = "Please authenticate",
                    subtitle = "'Awesome Feature' requires your authentication",
                    description = "'Awesome Feature' exposes data private to you, which is why you need to authenticate.",
                    negativeButtonText = "Cancel",
                    prompt = "Touch the fingerprint sensor",
                    notRecognizedErrorText = "Not recognized"
                )
                    .subscribe(
                        {
                            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                dialogFinger?.dismiss()
                                Log.e("pppppppppppp", "showDialogFingerDevice:111 ")

                            }, 0)
                            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                passWordCorrect()
                                Log.e("pppppppppppp", "showDialogFingerDevice: 2222")
                            }, 300)
                        },
                        { throwable ->
                            when (throwable) {
                                is BiometricAuthenticationCancelledException -> {
                                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                        dialogFinger?.dismiss()
                                        Log.e("pppppppppppp", "showDialogFingerDevice:333 ")

                                    }, 0)
                                }

                                is BiometricAuthenticationException -> {
                                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                        dialogFinger?.dismiss()
                                        Log.e("pppppppppppp", "showDialogFingerDevice:444 ")
                                        passWordCorrect()

                                    }, 0)
                                    // Might want to check throwable.errorMessageId for fields in BiometricConstants.Error,
                                    // to get more information about the error / further actions here.
                                }

                                else -> {
                                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                                        dialogFinger?.dismiss()
                                        Log.e("pppppppppppp", "showDialogFingerDevice:555 ")

                                    }, 0)
                                }
                            }
                        }
                    )
            }
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    private fun initListener() {
        // patter lock

        binding.patternLock.binding.lockView.setCallBack {
            when (it.list) {
                Hawk.get(
                    KeyLock.KEY_PATTERN, (mutableListOf<Int>())
                ) -> {
                    PreferencesUtils.putInteger(KeyLock.TIME_ERROR, 0)
                    PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, 0)
                    passWordCorrect()
                    PatternLockView.CODE_PASSWORD_CORRECT
                }

                else -> {
                    takePhoto()
                    //
                    passWordInCorrect(context.getString(R.string.wrong_pattern))
                    PatternLockView.CODE_PASSWORD_ERROR
                }
            }
        }
        // pin  lock
        binding.pinLock.listenerCorrect = {
            PreferencesUtils.putInteger(KeyLock.TIME_ERROR, 0)
            PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, 0)
            Handler(Looper.getMainLooper()).postDelayed({
                passWordCorrect()
            }, 270)
        }
        binding.pinLock.listenerInCorrect = {
            takePhoto()
            passWordInCorrect(context.getString(R.string.pin_incorrect))
        }
        //
    }

    private fun takePhoto() {
        val time = PreferencesUtils.getInteger(KeyLock.TIME_ERROR, 0)
        val timeQuestion = PreferencesUtils.getInteger(KeyLock.TIME_QUESTION, 0)
        // error
        if (time == PreferencesUtils.getInteger(KeyLock.TIME_TAKE_PHOTO, 3) - 1) {
            if (PreferencesUtils.getBoolean(IntrusionAlert.ON_INTRUSION, false)) {
                takePhoto?.invoke()//
            }
            PreferencesUtils.putInteger(KeyLock.TIME_ERROR, 0)
            PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, 0)
        } else {
            PreferencesUtils.putInteger(KeyLock.TIME_ERROR, time + 1)
        }
        //question
        if (timeQuestion == 5) {
            if (PreferencesUtils.getString(KeyQuestion.KEY_ANSWER).isNotEmpty()) {
                showDialogQuestion()
            }
            PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, 0)
        } else {
            PreferencesUtils.putInteger(KeyLock.TIME_QUESTION, timeQuestion + 1)
        }
    }

    private fun setupLayout(): WindowManager.LayoutParams {
        val mLayoutParams: WindowManager.LayoutParams
        val flag =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        val type =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        val height = getHeight()
        mLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, height, type, flag, PixelFormat.TRANSLUCENT
        )
        mLayoutParams.gravity = Gravity.TOP
        mLayoutParams.alpha = 1F //
        return mLayoutParams
    }

    fun clearView() {
        try {
            windowManager.removeView(binding.root)
            stopCamera?.invoke()
        } catch (e: IllegalArgumentException) {
        }
    }

    private fun getHeight(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        return if (getNavigationBarSize(context).y > 0) {
            height + (getNavigationBarSize(context).y)
        } else {
            WindowManager.LayoutParams.MATCH_PARENT
        }
    }

    private fun getNavigationBarSize(context: Context): Point {
        val appUsableSize = getAppUsableScreenSize(context)
        val realScreenSize = getRealScreenSize(context)

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
        }
        // navigation bar at the bottom
        return if (appUsableSize.y < realScreenSize.y) {
            Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
        } else Point()
        // navigation bar is not present
    }

    private fun getAppUsableScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    private fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        return size
    }

    private fun checkViewLock() {
        binding.patternLock.binding.lockView.apply {
            setEnableVibrate(PreferencesUtils.getBoolean(KeyLock.VIBRATION, true))
            setPatternInvisible(PreferencesUtils.getBoolean(KeyLock.HIDE_DRAW_LINE, false))
        }
        Log.e(
            "nnnnnnnnnnnn",
            "checkViewLock:1111 ${context.getString(R.string.pattern)} ${
                PreferencesUtils.getString(
                    KeyLock.LOCK,
                    context.getString(R.string.pattern)
                )
            }  ${PreferencesUtils.getBoolean(KeyLock.HIDE_DRAW_LINE, true)}",
        )

        when (PreferencesUtils.getString(KeyLock.LOCK, context.getString(R.string.pattern))) {
            context.getString(R.string.pattern) -> {
                Log.e(
                    "nnnnnnnnnnnn",
                    "checkViewLock:222 ${context.getString(R.string.pattern)} ${
                        PreferencesUtils.getString(
                            KeyLock.LOCK,
                            context.getString(R.string.pattern)
                        )
                    }"
                )
                binding.patternLock.visibility = View.VISIBLE
                binding.pinLock.visibility = View.GONE
                binding.viewPinLock.visibility = View.GONE
                binding.textLock.text = context.getString(R.string.content_pattern)
            }

            context.getString(R.string.pin) -> {

                Log.e(
                    "nnnnnnnnnnnn",
                    "checkViewLock:3333 ${
                        PreferencesUtils.getString(
                            KeyLock.LOCK,
                            context.getString(R.string.pattern)
                        )
                    }"
                )
                binding.pinLock.visibility = View.VISIBLE
                binding.viewPinLock.visibility = View.VISIBLE
                binding.patternLock.visibility = View.INVISIBLE
                binding.textLock.text = context.getString(R.string.enter_pin)
            }
        }
    }

    private fun passWordCorrect() {
        clearView()
    }

    private fun passWordInCorrect(error: String) {
        if (PreferencesUtils.getBoolean(KeyLock.VIBRATION, true)) {
            VibrationUtil.startVibration(context)
        }
        binding.textLock.setTextColor(context.resources.getColor(R.color.color_CCAC2D2D))
        binding.textLock.text = error
    }

    private fun showDialogQuestion() {
        dialogQuestion = DialogQuestion(context, R.style.StyleDialog)
        dialogQuestion?.setCancelable(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            dialogQuestion?.window!!.setType(
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            )
        else
            dialogQuestion?.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        dialogQuestion?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialogQuestion?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogQuestion?.show()
        dialogQuestion?.listenerClickOkay = {
            val answer = PreferencesUtils.getString(KeyQuestion.KEY_ANSWER, "")
            if (it.equals(answer, true)) {
                passWordCorrect()
            } else {
                passWordInCorrect(context.getString(R.string.answer_incorrect))
            }
            dialogQuestion?.dismiss()
        }
    }

    private fun showDialogCustomFinger() {
        val bindDialog =
            LayoutDialogFingerprintBinding.inflate(LayoutInflater.from(context))
        dialogFinger = DialogFingerprint(context, bindDialog) {
            showDialogFingerDevice()
        }
        dialogFinger?.let {
            it.setCancelable(true)
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                it.window!!.setType(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                )
            else
                it.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        }
        dialogFinger?.setOnDismissListener {
            Provider.fpManager?.let {
                when (it) {
                    is PieBiometricAuth -> {
                        it.cancellationSignal?.cancel()
                    }

                    is AndroidXBiometricAuth -> {
                        it.biometricPrompt?.cancelAuthentication()
                    }
                }
            }
        }
        dialogFinger?.show()
    }
}