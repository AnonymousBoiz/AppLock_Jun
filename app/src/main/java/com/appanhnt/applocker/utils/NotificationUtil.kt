package com.appanhnt.applocker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.appanhnt.applocker.R
import com.appanhnt.applocker.activity.splash.SplashActivity

@RequiresApi(api = Build.VERSION_CODES.O)
object NotificationUtil {
    private const val NOTIFICATION_CHANNEL_ID = "10101"

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotification(mContext: Service, title: String?, message: String?) {
        val resultIntent = Intent(mContext, SplashActivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            112 /* Request code */,
            resultIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel =
            NotificationChannel(NOTIFICATION_CHANNEL_ID, "App lock background task ", importance)
        mNotificationManager.createNotificationChannel(notificationChannel)
        val mBuilder = NotificationCompat.Builder(mContext)
        mBuilder.setSmallIcon(R.drawable.notifylock)
        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
        mBuilder.setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(false)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(resultPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            mContext.startForeground(145, mBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        }else{
            mContext.startForeground(145, mBuilder.build())
        }
    }

    fun cancelNotification(mContext: Service) {
        val mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(145)
    }
}