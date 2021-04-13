package com.example.kotlincashloan.ui.loans.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.adapter.loans.StepClickListener
import com.example.kotlincashloan.common.GeneralDialogFragment
import com.example.kotlincashloan.extension.editUtils
import com.example.kotlincashloan.extension.listListResult
import com.example.kotlincashloan.service.model.Loans.*
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.service.model.profile.GetLoanModel
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.loans.fragment.dialogue.StepBottomFragment
import com.example.kotlincashloan.utils.ObservedInternet
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.fragment_loan_step_fifth.view.*
import kotlinx.android.synthetic.main.fragment_loan_step_five.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*

class LoanStepFiveFragment(var status: Boolean, var listLoan: GetLoanModel, var permission: Int, var applicationStatus: Boolean) :
    Fragment(), ListenerGeneralResult, StepClickListener {
    private var viewModel = LoansViewModel()

    private var getListWorkDta = ""
    private var getListTypeWorkDta = ""
    private var getListYearsDtaF = ""
    private var getListYearsDta = ""
    private var getListIncomeDta = ""
    private var getListTypeIncomeDta = ""
    private var getListAdditionalDta = ""

    private var typeId = ""
    private var yearsRfId = ""
    private var yearsId = ""
    private var incomeId = ""
    private var typeIncomeId = ""
    private var additionalId = ""

    private var typeWorkPosition = ""
    private var yearsPosition = ""
    private var experiencePosition = ""
    private var incomePosition = ""
    private var typeIncomePosition = ""
    private var incomeAdditionalPosition = ""

    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var listWork: ArrayList<ListWorkResultModel> = arrayListOf()
    private var listTypeWork: ArrayList<ListTypeWorkModel> = arrayListOf()
    private var listYears: ArrayList<ListYearsResultModel> = arrayListOf()
    private var listWorkExperience: ArrayList<ListYearsResultModel> = arrayListOf()
    private var listIncome: ArrayList<ListIncomeResultModel> = arrayListOf()
    private var listTypeIncome: ArrayList<ListTypeIncomeModel> = arrayListOf()
    private var listIncomeAdditional: ArrayList<ListIncomeResultModel> = arrayListOf()
    private lateinit var alert: LoadingAlert

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_step_five, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alert = LoadingAlert(requireActivity())
        if (permission == 4) {
            alert.show()
        }
        initRestart()
        initClick()
        initView()
    }

    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            fire_ste_no_connection.visibility = View.VISIBLE
            layout_fire.visibility = View.GONE
            fire_ste_technical_work.visibility = View.GONE
            fire_ste_access_restricted.visibility = View.GONE
            fire_ste_not_found.visibility = View.GONE
        } else {
            viewModel.errorSaveLoan.value = null
            initListWork()
            initListTypeWork()
            initListYears()
            initWorkExperience()
            initListIncome()
            initLisTypeIncome()
            initListIncomeAdditional()
        }
    }

    override fun onResume() {
        super.onResume()
        if (additionalId == "-1") {
            fire_additional_amount.visibility = View.GONE
        }
        //В поле длинная строка. Если поле непустое удоляет hide
        if (fire_work_experience.text.isNotEmpty()) {
            fire_work_experience.hint = null
        }
    }

    private fun initClick() {
        bottom_loan_fire.setOnClickListener {
            if (fire_additional_amount.visibility == View.GONE) {
                additionalId = "-1"
            }
            if (validate()) {
                initSaveLoan()
            }
        }

        access_restricted.setOnClickListener {
            initRestart()
        }

        no_connection_repeat.setOnClickListener {
            initRestart()
        }

        technical_work.setOnClickListener {
            initRestart()
        }

        not_found.setOnClickListener {
            initRestart()
        }

        five_cross_back.setOnClickListener {
            (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(3)
            hidingErrors()
        }

        fire_post.setOnClickListener {
            fire_post.isClickable = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listTypeWork.size) {
                    if (i <= listTypeWork.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listTypeWork[i - 1].name.toString(), "listTypeWork", i - 1, listTypeWork[i - 1].id!!.toInt(), listTypeWork[i - 1].name.toString())
                        )
                    }
                    if (i == listTypeWork.size) {
                        itemDialog.add(GeneralDialogModel("Другое", "listTypeWork", i, 0, "Другое"))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, typeWorkPosition, "Кем Вы работаете?")
            }
        }

        fire_work_experience_r_f.setOnClickListener {
            fire_work_experience_r_f.isClickable = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listYears.size) {
                    if (i <= listYears.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listYears[i - 1].name.toString(),
                                "listYears",
                                i - 1,
                                0,
                                listYears[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, yearsPosition, "Сколько Вы работаете в России?")
            }
        }

        fire_work_experience.setOnClickListener {
            fire_work_experience.isClickable = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listWorkExperience.size) {
                    if (yearsRfId != "") {
                        if (yearsRfId.toInt() >= listWorkExperience[i - 1].id!!.toInt()) {
                            itemDialog.add(
                                GeneralDialogModel(
                                    listWorkExperience[i - 1].name.toString(),
                                    "listWorkExperience",
                                    i - 1,
                                    0,
                                    listWorkExperience[i - 1].name.toString()
                                )
                            )
                        }
                    } else {
                        if (i <= listWorkExperience.size) {
                            itemDialog.add(
                                GeneralDialogModel(
                                    listWorkExperience[i - 1].name.toString(),
                                    "listWorkExperience",
                                    i - 1,
                                    0,
                                    listWorkExperience[i - 1].name.toString()
                                )
                            )
                        }
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(
                    itemDialog,
                    experiencePosition,
                    "Сколько Вы работаете на последнем месте работы в России?"
                )
            }
        }

        fire_list_income.setOnClickListener {
            fire_list_income.isClickable = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listIncome.size) {
                    if (i <= listIncome.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listIncome[i - 1].name.toString(),
                                "listIncome",
                                i - 1,
                                0,
                                listIncome[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, incomePosition, "Ежемесячный доход")
            }
        }

        fire_additional_income.setOnClickListener {
            fire_additional_income.isClickable = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listTypeIncome.size) {
                    if (i <= listTypeIncome.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listTypeIncome[i - 1].name.toString(),
                                "listTypeIncome",
                                i - 1,
                                0,
                                listTypeIncome[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, typeIncomePosition, "Дополнительный доход")
            }
        }

        fire_additional_amount.setOnClickListener {
            fire_additional_amount.isClickable = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listIncomeAdditional.size) {
                    if (i <= listIncomeAdditional.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listIncomeAdditional[i - 1].name.toString(),
                                "listIncomeAdditional",
                                i - 1,
                                0,
                                listIncomeAdditional[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, incomeAdditionalPosition, "Сумма доп. дохода")
            }
        }
    }

    //очещает список
    private fun initClearList() {
        itemDialog.clear()
    }

    // TODO: 21-2-12 Получает информацию из адаптера
    override fun listenerClickResult(model: GeneralDialogModel) {

        if (model.key == "listTypeWork") {
            if (itemDialog.first { it.id == model.id }.name == "Другое") {
                fire_post.isClickable = true
                fire_post.error = null
                fire_post.setText("Другое")
                typeWorkPosition = "Другое"
                typeId = "9999"
                five_layout_text.visibility = View.VISIBLE
            } else {
                fire_post.isClickable = true
                fire_post.error = null
                fire_post.setText(listTypeWork[model.position].name)
                typeWorkPosition = listTypeWork[model.position].name.toString()
                typeId = listTypeWork[model.position].id!!
                five_layout_text.visibility = View.GONE
                fire_step_four_working.setText("")
                editUtils(fire_step_four_working, step_four_working_error, "", false)
            }
        }

        if (model.key == "listYears") {
            fire_work_experience_r_f.isClickable = true
            fire_work_experience_r_f.error = null
            fire_work_experience_r_f.setText(listYears[model.position].name)
            yearsPosition = listYears[model.position].name.toString()
            yearsRfId = listYears[model.position].id!!
            if (yearsId != "") {
                if (yearsId.toInt() >= yearsRfId.toInt()) {
                    for (position in 0..listWorkExperience.size) {
                        val pos = listWorkExperience[position].id.toString()
                        if (pos == yearsRfId) {
                            yearsId = listWorkExperience[position].id!!
                            experiencePosition = listWorkExperience[position].name.toString()
                            val l = listWorkExperience[position].name.toString()
                            fire_work_experience.setText(l)
                            break
                        }
                    }
                }
            }
        }

        if (model.key == "listWorkExperience") {
            fire_work_experience.isClickable = true
            fire_work_experience.error = null
            fire_work_experience.hint = ""
            fire_work_experience.setText(listWorkExperience[model.position].name)
            experiencePosition = listWorkExperience[model.position].name.toString()
            yearsId = listWorkExperience[model.position].id!!
        }

        if (model.key == "listIncome") {
            fire_list_income.isClickable = true
            fire_list_income.error = null
            fire_list_income.setText(listIncome[model.position].name)
            incomePosition = listIncome[model.position].name.toString()
            incomeId = listIncome[model.position].id!!
        }

        if (model.key == "listTypeIncome") {
            fire_additional_income.isClickable = true
            fire_additional_income.error = null
            fire_additional_income.setText(listTypeIncome[model.position].name)
            typeIncomePosition = listTypeIncome[model.position].name.toString()
            typeIncomeId = listTypeIncome[model.position].id!!
            if (listTypeIncome[model.position].id!! == "1") {
                fire_additional_amount.visibility = View.GONE
                fire_additional_amount.text = null
            } else {
                fire_additional_amount.visibility = View.VISIBLE
            }
        }

        if (model.key == "listIncomeAdditional") {
            fire_additional_amount.isClickable = true
            fire_additional_amount.error = null
            fire_additional_amount.setText(listIncomeAdditional[model.position].name)
            incomeAdditionalPosition = listIncomeAdditional[model.position].name.toString()
            additionalId = listIncomeAdditional[model.position].id!!
        }
    }

    //Получает данные на редактирование заёма
    private fun getLists() {
        if (status == true) {
            if (applicationStatus == false) {
                bottom_loan_fire.setText("Сохранить")
                five_cross_back.visibility = View.GONE
            }
            try {
            //place_work
            fire_step_four_residence.setText(listLoan.placeWork)
            //type_work
            if (listLoan.typeWork == "9999") {
                fire_post.setText("Другое")
                fire_step_four_working.setText(listLoan.otherTypeWork)
                typeWorkPosition = "Другое"
                typeId = listLoan.typeWork.toString()
                five_layout_text.visibility = View.VISIBLE
            } else {
                fire_post.setText(listTypeWork.first { it.id == listLoan.typeWork }.name)
                typeWorkPosition = listTypeWork.first { it.id == listLoan.typeWork }.name.toString()
                typeId = listTypeWork.first { it.id == listLoan.typeWork }.id!!
                five_layout_text.visibility = View.GONE
            }
            //work_exp_ru
            fire_work_experience_r_f.setText(listYears.first { it.id == listLoan.workExpRu }.name)
            yearsPosition = listYears.first { it.id == listLoan.workExpRu }.name.toString()
            yearsRfId = listYears.first { it.id == listLoan.workExpRu }.id!!
            //work_exp_last
            fire_work_experience.setText(listWorkExperience.first { it.id == listLoan.workExpLast }.name)
            experiencePosition =
                listWorkExperience.first { it.id == listLoan.workExpLast }.name.toString()
            yearsId = listWorkExperience.first { it.id == listLoan.workExpLast }.id!!
            //income
            fire_list_income.setText(listIncome.first { it.id == listLoan.income }.name)
            incomePosition = listIncome.first { it.id == listLoan.income }.name.toString()
            incomeId = listIncome.first { it.id == listLoan.income }.id!!
            //sub_income_id
            fire_additional_income.setText(listTypeIncome.first { it.id == listLoan.subIncomeId }.name)
            typeIncomePosition =
                listTypeIncome.first { it.id == listLoan.subIncomeId }.name.toString()
            typeIncomeId = listTypeIncome.first { it.id == listLoan.subIncomeId }.id!!
            //sub_income_sum
            fire_additional_amount.setText(listIncomeAdditional.first { it.id == listLoan.subIncomeSum }.name)
            incomeAdditionalPosition =
                listIncomeAdditional.first { it.id == listLoan.subIncomeSum }.name.toString()
            additionalId = listIncomeAdditional.first { it.id == listLoan.subIncomeSum }.id!!

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    // TODO: 21-2-5  Вид занятости
    private fun initListWork() {
        val mapWork = mutableMapOf<String, String>()
        mapWork["login"] = AppPreferences.login.toString()
        mapWork["token"] = AppPreferences.token.toString()
        mapWork["id"] = "0"
        viewModel.listWork(mapWork)

        viewModel.getListWorkDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListWorkDta = result.code.toString()
                listWork = result.result
                getResultOk()
            } else {
                getListWorkDta = result.error.code.toString()
//                listResult(result.error.code!!)
                getErrorCode(result.error.code!!)
            }
        })

        viewModel.errorListWork.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListWorkDta = error
//                errorList(error)
                getErrorCode(error.toInt())
            }
        })
    }

    // TODO: 21-2-5 Должность
    private fun initListTypeWork() {
        val mapType = mutableMapOf<String, String>()
        mapType["login"] = AppPreferences.login.toString()
        mapType["token"] = AppPreferences.token.toString()
        mapType["id"] = "0"
        viewModel.listTypeWork(mapType)

        viewModel.getListTypeWorkDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListTypeWorkDta = result.code.toString()
                listTypeWork = result.result
                getResultOk()
            } else {
                getListTypeWorkDta = result.error.code.toString()
                getErrorCode(result.error.code!!)
            }
        })

        viewModel.errorListTypeWork.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListWorkDta = error
                getErrorCode(error.toInt())
            }
        })
    }

    // TODO: 21-2-5 Стаж работы в РФ
    private fun initListYears() {
        val mapYears = mutableMapOf<String, String>()
        mapYears["login"] = AppPreferences.login.toString()
        mapYears["token"] = AppPreferences.token.toString()
        mapYears["id"] = "0"
        viewModel.listYears(mapYears)

        viewModel.getListYearsDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListYearsDtaF = result.code.toString()
                listYears = result.result
                getResultOk()
            } else {
                getListYearsDtaF = result.error.code.toString()
                getErrorCode(result.error.code!!)
            }
        })

        viewModel.errorListYears.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListYearsDtaF = error
                getErrorCode(error.toInt())
            }
        })
    }

    // TODO: 21-2-5 Стаж работы в последнем месте 
    private fun initWorkExperience() {
        val mapYearsDta = mutableMapOf<String, String>()
        mapYearsDta["login"] = AppPreferences.login.toString()
        mapYearsDta["token"] = AppPreferences.token.toString()
        mapYearsDta["id"] = "0"
        viewModel.listYears(mapYearsDta)

        viewModel.getListYearsDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListYearsDta = result.code.toString()
                listWorkExperience = result.result
                getResultOk()
            } else {
                getListWorkDta = result.error.code.toString()
                getErrorCode(result.error.code!!)
            }
        })

        viewModel.errorListYears.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListWorkDta = error
                getErrorCode(error.toInt())
            }
        })
    }


    // TODO: 21-2-5 Ежемесячный доход
    private fun initListIncome() {
        val mapIncome = mutableMapOf<String, String>()
        mapIncome["login"] = AppPreferences.login.toString()
        mapIncome["token"] = AppPreferences.token.toString()
        mapIncome["id"] = "0"
        viewModel.listIncome(mapIncome)

        viewModel.getListIncomeDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListIncomeDta = result.code.toString()
                listIncome = result.result
                getResultOk()
            } else {
                getListIncomeDta = result.error.code.toString()
                getErrorCode(result.error.code!!)
            }
        })

        viewModel.errorListIncome.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListIncomeDta = error
                getErrorCode(error.toInt())
            }
        })
    }


    // TODO: 21-2-5 Дополнительный доход
    private fun initLisTypeIncome() {
        val mapTypeIncome = mutableMapOf<String, String>()
        mapTypeIncome["login"] = AppPreferences.login.toString()
        mapTypeIncome["token"] = AppPreferences.token.toString()
        mapTypeIncome["id"] = "0"
        viewModel.listTypeIncome(mapTypeIncome)

        viewModel.getListTypeIncomeDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListTypeIncomeDta = result.code.toString()
                listTypeIncome = result.result
                getResultOk()
            } else {
                getListTypeIncomeDta = result.error.code.toString()
                getErrorCode(result.error.code!!)
            }
        })

        viewModel.errorListTypeIncome.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListTypeIncomeDta = error
                getErrorCode(error.toInt())
            }
        })
    }


    // TODO: 21-2-5 Сумма доп. дохода
    private fun initListIncomeAdditional() {
        val mapAdditional = mutableMapOf<String, String>()
        mapAdditional["login"] = AppPreferences.login.toString()
        mapAdditional["token"] = AppPreferences.token.toString()
        mapAdditional["id"] = "0"
        viewModel.listIncome(mapAdditional)

        viewModel.getListIncomeDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListAdditionalDta = result.code.toString()
                listIncomeAdditional = result.result
                getResultOk()
            } else {
                getListAdditionalDta = result.error.code.toString()
                getErrorCode(result.error.code!!)
            }
        })

        viewModel.errorListIncome.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListAdditionalDta = error
                getErrorCode(error.toInt())
            }
        })
        alert.hide()
    }


    // TODO: 21-2-8  Сохронение Данные о трудовой деятельности.
    private fun initSaveLoan() {
        GetLoanActivity.alert.show()
        val mapSave = mutableMapOf<String, String>()
        mapSave["login"] = AppPreferences.login.toString()
        mapSave["token"] = AppPreferences.token.toString()
        mapSave["id"] = AppPreferences.applicationId.toString()
        mapSave["type_work"] = typeId

        if (fire_step_four_working.text.isNotEmpty()) {
            mapSave["other_type_work"] = fire_step_four_working.text.toString()
        }

        mapSave["work_exp_ru"] = yearsRfId
        mapSave["work_exp_last"] = yearsId
        mapSave["income"] = incomeId
        mapSave["sub_income_id"] = typeIncomeId
        if (additionalId == "-1") {
            mapSave["sub_income_sum"] = ""
        } else {
            mapSave["sub_income_sum"] = additionalId
        }
        mapSave["place_work"] = fire_step_four_residence.text.toString()
        mapSave["step"] = "4"

        viewModel.saveLoans(mapSave).observe(viewLifecycleOwner, Observer { result ->
            val data = result.data
            val msg = result.msg
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        layout_fire.visibility = View.VISIBLE
                        fire_ste_technical_work.visibility = View.GONE
                        fire_ste_no_connection.visibility = View.GONE
                        fire_ste_access_restricted.visibility = View.GONE
                        fire_ste_not_found.visibility = View.GONE
                        if (applicationStatus == false) {
                            if (status == true) {
                                requireActivity().onBackPressed()
                            }
                        } else {
                            (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(5)
                        }

                    } else if (data.error.code != null) {
                        listListResult(data.error.code!!.toInt(), activity as AppCompatActivity)
                    } else if (data.reject != null) {
                        initBottomSheet(data.reject.message.toString())
                    }
                }
                Status.ERROR -> {
                    listListResult(msg!!, activity as AppCompatActivity)
                }
                Status.NETWORK -> {
                    listListResult(msg!!, activity as AppCompatActivity)
                }
            }
            GetLoanActivity.alert.hide()
        })
    }


    //Вызов деалоговова окна с отоброжением получаемого списка.
    private fun initBottomSheet(
        list: ArrayList<GeneralDialogModel>,
        selectionPosition: String,
        title: String
    ) {
        val stepBottomFragment = GeneralDialogFragment(this, list, selectionPosition, title)
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    private fun initBottomSheet(message: String) {
        val stepBottomFragment =
            StepBottomFragment(
                this, message
            )
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    private fun getResultOk() {
        if (getListWorkDta == "200" && getListTypeWorkDta == "200" && getListYearsDtaF == "200" &&
            getListYearsDta == "200" && getListIncomeDta == "200" && getListTypeIncomeDta == "200" && getListAdditionalDta == "200"
        ) {
            layout_fire.visibility = View.VISIBLE
            fire_ste_technical_work.visibility = View.GONE
            fire_ste_no_connection.visibility = View.GONE
            fire_ste_access_restricted.visibility = View.GONE
            fire_ste_not_found.visibility = View.GONE
            getLists()
        }
    }

    private fun getErrorCode(error: Int) {
        listListResult(
            error, fire_ste_technical_work as LinearLayout, fire_ste_no_connection
                    as LinearLayout, layout_fire as ConstraintLayout, fire_ste_access_restricted
                    as LinearLayout, fire_ste_not_found as LinearLayout, requireActivity()
        )
    }

    //Метотд для скрытия клавиатуры
    private fun closeKeyboard() {
        val view: View = requireActivity().currentFocus!!
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            val manager = requireActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager?
            manager!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun validate(): Boolean {
        var valid = true
        if (fire_step_four_residence.text.isEmpty()) {
            editUtils(fire_step_four_residence, step_four_residence_error, "Заполните поле", true)
            valid = false
        }

        if (five_layout_text.visibility != View.GONE) {
            if (fire_step_four_working.text.isEmpty()) {
                editUtils(fire_step_four_working, step_four_working_error, "Заполните поле", true)
                valid = false
            }
        }

        if (fire_post.text.isEmpty()) {
            editUtils(fire_post, fire_post_error, "Выберите из списка", true)
            valid = false
        }
        if (fire_work_experience_r_f.text.isEmpty()) {
            editUtils(
                fire_work_experience_r_f,
                work_experience_r_f_error,
                "Выберите из списка",
                true
            )
            valid = false
        }
        if (fire_work_experience.text.isEmpty()) {
            editUtils(fire_work_experience, work_experience_error, "Выберите из списка", true)
            valid = false
        }
        if (fire_list_income.text.isEmpty()) {
            editUtils(fire_list_income, list_income_error, "Выберите из списка", true)
            valid = false
        }
        if (fire_additional_income.text.isEmpty()) {
            editUtils(fire_additional_income, additional_income_error, "Выберите из списка", true)
            valid = false
        }
        if (fire_additional_amount.visibility != View.GONE) {
            if (fire_additional_amount.text.isEmpty()) {
                editUtils(
                    fire_additional_amount,
                    additional_amount_error,
                    "Выберите из списка",
                    true
                )
                valid = false
            }
        }
        return valid
    }

    private fun initView() {
        fire_step_four_working.addTextChangedListener {
            editUtils(fire_step_four_working, step_four_working_error, "", false)
        }

        fire_step_four_residence.addTextChangedListener {
            editUtils(fire_step_four_residence, step_four_residence_error, "", false)
        }
        fire_post.addTextChangedListener {
            editUtils(fire_post, fire_post_error, "", false)
        }
        fire_work_experience_r_f.addTextChangedListener {
            editUtils(fire_work_experience_r_f, work_experience_r_f_error, "", false)
        }
        fire_work_experience.addTextChangedListener {
            editUtils(fire_work_experience, work_experience_error, "", false)
        }
        fire_list_income.addTextChangedListener {
            editUtils(fire_list_income, list_income_error, "", false)
        }
        fire_additional_income.addTextChangedListener {
            editUtils(fire_additional_income, additional_income_error, "", false)
        }
        fire_additional_amount.addTextChangedListener {
            editUtils(fire_additional_amount, additional_amount_error, "", false)
        }
    }

    //проверяет если был откат назад отключает ошибки
    private fun hidingErrors() {
        editUtils(fire_step_four_residence, step_four_residence_error, "", false)
        editUtils(fire_post, fire_post_error, "", false)
        editUtils(fire_work_experience_r_f, work_experience_r_f_error, "", false)
        editUtils(fire_work_experience, work_experience_error, "", false)
        editUtils(fire_list_income, list_income_error, "", false)
        editUtils(fire_additional_income, additional_income_error, "", false)
        editUtils(fire_additional_amount, additional_amount_error, "", false)
    }

    override fun onClickStepListener() {
        requireActivity().finish()
    }

}
