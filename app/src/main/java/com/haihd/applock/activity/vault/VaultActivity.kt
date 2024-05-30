package com.haihd.applock.activity.vault

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.haihd.applock.adapter.MediaHideAdapter
import com.haihd.applock.item.ItemImageHide
import com.haihd.applock.item.ItemVideoHide
import com.haihd.applock.key.Vault
import com.haihd.applock.viewmodel.HideImageViewModel
import com.haihd.applock.viewmodel.HideVideoModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.utils.PreferencesUtils
import com.haihd.applock.R
import com.haihd.applock.activity.album.AlbumImageActivity
import com.haihd.applock.activity.album.AlbumVideoActivity
import com.haihd.applock.databinding.ActivityVaultBinding
import com.lutech.ads.AdsManager

import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject

class VaultActivity : BaseActivity<ActivityVaultBinding>() {
    private val photoViewModel by inject<HideImageViewModel>()
    private lateinit var adapterImageHide: MediaHideAdapter
    private var listMediaHide = mutableListOf<Any>()
    private val videoViewModel by inject<HideVideoModel>()

    override fun initView() {
        AdsManager.loadCollapseBannerAds(this, binding.frAdcontainer, getString(R.string.applock_collapsible_banner_id))

        setLocale(PreferencesUtils.getString(KEY_LANGUAGE, "en"))
        setStatusBarHomeTransparent(this)
        binding.vault.setPadding(0, getHeightStatusBar(), 0, 0)
        //
        adapterImageHide = MediaHideAdapter(this, this.listMediaHide)
        binding.rcv.adapter = adapterImageHide
    }

    override fun onStart() {
        super.onStart()
        setLocale(PreferencesUtils.getString(KEY_LANGUAGE, "en"))

    }
    override fun initData() {
        this.listMediaHide.clear()
        photoViewModel.listImageHide.observe(this) {
            if (it?.bitmap != null) {
                hideEmpty()
                this.listMediaHide.add(it)
                adapterImageHide.notifyItemInserted(this.listMediaHide.indexOf(it))
            } else {
                if (this.listMediaHide.isNotEmpty()) {
                    hideEmpty()
                } else {
                    showEmpty()
                }
            }
        }
        //
        val listImage = Hawk.get(Vault.KEY_FILE_NAME_IMAGE, mutableListOf<ItemImageHide>())
        if (!listImage.isNullOrEmpty()) {
            hideEmpty()
            photoViewModel.decryptListImage(listImage)
        } else {
            showEmpty()
        }

        photoViewModel.imageHideSelected.observe(this) {
            if (it != null) {
                for (i in it) {
                    if (i.bitmap != null) {
                        if (this.listMediaHide.size > 0) {
                            if (!checkSameBitmap(i.bitmap!!, this.listMediaHide)) {
                                this.listMediaHide.add(i)
                                adapterImageHide.notifyItemInserted(this.listMediaHide.size - 1)
                            }
                        } else {
                            this.listMediaHide.add(i)
                            adapterImageHide.notifyItemInserted(this.listMediaHide.size - 1)
                        }
                    }
                    if (this.listMediaHide.isEmpty()) {
                        showEmpty()
                    } else {
                        hideEmpty()
                    }
                }
                photoViewModel.imageHideSelected.value = null
            }
        }

        photoViewModel.recoverImage.observe(this) {
//            // ads
//            EzAdControl.getInstance(activity as ActivityVault).showAds()
            //
            for (item in this.listMediaHide) {
                item as ItemImageHide
                if (item.fileName == it.fileName) {
                    val pos = this.listMediaHide.indexOf(item)
                    this.listMediaHide.remove(item)
                    adapterImageHide.notifyItemRemoved(pos)
                    if (pos > 0) {
                        adapterImageHide.notifyItemChanged(pos - 1)
                    }
                    break
                }
            }
            if (this.listMediaHide.isEmpty()) {
                showEmpty()
            } else {
                hideEmpty()
            }
        }

//video
        videoViewModel.listVideoHide.observe(this) {
            if (it != null) {
                this.listMediaHide.addAll(it)
                adapterImageHide.notifyDataSetChanged()
            } else {
                if (this.listMediaHide.isNotEmpty()) {
                    hideEmpty()
                } else {
                    showEmpty()
                }
            }

        }
        videoViewModel.videoHideSelected.observe(this) {
            if (it != null) {
                for (i in it) {
                    if (!i.decryptPath.isNullOrEmpty()) {
                        if (this.listMediaHide.size > 0) {
                            this.listMediaHide.add(i)
                            adapterImageHide.notifyItemInserted(this.listMediaHide.size - 1)
                        } else {
                            this.listMediaHide.add(i)
                            adapterImageHide.notifyItemInserted(this.listMediaHide.size - 1)
                        }
                    }
                    if (this.listMediaHide.isEmpty()) {
                        showEmpty()
                    } else {
                        hideEmpty()
                    }
                }
                videoViewModel.videoHideSelected.value = null
            }
        }

        val listVideo = Hawk.get(Vault.KEY_FILE_NAME_VIDEO, mutableListOf<ItemVideoHide>())
        Log.d("ooooooooooooooo", listVideo.size.toString())
        if (listVideo.isNotEmpty()) {
            hideEmpty()
        } else {
            showEmpty()
            videoViewModel.videoHideSelected.value = null
        }

        videoViewModel.recoverVideo.observe(this) {
            for (item in this.listMediaHide) {
                item as ItemVideoHide
                if (item.thumbnailPath == it) {
                    val pos = this.listMediaHide.indexOf(item)
                    this.listMediaHide.remove(item)
                    adapterImageHide.notifyItemRemoved(pos)
                    if (pos > 0) {
                        adapterImageHide.notifyItemChanged(pos - 1)
                    }
                    break
                }
            }
            if (this.listMediaHide.isEmpty()) {
                showEmpty()
            } else {
                hideEmpty()
            }
        }
    }

    private fun showEmpty() {
        binding.ivEmpty.visibility = View.VISIBLE
        binding.ivTool.visibility = View.VISIBLE
        binding.tvContentEmpty.visibility = View.VISIBLE
    }

    private fun hideEmpty() {
        binding.ivEmpty.visibility = View.GONE
        binding.ivTool.visibility = View.GONE
        binding.tvContentEmpty.visibility = View.GONE
    }

    fun checkSameBitmap(b: Bitmap, list: MutableList<Any>): Boolean {
        for (i in list) {
            i as ItemImageHide
            if (b.sameAs(i.bitmap)) {
                return true
            }
        }
        return false
    }

    override fun initListener() {
        binding.icBackVault.setOnClickListener {
            onBackPressed()
        }
        //
        binding.icAdd.setOnClickListener {
            if (binding.tvImage.visibility == View.VISIBLE) {
                binding.tvImage.visibility = View.GONE
                binding.tvVideo.visibility = View.GONE
            } else {
                binding.tvImage.visibility = View.VISIBLE
                binding.tvVideo.visibility = View.VISIBLE
            }
//            when (binding.tabLayout.selectedTabPosition) {
//                0 -> {
//                    launchActivity<AlbumActivityImage> { }
//                }
//                1 -> {
//            launchActivity<AlbumVideoActivity> { }
//                }
//            }
        }
        binding.tvImage.setOnClickListener {
            val intent = Intent(this, AlbumImageActivity::class.java)
            showAds(intent)
//            launchActivity<AlbumImageActivity> { }
        }
        binding.tvVideo.setOnClickListener {
            val intent = Intent(this, AlbumVideoActivity::class.java)
            showAds(intent)
//            launchActivity<AlbumVideoActivity> {  }
        }
        //
        photoViewModel.isHide.observe(this) {
            if (it) {
                // ads
                showAds(null)
                photoViewModel.isHide.postValue(false)
            }
        }
    }

    override fun viewBinding(): ActivityVaultBinding {
        return ActivityVaultBinding.inflate(LayoutInflater.from(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        photoViewModel.listImageHide.postValue(null)
        val list = arrayListOf<ItemVideoHide>()
        for (item in listMediaHide) {
            if (item is ItemVideoHide) {
                list.add(item)
            }
        }
        videoViewModel.listVideoHide.postValue(list)
    }

}