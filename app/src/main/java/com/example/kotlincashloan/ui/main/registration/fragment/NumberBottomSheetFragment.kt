package com.example.kotlinscreenscanner.ui.login.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingConnection
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.main.registration.login.MainActivity
import com.example.kotlinscreenscanner.ui.login.QuestionnaireActivity
import com.example.myapplication.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_number_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_number_bottom_sheet.number_next

class NumberBottomSheetFragment(var idPhone: Int) : BottomSheetDialogFragment() {
    private var viewModel = LoginViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_number_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.alert = LoadingAlert(activity as  AppCompatActivity)
        initClick()
    }

    private fun initClick() {
        number_bottom_code.setOnClickListener {
            this.dismiss()
        }

        number_next.setOnClickListener {
                val map = HashMap<String, Int>()
                map.put("id", idPhone)
                if (number_text_sms.text.isNotEmpty()){
                    map.put("code", number_text_sms.text.toString().toInt())
                }
            if (validate()) {
                MainActivity.alert.show()
                viewModel.smsConfirmation(map).observe(viewLifecycleOwner, Observer { result ->
                    val msg = result.msg
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            if (data!!.result == null) {
                                if (data.error.code == 400 || data.error.code == 500 || data.error.code == 409) {
                                    number_incorrect.visibility = View.VISIBLE
                                } else {
                                    loadingMistake(activity as AppCompatActivity)
                                }
                            } else {
                                number_incorrect.visibility = View.GONE
                                AppPreferences.receivedSms = number_text_sms.text.toString()
                                val intent = Intent(context, QuestionnaireActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        Status.ERROR -> {
                            loadingMistake(activity as AppCompatActivity)
                        }
                        Status.NETWORK -> {
                            loadingConnection(activity as AppCompatActivity)
                        }
                    }
                    MainActivity.alert.hide()
                })
            }
        }
    }

    override fun onStart() {
        super.onStart()
        number_incorrect.visibility = View.GONE
    }

    private fun validate(): Boolean {
        var valid = true
        if (number_text_sms.text.toString().isEmpty()) {
            number_text_sms.error = "Заполните поле"
            number_incorrect.visibility = View.GONE
            valid = false
        }
        return valid

    }
}