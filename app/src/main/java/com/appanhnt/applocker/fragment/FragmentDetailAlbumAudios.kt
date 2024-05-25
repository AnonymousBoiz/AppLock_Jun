package com.appanhnt.applocker.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.appanhnt.applocker.R
import com.appanhnt.applocker.adapter.DetailAlbumRestoreAudiosAdapter
import com.appanhnt.applocker.databinding.FragmentDetailAlbumAudioBinding
import com.appanhnt.applocker.databinding.LayoutDialogDeleteImageBinding
import com.appanhnt.applocker.dialog.DialogDeleteImage
import com.appanhnt.applocker.item.ItemAlbumRestoreAudios
import com.appanhnt.applocker.utils.DialogLoadingUtils
import com.appanhnt.applocker.utils.ImageUtil
import com.appanhnt.applocker.utils.RecycleViewUtils
import com.anhnt.baseproject.fragment.BaseFragment
import com.appanhnt.applocker.activity.restore.RestoreActivity

import java.io.File

class FragmentDetailAlbumAudios(var item: ItemAlbumRestoreAudios) :
    BaseFragment<FragmentDetailAlbumAudioBinding>() {
    private var dialogQuestion: DialogDeleteImage? = null
    private var adapterDetailAlbumRestoreAudios: DetailAlbumRestoreAudiosAdapter? = null
    var onBack: (() -> Unit)? = null
    override fun initView() {
        adapterDetailAlbumRestoreAudios =
            DetailAlbumRestoreAudiosAdapter(requireContext(), item.listAudios)
        binding.rclAudios.adapter = adapterDetailAlbumRestoreAudios
        RecycleViewUtils.clearAnimation(binding.rclAudios)
    }

    override fun initData() {

    }

    override fun initListener() {
        adapterDetailAlbumRestoreAudios?.listenerOnClick = {
            selectImage(it)
        }
        binding.txtSelectAll.setOnClickListener {
            var i = 0
            item.listAudios.forEach {
                if (it.isSelected) {
                    i++
                }
            }
            item.listAudios.forEach {
                if (!it.isSelected) {
                    it.isSelected = true
                    it.number = ++i
                    adapterDetailAlbumRestoreAudios?.notifyItemChanged(item.listAudios.indexOf(it))
                }
            }
        }
        //
        binding.txtRestore.setOnClickListener {
            showDialogQuestion(requireContext().getString(R.string.do_want_restore_audios))
        }

    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailAlbumAudioBinding {
        return FragmentDetailAlbumAudioBinding.inflate(inflater, container, false)
    }

    private fun selectImage(it: Int) {
        var i = 0
        item.listAudios[it].isSelected = !item.listAudios[it].isSelected
        if (item.listAudios[it].isSelected) {
            item.listAudios.forEach { p ->
                if (p.isSelected) {
                    i++
                }
            }
            item.listAudios[it].number = i
            adapterDetailAlbumRestoreAudios?.notifyItemChanged(it)
        } else {
            item.listAudios.forEach { i ->
                if (i.number > item.listAudios[it].number) {
                    i.number -= 1
                    adapterDetailAlbumRestoreAudios?.notifyItemChanged(item.listAudios.indexOf(i))
                }
            }
            item.listAudios[it].number = 0
            adapterDetailAlbumRestoreAudios?.notifyItemChanged(it)
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
                restoreAudio()
            }
            listenerNo = {
                dialogQuestion?.dismiss()
            }
        }
        content?.let { dialogQuestion?.binding?.txtContent?.text = it }
        dialogQuestion?.show()
    }


    private fun restoreAudio() {
        DialogLoadingUtils.showDialogHiding(requireContext(), true)
        val listAudios = mutableListOf<File>()
        item.listAudios.forEach {
            if (it.isSelected) {
                listAudios.add(File(it.path))
                it.isSelected = false
            }
        }
        ImageUtil.recoverListAudios(listAudios, requireContext())
        DialogLoadingUtils.showDialogHiding(requireContext(), false)
        if (requireActivity() is RestoreActivity){
            (requireActivity() as RestoreActivity).showIntersAds(null)
        }
        onBack?.invoke()
    }
}