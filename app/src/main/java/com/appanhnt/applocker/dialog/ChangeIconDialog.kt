package com.appanhnt.applocker.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.appanhnt.applocker.databinding.LayoutDialogChangeIconAppBinding


class ChangeIconDialog(context: Context, style: Int, val icon: Int, val callback: Callback) :
    Dialog(context, style) {
    lateinit var binding: LayoutDialogChangeIconAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        initListener()
    }

    private fun initData() {
        binding.ivIconApp.setImageResource(icon)
    }

    override fun show() {
        super.show()
    }

    private fun initListener() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnRestart.setOnClickListener {
            callback.onRestart()
            dismiss()
        }
    }

    private fun initView() {
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        binding = LayoutDialogChangeIconAppBinding.inflate(LayoutInflater.from(context))
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.setGravity(Gravity.BOTTOM);
        setContentView(binding.root)
    }

    interface Callback {
        fun onRestart()
    }
}