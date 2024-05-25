package com.appanhnt.applocker.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.appanhnt.applocker.R
import com.appanhnt.applocker.adapter.DetailAlbumRestoreImageAdapter
import com.appanhnt.applocker.databinding.FragmentDetailAlbumRestoreIamgeBinding
import com.appanhnt.applocker.databinding.LayoutDialogDeleteImageBinding
import com.appanhnt.applocker.dialog.DialogDeleteImage
import com.appanhnt.applocker.item.ItemAlbumRestoreImages
import com.appanhnt.applocker.utils.DialogLoadingUtils
import com.appanhnt.applocker.utils.ImageUtil
import com.appanhnt.applocker.utils.RecycleViewUtils
import com.anhnt.baseproject.fragment.BaseFragment
import com.appanhnt.applocker.activity.restore.RestoreActivity

import java.io.File

class FragmentDetailAlbumImage(var item: ItemAlbumRestoreImages) :
    BaseFragment<FragmentDetailAlbumRestoreIamgeBinding>() {
    var onBack: (() -> Unit)? = null
    private var dialogQuestion: DialogDeleteImage? = null
    private var adapterDetailAlbumRestoreImage: DetailAlbumRestoreImageAdapter? = null
    override fun initView() {
        adapterDetailAlbumRestoreImage =
            DetailAlbumRestoreImageAdapter(requireContext(), item.listPhoto)
        binding.rclImages.adapter = adapterDetailAlbumRestoreImage
        RecycleViewUtils.clearAnimation(binding.rclImages)
    }

    override fun initData() {

    }

    override fun initListener() {
        adapterDetailAlbumRestoreImage?.listenerOnClick = {
            selectImage(it)
        }
        binding.txtSelectAll.setOnClickListener {
            var i = 0
            item.listPhoto.forEach {
                if (it.isSelected) {
                    i++
                }
            }
            item.listPhoto.forEach {
                if (!it.isSelected) {
                    it.isSelected = true
                    it.number = ++i
                    adapterDetailAlbumRestoreImage?.notifyItemChanged(item.listPhoto.indexOf(it))
                }
            }
        }
        //
        binding.txtRestore.setOnClickListener {
            showDialogQuestion(requireContext().getString(R.string.do_want_restore_photos))
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailAlbumRestoreIamgeBinding {
        return FragmentDetailAlbumRestoreIamgeBinding.inflate(inflater, container, false)
    }

    private fun selectImage(it: Int) {
        var i = 0
        item.listPhoto[it].isSelected = !item.listPhoto[it].isSelected
        if (item.listPhoto[it].isSelected) {
            item.listPhoto.forEach { p ->
                if (p.isSelected) {
                    i++
                }
            }
            item.listPhoto[it].number = i
            adapterDetailAlbumRestoreImage?.notifyItemChanged(it)
        } else {
            item.listPhoto.forEach { i ->
                if (i.number > item.listPhoto[it].number) {
                    i.number -= 1
                    adapterDetailAlbumRestoreImage?.notifyItemChanged(item.listPhoto.indexOf(i))
                }
            }
            item.listPhoto[it].number = 0
            adapterDetailAlbumRestoreImage?.notifyItemChanged(it)
        }
    }

    private fun showDialogQuestion(content: String? = null) {
        val bindDialog =
            LayoutDialogDeleteImageBinding.inflate(LayoutInflater.from(requireContext()))
        dialogQuestion = DialogDeleteImage(requireContext(), bindDialog).apply {
            setCancelable(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            listenerYes = {
                dialogQuestion?.dismiss()
                restoreImage()
            }
            listenerNo = {
                dialogQuestion?.dismiss()
            }
        }
        content?.let { dialogQuestion?.binding?.txtContent?.text = it }
        dialogQuestion?.show()
    }

    private fun restoreImage() {
        DialogLoadingUtils.showDialogHiding(requireContext(), true)
        val listImage = mutableListOf<File>()
        item.listPhoto.forEach {
            if (it.isSelected) {
                listImage.add(File(it.path))
                it.isSelected = false
            }
        }
        ImageUtil.recoverListFileImage(listImage, requireContext())
        DialogLoadingUtils.showDialogHiding(requireContext(), false)
        if (requireActivity() is RestoreActivity){
            (requireActivity() as RestoreActivity).showIntersAds(null)
        }
        onBack?.invoke()
    }
}