package com.haihd.applock.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.GridLayoutManager
import com.haihd.applock.R
import com.haihd.applock.activity.theme.ThemeActivity
import com.haihd.applock.adapter.LockThemeAdapter
import com.haihd.applock.databinding.FragmentLockThemeBinding
import com.haihd.applock.dialog.DialogShowThemeLock
import com.haihd.applock.item.ItemLockTheme
import com.haihd.applock.key.KeyTheme
import com.haihd.applock.utils.RecycleViewUtils
import com.haihd.applock.viewmodel.ThemeViewModel
import com.anhnt.baseproject.fragment.BaseFragment
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.activity.theme.ApplyThemeSuccessActivity

import org.koin.android.ext.android.inject

class LockThemeFragment : BaseFragment<FragmentLockThemeBinding>() {
    private val listLockTheme = mutableListOf<ItemLockTheme>()
    private lateinit var adapterLockTheme: LockThemeAdapter
    private var dialogShow: DialogShowThemeLock? = null
    private val viewModel by inject<ThemeViewModel>()
    override fun initView() {
        adapterLockTheme = LockThemeAdapter(requireContext(), listLockTheme)
        binding.rclLockTheme.adapter = adapterLockTheme
        (binding.rclLockTheme.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position==3) {
                        3
                    } else {
                        1
                    }
                }

            }
        // remove anim rcl
        RecycleViewUtils.clearAnimation(binding.rclLockTheme)
        //load ads
        loadAds()
    }

    private fun loadAds() {

//        adapterLockTheme.adsView = nativeAd
//        adapterLockTheme.addAds(
//            ItemLockTheme(
//                "", null, null,
//                isSelected = false,
//                isAds = true
//            ), 3
//        )
    }

    override fun initData() {
        viewModel.listTheme.observe(this) {
            if (!it.isNullOrEmpty()) {
                listLockTheme.clear()
                listLockTheme.addAll(it)
                adapterLockTheme.notifyDataSetChanged()
            } else {
                viewModel.getListTheme(requireContext())
            }
        }
//        Handler().postDelayed({
//            if (listLockTheme.isNullOrEmpty()) {
//                viewModel.getListTheme(requireContext())
//            }
//        }, 3000)
    }

    override fun initListener() {
        adapterLockTheme.listenerOnClick = {
            val data = listLockTheme[it]
            showDialogShow(data.themPattern, data.themPin, it)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLockThemeBinding {
        return FragmentLockThemeBinding.inflate(inflater, container, false)
    }

    private fun showDialogShow(pattern: Drawable?, pin: Drawable?, position: Int) {
        dialogShow = DialogShowThemeLock(requireContext(), pattern, pin, R.style.MyDialogTheme)
        dialogShow?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogShow?.show()
        Log.e("CCCCCCCCCCCCC", "checkSelected:9999 &=$pattern")

        dialogShow?.listenerApply = {
            for (item in listLockTheme) {
                if (item.folder==listLockTheme[position].folder) {
                    PreferencesUtils.putString(KeyTheme.KEY_THEME, item.folder)
                    PreferencesUtils.putString(
                        KeyTheme.KEY_BACKGROUND, item.folder)
                    Log.e("CCCCCCCCCCCCC", "checkSelected: &=${item.folder}")

                    item.isSelected = true
                    adapterLockTheme.notifyItemChanged(listLockTheme.indexOf(item))
                    viewModel.changeFolderTheme.value = item.folder
                } else if (item.isSelected) {
                    Log.e("CCCCCCCCCCCCC", "checkSelected:555 &=${item.folder}")

                    item.isSelected = false
                    adapterLockTheme.notifyItemChanged(listLockTheme.indexOf(item))
                }
            }
            // ads

            val intent = Intent(requireContext(), ApplyThemeSuccessActivity::class.java)
            ApplyThemeSuccessActivity.drawable = pattern
            if (requireActivity() is ThemeActivity){
                (requireActivity() as ThemeActivity).showInterAds(intent)
            }
//            requireActivity().launchActivity<ApplyThemeSuccessActivity> {
//                ApplyThemeSuccessActivity.drawable = pattern
//            }
        }
    }
}