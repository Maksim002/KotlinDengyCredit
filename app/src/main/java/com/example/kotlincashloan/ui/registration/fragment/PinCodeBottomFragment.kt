package com.example.kotlinscreenscanner.ui.login.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingConnection
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlinscreenscanner.adapter.PintCodeBottomListener
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.myapplication.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
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
        HomeActivity.alert = LoadingAlert(activity as AppCompatActivity)
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
                        initRequest(numberOne, secondNumber)
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
            map.put("uid","null")
            map.put("system", "1")
            HomeActivity.alert.show()
            viewModel.auth(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result == null) {
                            if (data.error.code == 400 || data.error.code == 500 || data.error.code == 409){
                                loadingMistake(activity as AppCompatActivity)
                            }else if (data.error.code == 401){
                                initTransition()
                            }
                            else{
                                loadingMistake(activity as AppCompatActivity)
                            }
                        } else {
                            this.dismiss()
                            AppPreferences.token = data.result.token
                            initAuthorized()
                        }
                    }
                    Status.ERROR ->{
                        if (msg == "400" || msg == "500" || msg == "409"){
                            loadingMistake(activity as AppCompatActivity)
                        }else if (msg == "401"){
                            initTransition()
                        }else{
                            loadingMistake(activity as AppCompatActivity)
                        }
                    }
                    Status.NETWORK -> {
                        loadingConnection(activity as AppCompatActivity)
                    }
                }
                HomeActivity.alert.hide()
            })
        }
    }

    private fun initAuthorized(){
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }

    private fun initTransition() {
        val intent = Intent(context, HomeActivity::class.java)
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