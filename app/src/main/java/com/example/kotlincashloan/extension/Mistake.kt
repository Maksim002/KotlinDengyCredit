package com.example.kotlincashloan.extension


import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinscreenscanner.ui.login.fragment.MistakeBottomSheetFragment

val bottomSheetDialogFragment = MistakeBottomSheetFragment()

fun Activity.loadingMistake(activity: AppCompatActivity){
    try {
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
    } catch (e: Exception) {
        println()
    }
}


