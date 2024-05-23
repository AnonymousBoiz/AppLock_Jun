package com.appanhnt.applocker.activity.permission

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import com.appanhnt.applocker.service.BackgroundManager
import com.appanhnt.applocker.service.LockService
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.launchActivity
import com.appanhnt.applocker.databinding.ActivityRequestPermissionBinding
import org.koin.core.component.KoinApiExtension

class RequestPermissionActivity : BaseActivity<ActivityRequestPermissionBinding>() {
    override fun initView() {
        setStatusBarHomeTransparent(this)
        // set up view
        setUpView()
    }

    @KoinApiExtension
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LockService.isCreate = true
    }

    override fun initData() {
        BackgroundManager.requestPermission(this, false)
    }

    @KoinApiExtension
    override fun initListener() {
        //overlay
        binding.switchStatusOverlay.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showDialogPermission(true)
            } else {
                binding.switchStatusOverlay.isChecked = false
            }
        }
        // usage access
        binding.switchUsageAccess.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDialogPermission(false)
            } else {
                binding.switchUsageAccess.isChecked = false
            }
        }
        binding.icBack.setOnClickListener {
            onBackPressed()
        }
        //
        binding.icBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun viewBinding(): ActivityRequestPermissionBinding {
        return ActivityRequestPermissionBinding.inflate(LayoutInflater.from(this))
    }

    @KoinApiExtension
    @RequiresApi(Build.VERSION_CODES.M)
    private fun overlayPermission() {
        LockService.isRequestPermission = true
        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        myIntent.data = Uri.parse("package:${packageName}")
        startActivity(myIntent)
    }

    @KoinApiExtension
    private fun showDialogPermission(isOverlay: Boolean) {
        if (isOverlay) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                overlayPermission()
        } else {
            BackgroundManager.openUsageStats(this)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            launchActivity<GuidePermissionActivity> { }
        }, 200)

        if (isOverlay) {
            binding.switchStatusOverlay.isChecked = false
        } else {
            binding.switchUsageAccess.isChecked = false
        }
    }

    override fun onRestart() {
        super.onRestart()
        setUpView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun setUpView() {
        // overlay
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            binding.apply {
                layoutOverlay.visibility = View.GONE
                imgBackgroundOverlay.visibility = View.GONE
            }
        } else if (Settings.canDrawOverlays(this)) {
            binding.apply {
                layoutOverlay.visibility = View.GONE
                imgBackgroundOverlay.visibility = View.GONE
            }
        } else {
            binding.switchStatusOverlay.isChecked = false
        }

        // usage access
        if (BackgroundManager.isAccessGranted(this)) {
            binding.apply {
                layoutStateUsageAccess.visibility = View.GONE
                imgBackgroundUsageAccess.visibility = View.GONE
            }
        } else {
            binding.switchUsageAccess.isChecked = false
        }
    }

}