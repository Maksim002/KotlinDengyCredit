package com.example.kotlincashloan.extension


import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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

fun Fragment.loadingMistake(activity: AppCompatActivity){
    try {
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
    } catch (e: Exception) {
        println()
    }
}


