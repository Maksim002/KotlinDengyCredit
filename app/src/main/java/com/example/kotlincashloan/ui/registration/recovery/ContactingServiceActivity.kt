package com.example.kotlincashloan.ui.registration.recovery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingConnection
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.service.model.recovery.ListSupportTypeResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TimerListener
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.kotlinscreenscanner.ui.login.fragment.ShippedSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.YourApplicationFragment
import com.example.myapplication.LoginViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.AppPreferences.toFullPhone
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.activity_contacting_service.*
import kotlinx.android.synthetic.main.activity_contacting_service.questionnaire_phone_additional
import kotlinx.android.synthetic.main.actyviti_questionnaire.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class ContactingServiceActivity : AppCompatActivity() {
    private var viewModel = LoginViewModel()
    private var numberCharacters: Int = 0
    private var myViewMode = PasswordViewMode()
    private var typeId: Int = 0
    private val handler = Handler()
    private var profNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacting_service)
        HomeActivity.alert = LoadingAlert(this)
        MainActivity.timer = TimerListener(this)

        initArgument()
        initToolBar()
        getListCountry()
        getType()
        iniClick()
    }

    private fun initArgument() {
            profNumber = intent.extras!!.getString("number").toString()
    }

    private fun iniClick() {
        no_connection_repeat.setOnClickListener {
            if (typeId != 0) {
                initResult()
            } else {
                getListCountry()
            }
        }

        access_restricted.setOnClickListener {
            if (typeId != 0) {
                getListCountry()
            }
        }

        not_found.setOnClickListener {
            if (typeId != 0) {
                getListCountry()
            }

        }

        technical_work.setOnClickListener {
            if (typeId != 0) {
                getListCountry()
            }
        }

        password_recovery_send.setOnClickListener {
            if (validate()) {
                initResult()
            }
        }
    }

    //Метотд для скрытия клавиатуры
    private fun closeKeyboard() {
        val view: View = this.currentFocus!!
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            val manager = this.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager?
            manager!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun initToolBar() {
        setSupportActionBar(password_recovery_toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
    }

    private fun initResult() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            password_no_questionnaire.visibility = View.VISIBLE
            password_access_restricted.visibility = View.GONE
            password_not_found.visibility = View.GONE
            password_technical_work.visibility = View.GONE
            contacting_layout.visibility = View.GONE
        } else {
            val map = mutableMapOf<String, String>()
            map["type"] = typeId.toString()
            map["last_name"] = questionnaire_phone_surnames.text.toString()
            map["first_name"] = questionnaire_phone_name.text.toString()
            map["second_name"] = questionnaire_phone_patronymics.text.toString()
            map["phone"] = MyUtils.toFormatMask(questionnaire_phone_additional.text.toString())
            map["comment"] = password_recovery_comments.text.toString()
            HomeActivity.alert.show()
            myViewMode.supportTicket(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result == null) {
                            if (data.error.code == 500 || data.error.code == 400) {
                                password_no_questionnaire.visibility = View.GONE
                                contacting_layout.visibility = View.VISIBLE
                                loadingMistake(this)
                            } else if (data.error.code == 409) {
                                password_no_questionnaire.visibility = View.GONE
                                contacting_layout.visibility = View.VISIBLE
                                initBottomSheetMistake()
                            } else if (data.error.code == 401) {
                                initAuthorized()
                            }
                        } else {
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                            initBottomSheet()
                            initVisibilities()
                        }
                    }
                    Status.ERROR -> {
                        if (msg == "401") {
                            initAuthorized()
                        } else if (msg == "409") {
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                            initBottomSheetMistake()
                        } else if (msg == "500" || msg == "400") {
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        } else {
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        }
                    }
                    Status.NETWORK -> {
                        if (msg == "600") {
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        } else {
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                            loadingConnection(this)
                        }
                    }
                }
                HomeActivity.alert.hide()
            })
        }
    }

    private fun initAuthorized() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun initBottomSheet() {
        val bottomSheetDialogFragment = YourApplicationFragment(profNumber)
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initBottomSheetMistake() {
        val bottomSheetDialogFragment = ShippedSheetFragment(profNumber)
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getListCountry() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            password_no_questionnaire.visibility = View.VISIBLE
            contacting_layout.visibility = View.GONE
            password_access_restricted.visibility = View.GONE
            password_not_found.visibility = View.GONE
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
                                    this, android.R.layout.simple_dropdown_item_1line, data.result
                                )
                                questionnaire_phone_list_country.setAdapter(adapterListCountry)
                                list = data.result
                                initVisibilities()
                            } else {
                                if (data.error.code == 403) {
                                    password_no_questionnaire.visibility = View.GONE
                                    password_access_restricted.visibility = View.VISIBLE
                                    contacting_layout.visibility = View.GONE

                                } else if (data.error.code == 404) {
                                    password_no_questionnaire.visibility = View.GONE
                                    password_not_found.visibility = View.VISIBLE
                                    contacting_layout.visibility = View.GONE

                                } else if (data.error.code == 401) {
                                    initAuthorized()
                                } else {
                                    loadingMistake(this)
                                    password_no_questionnaire.visibility = View.GONE
                                    contacting_layout.visibility = View.VISIBLE
                                }
                            }
                        }
                        Status.ERROR -> {
                            if (msg == "404") {
                                password_no_questionnaire.visibility = View.GONE
                                password_not_found.visibility = View.VISIBLE
                                contacting_layout.visibility = View.GONE

                            } else if (msg == "403") {
                                password_no_questionnaire.visibility = View.GONE
                                password_access_restricted.visibility = View.VISIBLE
                                contacting_layout.visibility = View.GONE

                            } else if (msg == "401") {
                                initAuthorized()
                            } else {
                                loadingMistake(this)
                                password_no_questionnaire.visibility = View.GONE
                                contacting_layout.visibility = View.VISIBLE
                            }
                        }
                        Status.NETWORK -> {
                            if (msg == "600") {
                                loadingMistake(this)
                                password_no_questionnaire.visibility = View.GONE
                                contacting_layout.visibility = View.VISIBLE
                            } else {
                                loadingConnection(this)
                                password_no_questionnaire.visibility = View.GONE
                                contacting_layout.visibility = View.VISIBLE
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
                questionnaire_phone_list_country.error = null
                questionnaire_phone_additional.error = null
                questionnaire_phone_additional.mask = ""
                questionnaire_phone_additional.mask = list[position].phoneMask
                if (questionnaire_phone_list_country.text.toString() != "") {
                    questionnaire_phone_additional.visibility = View.VISIBLE
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
                            closeKeyboard()
                            questionnaire_phone_list_country.showDropDown()
                        }
                    } catch (e: Exception) {
                    }
                }
        }
    }

    fun initVisibilities() {
        password_no_questionnaire.visibility = View.GONE
        contacting_layout.visibility = View.VISIBLE
    }

    private fun getType() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            password_no_questionnaire.visibility = View.VISIBLE
            contacting_layout.visibility = View.GONE
            password_access_restricted.visibility = View.GONE
            password_not_found.visibility = View.GONE
        } else {
            var list: ArrayList<ListSupportTypeResultModel> = arrayListOf()
            val map = HashMap<String, Int>()
            map.put("id", 0)
            HomeActivity.alert.show()
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
                            initVisibilities()
                        } else {
                            if (data.error.code == 403) {
                                password_access_restricted.visibility = View.VISIBLE
                                contacting_layout.visibility = View.GONE

                            } else if (data.error.code == 404) {
                                password_not_found.visibility = View.VISIBLE
                                contacting_layout.visibility = View.GONE

                            } else if (data.error.code == 401) {
                                initAuthorized()
                            } else {
                                loadingMistake(this)
                                password_no_questionnaire.visibility = View.GONE
                                contacting_layout.visibility = View.VISIBLE
                            }
                        }
                    }
                    Status.ERROR -> {
                        if (msg == "404") {
                            password_not_found.visibility = View.VISIBLE
                            contacting_layout.visibility = View.GONE

                        } else if (msg == "403") {
                            password_access_restricted.visibility = View.VISIBLE
                            contacting_layout.visibility = View.GONE

                        } else if (msg == "401") {
                            initAuthorized()
                        } else {
                            loadingMistake(this)
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                        }
                    }
                    Status.NETWORK -> {
                        if (msg == "600") {
                            loadingMistake(this)
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                        } else {
                            loadingConnection(this)
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                        }
                    }
                }
                HomeActivity.alert.hide()
            })
            password_recovery_type.keyListener = null
            password_recovery_type.setOnItemClickListener { adapterView, view, position, l ->
                password_recovery_type.showDropDown()
                typeId = list[position].id!!
                password_recovery_type.error = null
                password_recovery_type.clearFocus()
            }
            password_recovery_type.setOnClickListener {
                password_recovery_type.showDropDown()
            }
            password_recovery_type.onFocusChangeListener =
                View.OnFocusChangeListener { view, hasFocus ->
                    try {
                        if (hasFocus) {
                            closeKeyboard()
                            password_recovery_type.showDropDown()
                        }
                    } catch (e: Exception) {
                    }
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

        if (!valid) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }

        return valid
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(Runnable { // Do something after 5s = 500ms
            MainActivity.timer.timeStop()
        }, 2000)
        TransitionAnimation(this).transitionRight(contacts_layout_anim)
    }

    override fun onStop() {
        super.onStop()
        if (AppPreferences.token != ""){
            MainActivity.timer.timeStart()
            AppPreferences.isNumber = false
        }
    }
}