package com.example.kotlincashloan.extension


import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincashloan.adapter.listener.ListenerApiInternet
import com.example.kotlincashloan.ui.loans.dialogue.ConnectionBottomLoanFragment
import com.example.kotlincashloan.ui.loans.dialogue.LoanBottomSheetFragment
import com.example.kotlincashloan.ui.registration.recovery.fragment.BottomDialogListener
import com.example.kotlinscreenscanner.ui.login.fragment.ConnectionApiSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.ConnectionBottomSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.MistakeBottomDialogFragment
import com.example.kotlinscreenscanner.ui.login.fragment.MistakeBottomSheetFragment


val bottomSheetBottomFragment = MistakeBottomDialogFragment()
val connectionSheetDialogFragment = ConnectionBottomSheetFragment()

fun loadingMistake(activity: AppCompatActivity, listener: BottomDialogListener? = null, string: String? = null){
    val bottomSheetDialogFragment = MistakeBottomSheetFragment(listener, string)
    try {
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
    } catch (e: Exception) {
        println()
    }
}

fun loadingMistakeCode(activity: AppCompatActivity){
    val bottomSheetDialogFragment = MistakeBottomSheetFragment()
    try {
        bottomSheetBottomFragment.isCancelable = false;
        bottomSheetBottomFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
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


fun loadingMistakeIm(activity: AppCompatActivity){
    val bottomSheetDialogFragment = LoanBottomSheetFragment()
    try {
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(activity.supportFragmentManager, bottomSheetDialogFragment.tag)
    } catch (e: Exception) {
        println()
    }
}

fun loadingConnectionIm(activity: AppCompatActivity){
    val connectionSheetDialogFragment = ConnectionBottomLoanFragment()
    try {
        connectionSheetDialogFragment.isCancelable = false;
        connectionSheetDialogFragment.show(activity.supportFragmentManager, connectionSheetDialogFragment.tag)
    } catch (e: Exception) {
        println()
    }
}

fun apiConnection(activity: AppCompatActivity, listener: ListenerApiInternet, boolean: Boolean? = null){
    val connectionSheetDialogFragment = ConnectionApiSheetFragment(listener)
    try {
        if (boolean == true){
            connectionSheetDialogFragment.dismiss()
        }else{
            connectionSheetDialogFragment.isCancelable = false;
            connectionSheetDialogFragment.show(activity.supportFragmentManager, connectionSheetDialogFragment.tag)
        }
    } catch (e: Exception) {
        println()
    }
}

