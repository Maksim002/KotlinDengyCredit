package com.example.kotlincashloan.extension


import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinscreenscanner.ui.login.fragment.MistakeBottomSheetFragment

val bottomSheetDialogFragment = MistakeBottomSheetFragment()

fun loadingMistake(activity: AppCompatActivity){
    try {
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
    } catch (e: Exception) {
        println()
    }
}


