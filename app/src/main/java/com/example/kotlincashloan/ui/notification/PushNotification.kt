package com.example.kotlincashloan.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kotlincashloan.R
import com.example.kotlinscreenscanner.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotification : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        showNotification(p0.notification?.title.toString(), p0.notification?.body.toString(), p0.notification!!.clickAction.toString())
    }

    private fun showNotification(
        title: String,
        message: String,
        clickAction: String
    ) {

        FirebaseMessaging.getInstance().subscribeToTopic("kotlincashloan")

        val notChannelId = "com.example.kotlincashloan"
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val c =
                NotificationChannel(
                    notChannelId,
                    "kotlincashloan",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            c.description = "kotlincashloan"
            c.enableLights(true)
            c.lightColor = Color.BLUE
            notificationManager.createNotificationChannel(c)

        }


        val resultIntent = Intent(this, MainActivity::class.java)
        clickAction
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(this, notChannelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_arrow)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .setContentText(message)
            .setOngoing(true)


        val manager = NotificationManagerCompat.from(this)
        manager.notify(1998, builder.build())
    }
}