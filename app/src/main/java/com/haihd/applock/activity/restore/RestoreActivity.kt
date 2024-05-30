package com.haihd.applock.activity.restore

import android.content.Intent
import android.view.LayoutInflater
import android.view.Window
import com.haihd.applock.dialog.DialogDeleteImage
import com.haihd.applock.fragment.FragmentDetailAlbumAudios
import com.haihd.applock.fragment.FragmentDetailAlbumImage
import com.haihd.applock.fragment.FragmentDetailAlbumVideos
import com.haihd.applock.fragment.FragmentRestore
import com.haihd.applock.item.ItemAlbumRestoreAudios
import com.haihd.applock.item.ItemAlbumRestoreImages
import com.haihd.applock.item.ItemAlbumRestoreVideos
import com.haihd.applock.viewmodel.RestoreAudioViewModel
import com.haihd.applock.viewmodel.RestoreImageViewModel
import com.haihd.applock.viewmodel.RestoreVideoViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.haihd.applock.R
import com.haihd.applock.activity.home.HomeActivity
import com.haihd.applock.databinding.ActivityRetoreBinding
import com.haihd.applock.databinding.LayoutDialogDeleteImageBinding

import org.koin.android.ext.android.inject

class RestoreActivity : BaseActivity<ActivityRetoreBinding>() {
    private val viewModelRestoreImages by inject<RestoreImageViewModel>()
    private val viewModelRestoreVideos by inject<RestoreVideoViewModel>()
    private val viewModelRestoreAudios by inject<RestoreAudioViewModel>()
    private var dialogQuestion: DialogDeleteImage? = null
    override fun initView() {
        //
        setStatusBarHomeTransparent(this)
        binding.restore.setPadding(0, getHeightStatusBar(), 0, 0)
        fragmentStart()
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.icBackRestoreImage.setOnClickListener {
            onBackPressed()
        }
    }

    override fun viewBinding(): ActivityRetoreBinding {
        return ActivityRetoreBinding.inflate(LayoutInflater.from(this))
    }

    private fun fragmentStart() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = FragmentRestore(0)
        transaction.add(R.id.layout_fragment, fragment, "ALL")
        transaction.commit()
    }

    private fun fragmentAllRestore(position: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = FragmentRestore(position)
        transaction.apply {
            setCustomAnimations(
                R.anim.slide_enter,
                R.anim.slide_exit,
                R.anim.fadein,
                R.anim.fadeout
            )
            replace(R.id.layout_fragment, fragment, "ALL")
            addToBackStack(null).commit()
        }
    }

    fun fragmentDetailAlbumImage(item: ItemAlbumRestoreImages) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = FragmentDetailAlbumImage(item)
        fragment.onBack = {
            // ads
            showAds(null)
            fragmentAllRestore(0)
        }
        transaction.apply {
            setCustomAnimations(
                R.anim.fadein,
                R.anim.fadeout,
                R.anim.slide_enter,
                R.anim.slide_exit
            )
            replace(R.id.layout_fragment, fragment, "DETAIL_IMAGE")
            addToBackStack(null).commit()
        }
    }

    fun fragmentDetailAlbumVideo(item: ItemAlbumRestoreVideos) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = FragmentDetailAlbumVideos(item)
        fragment.onBack = {
            // ads
            showAds(null)
            fragmentAllRestore(1)
        }
        transaction.apply {
            setCustomAnimations(
                R.anim.fadein,
                R.anim.fadeout,
                R.anim.slide_enter,
                R.anim.slide_exit
            )
            replace(R.id.layout_fragment, fragment, "DETAIL_VIDEO")
            addToBackStack(null).commit()
        }
    }

    fun fragmentDetailAlbumAudio(item: ItemAlbumRestoreAudios) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = FragmentDetailAlbumAudios(item)
        fragment.onBack = {
            // ads
            showAds(null)
            fragmentAllRestore(2)
        }
        transaction.apply {
            setCustomAnimations(
                R.anim.fadein,
                R.anim.fadeout,
                R.anim.slide_enter,
                R.anim.slide_exit
            )
            replace(R.id.layout_fragment, fragment, "DETAIL_AUDIO")
            addToBackStack(null).commit()
        }
    }

    private fun showDialog(content: String? = null, item: Any, position: Int) {
        val bindDialog =
            LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.let {
            it.setCancelable(true)
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            content?.let { c -> it.binding.txtContent.text = c }
        }

        dialogQuestion?.listenerYes = {
            when (item) {
                is ItemAlbumRestoreImages -> {
                    item.listPhoto.forEach {
                        if (it.isSelected) {
                            it.isSelected = false
                        }
                    }
                }
                is ItemAlbumRestoreVideos -> {
                    item.listVideos.forEach {
                        if (it.isSelected) {
                            it.isSelected = false
                        }
                    }
                }
                is ItemAlbumRestoreAudios -> {
                    item.listAudios.forEach {
                        if (it.isSelected) {
                            it.isSelected = false
                        }
                    }
                }
            }
            dialogQuestion?.dismiss()
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
        }
        dialogQuestion?.setOnDismissListener {
            fragmentAllRestore(position)
//            super.onBackPressed()
        }
        dialogQuestion?.show()
    }

    override fun onBackPressed() {
        val myFragmentDetailImage = supportFragmentManager.findFragmentByTag("DETAIL_IMAGE")
        val myFragmentDetailVideos = supportFragmentManager.findFragmentByTag("DETAIL_VIDEO")
        val myFragmentDetailAudios = supportFragmentManager.findFragmentByTag("DETAIL_AUDIO")
        val myFragmentAll = supportFragmentManager.findFragmentByTag("ALL")
        if (myFragmentDetailImage!=null&&myFragmentDetailImage.isVisible) {
            var isCheck = false
            val list = (myFragmentDetailImage as FragmentDetailAlbumImage).item.listPhoto
            if (!list.isNullOrEmpty()) {
                for (it in list) {
                    try {
                        if (it.isSelected) {
                            isCheck = true
                            showDialog(
                                getString(R.string.deselected),
                                (myFragmentDetailImage).item, 0
                            )
                            break
                        }
                    } catch (e: NullPointerException) {
                    }
                }
            }
            if (!isCheck) {
                dialogQuestion?.dismiss()
                fragmentAllRestore(0)
//                fragmentManager.popBackStack()
            }
        } else if (myFragmentDetailVideos!=null&&myFragmentDetailVideos.isVisible) {
            var isCheck = false
            val list = (myFragmentDetailVideos as FragmentDetailAlbumVideos).item.listVideos
            if (!list.isNullOrEmpty()) {
                for (it in list) {
                    if (it.isSelected) {
                        isCheck = true
                        showDialog(
                            getString(R.string.deselected),
                            (myFragmentDetailVideos).item, 1
                        )
                        break
                    }
                }
            }
            if (!isCheck) {
                dialogQuestion?.dismiss()
                fragmentAllRestore(1)
//                fragmentManager.popBackStack()
            }
        } else if (myFragmentDetailAudios!=null&&myFragmentDetailAudios.isVisible) {
            var isCheck = false
            val list = (myFragmentDetailAudios as FragmentDetailAlbumAudios).item.listAudios
            if (!list.isNullOrEmpty()) {
                for (it in list) {
                    if (it.isSelected) {
                        isCheck = true
                        showDialog(
                            getString(R.string.deselected),
                            (myFragmentDetailAudios).item, 2
                        )
                        break
                    }
                }
            }
            if (!isCheck) {
                dialogQuestion?.dismiss()
                fragmentAllRestore(2)
//                fragmentManager.popBackStack()
            }
        } else if (myFragmentAll!=null&&myFragmentAll.isVisible) {
            viewModelRestoreImages.albumRestorePhotoLiveData.value = null
            viewModelRestoreImages.listAlbumRestorePhotoLiveData.value = null
            viewModelRestoreVideos.albumRestoreVideosLiveData.value = null
            viewModelRestoreVideos.listAlbumRestoreVideosLiveData.value = null
            viewModelRestoreAudios.albumRestoreAudioLiveData.value = null
            viewModelRestoreAudios.listAlbumRestoreAudioLiveData.value = null
            dialogQuestion?.dismiss()
            launchActivity<HomeActivity> { }
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
        }
    }

    fun showIntersAds(intent: Intent?){
        showAds(intent)
    }
}