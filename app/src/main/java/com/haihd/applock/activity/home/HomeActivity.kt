package com.haihd.applock.activity.home

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.haihd.applock.R
import com.haihd.applock.activity.theme.ThemeActivity
import com.haihd.applock.dialog.DialogPermission
import com.haihd.applock.item.ItemSpeedUp
import com.haihd.applock.key.KeyLock
import com.haihd.applock.service.BackgroundManager
import com.haihd.applock.utils.AppUtil
import com.haihd.applock.utils.RecycleViewUtils
import com.haihd.applock.viewmodel.AppLockViewModel
import com.haihd.applock.viewmodel.HideImageViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.utils.PreferencesUtils
import com.anhnt.baseproject.view.rate.DialogRating
import com.anhnt.baseproject.view.rate.DialogRatingState
import com.haihd.applock.activity.intrusion.IntrusionAlertActivity
import com.haihd.applock.activity.applock.AppLockActivity
import com.haihd.applock.activity.icon.IconCamouflage2Activity
import com.haihd.applock.activity.permission.GuidePermissionActivity
import com.haihd.applock.activity.setting.SettingsActivity
import com.haihd.applock.activity.vault.VaultActivity
import com.haihd.applock.adapter.ProtectedAdapter
import com.haihd.applock.databinding.ActivityHomeBinding
import com.haihd.applock.item.ItemAppLock
import com.haihd.applock.item.ItemGridMain
import com.haihd.applock.key.KeyApp
import com.haihd.applock.service.LockService
import com.lutech.ads.AdsManager
import com.lutech.ads.extensions.isNotificationPermissionGranted
import com.lutech.ads.extensions.requestNotificationPermission
import com.lutech.ads.utils.Constants
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

                    override fun onRequestNotification() {
                        requestNotificationPermission()
                    }

                    override fun onGoSettings() {
                        if (!BackgroundManager.isAccessGranted(this@HomeActivity)) {
                            showDialogPermission(false)
                        } else if (!Settings.canDrawOverlays(this@HomeActivity)){
                            showDialogPermission(true)
                        }else if (!BackgroundManager.isIgnoringBatteryOptimizations(this@HomeActivity)) {
                            requestIgnoreBatteryOptimize()
                        }else if (isNotificationPermissionGranted().not()){
                            requestNotificationPermission()
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
        AdsManager.loadNativeAds(this, binding.myTemplate, R.string.applock_native_home_id)
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
//        binding.rclLinear.adapter = linerMainAdapter
        //
        protectedAdapter = ProtectedAdapter(this, listProtected)
        binding.rclProtected.adapter = protectedAdapter
        // clear animation rcl
        RecycleViewUtils.clearAnimation(binding.rclProtected)
        activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        //
        // intro view
        if (PreferencesUtils.getBoolean(KeyLock.FIRST_INTRO, true)) {
            PreferencesUtils.putBoolean(KeyLock.FIRST_INTRO, false)
            binding.rclProtected.post {
                initIntroView()
            }
        }

        binding.clLockTheme.setOnClickListener {
            val intent = Intent(this, ThemeActivity::class.java)
            showAds(intent)
//            launchActivity<ThemeActivity> {}
        }

        binding.clGallery.setOnClickListener {
//            LockService.isCreate = true // not lock when request any permission
            val intent = Intent(this, VaultActivity::class.java)
            showAds(intent)
//            launchActivity<ActivityVault> {}

        }


        binding.clFinger.setOnClickListener {

        }

        binding.btnIntrusionAlert.setOnClickListener {
            val intent = Intent(this, IntrusionAlertActivity::class.java)
            showAds(intent)
//            launchActivity<IntrusionAlertActivity> {}
        }

        binding.btnDrawer.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            showAds(intent)
//            launchActivity<SettingsActivity> {}
        }

        binding.clIcon.setOnClickListener {
            val intent = Intent(this, IconCamouflage2Activity::class.java)
            showAds(intent)
//            launchActivity<IconCamouflage2Activity> {}
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
            val intent = Intent(this, AppLockActivity::class.java)
            showAds(intent)
//            launchActivity<AppLockActivity> { }
        }
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
            overlayPermission()
        } else {
            BackgroundManager.openUsageStats(this)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            launchActivity<GuidePermissionActivity> { }
        }, 200)
    }

    @KoinApiExtension
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.NOTIFICATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialogPermission?.grantedNotification()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AdsManager.isUserOutApp = false
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