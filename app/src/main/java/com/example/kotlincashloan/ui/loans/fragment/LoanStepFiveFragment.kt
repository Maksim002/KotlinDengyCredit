package com.example.kotlincashloan.ui.loans.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.common.GeneralDialogFragment
import com.example.kotlincashloan.service.model.Loans.*
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.loans.fragment.dialogue.StepBottomFragment
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_loan_step_five.*
import kotlinx.android.synthetic.main.fragment_loan_step_four.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*

class LoanStepFiveFragment : Fragment(), ListenerGeneralResult {
    private var viewModel = LoansViewModel()

    private var getListWorkDta = ""
    private var getListTypeWorkDta = ""
    private var getListYearsDtaF = ""
    private var getListYearsDta = ""
    private var getListIncomeDta = ""
    private var getListTypeIncomeDta = ""
    private var getListAdditionalDta = ""

    private var workId = ""
    private var typeId = ""
    private var yearsRfId = ""
    private var yearsId = ""
    private var incomeId = ""
    private var typeIncomeId = ""
    private var additionalId = ""

    private var workPosition = -1
    private var typeWorkPosition = -1
    private var yearsPosition = -1
    private var experiencePosition = -1
    private var incomePosition = -1
    private var typeIncomePosition = -1
    private var incomeAdditionalPosition = -1

    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var listWork: ArrayList<ListWorkResultModel> = arrayListOf()
    private var listTypeWork: ArrayList<ListTypeWorkModel> = arrayListOf()
    private var listYears: ArrayList<ListYearsResultModel> = arrayListOf()
    private var listWorkExperience: ArrayList<ListYearsResultModel> = arrayListOf()
    private var listIncome: ArrayList<ListIncomeResultModel> = arrayListOf()
    private var listTypeIncome: ArrayList<ListTypeIncomeModel> = arrayListOf()
    private var listIncomeAdditional: ArrayList<ListIncomeResultModel> = arrayListOf()

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

        initListWork()
        initListTypeWork()
        initListYears()
        initWorkExperience()
        initListIncome()
        initLisTypeIncome()
        initListIncomeAdditional()
        initClick()
    }

    private fun initClick() {
        bottom_loan_fire.setOnClickListener {
            if (validate()){
                initSaveLoan()
            }
        }

        access_restricted.setOnClickListener {
            initListWork()
            initListTypeWork()
            initListYears()
            initWorkExperience()
            initListIncome()
            initLisTypeIncome()
            initListIncomeAdditional()
        }

        no_connection_repeat.setOnClickListener {
            initListWork()
            initListTypeWork()
            initListYears()
            initWorkExperience()
            initListIncome()
            initLisTypeIncome()
            initListIncomeAdditional()
        }

        technical_work.setOnClickListener {
            initListWork()
            initListTypeWork()
            initListYears()
            initWorkExperience()
            initListIncome()
            initLisTypeIncome()
            initListIncomeAdditional()
        }

        not_found.setOnClickListener {
            initListWork()
            initListTypeWork()
            initListYears()
            initWorkExperience()
            initListIncome()
            initLisTypeIncome()
            initListIncomeAdditional()
        }

        fire_type_employment.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listWork.size) {
                    if (i <= listWork.size) {
                        itemDialog.add(GeneralDialogModel(listWork[i-1].name.toString(), "listWork", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, workPosition)
            }
        }

        fire_post.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listTypeWork.size) {
                    if (i <= listTypeWork.size) {
                        itemDialog.add(GeneralDialogModel(listTypeWork[i-1].name.toString(), "listTypeWork", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, typeWorkPosition)
            }
        }

        fire_work_experience_r_f.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listYears.size) {
                    if (i <= listYears.size) {
                        itemDialog.add(GeneralDialogModel(listYears[i-1].name.toString(), "listYears", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, yearsPosition)
            }
        }

        fire_work_experience.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listWorkExperience.size) {
                    if (i <= listWorkExperience.size) {
                        itemDialog.add(GeneralDialogModel(listWorkExperience[i-1].name.toString(), "listWorkExperience", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, experiencePosition)
            }
        }

        fire_list_income.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listIncome.size) {
                    if (i <= listIncome.size) {
                        itemDialog.add(GeneralDialogModel(listIncome[i-1].name.toString(), "listIncome", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, incomePosition)
            }
        }

        fire_additional_income.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listTypeIncome.size) {
                    if (i <= listTypeIncome.size) {
                        itemDialog.add(GeneralDialogModel(listTypeIncome[i-1].name.toString(), "listTypeIncome", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, typeIncomePosition)
            }
        }

        fire_additional_amount.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listIncomeAdditional.size) {
                    if (i <= listIncomeAdditional.size) {
                        itemDialog.add(GeneralDialogModel(listIncomeAdditional[i-1].name.toString(), "listIncomeAdditional", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, incomeAdditionalPosition)
            }
        }
    }

    //очещает список
    private fun initClearList(){
        itemDialog.clear()
    }

    // TODO: 21-2-12 Получает информацию из адаптера
    override fun listenerClickResult(model: GeneralDialogModel) {
        if (model.key == "listWork") {
            fire_type_employment.setText(listWork[model.position].name)
            workPosition = model.position
            workId = listWork[model.position].id!!
        }

        if (model.key == "listTypeWork") {
            fire_post.setText(listTypeWork[model.position].name)
            typeWorkPosition = model.position
            typeId = listTypeWork[model.position].id!!
        }

        if (model.key == "listYears") {
            fire_work_experience_r_f.setText(listYears[model.position].name)
            yearsPosition = model.position
            yearsRfId = listYears[model.position].id!!
        }

        if (model.key == "listWorkExperience") {
            fire_work_experience.setText(listWorkExperience[model.position].name)
            experiencePosition = model.position
            yearsId = listWorkExperience[model.position].id!!
        }

        if (model.key == "listIncome") {
            fire_list_income.setText(listIncome[model.position].name)
            incomePosition = model.position
            incomeId = listIncome[model.position].id!!
        }

        if (model.key == "listTypeIncome") {
            fire_additional_income.setText(listTypeIncome[model.position].name)
            typeIncomePosition = model.position
            typeIncomeId = listTypeIncome[model.position].id!!
        }

        if (model.key == "listIncomeAdditional") {
            fire_additional_amount.setText(listIncomeAdditional[model.position].name)
            incomeAdditionalPosition = model.position
            additionalId = listIncomeAdditional[model.position].id!!
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
                getResultOk()
                listWork = result.result
            } else {
                getListWorkDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListWork.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListWorkDta = error
                errorList(error)
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
                getResultOk()
                listTypeWork = result.result
            } else {
                getListTypeWorkDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListTypeWork.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListWorkDta = error
                errorList(error)
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
                getResultOk()
                listYears = result.result
            } else {
                getListYearsDtaF = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListYears.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListYearsDtaF = error
                errorList(error)
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
                getResultOk()
                listWorkExperience = result.result
            } else {
                getListWorkDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListYears.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListWorkDta = error
                errorList(error)
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
                getResultOk()
                listIncome = result.result
            } else {
                getListIncomeDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListIncome.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListIncomeDta = error
                errorList(error)
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
                getResultOk()
                listTypeIncome = result.result
            } else {
                getListTypeIncomeDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListTypeIncome.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListTypeIncomeDta = error
                errorList(error)
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
                getResultOk()
                listIncomeAdditional = result.result
            } else {
                getListAdditionalDta = result.error.code.toString()
            }
        })

        viewModel.errorListIncome.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListAdditionalDta = error
                errorList(error)
            }
        })
    }


    // TODO: 21-2-8  Сохронение Данные о трудовой деятельности.
    private fun initSaveLoan(){
        val mapSave = mutableMapOf<String, String>()
        mapSave["login"] = AppPreferences.login.toString()
        mapSave["token"] = AppPreferences.token.toString()
        mapSave["id"] = AppPreferences.sum.toString()
        mapSave["work"] = workId
        mapSave["type_work"] = typeId
        mapSave["work_exp_ru"] = yearsRfId
        mapSave["work_exp_last"] = yearsId
        mapSave["income"] = incomeId
        mapSave["sub_income_id"] = typeIncomeId
        mapSave["sub_income_sum"] = additionalId
        mapSave["place_work"] = fire_step_four_residence.text.toString()
        mapSave["step"] = "3"
        viewModel.saveLoan(mapSave)

        viewModel.getSaveLoan.observe(viewLifecycleOwner, Observer { result->
            if (result.result != null){
                layout_fire.visibility = View.VISIBLE
                fire_ste_technical_work.visibility = View.GONE
                fire_ste_no_connection.visibility = View.GONE
                fire_ste_access_restricted.visibility = View.GONE
                fire_ste_not_found.visibility = View.GONE
                (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(5)
            }else if (result.reject != null){
                initBottomSheet(result.reject!!.message.toString())
            }else if (result.error.code != null){
                listResult(result.error.code!!)
            }
        })

        viewModel.errorSaveLoan.observe(viewLifecycleOwner, Observer { error->
            if (error != null){
                errorList(error)
            }
        })
    }

    //Вызов деалоговова окна с отоброжением получаемого списка.
    private fun initBottomSheet(list: ArrayList<GeneralDialogModel>, selectionPosition: Int) {
        val stepBottomFragment = GeneralDialogFragment(this,list, selectionPosition)
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    private fun initBottomSheet(message: String) {
        val stepBottomFragment =
            StepBottomFragment(
                message
            )
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    private fun getResultOk() {
        if (getListWorkDta == "200" && getListTypeWorkDta == "200" && getListYearsDtaF == "200" &&
            getListYearsDta == "200" && getListIncomeDta == "200" && getListTypeIncomeDta == "200" && getListAdditionalDta == "200") {
            layout_fire.visibility = View.VISIBLE
            fire_ste_technical_work.visibility = View.GONE
            fire_ste_no_connection.visibility = View.GONE
            fire_ste_access_restricted.visibility = View.GONE
            fire_ste_not_found.visibility = View.GONE
        }
    }

    private fun listResult(result: Int) {
        if (result == 400 || result == 500 || result == 409 || result == 429) {
            fire_ste_technical_work.visibility = View.VISIBLE
            layout_fire.visibility = View.GONE
            fire_ste_no_connection.visibility = View.GONE
            fire_ste_access_restricted.visibility = View.GONE
            fire_ste_not_found.visibility = View.GONE
        } else if (result == 403) {
            fire_ste_access_restricted.visibility = View.VISIBLE
            layout_fire.visibility = View.GONE
            fire_ste_technical_work.visibility = View.GONE
            fire_ste_no_connection.visibility = View.GONE
            fire_ste_not_found.visibility = View.GONE
        } else if (result == 404) {
            fire_ste_not_found.visibility = View.VISIBLE
            layout_fire.visibility = View.GONE
            fire_ste_technical_work.visibility = View.GONE
            fire_ste_no_connection.visibility = View.GONE
            fire_ste_access_restricted.visibility = View.GONE
        } else if (result == 401) {
            initAuthorized()
        }
    }

    private fun errorList(error: String) {
        if (error == "400" || error == "500" || error == "600" || error == "429" || error == "409") {
            fire_ste_technical_work.visibility = View.VISIBLE
            layout_fire.visibility = View.GONE
            fire_ste_no_connection.visibility = View.GONE
            fire_ste_access_restricted.visibility = View.GONE
            fire_ste_not_found.visibility = View.GONE
        } else if (error == "403") {
            fire_ste_access_restricted.visibility = View.VISIBLE
            layout_fire.visibility = View.GONE
            fire_ste_technical_work.visibility = View.GONE
            fire_ste_no_connection.visibility = View.GONE
            fire_ste_not_found.visibility = View.GONE
        } else if (error == "404") {
            fire_ste_not_found.visibility = View.VISIBLE
            layout_fire.visibility = View.GONE
            fire_ste_technical_work.visibility = View.GONE
            fire_ste_no_connection.visibility = View.GONE
            fire_ste_access_restricted.visibility = View.GONE
        } else if (error == "601") {
            fire_ste_no_connection.visibility = View.VISIBLE
            fire_ste_not_found.visibility = View.GONE
            layout_fire.visibility = View.GONE
            fire_ste_technical_work.visibility = View.GONE
            fire_ste_access_restricted.visibility = View.GONE
        } else if (error == "401") {
            initAuthorized()
        }
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
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
            fire_step_four_residence.error = "Поле не должно быть пустым"
            valid = false
        }else {
            fire_step_four_residence.error = null
        }

        if (fire_type_employment.text.isEmpty()) {
            fire_type_employment.error = "Поле не должно быть пустым"
            valid = false
        }else {
            fire_type_employment.error = null
        }

        if (fire_post.text.isEmpty()) {
            fire_post.error = "Поле не должно быть пустым"
            valid = false
        }else {
            fire_post.error = null
        }

        if (fire_work_experience_r_f.text.isEmpty()) {
            fire_work_experience_r_f.error = "Поле не должно быть пустым"
            valid = false
        }else {
            fire_work_experience_r_f.error = null
        }

        if (fire_work_experience.text.isEmpty()) {
            fire_work_experience.error = "Поле не должно быть пустым"
            valid = false
        }else {
            fire_work_experience.error = null
        }

        if (fire_list_income.text.isEmpty()) {
            fire_list_income.error = "Поле не должно быть пустым"
            valid = false
        }else {
            fire_list_income.error = null
        }

        if (fire_additional_income.text.isEmpty()) {
            fire_additional_income.error = "Поле не должно быть пустым"
            valid = false
        }else {
            fire_additional_income.error = null
        }

        if (fire_additional_amount.text.isEmpty()) {
            fire_additional_amount.error = "Поле не должно быть пустым"
            valid = false
        }else {
            fire_additional_amount.error = null
        }

        return valid
    }
}
