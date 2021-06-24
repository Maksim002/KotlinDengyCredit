package com.example.kotlincashloan.extension

import android.app.Activity
import com.example.kotlincashloan.adapter.listener.GetApiListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.timelysoft.tsjdomcom.service.AppPreferences

private var recPosition = 0

fun getApi(activity: Activity, listener: GetApiListener) {
    val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    val configSettings = FirebaseRemoteConfigSettings.Builder().build()
    remoteConfig.setConfigSettingsAsync(configSettings);
    remoteConfig.fetch(0).addOnCompleteListener(OnCompleteListener<Void?> { task ->
        if (task.isSuccessful) {
            remoteConfig.fetchAndActivate()
            val urlApi = remoteConfig.getString("url_dev1")
            val tokenApi = remoteConfig.getString("token_dev")

            if (AppPreferences.urlApi != urlApi || AppPreferences.tokenApi != tokenApi) {
                if (urlApi != "" && tokenApi != "") {
                    AppPreferences.urlApi = urlApi
                    AppPreferences.tokenApi = tokenApi
                }
                listener.onClickListenerApi()
            }else if (AppPreferences.tokenApi == "" && AppPreferences.urlApi == "") {
                if (recPosition <= 5) {
                    getApi(activity, listener)
                    recPosition++
                } else {
                    listener.onClickListenerApi()
                }
            }else{
                listener.onClickListenerApi()
            }
        } else {
            if (AppPreferences.tokenApi != "" && AppPreferences.urlApi != ""){
                listener.onClickListenerApi()
            }else{
                listener.onClickListenerError()
            }
        }
    })
}