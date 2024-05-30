package com.haihd.applock.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.haihd.applock.R
import com.haihd.applock.adapter.DetailAlbumRestoreVideosAdapter
import com.haihd.applock.databinding.FragmentDetailAlbumVideoBinding
import com.haihd.applock.databinding.LayoutDialogDeleteImageBinding
import com.haihd.applock.dialog.DialogDeleteImage
import com.haihd.applock.item.ItemAlbumRestoreVideos
import com.haihd.applock.utils.DialogLoadingUtils
import com.haihd.applock.utils.ImageUtil
import com.haihd.applock.utils.RecycleViewUtils
import com.anhnt.baseproject.fragment.BaseFragment
import com.haihd.applock.activity.restore.RestoreActivity

import java.io.File

class FragmentDetailAlbumVideos(var item: ItemAlbumRestoreVideos) :
    BaseFragment<FragmentDetailAlbumVideoBinding>() {
    private var dialogQuestion: DialogDeleteImage? = null
    private var adapterDetailAlbumRestoreVideos: DetailAlbumRestoreVideosAdapter? = null
    var onBack: (() -> Unit)? = null

    override fun initView() {
        adapterDetailAlbumRestoreVideos =
            DetailAlbumRestoreVideosAdapter(requireContext(), item.listVideos)
        binding.rclVideos.adapter = adapterDetailAlbumRestoreVideos
        RecycleViewUtils.clearAnimation(binding.rclVideos)
    }

    override fun initData() {
    }

    override fun initListener() {
        adapterDetailAlbumRestoreVideos?.listenerOnClick = {
            selectImage(it)
        }
        binding.txtSelectAll.setOnClickListener {
            var i = 0
            item.listVideos.forEach {
                if (it.isSelected) {
                    i++
                }
            }
            item.listVideos.forEach {
                if (!it.isSelected) {
                    it.isSelected = true
                    it.number = ++i
                    adapterDetailAlbumRestoreVideos?.notifyItemChanged(item.listVideos.indexOf(it))
                }
            }
        }
        //
        binding.txtRestore.setOnClickListener {
            showDialogQuestion(requireContext().getString(R.string.do_want_restore_videos))
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailAlbumVideoBinding {
        return FragmentDetailAlbumVideoBinding.inflate(inflater, container, false)
    }

    private fun selectImage(it: Int) {
        var i = 0
        item.listVideos[it].isSelected = !item.listVideos[it].isSelected
        if (item.listVideos[it].isSelected) {
            item.listVideos.forEach { p ->
                if (p.isSelected) {
                    i++
                }
            }
            item.listVideos[it].number = i
            adapterDetailAlbumRestoreVideos?.notifyItemChanged(it)
        } else {
            item.listVideos.forEach { i ->
                if (i.number > item.listVideos[it].number) {
                    i.number -= 1
                    adapterDetailAlbumRestoreVideos?.notifyItemChanged(item.listVideos.indexOf(i))
                }
            }
            item.listVideos[it].number = 0
            adapterDetailAlbumRestoreVideos?.notifyItemChanged(it)
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
                restoreVideo()
            }
            listenerNo = {
                dialogQuestion?.dismiss()
            }
        }
        content?.let { dialogQuestion?.binding?.txtContent?.text = it }
        dialogQuestion?.show()
    }

    private fun restoreVideo() {
        DialogLoadingUtils.showDialogHiding(requireContext(), true)
        val listVideos = mutableListOf<File>()
        item.listVideos.forEach {
            if (it.isSelected) {
                listVideos.add(File(it.path))
                it.isSelected = false
            }
        }
        ImageUtil.recoverListVideo(listVideos, requireContext())
        DialogLoadingUtils.showDialogHiding(requireContext(), false)
        if (requireActivity() is RestoreActivity){
            (requireActivity() as RestoreActivity).showIntersAds(null)
        }
        onBack?.invoke()
    }
}