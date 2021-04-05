package com.example.kotlincashloan.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

lateinit var bitmapIm: Bitmap

fun baseToBitmap(string: String){
    val imageBytes = Base64.decode(string, Base64.DEFAULT)
    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    val nh = (decodedImage.height * (512.0 / decodedImage.width)).toInt()
    bitmapIm = Bitmap.createScaledBitmap(decodedImage, 512, nh, true)
}