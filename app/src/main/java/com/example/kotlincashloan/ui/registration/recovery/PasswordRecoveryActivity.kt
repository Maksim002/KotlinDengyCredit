package com.example.kotlincashloan.ui.registration.recovery

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.registration.login.MainActivity
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.example.kotlinscreenscanner.ui.login.fragment.NumberBusyBottomSheetFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.NetworkRepository
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.activity_password_recovery.*
import java.util.ArrayList

class PasswordRecoveryActivity : AppCompatActivity() {
    private var viewModel = NetworkRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)
        initToolBar()
        getListCountry()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initToolBar() {
        setSupportActionBar(password_recovery_toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Восстановление пароля"
    }

    override fun onStart() {
        super.onStart()
        questionnaire_phone_additional.mask = AppPreferences.isFormatMask
    }

    private fun initBusyBottomSheet() {
        val bottomSheetDialogFragment = NumberBusyBottomSheetFragment()
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
}