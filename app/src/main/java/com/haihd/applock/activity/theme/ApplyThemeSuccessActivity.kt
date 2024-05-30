package com.haihd.applock.activity.theme

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import com.haihd.applock.databinding.ActivityApplyThemeSuccessBinding
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.haihd.applock.R
import com.haihd.applock.activity.home.HomeActivity
import com.lutech.ads.AdsManager


class ApplyThemeSuccessActivity : BaseActivity<ActivityApplyThemeSuccessBinding>() {

    companion object{
        var drawable: Drawable? = null
    }
    override fun initView() {
        setStatusBarHomeTransparent(this)
        AdsManager.loadNativeAds(this, binding.myTemplate, R.string.applock_native_home_id)
        binding.album.setPadding(0, getHeightStatusBar(), 0, 0)
    }

    override fun initData() {
//        binding.tvContent.text =
//            getString(R.string.lock) + " " + data?.name + " " + getString(R.string.txt_hide_app_success)
        binding.ivIcon.setImageDrawable(drawable)
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

    override fun viewBinding(): ActivityApplyThemeSuccessBinding {
        return ActivityApplyThemeSuccessBinding.inflate(LayoutInflater.from(this))
    }

}