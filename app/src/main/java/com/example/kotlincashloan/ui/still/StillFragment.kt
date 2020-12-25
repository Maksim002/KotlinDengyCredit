package com.example.kotlincashloan.ui.still

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.banPressed
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_still.*
import org.aviran.cookiebar2.CookieBar

class StillFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_still, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        requireActivity().onBackPressedDispatcher.addCallback(this) {}

        setTitle("Eще", resources.getColor(R.color.whiteColor))

        initClick()
    }

    private fun initClick() {
        still_exit.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            AppPreferences.token = ""
            MainActivity.timer.timeStop()
            startActivity(intent)
        }
    }

    fun setTitle(title: String?, color: Int) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color)
        }
    }

    override fun onResume() {
        super.onResume()
        //меняет цвета навигационной понели
        ColorWindows(activity as AppCompatActivity).noRollback()
    }
}