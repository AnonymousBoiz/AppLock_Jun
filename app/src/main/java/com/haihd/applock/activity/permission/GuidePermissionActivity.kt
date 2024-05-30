package com.haihd.applock.activity.permission

import android.view.LayoutInflater
import com.haihd.applock.databinding.ActivityTransBinding
import com.anhnt.baseproject.activity.BaseActivity

class GuidePermissionActivity : BaseActivity<ActivityTransBinding>() {
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