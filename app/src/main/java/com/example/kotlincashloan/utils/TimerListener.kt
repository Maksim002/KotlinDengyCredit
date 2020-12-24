package com.example.kotlincashloan.utils

import android.app.Activity
import android.os.CountDownTimer
import com.timelysoft.tsjdomcom.service.AppPreferences


class TimerListener(var activity: Activity){
    private lateinit var timer: CountDownTimer
    var numberContractions = 0

    init {
        try {
            timer = object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
//                    Toast.makeText(activity.applicationContext, millisUntilFinished.toString() , Toast.LENGTH_LONG).show()
//                    Toast.makeText(activity.applicationContext, "seconds remaining: " + millisUntilFinished / 1000, Toast.LENGTH_LONG).show()
                }

                override fun onFinish() {
                    AppPreferences.token = ""
                    activity.finish()
                    AppPreferences.isNumber = true
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