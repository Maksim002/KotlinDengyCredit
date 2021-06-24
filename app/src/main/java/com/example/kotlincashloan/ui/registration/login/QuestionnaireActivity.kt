package com.example.kotlinscreenscanner.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.common.GeneralDialogFragment
import com.example.kotlincashloan.extension.*
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.service.model.CounterResultModel
import com.example.kotlinscreenscanner.service.model.ListGenderResultModel
import com.example.kotlinscreenscanner.service.model.ListNationalityResultModel
import com.example.kotlinscreenscanner.service.model.ListSecretQuestionResultModel
import com.example.kotlinscreenscanner.ui.login.fragment.AuthorizationBottomSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.AuthorizationBusyBottomFragment
import com.example.myapplication.LoginViewModel
import com.example.spinnerdatepickerlib.DatePicker
import com.example.spinnerdatepickerlib.DatePickerDialog
import com.example.spinnerdatepickerlib.SpinnerDatePickerDialogBuilder
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.actyviti_questionnaire.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.text.SimpleDateFormat
import java.util.*

class QuestionnaireActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener, ListenerGeneralResult {
    private var viewModel = LoginViewModel()
    private var data: String = ""
    private var idSex: Int = 0
    private var listNationalityId: Int = 0
    private var listSecretQuestionId: Int = 0
    private var listNationalityCounterId: Int = 0
    private var included = false
    private val onCancel: DatePickerDialog.OnDateCancelListener? = null
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var yearSelective = 0
    private var monthSelective = 0
    private var dayOfMonthSelective = 0
    private var codeMack = ""
    private var reNum = ""
    private var nationalityCounter = 0
    private var inputsAnim = true

    private var genderPosition = ""
    private var nationalityPosition = ""
    private var questionPosition = ""
    private var counterNationalPosition = ""

    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var listGender: ArrayList<ListGenderResultModel> = arrayListOf()
    private var listNationality: ArrayList<ListNationalityResultModel> = arrayListOf()
    private var listSecretQuestion: ArrayList<ListSecretQuestionResultModel> = arrayListOf()
    private var listNationalityCounter: ArrayList<CounterResultModel> = arrayListOf()
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actyviti_questionnaire)
        HomeActivity.alert = LoadingAlert(this)

        //формат даты
        simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)

        initToolBar()
        iniData()
        getIdSxs()
        initClock()
        initViews()
        initCheck()
    }

    private fun initCheck() {
        questionnaire_enter.setOnClickListener {
            if (validate()) {
                questionnaire_enter.isEnabled = false
                initResult()
            }
        }

        no_connection_repeat.setOnClickListener {
            if (idSex != 0 && listSecretQuestionId != 0 && listNationalityId != 0){
                initResult()
            }else{
                getIdSxs()
            }
        }

        access_restricted.setOnClickListener {
            if (idSex != 0 && listSecretQuestionId != 0 && listNationalityId != 0){
                initResult()
            }else{
                getIdSxs()
            }
        }

        not_found.setOnClickListener {
            if (idSex != 0 && listSecretQuestionId != 0 && listNationalityId != 0){
                initResult()
            }else{
                getIdSxs()
            }
        }

        technical_work.setOnClickListener {
            if (idSex != 0 && listSecretQuestionId != 0 && listNationalityId != 0){
                initResult()
            }else{
                getIdSxs()
            }
        }
    }

    private fun nouInternet(){
        questionnaire_no_questionnaire.visibility = View.VISIBLE
        questionnaire_layout.visibility = View.GONE
        questionnaire_technical_work.visibility = View.GONE
        questionnaire_not_found.visibility = View.GONE
        questionnaire_access_restricted.visibility = View.GONE
        layout_questionnaire.visibility = View.GONE
    }

    private fun initResult() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            nouInternet()
        } else {
            val map = mutableMapOf<String, String>()
            map["last_name"] = questionnaire_text_surnames.text.toString()
            map["first_name"] = questionnaire_text_name.text.toString()
            map["second_name"] = questionnaire_text_patronymics.text.toString()
            map["u_date"] = data
            map["gender"] = idSex.toString()
            map["nationality"] = listNationalityId.toString()

            val matchedResults = Regex(pattern = """\d+""").findAll(input = questionnaire_phone_number.text.toString())
            val result = StringBuilder()
            for (matchedText in matchedResults) {
               val firstNum = result.append(matchedText.value).toString()
                map["first_phone"] = firstNum
            }
            map["second_phone"] = try {
                reNum
            } catch (e: Exception) {
                ""
            }
            map["question"] = listSecretQuestionId.toString()
            map["response"] = questionnaire_secret_response.text.toString()
            map["sms_code"] = AppPreferences.receivedSms.toString()
            map.put("uid", AppPreferences.pushNotificationsId.toString())
            map.put("system", "1")
            HomeActivity.alert.show()
            viewModel.questionnaire(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result == null) {
                            if (data.error.code != 409) {
                                result()
                                loadingMistake(this)
                            } else if (data.error.code == 401) {
                                initAuthorized()
                            } else if (data.error.code == 400 || data.error.code == 500 || data.error.code == 403) {
                                result()
                                loadingMistake(this)
                            } else {
                                result()
                                initBusyBottomSheet()
                            }
                        } else {
                            result()
                            initBottomSheet()
                        }
                    }
                    Status.ERROR ->{
                        if (msg == "401") {
                            initAuthorized()
                        } else if (msg == "409") {
                            result()
                            loadingMistake(this)
                        } else {
                            result()
                            loadingMistake(this)
                        }
                    }
                    Status.NETWORK ->{
                        if (msg == "601"){
                            result()
                            loadingMistake(this)
                        }else if (msg == "600"){
                            result()
                            loadingConnection(this)
                        }
                    }
                }
                questionnaire_enter.isEnabled = true
                HomeActivity.alert.hide()
            })
        }
    }

    private fun initClock() {

        questionnaire_phone_additional.addTextChangedListener {
            editUtils(questionnaire_layout_additional, questionnaire_phone_additional, questionnaire_additional_error, "Введите правильный номер", false)
            initCleaningRoom()
        }

        questionnaire_available_countries.setOnClickListener {
            questionnaire_available_countries.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listNationalityCounter.size) {
                    if (i <= listNationalityCounter.size) {
                        itemDialog.add(GeneralDialogModel(listNationalityCounter[i - 1].name.toString(), "listNationalityCounter", i - 1, 0, listNationalityCounter[i-1].name))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, counterNationalPosition, "Список доступных стран", questionnaire_available_countries)
            }
        }

        questionnaire_id_sxs.setOnClickListener {
            questionnaire_id_sxs.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listGender.size) {
                    if (i <= listGender.size) {
                        itemDialog.add(GeneralDialogModel(listGender[i - 1].name.toString(), "listGender", i - 1, 0, listGender[i - 1].name.toString()))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, genderPosition, "Выберите пол", questionnaire_id_sxs)
            }
        }

        questionnaire_id_nationality.setOnClickListener {
            questionnaire_id_nationality.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listNationality.size) {
                    if (i <= listNationality.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listNationality[i - 1].name.toString(),
                                "listNationality",
                                i - 1
                            , 0)
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, nationalityPosition, "Выберите гражданство", questionnaire_id_nationality)
            }
        }

        questionnaire_id_secret.setOnClickListener {
            questionnaire_id_secret.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listSecretQuestion.size) {
                    if (i <= listSecretQuestion.size) {
                        itemDialog.add(GeneralDialogModel(listSecretQuestion[i - 1].name.toString(), "listSecretQuestion", i - 1, listSecretQuestion[i-1].id))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, questionPosition, "Выберите секретный вопрос", questionnaire_id_secret)
            }
        }

        questionnaire_agreement.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                questionnaire_enter.isClickable = true
                questionnaire_enter.setBackgroundColor(resources.getColor(R.color.orangeColor))
                included = true
            }else{
                questionnaire_enter.isClickable = false
                questionnaire_enter.setBackgroundColor(resources.getColor(R.color.blueColor))
                included = false
            }
        }

        questionnaire_registration.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
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

    private fun initAuthorized(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun initBottomSheet() {
        val bottomSheetDialogFragment = AuthorizationBottomSheetFragment()
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initBusyBottomSheet() {
        val bottomSheetDialogFragment = AuthorizationBusyBottomFragment()
        bottomSheetDialogFragment.isCancelable = false;
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun initToolBar() {
        setSupportActionBar(questionnaire_toolbar)
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
        //меняет цвет статус бара
        ColorWindows(this).statusBarTextColor()
        if (!included){
            questionnaire_enter.isClickable = false
            questionnaire_enter.setBackgroundColor(resources.getColor(R.color.blueColor))
            questionnaire_phone_number.setText(AppPreferences.number.toString())
        }else{
            questionnaire_enter.isClickable = true
            questionnaire_enter.setBackgroundColor(resources.getColor(R.color.orangeColor))
        }
    }

    private fun iniData() {
        questionnaire_date_birth.setOnClickListener(View.OnClickListener { v: View? ->
            questionnaire_date_birth.isEnabled = false
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                questionnaire_date_birth.isEnabled = true
            }, 900)

            if (yearSelective != 0 && monthSelective != 0 && dayOfMonthSelective != 0) {
                showDate(yearSelective, monthSelective, dayOfMonthSelective, R.style.DatePickerSpinner)
            }else{
                showDate(1990, 1, 1, R.style.DatePickerSpinner)
            }
        })
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar: Calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
        questionnaire_date_birth.setText(simpleDateFormat.format(calendar.getTime()))
        data = (MyUtils.convertDate(year, monthOfYear + 1, dayOfMonth))
        yearSelective = year
        monthSelective = monthOfYear
        dayOfMonthSelective = dayOfMonth
    }

    @VisibleForTesting
    fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        SpinnerDatePickerDialogBuilder()
            .context(this)
            .callback(this)
            .onCancel(onCancel)
            .spinnerTheme(spinnerTheme)
            .defaultDate(year, monthOfYear, dayOfMonth)
            .build()
            .show()
    }

    //Регистрация. Выбо пола.
    private fun getIdSxs() {
         ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            nouInternet()
        } else {
            val map = HashMap<String, Int>()
            map.put("id", 0)
//            HomeActivity.alert.show()
            shimmerStart(shimmer_questionnaire, this)
            viewModel.listGender(map).observe(this, androidx.lifecycle.Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result != null) {
                            listGender = data.result
                            result()
                            getListNationality()
                        } else {
                            if (data.error != null){
                                errorMessage(data.error.code.toString())
                            }else{
                                errorMessage(data.code.toString())
                            }
                        }
                    }
                    Status.ERROR -> {
                        errorMessage(msg.toString())
                    }
                    Status.NETWORK -> {
                        netWork(msg.toString())
                    }
                }
//                HomeActivity.alert.hide()
            })
        }
    }

    // Регистрация. Список доступных стран
    private fun getListCountry() {
         ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            nouInternet()
        } else {
            val map = HashMap<String, Int>()
            map.put("id", 0)
//            HomeActivity.alert.show()
            viewModel.listAvailableCountry(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result != null) {
                            listNationalityCounter = data.result
                            counterNationalPosition = listNationalityCounter[0].name.toString()
                            questionnaire_available_countries.setText("+" + result.data.result[0].phoneCode.toString())
                            questionnaire_phone_additional.mask = result.data.result[0].phoneMaskSmall
                            nationalityCounter = result.data.result[0].phoneLength!!.toInt()
                            result()
                        } else {
                            if (data.error != null){
                                errorMessage(data.error.code.toString())
                            }else{
                                errorMessage(data.code.toString())
                            }
                        }
                    }
                    Status.ERROR -> {
                        errorMessage(msg.toString())
                    }
                    Status.NETWORK -> {
                        netWork(msg.toString())
                    }
                }
//                HomeActivity.alert.hide()
                shimmerStop(shimmer_questionnaire, this)
            })
        }
    }



    //Регистрация получение грожданство
    private fun getListNationality() {
         ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            nouInternet()
        } else {
            val map = HashMap<String, Int>()
            map.put("id", 0)
//            HomeActivity.alert.show()
            viewModel.listNationality(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result != null) {
                            listNationality = data.result
                            result()
                            getAutoOperation()
                        } else {
                            if (data.error != null){
                                errorMessage(data.error.code.toString())
                            }else{
                                errorMessage(data.code.toString())
                            }
                        }
                    }
                    Status.ERROR -> {
                        errorMessage(msg.toString())
                    }
                    Status.NETWORK -> {
                        netWork(msg.toString())
                    }
                }
//                HomeActivity.alert.hide()
            })
        }
    }

    // Регистрация выбор секретного вопроса
    private fun getAutoOperation() {
         ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            nouInternet()
        } else {
            val map = HashMap<String, Int>()
            map.put("id", 0)
//            HomeActivity.alert.show()
            viewModel.listSecretQuestion(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result != null) {
                            listSecretQuestion = data.result
                            result()
                            getListCountry()
                        } else {
                            if (data.error != null){
                                errorMessage(data.error.code.toString())
                            }else{
                                errorMessage(data.code.toString())
                            }
                        }
                    }
                    Status.ERROR -> {
                        errorMessage(msg.toString())
                    }
                    Status.NETWORK -> {
                        netWork(msg.toString())
                    }
                }
//                HomeActivity.alert.hide()
                shimmerStop(shimmer_questionnaire, this)
            })
        }
    }

    private fun result(){
        questionnaire_no_questionnaire.visibility = View.GONE
        questionnaire_technical_work.visibility = View.GONE
        questionnaire_not_found.visibility = View.GONE
        questionnaire_access_restricted.visibility = View.GONE
        layout_questionnaire.visibility = View.VISIBLE
        questionnaire_layout.visibility = View.VISIBLE
    }

    private fun errorMessage(msg: String){
        if (msg == "404") {
            layout_questionnaire.visibility = View.GONE
            questionnaire_no_questionnaire.visibility = View.GONE
            questionnaire_not_found.visibility = View.VISIBLE
            questionnaire_layout.visibility = View.GONE

        } else if (msg == "403") {
            layout_questionnaire.visibility = View.GONE
            questionnaire_no_questionnaire.visibility = View.GONE
            questionnaire_access_restricted.visibility = View.VISIBLE
            questionnaire_layout.visibility = View.GONE

        } else if (msg == "401") {
            initAuthorized()
        } else {
            layout_questionnaire.visibility = View.GONE
            questionnaire_no_questionnaire.visibility = View.GONE
            questionnaire_technical_work.visibility = View.VISIBLE
            questionnaire_layout.visibility = View.GONE
        }
    }

    private fun netWork(msg: String){
        if (msg == "601") {
            layout_questionnaire.visibility = View.GONE
            questionnaire_no_questionnaire.visibility = View.GONE
            questionnaire_layout.visibility = View.GONE
            questionnaire_technical_work.visibility = View.VISIBLE
        } else if (msg == "600"){
            layout_questionnaire.visibility = View.GONE
            questionnaire_no_questionnaire.visibility = View.VISIBLE
            questionnaire_layout.visibility = View.GONE
            questionnaire_technical_work.visibility = View.GONE
            questionnaire_not_found.visibility = View.GONE
            questionnaire_access_restricted.visibility = View.GONE
        }
    }

    // TODO: 21-2-12 Получает информацию из адаптера
    override fun listenerClickResult(model: GeneralDialogModel) {
        if (model.key == "listGender") {
            questionnaire_id_sxs.isEnabled = true
            questionnaire_id_sxs.setText(listGender[model.position].name)
            genderPosition = listGender[model.position].name.toString()
            idSex = listGender[model.position].id!!.toInt()
            questionnaire_id_sxs.error = null
        }

        if (model.key == "listNationality") {
            questionnaire_id_nationality.isEnabled = true
            questionnaire_id_nationality.setText(listNationality[model.position].name)
            nationalityPosition = listNationality[model.position].name.toString()
            listNationalityId = listNationality[model.position].id!!.toInt()
            questionnaire_id_nationality.error = null
        }

        if (model.key == "listSecretQuestion") {
            questionnaire_id_secret.isEnabled = true
            questionnaire_id_secret.setText(listSecretQuestion[model.position].name)
            questionPosition = listSecretQuestion[model.position].name.toString()
            listSecretQuestionId = listSecretQuestion[model.position].id!!.toInt()
            questionnaire_id_secret.error = null
        }

        if (model.key == "listNationalityCounter") {
            questionnaire_available_countries.isEnabled = true
            questionnaire_phone_additional.mask = null
            questionnaire_phone_additional.error = null
            questionnaire_phone_additional.text = null
            questionnaire_available_countries.setText("+" + listNationalityCounter[model.position].phoneCode.toString())
            counterNationalPosition = listNationalityCounter[model.position].name.toString()
            listNationalityCounterId = listNationalityCounter[model.position].id!!.toInt()
            nationalityCounter = listNationalityCounter[model.position].phoneLength!!.toInt()
            codeMack = listNationalityCounter[model.position].phoneCode.toString()
            questionnaire_phone_additional.mask = listNationalityCounter[model.position].phoneMaskSmall
        }
    }

    //метод удаляет все символы из строки
    private fun initCleaningRoom(){
        if (questionnaire_phone_additional.text.toString() != "") {
            val matchedResults = Regex(pattern = """\d+""").findAll(input = questionnaire_available_countries.text.toString() + questionnaire_phone_additional.text.toString())
            val result = StringBuilder()
            for (matchedText in matchedResults) {
                reNum = result.append(matchedText.value).toString()
            }
        }else{
            reNum = ""
        }
    }

    //очещает список
    private fun initClearList() {
        itemDialog.clear()
    }

    //Вызов деалоговова окна с отоброжением получаемого списка.
    private fun initBottomSheet(list: ArrayList<GeneralDialogModel>, selectionPosition: String, title: String, id: AutoCompleteTextView) {
        val stepBottomFragment = GeneralDialogFragment(this, list, selectionPosition, title, id)
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(supportFragmentManager, stepBottomFragment.tag)
    }

    override fun onResume() {
        super.onResume()
        if (inputsAnim) {
            TransitionAnimation(this).transitionRight(question_layout_anim)
            inputsAnim = false
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (questionnaire_text_surnames.text.toString().isEmpty()) {
            editUtils(questionnaire_text_surnames, questionnaire_surnames_error, "Заполните поле", true)
            valid = false
        }

        if (questionnaire_phone_additional.text!!.isNotEmpty()){
            if (reNum.length != nationalityCounter) {
                editUtils(questionnaire_layout_additional, questionnaire_phone_additional, questionnaire_additional_error, "Введите правильный номер", true)
                valid = false
            }
        }


        if (questionnaire_text_name.text.toString().isEmpty()) {
            editUtils(questionnaire_text_name, questionnaire_name_error, "Заполните поле", true)
            valid = false
        }

        if (questionnaire_date_birth.text!!.toString().isEmpty()) {
            editUtils(questionnaire_date_birth, questionnaire_birth_error, "Выберите дату", true)
            valid = false
        }

        if (questionnaire_id_sxs.text!!.toString().isEmpty()) {
            editUtils(questionnaire_id_sxs, questionnaire_sxs_error, "Выберите из списка", true)
            valid = false
        }

        if (questionnaire_id_nationality.text.toString().isEmpty()) {
            editUtils(questionnaire_id_nationality, questionnaire_nationality_error, "Выберите из списка", true)
            valid = false
        }

        if (questionnaire_id_secret.text.toString().isEmpty()) {
            editUtils(questionnaire_id_secret, questionnaire_secret_error, "Выберите из списка", true)
            valid = false
        }

        if (questionnaire_secret_response.text.toString().isEmpty()) {
            editUtils(questionnaire_secret_response, questionnaire_sresponse_error, "Заполните поле", true)
            valid = false
        }
        if (!valid){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }
        return valid
    }

    private fun initViews() {
        questionnaire_text_surnames.addTextChangedListener {
            editUtils(questionnaire_text_surnames, questionnaire_surnames_error, "Заполните поле", false)
        }

        questionnaire_text_name.addTextChangedListener {
            editUtils(questionnaire_text_name, questionnaire_name_error, "Заполните поле", false)
        }

        questionnaire_date_birth.addTextChangedListener {
            editUtils(questionnaire_date_birth, questionnaire_birth_error, "Выберите дату", false)
        }

        questionnaire_id_sxs.addTextChangedListener {
            editUtils(questionnaire_id_sxs, questionnaire_sxs_error, "Выберите из списка", false)
        }

        questionnaire_id_nationality.addTextChangedListener {
            editUtils(questionnaire_id_nationality, questionnaire_nationality_error, "Выберите из списка", false)
        }

        questionnaire_id_secret.addTextChangedListener {
            editUtils(questionnaire_id_secret, questionnaire_secret_error, "Выберите из списка", false)
        }

        questionnaire_secret_response.addTextChangedListener {
            editUtils(questionnaire_secret_response, questionnaire_sresponse_error, "Заполните поле", false)
        }

    }

}