package com.appanhnt.applocker.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.appanhnt.applocker.adapter.PagerRestoreAdapter
import com.appanhnt.applocker.databinding.FragmentRestoreBinding
import com.anhnt.baseproject.fragment.BaseFragment

class FragmentRestore(var position: Int) : BaseFragment<FragmentRestoreBinding>() {

    override fun initView() {
        fragmentManager?.let {
            val adapter = PagerRestoreAdapter(childFragmentManager, requireContext())
            binding.viewPager.adapter = adapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
            binding.viewPager.setCurrentItem(position, true)
            binding.viewPager.offscreenPageLimit = 3
        }
    }

    override fun initData() {
    }

    override fun initListener() {
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRestoreBinding {
        return FragmentRestoreBinding.inflate(inflater, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}