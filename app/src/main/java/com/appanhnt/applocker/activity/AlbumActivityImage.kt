package com.appanhnt.applocker.activity

import android.app.Activity
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
import com.appanhnt.applocker.adapter.AdapterAlbum
import com.appanhnt.applocker.dialog.DialogDeleteImage
import com.appanhnt.applocker.dialog.DialogHiding
import com.appanhnt.applocker.dialog.DialogReadMediaPermission
import com.appanhnt.applocker.item.ItemDetailPhoto
import com.appanhnt.applocker.key.Vault
import com.appanhnt.applocker.utils.RecycleViewUtils
import com.appanhnt.applocker.viewmodel.HideImageViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.ActivityAlbumBinding
import com.appanhnt.applocker.databinding.LayoutDialogDeleteImageBinding
import com.appanhnt.applocker.key.KEY_COUNT
import com.appanhnt.applocker.key.REQUEST_CODE_READ_MEDIA_PMS
import com.google.android.gms.ads.ez.EzAdControl
import org.koin.android.ext.android.inject
import java.io.File


class AlbumActivityImage : BaseActivity<ActivityAlbumBinding>() {
    private lateinit var adapterAlbum: AdapterAlbum
    private val listDetailPhoto = mutableListOf<ItemDetailPhoto>()
    private var dialogQuestion: DialogDeleteImage? = null
    private var dialogHiding: DialogHiding? = null
    var count = 0
    private val viewModel by inject<HideImageViewModel>()
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
        // ads
        EzAdControl.getInstance(this).showAds()
        //
        setStatusBarHomeTransparent(this)
        binding.album.setPadding(0, getHeightStatusBar(), 0, 0)
//
        adapterAlbum = AdapterAlbum(this, listDetailPhoto)
        binding.rclAlbum.adapter = adapterAlbum
        RecycleViewUtils.clearAnimation(binding.rclAlbum)
        checkItemCount()
    }

    override fun initData() {
        if (checkReadMediaPms()) {
            listDetailPhoto.clear()
            viewModel.imageStoreLiveData.observe(this) {
                if (it.path.isNotEmpty()) {
                    listDetailPhoto.add(ItemDetailPhoto(it))
                    adapterAlbum.notifyItemInserted(listDetailPhoto.size - 1)
                }

            }
            viewModel.getImagesGallery(this, true)
        } else {
            dialogPms.show()
        }
        //
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


    override fun initListener() {
        adapterAlbum.listenerOnClick = {
            selectImage(it)
            checkItemCount()
        }
        //
        binding.icBackAlbum.setOnClickListener {
            onBackPressed()
        }
        //
        binding.ivSelectAll.setOnClickListener {
            var i = 0
            listDetailPhoto.forEach {
                if (it.isSelected) {
                    i++
                }
            }
            if (i == listDetailPhoto.size) {
                listDetailPhoto.forEach {
                    it.isSelected = false
                    adapterAlbum.notifyItemChanged(listDetailPhoto.indexOf(it))
                }
            } else {
                listDetailPhoto.forEach {
                    if (!it.isSelected) {
                        it.isSelected = true
                        adapterAlbum.notifyItemChanged(listDetailPhoto.indexOf(it))
                    }
                }
            }
            checkItemCount()
        }
        //hide
        binding.txtHide.setOnClickListener {
//            showDialogDeleteImage(getString(R.string.delete_after_hiding))
            encryptImage()
        }
    }

    private fun checkItemCount() {
        count = listDetailPhoto.count { it.isSelected }
        if (count == 0) {
            binding.txtHide.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#848484"))
        } else {
            binding.txtHide.backgroundTintList = null
        }
        binding.txtHide.text = getString(R.string.hide) + " ($count)"
    }

    override fun viewBinding(): ActivityAlbumBinding {
        return ActivityAlbumBinding.inflate(LayoutInflater.from(this))
    }

    private fun encryptImage() {
        showDialogHiding()
        viewModel.isHide.observe(this) {
            if (it) {
                Handler().postDelayed({
                    dialogHiding?.dismiss()
                    listDetailPhoto.forEach {
                        if (it.isSelected) {
                            val file = it.file
                            viewModel.deleteImage(file.absolutePath, this)
                        }
                    }
                }, 200)
            }
        }
        viewModel.encryptFile(listDetailPhoto, this, filesDir.path)
    }

    private fun selectImage(it: Int) {
        var i = 0
        listDetailPhoto[it].isSelected = !listDetailPhoto[it].isSelected
        if (listDetailPhoto[it].isSelected) {
            listDetailPhoto.forEach { p ->
                if (p.isSelected) {
                    i++
                }
            }
            listDetailPhoto[it].number = i
            adapterAlbum.notifyItemChanged(it)
        } else {
            listDetailPhoto.forEach { item ->
                if (item.number > listDetailPhoto[it].number) {
                    item.number -= 1
                    adapterAlbum.notifyItemChanged(listDetailPhoto.indexOf(item))
                }
            }
            listDetailPhoto[it].number = 0
            adapterAlbum.notifyItemChanged(it)
        }
    }

    private fun showDialogDeleteImage(content: String? = null) {
        val bindDialog = LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.let {
            it.setCancelable(false)
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            content?.let { c -> it.binding.txtContent.text = c }
            it.show()
        }
        dialogQuestion?.listenerYes = {
            dialogQuestion?.dismiss()
            encryptImage()
            listDetailPhoto.forEach {
                if (it.isSelected) {
                    val file = it.file
                    viewModel.deleteImage(file.absolutePath, this)
                }
            }
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
            encryptImage()
        }
    }

    private fun showDialogHiding() {
        dialogHiding = DialogHiding(this)
        dialogHiding?.apply {
            setCancelable(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            show()
        }
        dialogHiding?.setOnDismissListener {
            onBackPressed()
            launchActivity<ResultSuccessActivity> {
                putExtra(KEY_COUNT, count)
            }
            EzAdControl.getInstance(this).showAds()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Vault.REQUEST_CODE_DELETE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                //                    remove success)
            }
        }

        if (requestCode == REQUEST_CODE_READ_MEDIA_PMS) {
            if (checkReadMediaPms()) {
                dialogPms.dismiss()
                initData()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.imageStoreLiveData.postValue(File(""))
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}