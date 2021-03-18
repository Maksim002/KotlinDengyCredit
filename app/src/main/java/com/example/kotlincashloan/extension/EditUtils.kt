package com.example.kotlincashloan.extension

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.kotlincashloan.R

fun editUtils(textId: TextView, textHide: TextView, text: String, boolean: Boolean) {
    if (boolean){
        textId.setBackgroundResource(R.drawable.circle_orange_background_edit_red);
        textId.setTextColor(Color.RED)
        textId.setHintTextColor(Color.RED)
        textHide.text = text
        textHide.visibility = View.VISIBLE
        textHide.setTextColor(Color.RED)
    }else{
        textId.setBackgroundResource(R.drawable.circle_orange_background_grey);
        textId.setTextColor(Color.parseColor("#6A6868"))
        textId.setHintTextColor(Color.GRAY)
        textHide.visibility = View.GONE
    }
}

fun editUtils(textId: TextView, textHide: TextView, text: String, boolean: Boolean, viz: Int? = null) {
    if (boolean){
        textId.setBackgroundResource(R.drawable.circle_orange_background_edit_red);
        textId.setTextColor(Color.RED)
        textId.setHintTextColor(Color.RED)
        textHide.text = text
        textHide.visibility = View.VISIBLE
        textHide.setTextColor(Color.RED)
    }else{
        textId.setBackgroundResource(R.drawable.circle_orange_background_layout_im);
        textId.setTextColor(Color.parseColor("#6A6868"))
        textId.setHintTextColor(Color.GRAY)
        textHide.visibility = View.GONE
    }
}

fun editUtils(textId: EditText, textHide: TextView, text: String, boolean: Boolean) {
    if (boolean){
        textId.setBackgroundResource(R.drawable.circle_orange_background_edit_red);
        textId.setTextColor(Color.RED)
        textId.setHintTextColor(Color.RED)
        textHide.text = text
        textHide.visibility = View.VISIBLE
        textHide.setTextColor(Color.RED)
    }else{
        textId.setBackgroundResource(R.drawable.circle_orange_background_grey);
        textId.setTextColor(Color.parseColor("#6A6868"))
        textId.setHintTextColor(Color.GRAY)
        textHide.visibility = View.GONE
    }
}

fun editUtils(layoutId: LinearLayout,textId: TextView, textHide: TextView, text: String, boolean: Boolean) {
    if (boolean){
        layoutId.setBackgroundResource(R.drawable.circle_orange_background_edit_red);
        textId.setTextColor(Color.RED)
        textId.setHintTextColor(Color.RED)
        textHide.text = text
        textHide.setTextColor(Color.RED)
        textHide.visibility = View.VISIBLE
    }else{
        layoutId.setBackgroundResource(R.drawable.circle_orange_background_grey);
        textId.setTextColor(Color.parseColor("#6A6868"))
        textId.setHintTextColor(Color.GRAY)
        textHide.visibility = View.GONE
    }
}

fun editUtils(layoutId: ConstraintLayout,textId: TextView, textHide: TextView, text: String, boolean: Boolean) {
    if (boolean){
        layoutId.setBackgroundResource(R.drawable.circle_orange_background_edit_red);
        textId.setTextColor(Color.RED)
        textId.setHintTextColor(Color.RED)
        textHide.text = text
        textHide.setTextColor(Color.RED)
        textHide.visibility = View.VISIBLE
    }else{
        layoutId.setBackgroundResource(R.drawable.circle_orange_background_grey);
        textId.setTextColor(Color.parseColor("#6A6868"))
        textId.setHintTextColor(Color.GRAY)
        textHide.visibility = View.GONE
    }
}

fun changeImage(idImage: ImageView, boolean: Boolean , activity: Activity){
    if (boolean){
        idImage.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_added_image))
    }else{
        idImage.setImageDrawable(activity.resources.getDrawable(R.drawable.ic_add_image))
    }
}