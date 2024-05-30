package com.haihd.applock.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.haihd.applock.activity.restore.RestoreActivity
import com.haihd.applock.adapter.AlbumRestoreVideoAdapter
import com.haihd.applock.databinding.FragmentAlbumVideosBinding
import com.haihd.applock.item.ItemAlbumRestoreVideos
import com.haihd.applock.utils.RecycleViewUtils
import com.haihd.applock.viewmodel.RestoreVideoViewModel
import com.anhnt.baseproject.fragment.BaseFragment
import org.koin.android.ext.android.inject
import java.io.File

class FragmentAlbumRestoreVideos : BaseFragment<FragmentAlbumVideosBinding>() {
    private val viewModelRestoreVideos by inject<RestoreVideoViewModel>()
    private var adapterAlbumRestoreVideo: AlbumRestoreVideoAdapter? = null
    private var listAlbumRestoreVideo = mutableListOf<ItemAlbumRestoreVideos>()
    override fun initView() {
        adapterAlbumRestoreVideo = AlbumRestoreVideoAdapter(requireContext(), listAlbumRestoreVideo)
        binding.rclRestoreVideo.adapter = adapterAlbumRestoreVideo
        RecycleViewUtils.clearAnimation(binding.rclRestoreVideo)
    }

    override fun initData() {
        listAlbumRestoreVideo.clear()
        viewModelRestoreVideos.listAlbumRestoreVideosLiveData.observe(this) {
            if (it!=null&&listAlbumRestoreVideo.isEmpty()) {
//                listAlbumRestoreVideo.clear()
                listAlbumRestoreVideo.addAll(it)
            }
        }
//        listAlbumRestoreVideo.clear()
        viewModelRestoreVideos.albumRestoreVideosLiveData.observe(this) {
            if (it!=null&&it.listVideos.isNotEmpty()&&!listAlbumRestoreVideo.contains(it)&&!checkDuplicateFolder(
                    File(it.path).name
                )
            ) {
                listAlbumRestoreVideo.add(it)
                adapterAlbumRestoreVideo?.notifyItemInserted(listAlbumRestoreVideo.size - 1)
            }
            if (listAlbumRestoreVideo.isEmpty()) {
                binding.txtNoVideos.visibility = View.VISIBLE
            } else {
                binding.txtNoVideos.visibility = View.INVISIBLE
            }
        }
        viewModelRestoreVideos.getVideoRestore(requireContext())
        //
        viewModelRestoreVideos.position.observe(this) {
            binding.rclRestoreVideo.layoutManager?.apply {
                if (itemCount > it) {
                    scrollToPosition(it)
                }
            }
        }
    }

    override fun initListener() {
        adapterAlbumRestoreVideo?.listenerOnClick = {
            (activity as RestoreActivity).fragmentDetailAlbumVideo(listAlbumRestoreVideo[it])
            viewModelRestoreVideos.listAlbumRestoreVideosLiveData.value =
                (listAlbumRestoreVideo).toMutableList()
            viewModelRestoreVideos.position.value =
                ((binding.rclRestoreVideo.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())
        }
    }


    private fun checkDuplicateFolder(name: String): Boolean {
        for (i in listAlbumRestoreVideo) {
            if (File(i.path).name==name) {
                return true
            }
        }
        return false
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAlbumVideosBinding {
        return FragmentAlbumVideosBinding.inflate(inflater, container, false)
    }
}