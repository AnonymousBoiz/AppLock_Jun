package com.appanhnt.applocker.activity.preview


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.ActivityPreviewImageBinding
import com.appanhnt.applocker.databinding.LayoutDialogDeleteImageBinding
import com.appanhnt.applocker.databinding.LayoutDialogInfoImageBinding
import com.appanhnt.applocker.dialog.DialogDeleteImage
import com.appanhnt.applocker.dialog.DialogHiding
import com.appanhnt.applocker.dialog.DialogInfoImage
import com.appanhnt.applocker.item.ItemImageHide
import com.appanhnt.applocker.key.Vault
import com.appanhnt.applocker.service.LockService
import com.appanhnt.applocker.utils.CryptoUtil
import com.appanhnt.applocker.utils.IOUtil
import com.appanhnt.applocker.utils.ImageUtil
import com.appanhnt.applocker.viewmodel.HideImageViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getFileLength
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@KoinApiExtension
class PreviewImageActivity : BaseActivity<ActivityPreviewImageBinding>() {
    private var path: String? = null
    private var name: String? = null
    private var dialogQuestion: DialogDeleteImage? = null
    private var dialogInfo: DialogInfoImage? = null
    private val viewModelHide by inject<HideImageViewModel>()
    private var dialogHiding: DialogHiding? = null
    private lateinit var hide: ItemImageHide
    override fun initView() {
        setStatusBarHomeTransparent(this)
//
        binding.showImage.setPadding(0, getHeightStatusBar(), 0, 0)
//        setAppActivityFullScreenOver(this)
        name = intent.getStringExtra(Vault.KEY_IMAGE_NAME)
        path = intent.getStringExtra(Vault.KEY_IMAGE_PATH)
        hide = ItemImageHide(name!!, path!!)
        binding.namrImage.text = name

        path?.let {
            val decrypted: ByteArray? = CryptoUtil.decrypt(IOUtil.read(File(it)), Vault.PW(), it)
            if (decrypted!=null) {
                val bitmap = ImageUtil.decryptNDisplay(path!!) ?: ImageUtil.decodeBitmap(decrypted)
                if (bitmap==null) {
                    binding.error.text =
                        getString(R.string.can_t_open_because_memory_is_not_enough).replace(
                            "\\n",
                            "\n"
                        )
                    binding.error.isVisible = true
                } else {
                    binding.error.isVisible = false
                    Glide.with(this)
                        .load(bitmap)
                        .into(binding.image)
                }

            }
        }
    }

    override fun initData() {
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initListener() {
        binding.icDelete.setOnClickListener {
            name?.let {
                showDialogDeleteImage(it)
            }
        }
        binding.icLock.setOnClickListener {
            showDialogRecoverImage(resources.getString(R.string.recover_dialog_title_image))
        }
        binding.icCircleFill.setOnClickListener {
            showDialogInfo()
        }
        binding.icBackShowImage.setOnClickListener {
            onBackPressed()
        }
    }

    private fun deleteImageHide(name: String) {
        val list = Hawk.get(Vault.KEY_FILE_NAME_IMAGE, mutableListOf<ItemImageHide>())
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.fileName==name) {
                iterator.remove()
                viewModelHide.recoverImage(item.thumbnailPath)
                viewModelHide.recoverImage.value = hide
            }
        }
        Hawk.put(Vault.KEY_FILE_NAME_IMAGE, list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==Vault.REQUEST_CODE_DELETE_IMAGE) {
            if (resultCode==Activity.RESULT_OK) {
                //                    remove success)
            }
        }
    }

    private fun showDialogDeleteImage(name: String, content: String? = null) {
        val bindDialog = LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        content?.let { dialogQuestion?.binding?.txtContent?.text = it }
        dialogQuestion?.show()
        dialogQuestion?.listenerYes = {
            deleteImageHide(name)
            dialogQuestion?.dismiss()
            showDialogHiding()
            Handler().postDelayed({
                dialogHiding?.dismiss()
            }, 250)
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
        }
    }

    private fun showDialogRecoverImage(content: String? = null) {
        val bindDialog = LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        content?.let { dialogQuestion?.binding?.txtContent?.text = it }
        dialogQuestion?.show()
        dialogQuestion?.listenerYes = {
            LockService.isCreate = true // not lock when request any permission
            requestPermission(complete = {
                if (it) {
                    val msg: Int
                    path?.let { p ->
                        name?.let { n ->
                            showDialogHiding()
                            if (ImageUtil.recoverImage(n, this, p)) {
                                viewModelHide.recoverImage.value = hide
                                msg = R.string.image_recovered_msg
                            } else
                                msg = R.string.image_not_recovered_msg
                            Toast.makeText(this, getString(msg), Toast.LENGTH_SHORT).show()
                            Handler().postDelayed({
                                dialogHiding?.dismiss()
                            }, 250)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        R.string.permission_denied_to_recover_msg_img,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDialogInfo() {
        val bindDialog = LayoutDialogInfoImageBinding.inflate(LayoutInflater.from(this))
        var file: File? = null
        var date: String? = null
        path?.let {
            file = File(it)
            date = SimpleDateFormat("dd/MM/yyyy").format(Date(file!!.lastModified()))
        }
        dialogInfo = DialogInfoImage(this, bindDialog)
        dialogInfo?.apply {
            setCancelable(true)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            binding.apply {
                txtName.text = name
                txtSize.text = file?.getFileLength()
                txtDate.text = date
                txtPath.text = path
            }
            show()
        }
    }

    override fun viewBinding(): ActivityPreviewImageBinding {
        return ActivityPreviewImageBinding.inflate(LayoutInflater.from(this))
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
        }
    }
}