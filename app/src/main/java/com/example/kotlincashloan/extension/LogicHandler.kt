package com.example.kotlincashloan.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.kotlincashloan.service.model.Loans.MyImageModel
import java.io.ByteArrayOutputStream

//Конвертироет в растовое изоброжение
lateinit var setBitmapIm: Bitmap
fun baseToBitmap(string: String){
    val imageBytes = Base64.decode(string, Base64.DEFAULT)
    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    val nh = (decodedImage.height * (512.0 / decodedImage.width)).toInt()
    setBitmapIm = Bitmap.createScaledBitmap(decodedImage, 512, nh, true)
}

//encode image to base64 string
lateinit var getBitmapIm: String
fun imageConverter(bitmap: Bitmap) {
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val imageBytes: ByteArray = baos.toByteArray()
    val image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
    getBitmapIm = image
}