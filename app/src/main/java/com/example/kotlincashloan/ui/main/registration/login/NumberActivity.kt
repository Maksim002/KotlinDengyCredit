package com.example.kotlinscreenscanner.ui.login

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.main.registration.login.MainActivity
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.example.kotlinscreenscanner.ui.login.fragment.NumberBottomSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.NumberBusyBottomSheetFragment
import com.example.myapplication.LoginViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.AppPreferences.toFullPhone
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.activity_number.*
import java.util.*
import kotlin.collections.HashMap

class NumberActivity : AppCompatActivity() {
    private var viewModel = LoginViewModel()
    private var numberCharacters: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number)
        MainActivity.alert = LoadingAlert(this)
        initClick()
        initViews()
        initToolBar()

    }

    private fun initBottomSheet(id: Int) {
        val bottomSheetDialogFragment = NumberBottomSheetFragment(id)
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initBusyBottomSheet() {
        val bottomSheetDialogFragment = NumberBusyBottomSheetFragment()
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        number_focus_text.requestFocus()
        getListCountry()
    }

    private fun initClick() {
        number_next.setOnClickListener {
            if (validate()) {
                MainActivity.alert.show()
                val map = HashMap<String, String>()
                map.put("phone", MyUtils.toFormatMask(number_phone.text.toString()))
                viewModel.numberPhones(map).observe(this, Observer { result ->
                    val msg = result.msg
                    val data = result.data
                    when (result.status) {
                        Status.SUCCESS -> {
                            if (data!!.result == null) {
                                if (data.error.code != 409){
                                    loadingMistake(this)
                                }else{
                                    initBusyBottomSheet()
                                }
                            } else {
                                AppPreferences.number = number_phone.text.toString()
                                initBottomSheet(data.result.id!!)
                            }
                        }
                        Status.ERROR -> {
                            loadingMistake(this)

                        }
                        Status.NETWORK ->{
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                    MainActivity.alert.hide()
                })
            }
        }
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
                        number_list_country.setAdapter(adapterListCountry)
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
        number_list_country.keyListener = null
        number_list_country.setOnItemClickListener { adapterView, view, position, l ->
            number_list_country.showDropDown()
            AppPreferences.numberCharacters = list[position].phoneLength!!.toInt()
            AppPreferences.isFormatMask = list[position].phoneMask
            numberCharacters = list[position].phoneLength!!.toInt()
            number_phone.setText("")
            number_phone.mask = ""
            if (position == 0) {
                number_phone.mask = list[position].phoneMask
            } else if (position == 1) {
                number_phone.mask = list[position].phoneMask
            } else if (position == 2) {
                number_phone.mask = list[position].phoneMask
            }
            if (number_list_country.text.toString() != "") {
                number_phone.visibility = View.VISIBLE
            }

            number_list_country.clearFocus()
        }
        number_list_country.setOnClickListener {
            number_list_country.showDropDown()
        }
        number_list_country.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            try {
                if (hasFocus) {
                    number_list_country.showDropDown()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (number_list_country.text.isEmpty()) {
            number_list_country.error = "Выберите страну"
            valid = false
        } else {
            number_phone.error = null
        }

        if (numberCharacters != 11) {
            if (number_phone.text!!.toString().length != 19) {
                number_phone.error = "Введите валидный номер"
                valid = false
            } else {
                number_phone.error = null
            }
        } else {
            if (number_phone.text!!.toString().toFullPhone().length != 20) {
                number_phone.error = "Введите валидный номер"
                valid = false
            } else {
                number_phone.error = null
            }
        }
        if (!valid){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }
        return valid
    }

    private fun initViews() {
        number_list_country.addTextChangedListener {
            number_list_country.error = null
            number_phone.error = null
        }
    }
}