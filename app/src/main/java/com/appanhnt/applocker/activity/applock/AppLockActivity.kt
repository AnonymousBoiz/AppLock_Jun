package com.appanhnt.applocker.activity.applock

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.appanhnt.applocker.R
import com.appanhnt.applocker.adapter.AppLockViewPagerAdapter
import com.appanhnt.applocker.databinding.ActivityAppLockBinding
import com.appanhnt.applocker.viewmodel.AppLockViewModel
import com.appanhnt.applocker.viewmodel.NotifyViewModel
import com.anhnt.baseproject.activity.BaseActivity
import com.anhnt.baseproject.extensions.getHeightStatusBar
import com.anhnt.baseproject.utils.PreferencesUtils
import com.appanhnt.applocker.BuildConfig
import com.appanhnt.applocker.adapter.AppLockAdapter
import com.appanhnt.applocker.item.ItemAppLock
import com.appanhnt.applocker.key.KeyApp
import com.lutech.ads.AdsManager
import com.lutech.ads.banner.BannerPlugin
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinApiExtension


class AppLockActivity : BaseActivity<ActivityAppLockBinding>() {
    companion object {
        var isNotifyPermission = false
    }

    private var listApp: MutableList<ItemAppLock> = mutableListOf()
    private var listSearch: MutableList<ItemAppLock> = mutableListOf()
    private var adapterApp: AppLockAdapter? = null
    private val viewModel by inject<AppLockViewModel>()
    private val viewNotifyModel by inject<NotifyViewModel>()
    private val viewModelLock by inject<AppLockViewModel>()

    override fun initView() {
        //
        //translucentStatus
        setStatusBarHomeTransparent(this)
        binding.applock.setPadding(0, getHeightStatusBar(), 0, 0)
        adapterApp = AppLockAdapter(this, listApp)
//        binding.rclApp.adapter = adapterApp

//
        val adapter = AppLockViewPagerAdapter(supportFragmentManager, this)
        binding.viewpager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)

        for (i in 0 until adapter.count) {
            val tabItemView: View =
                LayoutInflater.from(this).inflate(R.layout.layout_tab_item_app_lock, null)
            val tabItemText = tabItemView.findViewById<TextView>(R.id.title)
            tabItemText.text = adapter.getPageTitle(i)
            binding.tabLayout.getTabAt(i)?.setCustomView(tabItemView)
        }
        binding.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#3692FF"))
        binding.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                for (i in 0..adapter.count) {
                    binding.tabLayout.getTabAt(i)?.view?.findViewById<TextView>(R.id.title)
                        ?.setTextColor(Color.parseColor("#80FFFFFF"))
                }
                binding.tabLayout.getTabAt(position)?.view?.findViewById<TextView>(R.id.title)
                    ?.setTextColor(Color.parseColor("#FFFFFF"))
                if (position != 0 && viewModel.listListItemAppLock[position - 1].list.isEmpty()) {
                    binding.viewpager.visibility = View.GONE
                    binding.txtEmpty.visibility = View.VISIBLE

                } else {
                    binding.viewpager.visibility = View.VISIBLE
                    binding.txtEmpty.visibility = View.GONE
                }

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        binding.tabLayout.getTabAt(0)?.view?.findViewById<TextView>(R.id.title)
            ?.setTextColor(Color.parseColor("#FFFFFF"))

        AdsManager.loadCollapseBannerAds(this, binding.frAdcontainer, getString(R.string.applock_collapsible_banner_id))
    }

    fun showIntersAds(intent: Intent){
        showAds(intent)
    }

    @KoinApiExtension
    override fun initData() {
        isNotifyPermission = false
        // show app
        viewModel.listAppLiveData.observe(this) {
            addDataApp(it.toMutableList())

        }
//        viewModel.loadApp(this)
    }

    override fun viewBinding(): ActivityAppLockBinding {
        return ActivityAppLockBinding.inflate(LayoutInflater.from(this))
    }

    override fun initListener() {
        adapterApp?.listenerClickItem = {
            if (it >= 0) {
                if (listApp[it].packageName == "null") {
                    if (checkPermission()) {
                        viewNotifyModel.isLockNotification.postValue(!listApp[it].isLocked)
                        PreferencesUtils.putBoolean(
                            KeyApp.LOCK_NOTIFICATION, !listApp[it].isLocked
                        )
                    }
                } else {
                    if (listApp[it].packageName == "com.google.android.packageinstaller") {
                        PreferencesUtils.putBoolean(
                            KeyApp.LOCK_UNINSTALL, !listApp[it].isLocked
                        )
                    }
                }
                listApp[it].isLocked = !listApp[it].isLocked
                adapterApp?.notifyItemChanged(it)
                // view model update change UI app main lock
                viewModel.changeAppLock.value = (true)
                changeListAppLocked()
            }
        }
        // back
        binding.icBackLockApp.setOnClickListener {
            showAds(isBackScreen = true)
        }
        // search
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                var check = false;
                if (s.toString().isNotEmpty()) {
                    for (item in listSearch) {
                        if (item.name.contains(s.toString(), true)) {
                            check = true;
                        }
                    }
                    if (check) {
                        binding.viewpager.visibility = View.VISIBLE
                        binding.txtEmpty.visibility = View.GONE
                    } else {
                        binding.viewpager.visibility = View.GONE
                        binding.txtEmpty.visibility = View.VISIBLE

                    }

                } else {
                    listApp.clear()
                    listApp.addAll(listSearch)
                    adapterApp?.notifyDataSetChanged()
                    binding.viewpager.visibility = View.VISIBLE
                    binding.txtEmpty.visibility = View.GONE
                }
                viewModel.textSearch.postValue(binding.search.text.toString())
            }

        })
    }

    private fun changeListAppLocked() {
        val lisAppLock = mutableListOf<String>() //
        for (itemAdvanced in listApp) {
            if (itemAdvanced.isLocked) {
                itemAdvanced.packageName?.let {
                    lisAppLock.add(it)
                }
            }
        } //
        Hawk.put(KeyApp.KEY_APP_LOCK, lisAppLock)
    }

    override fun onRestart() {
        super.onRestart()
        isNotifyPermission = false
    }

    private fun isNotificationServiceRunning(context: Context): Boolean {
        val contentResolver: ContentResolver = context.contentResolver
        val enabledNotificationListeners: String? =
            Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        val packageName: String = context.packageName
        return if (enabledNotificationListeners.isNullOrEmpty()) false else enabledNotificationListeners.contains(
            packageName
        )
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (!isNotificationServiceRunning(this)) {
                isNotifyPermission = true
                showExplanation(
                    getString(R.string.app_name),
                    getString(R.string.DESCRIBE_ACCESS_NOTIFICATION_POLICY), this
                )
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addDataApp(listAppLive: MutableList<ItemAppLock>) {
        listApp.clear()
        listSearch.clear()
        val listNormalUnLock = mutableListOf<ItemAppLock>()
        val listNormalLock = mutableListOf<ItemAppLock>()
        val listAdvancedUnLock = mutableListOf<ItemAppLock>()
        val listAdvancedLock = mutableListOf<ItemAppLock>()
        if (PreferencesUtils.getBoolean(KeyApp.LOCK_NOTIFICATION)) {
            addItemNotification(listAdvancedLock)
        } else {
            addItemNotification(listAdvancedUnLock)
        }
        if (PreferencesUtils.getBoolean(KeyApp.LOCK_UNINSTALL)) {
            addItemUninstall(listAdvancedLock)
        } else {
            addItemUninstall(listAdvancedUnLock)
        }
        for (itemApp in listAppLive) {
            if (itemApp.packageName == "com.android.settings" && itemApp.isLocked ||
                itemApp.packageName == "com.android.vending" && itemApp.isLocked
            ) {
                if (itemApp.name.isNotEmpty()) {
                    listAdvancedLock.add(itemApp)
                }
            } else if (itemApp.isLocked) {
                if (itemApp.name.isNotEmpty()) {
                    listNormalLock.add(itemApp)
                }
            }

        }
        //
        for (itemApp in listAppLive) {
            if (itemApp.packageName == "com.android.settings" && !itemApp.isLocked ||
                itemApp.packageName == "com.android.vending" && !itemApp.isLocked
            ) {
                if (itemApp.name.isNotEmpty()) {
                    listAdvancedUnLock.add(itemApp)
                }
            } else if (!itemApp.isLocked) {
                if (itemApp.name.isNotEmpty()) {
                    listNormalUnLock.add(itemApp)
                }
            }
        }
        //sort
        listAdvancedLock.sortWith { o1, o2 ->
            o1.name.toCharArray()[0].compareTo(o2.name.toCharArray()[0])
        }
        listAdvancedUnLock.sortWith { o1, o2 ->
            o1.name.toCharArray()[0].compareTo(o2.name.toCharArray()[0])
        }
        listNormalLock.sortWith { o1, o2 ->
            o1.name.toCharArray()[0].compareTo(o2.name.toCharArray()[0])
        }
        listNormalUnLock.sortWith(compareBy { it.name })
        listNormalUnLock.sortWith(compareBy { -it.isSys })

        listApp.apply {
            add(ItemAppLock(null, getString(R.string.advanced), null, false, null))
            addAll(listAdvancedLock)
            addAll(listAdvancedUnLock)
            add(ItemAppLock(null, getString(R.string.locked), null, false, null))
            addAll(listNormalLock)
            addAll(listNormalUnLock)
        }
        listSearch.addAll(listApp)
        adapterApp?.notifyDataSetChanged()
        changeListAppLocked()
    }

    private fun showExplanation(title: String, message: String, context: Context) {
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.allow) { _, _ ->
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
            .create()
        alertDialog.show()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addItemNotification(list: MutableList<ItemAppLock>) {
        list.add(
            ItemAppLock(
                resources.getDrawable(R.drawable.ic_notification),
                getString(R.string.notification_lock),
                getString(R.string.description_lock_noti),
                PreferencesUtils.getBoolean(KeyApp.LOCK_NOTIFICATION), "null"
            )
        )
        //
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addItemUninstall(list: MutableList<ItemAppLock>) {
        list.add(
            ItemAppLock(
                resources.getDrawable(R.drawable.ic_protection),
                getString(R.string.protection_lock),
                getString(R.string.description_protection_lock),
                PreferencesUtils.getBoolean(KeyApp.LOCK_UNINSTALL),
                "com.google.android.packageinstaller"
            )
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModelLock.changeAppLock.value = true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.textSearch.postValue("")
    }
}