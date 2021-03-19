package com.example.kotlincashloan.ui.loans

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel: ViewModel() {
    private val bitmapStore = MutableLiveData<HashMap<String, Bitmap>>()
    private lateinit var stringName: String

    fun getBitmaps() = bitmapStore

    fun updateBitmaps(bitmaps:HashMap<String, Bitmap>){
        bitmapStore.postValue(bitmaps)

    }
    fun updateKey(string: String){
        stringName = string
    }
}