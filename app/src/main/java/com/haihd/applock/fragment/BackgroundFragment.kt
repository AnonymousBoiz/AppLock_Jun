package com.haihd.applock.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.GridLayoutManager
import com.haihd.applock.R
import com.haihd.applock.activity.theme.ThemeActivity
import com.haihd.applock.adapter.LockScreenAdapter
import com.haihd.applock.databinding.FragmentLockThemeBinding
import com.haihd.applock.dialog.DialogShowLockScreen2
import com.haihd.applock.item.ItemLockScreen
import com.haihd.applock.key.KeyTheme
import com.haihd.applock.utils.RecycleViewUtils
import com.haihd.applock.viewmodel.ThemeViewModel
import com.anhnt.baseproject.fragment.BaseFragment
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.activity.theme.ApplyThemeSuccessActivity

import com.yalantis.ucrop.UCrop
import org.koin.android.ext.android.inject
import java.io.File


class BackgroundFragment : BaseFragment<FragmentLockThemeBinding>() {
    private val PICK_IAMAGE = 1
    var listPhotoScreen = mutableListOf<ItemLockScreen>()
    var adapterLockScreen: LockScreenAdapter? = null
    private var dialogShow: DialogShowLockScreen2? = null
    private val viewModel by inject<ThemeViewModel>()

    override fun initView() {

        adapterLockScreen = LockScreenAdapter(requireContext(), listPhotoScreen)
        binding.rclLockTheme.adapter = adapterLockScreen
        (binding.rclLockTheme.layoutManager as GridLayoutManager).spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (position == 3) {
                        3
                    } else {
                        1
                    }
                }

            }
        // clear anim rcl
        RecycleViewUtils.clearAnimation(binding.rclLockTheme)
        //load ads
        loadAds()
    }

    override fun initData() {
        viewModel.getBackground(requireContext())
        listPhotoScreen.clear()
        viewModel.listBackground.observe(this) {
            if (!it.isNullOrEmpty()) {
                listPhotoScreen.clear()
                listPhotoScreen.addAll(it)
                adapterLockScreen?.notifyDataSetChanged()
            }
        }
//        Handler().postDelayed({
//            if (listPhotoScreen.isNullOrEmpty()) {
//                viewModel.getBackground(requireContext())
//            }
//        }, 3000)
        //
        viewModel.changeFolderTheme.observe(this) {
            for (item in listPhotoScreen) {
                if (item.folder == it) {
                    item.isSelected = true
                    adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(item))
                } else if (item.isSelected) {
                    item.isSelected = false
                    adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(item))
                }
            }
        }
    }

    override fun initListener() {
        adapterLockScreen?.listenerOnClick = {
            if (listPhotoScreen[it].folder == KeyTheme.BG_CUSTOM) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    (activity as ThemeActivity).requestPermission(complete = { allow ->
                        if (allow) {
                            pickImage()
                        }
                    }, Manifest.permission.READ_MEDIA_IMAGES)
                }else {
                    (activity as ThemeActivity).requestPermission(complete = { allow ->
                        if (allow) {
                            pickImage()
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                dialogShow = DialogShowLockScreen2(
                    requireContext(),
                    listPhotoScreen[it].photo,
                    R.style.MyDialogTheme
                )
                dialogShow?.apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                    show()
                    listenerApply = {
                        for (i in listPhotoScreen) {
                            if (i.folder == listPhotoScreen[it].folder) {
                                PreferencesUtils.putString(
                                    KeyTheme.KEY_BACKGROUND,
                                    listPhotoScreen[it].folder
                                )
                                i.isSelected = true
                                adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(i))
                            } else {
                                i.isSelected = false
                                adapterLockScreen?.notifyItemChanged(listPhotoScreen.indexOf(i))
                            }
                        }
                        dialogShow?.dismiss()
                        // ads
                        val intent = Intent(requireContext(), ApplyThemeSuccessActivity::class.java)
                        ApplyThemeSuccessActivity.drawable = listPhotoScreen[it].photo
                        if (requireActivity() is ThemeActivity){
                            (requireActivity() as ThemeActivity).showInterAds(intent)
                        }

//                        requireActivity().launchActivity<ApplyThemeSuccessActivity> {
//                            ApplyThemeSuccessActivity.drawable = listPhotoScreen[it].photo
//                        }
                    }
                }
            }
        }
    }

    private fun loadAds() {
//        adapterLockScreen?.adsView = nativeAd
//        adapterLockScreen?.addAds(
//            ItemLockScreen(
//                "", null,
//                isSelected = false,
//                isAds = true
//            ), 3
//        )
    }

    private fun pickImage() {
        try {
            val pickPhoto =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhoto, PICK_IAMAGE)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IAMAGE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IAMAGE -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let {
                        UCrop.of(it, Uri.fromFile(File(requireContext().cacheDir, "abc.png")))
                            .useSourceImageAspectRatio()
                            .withAspectRatio(9F, 16F)
                            .start(activity as ThemeActivity)
                    }
                }
            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLockThemeBinding {
        return FragmentLockThemeBinding.inflate(inflater, container, false)
    }
}