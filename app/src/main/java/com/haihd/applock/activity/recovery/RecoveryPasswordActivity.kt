package com.haihd.applock.activity.recovery

import android.text.TextUtils
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.haihd.applock.R
import com.haihd.applock.key.KeyQuestion
import com.haihd.applock.key.KeyTheme
import com.haihd.applock.utils.ThemeUtils
import com.haihd.applock.viewmodel.ThemeViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.activity.concern.ConcernedAppActivity
import com.haihd.applock.databinding.ActivityRecoveryPasswordBinding

import org.koin.android.ext.android.inject


class RecoveryPasswordActivity : BaseActivity<ActivityRecoveryPasswordBinding>() {
    private val viewThemeModel by inject<ThemeViewModel>()

    override fun initView() {
        setStatusBarHomeTransparent(this)
        checkViewQuestion()
        binding.layoutCreateQuestion.setPadding(0, getHeightStatusBar(), 0, 0)
    }

    override fun initData() {
        // theme set up background
        if (PreferencesUtils.getString(
                KeyTheme.KEY_BACKGROUND,
                "default"
            )!=KeyTheme.BG_CUSTOM
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
        // save
        binding.btnSave.setOnClickListener {
            if (!TextUtils.isEmpty(binding.edtAnswer.text?.trim())) {
                PreferencesUtils.putString(
                    KeyQuestion.KEY_ANSWER, binding.edtAnswer.text.toString()
                )
//                launchActivity<MainLockActivity> { }
                launchActivity<ConcernedAppActivity> { }
            }
        }
        // back
        binding.icBack.setOnClickListener {
            showAds(isBackScreen = true)
        }
    }

    private fun checkViewQuestion() {
        binding.txtQuestion.text = PreferencesUtils.getString(
            KeyQuestion.KEY_QUESTION, getString(R.string.recovery_code)
        )
    }

    override fun viewBinding(): ActivityRecoveryPasswordBinding {
        return ActivityRecoveryPasswordBinding.inflate(LayoutInflater.from(this))
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra(KeyQuestion.KEY_CHANGE_QUESTION, false)) {
            // ads
            super.onBackPressed()
        } else {
            finishAffinity()
        }
    }

}