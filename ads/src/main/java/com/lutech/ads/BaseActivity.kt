package com.lutech.ads

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.lutech.ads.AdsManager
import com.lutech.ads.dialog.LoadingAdsDialog
import com.lutech.ads.dialog.NoInternetDialog
import com.lutech.ads.extensions.hideBottomNavigationBar
import com.lutech.ads.interstitial.AdsListener
import com.lutech.ads.interstitial.InterstitialAdsManager
import com.lutech.ads.receiver.NetworkChangeReceiver
import com.lutech.ads.utils.Utils
import kotlinx.android.synthetic.main.layout_loading_ads.*


class BaseActivity : AppCompatActivity(), NetworkChangeReceiver.NetworkStateListener, AdsListener {
    private lateinit var mNoInternetDialog: NoInternetDialog

    private var mIntent: Intent? = null

    private var mIsBackScreen: Boolean = false

    private var mIsFinish: Boolean = false

    private lateinit var mBroadcastReceiver: NetworkChangeReceiver

    private lateinit var mLoadingAdsDialog: LoadingAdsDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        hideBottomNavigationBar()
//        Utils.setLanguageForApp(this)
        mNoInternetDialog =  NoInternetDialog.newInstance()
        mBroadcastReceiver =
            NetworkChangeReceiver(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            registerReceiver(mBroadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), RECEIVER_EXPORTED)
        }else{
            registerReceiver(mBroadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }

        mLoadingAdsDialog = LoadingAdsDialog.newInstance()
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) {
//            hideBottomNavigationBar()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        if(mBroadcastReceiver != null){
            unregisterReceiver(mBroadcastReceiver)
        }
    }

    protected fun showAds(intent: Intent? = null, isBackScreen: Boolean = false, isFinish: Boolean = false) {
        if(isBackScreen){
            mIsBackScreen = true
            if(AdsManager.IsShowAdsWhenClickButtonBack){
                InterstitialAdsManager.showAds(this, this)
            }else{
                finish()
            }
        }else{
            mIsFinish = isFinish
            mIntent = null
            mIntent = intent
            InterstitialAdsManager.showAds(this, this)
        }
    }

    override fun onInternetAvailable() {
        if (mNoInternetDialog != null) {
            mNoInternetDialog.dismiss()
        }
    }

    override fun onOffline() {
        if (mNoInternetDialog != null) {
            mNoInternetDialog.show(supportFragmentManager, NoInternetDialog.TAG)
        }
    }

    override fun onAdDismissed() {
        mLoadingAdsDialog.dismiss()
//        layoutLoadingAds.visibility = View.GONE
        if(mIsBackScreen){
            mIsBackScreen = false
            finish()
        }else{
            mIntent?.let {
                startActivity(it)
                if(mIsFinish) finish()
            }
        }
    }

    override fun onWaitAds() {
        mLoadingAdsDialog?.let {
            it.show(supportFragmentManager.beginTransaction().remove(it),LoadingAdsDialog.TAG)
        }
    }
}