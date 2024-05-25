package com.appanhnt.applocker.activity.setting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.*
import com.appanhnt.applocker.R
import com.appanhnt.applocker.adapter.SettingAdapter
import com.appanhnt.applocker.databinding.ActivitySettingsBinding
import com.appanhnt.applocker.dialog.DialogQuestion
import com.appanhnt.applocker.item.ItemSetting
import com.appanhnt.applocker.key.KeyLock
import com.appanhnt.applocker.key.KeyQuestion
import com.appanhnt.applocker.service.BackgroundManager
import com.appanhnt.applocker.utils.AppUtil
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.KeyboardUtils
import com.anhnt.baseproject.utils.PreferencesUtils
import com.appanhnt.applocker.activity.locktype.LockTypeActivity
import com.appanhnt.applocker.activity.recovery.RecoveryPasswordActivity
import com.appanhnt.applocker.activity.language.LanguageActivity
import com.appanhnt.applocker.activity.lock.LockActivity
import com.appanhnt.applocker.activity.icon.IconCamouflage2Activity
import com.appanhnt.applocker.dialog.DialogPasswordType
import com.appanhnt.applocker.dialog.DialogRelockSettings
import com.appanhnt.applocker.service.LockService
import com.appanhnt.applocker.utils.ActionUtils
import com.appanhnt.applocker.utils.SharedPrefs

import com.tailoredapps.biometricauth.BiometricAuth
import org.koin.core.component.KoinApiExtension

class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {
    private val listSetting = mutableListOf<ItemSetting>()
    private lateinit var settingAdapter: SettingAdapter
    private lateinit var mDialogPasswordType: DialogPasswordType
    private lateinit var mDialogRelockSettings: DialogRelockSettings
    private var dialogQuestion: DialogQuestion? = null
    private lateinit var mService: LockService
    private var mBound: Boolean = false

    @OptIn(KoinApiExtension::class)
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LockService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }

    @OptIn(KoinApiExtension::class)
    override fun onStart() {
        super.onStart()
        Intent(this, LockService::class.java).also { intent ->
            this.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
    @OptIn(KoinApiExtension::class)
    override fun initView() {
        setLocale(PreferencesUtils.getString(KEY_LANGUAGE, "en"))
        mDialogPasswordType = DialogPasswordType(this){
            type ->
            changePasswordType(type)
        };
        mDialogRelockSettings = DialogRelockSettings(this){
                typeRelock ->
           changeRelockType(typeRelock)
        };
        val type =  PreferencesUtils.getString(KeyLock.LOCK, this.getString(R.string.pattern))
        intPasswordType(type)
        initRelockType(PreferencesUtils.getBoolean(KeyLock.AFTER_SCREEN_OF, false))

        setFeatureSetting()
        //translucentStatus
        setStatusBarHomeTransparent(this)
        binding.head.setPadding(0, getHeightStatusBar(), 0, 0)
        // lock new app
        binding.switchLockApp.isChecked = PreferencesUtils.getBoolean(KeyLock.LOCK_NEW_APP, false)
        binding.switchVibration.isChecked = PreferencesUtils.getBoolean(KeyLock.VIBRATION, true)
        binding.switchHideDraw.isChecked = PreferencesUtils.getBoolean(KeyLock.HIDE_DRAW_LINE, false)
        //
        settingAdapter = SettingAdapter(this, listSetting)
        settingAdapter.listenerClick = {
            val data = listSetting[it]
            when (data.name) {
                getString(R.string.change_passwrod) -> {
                    launchActivity<LockActivity> {
                        putExtra(
                            KeyLock.CHANGE_PASSWORD, true
                        )
                        putExtra(
                            KeyLock.PKG_APP, "com.appanhnt.applocker"
                        )

                    }
                }

                getString(R.string.change_question) -> {
                    showDialogQuestion()
                }

                getString(R.string.rate) -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data =
                        Uri.parse("market://details?id=com.ezmobi.applock")
                    startActivity(intent)
                }

                getString(R.string.language) -> {
                    launchActivity<LanguageActivity> {
                        putExtra("SETTING","LANGUAGE_SETTING")
                    }
                }
            }
        }
        binding.rclSetting.adapter = settingAdapter

        // load ads native
        // check view fingerprint
        checkFingerprint {
            when (it) {
                AppUtil.HAVE_SUPPORT -> {
                    binding.swichFinger.isChecked =
                        PreferencesUtils.getBoolean(KeyLock.LOCK_FINGER, false)
                }

                AppUtil.NO_ANY_FINGERPRINT -> {
                    binding.swichFinger.isChecked = false
                    //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
                }

                AppUtil.NOT_SUPPORT -> {
                    binding.swichFinger.isChecked = false
                    //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
                }
            }
        }
        setUpRate()
    }

    private fun intPasswordType(type: String){
        if (type == "PIN"){
            binding.txtPasswordType.text = getString(R.string.pin)
        }else{
            binding.txtPasswordType.text = getString(R.string._pattern)
        }
    }
    private fun changePasswordType(type: String){
        if (type == "PIN"){
            binding.txtPasswordType.text = getString(R.string.pin)
        }else{
            binding.txtPasswordType.text = getString(R.string._pattern)
        }
        if (!PreferencesUtils.getBoolean(type)){
            val intent = Intent(this, LockTypeActivity::class.java)
            val bundle = Bundle()
            bundle.putString("TYPE",type)
            bundle.putBoolean("PASSWORD_TYPE",true)
            intent.putExtras(bundle)
            startActivity(intent)
        }else{
            PreferencesUtils.putString(KeyLock.LOCK,type)
        }

    }

    private fun initRelockType(type: Boolean){
        if (type){
            binding.txtRelock.text = getString(R.string.after_screen_off)
        }else{
            binding.txtRelock.text = getString(R.string.after_exit)
        }
    }
    @OptIn(KoinApiExtension::class)
    private fun changeRelockType(type: Boolean){
      if (type){
          binding.txtRelock.text = getString(R.string.after_screen_off)
          PreferencesUtils.putBoolean(KeyLock.AFTER_SCREEN_OF,true)
          mService.registerScreenStateBroadCast()
      }else{
          binding.txtRelock.text = getString(R.string.after_exit)
          PreferencesUtils.putBoolean(KeyLock.AFTER_SCREEN_OF,false)
          mService.unRegisterScreenStateBroadCast()
      }

    }



    private fun setFeatureSetting() {
        binding.llLanguage.setOnClickListener {
            launchActivity<LanguageActivity> {
                putExtra("SETTING","LANGUAGE_SETTING")
            }
        }

        binding.btnChangePass.setOnClickListener {
            launchActivity<LockActivity> {
                putExtra(
                    KeyLock.CHANGE_PASSWORD, true
                )
                putExtra(
                    KeyLock.PKG_APP, "com.appanhnt.applocker"
                )
            }
        }
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            Log.e("nnnnnnnnnnnn", "setFeatureSetting:  switchVibration $isChecked", )

            PreferencesUtils.putBoolean(KeyLock.VIBRATION,isChecked)
        }

        binding.switchHideDraw.setOnCheckedChangeListener { _, isChecked ->
            Log.e("nnnnnnnnnnnn", "setFeatureSetting:  switchHideDraw $isChecked", )
            PreferencesUtils.putBoolean(KeyLock.HIDE_DRAW_LINE,isChecked)
        }

        binding.switchAppLock.isChecked = PreferencesUtils.getBoolean("APP_LOCK",true)
        binding.switchAppLock.setOnCheckedChangeListener { _, isChecked ->
            PreferencesUtils.putBoolean("APP_LOCK",isChecked)
            if (isChecked){
//                if (!BackgroundManager.isServiceRunning(LockService::class.java, this)) {
                    Log.e("ttttt", "startService: ", )
                    BackgroundManager.startService(LockService::class.java, this, this)
//                }
            }else{
                if (BackgroundManager.isServiceRunning(LockService::class.java, this)) {
                    Log.e("ttttt", "stopService: ", )
                    BackgroundManager.stopService(LockService::class.java, this, this)
                }
            }
        }

        binding.btnFeedback.setOnClickListener {
            ActionUtils.sendFeedback(this)
        }

        binding.llShare.setOnClickListener {
            ActionUtils.shareApp(this)
        }

        binding.llPolicy.setOnClickListener {
            ActionUtils.showPolicy(this)
        }
        binding.llPatternDrop.setOnClickListener {
            showPopupMenu(it)
        }
        binding.llRelockSetting.setOnClickListener {
            showPopupMenuRelock(it)
        }

        binding.llRateUs.setOnClickListener {
            ActionUtils.showRateDialog(this, false, callback = {
                if (it) hideRate()
            })
        }

        binding.llIcon.setOnClickListener {
            launchActivity<IconCamouflage2Activity> { }
        }
    }

    private fun hideRate() {
        binding.llRateUs.visibility = View.GONE
    }

    private fun setUpRate() {
        if (SharedPrefs.isRated(this)) {
            hideRate()
        }
    }

    override fun initData() {
        listSetting.clear()
        listSetting.apply {
            add(
                ItemSetting(
                    R.drawable.ic_change_password, getString(R.string.change_passwrod), null
                )
            )
            add(
                ItemSetting(
                    R.drawable.ic_change_question, getString(R.string.change_question), null
                )
            )
            add(
                ItemSetting(
                    R.drawable.ic_rate, getString(R.string.rate), null
                )
            )
            add(
                ItemSetting(
                    R.drawable.ic_rate, getString(R.string.language), null
                )
            )
        }
    }

    override fun initListener() {
        binding.icBackSettings.setOnClickListener {
            onBackPressed()
        }
        //
        binding.switchLockApp.setOnCheckedChangeListener { buttonView, isChecked ->
            PreferencesUtils.putBoolean(KeyLock.LOCK_NEW_APP, isChecked)
        }
        //
        binding.swichFinger.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkFingerprint {
                    when (it) {
                        AppUtil.HAVE_SUPPORT -> {
                            PreferencesUtils.putBoolean(KeyLock.LOCK_FINGER, true)
                        }

                        AppUtil.NO_ANY_FINGERPRINT -> {
                            binding.swichFinger.isChecked = false
                            //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
                            toast(getString(R.string.dont_have_fingerprint))
                        }

                        AppUtil.NOT_SUPPORT -> {
                            binding.swichFinger.isChecked = false
                            //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
                            toast(getString(R.string.can_not_use_finger_print))
                        }
                    }
                }
            } else {
                PreferencesUtils.putBoolean(KeyLock.LOCK_FINGER, false)
            }
        }
    }

    override fun viewBinding(): ActivitySettingsBinding {
        return ActivitySettingsBinding.inflate(LayoutInflater.from(this))
    }

    private fun checkFingerprint(state: (Int) -> Unit) {
        val biometricAuth = BiometricAuth.create(this, useAndroidXBiometricPrompt = false)
        if (!biometricAuth.hasFingerprintHardware) {
            state.invoke(AppUtil.NOT_SUPPORT)
//            //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
        } else if (!biometricAuth.hasFingerprintsEnrolled) {
//            //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
            state.invoke(AppUtil.NO_ANY_FINGERPRINT)
        } else {
            state.invoke(AppUtil.HAVE_SUPPORT)
        }
    }

    private fun showDialogQuestion() {
        KeyboardUtils.showSoftKeyboard(this)
        dialogQuestion = DialogQuestion(this, R.style.StyleDialog)
        dialogQuestion?.setCanceledOnTouchOutside(true)
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
            KeyboardUtils.hideSoftKeyboardToggleSoft(this)
            val answer = PreferencesUtils.getString(KeyQuestion.KEY_ANSWER, "")
            if (it.equals(answer, true)) {
                launchActivity<RecoveryPasswordActivity> {
                    putExtra(KeyQuestion.KEY_CHANGE_QUESTION, true)
                }
                dialogQuestion?.dismiss()
            } else {
                dialogQuestion?.binding?.txtError?.visibility = View.VISIBLE
            }
        }
    }

    override fun onStop() {
        super.onStop()
        this.unbindService(connection)
        mBound = false
    }

    private fun showPopupMenu(view: View) {
        mDialogPasswordType.showAsDropDown(view)
    }
    private fun showPopupMenuRelock(view: View) {
        mDialogRelockSettings.showAsDropDown(view)
    }
}