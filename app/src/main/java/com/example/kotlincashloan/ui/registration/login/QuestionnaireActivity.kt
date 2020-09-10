package com.example.kotlinscreenscanner.ui.login

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlinscreenscanner.service.model.ListGenderResultModel
import com.example.kotlinscreenscanner.service.model.ListNationalityResultModel
import com.example.kotlinscreenscanner.service.model.ListSecretQuestionResultModel
import com.example.kotlinscreenscanner.ui.login.fragment.AuthorizationBottomSheetFragment
import com.example.kotlinscreenscanner.ui.login.fragment.AuthorizationBusyBottomFragment
import com.example.myapplication.LoginViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.actyviti_questionnaire.*
import java.util.*


class QuestionnaireActivity : AppCompatActivity() {
    private var viewModel = LoginViewModel()
    private var data: String = ""
    private var idSex: Int = 0
    private var listNationalityId: Int = 0
    private var listSecretQuestionId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actyviti_questionnaire)
        initToolBar()
        iniData()
        getListNationality()
        getIdSxs()
        getAutoOperation()
        iniClock()
        initViews()
        initCheck()
    }

    private fun initCheck() {
        questionnaire_enter.setOnClickListener {
            if (validate()) {

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
                }catch (e: Exception){
                    ""
                }
                map["question"] = listSecretQuestionId.toString()
                map["response"] = questionnaire_secret_response.text.toString()
                map["sms_code"] = AppPreferences.receivedSms.toString()

                viewModel.questionnaire(map)
                    .observe(this, Observer { result ->
                        val msg = result.msg
                        val data = result.data
                        when (result.status) {
                            Status.SUCCESS -> {
                                if (data!!.result == null) {
                                    if (data.error.code != 409) {
                                        loadingMistake(this)
                                    } else {
                                        initBusyBottomSheet()
                                    }
                                } else {
                                    initBottomSheet()
                                }
                            }
                            Status.ERROR -> {
                                loadingMistake(this)
                            }
                            Status.NETWORK -> {
                                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
        }
    }

    private fun iniClock() {
        questionnaire_agreement.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                questionnaire_enter.isClickable = true
                questionnaire_enter.setBackgroundColor(resources.getColor(R.color.orangeColor))
            }else{
                questionnaire_enter.isClickable = false
                questionnaire_enter.setBackgroundColor(resources.getColor(R.color.blueColor))

            }
        }
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
        supportActionBar!!.title = "Регистрация"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        questionnaire_enter.isClickable = false
        questionnaire_enter.setBackgroundColor(resources.getColor(R.color.blueColor))
        questionnaire_phone_number.setText(AppPreferences.number.toString())
        questionnaire_phone_additional.mask = AppPreferences.isFormatMask
    }

    private fun iniData() {
        questionnaire_date_birth.setOnClickListener(View.OnClickListener { v: View? ->
            val datePickerDialogFragment = com.example.kotlincashloan.custom_view.DatePickerDialogFragment()
            datePickerDialogFragment.setOnDateChooseListener { year, month, day ->
                questionnaire_date_birth.setText("$day-$month-$year")
                data = (MyUtils.convertDate(year, month, day))
            }
            datePickerDialogFragment.show(fragmentManager, "DatePickerDialogFragment")
        })
    }

    private fun getIdSxs() {
        var list: ArrayList<ListGenderResultModel> = arrayListOf()
        val map = HashMap<String, Int>()
        map.put("id", 0)
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
                    questionnaire_id_sxs.showDropDown()
                }
            } catch (e: Exception) {
            }
        }
        questionnaire_id_sxs.clearFocus()
    }


    private fun getListNationality() {
        var list: ArrayList<ListNationalityResultModel> = arrayListOf()
        val map = HashMap<String, Int>()
        map.put("id", 0)
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
        viewModel.listSecretQuestion(map).observe(this, Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        val adapterListSecretQuestion = ArrayAdapter(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            data!!.result
                        )
                        questionnaire_id_secret.setAdapter(adapterListSecretQuestion)
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
                    questionnaire_id_secret.showDropDown()
                } catch (e: Exception) {

                }
            }
        questionnaire_id_secret.clearFocus()

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

        if (questionnaire_text_patronymics.text.toString().isEmpty()) {
            questionnaire_text_patronymics.error = "Введите отчество"
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
    }
}