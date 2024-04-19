package com.appanhnt.applocker.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appanhnt.applocker.R
import com.appanhnt.applocker.item.ItemAppLock
import com.appanhnt.applocker.key.KeyApp
import com.appanhnt.applocker.service.LockService
import com.appanhnt.applocker.utils.AppUtil
import com.anhnt.baseproject.viewmodel.BaseViewModel
import com.appanhnt.applocker.item.ItemListAppLock
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import kotlin.random.Random

class AppLockViewModel(application: Application) : BaseViewModel(application) {
    val listAppLiveData: MutableLiveData<MutableList<ItemAppLock>> = MutableLiveData()
    val listAppAll = arrayListOf<ItemAppLock>()
    val listListItemAppLock = arrayListOf(
        ItemListAppLock(application.getString(R.string.hot), arrayListOf()),
        ItemListAppLock(application.getString(R.string.system), arrayListOf()),
        ItemListAppLock(application.getString(R.string.social), arrayListOf()),
        ItemListAppLock(application.getString(R.string.payment), arrayListOf()),
        ItemListAppLock(application.getString(R.string.player), arrayListOf()),
        ItemListAppLock(application.getString(R.string.game), arrayListOf())
    )
    val currentItem: MutableLiveData<ItemAppLock> = MutableLiveData()
    val textSearch: MutableLiveData<String> = MutableLiveData()
    val listListItemAppLockAll = arrayListOf(
        ItemListAppLock(application.getString(R.string.all), arrayListOf())
    )
    var changeAppLock: MutableLiveData<Boolean> = MutableLiveData()

    @KoinApiExtension
    fun loadApp(context: Context, pkgNew: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val pkgAppsList: List<ResolveInfo> =
                context.packageManager.queryIntentActivities(mainIntent, 0)
            for (item in listListItemAppLock) {
                item.list.clear()
            }
            listAppAll.clear()
            getItemDetailApp(pkgAppsList, context, pkgNew)
        }
    }


    @KoinApiExtension
    private fun getItemDetailApp(
        pkgAppsList: List<ResolveInfo>,
        context: Context,
        pkgNew: String? = null
    ) {
        //
        val listApp = mutableListOf<ItemAppLock>()
//        AppLok.listApp.clear()
        val listAppLocked = Hawk.get(KeyApp.KEY_APP_LOCK, mutableListOf<String>())

        for (resolveInfo in pkgAppsList) {
            // get packageName
            try {
                val pkg = resolveInfo.activityInfo.packageName
                // check pkg system
                var isSys = false
                if (isSystemPackage(resolveInfo.activityInfo.applicationInfo)) {
                    Log.d("getItemDetailApp", "getItemDetailApp: $pkg ")
                    for (it in AppUtil.listPkgSettingFinal) {
                        if (pkg.contains(it)) {
                            isSys = true
                            break
                        }
                    }
                    if (!isSys) {
                        continue
                    }
                }
//                Log.e("isSystemPackage", .toString())
                // get icon

                val iconApp = context.packageManager.getApplicationIcon(pkg)
                // get name
                val name = context.packageManager.getApplicationLabel(
                    context.packageManager.getApplicationInfo(
                        pkg, PackageManager.GET_META_DATA
                    )
                )
                // check app locked
                var isLocked = false
                for (itemLocked in listAppLocked) {
                    if (itemLocked == pkg) {
                        isLocked = true
                    }
                }
                // check lock new app
                pkgNew?.let {
                    if (pkg == it) {
                        isLocked = true
                        listAppLocked.add(it)
                        Hawk.put(KeyApp.KEY_APP_LOCK, listAppLocked)
                    }
                }
                //add
                val item = ItemAppLock(
                    iconApp,
                    name.toString(),
                    context.getString(R.string.application),
                    isLocked,
                    pkg, if (isSys) 1 else 0
                )
                listApp.add(
                    item
                )
                listAppAll.add(item)
                listListItemAppLockAll[0].list.add(item)
                if (AppUtil.listPkgHotApp.contains(item.packageName)) {
                    listListItemAppLock[0].list.add(item)
                }
                if (isSys) {
                    listListItemAppLock[1].list.add(item)
                }
                if (AppUtil.listPkSocialApp.contains(item.packageName)) {
                    listListItemAppLock[2].list.add(item)
                }
                if (AppUtil.listPkPaymentApp.contains(item.packageName)) {
                    listListItemAppLock[3].list.add(item)
                }
                if (AppUtil.listPkPlayerApp.contains(item.packageName)) {
                    listListItemAppLock[4].list.add(item)
                }
                if (AppUtil.listPkGameApp.contains(item.packageName)) {
                    listListItemAppLock[5].list.add(item)
                }

            } catch (ex: PackageManager.NameNotFoundException) {
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            listAppLiveData.value = (listApp)
            LockService.listMyApp.clear()
            LockService.listMyApp.addAll(listApp)
            pkgNew?.let {
                changeAppLock.value = true
            }
        }
    }

    private fun isSystemPackage(applicationInfo: ApplicationInfo): Boolean {
        if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
            return true
        }
        return false
    }
}