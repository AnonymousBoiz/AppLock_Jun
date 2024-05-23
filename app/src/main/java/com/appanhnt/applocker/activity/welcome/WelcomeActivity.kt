package com.appanhnt.applocker.activity.welcome

import android.annotation.SuppressLint
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.appanhnt.applocker.R
import com.appanhnt.applocker.activity.concern.ConcernedAppActivity
import com.appanhnt.applocker.activity.locktype.LockTypeActivity
import com.appanhnt.applocker.utils.AppUtil
import com.appanhnt.applocker.viewmodel.AppLockViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.launchActivity
import com.appanhnt.applocker.databinding.ActivityWelcomeBinding
import org.koin.android.ext.android.inject

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    private val viewModel by inject<AppLockViewModel>()

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView() {
        Glide.with(this).load(getDrawable(R.color.background)).into(binding.bg)
        //
        setStatusBarHomeTransparent(this)
    }

    override fun initListener() {
        viewModel.listAppLiveData.observe(this) {
            var isConcerned = false
            for (item in it) {
                if (AppUtil.listPkgConcernedApp.contains(item.packageName)) {
                    isConcerned = true
                    break
                }
            }
            binding.btnStart.setOnClickListener {
                if (isConcerned) {
                    launchActivity<ConcernedAppActivity> { }
                } else {
                    launchActivity<LockTypeActivity> { }
                }
                finish()
            }
        }
    }

    override fun viewBinding(): ActivityWelcomeBinding {
        return ActivityWelcomeBinding.inflate(LayoutInflater.from(this))
    }

    override fun initData() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}