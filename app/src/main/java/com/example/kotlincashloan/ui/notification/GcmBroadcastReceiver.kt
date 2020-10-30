package com.example.kotlincashloan.ui.notification

import android.app.*
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.legacy.content.WakefulBroadcastReceiver
import com.example.kotlincashloan.R
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.kotlinscreenscanner.ui.login.NumberActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.AppPreferences.dataKey
import com.timelysoft.tsjdomcom.service.AppPreferences.init
import java.util.*

class GcmBroadcastReceiver : WakefulBroadcastReceiver() {
    var ctx: Context? = null
    override fun onReceive(context: Context, intent: Intent) {
        init(context)
        // Explicitly specify that GcmIntentService will handle the intent.
        val comp =
            ComponentName(context.packageName, PushNotification::class.java.name)
        // Start the service, keeping the device awake while it is launching.=
        ctx = context
        postNotification(Objects.requireNonNull(intent.extras)!!)
        startWakefulService(context, intent.setComponent(comp))
        resultCode = Activity.RESULT_OK
    }

    // post GCM message to notification center.
    private fun postNotification(data: Bundle) {
        val msg = data.getString("alert")
        Log.i(ContentValues.TAG, "message: $msg")
        if (msg != null) // on app startup, this was always getting called with empty message
            return
        dataKey = data.getString("action", "")
    }
}