package com.example.kotlincashloan.extension


import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.example.kotlinscreenscanner.ui.login.fragment.ConnectionBottomSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.MistakeBottomSheetFragment

val bottomSheetDialogFragment = MistakeBottomSheetFragment()
val connectionSheetDialogFragment = ConnectionBottomSheetFragment()

fun loadingMistake(activity: AppCompatActivity){
    try {
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
    } catch (e: Exception) {
        println()
    }
}

fun loadingConnection(activity: AppCompatActivity){
    try {
        connectionSheetDialogFragment.isCancelable = false;
        connectionSheetDialogFragment.show(activity.supportFragmentManager, connectionSheetDialogFragment.tag)
    } catch (e: Exception) {
        println()
    }
}


