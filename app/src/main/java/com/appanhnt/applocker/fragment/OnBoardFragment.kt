package com.appanhnt.applocker.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.FragmentOnBoardBinding
import com.anhnt.baseproject.fragment.BaseFragment


class OnBoardFragment(
    var idImage: Int = R.drawable.img_inside_1,
    var idTextTitle: Int = R.string.text_on_boarding_title_1,
    var idTextBody: Int = R.string.text_on_boarding_body_1
) : BaseFragment<FragmentOnBoardBinding>() {


    override fun initView() {
        binding.tvInsideTitle.setSelected(true)
        binding.tvInsideBody.setSelected(true)

        binding.imgInside.setImageResource(idImage)
        binding.tvInsideTitle.text = getString(idTextTitle)
        binding.tvInsideBody.text = getString(idTextBody)
    }

    override fun initData() {
    }

    override fun initListener() {
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOnBoardBinding {
        return FragmentOnBoardBinding.inflate(inflater, container, false)
    }
}