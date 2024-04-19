package com.appanhnt.applocker.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.appanhnt.applocker.databinding.LayoutDialogPermissionBinding
import com.appanhnt.applocker.service.BackgroundManager


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

        if (BackgroundManager.isIgnoringBatteryOptimizations(context)) {
            grantedIgnoringBatteryOptimize()
        }
        if (BackgroundManager.isAccessGranted(context)) {
            grantedUsageAccess()
        }
        if (Settings.canDrawOverlays(context)) {
            grantedOverLaysPms()
        }
    }

    fun grantedOverLaysPms() {
        binding.btnSwitchDisplayOverApp.isChecked = true
        if (BackgroundManager.checkPermission(context)) {
            dismiss()
        }
    }

    fun grantedIgnoringBatteryOptimize() {
        binding.btnSwitchOptimize.isChecked = true
        if (BackgroundManager.checkPermission(context)) {
            dismiss()
        }
    }

    fun grantedUsageAccess() {
        binding.btnSwitchUsageAccess.isChecked = true
        if (BackgroundManager.checkPermission(context)) {
            dismiss()
        }
    }

    interface Callback {
        fun onGoUsageAccess()
        fun onGoDisplayOverApp()
        fun onGoProtectApp()
        fun onGoSettings()
    }

}