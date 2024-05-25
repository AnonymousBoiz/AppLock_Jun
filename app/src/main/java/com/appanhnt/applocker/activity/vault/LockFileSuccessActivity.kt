package com.appanhnt.applocker.activity.vault

import android.content.Intent
import android.view.LayoutInflater
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.ActivityLockFileSuccessBinding
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.appanhnt.applocker.activity.home.HomeActivity
import com.appanhnt.applocker.key.KEY_COUNT
import com.lutech.ads.AdsManager


class LockFileSuccessActivity : BaseActivity<ActivityLockFileSuccessBinding>() {
    override fun initView() {
        setStatusBarHomeTransparent(this)
        AdsManager.loadNativeAds(this, binding.myTemplate, R.string.applock_native_home_id)

        binding.album.setPadding(0, getHeightStatusBar(), 0, 0)
    }

    override fun initData() {
        binding.tvContent.text = "${intent.getIntExtra(KEY_COUNT, 0)} " + getString(R.string.txt_hide_success)
    }

    override fun initListener() {
        binding.ivHome.setOnClickListener {
            launchActivity<HomeActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
        binding.icBackAlbum.setOnClickListener {
            onBackPressed()
        }
    }

    override fun viewBinding(): ActivityLockFileSuccessBinding {
        return ActivityLockFileSuccessBinding.inflate(LayoutInflater.from(this))
    }

}