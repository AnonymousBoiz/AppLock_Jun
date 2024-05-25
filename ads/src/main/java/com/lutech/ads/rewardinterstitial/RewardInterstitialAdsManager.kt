package com.lutech.ads.rewardinterstitial

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.lutech.ads.AdsManager
import com.lutech.ads.R
import com.lutech.ads.billing.BillingClientSetup
import com.lutech.ads.reward.RewardAdsListener
import com.lutech.ads.utils.Constants
import com.lutech.ads.utils.Utils


@SuppressLint("StaticFieldLeak")
object RewardInterstitialAdsManager {

    private var mRewardedAd: RewardedInterstitialAd? = null

    private var mRewardAdsListener: RewardAdsListener? = null

    private var mLoadFail = false

    private var mWaitLoading = false

    private var mActivity: Activity? = null

    private var mOnUserEarnedRewardListener: OnUserEarnedRewardListener? = null

    fun loadAds(context: Context) {

        mLoadFail = false

        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            context,
            context.getString(R.string.appname_reward_interstitial_ads_id),
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("1111122221", "onAdFailedToLoad")
                    mRewardedAd = null
                    mLoadFail = true
                    if (mRewardAdsListener != null && mWaitLoading) {
                        mRewardAdsListener?.onLoadFailOrShowFail()
                    }
                }

                override fun onAdLoaded(rewardedAd: RewardedInterstitialAd) {
                    Log.d("1111122221", "onAdLoaded" + (mActivity != null))
                    mRewardedAd = rewardedAd
                    if (mWaitLoading && mActivity != null && mOnUserEarnedRewardListener != null) {
                        mWaitLoading = false
                        mRewardedAd?.setOnPaidEventListener {
                            Utils.setTrackAdRevenueByAdjust(it.valueMicros, it.currencyCode)
                            Utils.setTrackPaidAdEvent(context,it, Constants.REWARD_INTER)
                        }
                        mRewardedAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
//                                    mRewardAdsListener?.onAdDismissed()
                                    mRewardedAd = null
                                    AdsManager.IsReadyShowOpenAds = true
                                    AdsManager.LastTimeShowAds = System.currentTimeMillis() / 1000
                                    loadAds(mActivity!!)
                                    mRewardAdsListener?.onRewardDismissed()
                                    mWaitLoading = false
                                }

                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                    super.onAdFailedToShowFullScreenContent(p0)
                                    Log.d("1111122221", "onAdFailedToShowFullScreenContent:   +${p0.message}")

                                    loadAds(mActivity!!)
                                    mRewardAdsListener?.onLoadFailOrShowFail()
                                    AdsManager.IsReadyShowOpenAds = true
                                }

                                override fun onAdShowedFullScreenContent() {
                                    Log.d("1111122221", "onAdShowedFullScreenContent")
                                    AdsManager.IsReadyShowOpenAds = false
                                    mRewardedAd = null
                                }

                                override fun onAdClicked() {
                                    super.onAdClicked()
                                    AdsManager.currentAdClickEventCount++
                                    Utils.setTrackAdClickEvent(context, Constants.REWARD_INTER)
                                }
                            }
                        mRewardedAd?.show(mActivity!!, mOnUserEarnedRewardListener!!)
                    }
                }
            })
    }

    fun showAds(activity: Activity, adsListener: RewardAdsListener, onUserEarnedRewardListener: OnUserEarnedRewardListener) {
        if(!AdsManager.IsShowRewardAds || BillingClientSetup.isUpgraded(activity) || AdsManager.isDebugMode || AdsManager.currentAdClickEventCount >= AdsManager.MaxAdClickEventCountForOneSessionPerUser) {
            adsListener?.onLoadFailOrShowFail()
            return
        }

        mOnUserEarnedRewardListener = onUserEarnedRewardListener
        mActivity = activity
        mRewardAdsListener = adsListener
        if (mLoadFail) {
            adsListener?.onLoadFailOrShowFail()
            loadAds(activity)
        } else {
            if (mRewardedAd != null) {
                mRewardedAd?.setOnPaidEventListener {
                    Utils.setTrackAdRevenueByAdjust(it.valueMicros, it.currencyCode)
                    Utils.setTrackPaidAdEvent(activity,it, Constants.REWARD_INTER)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    mRewardedAd?.show(activity, mOnUserEarnedRewardListener!!)
                    mRewardedAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
//                                adsListener?.onAdDismissed()
                                AdsManager.IsReadyShowOpenAds = true
                                Log.d("55555555555555", "onAdDismissedFullScreenContent")
                                mRewardedAd = null
                                AdsManager.LastTimeShowAds = System.currentTimeMillis() / 1000
                                loadAds(activity)
                                adsListener.onRewardDismissed()
                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                super.onAdFailedToShowFullScreenContent(p0)
                                Log.d("1111122221", "onAdFailedToShowFullScreenContent:   +${p0.message}")
                                loadAds(activity)
                                adsListener?.onLoadFailOrShowFail()
                                AdsManager.IsReadyShowOpenAds = true
                            }

                            override fun onAdShowedFullScreenContent() {
                                AdsManager.IsReadyShowOpenAds = false
                                Log.d("55555555555555", "onAdShowedFullScreenContent")
                                mRewardedAd = null
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                AdsManager.currentAdClickEventCount++
                                Utils.setTrackAdClickEvent(activity, Constants.REWARD_INTER)
                            }
                        }
                }, 1000)
            } else {
                adsListener?.onLoadFailOrShowFail()
                mWaitLoading = true
            }
        }
    }
}