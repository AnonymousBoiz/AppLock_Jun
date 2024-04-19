package com.appanhnt.applocker.activity

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.ActivityLockBinding
import com.appanhnt.applocker.dialog.DialogQuestion
import com.appanhnt.applocker.item.ItemEnterPin
import com.appanhnt.applocker.key.KeyLock
import com.appanhnt.applocker.key.KeyQuestion
import com.appanhnt.applocker.key.KeyTheme
import com.appanhnt.applocker.service.BackgroundManager
import com.appanhnt.applocker.utils.ThemeUtils
import com.appanhnt.applocker.utils.VibrationUtil
import com.appanhnt.applocker.viewmodel.AppLockViewModel
import com.appanhnt.applocker.viewmodel.ThemeViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.KeyboardUtils
import com.anhnt.baseproject.utils.PreferencesUtils
import com.appanhnt.applocker.activity.MainLockActivity
import com.appanhnt.applocker.activity.SettingsActivity
import com.appanhnt.applocker.key.KeyApp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.ez.EzAdControl
import com.orhanobut.hawk.Hawk
import com.reginald.patternlockview.PatternLockView
import org.koin.android.ext.android.inject


class CreateLockActivity : BaseActivity<ActivityLockBinding>() {
    private var passCreate: PatternLockView.Password? = null
    private var listEnterLock: MutableList<ItemEnterPin> = mutableListOf()
    private var isPinAgain = false
    private val viewModel by inject<AppLockViewModel>()
    private val viewThemeModel by inject<ThemeViewModel>()
    private var styleViewLock: String? = null
    private var actionFromSetting = false;
    private var isPin: Boolean = false;
    override fun initView() {
        setStatusBarHomeTransparent(this)
        val padding = resources.getDimensionPixelSize(R.dimen._15sdp)
        binding.layoutCreateLock.setPadding(padding, getHeightStatusBar(), padding, padding)
        // request permission
//        checkCreateLock() //
        val bundle = intent.extras
        val type = bundle?.getString("TYPE") ?: getString(R.string.pattern)
        actionFromSetting = bundle?.getBoolean("PASSWORD_TYPE", false) ?: false
        binding.llSwitch.setOnClickListener {
            isPin = !isPin
            styleViewLock = if (isPin) getString(R.string.pin) else getString(R.string.pattern)
            checkViewLock()
        }
        if (actionFromSetting){
            binding.btnBack.visibility = View.VISIBLE
            styleViewLock = type
            Log.e("vvvvv", "initView: $type", )
            binding.btnBack.setOnClickListener {
                finish()
            }
        }else{
            binding.btnBack.visibility = View.GONE
        }
        checkViewLock()
    }

    override fun viewBinding(): ActivityLockBinding {
        return ActivityLockBinding.inflate(LayoutInflater.from(this))
    }

    override fun initData() {
        // theme set up background
        if (PreferencesUtils.getString(
                KeyTheme.KEY_BACKGROUND,
                "default"
            ) != KeyTheme.BG_CUSTOM
        ) {
            binding.themeBackground.setImageDrawable(ThemeUtils.themeBackground(this))
        } else {
            viewThemeModel.customBackground.observe(this) {
                Glide.with(this).load(it).apply(
                    RequestOptions().override(
                        binding.themeBackground.width,
                        binding.themeBackground.height
                    )
                ).into(binding.themeBackground)
            }
        }
    }

    override fun initListener() {
        // pattern lock

        binding.patternLock.binding.lockView.setCallBack { password ->
            if (password.list.size > 3) {
//                listenerCorrect?.invoke()
                when {
                    passCreate == null -> {
                        passCreate = password
                        resetPattern()
                        binding.ivStep.isSelected = true
                        binding.ivStep.setImageResource(R.drawable.ic_set_password2)
                        binding.tvTitle.text = getString(R.string.confirm_your_password)
//                        binding.textLock.text = resources.getString(R.string.make_pass_again)
                        PatternLockView.CODE_PASSWORD_CORRECT
                    }

                    passCreate != null && password == passCreate -> {
//                        binding.textLock.setTextColor(ThemeUtils.getLineColorText())
//                        binding.textLock.text = resources.getString(R.string.click_create_passwork)
//                        binding.btnCreate.visibility = View.VISIBLE
                        savePassLock()
                        binding.patternLock.binding.lockView.showPasswordWithAnim(password.list)

                        PatternLockView.CODE_PASSWORD_CORRECT
                    }

                    passCreate != null && password != passCreate -> {
                        if (PreferencesUtils.getBoolean(KeyLock.VIBRATION, true)) {
                            VibrationUtil.startVibration(this)
                        }
//                        binding.textLock.setTextColor(resources.getColor(R.color.color_CCAC2D2D))
//                        binding.textLock.text = resources.getString(R.string.make_pass_again)
                        PatternLockView.CODE_PASSWORD_ERROR
                    }

                    else -> {
                        PatternLockView.CODE_PASSWORD_ERROR
                    }
                }

            } else {
                // password is error
                if (PreferencesUtils.getBoolean(KeyLock.VIBRATION, true)) {
                    VibrationUtil.startVibration(this)

                }
//                binding.textLock.setTextColor(resources.getColor(R.color.color_CCAC2D2D))
                resetPattern()
                PatternLockView.CODE_PASSWORD_ERROR
            }
        }
        // click create
//        binding.btnCreate.setOnClickListener {
//            savePassLock()
//        }
        //click select view lock
//        binding.layoutSelectLock.setOnClickListener {
//            val dialogSelectLock = DialogSelectLock(this, binding.textSelectLock.text.toString())
//            dialogSelectLock.apply {
//                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                show()
//                listenerClickOkay = {
//                    styleViewLock = it
//                    checkViewLock(true)
//                }
//            }
//        }
        // click number
        binding.pinLock.listenerEnter = { mlistEnterLock ->

//            listCreatePinLock.clear()
            if (mlistEnterLock.all { it.isEnterPin } && !isPinAgain) {
                Log.e(
                    "aaaaaaaaaaaa",
                    "initListener:$mlistEnterLock  ${mlistEnterLock.size}  ${mlistEnterLock.all { it.isEnterPin }} "
                )
                for (item in mlistEnterLock) {
                    if (item.isEnterPin) {
                        listEnterLock.add(item)
                    }
                }
                isPinAgain = true
                binding.ivStep.isSelected = true
                binding.ivStep.setImageResource(R.drawable.ic_set_password2)
                binding.tvTitle.text = getString(R.string.confirm_your_password)
                reLoadEnterAgain()
                PatternLockView.CODE_PASSWORD_ERROR
            }


            if (isPinAgain && mlistEnterLock.all { it.isEnterPin }) {

                if (mlistEnterLock == listEnterLock) {
                    savePassLock()
                    PatternLockView.CODE_PASSWORD_ERROR
                } else {
                    if (PreferencesUtils.getBoolean(KeyLock.VIBRATION, true)) {
                        VibrationUtil.startVibration(this)
                    }
                    reLoadEnterAgain()
                    PatternLockView.CODE_PASSWORD_ERROR
                }
            }
            //
//            if (size > 3&&!isPinAgain) {
//                binding.btnCreate.visibility = View.VISIBLE
//            } else {
//                binding.btnCreate.visibility = View.INVISIBLE
//            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BackgroundManager.REQUEST_CODE_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    BackgroundManager.startServiceAndUsageData(this)
                }
            } else {
                BackgroundManager.startServiceAndUsageData(this)
            }
        }
    }

    private fun resetPattern() {
        binding.patternLock.postDelayed(binding.patternLock.binding.lockView::reset, 400)
    }

    private fun checkViewLock(isChangeView: Boolean = false) {
        binding.patternLock.binding.lockView.apply {
            setEnableVibrate(false)
            setPatternInvisible(PreferencesUtils.getBoolean(KeyLock.HIDE_DRAW_LINE, false))

        }
//        binding.btnCreate.visibility = View.INVISIBLE
        when ( styleViewLock ?: PreferencesUtils.getString(
            KeyLock.LOCK,
            getString(R.string.pattern)
        )) {
            getString(R.string.pattern) -> {
                // them view knock
                if (binding.pinLock.isShown) {
                    passCreate = null
//                    binding.btnCreate.visibility = View.INVISIBLE
                }
//                binding.textSelectLock.text = getString(R.string.pattern)
//                binding.icSelectLock.setImageResource(R.drawable.ic_pattern)My
                binding.patternLock.visibility = View.VISIBLE
                binding.pinLock.visibility = View.INVISIBLE
                binding.viewPinLock.visibility = View.INVISIBLE
                binding.tvContent.text = getString(R.string.connect_at_least_4_dots)
                binding.txtSwitch.text = getString(R.string.switch_to_pin)
//                binding.textLock.text = getString(R.string.content_pattern)
            }

            getString(R.string.pin) -> {
                if (binding.patternLock.isShown) {
                    listEnterLock.clear()
//                    binding.btnCreate.visibility = View.INVISIBLE
                }
//                binding.textSelectLock.text = getString(R.string.pin)
//                binding.icSelectLock.setImageResource(R.drawable.ic_number_lock)
                binding.pinLock.visibility = View.VISIBLE
                binding.viewPinLock.visibility = View.VISIBLE
                binding.patternLock.visibility = View.INVISIBLE
                binding.tvContent.text = getString(R.string.enter_pin)
                binding.txtSwitch.text = getString(R.string.switch_to_pattern)
//                binding.textLock.text = getString(R.string.content_create_pin)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (intent.getBooleanExtra(KeyLock.CHANGE_PASSWORD, false)) {
            launchActivity<SettingsActivity> { }
        }
        if (actionFromSetting){
            finish()
        }
    }

    private fun savePassLock() {
        when (styleViewLock ?: PreferencesUtils.getString(
            KeyLock.LOCK,
            getString(R.string.pattern)
        )) {
            getString(R.string.pattern) -> {
                passCreate?.list.let {
                    Hawk.put(KeyLock.KEY_PATTERN, it)
                }
                PreferencesUtils.putBoolean("PATTERN",true)
                createSuccessful()
            }

            else -> {
                isPinAgain = true
//                binding.btnCreate.visibility = View.INVISIBLE
//                binding.pinLock.clearEnterPin()
//                binding.textLock.text = getString(R.string.make_pass_again)
//                binding.textLock.setTextColor(ThemeUtils.getLineColorText())
//                reLoadEnterAgain()
                binding.pinLock.listenerEnterComplete = {
                    if (listEnterLock.containsAll(it)) {
                        Hawk.put(KeyLock.KEY_PIN, listEnterLock)
                        PreferencesUtils.putBoolean("PIN",true)
                        createSuccessful()
                    } else {
                        if (PreferencesUtils.getBoolean(KeyLock.VIBRATION, true)) {
                            VibrationUtil.startVibration(this)
                        }
//                        binding.textLock.setTextColor(resources.getColor(R.color.color_CCAC2D2D))
//                        binding.textLock.text = getString(R.string.pin_incorrect)
                    }
                    binding.pinLock.clearEnterPin()
                }
            }
        }
    }

    private fun createSuccessful() {
        if (!styleViewLock.isNullOrEmpty()) {
            PreferencesUtils.putString(KeyLock.LOCK, styleViewLock)
        }
        Toast.makeText(this, "Create successful !", Toast.LENGTH_SHORT).show()
        if (actionFromSetting){
            finish()
            return
        }
        if (intent.getBooleanExtra(KeyLock.CHANGE_PASSWORD, false)) {
//             ads
            EzAdControl.getInstance(this).showAds()
            launchActivity<MainLockActivity> { }
        } else {
            PreferencesUtils.putBoolean(KeyApp.CREATED_PASSWORD, true)

            if (PreferencesUtils.getString(
                    KeyQuestion.KEY_ANSWER, ""
                ).isNullOrEmpty()
            ) {
//                launchActivity<ConcernedApp> { }
                launchActivity<CreateQuestion> {
//                    putExtra(KeyQuestion.KEY_CHANGE_QUESTION, true)
                }
            } else {
                launchActivity<MainLockActivity> { }
            }
        }
    }
//    private var dialogQuestion: DialogQuestion? = null
//    private fun showDialogQuestion() {
//        KeyboardUtils.showSoftKeyboard(this)
//        dialogQuestion = DialogQuestion(this, R.style.StyleDialog)
//        dialogQuestion?.setCanceledOnTouchOutside(true)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//            dialogQuestion?.window!!.setType(
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            )
//        else
//            dialogQuestion?.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
//        dialogQuestion?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
//        dialogQuestion?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialogQuestion?.show()
//        dialogQuestion?.listenerClickOkay = {
//            KeyboardUtils.hideSoftKeyboardToggleSoft(this)
//            val answer = PreferencesUtils.getString(KeyQuestion.KEY_ANSWER, "")
//            if (it.equals(answer, true)) {
//                launchActivity<CreateQuestion> {
//                    putExtra(KeyQuestion.KEY_CHANGE_QUESTION, true)
//                }
//                dialogQuestion?.dismiss()
//            } else {
//                dialogQuestion?.binding?.txtError?.visibility = View.VISIBLE
//            }
//        }
//    }

    private fun reLoadEnterAgain() {
        binding.pinLock.listEnterPin.clear()
        for (item in listEnterLock) {
            if (item.isEnterPin) {
                binding.pinLock.listEnterPin.add(ItemEnterPin())
            }
        }
        binding.pinLock.adapterPin.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
    }


}