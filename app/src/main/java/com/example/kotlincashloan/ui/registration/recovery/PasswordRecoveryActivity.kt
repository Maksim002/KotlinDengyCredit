package com.example.kotlincashloan.ui.registration.recovery

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingConnection
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.example.kotlinscreenscanner.ui.login.fragment.PasswordRecoveryErrorFragment
import com.example.kotlinscreenscanner.ui.login.fragment.PasswordRecoveryFragment
import com.example.myapplication.LoginViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.AppPreferences.toFullPhone
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_password_recovery.*
import kotlinx.android.synthetic.main.activity_password_recovery.questionnaire_phone_additional
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.ArrayList

class PasswordRecoveryActivity : AppCompatActivity() {
    private var viewModel = LoginViewModel()
    private var myModel = PasswordViewMode()
    private var numberCharacters: Int = 0
    private var inputsAnim = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)
        HomeActivity.alert = LoadingAlert(this)
        iniClick()
        initToolBar()
    }

    private fun iniClick() {
        password_recovery_enter.setOnClickListener {
            if (validate()) {
                initResult()
            }
        }

        no_connection_repeat.setOnClickListener {
            if (numberCharacters != 0){
                initResult()
            }else{
                getListCountry()
            }
        }

        access_restricted.setOnClickListener {
            getListCountry()
        }

        not_found.setOnClickListener {
            getListCountry()
        }

        technical_work.setOnClickListener {
            getListCountry()
        }
    }

    private fun initToolBar() {
        setSupportActionBar(password_toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
    }

    fun initResult(){
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            recovery_no_questionnaire.visibility = View.VISIBLE
            password_layout.visibility = View.GONE
        } else {
            val map = HashMap<String, String>()
            map.put("phone", MyUtils.toFormatMask(questionnaire_phone_additional.text.toString()))
            map.put("response", password_recovery_word.text.toString())
            HomeActivity.alert.show()
            myModel.recoveryAccess(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result == null) {
                            if (data.error.code == 404) {
                                initBusyBottomSheetError()
                                initVisibilities()
                            } else if (data.error.code == 401) {
                                initAuthorized()
                            } else {
                                loadingMistake(this)
                                recovery_no_questionnaire.visibility = View.GONE
                                password_layout.visibility = View.VISIBLE
                            }
                        } else {
                            initBusyBottomSheet()
                            initVisibilities()
                        }
                    }
                    Status.ERROR -> {
                        if (msg == "404") {
                            initBusyBottomSheetError()
                            initVisibilities()
                        } else if (msg == "401") {
                            initAuthorized()
                        } else {
                            recovery_no_questionnaire.visibility = View.GONE
                            password_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        }
                    }
                    Status.NETWORK ->{
                        if (msg == "600"){
                            recovery_no_questionnaire.visibility = View.GONE
                            password_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        }else{
                            recovery_no_questionnaire.visibility = View.GONE
                            password_layout.visibility = View.VISIBLE
                            loadingConnection(this)
                        }
                    }
                }
                HomeActivity.alert.hide()
            })
        }
    }

    fun initVisibilities(){
        recovery_no_questionnaire.visibility = View.GONE
        password_layout.visibility = View.VISIBLE
    }

    private fun initAuthorized(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        questionnaire_phone_additional.mask = AppPreferences.isFormatMask
        password_focus_text.requestFocus()
        getListCountry()
    }

    private fun initBusyBottomSheetError() {
        val bottomSheetDialogFragment = PasswordRecoveryErrorFragment()
        inputsAnim = false
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initBusyBottomSheet() {
        val bottomSheetDialogFragment = PasswordRecoveryFragment()
        inputsAnim  = false
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun getListCountry() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            recovery_no_questionnaire.visibility = View.VISIBLE
            password_layout.visibility = View.GONE
            recovery_technical_work.visibility = View.GONE
            recovery_not_found.visibility = View.GONE
            recovery_access_restricted.visibility = View.GONE
        } else {
            var list: ArrayList<CounterResultModel> = arrayListOf()
            val map = HashMap<String, Int>()
            map.put("id", 0)
            HomeActivity.alert.show()
            viewModel.listAvailableCountry(map)
                .observe(this, androidx.lifecycle.Observer { result ->
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
                                initVisibilities()
                            } else {
                                if (data.error.code == 403) {
                                    recovery_no_questionnaire.visibility = View.GONE
                                    recovery_access_restricted.visibility = View.VISIBLE
                                    password_layout.visibility = View.GONE

                                } else if (data.error.code == 404) {
                                    recovery_no_questionnaire.visibility = View.GONE
                                    recovery_not_found.visibility = View.VISIBLE
                                    password_layout.visibility = View.GONE

                                } else if (data.error.code == 401) {
                                    initAuthorized()
                                } else {
                                    recovery_no_questionnaire.visibility = View.GONE
                                    recovery_technical_work.visibility = View.VISIBLE
                                    password_layout.visibility = View.GONE
                                }
                            }
                        }
                        Status.ERROR -> {
                            if (msg == "404") {
                                recovery_no_questionnaire.visibility = View.GONE
                                recovery_not_found.visibility = View.VISIBLE
                                password_layout.visibility = View.GONE

                            } else if (msg == "403") {
                                recovery_no_questionnaire.visibility = View.GONE
                                recovery_access_restricted.visibility = View.VISIBLE
                                password_layout.visibility = View.GONE

                            } else if (msg == "401") {
                                initAuthorized()
                            } else {
                                recovery_no_questionnaire.visibility = View.GONE
                                recovery_technical_work.visibility = View.VISIBLE
                                password_layout.visibility = View.GONE
                            }
                        }
                        Status.NETWORK ->{
                            if (msg == "600"){
                                recovery_no_questionnaire.visibility = View.GONE
                                recovery_technical_work.visibility = View.VISIBLE
                                password_layout.visibility = View.GONE
                            }else{
                                recovery_technical_work.visibility = View.GONE
                                recovery_no_questionnaire.visibility = View.VISIBLE
                                password_layout.visibility = View.GONE
                            }
                        }
                    }
                    HomeActivity.alert.hide()
                })
            questionnaire_phone_list_country.keyListener = null
            questionnaire_phone_list_country.setOnItemClickListener { adapterView, view, position, l ->
                questionnaire_phone_list_country.showDropDown()
                AppPreferences.numberCharacters = list[position].phoneLength!!.toInt()
                AppPreferences.isFormatMask = list[position].phoneMask
                numberCharacters = list[position].phoneLength!!.toInt()
                questionnaire_phone_additional.setText("")
                questionnaire_phone_additional.mask = ""
                questionnaire_phone_additional.mask = list[position].phoneMask
                if (questionnaire_phone_list_country.text.toString() != "") {
                    layout_visible.visibility = View.VISIBLE
                }

                questionnaire_phone_list_country.clearFocus()
            }
            questionnaire_phone_list_country.setOnClickListener {
                questionnaire_phone_list_country.showDropDown()
            }
            questionnaire_phone_list_country.onFocusChangeListener =
                View.OnFocusChangeListener { view, hasFocus ->
                    try {
                        if (hasFocus) {
                            questionnaire_phone_list_country.showDropDown()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        if (inputsAnim) {
            TransitionAnimation(this).transitionRight(password_layout_anim)
            inputsAnim = false
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