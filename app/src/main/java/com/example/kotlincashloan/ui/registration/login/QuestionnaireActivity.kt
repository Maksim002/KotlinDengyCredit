package com.example.kotlinscreenscanner.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingConnection
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.service.model.Loans.ListTrafficSource
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
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


class QuestionnaireActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener{
    private var viewModel = LoginViewModel()
    private var data: String = ""
    private var idSex: Int = 0
    private var listNationalityId: Int = 0
    private var listSecretQuestionId: Int = 0
    private var listАoundId: Int = 0
    private var included = false
    private val onCancel: DatePickerDialog.OnDateCancelListener? = null
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var yearSelective = 0
    private var monthSelective = 0
    private var dayOfMonthSelective = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actyviti_questionnaire)
        HomeActivity.alert = LoadingAlert(this)

        //формат даты
        simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)

        initToolBar()
        iniData()
        getListNationality()
        getIdSxs()
        getAutoOperation()
        gitFound()
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
            if (idSex != 0 && listSecretQuestionId != 0 && listNationalityId != 0 && listАoundId != 0){
                initResult()
            }else{
                getIdSxs()
                getAutoOperation()
                getListNationality()
                gitFound()
            }
        }

        access_restricted.setOnClickListener {
            if (idSex != 0 && listSecretQuestionId != 0 && listNationalityId != 0 && listАoundId != 0){
                initResult()
            }else{
                getIdSxs()
                getAutoOperation()
                getListNationality()
                gitFound()
            }
        }

        not_found.setOnClickListener {
            if (idSex != 0 && listSecretQuestionId != 0 && listNationalityId != 0 && listАoundId != 0){
                initResult()
            }else{
                getIdSxs()
                getAutoOperation()
                getListNationality()
                gitFound()
            }
        }

        technical_work.setOnClickListener {
            if (idSex != 0 && listSecretQuestionId != 0 && listNationalityId != 0 && listАoundId != 0){
                initResult()
            }else{
                getIdSxs()
                getAutoOperation()
                getListNationality()
                gitFound()
            }
        }
    }

    private fun initResult() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            questionnaire_no_questionnaire.visibility = View.VISIBLE
            questionnaire_layout.visibility = View.GONE
            questionnaire_technical_work.visibility = View.GONE
            questionnaire_not_found.visibility = View.GONE
            questionnaire_access_restricted.visibility = View.GONE
        } else {
            val map = mutableMapOf<String, String>()
            map["last_name"] = questionnaire_text_surnames.text.toString()
            map["first_name"] = questionnaire_text_name.text.toString()
            map["second_name"] = questionnaire_text_patronymics.text.toString()
            map["u_date"] = data
            map["gender"] = idSex.toString()
            map["nationality"] = listNationalityId.toString()
            map["first_phone"] =
                MyUtils.toFormatMask(questionnaire_phone_number.text.toString())
            map["second_phone"] = try {
                MyUtils.toFormatMask(questionnaire_phone_additional.text.toString())
            } catch (e: Exception) {
                ""
            }
            map["question"] = listSecretQuestionId.toString()
            map["traffic_source"] = listАoundId.toString()
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
                                questionnaire_no_questionnaire.visibility = View.GONE
                                questionnaire_layout.visibility = View.VISIBLE
                                loadingMistake(this)
                            } else if (data.error.code == 401) {
                                initAuthorized()
                            } else if (data.error.code == 400 || data.error.code == 500 || data.error.code == 403) {
                                questionnaire_no_questionnaire.visibility = View.GONE
                                questionnaire_layout.visibility = View.VISIBLE
                                loadingMistake(this)
                            } else {
                                questionnaire_no_questionnaire.visibility = View.GONE
                                questionnaire_layout.visibility = View.VISIBLE
                                initBusyBottomSheet()
                                initVisibilities()
                            }
                        } else {
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_layout.visibility = View.VISIBLE
                            initBottomSheet()
                            initVisibilities()
                        }
                    }
                    Status.ERROR ->{
                        if (msg == "401") {
                            initAuthorized()
                        } else if (msg == "409") {
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        } else {
                            questionnaire_layout.visibility = View.VISIBLE
                            questionnaire_no_questionnaire.visibility = View.GONE
                            loadingMistake(this)
                        }
                    }
                    Status.NETWORK ->{
                        if (msg == "600"){
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        }else{
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_layout.visibility = View.VISIBLE
                            loadingConnection(this)
                        }
                    }
                }
                questionnaire_enter.isEnabled = true
                HomeActivity.alert.hide()
            })
        }
    }

    fun initVisibilities(){
        questionnaire_no_questionnaire.visibility = View.GONE
        questionnaire_layout.visibility = View.VISIBLE
    }

    private fun initClock() {
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
        if (!included){
            questionnaire_enter.isClickable = false
            questionnaire_enter.setBackgroundColor(resources.getColor(R.color.blueColor))
            questionnaire_phone_number.setText(AppPreferences.number.toString())
            questionnaire_phone_additional.mask = AppPreferences.isFormatMask
        }else{
            questionnaire_enter.isClickable = true
            questionnaire_enter.setBackgroundColor(resources.getColor(R.color.orangeColor))
        }
    }

    private fun iniData() {
        questionnaire_date_birth.setOnClickListener(View.OnClickListener { v: View? ->
//            val datePickerDialogFragment = com.example.kotlincashloan.custom_view.DatePickerDialogFragment()
//            datePickerDialogFragment.setOnDateChooseListener { year, month, day ->
//                questionnaire_date_birth.setText("$day-$month-$year")
//                data = (MyUtils.convertDate(year, month, day))
//            }
//            datePickerDialogFragment.show(fragmentManager, "DatePickerDialogFragment")

            if (yearSelective != 0 && monthSelective != 0 && dayOfMonthSelective != 0) {
                showDate(yearSelective, monthSelective, dayOfMonthSelective, R.style.DatePickerSpinner)
            }else{
                showDate(1990, 0, 1, R.style.DatePickerSpinner)
            }
        })
    }

    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar: Calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
        questionnaire_date_birth.setText(simpleDateFormat.format(calendar.getTime()))
        data = (MyUtils.convertDate(year, monthOfYear, dayOfMonth))
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

    private fun getIdSxs() {
        var list: ArrayList<ListGenderResultModel> = arrayListOf()
        val map = HashMap<String, Int>()
        map.put("id", 0)
        HomeActivity.alert.show()
        viewModel.listGender(map).observe(this, androidx.lifecycle.Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        val adapterIdSxs = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, data.result)
                        questionnaire_id_sxs.setAdapter(adapterIdSxs)
                        list = data.result
                    }else{
                        if (data.error.code == 403){
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_access_restricted.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE

                        }else if (data.error.code == 404){
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_not_found.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE

                        }else if (data.error.code == 401){
                            initAuthorized()
                        }else{
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_technical_work.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE
                        }
                    }
                }
                Status.ERROR -> {
                    if (msg == "404"){
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_not_found.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE

                    }else if (msg == "403"){
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_access_restricted.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE

                    }else if (msg == "401"){
                        initAuthorized()
                    }else{
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                    }
                }
                Status.NETWORK -> {
                    if (msg == "600"){
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                    }else{
                        questionnaire_no_questionnaire.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.GONE
                        questionnaire_not_found.visibility = View.GONE
                        questionnaire_access_restricted.visibility = View.GONE
                    }

                }
            }
            HomeActivity.alert.hide()
        })
        questionnaire_id_sxs.keyListener = null
        questionnaire_id_sxs.setOnItemClickListener { adapterView, view, position, l ->
            questionnaire_id_sxs.showDropDown()
            idSex = list[position].id!!
            questionnaire_id_sxs.clearFocus()
        }
        questionnaire_id_sxs.setOnClickListener {
            questionnaire_id_sxs.showDropDown()
        }
        questionnaire_id_sxs.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            try {
                if (hasFocus) {
                    closeKeyboard()
                    questionnaire_id_sxs.showDropDown()
                }
            } catch (e: Exception) {
            }
        }
        questionnaire_id_sxs.clearFocus()
    }


    // Получет места те о которых узнал пользхователь о нас
    private fun gitFound() {
        var list: ArrayList<ListTrafficSource> = arrayListOf()
        val map = HashMap<String, Int>()
        map.put("id", 0)
        viewModel.listTrafficSource(map).observe(this, Observer { result->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        val adapterFound = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, data.result)
                        questionnaire_found.setAdapter(adapterFound)
                        list = data.result
                    }else{
                        if (data.error.code == 403){
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_access_restricted.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE

                        }else if (data.error.code == 404){
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_not_found.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE

                        }else if (data.error.code == 401){
                            initAuthorized()
                        }else{
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_technical_work.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE
                        }
                    }
                }
                Status.ERROR -> {
                    if (msg == "404"){
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_not_found.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE

                    }else if (msg == "403"){
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_access_restricted.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE

                    }else if (msg == "401"){
                        initAuthorized()
                    }else{
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                    }
                }
                Status.NETWORK -> {
                    if (msg == "600"){
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                    }else{
                        questionnaire_no_questionnaire.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.GONE
                        questionnaire_not_found.visibility = View.GONE
                        questionnaire_access_restricted.visibility = View.GONE
                    }

                }
            }
            HomeActivity.alert.hide()
        })
        questionnaire_found.keyListener = null
        questionnaire_found.setOnItemClickListener { adapterView, view, position, l ->
            questionnaire_found.showDropDown()
            listАoundId = list[position].id!!
            questionnaire_found.clearFocus()
            questionnaire_owner.requestFocus()

        }
        questionnaire_found.setOnClickListener {
            questionnaire_found.showDropDown()
        }
        questionnaire_found.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                try {
                    if (hasFocus) {
                        closeKeyboard()
                        questionnaire_found.showDropDown()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        questionnaire_found.clearFocus()
    }


    private fun getListNationality() {
        var list: ArrayList<ListNationalityResultModel> = arrayListOf()
        val map = HashMap<String, Int>()
        map.put("id", 0)
        HomeActivity.alert.show()
        viewModel.listNationality(map).observe(this, Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        val adapterListNationality = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, data.result)
                        questionnaire_id_nationality.setAdapter(adapterListNationality)
                        list = data.result
                    } else {
                        if (data.error.code == 403){
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_access_restricted.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE

                        }else if (data.error.code == 404){
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_not_found.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE

                        }else if (data.error.code == 401){
                            initAuthorized()
                        }else{
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_technical_work.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE
                        }
                    }
                }
                Status.ERROR -> {
                    if (msg == "404") {
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_not_found.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE

                    } else if (msg == "403") {
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_access_restricted.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE

                    } else if (msg == "401") {
                        initAuthorized()
                    }else{
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                    }
                }
                Status.NETWORK -> {
                    if (msg == "600"){
                        questionnaire_no_questionnaire.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                    }else{
                        questionnaire_no_questionnaire.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.GONE
                        questionnaire_not_found.visibility = View.GONE
                        questionnaire_access_restricted.visibility = View.GONE
                    }
                }
            }
            HomeActivity.alert.hide()
        })
        questionnaire_id_nationality.keyListener = null
        questionnaire_id_nationality.setOnItemClickListener { adapterView, view, position, l ->
            questionnaire_id_nationality.showDropDown()
            listNationalityId = list[position].id!!
            questionnaire_id_nationality.clearFocus()
        }
        questionnaire_id_nationality.setOnClickListener {
            questionnaire_id_nationality.showDropDown()
        }
        questionnaire_id_nationality.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                try {
                    if (hasFocus) {
                        closeKeyboard()
                        questionnaire_id_nationality.showDropDown()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        questionnaire_id_nationality.clearFocus()
    }

    private fun getAutoOperation() {
        var list: ArrayList<ListSecretQuestionResultModel> = arrayListOf()
        val map = HashMap<String, Int>()
        map.put("id", 0)
        HomeActivity.alert.show()
        viewModel.listSecretQuestion(map).observe(this, Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        val adapterListSecretQuestion = ArrayAdapter(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            data.result
                        )
                        questionnaire_id_secret.setAdapter(adapterListSecretQuestion)
                        list = data.result
                    }else{
                        if (data.error.code == 403){
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_access_restricted.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE

                        }else if (data.error.code == 404){
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_not_found.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE

                        }else if (data.error.code == 401){
                            initAuthorized()
                        }else{
                            questionnaire_no_questionnaire.visibility = View.GONE
                            questionnaire_technical_work.visibility = View.VISIBLE
                            questionnaire_layout.visibility = View.GONE
                        }
                    }
                }
                Status.ERROR -> {
                    if (msg == "404") {
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_not_found.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE

                    } else if (msg == "403") {
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_access_restricted.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE

                    } else if (msg == "401") {
                        initAuthorized()
                    }else{
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                    }
                }
                Status.NETWORK -> {
                    if (msg == "600"){
                        questionnaire_no_questionnaire.visibility = View.GONE
                        questionnaire_technical_work.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                    }else{
                        questionnaire_no_questionnaire.visibility = View.VISIBLE
                        questionnaire_layout.visibility = View.GONE
                    }
                }
            }
            HomeActivity.alert.hide()
        })

        questionnaire_id_secret.setKeyListener(null)

        questionnaire_id_secret.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                questionnaire_id_secret.showDropDown()
                parent.getItemAtPosition(position).toString()
                listSecretQuestionId = list[position].id!!
                questionnaire_id_secret.clearFocus()
                questionnaire_owner.requestFocus()
            }
        questionnaire_id_secret.setOnClickListener {
            questionnaire_id_secret.showDropDown()
        }
        questionnaire_id_secret.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                try {
                    closeKeyboard()
                    questionnaire_id_secret.showDropDown()
                } catch (e: Exception) {

                }
            }
        questionnaire_id_secret.clearFocus()
    }

    override fun onResume() {
        super.onResume()
        TransitionAnimation(this).transitionRight(question_layout_anim)
    }

    private fun validate(): Boolean {
        var valid = true
        if (questionnaire_text_surnames.text.toString().isEmpty()) {
            questionnaire_text_surnames.error = "Введите фамилию"
            valid = false
        }

        if (questionnaire_text_name.text.toString().isEmpty()) {
            questionnaire_text_name.error = "Введите имя"
            valid = false
        }

        if (questionnaire_date_birth.text!!.toString().isEmpty()) {
            questionnaire_date_birth.error = "Выберите дату"
            valid = false
        } else {
            questionnaire_date_birth.error = null
        }

        if (questionnaire_id_sxs.text!!.toString().isEmpty()) {
            questionnaire_id_sxs.error = "Выберите пол"
            valid = false
        } else {
            questionnaire_id_sxs.error = null
        }

        if (questionnaire_id_nationality.text.toString().isEmpty()) {
            questionnaire_id_nationality.error = "Выберите гражданство"
            valid = false
        } else {
            questionnaire_id_nationality.error = null
        }

        if (questionnaire_id_secret.text.toString().isEmpty()) {
            questionnaire_id_secret.error = "Выберите секретный вопрос"
            valid = false
        } else {
            questionnaire_id_secret.error = null
        }

        if (questionnaire_found.text.toString().isEmpty()) {
            questionnaire_found.error = "Выберите ответ"
            valid = false
        } else {
            questionnaire_found.error = null
        }

        if (questionnaire_secret_response.text.toString().isEmpty()) {
            questionnaire_secret_response.error = "Поле не должно быть пустым"
            valid = false
        }
        if (!valid){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }
        return valid
    }

    private fun initViews() {
        questionnaire_date_birth.addTextChangedListener {
            questionnaire_date_birth.error = null
        }

        questionnaire_id_sxs.addTextChangedListener {
            questionnaire_id_sxs.error = null
        }

        questionnaire_id_nationality.addTextChangedListener {
            questionnaire_id_nationality.error = null
        }

        questionnaire_id_secret.addTextChangedListener {
            questionnaire_id_secret.error = null
        }

        questionnaire_found.addTextChangedListener {
            questionnaire_found.error = null
        }
    }

}