package com.example.kotlincashloan.extension

import android.os.Handler
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlinscreenscanner.ui.MainActivity

private val handler = Handler()

//остонавливает таймер
fun initSuspendTime(){
    handler.postDelayed(Runnable { // Do something after 5s = 500ms
        MainActivity.timer.timeStop()
        GetLoanActivity.timer.timeStop()
    }, 10000)
}

//остонавливает счотчик таймера handler
fun initClear(){
    handler.removeCallbacksAndMessages(null);
}