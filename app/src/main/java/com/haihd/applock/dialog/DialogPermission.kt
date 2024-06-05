package com.haihd.applock.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.view.isVisible
import com.haihd.applock.databinding.LayoutDialogPermissionBinding
import com.haihd.applock.service.BackgroundManager
import com.haihd.applock.service.LockService
import com.lutech.ads.extensions.isNotificationPermissionGranted


class DialogPermission(context: Context, style: Int, val callback: Callback) :
    Dialog(context, style) {
    lateinit var binding: LayoutDialogPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        initListener()
    }

    private fun initData() {

    }

    override fun show() {
        super.show()
    }

    private fun initListener() {
        binding.btnUsageAccess.setOnClickListener {
            callback.onGoUsageAccess()
        }
        binding.btnDisplayOverApp.setOnClickListener {
            callback.onGoDisplayOverApp()
        }
        binding.btnOptimize.setOnClickListener {
            callback.onGoProtectApp()
        }
        binding.btnGotoSet.setOnClickListener {
            callback.onGoSettings()
        }
        binding.btnNotification.setOnClickListener {
            callback.onRequestNotification()
        }
    }

    private fun initView() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding = LayoutDialogPermissionBinding.inflate(LayoutInflater.from(context))
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window?.setGravity(Gravity.BOTTOM);
        setContentView(binding.root)

        binding.btnNotification.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

        if (BackgroundManager.isIgnoringBatteryOptimizations(context)) {
            grantedIgnoringBatteryOptimize()
        }
        if (BackgroundManager.isAccessGranted(context)) {
            grantedUsageAccess()
        }
        if (Settings.canDrawOverlays(context)) {
            grantedOverLaysPms()
        }

        if (context.isNotificationPermissionGranted()) {
            grantedNotification()
        }
    }

    fun grantedOverLaysPms() {
        binding.btnSwitchDisplayOverApp.isChecked = true
        dismissIfGrantedAll()
    }

    private fun dismissIfGrantedAll(){
        if (BackgroundManager.checkPermission(context)) {
            if (!BackgroundManager.isServiceRunning(LockService::class.java, context)) {
                BackgroundManager.startService(context)
            }
            dismiss()
        }
    }

    fun grantedNotification() {
        binding.btnSwitchNotification.isChecked = true
        dismissIfGrantedAll()
    }

    fun grantedIgnoringBatteryOptimize() {
        binding.btnSwitchOptimize.isChecked = true
        dismissIfGrantedAll()
    }

    fun grantedUsageAccess() {
        binding.btnSwitchUsageAccess.isChecked = true
        dismissIfGrantedAll()
    }

    interface Callback {
        fun onGoUsageAccess()
        fun onGoDisplayOverApp()
        fun onGoProtectApp()
        fun onGoSettings()
        fun onRequestNotification()
    }

}