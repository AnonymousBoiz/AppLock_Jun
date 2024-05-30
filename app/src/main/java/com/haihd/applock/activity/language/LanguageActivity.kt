package com.haihd.applock.activity.language

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.haihd.applock.adapter.LanguageAdapter
import com.haihd.applock.databinding.ActivityLanguageBinding
import com.haihd.applock.key.KeyApp
import com.haihd.applock.key.KeyLock
import com.haihd.applock.utils.NotificationLockUtils
import com.haihd.applock.viewmodel.LanguageViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.activity.locktype.LockTypeActivity
import com.haihd.applock.activity.intro.IntroActivity
import com.haihd.applock.activity.lock.LockActivity
import com.haihd.applock.activity.home.HomeActivity
import com.haihd.applock.service.LockService
import com.lutech.ads.AdsManager
import org.koin.android.ext.android.inject

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    private val viewModel by inject<LanguageViewModel>()
    private val adapter by lazy {
        LanguageAdapter(this, viewModel.languageList).apply {
            setCurrentItem(PreferencesUtils.getString(KEY_LANGUAGE, "en"))
        }
    }
    private val isChooseLanguage = PreferencesUtils.getBoolean(KeyApp.IS_CHOOSE_LANGUAGE, false)

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        AdsManager.loadNativeAds(this, binding.myTemplate, com.lutech.ads.R.string.applock_native_language_id)

        val type = intent.getStringExtra("SETTING")
        if (!isChooseLanguage) {
            binding.ivBack.visibility = View.GONE
        }
        setStatusBarHomeTransparent(this)
        setStatusBarHomeTransparent(this)
        binding.layoutTop.setPadding(0, getHeightStatusBar(), 0, 0)

//        event
        binding.ivDone.setOnClickListener {
            if (type == "LANGUAGE_SETTING") {
                PreferencesUtils.putString(KEY_LANGUAGE, adapter.getCurrentItem().code)
                setLocale(adapter.getCurrentItem().code)
                finishAffinity()
                launchActivity<HomeActivity> {

                }
            }else {
                PreferencesUtils.putString(KEY_LANGUAGE, adapter.getCurrentItem().code)
                setLocale(adapter.getCurrentItem().code)
                if (isChooseLanguage) {
                    launchActivity<LockActivity> {
                        putExtra(KeyApp.LOCK_MY_APP, true)
                        putExtra(NotificationLockUtils.DETAIL, LockService.showDetailNotify)
                        putExtra(
                            KeyLock.PKG_APP, "com.appanhnt.applocker"
                        )
                    }
                    finish()
                } else {
                    PreferencesUtils.putBoolean(KeyApp.IS_CHOOSE_LANGUAGE, true)
                    launchActivity<LockTypeActivity> { }
                    if (PreferencesUtils.getBoolean("is_skip_onboard")) {
                        finish()
                    } else {
                        launchActivity<IntroActivity> { }
                        finish()
                    }
                }
            }
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initListener() {
    }

    override fun viewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(LayoutInflater.from(this))
    }

    override fun initData() {
        binding.rcLanguage.layoutManager = LinearLayoutManager(this)
        binding.rcLanguage.adapter = adapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}