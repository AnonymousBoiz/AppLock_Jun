package com.haihd.applock.utils

import android.app.Notification
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
import com.haihd.applock.R
import com.haihd.applock.activity.splash.SplashActivity

@RequiresApi(api = Build.VERSION_CODES.O)
object NotificationUtil {
    private const val NOTIFICATION_CHANNEL_ID = "10101"

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotification(mContext: Service, title: String?, message: String?) {
        mContext.apply {
            val chan =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "App lock background task ", NotificationManager.IMPORTANCE_DEFAULT)

            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)


            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                startForeground(145, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            }else{
                startForeground(145, notification)
            }
        }
    }

    fun cancelNotification(mContext: Service) {
        val mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(145)
    }
}