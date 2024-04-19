package com.appanhnt.applocker.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.LayoutPatternLockBinding
import com.appanhnt.applocker.key.KeyLock
import com.appanhnt.applocker.utils.ThemeUtils
import com.anhnt.baseproject.utils.PreferencesUtils

class ViewPatternLockWindow(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    lateinit var binding: LayoutPatternLockBinding

    init {
        initView()
        initListener()
    }

    private fun initListener() {
//
    }

    private fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_pattern_lock, this, true)
        binding = LayoutPatternLockBinding.bind(view)
        // theme
        val dotNormal = ThemeUtils.themeDotPatternNormal(context)
        val dotHighlight = ThemeUtils.themeDotPatternHighlight(context)
        val dotError = ThemeUtils.themeDotPatternError(context)
        binding.lockView.apply {
            setEnableVibrate(PreferencesUtils.getBoolean(KeyLock.VIBRATION, true))
            setPatternInvisible(PreferencesUtils.getBoolean(KeyLock.HIDE_DRAW_LINE, false))
            setNodeHighlightSrc(dotHighlight)
            setNodeSrc(dotNormal)
            setNodeErrorSrc(dotError)
            setLineColor(ThemeUtils.getLineColor())
            setLineCorrectColor(ThemeUtils.getLineColor())
            setSize(3)
        }
    }
}