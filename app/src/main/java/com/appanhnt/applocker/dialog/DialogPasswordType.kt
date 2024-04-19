package com.appanhnt.applocker.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.ItemMenuCustomLayoutBinding
import com.appanhnt.applocker.key.KeyLock
import com.anhnt.baseproject.utils.PreferencesUtils

class DialogPasswordType(val context: Context, val onListerChoose: (type: String) -> Unit): PopupWindow() {


    init {
        setUpPopupmenu()
    }
    private fun setUpPopupmenu() {
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        elevation = 10f
        val binding = ItemMenuCustomLayoutBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root
        initView(binding)
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true
    }
    private fun initView(binding: ItemMenuCustomLayoutBinding) {
        val type = PreferencesUtils.getString(KeyLock.LOCK, context.getString(R.string.pattern))
        if (type == "PIN"){
            pinChoose(binding)
        }else{
            patternChoose(binding)
        }
        binding.rlPattern.setOnClickListener {
            patternChoose(binding)
            this.dismiss()
//            PreferencesUtils.putString(KeyLock.LOCK, context.getString(R.string.pattern))
            onListerChoose(context.getString(R.string.pattern))
        }
        binding.rlPin.setOnClickListener {
            pinChoose(binding)
            this.dismiss()
//            PreferencesUtils.putString(KeyLock.LOCK, context.getString(R.string.pin))
            onListerChoose(context.getString(R.string.pin))
        }
    }

    private fun patternChoose(binding: ItemMenuCustomLayoutBinding){
        binding.ivPinDone.visibility = View.GONE
        binding.ivPatternDone.visibility = View.VISIBLE
        binding.txtPin.setTextColor(Color.parseColor("#80FFFFFF"))
        binding.txtPattern.setTextColor(Color.parseColor("#FFFFFF"))
    }
    private fun pinChoose(binding: ItemMenuCustomLayoutBinding){
        binding.ivPinDone.visibility = View.VISIBLE
        binding.ivPatternDone.visibility = View.GONE
        binding.txtPin.setTextColor(Color.parseColor("#FFFFFF"))
        binding.txtPattern.setTextColor(Color.parseColor("#80FFFFFF"))
    }

}

