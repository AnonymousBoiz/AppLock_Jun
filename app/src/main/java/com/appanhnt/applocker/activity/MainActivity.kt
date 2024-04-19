package com.appanhnt.applocker.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.appanhnt.applocker.R
import com.appanhnt.applocker.activity.ConcernedApp
import com.appanhnt.applocker.activity.CreateLockActivity
import com.appanhnt.applocker.databinding.ActivityMainBinding
import com.appanhnt.applocker.utils.AppUtil
import com.appanhnt.applocker.viewmodel.AppLockViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.launchActivity
import com.appanhnt.applocker.ads.Utils
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity<ActivityMainBinding>() {
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
                    launchActivity<ConcernedApp> { }
                } else {
                    launchActivity<CreateLockActivity> { }
                }
                finish()
            }
        }
    }

    override fun viewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun initData() {

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}