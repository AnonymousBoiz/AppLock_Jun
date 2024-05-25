package com.lutech.ads.extensions

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lutech.ads.R
import com.lutech.ads.interstitial.InterstitialAdsManager
import com.lutech.ads.utils.Constants
import com.lutech.ads.utils.SharePref
import com.lutech.ads.utils.Utils
import kotlinx.android.synthetic.main.dialog_update_version.*
import kotlinx.android.synthetic.main.layout_dialog_rating_app.*

fun AppCompatActivity.showUpdateVersionDialog(versionCode: Int, showAds: () -> Unit){
    var isCloseApp  = true

    val mWarningDialog = onCreateDialog(R.layout.dialog_update_version, false)

    mWarningDialog.btnGotoStore.setOnClickListener {
        Utils.goToCHPlay(this)
    }

    mWarningDialog.btnContinue.setOnClickListener {
        if (isCloseApp) {
            finish()
        } else {
            showAds()
        }
    }
    mWarningDialog.btnDoLater.setOnClickListener {
        mWarningDialog.dismiss()
        showAds()
    }

    if (versionCode >= Constants.MINIMUM_VERSION_CODE) {
        if (versionCode < Constants.CURERENT_VERSION_CODE) {
            mWarningDialog.btnDoLater.visibility = View.VISIBLE
            isCloseApp= false
            InterstitialAdsManager.stopTimer()
            mWarningDialog.show()
        } else {
            showAds()
        }
    } else {
        InterstitialAdsManager.stopTimer()
        mWarningDialog.show()
    }
}

fun AppCompatActivity.showRatingDialog(){
    val mDialogRating = onCreateDialog(R.layout.layout_dialog_rating_app, false)
    var ratingNumber = 0
    mDialogRating.show()

    mDialogRating.apply {
        ivStar1.setOnClickListener {
            ratingNumber = 1
            handleRatingStar(mDialogRating,ratingNumber,applicationContext)
            resetStar(mDialogRating)
            ivStar1.setImageResource(R.drawable.ic_rating_enable)
        }

        ivStar2.setOnClickListener {
            ratingNumber = 2
            handleRatingStar(mDialogRating,ratingNumber,applicationContext)
            resetStar(mDialogRating)
            ivStar1.setImageResource(R.drawable.ic_rating_enable)
            ivStar2.setImageResource(R.drawable.ic_rating_enable)
        }

        ivStar3.setOnClickListener {
            ratingNumber = 3
            handleRatingStar(mDialogRating,ratingNumber,applicationContext)
            resetStar(mDialogRating)
            ivStar1.setImageResource(R.drawable.ic_rating_enable)
            ivStar2.setImageResource(R.drawable.ic_rating_enable)
            ivStar3.setImageResource(R.drawable.ic_rating_enable)
        }

        ivStar4.setOnClickListener {
            ratingNumber = 4
            handleRatingStar(mDialogRating,ratingNumber,applicationContext)
            ivStar1.setImageResource(R.drawable.ic_rating_enable)
            ivStar2.setImageResource(R.drawable.ic_rating_enable)
            ivStar3.setImageResource(R.drawable.ic_rating_enable)
            ivStar4.setImageResource(R.drawable.ic_rating_enable)
            ivStar5.setImageResource(R.drawable.ic_rating_disable)
        }
        ivStar5.setOnClickListener {
            ratingNumber = 5
            handleRatingStar(mDialogRating,ratingNumber,applicationContext)
            ivStar1.setImageResource(R.drawable.ic_rating_enable)
            ivStar2.setImageResource(R.drawable.ic_rating_enable)
            ivStar3.setImageResource(R.drawable.ic_rating_enable)
            ivStar4.setImageResource(R.drawable.ic_rating_enable)
            ivStar5.setImageResource(R.drawable.ic_rating_enable)
        }

    }

    mDialogRating.ratingbar.setOnRatingBarChangeListener { ratingBar, fl, b ->
        var title = ""
        var content = ""
        when (fl.toInt()) {
            1 -> {
                title = getString(R.string.txt_title_bad)
                content = getString(R.string.txt_content_bad)
                mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_1)
            }
            2 -> {
                title = getString(R.string.txt_title_bad)
                content = getString(R.string.txt_content_bad)
                mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_2)
            }
            3 -> {
                title = getString(R.string.txt_title_bad)
                content = getString(R.string.txt_content_bad)
                mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_3)
            }
            4 -> {
                title = getString(R.string.txt_title_good)
                content = getString(R.string.txt_content_good)
                mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_4)
            }
            5 -> {
                title = getString(R.string.txt_title_good)
                content = getString(R.string.txt_content_good)
                mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_5)
            }
        }
        mDialogRating.txtTitleDialogRating.text = title
        mDialogRating.txtDesDialogRating.text = content
        mDialogRating.btnRatingApp.visibility = View.VISIBLE
    }

    mDialogRating.btnRatingApp.setOnClickListener {
        if (mDialogRating.ratingbar.rating >= 4) {
            mDialogRating.dismiss()
            Utils.goToCHPlay(this)
        } else {
            SharePref.setIsRating(applicationContext)
            mDialogRating.dismiss()
            sendFeedback()
        }
    }

    mDialogRating.btnMaybeLater.setOnClickListener {
        mDialogRating.dismiss()
    }
}

private fun resetStar(mDialogRating: Dialog){
    mDialogRating.apply {
        val arrays = arrayOf(ivStar1, ivStar2,ivStar3,ivStar4,ivStar5)
        for (index  in arrays.indices) {
            val star = arrays[index]
            star.setImageResource(R.drawable.ic_rating_disable)
        }
    }
}

private fun handleRatingStar(mDialogRating: Dialog, ratingNumber: Int, context: Context){
    var title = ""
    var content = ""
    when (ratingNumber) {
        1 -> {
            title = context.getString(R.string.txt_title_bad)
            content = context.getString(R.string.txt_content_bad)
            mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_1)
        }
        2 -> {
            title = context.getString(R.string.txt_title_bad)
            content = context.getString(R.string.txt_content_bad)
            mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_2)
        }
        3 -> {
            title = context.getString(R.string.txt_title_bad)
            content = context.getString(R.string.txt_content_bad)
            mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_3)
        }
        4 -> {
            title = context.getString(R.string.txt_title_good)
            content = context.getString(R.string.txt_content_good)
            mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_4)
        }
        5 -> {
            title = context.getString(R.string.txt_title_good)
            content = context.getString(R.string.txt_content_good)
            mDialogRating.imgIconRate.setImageResource(R.drawable.ic_rate_5)
        }
    }
    mDialogRating.txtTitleDialogRating.text = title
    mDialogRating.txtDesDialogRating.text = content
    mDialogRating.btnRatingApp.visibility = View.VISIBLE
}

