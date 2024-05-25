package com.lutech.ads.banner

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

internal object RemoteConfigManager {

    private val gson by lazy { Gson() }

    fun fetchAndActivate() {
        FirebaseRemoteConfig.getInstance().fetchAndActivate()
    }

    fun getBannerConfig(key: String): BannerConfig? {
        return getConfig<BannerConfig>(key)
    }

    private inline fun <reified T> getConfig(configName: String): T? {
        return try {
            val data = FirebaseRemoteConfig.getInstance().getString(configName)
            gson.fromJson<T>(data, object : TypeToken<T>() {}.type)
        } catch (ignored: Throwable) {
            null
        }
    }

    data class BannerConfig(
        @SerializedName("ad_unit_id")
        val adUnitId: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("refresh_rate_sec")
        val refreshRateSec: Int?,
        @SerializedName("cb_fetch_interval_sec")
        val cbFetchIntervalSec: Int?
    ) {
        companion object {
            const val TYPE_STANDARD = "standard"
            const val TYPE_ADAPTIVE = "adaptive"
            const val TYPE_COLLAPSIBLE_TOP = "collapsible_top"
            const val TYPE_COLLAPSIBLE_BOTTOM = "collapsible_bottom"
        }
    }
}