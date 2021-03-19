package com.example.kotlincashloan.ui.registration.recovery

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.common.GeneralDialogFragment
import com.example.kotlincashloan.extension.editUtils
import com.example.kotlincashloan.extension.loadingConnection
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.example.kotlinscreenscanner.ui.login.fragment.PasswordRecoveryErrorFragment
import com.example.kotlinscreenscanner.ui.login.fragment.PasswordRecoveryFragment
import com.example.myapplication.LoginViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_password_recovery.*
import kotlinx.android.synthetic.main.activity_password_recovery.questionnaire_phone_additional
import kotlinx.android.synthetic.main.activity_password_recovery.questionnaire_phone_list_country
import kotlinx.android.synthetic.main.actyviti_questionnaire.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.ArrayList

class PasswordRecoveryActivity : AppCompatActivity(), ListenerGeneralResult {
    private var viewModel = LoginViewModel()
    private var myModel = PasswordViewMode()
    private var numberCharacters: Int = 0
    private var inputsAnim = true
    private var availableCountry = 0
    private var reNum = ""

    private var countryPosition = ""

    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var listAvailableCountry: ArrayList<CounterResultModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)
        HomeActivity.alert = LoadingAlert(this)
        iniClick()
        initView()
        initToolBar()
        getListCountry()
    }

    private fun iniClick() {

        questionnaire_phone_additional.addTextChangedListener {
            editUtils(layout_phone_additional, questionnaire_phone_additional, phone_additional_error, "Заполните поле", false)
            initCleaningRoom()
        }

        questionnaire_phone_list_country.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listAvailableCountry.size) {
                    if (i <= listAvailableCountry.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listAvailableCountry[i - 1].name.toString(), "listTypeWork", i - 1, 0, listAvailableCountry[i - 1].name.toString()))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, countryPosition, "Список доступных стран")
            }
        }

        password_recovery_enter.setOnClickListener {
            if (validate()) {
                initResult()
            }
        }

        no_connection_repeat.setOnClickListener {
            if (numberCharacters != 0) {
                initResult()
            } else {
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

    // TODO: 21-2-12 Получает информацию из адаптера
    override fun listenerClickResult(model: GeneralDialogModel) {
        if (model.key == "listTypeWork") {
            questionnaire_phone_list_country.error = null
            questionnaire_phone_additional.error = null
            password_recovery_word.error = null
            questionnaire_phone_additional.setText("")
            questionnaire_phone_additional.mask = ""
            questionnaire_phone_list_country.setText("+" + listAvailableCountry[model.position].phoneCode.toString())
            countryPosition = listAvailableCountry[model.position].name.toString()
//            AppPreferences.numberCharacters = listAvailableCountry[model.position].phoneLength!!.toInt()
            availableCountry = listAvailableCountry[model.position].phoneLength!!.toInt()
//            AppPreferences.isFormatMask = listAvailableCountry[model.position].phoneMaskSmall
            numberCharacters = listAvailableCountry[model.position].phoneLength!!.toInt()
            questionnaire_phone_additional.mask = listAvailableCountry[model.position].phoneMaskSmall
        }
    }

    //метод удаляет все символы из строки
    private fun initCleaningRoom() {
        if (questionnaire_phone_additional.text.toString() != "") {
            val matchedResults =
                Regex(pattern = """\d+""").findAll(input = questionnaire_phone_list_country.text.toString() + questionnaire_phone_additional.text.toString())
            val result = StringBuilder()
            for (matchedText in matchedResults) {
                reNum = result.append(matchedText.value).toString()
            }
        } else {
            reNum = ""
        }
    }

    private fun initToolBar() {
        setSupportActionBar(password_toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
    }

    fun initResult() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            recovery_no_questionnaire.visibility = View.VISIBLE
            password_layout.visibility = View.GONE
        } else {
            val map = HashMap<String, String>()
            map.put("phone", reNum)
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
                    Status.NETWORK -> {
                        if (msg == "600") {
                            recovery_no_questionnaire.visibility = View.GONE
                            password_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        } else {
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

    fun initVisibilities() {
        recovery_no_questionnaire.visibility = View.GONE
        password_layout.visibility = View.VISIBLE
    }

    private fun initAuthorized() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        //меняет цвет статус бара
        ColorWindows(this).statusBarTextColor()
//        questionnaire_phone_additional.mask = AppPreferences.isFormatMask
        password_focus_text.requestFocus()
//        getListCountry()
    }

    private fun initBusyBottomSheetError() {
        val bottomSheetDialogFragment = PasswordRecoveryErrorFragment()
        inputsAnim = false
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initBusyBottomSheet() {
        val bottomSheetDialogFragment = PasswordRecoveryFragment()
        inputsAnim = false
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
                                listAvailableCountry = data.result
                                countryPosition = result.data.result[0].name.toString()
                                questionnaire_phone_list_country.setText("+" + result.data.result[0].phoneCode)
                                questionnaire_phone_additional.mask = result.data.result[0].phoneMaskSmall
                                availableCountry = result.data.result[0].phoneLength!!.toInt()
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
                        Status.NETWORK -> {
                            if (msg == "600") {
                                recovery_no_questionnaire.visibility = View.GONE
                                recovery_technical_work.visibility = View.VISIBLE
                                password_layout.visibility = View.GONE
                            } else {
                                recovery_technical_work.visibility = View.GONE
                                recovery_no_questionnaire.visibility = View.VISIBLE
                                password_layout.visibility = View.GONE
                            }
                        }
                    }
                    HomeActivity.alert.hide()
                })
        }
    }

    //очещает список
    private fun initClearList() {
        itemDialog.clear()
    }

    //Вызов деалоговова окна с отоброжением получаемого списка.
    private fun initBottomSheet(list: ArrayList<GeneralDialogModel>, selectionPosition: String, title: String) {
        val stepBottomFragment = GeneralDialogFragment(this, list, selectionPosition, title)
        stepBottomFragment.show(supportFragmentManager, stepBottomFragment.tag)
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

        if (questionnaire_phone_additional.text!!.isNotEmpty()) {
            if (reNum.length != availableCountry) {
                editUtils(layout_phone_additional, questionnaire_phone_additional, phone_additional_error, "Ввидите правильный номер", true)
                valid = false
            }
        }else{
            editUtils(layout_phone_additional, questionnaire_phone_additional, phone_additional_error, "Заполните поле", true)
            valid = false
        }


        if (password_recovery_word.text.toString().isEmpty()) {
            editUtils(password_recovery_word, recovery_word_error, "Заполните поле", true)
            valid = false
        }

        if (!valid) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }
        return valid
    }

    private fun initView(){
        password_recovery_word.addTextChangedListener {
            editUtils(password_recovery_word, recovery_word_error, "Заполните поле", false)
        }
    }
}