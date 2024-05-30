package com.haihd.applock.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.haihd.applock.R
import com.haihd.applock.dialog.DialogRateApp
import com.haihd.applock.key.KeyApp.EMAIL
import com.haihd.applock.key.KeyApp.POLICY_URL
import com.haihd.applock.key.KeyApp.SUBJECT
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

object ActionUtils {

    fun openLink(c: Context, url: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url.replace("HTTPS", "https"))
            c.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(c, "No browser!", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendFeedback(context: Context) {
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(EMAIL))
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            context.getString(R.string.app_name) + " Feedback"
        )
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        emailIntent.selector = selectorIntent
//        context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send email using..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun showRateDialog(context: Activity, isFinish: Boolean, callback: (Boolean) -> Unit) {
        DialogRateApp(context, object: DialogRateApp.Callback{
            override fun onSubmit(data: String) {
                Toast.makeText(context, context.getString(R.string.thank_you), Toast.LENGTH_SHORT)
                    .show()
                SharedPrefs.setRated(context)
                if (isFinish) {
                    context.finishAffinity()
                }
                callback(true)
            }

            override fun onRate(starCount: Int) {
                rateInApp(context)
                SharedPrefs.setRated(context)
                callback(true)
            }
        }).show()
    }
    private fun rateInApp(context: Activity) {
        val manager: ReviewManager = ReviewManagerFactory.create(context)
        val request: Task<ReviewInfo> = manager.requestReviewFlow()
        request.addOnCompleteListener(OnCompleteListener<ReviewInfo?> { task: Task<ReviewInfo?> ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                val reviewInfo = task.result
                reviewInfo?.let { manager.launchReviewFlow(context, it) }
            }
        })
    }

    fun shareApp(context: Context) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = "https://play.google.com/store/apps/details?id=" + context.packageName
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(sharingIntent, "Share to"))
    }

    fun showPolicy(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(POLICY_URL)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}