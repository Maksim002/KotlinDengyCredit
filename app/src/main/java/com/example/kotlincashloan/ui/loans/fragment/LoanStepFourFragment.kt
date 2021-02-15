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
import com.example.kotlincashloan.service.model.Loans.ListCityResultModel
import com.example.kotlincashloan.service.model.Loans.ListFamilyStatusModel
import com.example.kotlincashloan.service.model.Loans.ListNumbersResultModel
import com.example.kotlincashloan.service.model.Loans.ListYearsResultModel
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.loans.fragment.dialogue.StepBottomFragment
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_loan_step_four.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*

class LoanStepFourFragment : Fragment(), ListenerGeneralResult {
    private var viewModel = LoansViewModel()

    private var getListCityDta = ""
    private var getListFamilyStatusDta = ""
    private var getListNumbersDta = ""
    private var getListYearsDta = ""

    private var cityId = ""
    private var statusId = ""
    private var familyId = ""
    private var childrenId = ""
    private var liveId = ""
    private var cardId = ""

    private var cityPosition = -1
    private var familyPosition = -1
    private var numbersPosition = -1
    private var childrenPosition = -1
    private var yearsPosition = -1
    private var catsNamesPosition = -1

    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var listCity: ArrayList<ListCityResultModel> = arrayListOf()
    private var listFamilyStatus: ArrayList<ListFamilyStatusModel> = arrayListOf()
    private var listNumbers: ArrayList<ListNumbersResultModel> = arrayListOf()
    private var listNumbersChildren: ArrayList<ListNumbersResultModel> = arrayListOf()
    private var listYears: ArrayList<ListYearsResultModel> = arrayListOf()
    //Наличие банковской карты
    private var listCatsNames = arrayOf("Нет", "Да")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_step_four, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInternet()
        initClick()
    }

    private fun initClick() {
        access_restricted.setOnClickListener {
            initInternet()

        }

        no_connection_repeat.setOnClickListener {
            initInternet()
        }

        technical_work.setOnClickListener {
            initInternet()
        }

        not_found.setOnClickListener {
            initInternet()
        }

        bottom_loan_four.setOnClickListener {
            if (validate()){
                initSaveLoan()
            }
        }

        loans_step_four_city.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listCity.size) {
                    if (i <= listCity.size) {
                        itemDialog.add(GeneralDialogModel(listCity[i-1].name.toString(), "listCity", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, cityPosition)
            }
        }

        loans_step_four_status.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listFamilyStatus.size) {
                    if (i <= listFamilyStatus.size) {
                        itemDialog.add(GeneralDialogModel(listFamilyStatus[i-1].name.toString(), "listFamilyStatus", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, familyPosition)
            }
        }

        loans_step_four_family.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listNumbers.size) {
                    if (i <= listNumbers.size) {
                        itemDialog.add(GeneralDialogModel(listNumbers[i-1].name.toString(), "listNumbers", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, numbersPosition)
            }
        }

        loans_step_four_children.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listNumbersChildren.size) {
                    if (i <= listNumbersChildren.size) {
                        itemDialog.add(GeneralDialogModel(listNumbersChildren[i-1].name.toString(), "listNumbersChildren", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, childrenPosition)
            }
        }

        loans_step_four_federation.setOnClickListener {
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

        loans_step_four_card.setOnClickListener {
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listCatsNames.size) {
                    if (i <= listCatsNames.size) {
                        itemDialog.add(GeneralDialogModel(listCatsNames[i-1], "listCatsNames", i-1))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, catsNamesPosition)
            }
        }
    }

    //очещает список
    private fun initClearList(){
        itemDialog.clear()
    }

    // TODO: 21-2-12 Получает информацию из адаптера
    override fun listenerClickResult(model: GeneralDialogModel) {
        if (model.key == "listCity") {
            loans_step_four_city.error = null
            loans_step_four_city.setText(listCity[model.position].name)
            cityPosition = model.position
            cityId = listCity[model.position].id!!
        }
        if (model.key == "listFamilyStatus") {
            loans_step_four_status.error = null
            loans_step_four_status.setText(listFamilyStatus[model.position].name)
            familyPosition = model.position
            statusId = listFamilyStatus[model.position].id!!
        }

        if (model.key == "listNumbers") {
            loans_step_four_family.error = null
            loans_step_four_family.setText(listNumbers[model.position].name)
            numbersPosition = model.position
            familyId = listNumbers[model.position].id!!
        }

        if (model.key == "listNumbersChildren") {
            loans_step_four_children.error = null
            loans_step_four_children.setText(listNumbersChildren[model.position].name)
            childrenPosition = model.position
            childrenId = listNumbersChildren[model.position].id!!
        }

        if (model.key == "listYears") {
            loans_step_four_federation.error = null
            loans_step_four_federation.setText(listYears[model.position].name)
            yearsPosition = model.position
            liveId = listYears[model.position].id!!
        }

        if (model.key == "listCatsNames") {
            loans_step_four_card.error = null
            loans_step_four_card.setText(listCatsNames[model.position])
            catsNamesPosition = model.position
            cardId = listCatsNames[model.position]
        }
    }

    private fun initInternet() {
        getValueNull()
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            loans_ste_no_connection.visibility = View.VISIBLE
            loans_step_layout.visibility = View.GONE
            loans_ste_technical_work.visibility = View.GONE
            loans_ste_access_restricted.visibility = View.GONE
            loans_ste_not_found.visibility = View.GONE
        } else {
            initListCity()
            initListFamilyStatus()
            initListNumbers()
            initListNumbersChildren()
            initListYears()
        }
    }

    // TODO: 21-2-12  выберите город
    private fun initListCity() {
        val mapCity = mutableMapOf<String, String>()
        mapCity["login"] = AppPreferences.login.toString()
        mapCity["token"] = AppPreferences.token.toString()
        mapCity["id"] = "0"
        viewModel.listCity(mapCity)

        viewModel.getListCityDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListCityDta = result.code.toString()
                getResultOk()
                listCity = result.result
            } else {
                getListCityDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListCity.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListCityDta = error
                errorList(error)
            }
        })
    }

    // TODO: 21-2-12 Ваше семейное положение
    private fun initListFamilyStatus() {
        val mapStatus = mutableMapOf<String, String>()
        mapStatus["login"] = AppPreferences.login.toString()
        mapStatus["token"] = AppPreferences.token.toString()
        mapStatus["id"] = "0"
        viewModel.listFamilyStatus(mapStatus)

        viewModel.getListFamilyStatusDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListFamilyStatusDta = result.code.toString()
                getResultOk()
                listFamilyStatus = result.result
            } else {
                getListFamilyStatusDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListFamilyStatus.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListFamilyStatusDta = error
                errorList(error)
            }
        })
    }

    // TODO: 21-2-12 Численность семьи
    private fun initListNumbers() {
        val mapNumbers = mutableMapOf<String, String>()
        mapNumbers["login"] = AppPreferences.login.toString()
        mapNumbers["token"] = AppPreferences.token.toString()
        mapNumbers["id"] = "0"
        viewModel.listNumbers(mapNumbers)

        viewModel.getListNumbersDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListNumbersDta = result.code.toString()
                getResultOk()
                listNumbers = result.result
            } else {
                getListNumbersDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListNumbers.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListNumbersDta = error
                errorList(error)
            }
        })
    }

    // TODO: 21-2-12 Количество детей
    private fun initListNumbersChildren() {
        val mapNumbers = mutableMapOf<String, String>()
        mapNumbers["login"] = AppPreferences.login.toString()
        mapNumbers["token"] = AppPreferences.token.toString()
        mapNumbers["id"] = "0"
        viewModel.listNumbers(mapNumbers)

        viewModel.getListNumbersDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListNumbersDta = result.code.toString()
                getResultOk()
                listNumbersChildren = result.result

            } else {
                getListNumbersDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListNumbers.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListNumbersDta = error
                errorList(error)
            }
        })
    }

    // TODO: 21-2-12 Сколько лет проживаете в РФ
    private fun initListYears() {
        val mapYears = mutableMapOf<String, String>()
        mapYears["login"] = AppPreferences.login.toString()
        mapYears["token"] = AppPreferences.token.toString()
        mapYears["id"] = "0"
        viewModel.listYears(mapYears)

        viewModel.getListYearsDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                getListYearsDta = result.code.toString()
                getResultOk()
                listYears = result.result
            } else {
                getListYearsDta = result.error.code.toString()
                listResult(result.error.code!!)
            }
        })

        viewModel.errorListYears.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListYearsDta = error
                errorList(error)
            }
        })
    }

    //Сохронение на сервер данных
    private fun initSaveLoan() {
        val mapSave = mutableMapOf<String, String>()
        mapSave.put("login", AppPreferences.login.toString())
        mapSave.put("token", AppPreferences.token.toString())
        mapSave.put("id", AppPreferences.sum.toString())
        mapSave.put("city", cityId)
        mapSave.put("address", loans_step_four_residence.text.toString())
        mapSave.put("family_status", statusId)
        mapSave.put("count_family", familyId)
        mapSave.put("count_children", childrenId)
        mapSave.put("live_in_ru", liveId)
        mapSave.put("bank_card", cardId)
        mapSave.put("step", "2")

        viewModel.saveLoan(mapSave)

        viewModel.getSaveLoan.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                loans_step_layout.visibility = View.VISIBLE
                loans_ste_technical_work.visibility = View.GONE
                loans_ste_no_connection.visibility = View.GONE
                loans_ste_access_restricted.visibility = View.GONE
                loans_ste_not_found.visibility = View.GONE
                (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(4)
            } else if (result.reject != null) {
                initBottomSheet(result.reject!!.message!!)
                loans_step_layout.visibility = View.VISIBLE
                loans_ste_technical_work.visibility = View.GONE
                loans_ste_no_connection.visibility = View.GONE
                loans_ste_not_found.visibility = View.GONE
            } else if (result.result != null) {
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
        if (getListCityDta == "200" && getListFamilyStatusDta == "200" && getListNumbersDta == "200" && getListYearsDta == "200") {
            loans_step_layout.visibility = View.VISIBLE
            loans_ste_technical_work.visibility = View.GONE
            loans_ste_no_connection.visibility = View.GONE
            loans_ste_access_restricted.visibility = View.GONE
            loans_ste_not_found.visibility = View.GONE
        }
    }

    private fun getValueNull() {
        getListCityDta = ""
        getListFamilyStatusDta = ""
        getListNumbersDta = ""
        getListYearsDta = ""
    }

    private fun listResult(result: Int) {
        if (result == 400 || result == 500 || result == 409 || result == 429) {
            loans_ste_technical_work.visibility = View.VISIBLE
            loans_ste_no_connection.visibility = View.GONE
            loans_step_layout.visibility = View.GONE
            loans_ste_access_restricted.visibility = View.GONE
            loans_ste_not_found.visibility = View.GONE
        } else if (result == 403) {
            loans_ste_access_restricted.visibility = View.VISIBLE
            loans_ste_technical_work.visibility = View.GONE
            loans_ste_no_connection.visibility = View.GONE
            loans_step_layout.visibility = View.GONE
            loans_ste_not_found.visibility = View.GONE
        } else if (result == 404) {
            loans_ste_not_found.visibility = View.VISIBLE
            loans_ste_access_restricted.visibility = View.GONE
            loans_ste_technical_work.visibility = View.GONE
            loans_ste_no_connection.visibility = View.GONE
            loans_step_layout.visibility = View.GONE
        } else if (result == 401) {
            initAuthorized()
        }
    }

    private fun errorList(error: String) {
        if (error == "400" || error == "500" || error == "600" || error == "429" || error == "409") {
            loans_ste_technical_work.visibility = View.VISIBLE
            loans_ste_no_connection.visibility = View.GONE
            loans_step_layout.visibility = View.GONE
            loans_ste_access_restricted.visibility = View.GONE
            loans_ste_not_found.visibility = View.GONE
        } else if (error == "403") {
            loans_ste_access_restricted.visibility = View.VISIBLE
            loans_ste_technical_work.visibility = View.GONE
            loans_ste_no_connection.visibility = View.GONE
            loans_step_layout.visibility = View.GONE
            loans_ste_not_found.visibility = View.GONE
        } else if (error == "404") {
            loans_ste_not_found.visibility = View.VISIBLE
            loans_ste_access_restricted.visibility = View.GONE
            loans_ste_technical_work.visibility = View.GONE
            loans_ste_no_connection.visibility = View.GONE
            loans_step_layout.visibility = View.GONE
        } else if (error == "601") {
            loans_ste_no_connection.visibility = View.VISIBLE
            loans_step_layout.visibility = View.GONE
            loans_ste_technical_work.visibility = View.GONE
            loans_ste_access_restricted.visibility = View.GONE
            loans_ste_not_found.visibility = View.GONE
        } else if (error == "401") {
            initAuthorized()
        }
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    private fun validate(): Boolean {
        var valid = true
        if (loans_step_four_residence.text.isEmpty()) {
            loans_step_four_residence.error = "Поле не должно быть пустым"
            valid = false
        }else {
            loans_step_four_residence.error = null
        }

        if (loans_step_four_city.text.isEmpty()) {
            loans_step_four_city.error = "Поле не должно быть пустым"
            valid = false
        }else {
            loans_step_four_city.error = null
        }

        if (loans_step_four_status.text.isEmpty()) {
            loans_step_four_status.error = "Поле не должно быть пустым"
            valid = false
        }else {
            loans_step_four_status.error = null
        }

        if (loans_step_four_family.text.isEmpty()) {
            loans_step_four_family.error = "Поле не должно быть пустым"
            valid = false
        }else {
            loans_step_four_family.error = null
        }

        if (loans_step_four_children.text.isEmpty()) {
            loans_step_four_children.error = "Поле не должно быть пустым"
            valid = false
        }else {
            loans_step_four_children.error = null
        }

        if (loans_step_four_federation.text.isEmpty()) {
            loans_step_four_federation.error = "Поле не должно быть пустым"
            valid = false
        }else {
            loans_step_four_federation.error = null
        }

        if (loans_step_four_card.text.isEmpty()) {
            loans_step_four_card.error = "Поле не должно быть пустым"
            valid = false
        }else {
            loans_step_four_card.error = null
        }

        return valid
    }
}