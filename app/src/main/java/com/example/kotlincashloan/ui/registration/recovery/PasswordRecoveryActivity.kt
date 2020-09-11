package com.example.kotlincashloan.ui.registration.recovery

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.registration.login.MainActivity
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.example.kotlinscreenscanner.ui.login.fragment.PasswordRecoveryErrorFragment
import com.example.kotlinscreenscanner.ui.login.fragment.PasswordRecoveryFragment
import com.example.myapplication.LoginViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.AppPreferences.toFullPhone
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.activity_number.*
import kotlinx.android.synthetic.main.activity_password_recovery.*
import java.util.ArrayList

class PasswordRecoveryActivity : AppCompatActivity() {
    private var viewModel = LoginViewModel()
    private var myModel = PasswordViewMode()
    private var numberCharacters: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)
        MainActivity.alert = LoadingAlert(this)
        initToolBar()
        iniClick()
    }

    private fun iniClick() {
        password_recovery_enter.setOnClickListener {
            if (validate()) {
                MainActivity.alert.show()
                val map = HashMap<String, String>()
                map.put("phone", MyUtils.toFormatMask(questionnaire_phone_additional.text.toString()))
                map.put("response", password_recovery_word.text.toString())
                myModel.recoveryAccess(map).observe(this, Observer { result ->
                    val msg = result.msg
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            if (data!!.result == null) {
                                if (data.error.code == 404){
                                    initBusyBottomSheetError()
                                }
                            } else {
                                initBusyBottomSheet()
                            }
                        }
                        Status.ERROR -> {
                            loadingMistake(this)
                        }
                        Status.NETWORK -> {
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                    MainActivity.alert.hide()
                })
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initToolBar() {
        setSupportActionBar(password_recovery_toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
    }

    override fun onStart() {
        super.onStart()
        questionnaire_phone_additional.mask = AppPreferences.isFormatMask
        password_focus_text.requestFocus()
        getListCountry()
    }

    private fun initBusyBottomSheetError() {
        val bottomSheetDialogFragment = PasswordRecoveryErrorFragment()
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initBusyBottomSheet() {
        val bottomSheetDialogFragment = PasswordRecoveryFragment()
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun getListCountry() {
        var list: ArrayList<CounterResultModel> = arrayListOf()
        val map = HashMap<String, Int>()
        map.put("id", 0)
        MainActivity.alert.show()
        viewModel.listAvailableCountry(map).observe(this, androidx.lifecycle.Observer { result ->
            val msg = result.msg
            val data = result.data

            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        val adapterListCountry = ArrayAdapter(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            data.result
                        )
                        questionnaire_phone_list_country.setAdapter(adapterListCountry)
                        list = data.result
                    }else{
                        loadingMistake(this)
                    }
                }
                Status.ERROR -> {
                    loadingMistake(this)
                }
                Status.NETWORK -> {
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
            }
            MainActivity.alert.hide()
        })
        questionnaire_phone_list_country.keyListener = null
        questionnaire_phone_list_country.setOnItemClickListener { adapterView, view, position, l ->
            questionnaire_phone_list_country.showDropDown()
            AppPreferences.numberCharacters = list[position].phoneLength!!.toInt()
            AppPreferences.isFormatMask = list[position].phoneMask
            numberCharacters = list[position].phoneLength!!.toInt()
            questionnaire_phone_additional.setText("")
            questionnaire_phone_additional.mask = ""
            if (position == 0) {
                questionnaire_phone_additional.mask = list[position].phoneMask
            } else if (position == 1) {
                questionnaire_phone_additional.mask = list[position].phoneMask
            } else if (position == 2) {
                questionnaire_phone_additional.mask = list[position].phoneMask
            }
            if (questionnaire_phone_list_country.text.toString() != "") {
                layout_visible.visibility = View.VISIBLE
            }

            questionnaire_phone_list_country.clearFocus()
        }
        questionnaire_phone_list_country.setOnClickListener {
            questionnaire_phone_list_country.showDropDown()
        }
        questionnaire_phone_list_country.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            try {
                if (hasFocus) {
                    questionnaire_phone_list_country.showDropDown()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (questionnaire_phone_list_country.text.isEmpty()) {
            questionnaire_phone_list_country.error = "Выберите страну"
            valid = false
        } else {
            questionnaire_phone_list_country.error = null
        }

        if (numberCharacters != 11) {
            if (questionnaire_phone_additional.text!!.toString().length != 19) {
                questionnaire_phone_additional.error = "Введите валидный номер"
                valid = false
            } else {
                questionnaire_phone_additional.error = null
            }
        } else {
            if (questionnaire_phone_additional.text!!.toString().toFullPhone().length != 20) {
                questionnaire_phone_additional.error = "Введите валидный номер"
                valid = false
            } else {
                questionnaire_phone_additional.error = null
            }
        }
        if (password_recovery_word.text.toString().isEmpty()) {
            password_recovery_word.error = "Введите кодовое слово"
            valid = false
        }

        if (!valid){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }
        return valid
    }
}