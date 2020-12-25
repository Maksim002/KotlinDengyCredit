package com.example.kotlincashloan.utils

import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.kotlincashloan.R

class ColorWindows(var activity: AppCompatActivity) {

    fun rollback(){
        //меняет цвета навигационной понели
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.getWindow()
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(R.color.orangeColor)
            val toolbar = activity.findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(ColorDrawable(activity.resources.getColor(R.color.orangeColor)))
            toolbar.getNavigationIcon()!!.setColorFilter(activity.getResources().getColor(R.color.whiteColor), PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun noRollback(){
        //меняет цвета навигационной понели
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.getWindow()
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(R.color.orangeColor)
            val toolbar = activity.findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(ColorDrawable(activity.resources.getColor(R.color.orangeColor)))
        }
    }
}