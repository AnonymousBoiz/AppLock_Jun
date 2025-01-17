package com.appanhnt.applocker.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.ActivityResultSuccessBinding
import com.appanhnt.applocker.databinding.ActivityResultThemeSuccessBinding
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.appanhnt.applocker.item.ItemAppLock
import com.appanhnt.applocker.key.KEY_COUNT
import com.appanhnt.applocker.key.KEY_DATA


class ResultSuccessThemeActivity : BaseActivity<ActivityResultThemeSuccessBinding>() {

    companion object{
        var drawable: Drawable? = null
    }
    override fun initView() {
        setStatusBarHomeTransparent(this)
        binding.album.setPadding(0, getHeightStatusBar(), 0, 0)
    }

    override fun initData() {
//        binding.tvContent.text =
//            getString(R.string.lock) + " " + data?.name + " " + getString(R.string.txt_hide_app_success)
        binding.ivIcon.setImageDrawable(drawable)
    }

    override fun initListener() {
        binding.ivHome.setOnClickListener {
            launchActivity<MainLockActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
        binding.icBackAlbum.setOnClickListener {
            onBackPressed()
        }
    }

    override fun viewBinding(): ActivityResultThemeSuccessBinding {
        return ActivityResultThemeSuccessBinding.inflate(LayoutInflater.from(this))
    }

}