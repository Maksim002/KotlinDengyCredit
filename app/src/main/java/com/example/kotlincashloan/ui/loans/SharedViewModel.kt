package com.example.kotlincashloan.ui.loans

import android.graphics.Bitmap
import androidx.collection.arrayMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.adapter.rxjava2.Result.response


class SharedViewModel: ViewModel() {
    val bitmapStore = MutableLiveData<HashMap<String, Bitmap>>()
    private lateinit var stringName: String

    fun getBitmaps() = bitmapStore

    fun updateBitmaps(bitmaps: HashMap<String, Bitmap>){
        bitmapStore.postValue(bitmaps)

    }
    fun updateKey(string: String){
        stringName = string
    }
}