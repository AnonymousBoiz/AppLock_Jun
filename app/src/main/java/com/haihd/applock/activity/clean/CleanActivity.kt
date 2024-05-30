package com.haihd.applock.activity.clean

import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import com.haihd.applock.R
import com.haihd.applock.databinding.ActivityCleanBinding
import com.haihd.applock.viewmodel.CleanerViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt
import kotlin.random.Random


class CleanActivity : BaseActivity<ActivityCleanBinding>() {
    private val viewModelCleaner by inject<CleanerViewModel>()
    private val handler = Handler(Looper.getMainLooper())
    override fun initView() {
        setStatusBarHomeTransparent(this)
        binding.clean.setPadding(0, getHeightStatusBar(), 0, 0)
    }

    override fun initData() {
        var timing = false
        viewModelCleaner.sumStr.observe(this) {
            if (!it.isNullOrEmpty()&&!timing) {
                timing = true
                binding.apply {
                    val set = ConstraintSet()
                    set.clone(binding.layoutClean)
                    set.constrainPercentWidth(R.id.animationViewClean, 1F)
                    set.applyTo(binding.layoutClean)
                    binding.animationViewClean.setPadding(0, binding.clean.height, 0, 0)
                    binding.layoutInfo.visibility = View.INVISIBLE
                    btnDone.visibility = View.VISIBLE
                    btnDone.animate().alpha(1F).setDuration(200).start()
                    animationViewClean.setAnimation(R.raw.success)
                    animationViewClean.loop(false)
                    animationViewClean.playAnimation()
                    txtClean.text = getString(R.string.freed_up_memory)
                    txtNumber.visibility = View.VISIBLE
                    progressbar.visibility = View.GONE
                    txtNumber.text = it
                    binding.layoutInfo.visibility = View.VISIBLE
                    startValueAnimator(binding.layoutInfo)
                }
                // ads
                showAds(null)
                //
            }
        }
        viewModelCleaner.let {
            it.cleanFile(this)
            it.cleanData(this)
            it.cleanOptimizes(this)
        }

        val run = Runnable {
            val rate = Random.nextDouble(2.0, 10.0)
            val n =
                (((rate * 100).roundToInt() / 100.0) / (((rate * 100).roundToInt() / 100.0).toInt()).toDouble())
            val m = if (n!=1.0)
                (rate * 100).roundToInt() / 100.0 else (n + Random.nextDouble(1.0, 99.0))
            viewModelCleaner.sumStr.value = "$m Mb"
        }
        handler.postDelayed(run, 5000)
    }

    override fun initListener() {
        binding.icBackClean.setOnClickListener {
            onBackPressed()
        }
        binding.btnDone.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    override fun viewBinding(): ActivityCleanBinding {
        return ActivityCleanBinding.inflate(LayoutInflater.from(this))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModelCleaner.sumStr.value = null
    }


    private fun startValueAnimator(view: View) {
        val valueAnimator = ValueAnimator.ofFloat(-view.height.toFloat(), 0f)
        valueAnimator.apply {
            duration = 500
            interpolator = DecelerateInterpolator()
            addUpdateListener { valueAnimator ->
                view.translationY = valueAnimator.animatedValue as Float
            }
            start()
        }
    }

}