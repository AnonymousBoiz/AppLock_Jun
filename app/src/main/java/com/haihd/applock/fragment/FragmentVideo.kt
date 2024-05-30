package com.haihd.applock.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haihd.applock.activity.preview.PreviewVideoActivity
import com.haihd.applock.adapter.VideoHideAdapter
import com.haihd.applock.databinding.FragmentVideoBinding
import com.haihd.applock.item.ItemVideoHide
import com.haihd.applock.key.Vault
import com.haihd.applock.utils.CompareFile
import com.haihd.applock.viewmodel.HideVideoModel
import com.anhnt.baseproject.fragment.BaseFragment
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject
import java.io.File

class FragmentVideo : BaseFragment<FragmentVideoBinding>() {
    private lateinit var adapterVideoHide: VideoHideAdapter
    private var listIVideoHide = mutableListOf<ItemVideoHide>()
    private val viewModel by inject<HideVideoModel>()
    private val list = Hawk.get(Vault.KEY_FILE_NAME_VIDEO, mutableListOf<ItemVideoHide>())

    override fun initView() {
        adapterVideoHide = VideoHideAdapter(requireContext(), listIVideoHide)
        binding.rclVideoHide.adapter = adapterVideoHide
    }

    override fun initData() {

        //list first
        viewModel.listVideoHide.observe(this) {
            if (it!=null) {
                listIVideoHide.clear()
                listIVideoHide.addAll(it)
                adapterVideoHide.notifyDataSetChanged()
            } else {
                if (listIVideoHide.isNotEmpty()) {
                    binding.animationView.visibility = View.INVISIBLE
                } else {
                    binding.animationView.visibility = View.VISIBLE
                }
            }

        }
        viewModel.videoHideSelected.observe(this) {
            if (it!=null) {
                for (i in it) {
                    if (!i.decryptPath.isNullOrEmpty()) {
                        if (listIVideoHide.size > 0) {
                            if (!CompareFile.imagesAreEqual(
                                    File(i.decryptPath!!),
                                    File(listIVideoHide[0].decryptPath!!)
                                )
                            ) {
                                listIVideoHide.add(i)
                                adapterVideoHide.notifyItemInserted(listIVideoHide.size - 1)
                            }
                        } else {
                            listIVideoHide.add(i)
                            adapterVideoHide.notifyItemInserted(listIVideoHide.size - 1)
                        }
                    }
                    if (listIVideoHide.isEmpty()) {
                        binding.animationView.visibility = View.VISIBLE
                    } else {
                        binding.animationView.visibility = View.INVISIBLE
                    }
                }
                viewModel.videoHideSelected.value = null
            }
        }

        if (list.isNotEmpty()) {
            binding.animationView.visibility = View.INVISIBLE
        } else {
            binding.animationView.visibility = View.VISIBLE
            viewModel.videoHideSelected.value = null
        }
        //
        viewModel.recoverVideo.observe(this) {
//            // ads
//            EzAdControl.getInstance(activity as ActivityVault).showAds()
            //
            for (item in listIVideoHide) {
                if (item.thumbnailPath==it) {
                    val pos = listIVideoHide.indexOf(item)
                    listIVideoHide.remove(item)
                    adapterVideoHide.notifyItemRemoved(pos)
                    if (pos > 0) {
                        adapterVideoHide.notifyItemChanged(pos - 1)
                    }
                    break
                }
            }
            if (listIVideoHide.isEmpty()) {
                binding.animationView.visibility = View.VISIBLE
            } else {
                binding.animationView.visibility = View.INVISIBLE
            }
        }
    }

    override fun initListener() {
        adapterVideoHide.listenerOnClick = {
            startActivity(
                Intent(
                    requireContext(),
                    PreviewVideoActivity::class.java
                ).apply {
                    putExtra(Vault.KEY_VIDEO_DECRYPT_PATH, listIVideoHide[it].decryptPath)
                    putExtra(Vault.KEY_VIDEO_THUMBNAIL_PATH, listIVideoHide[it].thumbnailPath)
                }
            )
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVideoBinding {
        return FragmentVideoBinding.inflate(inflater, container, false)
    }

    override fun onStart() {
        super.onStart()
//        viewModel.decryptVideo(list, requireContext())
    }
}