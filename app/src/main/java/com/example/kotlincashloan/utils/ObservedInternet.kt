package com.example.kotlincashloan.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.timelysoft.tsjdomcom.service.AppPreferences
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class ObservedInternet{
    fun observedInternet(context: Context){
        val cm =  context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        AppPreferences.observedInternet = isConnected
    }
}