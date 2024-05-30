package com.haihd.applock.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.haihd.applock.databinding.LayoutDialogReadMediaPermissionBinding


class DialogReadMediaPermission(context: Context, style: Int, val callback: Callback) :
    Dialog(context, style) {
    lateinit var binding: LayoutDialogReadMediaPermissionBinding

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
        binding.btnGotoSet.setOnClickListener {
            callback.onGoSettings()
        }
    }

    private fun initView() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding = LayoutDialogReadMediaPermissionBinding.inflate(LayoutInflater.from(context))
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window?.setGravity(Gravity.BOTTOM);
        setContentView(binding.root)
    }

    interface Callback{
        fun onGoSettings()
    }
}