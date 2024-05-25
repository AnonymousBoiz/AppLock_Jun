package com.lutech.ads.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.lutech.ads.R
import kotlinx.android.synthetic.main.layout_loading_ads.view.*

class LoadingAdsDialog : DialogFragment() {

    companion object {
        const val TAG = "LoadingAdsDialog"

        fun newInstance(): LoadingAdsDialog {
            return LoadingAdsDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_loading_ads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        handleEvents(view)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (dialog?.isShowing == true || this.isAdded) return
        super.show(manager, tag)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setGravity(Gravity.TOP)
    }

    override fun dismiss() {
        if (dialog?.isShowing == true){
            super.dismiss()
        }
    }

    private fun initView(view: View) {
        view.layoutLoadingAds.visibility = View.VISIBLE
    }

    private fun handleEvents(view: View) {

    }
}