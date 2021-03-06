package com.example.kotlincashloan.utils

import android.app.Activity
import android.os.CountDownTimer
import android.os.Handler
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.timelysoft.tsjdomcom.service.AppPreferences


class TimerListenerLoan(var activity: Activity){
    private lateinit var timer: CountDownTimer
    var numberContractions = 0

    init {
        try {
            timer = object : CountDownTimer(120000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
//                    Toast.makeText(activity.applicationContext, millisUntilFinished.toString() , Toast.LENGTH_LONG).show()
//                    Toast.makeText(activity.applicationContext, "seconds remaining: " + millisUntilFinished / 1000, Toast.LENGTH_LONG).show()
                }

                override fun onFinish() {
                    AppPreferences.token = ""
                    if (AppPreferences.resultPassword == ""){
                        AppPreferences.password = ""
                    }
                    HomeActivity.repeatedClick = 1
                    AppPreferences.isNumber = true
                    AppPreferences.isPinCode = true
                    activity.finish()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun timeStart() {
        try {
            timer.start()
        } catch (e: Exception) {

        }
    }

    fun timeStop() {
        try {
            timer.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}