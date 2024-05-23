package com.appanhnt.applocker.activity

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.appanhnt.applocker.interfaces.OnHomePressedListener


class HomeWatcher(private val mContext: Context) {
    private val mFilter: IntentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    private var mListener: OnHomePressedListener? = null
    private var mRecevier: InnerRecevier? = null
    fun setOnHomePressedListener(listener: OnHomePressedListener?) {
        mListener = listener
        mRecevier = InnerRecevier()
    }

    fun startWatch() {
        if (mRecevier != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                mContext.registerReceiver(mRecevier, mFilter, Service.RECEIVER_EXPORTED)
            }else{
                mContext.registerReceiver(mRecevier, mFilter)
            }

        }
    }

    fun stopWatch() {
        if (mRecevier != null) {
            mContext.unregisterReceiver(mRecevier)
        }
    }

    internal inner class InnerRecevier : BroadcastReceiver() {
        private val SYSTEM_DIALOG_REASON_KEY = "reason"
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (reason != null) {
                    if (mListener != null) {
                        mListener!!.onHomePressed()
                    }
                }
            }
        }
    }

}