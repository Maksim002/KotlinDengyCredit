package com.example.kotlincashloan.extension
import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner

fun banPressed(owner: LifecycleOwner) {
    FragmentActivity().onBackPressedDispatcher.addCallback(owner) {
        // With blank your fragment BackPressed will be disabled.
    }
}