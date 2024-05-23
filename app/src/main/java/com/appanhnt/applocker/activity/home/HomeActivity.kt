package com.appanhnt.applocker.activity.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import com.appanhnt.applocker.R
import com.appanhnt.applocker.activity.theme.ThemeActivity
import com.appanhnt.applocker.adapter.LinerMainAdapter
import com.appanhnt.applocker.dialog.DialogPermission
import com.appanhnt.applocker.item.ItemSpeedUp
import com.appanhnt.applocker.key.KeyLock
import com.appanhnt.applocker.service.BackgroundManager
import com.appanhnt.applocker.utils.AppUtil
import com.appanhnt.applocker.utils.RecycleViewUtils
import com.appanhnt.applocker.viewmodel.AppLockViewModel
import com.appanhnt.applocker.viewmodel.HideImageViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.anhnt.baseproject.view.rate.DialogRating
import com.anhnt.baseproject.view.rate.DialogRatingState
import com.appanhnt.applocker.activity.same.SameImageActivity
import com.appanhnt.applocker.activity.restore.RestoreActivity
import com.appanhnt.applocker.activity.intrusion.IntrusionAlertActivity
import com.appanhnt.applocker.activity.applock.AppLockActivity
import com.appanhnt.applocker.activity.clean.CleanActivity
import com.appanhnt.applocker.activity.icon.IconCamouflage2Activity
import com.appanhnt.applocker.activity.permission.GuidePermissionActivity
import com.appanhnt.applocker.activity.setting.SettingsActivity
import com.appanhnt.applocker.activity.vault.ActivityVault
import com.appanhnt.applocker.adapter.ProtectedAdapter
import com.appanhnt.applocker.ads.Utils
import com.appanhnt.applocker.databinding.ActivityHomeBinding
import com.appanhnt.applocker.item.ItemAppLock
import com.appanhnt.applocker.item.ItemGridMain
import com.appanhnt.applocker.key.KeyApp
import com.appanhnt.applocker.service.LockService
import com.google.android.gms.ads.ez.listenner.NativeAdListener
import com.google.android.gms.ads.ez.nativead.AdmobNativeAdView2
import com.orhanobut.hawk.Hawk
import com.tailoredapps.biometricauth.BiometricAuth
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.OnTargetListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.Circle
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension


class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    private val listGridMain = mutableListOf<ItemGridMain>()
    private val listLinerMain = mutableListOf<ItemSpeedUp>()
    private val listProtected = mutableListOf<ItemAppLock>()
    private lateinit var linerMainAdapter: LinerMainAdapter
    private lateinit var protectedAdapter: ProtectedAdapter
    private val viewModel by inject<HideImageViewModel>()
    private val viewModelLock by inject<AppLockViewModel>()
    private val mi = ActivityManager.MemoryInfo()
    private lateinit var activityManager: ActivityManager
    private var dialogPermission: DialogPermission? = null

    @OptIn(KoinApiExtension::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!BackgroundManager.checkPermission(this)) {
            dialogPermission = DialogPermission(
                this,
                R.style.full_screen_dialog,
                object : DialogPermission.Callback {
                    override fun onGoUsageAccess() {
                        showDialogPermission(false)
                    }

                    override fun onGoDisplayOverApp() {
                        showDialogPermission(true)
                    }

                    override fun onGoProtectApp() {
                        if (!BackgroundManager.isIgnoringBatteryOptimizations(this@HomeActivity)) {
                            requestIgnoreBatteryOptimize()
                        }
                    }

                    override fun onGoSettings() {
                        if (!BackgroundManager.isAccessGranted(this@HomeActivity)) {
                            showDialogPermission(false)
                        } else if (!Settings.canDrawOverlays(this@HomeActivity)){
                            showDialogPermission(true)
                        }else if (!BackgroundManager.isIgnoringBatteryOptimizations(this@HomeActivity)) {
                            requestIgnoreBatteryOptimize()
                        }
                    }

                })
            dialogPermission?.show()
        }
    }

    private fun requestIgnoreBatteryOptimize() {
        val intent =
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.setData(Uri.parse("package:$packageName"))
        startActivityForResult(
            intent,
            BackgroundManager.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        )
    }

    @OptIn(KoinApiExtension::class)
    override fun initView() {
        Utils.loadNativeAds(this, binding.myTemplate, R.string.applock_native_home_id)
        val px = resources.getDimensionPixelSize(R.dimen._15sdp)
        val pxs = resources.getDimensionPixelSize(R.dimen._10sdp)
        setStatusBarHomeTransparent(this)
        binding.container.setPadding(0, getHeightStatusBar(), 0, 0)
        //
//        binding.rclGid.layoutManager = object : GridLayoutManager(this, 2) {
//            override fun canScrollVertically(): Boolean {
//                return false
//            }
//
//            override fun canScrollHorizontally(): Boolean {
//                return false
//            }
//        }
        //
//        binding.rclLinear.layoutManager = object : LinearLayoutManager(this) {
//            override fun canScrollVertically(): Boolean {
//                return false
//            }
//
//            override fun canScrollHorizontally(): Boolean {
//                return false
//            }
//        }
        linerMainAdapter = LinerMainAdapter(this, listLinerMain)
//        binding.rclLinear.adapter = linerMainAdapter
        //
        protectedAdapter = ProtectedAdapter(this, listProtected)
        binding.rclProtected.adapter = protectedAdapter
        // clear animation rcl
        RecycleViewUtils.clearAnimation(binding.rclProtected)
        activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        //
        loadAdsNative()
        // intro view
        if (PreferencesUtils.getBoolean(KeyLock.FIRST_INTRO, true)) {
            PreferencesUtils.putBoolean(KeyLock.FIRST_INTRO, false)
            binding.rclProtected.post {
                initIntroView()
            }
        }

        binding.clLockTheme.setOnClickListener {
            launchActivity<ThemeActivity> {}
        }

        binding.clGallery.setOnClickListener {
//            LockService.isCreate = true // not lock when request any permission
            launchActivity<ActivityVault> {}

        }

        binding.clIcon.setOnClickListener {
        }

        binding.clFinger.setOnClickListener {

        }

        binding.btnIntrusionAlert.setOnClickListener {
            launchActivity<IntrusionAlertActivity> {}
        }

        binding.btnDrawer.setOnClickListener {
            launchActivity<SettingsActivity> {}
        }

        binding.clIcon.setOnClickListener {
            launchActivity<IconCamouflage2Activity> {}
        }

        checkFingerprint {
            when (it) {
                AppUtil.HAVE_SUPPORT -> {
                    binding.swichFinger.isChecked =
                        PreferencesUtils.getBoolean(KeyLock.LOCK_FINGER, false)
                }

                AppUtil.NO_ANY_FINGERPRINT -> {
                    binding.swichFinger.isChecked = false
                    //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
                }

                AppUtil.NOT_SUPPORT -> {
                    binding.swichFinger.isChecked = false
                    //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
                }
            }
        }
    }


    private fun initIntroView() {
        val firstRoot = FrameLayout(this)
        val first = layoutInflater.inflate(R.layout.layout_target, firstRoot)
        val firstTarget = Target.Builder()
            .setAnchor(binding.rclProtected)
            .setShape(Circle(100f))
            .setOverlay(first)
            .setOnTargetListener(object : OnTargetListener {
                override fun onStarted() {
                }

                override fun onEnded() {
                }
            })
            .build()
        // create spotlight
        val spotlight = Spotlight.Builder(this)
            .setTargets(mutableListOf(firstTarget))
            .setBackgroundColorRes(R.color.spotlightBackground)
            .setDuration(800L)
            .setAnimation(DecelerateInterpolator(2f))
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                }

                override fun onEnded() {
                }
            })
            .build()

        spotlight.start()

        first.findViewById<View>(R.id.btn_got_it).setOnClickListener {
            spotlight.finish()
        }
        first.findViewById<View>(R.id.layout_target).setOnClickListener {
            // to do some thing
        }
    }

    override fun viewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(LayoutInflater.from(this))
    }

    @KoinApiExtension
    override fun initListener() {
        binding.swichFinger.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkFingerprint {
                    when (it) {
                        AppUtil.HAVE_SUPPORT -> {
                            PreferencesUtils.putBoolean(KeyLock.LOCK_FINGER, true)
                        }

                        AppUtil.NO_ANY_FINGERPRINT -> {
                            binding.swichFinger.isChecked = false
                            //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
                            toast(getString(R.string.dont_have_fingerprint))
                        }

                        AppUtil.NOT_SUPPORT -> {
                            binding.swichFinger.isChecked = false
                            //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
                            toast(getString(R.string.can_not_use_finger_print))
                        }
                    }
                }
            } else {
                PreferencesUtils.putBoolean(KeyLock.LOCK_FINGER, false)
            }
        }
        //
        binding.backgroundLock.setOnClickListener {
            viewModelLock.loadApp(this)
            launchActivity<AppLockActivity> { }
        }
        //
//        gridMainAdapter.listenerCLick = {
//            when (it) {
//                getString(R.string.warning) -> {
//                    launchActivity<IntrusionAlertActivity> {}
//                }
//                getString(R.string.setting) -> {
//                    launchActivity<SettingsActivity> {}
//                }
//                getString(R.string.vault) -> {
//                    LockService.isCreate = true // not lock when request any permission
//                    requestPermission(
//                        complete = { complete ->
//                            if (complete) {
//                                launchActivity<ActivityVault> {}
//                            }
//                        },
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    )
//                }
//                getString(R.string.theme) -> {
//                    launchActivity<ThemeActivity> {}
//                }
//            }
//        }
        //
        linerMainAdapter.listenerOnClick = {
            when (listLinerMain[it].resId) {
                R.drawable.ic_data -> {
                    LockService.isCreate = true // not lock when request any permission
                    requestPermission(
                        complete = { complete ->
                            if (complete) {
                                launchActivity<CleanActivity> {}
                            }
                        },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }

                R.drawable.ic_storage -> {
                    LockService.isCreate = true // not lock when request any permission
                    requestPermission(
                        complete = { complete ->
                            if (complete) {
                                launchActivity<RestoreActivity> { }
                            }
                        },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }

                R.drawable.ic_delete_same_image -> {
                    LockService.isCreate = true // not lock when request any permission
                    requestPermission(
                        complete = { complete ->
                            if (complete) {
//                                fistData = true
                                viewModel.getImagesGallery(this, false)
                                launchActivity<SameImageActivity> { }
                            }
                        },
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
        }
        //
//        binding.layoutRate.buttonRate.setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data =
//                Uri.parse("market://details?id=com.ezmobi.applock")
//            startActivity(intent)
//        }
    }

    private fun checkFingerprint(state: (Int) -> Unit) {
        val biometricAuth = BiometricAuth.create(this, useAndroidXBiometricPrompt = false)
        if (!biometricAuth.hasFingerprintHardware) {
            state.invoke(AppUtil.NOT_SUPPORT)
//            //The devices does not support fingerprint authentication (i.e. has no fingerprint hardware)
        } else if (!biometricAuth.hasFingerprintsEnrolled) {
//            //The user has not enrolled any fingerprints (i.e. fingerprint authentication is not activated by the user)
            state.invoke(AppUtil.NO_ANY_FINGERPRINT)
        } else {
            state.invoke(AppUtil.HAVE_SUPPORT)
        }
    }

    private fun loadAdsNative() {
        AdmobNativeAdView2.getNativeAd(
            this,
            R.layout.native_admod_home, object : NativeAdListener() {
                override fun onError() {}
                override fun onLoaded(nativeAd: RelativeLayout?) {
                    nativeAd?.let {
                        if (it.parent != null) {
                            (it.parent as ViewGroup).removeView(it)
                        }
//                        binding.adsView.addView(it)
                    }
                }

                override fun onClickAd() {
//                TODO("Not yet implemented")
                }
            })
    }

    @KoinApiExtension
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initData() {
        LockService.isCreate = true
        //
        viewModelLock.changeAppLock.observe(this) {
            if (it) {
                getDataAppProtected()
            }
        }

        // liner
        listLinerMain.apply {
            add(
                ItemSpeedUp(
                    R.drawable.ic_data,
                    "#39A1FF",
                    getString(R.string.memory_capacity),
                    50,
                    null,
                    (mi.totalMem - mi.availMem).toDouble(),
                    mi.totalMem.toDouble(),
                    getString(R.string.speed_up)
                )
            )
            add(
                ItemSpeedUp(
                    R.drawable.ic_storage, "#FDD56F", getString(R.string.recovery),
                    50, getString(R.string.check_restore), 0.0, 0.0,
                    getString(R.string.check_now)
                )
            )
            add(
                ItemSpeedUp(
                    R.drawable.ic_delete_same_image,
                    "#FF7777",
                    getString(R.string.delete_same_photo),
                    50,
                    getString(R.string.content_photo),
                    0.0,
                    0.0,
                    getString(R.string.check_now)
                )
            )
        }
        // protected
        getDataAppProtected()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDataAppProtected() {
        listProtected.clear()
        val listPro = Hawk.get(KeyApp.KEY_APP_LOCK, mutableListOf<String>())
        if (!listPro.isNullOrEmpty()) {
            for (it in listPro) {
                if (listProtected.size < 3) {
                    try {
                        val ic = when (it) {
                            "com.google.android.packageinstaller" -> {
                                resources.getDrawable(R.drawable.ic_protection)
                            }

                            "null" -> {
                                resources.getDrawable(R.drawable.ic_notification)
                            }

                            else -> packageManager.getApplicationIcon(it)
                        }
                        listProtected.add(ItemAppLock(ic, "", null, true, null))
                    } catch (e: PackageManager.NameNotFoundException) {

                    }
                }
            }
        }

        listProtected.add(
            ItemAppLock(
                resources.getDrawable(R.drawable.ic_add_app),
                "add",
                null,
                true,
                null
            )
        )
        binding.txtNumberApp.text = listPro.size.toString()
        protectedAdapter.notifyDataSetChanged()
    }

    override fun onRestart() {
        super.onRestart()
        listLinerMain[0].apply {
            remainingMemory = (mi.totalMem - mi.availMem).toDouble()
            totalMemory = mi.totalMem.toDouble()
        }
        linerMainAdapter.notifyItemChanged(0)
    }

    override fun onBackPressed() {
        try {
            showAppRating(true) {
                finishAffinity()
            }
        }catch (e: Exception){
            finishAffinity()
        }

    }

    private fun showAppRating(isHardShow: Boolean, complete: (Boolean) -> Unit) {
        DialogRating.ExtendBuilder(this)
            .setHardShow(isHardShow)
            .setListener { status ->
                when (status) {
                    DialogRatingState.RATE_BAD -> {
                        toast(resources.getString(R.string.thank_for_rate))
                        complete(false)
                    }

                    DialogRatingState.RATE_GOOD -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("market://details?id=com.ezmobi.applock")
                        startActivity(intent)
                        complete(true)
                    }

                    DialogRatingState.COUNT_TIME -> complete(false)
                }
            }
            .build()
            .show()
    }

    @KoinApiExtension
    private fun showDialogPermission(isOverlay: Boolean) {
        if (isOverlay) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                overlayPermission()
        } else {
            BackgroundManager.openUsageStats(this)
        }
        Handler().postDelayed({
            launchActivity<GuidePermissionActivity> { }
        }, 200)

    }

    @KoinApiExtension
    @RequiresApi(Build.VERSION_CODES.M)
    private fun overlayPermission() {
        LockService.isRequestPermission = true
        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        myIntent.data = Uri.parse("package:${packageName}")
        startActivity(myIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BackgroundManager.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            if (BackgroundManager.isIgnoringBatteryOptimizations(this)) {
                dialogPermission?.grantedIgnoringBatteryOptimize()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.swichFinger.isChecked = PreferencesUtils.getBoolean(KeyLock.LOCK_FINGER, false)
        if (dialogPermission != null && dialogPermission!!.isShowing) {
            if (BackgroundManager.isAccessGranted(this)) {
                dialogPermission?.grantedUsageAccess()
            }
            if (Settings.canDrawOverlays(this)) {
                dialogPermission?.grantedOverLaysPms()
            }
        }

    }

}