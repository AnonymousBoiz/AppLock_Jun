package com.appanhnt.applocker.activity

import android.text.TextUtils
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.ActivityCreateQuestionBinding
import com.appanhnt.applocker.key.KeyQuestion
import com.appanhnt.applocker.key.KeyTheme
import com.appanhnt.applocker.utils.ThemeUtils
import com.appanhnt.applocker.viewmodel.ThemeViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.appanhnt.applocker.activity.MainLockActivity
import com.google.android.gms.ads.ez.EzAdControl
import org.koin.android.ext.android.inject


class CreateQuestion : BaseActivity<ActivityCreateQuestionBinding>() {
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
                launchActivity<ConcernedApp> { }
            }
        }
        // back
        binding.icBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkViewQuestion() {
        binding.txtQuestion.text = PreferencesUtils.getString(
            KeyQuestion.KEY_QUESTION, getString(R.string.recovery_code)
        )
    }

    override fun viewBinding(): ActivityCreateQuestionBinding {
        return ActivityCreateQuestionBinding.inflate(LayoutInflater.from(this))
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra(KeyQuestion.KEY_CHANGE_QUESTION, false)) {
            // ads
            EzAdControl.getInstance(this).showAds()
            super.onBackPressed()
        } else {
            finishAffinity()
        }
    }

}