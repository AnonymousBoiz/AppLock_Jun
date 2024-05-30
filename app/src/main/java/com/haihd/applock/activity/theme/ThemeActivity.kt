package com.haihd.applock.activity.theme

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.Window
import com.haihd.applock.R
import com.haihd.applock.databinding.ActivityThemeBinding
import com.haihd.applock.dialog.DialogShowLockScreen
import com.haihd.applock.fragment.BackgroundFragment
import com.haihd.applock.fragment.LockThemeFragment
import com.haihd.applock.key.KeyTheme
import com.haihd.applock.viewmodel.ThemeViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.adapter.BasePagerAdapter
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.utils.PreferencesUtils

import com.yalantis.ucrop.UCrop
import org.koin.android.ext.android.inject
import java.io.FileNotFoundException
import java.io.IOException


class ThemeActivity : BaseActivity<ActivityThemeBinding>() {
    private var dialogShow: DialogShowLockScreen? = null
    private val viewModel by inject<ThemeViewModel>()
    private val fragmentLockScreen = BackgroundFragment()
    override fun initView() {
        // ads
//        EzAdControl.getInstance(this).showAds()
        //
        setStatusBarHomeTransparent(this)
        binding.theme.setPadding(0, getHeightStatusBar(), 0, 0)
        //

        val adapter = BasePagerAdapter(supportFragmentManager, 0).apply {
            addFragment(LockThemeFragment(), getString(R.string.LOCK_THEME))
            addFragment(fragmentLockScreen, getString(R.string.LOCK_BACKGROUND))
        }
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.setCurrentItem(0, true)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.icBackTheme.setOnClickListener {
            onBackPressed()
        }
    }

    override fun viewBinding(): ActivityThemeBinding {
        return ActivityThemeBinding.inflate(LayoutInflater.from(this))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            data?.let {
                val resultUri: Uri? = UCrop.getOutput(it)
                showDialogShow(resultUri)
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showDialogShow(uri: Uri?) {
        dialogShow = DialogShowLockScreen(this, uri, R.style.MyDialogTheme)
        dialogShow?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            show()
            listenerApply = {
//                change adapter rcl
                fragmentLockScreen.apply {

                    listPhotoScreen.forEachIndexed { index, itemLockScreen ->
                        if (itemLockScreen.folder == KeyTheme.BG_CUSTOM) {
                            PreferencesUtils.putString(
                                KeyTheme.KEY_BACKGROUND,
                                itemLockScreen.folder
                            )
                            itemLockScreen.isSelected = true
                            adapterLockScreen?.notifyItemChanged(
                                listPhotoScreen.indexOf(
                                    itemLockScreen
                                )
                            )
                        } else {

                            itemLockScreen.isSelected = false
                            adapterLockScreen?.notifyItemChanged(
                                listPhotoScreen.indexOf(
                                    itemLockScreen
                                )
                            )
                        }
                    }

                }
                try {
                    uri?.let {
                        val inputStream = contentResolver.openInputStream(it)
                        val dra = Drawable.createFromStream(inputStream, it.toString())
                        viewModel.customBackground.postValue(dra)
                    }
                } catch (e: FileNotFoundException) {
                    viewModel.customBackground.postValue(getDrawable(R.drawable.background_lock_app))
                }
                dialogShow?.dismiss()
                // ads

                val intent = Intent(this@ThemeActivity, ApplyThemeSuccessActivity::class.java)
                ApplyThemeSuccessActivity.drawable = getDrawableFromUri(uri)
                showAds(intent)
//                EzAdControl.getInstance(this@ThemeActivity).showAds()
//                launchActivity<ApplyThemeSuccessActivity> {
//                    ApplyThemeSuccessActivity.drawable = getDrawableFromUri(uri)
//                }
            }
        }
    }

    fun showInterAds(intent: Intent){
        showAds(intent)
    }

    fun getDrawableFromUri(uri: Uri?): Drawable? {
        try {
            val inputStream = contentResolver.openInputStream(uri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream!!.close()
            return BitmapDrawable(resources, bitmap)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}