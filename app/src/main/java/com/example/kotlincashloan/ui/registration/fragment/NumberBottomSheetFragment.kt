package com.example.kotlinscreenscanner.ui.login.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlinscreenscanner.ui.login.QuestionnaireActivity
import com.example.myapplication.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
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
        initClick()
    }

    private fun initClick() {
        number_bottom_code.setOnClickListener {
            this.dismiss()
        }
        closed.setOnClickListener {
            this.dismiss()
        }

        number_next.setOnClickListener {
            if (validate()) {
                val map = HashMap<String, Int>()
                map.put("id", idPhone)
                map.put("code", number_text_sms.text.toString().toInt())
                viewModel.smsConfirmation(map).observe(viewLifecycleOwner, Observer { result ->
                    val msg = result.msg
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            if (data!!.result == null) {
                                Toast.makeText(context, data.error.message, Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                AppPreferences.receivedSms = number_text_sms.text.toString()
                                val intent = Intent(context, QuestionnaireActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        Status.ERROR, Status.NETWORK -> {
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (number_text_sms.text!!.toString().length != 4) {
            number_text_sms.error = "Введите валидный sms"
            valid = false
        } else {
            number_text_sms.error = null
        }

        return valid
    }
}