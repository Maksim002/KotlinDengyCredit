package com.example.kotlincashloan.ui.notification

import android.app.*
import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService
import com.timelysoft.tsjdomcom.service.AppPreferences.dataKey
import com.timelysoft.tsjdomcom.service.AppPreferences.init
import java.util.*


class GcmBroadcastReceiver : BroadcastReceiver() {

    var ctx: Context? = null
    override fun onReceive(context: Context, intent: Intent) {
        init(context)
        // Explicitly specify that GcmIntentService will handle the intent.
        val i = Intent(".ui.notification.PushNotification")
        i.setClass(context, PushNotification::class.java)
        context.startService(i)

        val comp = ComponentName(context.packageName, PushNotification::class.java.name)
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