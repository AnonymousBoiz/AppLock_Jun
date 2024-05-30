package com.haihd.applock.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.haihd.applock.databinding.ItemDialogRelockSettingBinding
import com.haihd.applock.key.KeyLock
import com.anhnt.baseproject.utils.PreferencesUtils

class DialogRelockSettings(val context: Context, val onListerChooseAfterScreen: (type: Boolean) -> Unit): PopupWindow() {
    init {
        setUpPopupmenu()
    }
    private fun setUpPopupmenu() {
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        elevation = 10f
        val binding = ItemDialogRelockSettingBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root
        initView(binding)
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true
    }
    private fun initView(binding: ItemDialogRelockSettingBinding) {
        val type = PreferencesUtils.getBoolean(KeyLock.AFTER_SCREEN_OF,false)
        if (type){
            afterScreenOfChoose(binding)
        }else{
            afterExitChoose(binding)
        }
        binding.rlAfterExit.setOnClickListener {
            afterExitChoose(binding)
            this.dismiss()
            onListerChooseAfterScreen(false)
        }
        binding.rlAfterScreenOf.setOnClickListener {
            afterScreenOfChoose(binding)
            this.dismiss()
            onListerChooseAfterScreen(true)
        }
    }

    private fun afterExitChoose(binding: ItemDialogRelockSettingBinding){
        binding.ivPinDone.visibility = View.GONE
        binding.ivPatternDone.visibility = View.VISIBLE
        binding.txtPin.setTextColor(Color.parseColor("#80FFFFFF"))
        binding.txtPattern.setTextColor(Color.parseColor("#FFFFFF"))
    }
    private fun afterScreenOfChoose(binding: ItemDialogRelockSettingBinding){
        binding.ivPinDone.visibility = View.VISIBLE
        binding.ivPatternDone.visibility = View.GONE
        binding.txtPin.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtPattern.setTextColor(Color.parseColor("#80FFFFFF"))
    }

}

