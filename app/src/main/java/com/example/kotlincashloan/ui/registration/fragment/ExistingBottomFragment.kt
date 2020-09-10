package com.example.kotlinscreenscanner.ui.login.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.ExistingBottomListener
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.registration.login.MainActivity
import com.example.kotlinscreenscanner.ui.Top
import com.example.kotlinscreenscanner.ui.login.QuestionnaireActivity
import com.example.myapplication.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.fragment_existing_bottom.*
import java.util.HashMap

class ExistingBottomFragment(private val listener: ExistingBottomListener) : BottomSheetDialogFragment() {
    private var viewModel = LoginViewModel()
    var currentPinInput = ""
    var initpin = ""
    lateinit var activity: AppCompatActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_existing_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() {
        existing_one.setOnClickListener {
            currentPinInput += "1"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_two.setOnClickListener {
            currentPinInput += "2"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_three.setOnClickListener {
            currentPinInput += "3"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_four.setOnClickListener {
            currentPinInput += "4"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_five.setOnClickListener {
            currentPinInput += "5"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_six.setOnClickListener {
            currentPinInput += "6"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_seven.setOnClickListener {
            currentPinInput += "7"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_eight.setOnClickListener {
            currentPinInput += "8"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_nine.setOnClickListener {
            currentPinInput += "9"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_zero.setOnClickListener {
            currentPinInput += "0"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_btn.setOnClickListener {
            listener.existingClockListener()
            this.dismiss()
            AppPreferences.savePin = null
        }
        existing_removal.setOnClickListener {
            if (currentPinInput.isNotEmpty())
                currentPinInput = currentPinInput.substring(0, currentPinInput.length - 1)
            pin_verification_code.setText(currentPinInput)
        }
    }

    fun check() {
        if (currentPinInput.length == 4) {
            if (AppPreferences.savePin == currentPinInput) {
                val map = HashMap<String, String>()
                map.put("password", AppPreferences.password.toString())
                map.put("login", AppPreferences.login.toString())
                MainActivity.alert.show()
                viewModel.auth(map).observe(this, Observer { result ->
                    val msg = result.msg
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            if (data!!.result == null) {
                                loadingMistake(activity)
                                Toast.makeText(context, data.error.message, Toast.LENGTH_LONG).show()
                            } else {
                                AppPreferences.token = data.result.token
                                val intent = Intent(context, Top::class.java)
                                startActivity(intent)
                            }
                        }
                        Status.ERROR, Status.NETWORK -> {
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                    MainActivity.alert.hide()
                })
            } else {
                currentPinInput = ""
                pin_verification_code.setText(initpin)
                existing_liner_anim.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake))
            }
        }
    }
}