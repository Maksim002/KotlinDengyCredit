package com.example.kotlincashloan.ui.registration.recovery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
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
import com.example.kotlincashloan.service.model.recovery.ListSupportTypeResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.ui.registration.recovery.fragment.BottomDialogListener
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TimerListener
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.kotlinscreenscanner.ui.login.fragment.MistakeBottomSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.ShippedSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.YourApplicationFragment
import com.example.myapplication.LoginViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_contacting_service.*
import kotlinx.android.synthetic.main.activity_contacting_service.questionnaire_phone_additional
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class ContactingServiceActivity : AppCompatActivity(), ListenerGeneralResult, BottomDialogListener {
    private var viewModel = LoginViewModel()
    private var numberCharacters: Int = 0
    private var myViewMode = PasswordViewMode()
    private var typeId: Int = 0
    private val handler = Handler()
    private var profNumber = ""
    private var reNum = ""
    private var valod = false
    private var discovery = false
    private var countryCode = ""
    private var typeCode = ""

    private var countryPosition = ""
    private var typePosition = ""

    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    var listAvailableCountry: ArrayList<CounterResultModel> = arrayListOf()
    var listSupportType: ArrayList<ListSupportTypeResultModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacting_service)

        //меняет цвет статус бара
        ColorWindows(this).statusBarTextColor()
        HomeActivity.alert = LoadingAlert(this)
        MainActivity.timer = TimerListener(this)

        initArgument()
        initToolBar()
        getListCountry()
        initView()
        iniClick()
    }

    private fun initArgument() {
        if (profNumber != "") {
            profNumber = intent.extras!!.getString("number").toString()
        }
    }

    private fun iniClick() {

        questionnaire_phone_additional.addTextChangedListener {
            editUtils(
                layout_phone_number,
                questionnaire_phone_additional,
                questionnaire_phone_additional_error,
                "Введите правильный номер",
                false
            )
            initCleaningRoom()
        }

        questionnaire_phone_list_country.setOnClickListener {
            questionnaire_phone_list_country.isEnabled = false
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
                                listAvailableCountry[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(
                    itemDialog,
                    countryPosition,
                    "Список доступных стран",
                    questionnaire_phone_list_country
                )
            }
        }

        password_recovery_type.setOnClickListener {
            password_recovery_type.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listSupportType.size) {
                    if (i <= listSupportType.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listSupportType[i - 1].name.toString(),
                                "listSupportType",
                                i - 1,
                                0,
                                listSupportType[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(
                    itemDialog,
                    typePosition,
                    "Выберите тему обращения",
                    password_recovery_type
                )
            }
        }

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

    // TODO: 21-2-12 Получает информацию из адаптера
    override fun listenerClickResult(model: GeneralDialogModel) {
        if (model.key == "listAvailableCountry") {
            questionnaire_phone_list_country.isEnabled = true
            questionnaire_phone_list_country.error = null
            questionnaire_phone_additional.setText("")
            questionnaire_phone_additional.error = null
            questionnaire_phone_additional.mask = ""
            questionnaire_phone_list_country.setText("+" + listAvailableCountry[model.position].phoneCode.toString())
            countryPosition = listAvailableCountry[model.position].name.toString()
            AppPreferences.numberCharacters =
                listAvailableCountry[model.position].phoneLength!!.toInt()
            AppPreferences.isFormatMask = listAvailableCountry[model.position].phoneMaskSmall
            numberCharacters = listAvailableCountry[model.position].phoneLength!!.toInt()
            questionnaire_phone_additional.mask =
                listAvailableCountry[model.position].phoneMaskSmall
        }

        if (model.key == "listSupportType") {
            password_recovery_type.isEnabled = true
            password_recovery_type.error = null
            password_recovery_type.setText(listSupportType[model.position].name)
            typePosition = listSupportType[model.position].name.toString()
            typeId = listSupportType[model.position].id!!
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

    //Метотд для скрытия клавиатуры
    private fun closeKeyboard() {
        val view: View = this.currentFocus!!
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            val manager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
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
            map["phone"] = reNum
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
                                if (!discovery) {
                                    loadingMistake(this, this)
                                    discovery = true
                                }
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
                            if (!discovery) {
                                loadingMistake(this, this)
                                discovery = true
                            }
                        } else {
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                            if (!discovery) {
                                loadingMistake(this, this)
                                discovery = true
                            }
                        }
                    }
                    Status.NETWORK -> {
                        if (msg == "600" || msg == "601") {
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                            if (!discovery) {
                                loadingMistake(this, this, profNumber)
                                discovery = true
                            }
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

    // Список доступхныйх стран
    private fun getListCountry() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            password_no_questionnaire.visibility = View.VISIBLE
            contacting_layout.visibility = View.GONE
            password_access_restricted.visibility = View.GONE
            password_not_found.visibility = View.GONE
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
                                countryCode = "200"
                                listAvailableCountry = data.result
                                countryPosition = data.result[0].name.toString()
                                questionnaire_phone_list_country.setText("+" + result.data.result[0].phoneCode)
                                questionnaire_phone_additional.mask = result.data.result[0].phoneMaskSmall
                                numberCharacters = result.data.result[0].phoneLength!!.toInt()
//                                if (typeCode == "200" || countryCode == "200"){
//                                    initVisibilities()
//                                }
                                getType()
                            } else {
                                countryCode = data.error.code.toString()
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
                                    if (!discovery) {
                                        loadingMistake(this, this)
                                        discovery = true
                                    }
                                    password_no_questionnaire.visibility = View.GONE
                                    contacting_layout.visibility = View.VISIBLE
                                }
                            }
                        }
                        Status.ERROR -> {
                            if (msg != null) {
                                countryCode = msg
                            }
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
                                if (!discovery) {
                                    loadingMistake(this, this)
                                    discovery = true
                                }
                                password_no_questionnaire.visibility = View.GONE
                                contacting_layout.visibility = View.VISIBLE
                            }
                        }
                        Status.NETWORK -> {
                            if (msg == "600" || msg == "601") {
                                if (!discovery) {
                                    loadingMistake(this, this)
                                    discovery = true
                                }
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
            val map = HashMap<String, Int>()
            map.put("id", 0)
            HomeActivity.alert.show()
            myViewMode.listSupportType(map).observe(this, androidx.lifecycle.Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result != null) {
                            typeCode = "200"
                            listSupportType = data.result
                            if (typeCode == "200" || countryCode == "200") {
                                initVisibilities()
                            }
                        } else {
                            typeCode = data.error.code.toString()
                            if (data.error.code == 403) {
                                password_no_questionnaire.visibility = View.GONE
                                password_access_restricted.visibility = View.VISIBLE
                                contacting_layout.visibility = View.GONE
                            } else if (data.error.code == 404) {
                                password_no_questionnaire.visibility = View.GONE
                                contacting_layout.visibility = View.GONE
                                password_not_found.visibility = View.VISIBLE
                            } else if (data.error.code == 401) {
                                initAuthorized()
                            } else {
                                if (!discovery) {
                                    loadingMistake(this, this)
                                    discovery = true
                                }
                                password_no_questionnaire.visibility = View.GONE
                                contacting_layout.visibility = View.VISIBLE
                            }
                        }
                    }
                    Status.ERROR -> {
                        if (msg != null) {
                            typeCode = msg
                        }
                        if (msg == "404") {
                            password_no_questionnaire.visibility = View.GONE
                            password_not_found.visibility = View.VISIBLE
                            contacting_layout.visibility = View.GONE
                        } else if (msg == "403") {
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.GONE
                            password_access_restricted.visibility = View.VISIBLE
                        } else if (msg == "401") {
                            initAuthorized()
                        } else {
                            if (!discovery) {
                                loadingMistake(this, this)
                                discovery = true
                            }
                            password_no_questionnaire.visibility = View.GONE
                            contacting_layout.visibility = View.VISIBLE
                        }
                    }
                    Status.NETWORK -> {
                        if (msg == "600" || msg == "601") {
                            if (!discovery) {
                                loadingMistake(this, this)
                                discovery = true
                            }
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
        }
    }

    //Слушаеть на 2 открытие loadingMistake(this, this)
    override fun bottomOnClickListener(boolean: Boolean) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        discovery = true
    }

    //очещает список
    private fun initClearList() {
        itemDialog.clear()
    }

    //Вызов деалоговова окна с отоброжением получаемого списка.
    private fun initBottomSheet(
        list: ArrayList<GeneralDialogModel>,
        selectionPosition: String,
        title: String,
        id: AutoCompleteTextView
    ) {
        val stepBottomFragment = GeneralDialogFragment(this, list, selectionPosition, title, id)
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(supportFragmentManager, stepBottomFragment.tag)
    }

    private fun validate(): Boolean {
        var valid = true
        if (questionnaire_phone_surnames.text.toString().isEmpty()) {
            editUtils(
                questionnaire_phone_surnames,
                questionnaire_phone_surnames_error,
                "Заполните поле",
                true
            )
            valid = false
        }

        if (questionnaire_phone_name.text.toString().isEmpty()) {
            editUtils(
                questionnaire_phone_name,
                questionnaire_phone_name_error,
                "Заполните поле",
                true
            )
            valid = false
        }

        if (password_recovery_type.text.isEmpty()) {
            editUtils(password_recovery_type, questionnaire_type_error, "Выберите из списка", true)
            valid = false
        }

        if (questionnaire_phone_additional.text!!.isNotEmpty()) {
            if (reNum.length != numberCharacters) {
                editUtils(
                    layout_phone_number,
                    questionnaire_phone_additional,
                    questionnaire_phone_additional_error,
                    "Введите правильный номер",
                    true
                )
                valid = false
            }
        } else {
            editUtils(
                layout_phone_number,
                questionnaire_phone_additional,
                questionnaire_phone_additional_error,
                "Заполните поле",
                true
            )
            valid = false
        }

        if (!valid) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }

        return valid
    }

    //Метод слушает изменение в полях
    private fun initView() {
        questionnaire_phone_surnames.addTextChangedListener {
            editUtils(
                questionnaire_phone_surnames,
                questionnaire_phone_surnames_error,
                "Заполните поле",
                false
            )
        }

        questionnaire_phone_name.addTextChangedListener {
            editUtils(
                questionnaire_phone_name,
                questionnaire_phone_name_error,
                "Заполните поле",
                false
            )
        }

        password_recovery_type.addTextChangedListener {
            editUtils(password_recovery_type, questionnaire_type_error, "Выберите из списка", false)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(Runnable { // Do something after 5s = 500ms
            MainActivity.timer.timeStop()
        }, 2000)
        if (!valod) {
            TransitionAnimation(this).transitionRight(contacts_layout_anim)
            valod = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (AppPreferences.token != "") {
            MainActivity.timer.timeStart()
            AppPreferences.isNumber = false
        }
    }
}