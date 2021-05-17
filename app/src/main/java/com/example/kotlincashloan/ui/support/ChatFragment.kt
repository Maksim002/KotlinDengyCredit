package com.example.kotlincashloan.ui.support

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincashloan.R
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlinscreenscanner.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_profile_setting.*

class ChatFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("Техподдержка", resources.getColor(R.color.whiteColor), "В сети")
    }

    //Передаёт в toolBar текст
    fun setTitle(title: String?, color: Int, textOnline: String? = null) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color, textOnline)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onResume() {
        super.onResume()

        //меняет цвета навигационной понели
        ColorWindows(activity as AppCompatActivity).rollback()
        val backArrow = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        backArrow.setColorFilter(resources.getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        (activity as AppCompatActivity?)!!.getSupportActionBar()!!.setHomeAsUpIndicator(backArrow)
    }
}