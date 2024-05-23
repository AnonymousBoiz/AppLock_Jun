package com.appanhnt.applocker.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.appanhnt.applocker.item.ItemGroupIconCamouflage
import com.appanhnt.applocker.item.ItemIconCamouflage
import com.anhnt.baseproject.utils.PreferencesUtils
import com.anhnt.baseproject.viewmodel.BaseViewModel
import com.appanhnt.applocker.R
import com.appanhnt.applocker.key.KeyApp

class IconCamouflageViewModel(application: Application) : BaseViewModel(application) {
    val iconList = arrayListOf(
        ItemIconCamouflage(
            R.drawable.ic_default, "icon_0"
        ),
        ItemIconCamouflage(
            R.drawable.ic_calculator_1, "icon_1"
        ),
        ItemIconCamouflage(
            R.drawable.ic_calculator_2, "icon_2"
        ),
        ItemIconCamouflage(
            R.drawable.ic_calculator_3, "icon_3"
        ),
        ItemIconCamouflage(
            R.drawable.ic_calculator_4, "icon_4"
        ),
        ItemIconCamouflage(
            R.drawable.ic_office_reader_1, "icon_5"
        ),
        ItemIconCamouflage(
            R.drawable.ic_office_reader_2, "icon_6"
        ),
        ItemIconCamouflage(
            R.drawable.ic_office_reader_3, "icon_7"
        ),
        ItemIconCamouflage(
            R.drawable.ic_office_reader_4, "icon_8"
        ),
    )
    val iconGroupCamouflageList = arrayListOf(
        ItemGroupIconCamouflage(
            application.getString(R.string.txt_default),
            arrayListOf(
                iconList[0]
            )
        ),
        ItemGroupIconCamouflage(
            application.getString(R.string.txt_calculator),
            arrayListOf(
                iconList[1],
                iconList[2],
                iconList[3],
                iconList[4],
            )
        ),
        ItemGroupIconCamouflage(
            application.getString(R.string.txt_office),
            arrayListOf(
                iconList[5],
                iconList[6],
                iconList[7],
                iconList[8],
            )
        )
    )

    fun changeIconApp(context: Context, icon: ItemIconCamouflage) {
        for (item in iconList) {
            if (item == icon) {
                context.packageManager.setComponentEnabledSetting(
                    ComponentName(
                        context,
                        item.clazz
                    ), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
                )
                PreferencesUtils.putInteger(KeyApp.KEY_ICON, iconList.indexOf(item))
            } else {
                context.packageManager.setComponentEnabledSetting(
                    ComponentName(
                        context,
                        item.clazz
                    ), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}