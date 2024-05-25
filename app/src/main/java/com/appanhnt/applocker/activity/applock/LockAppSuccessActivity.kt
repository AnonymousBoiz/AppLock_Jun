package com.appanhnt.applocker.activity.applock

import android.content.Intent
import android.view.LayoutInflater
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.ActivityLockFileSuccessBinding
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.appanhnt.applocker.activity.home.HomeActivity
import com.appanhnt.applocker.item.ItemAppLock
import com.lutech.ads.AdsManager


class LockAppSuccessActivity : BaseActivity<ActivityLockFileSuccessBinding>() {

    companion object{
        var data: ItemAppLock? = null
    }
    override fun initView() {
        AdsManager.loadNativeAds(this, binding.myTemplate, R.string.applock_native_home_id)
        setStatusBarHomeTransparent(this)
        binding.album.setPadding(0, getHeightStatusBar(), 0, 0)
    }

    override fun initData() {
        binding.tvContent.text =
            getString(R.string.lock) + " " + data?.name + " " + getString(R.string.txt_hide_app_success)
        binding.ivIcon.setImageDrawable(data?.resId)
    }

    override fun initListener() {
        binding.ivHome.setOnClickListener {
            launchActivity<HomeActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
        binding.icBackAlbum.setOnClickListener {
            finish()
        }
    }

    override fun viewBinding(): ActivityLockFileSuccessBinding {
        return ActivityLockFileSuccessBinding.inflate(LayoutInflater.from(this))
    }

}