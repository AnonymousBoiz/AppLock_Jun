package com.haihd.applock.dialog

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import com.haihd.applock.databinding.DialogRateAppBinding
import com.haihd.applock.adapter.BaseDialog
import com.haihd.applock.utils.hide
import com.haihd.applock.utils.show
import com.ymb.ratingbar_lib.RatingBar


class DialogRateApp(context: Context, private val callback: Callback) :
    BaseDialog<DialogRateAppBinding>(context, DialogRateAppBinding::inflate) {
    private var handler: Handler? = null
    private var rd: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(
            Resources.getSystem().displayMetrics.widthPixels,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addEvent()
    }

    private fun addEvent() {
        binding.tvSubmit.setOnClickListener {
            callback.onSubmit(binding.edtContent.text.toString())
            dismiss()
        }
        binding.rating.setOnRatingChangedListener(object : RatingBar.OnRatingChangedListener {
            override fun onRatingChange(v: Float, v1: Float) {
                if (handler != null && rd != null) {
                    handler!!.removeCallbacks(rd!!)
                }
                handler = Handler(Looper.getMainLooper())
                rd = Runnable {
                    if (v1 < 4.0) {
                        binding.edtContent.show()
                        binding.tvSubmit.show()
                        binding.lnLater.hide()
                        binding.ivRate5s.hide()
                        binding.ivGuideNavigation.hide()
                        binding.tvBestForUs.hide()
                        return@Runnable
                    }
                    dismiss()
                    callback.onRate(v1.toInt())
                }
                handler!!.postDelayed(rd!!, 200)
            }
        })

        binding.lnLater.setOnClickListener {
            dismiss()
        }
    }

    interface Callback {
        fun onSubmit(data: String)
        fun onRate(starCount: Int)

    }
}