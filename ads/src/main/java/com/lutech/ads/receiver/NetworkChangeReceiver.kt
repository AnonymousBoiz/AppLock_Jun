package com.lutech.ads.receiver;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.lutech.ads.utils.Utils.isInternetAvailable


class NetworkChangeReceiver : BroadcastReceiver {
    private var mNetworkStateListener: NetworkStateListener? = null

    constructor() {}

    constructor(networkStateListener: NetworkStateListener?) {
        mNetworkStateListener = networkStateListener
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (context != null && intent != null) {
                val action = intent.action
                when (action) {
                    ConnectivityManager.CONNECTIVITY_ACTION -> if (isInternetAvailable(context)) {
                        mNetworkStateListener?.onInternetAvailable()
                    } else {
                        mNetworkStateListener?.onOffline()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface NetworkStateListener {
        fun onInternetAvailable()
        fun onOffline()
    }
}