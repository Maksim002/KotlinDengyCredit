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
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotification : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val data: Map<String, String> = p0.data
        val questionTitle = data["action"].toString()

        showNotification(p0.notification?.title.toString(), p0.notification?.body.toString(), questionTitle)

    }

    private fun showNotification(
        title: String,
        message: String,
        isData: String
    ) {

        FirebaseMessaging.getInstance().subscribeToTopic("testnotification")

        val notChannelId = "com.beksar.testnotification"
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val c =
                NotificationChannel(
                    notChannelId,
                    "testnotification",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            c.description = "testnotification"
            c.enableLights(true)
            c.lightColor = Color.BLUE
            notificationManager.createNotificationChannel(c)
        }

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, HomeActivity::class.java)
        resultIntent.putExtra("dataKey", isData)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(this, notChannelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_baseline_arrow_forward_24)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .setContentText(message)
            .setOngoing(true)


        val manager = NotificationManagerCompat.from(this)
        manager.notify(1998, builder.build())
    }
}