package com.haihd.applock.activity.same

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.haihd.applock.adapter.SetSameImageAdapter
import com.haihd.applock.dialog.DialogDeleteImage
import com.haihd.applock.item.ItemSameImage
import com.haihd.applock.item.ItemSetSameImage
import com.haihd.applock.key.Vault
import com.haihd.applock.utils.DialogLoadingUtils
import com.haihd.applock.utils.RecycleViewUtils
import com.haihd.applock.viewmodel.HideImageViewModel
import com.haihd.applock.viewmodel.SameImageViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.haihd.applock.databinding.ActivitySameImageBinding
import com.haihd.applock.databinding.LayoutDialogDeleteImageBinding

import org.koin.android.ext.android.inject

class SameImageActivity : BaseActivity<ActivitySameImageBinding>() {
    private val viewModel by inject<HideImageViewModel>()
    private val viewModelSameImage by inject<SameImageViewModel>()
    private var listSetSameImage: MutableList<ItemSetSameImage> = mutableListOf()
    private var adapterSetSameImage: SetSameImageAdapter? = null
    private var dialogQuestion: DialogDeleteImage? = null
    override fun initView() {
        // ads
//        EzAdControl.getInstance(this).showAds()
        //
        setStatusBarHomeTransparent(this)
        binding.same.setPadding(0, getHeightStatusBar(), 0, 0)
        //
        adapterSetSameImage = SetSameImageAdapter(this, listSetSameImage)
        binding.rclSetSameImage.adapter = adapterSetSameImage
        RecycleViewUtils.clearAnimation(binding.rclSetSameImage)
    }

    override fun initData() {
        listSetSameImage.clear()
        viewModelSameImage.listSameImageLiveData.observe(this) {
            if (!it.isNullOrEmpty()) {
                binding.animationView.visibility = View.INVISIBLE
                binding.layoutContent.visibility = View.VISIBLE
                val listItemSame = mutableListOf<ItemSameImage>()
                for (i in it) {
                    listItemSame.add(ItemSameImage(i))
                }
                listSetSameImage.add(ItemSetSameImage(listItemSame))
                adapterSetSameImage?.notifyItemInserted(listSetSameImage.size - 1)
                if (it.isEmpty()) {
                    binding.noSameImage.visibility = View.VISIBLE
                } else {
                    binding.noSameImage.visibility = View.INVISIBLE
                }
            } else {
                binding.animationView.visibility = View.INVISIBLE
                binding.layoutContent.visibility = View.VISIBLE
                binding.noSameImage.visibility = View.VISIBLE
            }
        }
        //
        viewModel.isDeleteSameImage.observe(this) {
            if (it) {
                // ads
                showAds(null)
                DialogLoadingUtils.showDialogHiding(this, false)
                if (listSetSameImage.isEmpty()) {
                    binding.noSameImage.visibility = View.VISIBLE
                } else {
                    binding.noSameImage.visibility = View.INVISIBLE
                }
                viewModel.isDeleteSameImage.value = false
            }
        }
        //
        // same Image
        viewModel.listImageStoreLiveData.observe(this) { list ->
            if (!list.isNullOrEmpty()) {
                viewModelSameImage.compareListImage(list)
            } else {
                binding.noSameImage.visibility = View.VISIBLE
                binding.animationView.visibility = View.INVISIBLE
                binding.layoutContent.visibility = View.VISIBLE
            }
        }
    }

    override fun initListener() {
        binding.txtSelectAll.setOnClickListener {
            for (i in listSetSameImage) {
                for (y in i.listImage) {
                    y.isSelected = true
                }
                adapterSetSameImage?.notifyItemChanged(listSetSameImage.indexOf(i))
            }
        }
        binding.txtDelete.setOnClickListener {
            showDialog()
        }
        binding.icBackSameImage.setOnClickListener {
            onBackPressed()
        }
    }

    override fun viewBinding(): ActivitySameImageBinding {
        return ActivitySameImageBinding.inflate(LayoutInflater.from(this))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==Vault.REQUEST_CODE_DELETE_IMAGE) {
            if (resultCode==Activity.RESULT_OK) {
                //                    remove success)
            }
        }
    }

    private fun showDialog(content: String? = null) {
        val bindDialog =
            LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(this))
        dialogQuestion = DialogDeleteImage(this, bindDialog)
        dialogQuestion?.let {
            it.setCancelable(true)
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
            content?.let { c -> it.binding.txtContent.text = c }
        }

        dialogQuestion?.listenerYes = {
            DialogLoadingUtils.showDialogHiding(this, true)
            val listSameImage = mutableListOf<String>()
            filterList(listSameImage)
            viewModel.deleteListImage(listSameImage, this)
            dialogQuestion?.dismiss()
        }
        dialogQuestion?.listenerNo = {
            dialogQuestion?.dismiss()
        }
        dialogQuestion?.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModelSameImage.listSameImageLiveData.value = null
        viewModel.listImageStoreLiveData.value = null
    }

    private fun filterList(listSameImage: MutableList<String>): Boolean {
        for (i in listSetSameImage) {
            for (y in i.listImage) {
                if (y.isSelected) {
                    listSameImage.add(y.file.path)
                    i.listImage.remove(y)
                    if (i.listImage.isEmpty()) {
                        listSetSameImage.remove(i)
                        adapterSetSameImage?.notifyItemRemoved(listSetSameImage.indexOf(i))
                    } else {
                        adapterSetSameImage?.notifyItemChanged(listSetSameImage.indexOf(i))
                    }
                    filterList(listSameImage)
                    return true
                }
            }
        }
        return true
    }
}