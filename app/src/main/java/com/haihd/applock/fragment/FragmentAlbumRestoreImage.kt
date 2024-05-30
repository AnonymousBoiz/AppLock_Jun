package com.haihd.applock.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.haihd.applock.activity.restore.RestoreActivity
import com.haihd.applock.adapter.AlbumRestoreImageAdapter
import com.haihd.applock.databinding.FragmentAlbumRestoreImageBinding
import com.haihd.applock.item.ItemAlbumRestoreImages
import com.haihd.applock.utils.RecycleViewUtils
import com.haihd.applock.viewmodel.RestoreImageViewModel
import com.anhnt.baseproject.fragment.BaseFragment
import org.koin.android.ext.android.inject
import java.io.File

class FragmentAlbumRestoreImage : BaseFragment<FragmentAlbumRestoreImageBinding>() {
    private val viewModelRestoreImages by inject<RestoreImageViewModel>()
    private var adapterAlbumRestoreImage: AlbumRestoreImageAdapter? = null
    private var listAlbumRestorePhoto = mutableListOf<ItemAlbumRestoreImages>()
    override fun initView() {
//        binding.animationView.visibility = View.VISIBLE
        adapterAlbumRestoreImage = AlbumRestoreImageAdapter(requireContext(), listAlbumRestorePhoto)
        binding.rclRestoreImage.adapter = adapterAlbumRestoreImage
        RecycleViewUtils.clearAnimation(binding.rclRestoreImage)
//        loadData = false
    }

    override fun initData() {
        listAlbumRestorePhoto.clear()
        viewModelRestoreImages.listAlbumRestorePhotoLiveData.observe(this) {
            if (it!=null&&listAlbumRestorePhoto.isEmpty()) {
//                listAlbumRestorePhoto.clear()
                listAlbumRestorePhoto.addAll(it)
            }
        }
//        listAlbumRestorePhoto.clear()
        viewModelRestoreImages.albumRestorePhotoLiveData.observe(this) {
            if (it!=null&&it.listPhoto.isNotEmpty()&&!listAlbumRestorePhoto.contains(it)&&!checkDuplicateFolder(
                    File(it.path).name
                )
            ) {
                listAlbumRestorePhoto.add(it)
                adapterAlbumRestoreImage?.notifyItemInserted(listAlbumRestorePhoto.size - 1)
//                }
            }
            if (listAlbumRestorePhoto.isEmpty()) {
                binding.txtNoImage.visibility = View.VISIBLE
            } else {
                binding.txtNoImage.visibility = View.INVISIBLE
            }
        }
        viewModelRestoreImages.getImageRestore(requireContext())
        //
        viewModelRestoreImages.position.observe(this) {
            binding.rclRestoreImage.layoutManager?.apply {
                if (itemCount > it) {
                    scrollToPosition(it)
                }
            }
        }
    }

    override fun initListener() {
        adapterAlbumRestoreImage?.listenerOnClick = {
            (activity as RestoreActivity).fragmentDetailAlbumImage(listAlbumRestorePhoto[it])
            viewModelRestoreImages.listAlbumRestorePhotoLiveData.value = (listAlbumRestorePhoto).toMutableList()
            viewModelRestoreImages.position.value = ((binding.rclRestoreImage.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())
        }
    }

    private fun checkDuplicateFolder(name: String): Boolean {
        for (i in listAlbumRestorePhoto) {
            if (File(i.path).name==name) {
                return true
            }
        }
        return false
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAlbumRestoreImageBinding {
        return FragmentAlbumRestoreImageBinding.inflate(inflater, container, false)
    }
}