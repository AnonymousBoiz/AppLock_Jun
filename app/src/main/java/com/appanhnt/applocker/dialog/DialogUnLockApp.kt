package com.appanhnt.applocker.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.LayoutDialogUnlockAppBinding
import com.appanhnt.applocker.item.ItemAppLock


class DialogUnLockApp(context: Context, style: Int, val data: ItemAppLock, val callback: Callback) :
    Dialog(context, style) {
    lateinit var binding: LayoutDialogUnlockAppBinding

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
        binding.tvUnlock.setOnClickListener {
            callback.unLock(data)
            dismiss()
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun initView() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding = LayoutDialogUnlockAppBinding.inflate(LayoutInflater.from(context))
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window?.setGravity(Gravity.BOTTOM);
        setContentView(binding.root)

        binding.tvUnLockQuestion.text = context.getString(R.string.are_you_sure_you_want_to_unlock) + " " + data.name + " ?"
        binding.ivIconApp.setImageDrawable(data.resId)
    }

    interface Callback{
        fun unLock(data: ItemAppLock)
    }
}