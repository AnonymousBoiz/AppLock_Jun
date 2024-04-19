package com.appanhnt.applocker.activity

import android.view.LayoutInflater
import com.appanhnt.applocker.databinding.ActivitySplashBinding
import com.anhnt.baseproject.activity.BaseActivity

class SplashActivity0 : BaseActivity<ActivitySplashBinding>() {
    override fun initView() {

    }

    override fun initData() {
    }

    override fun initListener() {
    }

    override fun viewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

}