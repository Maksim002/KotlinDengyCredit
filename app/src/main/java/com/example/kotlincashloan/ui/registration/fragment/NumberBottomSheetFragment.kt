package com.example.kotlinscreenscanner.ui.login.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.editUtils
import com.example.kotlincashloan.extension.loadingConnection
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlinscreenscanner.ui.login.QuestionnaireActivity
import com.example.myapplication.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.actyviti_questionnaire.*
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
        HomeActivity.alert = LoadingAlert(activity as AppCompatActivity)
        initClick()
        initView()
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
    }

    private fun initClick() {
        number_bottom_code.setOnClickListener {
            this.dismiss()
        }

        number_next.setOnClickListener {
            ObservedInternet().observedInternet(requireContext())
            if (!AppPreferences.observedInternet) {
                loadingConnection(activity as AppCompatActivity)
            } else {
                val map = HashMap<String, Int>()
                map.put("id", idPhone)
                if (number_text_sms.text.isNotEmpty()) {
                    map.put("code", number_text_sms.text.toString().toInt())
                }
                if (validate()) {
                    HomeActivity.alert.show()
                    viewModel.smsConfirmation(map).observe(viewLifecycleOwner, Observer { result ->
                        val msg = result.msg
                        val data = result.data
                        when (result.status) {
                            Status.SUCCESS -> {
                                if (data!!.result == null) {
                                    if (data.error.code == 500 || data.error.code == 409) {
                                        loadingMistake(activity as AppCompatActivity)
                                    } else if (data.error.code == 400) {
                                        number_incorrect.visibility = View.VISIBLE
                                    } else if (data.error.code == 401) {
                                        initAuthorized()
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
                                if (msg == "500" || msg == "409" || msg == "429") {
                                    loadingMistake(activity as AppCompatActivity)
                                } else if (msg == "400") {
                                    number_incorrect.visibility = View.VISIBLE
                                } else if (msg == "401") {
                                    initAuthorized()
                                } else {
                                    loadingMistake(activity as AppCompatActivity)
                                }
                            }
                            Status.NETWORK -> {
                                if (msg == "600") {
                                    loadingMistake(activity as AppCompatActivity)
                                    number_incorrect.visibility = View.VISIBLE
                                } else {
                                    loadingConnection(activity as AppCompatActivity)
                                }
                            }
                        }
                        HomeActivity.alert.hide()
                    })
                }
            }
        }
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        number_incorrect.visibility = View.GONE
    }

    //Мето проверяет заполнения полей
    private fun validate(): Boolean {
        var valid = true
        if (number_text_sms.text.toString().isEmpty()) {
            editUtils(number_text_sms, number_sms_error, "Заполните поле", true)
            number_incorrect.visibility = View.GONE
            valid = false
        }
        return valid

    }

    //Мето прослушивает поля на изменение
    private fun initView() {
        number_text_sms.addTextChangedListener {
            editUtils(number_text_sms, number_sms_error, "Заполните поле", false)
        }
    }
}