package com.example.kotlincashloan.ui.registration.recovery

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.service.model.recovery.ListSupportTypeResultModel
import com.example.kotlincashloan.ui.registration.login.MainActivity
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.example.kotlinscreenscanner.ui.login.fragment.ShippedSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.YourApplicationFragment
import com.example.myapplication.LoginViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.AppPreferences.toFullPhone
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.activity_contacting_service.*
import kotlinx.android.synthetic.main.activity_contacting_service.questionnaire_phone_additional
import java.util.ArrayList

class ContactingServiceActivity : AppCompatActivity() {
    private var viewModel = LoginViewModel()
    private var numberCharacters: Int = 0
    private var myViewMode = PasswordViewMode()
    private var typeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacting_service)
        initToolBar()
        getListCountry()
        getType()
        iniClick()
    }

    private fun iniClick() {
        password_recovery_send.setOnClickListener {
            if (validate()) {
                MainActivity.alert.show()
                val map = mutableMapOf<String, String>()
                map["type"] = typeId.toString()
                map["last_name"] = questionnaire_phone_surnames.text.toString()
                map["first_name"] = questionnaire_phone_name.text.toString()
                map["second_name"] = questionnaire_phone_patronymics.text.toString()
                map["phone"] = MyUtils.toFormatMask(questionnaire_phone_additional.text.toString())
                map["comment"] = password_recovery_comments.text.toString()
                myViewMode.supportTicket(map).observe(this, Observer { result ->
                    val msg = result.msg
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            if (data!!.result != null) {
                                initBottomSheet()
                            } else {
                                initBottomSheetMistake()
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

    private fun initBottomSheet() {
        val bottomSheetDialogFragment = YourApplicationFragment()
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initBottomSheetMistake() {
        val bottomSheetDialogFragment = ShippedSheetFragment()
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
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
            }
        }
    }

    private fun getType() {
        var list: ArrayList<ListSupportTypeResultModel> = arrayListOf()
        val map = HashMap<String, Int>()
        map.put("id", 0)
        MainActivity.alert.show()
        myViewMode.listSupportType(map).observe(this, androidx.lifecycle.Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        val adapterType = ArrayAdapter(
                            this, android.R.layout.simple_dropdown_item_1line, data.result
                        )
                        password_recovery_type.setAdapter(adapterType)
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
        password_recovery_type.keyListener = null
        password_recovery_type.setOnItemClickListener { adapterView, view, position, l ->
            password_recovery_type.showDropDown()
            typeId = list[position].id!!
            password_recovery_type.clearFocus()
        }
        password_recovery_type.setOnClickListener {
            password_recovery_type.showDropDown()
        }
        password_recovery_type.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            try {
                if (hasFocus) {
                    password_recovery_type.showDropDown()
                }
            } catch (e: Exception) {
            }
        }
    }
    private fun validate(): Boolean {
        var valid = true
        if (questionnaire_phone_surnames.text.toString().isEmpty()) {
            questionnaire_phone_surnames.error = "Введите фамилию"
            valid = false
        }

        if (questionnaire_phone_name.text.toString().isEmpty()) {
            questionnaire_phone_name.error = "Введите имя"
            valid = false
        }

        if (questionnaire_phone_patronymics.text.toString().isEmpty()) {
            questionnaire_phone_patronymics.error = "Введите отчество"
            valid = false
        }

        if (questionnaire_phone_list_country.text.isEmpty()) {
            questionnaire_phone_list_country.error = "Выберите страну"
            valid = false
        } else {
            questionnaire_phone_list_country.error = null
        }

        if (password_recovery_type.text.isEmpty()) {
            password_recovery_type.error = "Выберите тип оброщения"
            valid = false
        } else {
            password_recovery_type.error = null
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

        if (!valid){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }

        return valid

    }
}