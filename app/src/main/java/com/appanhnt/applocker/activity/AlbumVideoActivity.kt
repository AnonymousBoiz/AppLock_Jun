package com.appanhnt.applocker.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.appanhnt.applocker.R
import com.appanhnt.applocker.adapter.AdapterVideo
import com.appanhnt.applocker.databinding.LayoutDialogDeleteImageBinding
import com.appanhnt.applocker.dialog.DialogDeleteImage
import com.appanhnt.applocker.dialog.DialogHiding
import com.appanhnt.applocker.dialog.DialogReadMediaPermission
import com.appanhnt.applocker.item.ItemVideo
import com.appanhnt.applocker.utils.RecycleViewUtils
import com.appanhnt.applocker.viewmodel.HideImageViewModel
import com.appanhnt.applocker.viewmodel.HideVideoModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.appanhnt.applocker.activity.ResultSuccessActivity
import com.appanhnt.applocker.databinding.ActivityAlbumVideoBinding
import com.appanhnt.applocker.key.KEY_COUNT
import com.appanhnt.applocker.key.REQUEST_CODE_READ_MEDIA_PMS
import com.google.android.gms.ads.ez.EzAdControl
import org.koin.android.ext.android.inject
import java.io.File

class AlbumVideoActivity : BaseActivity<ActivityAlbumVideoBinding>() {
    private lateinit var adapterVideo: AdapterVideo
    private val listVideo = mutableListOf<ItemVideo>()
    private val viewModel by inject<HideVideoModel>()
    private val viewModelImage by inject<HideImageViewModel>()
    private var dialogQuestion: DialogDeleteImage? = null
    private var dialogHiding: DialogHiding? = null
    var count = 0
    private val dialogPms by lazy {
        DialogReadMediaPermission(
            this,
            R.style.full_screen_dialog,
            object : DialogReadMediaPermission.Callback {
                override fun onGoSettings() {
                    requestReadMediaPms()
                }
            })
    }

    override fun initView() {
        checkItemCount()
        // ads
        EzAdControl.getInstance(this).showAds()
        //
        setStatusBarHomeTransparent(this)
        binding.album.setPadding(0, getHeightStatusBar(), 0, 0)
        //
        adapterVideo = AdapterVideo(this, listVideo)
        binding.rclAlbum.adapter = adapterVideo
        // clear animation rcl
        RecycleViewUtils.clearAnimation(binding.rclAlbum)
    }

    override fun initData() {
        if (checkReadMediaPms()) {
            listVideo.clear()
            viewModel.videos.observe(this) {
                if (it.file.path.isNotEmpty()) {
                    listVideo.add((it))
                    adapterVideo.notifyItemInserted(listVideo.size - 1)
                }
            }
            viewModel.getVideoGallery(this)
        } else {
            dialogPms.show()
        }
    }

    override fun initListener() {
        adapterVideo.listenerOnClick = {
            selectImage(it)
            checkItemCount()
        }
        binding.ivSelectAll.setOnClickListener {
            var i = 0
            listVideo.forEach {
                if (it.isSelected) {
                    i++
                }
            }
            if (i == listVideo.size) {
                listVideo.forEach {
                    it.isSelected = false
                    adapterVideo.notifyItemChanged(listVideo.indexOf(it))
                }
            } else {
                listVideo.forEach {
                    if (!it.isSelected) {
                        it.isSelected = true
                        adapterVideo.notifyItemChanged(listVideo.indexOf(it))
                    }
                }
            }
            checkItemCount()
        }
        binding.txtHide.setOnClickListener {
            encryptVideo()
        }
        binding.icBackAlbum.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkItemCount() {
        count = listVideo.count { it.isSelected }
        if (count == 0) {
            binding.txtHide.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#848484"))
        } else {
            binding.txtHide.backgroundTintList = null
        }
        binding.txtHide.text = getString(R.string.hide) + " ($count)"
    }

    override fun viewBinding(): ActivityAlbumVideoBinding {
        return ActivityAlbumVideoBinding.inflate(LayoutInflater.from(this))
    }

    private fun selectImage(it: Int) {
        var i = 0
        listVideo[it].isSelected = !listVideo[it].isSelected
        if (listVideo[it].isSelected) {
            listVideo.forEach { p ->
                if (p.isSelected) {
                    i++
                }
            }
            listVideo[it].number = i
            adapterVideo.notifyItemChanged(it)
        } else {
            listVideo.forEach { item ->
                if (item.number > listVideo[it].number) {
                    item.number -= 1
                    adapterVideo.notifyItemChanged(listVideo.indexOf(item))
                }
            }
            listVideo[it].number = 0
            adapterVideo.notifyItemChanged(it)
        }
    }

    private fun showDialogDeleteVideo(content: String? = null) {
        val bindDialog = LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.apply {
            setCancelable(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            content?.let { c -> binding.txtContent.text = c }
        }
        dialogQuestion?.listenerYes = {
            dialogQuestion?.dismiss()
            encryptVideo()
            listVideo.forEach {
                if (it.isSelected) {
                    val file = it.file
                    viewModel.deleteVideo(file.absolutePath, this)
                }
            }
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
            encryptVideo()
        }
        if (!isFinishing) {
            try {
                dialogQuestion?.show()

            } catch (e: WindowManager.BadTokenException) {
            }
        }
    }

    private fun encryptVideo() {
        showDialogHiding()
        viewModel.isHide.observe(this) {
            if (it) {
                viewModelImage.isHide.postValue(it)
                viewModel.isHide.postValue(false)
            }
            Handler().postDelayed({
                dialogHiding?.dismiss()
                listVideo.forEach {
                    if (it.isSelected) {
                        val file = it.file
                        viewModel.deleteVideo(file.absolutePath, this)
                    }
                }
            }, 300)
        }
        viewModel.encryptVideo(listVideo, this, filesDir.path)
    }

    private fun checkReadMediaPms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        }
    }

    private fun requestReadMediaPms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivityForResult(intent, REQUEST_CODE_READ_MEDIA_PMS)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", applicationContext.packageName, null)
            intent.data = uri
            startActivityForResult(intent, REQUEST_CODE_READ_MEDIA_PMS)
        }
    }

    private fun showDialogHiding() {
        dialogHiding = DialogHiding(this)
        dialogHiding?.setCancelable(false)
        dialogHiding?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogHiding?.show()
        dialogHiding?.setOnDismissListener {
            onBackPressed()
            launchActivity<ResultSuccessActivity> {
                putExtra(KEY_COUNT, count)
            }
            EzAdControl.getInstance(this).showAds()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.videos.postValue(ItemVideo(File("")))
    }
}