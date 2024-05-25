package com.lutech.ads.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.AdjustEvent
import com.google.android.gms.ads.AdValue
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*


object Utils {


    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager == null) return true
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.state == NetworkInfo.State.CONNECTED
    }

    fun goToCHPlay(context: Context) {
        val appPackageName: String =
            context.packageName // getPackageName() from Context or Activity object
        var intent: Intent

        try {
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$appPackageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
        } catch (anfe: ActivityNotFoundException) {
            intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
        }
    }

    fun setLanguageForApp(context: Context) {
        val languageToLoad = SharePref.getIOSCountryData(context)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(Locale.forLanguageTag(languageToLoad)))
    }

    fun setTrackAdClickEvent(context: Context, ad_format: String, adUnitId: String = "", adUnitName: String = ""){
        val params = Bundle()
        if (adUnitId != "" && adUnitName != ""){
            params.putString("admob_ad_unit_id", adUnitId)
            params.putString("admob_ad_unit_name", adUnitName)
        }

        params.putString("admob_ad_format", ad_format)
        FirebaseAnalytics.getInstance(context).logEvent("admob_ad_click", params)
    }

    //Adjust
    fun setTrackAdRevenueByAdjust(revenue: Long, currency: String) {
        val adjustEventRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
        adjustEventRevenue.setRevenue((revenue / 1000000f).toDouble(), currency)
        Adjust.trackAdRevenue(adjustEventRevenue)
        setTrackRevenueByAdjust(Constants.AD_IMPRESSION, revenue, currency)
    }

    fun setTrackRevenueByAdjust(tokenEvent: String, revenue: Long, currency: String){
        val adjustEvent = AdjustEvent(tokenEvent)
        adjustEvent.setRevenue((revenue/1000000f).toDouble(), currency)
        Adjust.trackEvent(adjustEvent)
    }

    fun setTrackPaidAdEvent(context: Context, adValue: AdValue, adFormat: String) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.VALUE, (adValue.valueMicros / 1000000f).toString())
        params.putString(FirebaseAnalytics.Param.CURRENCY, adValue.currencyCode)

        params.putString("ad_format", adFormat)
//        params.putString("ab_test_name", adFormat)
//        params.putString("ab_test_variant", adFormat)

        FirebaseAnalytics.getInstance(context).logEvent("ad_revenue_sdk", params)
    }


    fun setTrackEventByAdjust(tokenEvent: String) {
        val adjustEvent = AdjustEvent(tokenEvent)
        Adjust.trackEvent(adjustEvent)
    }

    fun removeSpace(inputString: String): String {
        return inputString.replace("\\s|[^\\p{L}\\p{N}]".toRegex(), "")
    }
}

