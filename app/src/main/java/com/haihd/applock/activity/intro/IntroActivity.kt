package com.haihd.applock.activity.intro

import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.haihd.applock.R
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.adapter.ViewPagerAddFragmentsAdapter
import com.haihd.applock.databinding.ActivityIntroBinding
import com.haihd.applock.fragment.OnBoardFragment
import com.lutech.ads.AdsManager


class IntroActivity : BaseActivity<ActivityIntroBinding>() {
    override fun initView() {
        AdsManager.loadNativeAds(this, binding.myTemplate, R.string.applock_native_home_id)

        PreferencesUtils.putBoolean("is_skip_onboard", true)
        initViewPager()

//        AdmobManager.getInstance().loadNative(
//            this,
//            BuildConfig.native_guide,
//            binding.frAd,
//            R.layout.custom_native_onboarding
//        )
//        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)

    }

    override fun initData() {
    }

    override fun initListener() {
    }

    override fun viewBinding(): ActivityIntroBinding {
        return ActivityIntroBinding.inflate(LayoutInflater.from(this))
    }


    private fun initViewPager() {
        binding.viewpagerOnboard.adapter =
            ViewPagerAddFragmentsAdapter(supportFragmentManager, lifecycle).apply {
                addFrag(OnBoardFragment(R.drawable.img_inside_1, R.string.text_on_boarding_title_1,R.string.text_on_boarding_body_1))
                addFrag(OnBoardFragment(R.drawable.img_inside_2, R.string.text_on_boarding_title_2,R.string.text_on_boarding_body_2))
                addFrag(OnBoardFragment(R.drawable.img_inside_3, R.string.text_on_boarding_title_3,R.string.text_on_boarding_body_3))
            }
        binding.viewpagerOnboard.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.indicatorView.selection = position
                if (position == (binding.viewpagerOnboard.adapter as ViewPagerAddFragmentsAdapter).itemCount - 1) {
                    binding.tvNext.text = getString(R.string.done)
                } else
                    binding.tvNext.text = getString(R.string.next)
            }
        })

        binding.tvNext.setOnClickListener {
            if (binding.viewpagerOnboard.currentItem == (binding.viewpagerOnboard.adapter as ViewPagerAddFragmentsAdapter).itemCount - 1) {
//                startActivity(Intent(this@OnBoardActivity, PermissionActivity::class.java))
                finish()
            } else {
                binding.viewpagerOnboard.currentItem++
            }
            when (binding.viewpagerOnboard.currentItem) {
//                0 -> EventLogger.getInstance()?.logEvent("click_guide_1")
//                1 -> EventLogger.getInstance()?.logEvent("click_guide_2")
//                2 -> EventLogger.getInstance()?.logEvent("click_guide_3")
            }
        }
    }
}