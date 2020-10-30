package com.example.kotlincashloan.utils

import android.app.Application
import com.timelysoft.tsjdomcom.service.AppPreferences.init
import com.timelysoft.tsjdomcom.service.AppPreferences.token

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        init(this)
        token = ""
    }
}