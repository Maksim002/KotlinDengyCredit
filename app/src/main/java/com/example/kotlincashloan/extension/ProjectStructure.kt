package com.example.kotlincashloan.extension

import android.app.Activity
import android.graphics.Bitmap
import android.widget.ImageView
import com.example.kotlincashloan.ui.loans.SharedViewModel

fun getListsFifth(bitmap: HashMap<String, Bitmap>, id: ImageView, idIc: ImageView ,imageViewModel: SharedViewModel, activity: Activity, updateKey: String, idKey: String){
    if (bitmap[idKey] != null) {
        id.setImageBitmap(bitmap[idKey])
        imageViewModel.updateKey(updateKey)
        changeImage(idIc, true, activity)
    }
}
