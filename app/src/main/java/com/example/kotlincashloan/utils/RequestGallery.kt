package com.example.kotlincashloan.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.example.kotlincashloan.ui.support.ChatFragment
import java.io.File
import java.io.IOException

class RequestGallery(var activity: Activity){
    private val IMAGE_PICK_CODE = 10
    lateinit var intent: Intent

    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?, currentPhotoPath: String) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            if (data == null) {
                val rotatedBitmap: Bitmap?
                val imageBitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val nh = (imageBitmap.height * (512.0 / imageBitmap.width)).toInt()
                val scaled = Bitmap.createScaledBitmap(imageBitmap, 512, nh, true)
                val ei = ExifInterface(currentPhotoPath)
                val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                rotatedBitmap = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(scaled, 90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(scaled, 180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(scaled, 270F)
                    ExifInterface.ORIENTATION_NORMAL -> scaled
                    else -> scaled
                }
                ChatFragment().sendImage(rotatedBitmap!!)
            }else{
                val bm: Bitmap = MediaStore.Images.Media.getBitmap(activity.applicationContext.contentResolver, data.getData());
                ChatFragment().sendImage(bm)
            }
        }
    }

    //Делает картинку с камеры вертикальной
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}