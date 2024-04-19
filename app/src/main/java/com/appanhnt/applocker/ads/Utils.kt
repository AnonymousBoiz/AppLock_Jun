package com.appanhnt.applocker.ads

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.elyeproj.loaderviewlibrary.LoaderTextView
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


object Utils {

    fun loadNativeAds(context: Context, template: TemplateView, nativeIdAds: Int) {
        val adLoader = AdLoader.Builder(context, context.getString(nativeIdAds))
            .forNativeAd { nativeAd ->
                val styles: NativeTemplateStyle = NativeTemplateStyle.Builder().build()
                template.setStyles(styles)
                template.visibility = View.VISIBLE
                template.setNativeAd(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    template.visibility = View.GONE
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build()
            )
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    fun loadBannerAds(context: Context, mAdView: AdView) {
        val adRequest2 = AdRequest.Builder().build()
        mAdView.loadAd(adRequest2)

//        mAdView.setOnPaidEventListener {
//            setTrackRevenueByAdjust(it.valueMicros,it.currencyCode)
//        }

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                mAdView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                mAdView.visibility = View.GONE
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }

    private fun getAdSize(activity: Activity): AdSize {
        val displayMetrics = activity.resources.displayMetrics

        val adWidthPixels = displayMetrics.widthPixels.toFloat()

        val density = displayMetrics.density
        val adWidth = (adWidthPixels / density).toInt()

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

}