package com.lutech.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.elyeproj.loaderviewlibrary.LoaderTextView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.initialization.AdapterStatus
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.lutech.ads.billing.BillingClientSetup
import com.lutech.ads.interstitial.InterstitialAdsManager
import com.lutech.ads.nativead.NativeTemplateStyle
import com.lutech.ads.open.AppOpenManager
import com.lutech.ads.open.WelcomeBackActivity
import com.lutech.ads.reward.RewardAdsManager
import com.lutech.ads.rewardinterstitial.RewardInterstitialAdsManager
import com.lutech.ads.utils.Constants
import com.lutech.ads.utils.Utils
import java.util.concurrent.atomic.AtomicBoolean


@SuppressLint("StaticFieldLeak")
object AdsManager {
    // ads
    const val TYPE_ADS_OPEN = 0
    const val TYPE_ADS_INTER = 1

    lateinit var appOpenManager: AppOpenManager

    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    private var mContext: Context? = null

    var AdsType = 0
    var DistanceTimeShowSameAds = 2
    var DistanceTimeShowOtherAds = 3
    var LastTimeShowAds = 0L

    var IsReadyShowOpenAds = false
    var isUserOutApp = false

    var IsShowBannerAds = true
    var IsShowSplashAds = true
    var IsShowInterAds = true
    var IsShowOpenAds = true
    var IsShowCollapseAds = true
    var IsShowNativeAds = true
    var IsShowLoadingAds = true
    var IsShowNativeLanguageAds = true
    var IsShowRewardAds = true
    var IsShowAdsWhenClickButtonBack = false

    var RuleInter: Int = 1

    val isDebugMode = BuildConfig.DEBUG

    var MaxAdClickEventCountForOneSessionPerUser = 5
    var currentAdClickEventCount = 0

    fun isAppOpenManagerInitialized() = this::appOpenManager.isInitialized

    private fun resetVariablesValue(){
        currentAdClickEventCount = 0

        IsReadyShowOpenAds = false
        isMobileAdsInitializeCalled = AtomicBoolean(false)
    }

    private fun initAds(application: Application){
        mContext = application

        appOpenManager = AppOpenManager(application)

        if (!BillingClientSetup.isUpgraded(application)){
            MobileAds.initialize(
                application
            ) {
                val statusMap: Map<String, AdapterStatus> = it.getAdapterStatusMap()
                for (adapterClass in statusMap.keys) {
                    val status = statusMap[adapterClass]
                    Log.d("8888888888", String.format("Adapter name: %s, Description: %s, Latency: %d",
                        adapterClass, status!!.description, status.latency))
                }
                InterstitialAdsManager.loadAds(application)
                appOpenManager.fetchAd()
                RewardAdsManager.loadAds(application)
                RewardInterstitialAdsManager.loadAds(application)
            }
        }

//        loadAdsRemoteConfig()
    }

    fun requestConsentForm(activity: Activity, application: Application, onConsentLoaded: () -> Unit){
        // Set tag for under age of consent. false means users are not under age
        // of consent. )
        resetVariablesValue()
        val debugSettings = ConsentDebugSettings.Builder(activity)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//            .addTestDeviceHashedId("A13A84EBE813C078C632E98E992C1BBA")
            .build()

        val params = if (BuildConfig.DEBUG){
            ConsentRequestParameters
                .Builder()
                .setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build()
        }else{
            ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build()
        }

        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        if (BuildConfig.DEBUG){
            consentInformation.reset()
        }
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                // TODO: Load and show the consent form.
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity
                ) { loadAndShowError ->
                    // Consent gathering failed.
                    Log.w(
                        "6666666", String.format(
                            "%s: %s",
                            loadAndShowError?.errorCode,
                            loadAndShowError?.message
                        )
                    )

                    // Consent has been gathered.
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk(application, onConsentLoaded)
                    }
                }
            }, {
                    requestConsentError ->
                // Consent gathering failed.
                Log.w("6666666", String.format("%s: %s",
                    requestConsentError.errorCode,
                    requestConsentError.message
                ))
                onConsentLoaded()
            })

        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk(application, onConsentLoaded)
        }
    }

    private fun initializeMobileAdsSdk(application: Application, onConsentLoaded: () -> Unit) {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        Log.d("3333333333333","loadadss")
        // Initialize the Google Mobile Ads SDK.
        onConsentLoaded()
        initAds(application)
    }

    fun loadAdsRemoteConfig(){
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        IsShowSplashAds = mFirebaseRemoteConfig.getBoolean("is_show_splash_ads")
        DistanceTimeShowSameAds = mFirebaseRemoteConfig.getLong("distance_time_show_same_ads").toInt()
        DistanceTimeShowOtherAds = mFirebaseRemoteConfig.getLong("distance_time_show_other_ads").toInt()
        IsShowBannerAds = mFirebaseRemoteConfig.getBoolean("is_show_banner_ads")
        IsShowInterAds = mFirebaseRemoteConfig.getBoolean("is_show_inter_ads")
        IsShowOpenAds = mFirebaseRemoteConfig.getBoolean("is_show_open_ads")
        IsShowCollapseAds = mFirebaseRemoteConfig.getBoolean("is_show_collap_banner_ads")
        IsShowNativeLanguageAds = mFirebaseRemoteConfig.getBoolean("is_show_native_language")
        IsShowNativeAds = mFirebaseRemoteConfig.getBoolean("is_show_native_ads")
        IsShowLoadingAds = mFirebaseRemoteConfig.getBoolean("is_show_loading_ads")
        IsShowAdsWhenClickButtonBack = mFirebaseRemoteConfig.getBoolean("is_show_ads_when_click_button_back")
        IsShowRewardAds = mFirebaseRemoteConfig.getBoolean("is_show_reward_ads")

        InterstitialAdsManager.NumberOfClickButton = mFirebaseRemoteConfig.getLong("number_of_click_on_button").toInt()
        Constants.MAX_TIME_AT_SPLASH = mFirebaseRemoteConfig.getLong("max_time_at_splash")

        Constants.CURERENT_VERSION_CODE = mFirebaseRemoteConfig.getLong("current_version")
        Constants.MINIMUM_VERSION_CODE = mFirebaseRemoteConfig.getLong("minimum_version")

        RuleInter = mFirebaseRemoteConfig.getLong("rule_inter ").toInt()
        MaxAdClickEventCountForOneSessionPerUser = mFirebaseRemoteConfig.getLong("max_ad_click_event_count_for_one_session_per_user").toInt()
    }

    fun showWelcomeBackScreen(activity: Activity) {
        if (!IsShowOpenAds || !IsReadyShowOpenAds || BillingClientSetup.isUpgraded(activity) || isDebugMode) return
        Log.d(
            "9999999999",
            "show as=" + isUserOutApp + "__" + isShowOpenAds() + "__" + appOpenManager.isAdAvailable
        )
        if (isUserOutApp && isShowOpenAds() && appOpenManager.isAdAvailable) {
            val intent = Intent(activity, WelcomeBackActivity::class.java)
            activity.startActivity(intent)
            LastTimeShowAds = System.currentTimeMillis();
            isUserOutApp = false
        } else {
            isUserOutApp = false
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                isUserOutApp = true
            }
        })
    }

    fun loadNativeAds(
        context: Context,
        template: com.lutech.ads.nativead.TemplateView,
        nativeIdAds: Int,
        isShowNative: Boolean = IsShowNativeAds
    ) {
        if (isShowNative && !BillingClientSetup.isUpgraded(context)) {
            val adLoader = AdLoader.Builder(context, context.getString(nativeIdAds))
                .forNativeAd { nativeAd ->
                    val styles: NativeTemplateStyle = NativeTemplateStyle.Builder().build()

                    template.setStyles(styles)
                    template.visibility = View.VISIBLE
                    template.setNativeAd(nativeAd)
                    nativeAd.setOnPaidEventListener {
                        Utils.setTrackAdRevenueByAdjust(it.valueMicros, it.currencyCode)
                        Utils.setTrackPaidAdEvent(context,it, Constants.NATIVE)
                    }

                    template.isVisible = currentAdClickEventCount < MaxAdClickEventCountForOneSessionPerUser
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        template.visibility = View.GONE
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        currentAdClickEventCount++
                        template.isVisible = currentAdClickEventCount < MaxAdClickEventCountForOneSessionPerUser
                        val adUnitName = context.resources.getResourceEntryName(nativeIdAds)
                        Utils.setTrackAdClickEvent(context, Constants.NATIVE, context.getString(nativeIdAds), adUnitName)
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .build()
                )
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        } else {
            template.visibility = View.GONE
        }
    }

    fun loadBannerAds(activity: Activity, viewContainer: FrameLayout, bannerId: String) {
        val adView = AdView(activity)

        val loaderView = LoaderTextView(activity)
        loaderView.setTextColor(Color.WHITE)

        adView.setAdSize(getAdSize(activity))
        adView.adUnitId = bannerId

        if (IsShowBannerAds && !BillingClientSetup.isUpgraded(activity)) {
            viewContainer.removeAllViews()


            viewContainer.addView(adView)
            viewContainer.addView(loaderView)

            val adRequest2 = AdRequest.Builder().build()
            adView.loadAd(adRequest2)

            adView.setOnPaidEventListener {
                Utils.setTrackAdRevenueByAdjust(it.valueMicros,it.currencyCode)
                Utils.setTrackPaidAdEvent(activity,it, Constants.BANNER)
            }

            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    adView.visibility = View.VISIBLE
                    viewContainer.removeView(loaderView)
                    adView.isVisible = currentAdClickEventCount < MaxAdClickEventCountForOneSessionPerUser
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adView.visibility = View.GONE
                    viewContainer.visibility = View.GONE
                    // Code to be executed when an ad request fails.
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    currentAdClickEventCount++
                    adView.isVisible = currentAdClickEventCount < MaxAdClickEventCountForOneSessionPerUser
                    Utils.setTrackAdClickEvent(activity, Constants.BANNER)
                }
            }
        } else {
            adView.visibility = View.GONE
        }
    }

    private fun getAdSize(activity: Activity): AdSize {
        val displayMetrics = activity.resources.displayMetrics

        val adWidthPixels = displayMetrics.widthPixels.toFloat()

        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    fun loadCollapseBannerAds(activity: Activity, viewContainer: FrameLayout, bannerId: String) {
        val adView = AdView(activity)

        val loaderView = LoaderTextView(activity)
        loaderView.setTextColor(Color.WHITE)

        adView.setAdSize(getAdSize(activity))
        adView.adUnitId = bannerId

        if (IsShowCollapseAds && !BillingClientSetup.isUpgraded(activity)) {
            viewContainer.removeAllViews()


            viewContainer.addView(adView)
            viewContainer.addView(loaderView)

            val extras = Bundle()
            extras.putString("collapsible", "bottom")
            val adRequest: AdRequest = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()

            adView.loadAd(adRequest)

            adView.setOnPaidEventListener {
                Utils.setTrackAdRevenueByAdjust(it.valueMicros, it.currencyCode)
                Utils.setTrackPaidAdEvent(activity,it, Constants.COLLAPSIBLE_BANNER)
            }

            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    adView.visibility = View.VISIBLE

                    viewContainer.removeView(loaderView)

                    adView.isVisible = currentAdClickEventCount < MaxAdClickEventCountForOneSessionPerUser
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Code to be executed when an ad request fails.
                    adView.visibility = View.GONE
                    viewContainer.visibility = View.GONE
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    currentAdClickEventCount++
                    adView.isVisible = currentAdClickEventCount < MaxAdClickEventCountForOneSessionPerUser
                    Utils.setTrackAdClickEvent(activity, Constants.COLLAPSIBLE_BANNER)
                }
            }
        } else {
            loadBannerAds(activity,viewContainer, bannerId)
        }
    }


    private fun isShowOpenAds(): Boolean {
        val delta = System.currentTimeMillis() / 1000 - LastTimeShowAds
        val distance = getDistanceTimeShowAds(false)
        if (delta >= distance) {
            return true
        }
        return false
    }

    fun getDistanceTimeShowAds(isInterAds: Boolean): Int {
        return if ((isInterAds && AdsType == TYPE_ADS_INTER) || (!isInterAds && AdsType == TYPE_ADS_OPEN)) {
            DistanceTimeShowSameAds
        } else {
            DistanceTimeShowOtherAds
        }
    }
}