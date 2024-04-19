package com.appanhnt.applocker.activity

import android.view.LayoutInflater
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.ActivityTransBinding
import com.anhnt.baseproject.activity.BaseActivity

class Trans : BaseActivity<ActivityTransBinding>() {
    override fun initView() {

    }

    override fun initData() {
    }

    override fun initListener() {
        binding.btnGotoSet.setOnClickListener {
            finish()
        }
    }

    override fun viewBinding(): ActivityTransBinding {
        return ActivityTransBinding.inflate(LayoutInflater.from(this))
    }

}