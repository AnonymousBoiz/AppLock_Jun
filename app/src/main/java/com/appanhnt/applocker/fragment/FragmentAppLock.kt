package com.appanhnt.applocker.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.appanhnt.applocker.R
import com.appanhnt.applocker.databinding.FragmentAppLockBinding
import com.appanhnt.applocker.viewmodel.AppLockViewModel
import com.appanhnt.applocker.viewmodel.NotifyViewModel
import com.anhnt.baseproject.extensions.launchActivity
import com.anhnt.baseproject.fragment.BaseFragment
import com.anhnt.baseproject.utils.PreferencesUtils
import com.appanhnt.applocker.activity.applock.AppLockActivity
import com.appanhnt.applocker.activity.applock.LockAppSuccessActivity
import com.appanhnt.applocker.adapter.AppLockAdapter
import com.appanhnt.applocker.dialog.DialogUnLockApp
import com.appanhnt.applocker.item.ItemAppLock
import com.appanhnt.applocker.key.KeyApp
import com.orhanobut.hawk.Hawk
import org.koin.android.ext.android.inject


class FragmentAppLock(val index: Int) : BaseFragment<FragmentAppLockBinding>() {
    private var listApp: MutableList<ItemAppLock> = mutableListOf()
    private val viewModel by inject<AppLockViewModel>()
    private val viewNotifyModel by inject<NotifyViewModel>()
    private val viewModelLock by inject<AppLockViewModel>()
    private var adapterApp: AppLockAdapter? = null

    override fun initView() {
        adapterApp = AppLockAdapter(requireContext(), listApp)
        binding.rcv.adapter = adapterApp
    }

    override fun initData() {
        AppLockActivity.isNotifyPermission = false
        // show app
        viewModel.listAppLiveData.observe(this) {
            if (index == 0) {
                addDataApp(viewModel.listAppAll)
            } else {
                addDataApp(viewModel.listListItemAppLock[index - 1].list)
            }
        }
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
                if (!listApp[it].isLocked) {
                    listApp[it].isLocked = !listApp[it].isLocked
                    val intent = Intent(requireContext(), LockAppSuccessActivity::class.java)
                    if (requireActivity() is AppLockActivity){
                        LockAppSuccessActivity.data = listApp[it]
                        (requireActivity() as AppLockActivity).showIntersAds(intent)
                    }
//                    requireActivity().launchActivity<LockAppSuccessActivity> {
//                        LockAppSuccessActivity.data = listApp[it]
//                    }
                    viewModel.currentItem.postValue(listApp[it])
                    // view model update change UI app main lock
                    viewModel.changeAppLock.value = (true)
                    changeListAppLocked()
                } else {
                    DialogUnLockApp(
                        requireContext(),
                        R.style.full_screen_dialog,
                        listApp[it],
                        object : DialogUnLockApp.Callback {
                            override fun unLock(data: ItemAppLock) {
                                listApp[it].isLocked = !listApp[it].isLocked
                                viewModel.currentItem.postValue(listApp[it])
                                // view model update change UI app main lock
                                viewModel.changeAppLock.value = (true)
                                changeListAppLocked()
                                val customToastView: View =
                                    layoutInflater.inflate(R.layout.custom_toast_layout, null)
                                customToastView.findViewById<TextView>(R.id.content).text =
                                    getString(R.string.unlock) + " “${data.name}” " + getString(R.string.successful)
                                val customToast = Toast(requireContext())
                                customToast.view = customToastView
                                customToast.duration = Toast.LENGTH_SHORT
                                customToast.setGravity(
                                    Gravity.BOTTOM,
                                    0,
                                    10
                                )
                                customToast.show()
                            }
                        }).show()
                }

            }
        }

        viewModel.currentItem.observe(
            this
        ) {
            if (adapterApp!!.list.contains(it)) {
                adapterApp?.list?.let { it1 ->
                    adapterApp!!.notifyItemChanged(it1.indexOf(it))
                }
            }
        }

        viewModel.textSearch.observe(this) {
            adapterApp?.search(it)
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (!isNotificationServiceRunning(requireContext())) {
                AppLockActivity.isNotifyPermission = true
                showExplanation(
                    getString(R.string.app_name),
                    getString(R.string.DESCRIBE_ACCESS_NOTIFICATION_POLICY), requireContext()
                )
                false
            } else {
                true
            }
        } else {
            true
        }
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

    @RequiresApi(Build.VERSION_CODES.M)
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
    private fun addDataApp(listAppLive: MutableList<ItemAppLock>) {
        listApp.clear()
        listApp.apply {
            addAll(listAppLive)
        }
        adapterApp?.notifyDataSetChanged()
//        changeListAppLocked()
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


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAppLockBinding {
        return FragmentAppLockBinding.inflate(inflater, container, false)
    }
}