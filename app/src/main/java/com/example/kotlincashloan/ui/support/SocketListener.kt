package com.example.kotlincashloan.ui.support

import android.app.Activity
import android.widget.Toast
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SocketListener(var activity: Activity):  WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        activity.runOnUiThread {
            Toast.makeText(activity.applicationContext, "Socket Connection Successful!", Toast.LENGTH_SHORT).show()
            initializeView()
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        activity.runOnUiThread {
            
        }
    }

    private fun initializeView() {
        
    }
}