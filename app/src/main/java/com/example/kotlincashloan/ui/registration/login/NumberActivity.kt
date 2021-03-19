package com.example.kotlinscreenscanner.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.common.GeneralDialogFragment
import com.example.kotlincashloan.extension.editUtils
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
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
import kotlinx.android.synthetic.main.actyviti_questionnaire.*
import kotlinx.android.synthetic.main.fragment_loan_step_six.*
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.*
import kotlin.collections.HashMap

class NumberActivity : AppCompatActivity(), ListenerGeneralResult {
    private var viewModel = LoginViewModel()
    private var numberCharacters: Int = 0
    private var repeatAnim = false
    private var reNum = ""

    val bottomSheetDialogFragment = NumberBusyBottomSheetFragment()

    private var сountryPosition = ""

    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var listAvailableCountry: ArrayList<CounterResultModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number)
        HomeActivity.alert = LoadingAlert(this)
        initClick()
        initViews()
        initToolBar()
    }

    private fun initResult() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            number_no_connection.visibility = View.VISIBLE
            number_layout.visibility = View.GONE
        } else {
            val map = HashMap<String, String>()
            map.put("phone", reNum)
            HomeActivity.alert.show()
            no_connection_repeat.isEnabled = false
            viewModel.numberPhones(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result == null) {
                            if (data.error.code == 500 || data.error.code == 400 || data.error.code == 404) {
                                number_no_connection.visibility = View.GONE
                                number_layout.visibility = View.VISIBLE
                                loadingMistake(this)
                            } else if (data.error.code == 401) {
                                initAuthorized()
                            } else if (data.error.code == 409) {
                                initBusyBottomSheet()
                                initVisibilities()
                            } else {
                                number_no_connection.visibility = View.GONE
                                number_layout.visibility = View.VISIBLE
                                initVisibilities()
                            }
                        } else {
                            AppPreferences.number =
                                number_list_country.text.toString() + number_phone.text.toString()
                            initBottomSheet(data.result.id!!)
                            initVisibilities()
                        }
                    }
                    Status.ERROR -> {
                        if (msg == "500" || msg == "400" || msg == "404" || msg == "429") {
                            number_layout.visibility = View.GONE
                            initVisibilities()
                            loadingMistake(this)
                        } else if (msg == "401") {
                            initAuthorized()
                        } else if (msg == "409") {
                            initBusyBottomSheet()
                            initVisibilities()
                        } else {
                            loadingMistake(this)
                            number_no_connection.visibility = View.GONE
                            number_layout.visibility = View.VISIBLE
                        }
                    }
                    Status.NETWORK -> {
                        if (msg == "600") {
                            number_layout.visibility = View.GONE
                            initVisibilities()
                            loadingMistake(this)
                        } else {
                            number_no_connection.visibility = View.VISIBLE
                            number_layout.visibility = View.GONE
                        }
                    }
                }
                number_next.isEnabled = true
                HomeActivity.alert.hide()
                no_connection_repeat.isEnabled = true
            })
        }
    }

    private fun initBottomSheet(id: Int) {
        val bottomSheetDialogFragment = NumberBottomSheetFragment(id)
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initBusyBottomSheet() {
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        //меняет цвет статус бара
        ColorWindows(this).statusBarTextColor()
//        number_phone.visibility = View.GONE
        number_focus_text.requestFocus()
        getListCountry()
    }

    private fun initClick() {

        number_phone.addTextChangedListener {
            editUtils(
                number_layout_phone,
                number_phone,
                number_phone_error,
                "Ввидите правильный номер",
                false
            )
            initCleaningRoom()
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

        number_next.setOnClickListener {
            if (validate()) {
                number_next.isEnabled = false
                initResult()
            }
        }

        number_list_country.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listAvailableCountry.size) {
                    if (i <= listAvailableCountry.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listAvailableCountry[i - 1].name.toString(),
                                "listAvailableCountry",
                                i - 1,
                                0,
                                listAvailableCountry[i - 1].name
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, сountryPosition, "Список доступных стран")
            }
        }
    }

    private fun initToolBar() {
        setSupportActionBar(number_toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
    }

    fun initVisibilities() {
        number_layout.visibility = View.VISIBLE
        number_no_connection.visibility = View.GONE
    }

    //метод удаляет все символы из строки
    private fun initCleaningRoom() {
        if (number_phone.text.toString() != "") {
            val matchedResults =
                Regex(pattern = """\d+""").findAll(input = number_list_country.text.toString() + number_phone.text.toString())
            val result = StringBuilder()
            for (matchedText in matchedResults) {
                reNum = result.append(matchedText.value).toString()
            }
        } else {
            reNum = ""
        }
    }

    // Регистрация. Список доступных стран
    private fun getListCountry() {
        val map = HashMap<String, Int>()
        map.put("id", 0)
        HomeActivity.alert.show()
        viewModel.listAvailableCountry(map).observe(this, androidx.lifecycle.Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        number_phone.mask = ""
                        number_phone.text = null
                        number_list_country.setText("+" + result.data.result[0].phoneCode.toString())
                        number_phone.mask = result.data.result[0].phoneMaskSmall
                        numberCharacters = result.data.result[0].phoneLength!!.toInt()
                        сountryPosition = result.data.result[0].name.toString()
                        listAvailableCountry = data.result
                        initVisibilities()
                    } else {
                        if (data.error.code == 403) {
                            number_no_connection.visibility = View.GONE
                            number_access_restricted.visibility = View.VISIBLE
                            number_layout.visibility = View.GONE

                        } else if (data.error.code == 404) {
                            number_no_connection.visibility = View.GONE
                            number_not_found.visibility = View.VISIBLE
                            number_layout.visibility = View.GONE

                        } else if (data.error.code == 401) {
                            initAuthorized()
                        } else {
                            number_no_connection.visibility = View.GONE
                            number_technical_work.visibility = View.VISIBLE
                            number_layout.visibility = View.GONE
                        }
                    }
                }
                Status.ERROR -> {
                    if (msg == "404") {
                        number_no_connection.visibility = View.GONE
                        number_not_found.visibility = View.VISIBLE
                        number_layout.visibility = View.GONE

                    } else if (msg == "403") {
                        number_no_connection.visibility = View.GONE
                        number_access_restricted.visibility = View.VISIBLE
                        number_layout.visibility = View.GONE

                    } else if (msg == "401") {
                        initAuthorized()
                    } else {
                        number_no_connection.visibility = View.GONE
                        number_technical_work.visibility = View.VISIBLE
                        number_layout.visibility = View.GONE
                    }
                }
                Status.NETWORK -> {
                    if (msg == "601") {
                        number_no_connection.visibility = View.VISIBLE
                        number_layout.visibility = View.GONE
                        number_access_restricted.visibility = View.GONE
                        number_not_found.visibility = View.GONE
                        number_technical_work.visibility = View.GONE
                        if (bottomSheetDialogFragment.isResumed == true) {
                            bottomSheetDialogFragment.dismiss()
                        }
                    } else {
                        number_no_connection.visibility = View.GONE
                        number_technical_work.visibility = View.VISIBLE
                        number_layout.visibility = View.GONE
                    }
                }
            }
            HomeActivity.alert.hide()
        })
    }

    // TODO: 21-2-12 Получает информацию из адаптера
    override fun listenerClickResult(model: GeneralDialogModel) {
        if (model.key == "listAvailableCountry") {
            number_phone.error = null
            //Очещает старую маску при выборе новой
            number_phone.mask = ""
            // Очещает поле
            number_phone.text = null
            AppPreferences.numberCharacters =
                listAvailableCountry[model.position].phoneLength!!.toInt()
            AppPreferences.isFormatMask = listAvailableCountry[model.position].phoneMask
            number_list_country.setText("+" + listAvailableCountry[model.position].phoneCode.toString())
            сountryPosition = listAvailableCountry[model.position].name.toString()
            numberCharacters = listAvailableCountry[model.position].phoneLength!!.toInt()
            number_phone.mask = listAvailableCountry[model.position].phoneMaskSmall
        }
    }

    //очещает список
    private fun initClearList() {
        itemDialog.clear()
    }

    //Вызов деалоговова окна с отоброжением получаемого списка.
    private fun initBottomSheet(
        list: ArrayList<GeneralDialogModel>,
        selectionPosition: String,
        title: String
    ) {
        val stepBottomFragment = GeneralDialogFragment(this, list, selectionPosition, title)
        stepBottomFragment.show(supportFragmentManager, stepBottomFragment.tag)
    }

    private fun initAuthorized() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun validate(): Boolean {
        var valid = true
        if (number_list_country.text.isEmpty()) {
            number_list_country.error = "Выберите страну"
            valid = false
        } else {
            number_list_country.error = null
        }

        if (number_phone.text!!.isNotEmpty()) {
            if (reNum.length != numberCharacters) {
                editUtils(number_layout_phone, number_phone, number_phone_error, "Ввидите правильный номер", true)
                valid = false
            }
        }else{
            editUtils(number_layout_phone, number_phone, number_phone_error, "Заполните поле", true)
            valid = false
        }

        if (!valid) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }
        return valid
    }

    override fun onResume() {
        super.onResume()
        if (!repeatAnim) {
            TransitionAnimation(this).transitionRightActivity(number_layout_anim)
            repeatAnim = true
        }
    }

    private fun initViews() {
        number_list_country.addTextChangedListener {
            number_list_country.error = null
        }
    }
}