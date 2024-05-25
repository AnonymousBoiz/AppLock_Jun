package com.lutech.ads.banner

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.lutech.ads.AdsManager
import com.lutech.ads.banner.RemoteConfigManager.BannerConfig.Companion.TYPE_ADAPTIVE
import com.lutech.ads.banner.RemoteConfigManager.BannerConfig.Companion.TYPE_COLLAPSIBLE_BOTTOM
import com.lutech.ads.banner.RemoteConfigManager.BannerConfig.Companion.TYPE_COLLAPSIBLE_TOP
import com.lutech.ads.banner.RemoteConfigManager.BannerConfig.Companion.TYPE_STANDARD
import com.lutech.ads.billing.BillingClientSetup
import com.lutech.ads.utils.SharePref

@SuppressLint("ViewConstructor")
class BannerPlugin(
    private val activity: Activity,
    private val adContainer: ViewGroup,
    private val config: Config
) {
    companion object {
        fun fetchAndActivateRemoteConfig() {
            RemoteConfigManager.fetchAndActivate()
        }

        private var LOG_ENABLED = true

        fun setLogEnabled(enabled: Boolean) {
            LOG_ENABLED = enabled
        }

        internal fun log(message: String) {
            if (LOG_ENABLED) {
                Log.d("BannerPlugin", message)
            }
        }
    }

    class Config {
        lateinit var defaultAdUnitId: String

        lateinit var defaultBannerType: BannerType

        /**
         * Remote config key to retrieve banner config data remotely
         * */
        var configKey: String? = null

        /**
         * Banner refresh rate, in seconds. Pub are recommended to disable auto refresh from dashboard
         * Most of the case this is used to refresh a collapsible banner manually
         * */
        var defaultRefreshRateSec: Int? = null

        /**
         * In seconds, indicate minimum time b/w 2 collapsible banner requests.
         * Only works with BannerType.CollapsibleTop or BannerType.CollapsibleBottom
         * If it is the time to send ad request but the duration to last request collapsible banner < cbFetchInterval,
         * Adaptive banner will be shown instead.
         * */
        var defaultCBFetchIntervalSec: Int = 180

        var loadAdAfterInit = true
    }

    enum class BannerType {
        Standard,
        Adaptive,
        CollapsibleTop,
        CollapsibleBottom
    }

    private var adView: BaseAdView? = null

    init {
        initViewAndConfig()

        if (AdsManager.IsShowCollapseAds && config.loadAdAfterInit && !BillingClientSetup.isUpgraded(activity)
            && AdsManager.currentAdClickEventCount < AdsManager.MaxAdClickEventCountForOneSessionPerUser) {
            loadAd()
        }else{
            adContainer.visibility = View.GONE
        }
    }

    private fun initViewAndConfig() {
        var adUnitId = config.defaultAdUnitId
        var bannerType = config.defaultBannerType
        var cbFetchIntervalSec = config.defaultCBFetchIntervalSec
        var refreshRateSec: Int? = config.defaultRefreshRateSec

        config.configKey?.also { key ->
            val bannerConfig = RemoteConfigManager.getBannerConfig(key)

            adUnitId = bannerConfig?.adUnitId ?: adUnitId
            bannerType = when (bannerConfig?.type) {
                TYPE_STANDARD -> BannerType.Standard
                TYPE_ADAPTIVE -> BannerType.Adaptive
                TYPE_COLLAPSIBLE_TOP -> BannerType.CollapsibleTop
                TYPE_COLLAPSIBLE_BOTTOM -> BannerType.CollapsibleBottom
                else -> bannerType
            }
            refreshRateSec = bannerConfig?.refreshRateSec ?: refreshRateSec
            cbFetchIntervalSec = bannerConfig?.cbFetchIntervalSec ?: cbFetchIntervalSec
        }

        log(
            "\n adUnitId = $adUnitId " +
                    "\n bannerType = $bannerType " +
                    "\n refreshRateSec = $refreshRateSec " +
                    "\n cbFetchIntervalSec = $cbFetchIntervalSec"
        )

        adView = BaseAdView.Factory.getAdView(
            activity,
            adUnitId,
            bannerType,
            refreshRateSec,
            cbFetchIntervalSec
        )
        adContainer.addView(
            adView,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        )
    }

    fun onAdFailedToLoad(){
        adContainer.visibility = View.GONE
    }

    fun loadAd() {
        adView?.loadAd{ onAdFailedToLoad() }
    }
}