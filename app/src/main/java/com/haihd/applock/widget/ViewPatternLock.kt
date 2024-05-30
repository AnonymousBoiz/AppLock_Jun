package com.haihd.applock.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout

import com.haihd.applock.R
import com.haihd.applock.databinding.LayoutPatternLockBinding
import com.haihd.applock.key.KeyLock
import com.haihd.applock.utils.ThemeUtils
import com.anhnt.baseproject.utils.PreferencesUtils

class ViewPatternLock(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    lateinit var binding: LayoutPatternLockBinding

    init {
        initView()
        initListener()
    }

    private fun initListener() {
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
            setLineWidth(50)
            setNodeErrorSrc(dotError)
            setLineColor(ThemeUtils.getLineColor())
            setLineCorrectColor(ThemeUtils.getLineColor())
            setSize(3)
        }
    }
}