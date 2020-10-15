package com.example.kotlincashloan.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kotlincashloan.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import org.json.JSONObject


class PushNotification : FirebaseMessagingService() {
    private val TAG = "FirebaseMessagingServic"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            try {
                val data = JSONObject(remoteMessage.data as Map<*, *>)
                val jsonMessage = data.getString("extra_information")
                Log.d(
                    TAG, """onMessageReceived:Extra Information: $jsonMessage""".trimIndent())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title //get title
            val message = remoteMessage.notification!!.body //get message
            val click_action = remoteMessage.notification!!.clickAction //get click_action
            Log.d(TAG, "Message Notification Title: $title")
            Log.d(TAG, "Message Notification Body: $message")
            Log.d(TAG, "Message Notification click_action: $click_action")
            showNotification(title!!, message!!, click_action!!)
        }
    }

    private fun showNotification(title: String, message: String, action: String) {

        FirebaseMessaging.getInstance().subscribeToTopic("kotlincashloan")

        val notChannelId = "com.example.kotlincashloan"
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val c = NotificationChannel(notChannelId, "kotlincashloan", NotificationManager.IMPORTANCE_DEFAULT)
            c.description = "kotlincashloan"
            c.enableLights(true)
            c.lightColor = Color.BLUE
            notificationManager.createNotificationChannel(c)
        }

        var intent: Intent? = null
        if (action != null){
            intent = Intent("com.example.kotlincashloan.ui.registration.login")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this, notChannelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);


        val manager = NotificationManagerCompat.from(this)
        manager.notify(1998, builder.build())

    }
}