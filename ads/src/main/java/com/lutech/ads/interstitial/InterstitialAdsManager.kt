package com.lutech.ads.interstitial

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lutech.ads.AdsManager
import com.lutech.ads.AdsManager.AdsType
import com.lutech.ads.AdsManager.IsReadyShowOpenAds
import com.lutech.ads.AdsManager.IsShowInterAds
import com.lutech.ads.AdsManager.IsShowLoadingAds
import com.lutech.ads.AdsManager.LastTimeShowAds
import com.lutech.ads.AdsManager.RuleInter
import com.lutech.ads.AdsManager.TYPE_ADS_INTER
import com.lutech.ads.AdsManager.getDistanceTimeShowAds
import com.lutech.ads.utils.Constants
import com.lutech.ads.R
import com.lutech.ads.billing.BillingClientSetup
import com.lutech.ads.utils.Utils


@SuppressLint("StaticFieldLeak")
object InterstitialAdsManager {
    private var mInterstitialAd: InterstitialAd? = null

    private var mAdsListener: AdsListener? = null

    private var mLoadFail = false

    private var mWaitLoading = false

    private var mActivity: Activity? = null

    private var mContext: Context? = null

    var mIsNotDismissSplashAds: Boolean = true

    var NumberOfClickButton: Int = 3

    var mCurrentNumberOfClickButton: Int = 0

    private val mTimeNextScreen = object : CountDownTimer(Constants.MAX_TIME_AT_SPLASH, 1000) {
        override fun onTick(p0: Long) {

        }

        override fun onFinish() {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(100)
            }
        }
    }

    private var mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 100) {
                mIsNotDismissSplashAds = false
                IsReadyShowOpenAds = true
                mWaitLoading = false
                mAdsListener?.onAdDismissed()
                mContext?.let {
                    loadAds(it)
                }
            }
        }
    }

    fun loadAds(context: Context) {

        mLoadFail = false

        mContext = context

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            context.getString(R.string.applock_inters_id),
            adRequest,
            object : InterstitialAdLoadCallback() {

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("1111122221", "onAdFailedToLoad")
                    mInterstitialAd = null
                    mLoadFail = true
                    if (mAdsListener != null && mWaitLoading && mIsNotDismissSplashAds) {
                        mAdsListener?.onAdDismissed()
                        stopTimer()
                    }
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("1111122221", "onAdLoaded" + (mActivity != null))
//                    Log.d("1111122221" , interstitialAd.getResponseInfo().getMediationAdapterClassName())
                    mInterstitialAd = interstitialAd
                    if (mWaitLoading && mActivity != null && mIsNotDismissSplashAds) {
                        mWaitLoading = false
                        mInterstitialAd?.setOnPaidEventListener {
                            Utils.setTrackAdRevenueByAdjust(it.valueMicros, it.currencyCode)
                            Utils.setTrackPaidAdEvent(context,it, Constants.INTER)
                        }
                        mInterstitialAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    mAdsListener?.onAdDismissed()
                                    LastTimeShowAds = System.currentTimeMillis() / 1000
                                    AdsType = TYPE_ADS_INTER
                                    mInterstitialAd = null
                                    IsReadyShowOpenAds = true
                                    loadAds(mActivity!!)
                                    mWaitLoading = false
                                }

                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                    super.onAdFailedToShowFullScreenContent(p0)
                                    mAdsListener?.onAdDismissed()
                                    IsReadyShowOpenAds = true
                                    stopTimer()
                                    loadAds(mActivity!!)
                                }

                                override fun onAdShowedFullScreenContent() {
                                    Log.d("1111122221", "onAdShowedFullScreenContent")
                                    IsReadyShowOpenAds = false
                                    stopTimer()
                                    mInterstitialAd = null
                                }

                                override fun onAdClicked() {
                                    super.onAdClicked()
                                    AdsManager.currentAdClickEventCount++
                                    Utils.setTrackAdClickEvent(context, Constants.INTER)
                                }
                            }
                        mInterstitialAd?.show(mActivity!!)
                    }
                }
            })
    }

    fun showAds(activity: Activity, adsListener: AdsListener) {
        mIsNotDismissSplashAds = true
        val timeCurrent = System.currentTimeMillis() / 1000
        val timeShow = getDistanceTimeShowAds(true)
        if (!IsShowInterAds || BillingClientSetup.isUpgraded(activity) || AdsManager.isDebugMode || AdsManager.currentAdClickEventCount >= AdsManager.MaxAdClickEventCountForOneSessionPerUser) {
            adsListener?.onAdDismissed()
            return
        }
        when (RuleInter) {
            1 -> {
                // time
                if (timeCurrent - LastTimeShowAds < timeShow) {
                    adsListener?.onAdDismissed()
                } else {
                    showAdsIfConditionOk(activity, adsListener)
                }
            }
            2 -> {
                //click
                ++mCurrentNumberOfClickButton
                if (mCurrentNumberOfClickButton == 1 || mCurrentNumberOfClickButton == NumberOfClickButton + 1) {
                    mCurrentNumberOfClickButton = 1
                    showAdsIfConditionOk(activity, adsListener)
                } else {
                    adsListener?.onAdDismissed()
                }
            }
            3 -> {
                //both of that
                Log.d(
                    "5555555555666666",
                    "time oke:  " + (timeCurrent - LastTimeShowAds > timeShow) + "  click opke:  " + mCurrentNumberOfClickButton + "  " + NumberOfClickButton
                )
                ++mCurrentNumberOfClickButton
                if (timeCurrent - LastTimeShowAds > timeShow && (mCurrentNumberOfClickButton == 1 || mCurrentNumberOfClickButton >= NumberOfClickButton + 1)) {
                    mCurrentNumberOfClickButton = 1
                    showAdsIfConditionOk(activity, adsListener)
                } else {
                    adsListener?.onAdDismissed()
                }
            }
            else -> {
                // time
                if (timeCurrent - LastTimeShowAds < timeShow) {
                    adsListener?.onAdDismissed()
                } else {
                    showAdsIfConditionOk(activity, adsListener)
                }
            }
        }
    }

    private fun showAdsIfConditionOk(activity: Activity, adsListener: AdsListener) {
        mAdsListener = null
        mAdsListener = adsListener
        if (mLoadFail) {
            adsListener?.onAdDismissed()
        } else {
            if (mInterstitialAd != null) {
                if (IsShowLoadingAds) {
                    adsListener?.onWaitAds()
                }
                mInterstitialAd?.setOnPaidEventListener {
                    Utils.setTrackAdRevenueByAdjust(it.valueMicros, it.currencyCode)
                    Utils.setTrackPaidAdEvent(activity,it, Constants.INTER)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    mInterstitialAd?.show(activity)
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                mAdsListener?.onAdDismissed()
                                LastTimeShowAds = System.currentTimeMillis() / 1000
                                AdsType = TYPE_ADS_INTER
                                IsReadyShowOpenAds = true
                                mInterstitialAd = null
                                loadAds(activity)
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                                mAdsListener?.onAdDismissed()
                                IsReadyShowOpenAds = true
                                loadAds(activity)
                            }

                            override fun onAdShowedFullScreenContent() {
                                IsReadyShowOpenAds = false
                                mInterstitialAd = null
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                AdsManager.currentAdClickEventCount++
                                Utils.setTrackAdClickEvent(activity, Constants.INTER)
                            }
                        }
                }, 1000)
            } else {
                adsListener?.onAdDismissed()
            }
        }
    }

    fun showSplashAds(activity: Activity, adsListener: AdsListener) {
        mAdsListener = null
        mAdsListener = adsListener
        mTimeNextScreen.start()
        if (mLoadFail || !AdsManager.IsShowSplashAds|| !IsShowInterAds || BillingClientSetup.isUpgraded(activity) || AdsManager.isDebugMode) {
            mAdsListener?.onAdDismissed()
            stopTimer()
        } else {
            if (mInterstitialAd != null) {
                mInterstitialAd?.setOnPaidEventListener {
                    Utils.setTrackAdRevenueByAdjust(it.valueMicros, it.currencyCode)
                    Utils.setTrackPaidAdEvent(activity,it, Constants.INTER)
                }
                mInterstitialAd?.fullScreenContentCallback =
                    object : FullScreenContentCallback() {

                        override fun onAdDismissedFullScreenContent() {
                            Log.d("1111122221", "onAdDismissedFullScreenContent")
                            mAdsListener?.onAdDismissed()
                            LastTimeShowAds = System.currentTimeMillis() / 1000
                            AdsType = TYPE_ADS_INTER
                            IsReadyShowOpenAds = true
                            mInterstitialAd = null
                            loadAds(activity)
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            super.onAdFailedToShowFullScreenContent(p0)
                            Log.d("1111122221", "onAdFailedToShowFullScreenContent")
                            mAdsListener?.onAdDismissed()
                            stopTimer()
                            IsReadyShowOpenAds = true
                            loadAds(activity)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d("1111122221", "onAdShowedFullScreenContentSplashhhhhh")
                            IsReadyShowOpenAds = false
                            mInterstitialAd = null
                            stopTimer()
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            AdsManager.currentAdClickEventCount++
                            Utils.setTrackAdClickEvent(activity, Constants.INTER)
                        }
                    }
                mInterstitialAd?.show(activity)
            } else {
                mWaitLoading = true
                mActivity = activity
            }
        }
    }

    fun stopTimer() {
        mTimeNextScreen.cancel()
    }
}