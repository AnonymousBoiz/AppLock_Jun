package com.haihd.applock.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.LinearLayout
import com.haihd.applock.databinding.LayoutDialogFingerprintBinding

class DialogFingerprint(
    context: Context,
    var binding: LayoutDialogFingerprintBinding,
    val onListenerFinger: () -> Unit
) :
    AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setCancelable(false)
        initData()
        initView()
        initListener()
    }

    private fun initData() {
        window?.apply {
            val windowParams = attributes
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            windowParams.dimAmount = 0.7f
            attributes = windowParams
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun initView() {
        setContentView(binding.root)
    }

    private fun initListener() {
        binding.btnCandle.setOnClickListener {

            dismiss()
        }
        binding.icFinger.setOnClickListener {
            onListenerFinger()
        }
    }
}