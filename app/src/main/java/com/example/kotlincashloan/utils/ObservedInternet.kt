package com.example.kotlincashloan.utils

import android.content.Context
import android.net.ConnectivityManager
import com.timelysoft.tsjdomcom.service.AppPreferences

class ObservedInternet{
    fun observedInternet(context: Context){
        val cm =  context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        AppPreferences.observedInternet = isConnected
    }
}