package com.example.kotlinscreenscanner.ui.login.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.login.MainActivity
import com.example.kotlinscreenscanner.adapter.PintCodeBottomListener
import com.example.kotlinscreenscanner.ui.Top
import com.example.myapplication.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_pin_code_bottom.*
import java.util.HashMap

class PinCodeBottomFragment(private val listener: PintCodeBottomListener) : BottomSheetDialogFragment() {
    private var viewModel = LoginViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pin_code_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.alert = LoadingAlert(Activity())
        iniClick()
    }

    private fun iniClick() {
        bottom_sheet_closed.setOnClickListener {
            listener.pinCodeClockListener()
            this.dismiss()
            AppPreferences.savePin = null
        }

        bottom_sheet_resume.setOnClickListener {
            if (isValid()) {
                val numberOne = bottom_sheet_pin_code.text.toString()
                val secondNumber = bottom_sheet_repeat_code.text.toString()
                if (AppPreferences.savePin!!.isNotEmpty()) {
                    if (numberOne == AppPreferences.savePin && secondNumber == AppPreferences.savePin) {
                        initTransition()
                    }
                } else {
                    initRequest(numberOne, secondNumber)
                }
            }
        }
    }

    private fun initRequest(numberOne: String, secondNumber: String) {
        if (numberOne.isNotEmpty() && secondNumber.isNotEmpty()) {
            AppPreferences.savePin = numberOne
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
                            Toast.makeText(context, data.error.message, Toast.LENGTH_LONG).show()
                        } else {
                            AppPreferences.token = data.result.token
                            initTransition()
                        }
                    }
                    Status.ERROR, Status.NETWORK -> {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                }
                MainActivity.alert.hide()
            })
        }
    }

    private fun initTransition() {
        val intent = Intent(context, Top::class.java)
        startActivity(intent)
    }

    private fun isValid(): Boolean {
        var valid = true
        if (bottom_sheet_pin_code.text.toString() != bottom_sheet_repeat_code.text.toString()) {
            bottom_sheet_repeat_code.error = "Поля должны совпадать"
            bottom_sheet_pin_code.error = "Поля должны совпадать"
            valid = false
        } else if (bottom_sheet_repeat_code.text!!.toString().length != 4 && bottom_sheet_repeat_code.text!!.toString().length != 4) {
            bottom_sheet_pin_code.error = "Поле должно содержать 4 символа"
            bottom_sheet_repeat_code.error = "Поле должно содержать 4 символа"
            valid = false
        } else if (AppPreferences.savePin!!.isNotEmpty()) {
            if (bottom_sheet_pin_code.text.toString() != AppPreferences.savePin && bottom_sheet_repeat_code.text.toString() != AppPreferences.savePin) {
                bottom_sheet_pin_code.error = "Пин код неверный"
                bottom_sheet_repeat_code.error = "Пин код неверный"
                valid = false
            }
        } else {
            bottom_sheet_repeat_code.error = null
            bottom_sheet_pin_code.error = null
        }

        return valid
    }
}