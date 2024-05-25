package com.lutech.ads.extensions

import android.Manifest
import com.lutech.ads.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog


fun Context.onCreateDialog(
    dialog_update_version: Int,
    isCanceledOnTouchOutside: Boolean = true
): Dialog {
    val dialogRate = Dialog(this)
    dialogRate.setContentView(dialog_update_version)
    dialogRate.setCanceledOnTouchOutside(isCanceledOnTouchOutside)
    dialogRate.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    dialogRate.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialogRate
}

fun Context.onCreateBottomSheetDialog(
    dialog_update_version: Int,
    isCanceledOnTouchOutside: Boolean = true
): Dialog {
    val dialogRate = BottomSheetDialog(this, R.style.BottomSheetDialogStyle)
    dialogRate.setContentView(dialog_update_version)
    dialogRate.setCancelable(isCanceledOnTouchOutside)

    dialogRate.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    dialogRate.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialogRate
}

fun Context.shareApp(context: Context) {
    ShareCompat.IntentBuilder.from(context as Activity)
        .setType("text/plain")
        .setChooserTitle("Chooser title")
        .setText("http://play.google.com/store/apps/details?id=" + context.packageName)
        .startChooser()
}

fun Context.showNotice(msg: String)
        = Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()

fun Context.showNotice(msg: Int)
        = Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()

fun Context.getURLForResource(resourceId: Int): String {
    //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
    return Uri.parse("android.resource://$packageName/$resourceId").toString()

}

fun Fragment.getURLForResource(resourceId: Int): String {
    //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
    return "android.resource://${requireContext().packageName}/drawable/$resourceId"
}