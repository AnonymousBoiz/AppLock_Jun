package com.haihd.applock.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.haihd.applock.model.ItemNotificationModel
import com.anhnt.baseproject.viewmodel.BaseViewModel

class NotifyViewModel(application: Application) : BaseViewModel(application) {
        val listNotifyLiveData  : MutableLiveData<ItemNotificationModel> = MutableLiveData()
        val isLockNotification  : MutableLiveData<Boolean> = MutableLiveData()
}