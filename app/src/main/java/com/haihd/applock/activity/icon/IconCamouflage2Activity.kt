package com.haihd.applock.activity.icon

import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.haihd.applock.R
import com.haihd.applock.adapter.IconCamouflage2Adapter
import com.haihd.applock.databinding.ActivityIconCamouflageBinding
import com.haihd.applock.dialog.ChangeIconDialog
import com.haihd.applock.item.ItemIconCamouflage
import com.haihd.applock.viewmodel.IconCamouflageViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.key.KeyApp
import com.bumptech.glide.Glide
import com.lutech.ads.AdsManager
import org.koin.android.ext.android.inject

class IconCamouflage2Activity : BaseActivity<ActivityIconCamouflageBinding>() {
    private val viewModel by inject<IconCamouflageViewModel>()
    private var currentItem : ItemIconCamouflage? = null
    private val adapter by lazy {
        IconCamouflage2Adapter(
            this,
            viewModel.iconGroupCamouflageList,
            object : IconCamouflage2Adapter.Callback {
                override fun onSelectedIcon(icon: ItemIconCamouflage) {
                    binding.btnSave.setBackgroundResource(R.drawable.bg_btn_save)
                    currentItem = icon
                    Glide.with(this@IconCamouflage2Activity).load(icon.icon)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.loading)
                        .into(binding.ivTargetIcon)
                }

            })
    }

    override fun initView() {
        setStatusBarHomeTransparent(this)
        AdsManager.loadNativeAds(this, binding.myTemplate, R.string.applock_native_home_id)

        binding.top.setPadding(0, getHeightStatusBar(), 0, 0)

        binding.rvIconApp.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvIconApp.adapter = adapter
        currentItem?.let { adapter.setCurrentIcon(it) }

        val iconSave = PreferencesUtils.getInteger(KeyApp.KEY_ICON,-1);
        if (iconSave != -1) {
            binding.ivCurrentIcon.setImageResource(viewModel.iconList[iconSave].icon)
        }

//        event
        binding.btnContinue.setOnClickListener {
            finish()
        }
        binding.btnSave.setOnClickListener {
            if (currentItem != null) {
                ChangeIconDialog(
                    this,
                    R.style.full_screen_dialog,
                    currentItem!!.icon,
                    object : ChangeIconDialog.Callback {
                        override fun onRestart() {
                            viewModel.changeIconApp(this@IconCamouflage2Activity, currentItem!!)
                        }
                    }).show()
            }
        }
    }

    override fun initData() {

    }

    override fun initListener() {

    }

    override fun viewBinding(): ActivityIconCamouflageBinding {
        return ActivityIconCamouflageBinding.inflate(LayoutInflater.from(this))
    }

}