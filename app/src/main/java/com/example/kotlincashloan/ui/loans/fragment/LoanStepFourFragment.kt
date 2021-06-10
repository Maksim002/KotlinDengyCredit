package com.example.kotlincashloan.ui.loans.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.adapter.loans.StepClickListener
import com.example.kotlincashloan.common.GeneralDialogFragment
import com.example.kotlincashloan.extension.*
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
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.activity_get_loan.*
import kotlinx.android.synthetic.main.fragment_loan_step_four.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*

class LoanStepFourFragment(var status: Boolean, var listLoan: GetLoanModel, var permission: Int, var applicationStatus: Boolean, var listener: LoanClearListener) : Fragment(), ListenerGeneralResult, StepClickListener {
    private var viewModel = LoansViewModel()

    private var getListCityDta = ""
    private var getListFamilyStatusDta = ""
    private var getListNumbersDta = ""
    private var getListYearsDta = ""
    private var listAvailableCountryDta = ""

    private var cityId = ""
    private var statusId = ""
    private var familyId = ""
    private var childrenId = ""
    private var liveId = ""
    private var cardId = ""
    private var reNum = ""
    private var phoneLength = ""

    private var cityPosition = ""
    private var familyPosition = ""
    private var numbersPosition = ""
    private var childrenPosition = ""
    private var yearsPosition = ""
    private var catsNamesPosition = ""
    private var sixPosition = ""

    private var itemDialog: ArrayList<GeneralDialogModel> = arrayListOf()
    private var listCity: ArrayList<ListCityResultModel> = arrayListOf()
    private var listFamilyStatus: ArrayList<ListFamilyStatusModel> = arrayListOf()
    private var listNumbers: ArrayList<ListNumbersResultModel> = arrayListOf()
    private var listNumbersChildren: ArrayList<ListNumbersResultModel> = arrayListOf()
    private var listYears: ArrayList<ListYearsResultModel> = arrayListOf()
    private var listAvailableSix: ArrayList<SixNumResultModel> = arrayListOf()
    private lateinit var alert: LoadingAlert

    private var handler = Handler()

    //Наличие банковской карты
    private var listCatsNames = arrayOf("Нет", "Да")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_step_four, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alert = LoadingAlert(requireActivity())

        if (applicationStatus == false){
            // Отоброожает кнопку если статус true видем закрытия
            (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.GONE
        }else{
            (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.VISIBLE
        }

        if (permission == 3) {
//            alert.show()
        }

        initClick()
        initView()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        handler.postDelayed(Runnable { // Do something after 5s = 500ms
            if (menuVisible && isResumed) {
                initInternet()
            }
        }, 1000)
    }

    private fun initClick() {
        six_number_phone.addTextChangedListener {
            editUtils(layout_phone_number, six_number_phone, six_number_phone_error, "", false)
            initCleaningRoom()
        }

        (activity as GetLoanActivity?)!!.access_restricted.setOnClickListener {
            listener.loanClearClickListener()
//            initInternet()
        }

        (activity as GetLoanActivity?)!!.no_connection_repeat.setOnClickListener {
            listener.loanClearClickListener()
//            initInternet()
        }

        (activity as GetLoanActivity?)!!.technical_work.setOnClickListener {
            listener.loanClearClickListener()
//            initInternet()
        }

        (activity as GetLoanActivity?)!!.not_found.setOnClickListener {
            listener.loanClearClickListener()
//            initInternet()
        }

        bottom_loan_four.setOnClickListener {
            ObservedInternet().observedInternet(requireContext())
            if (!AppPreferences.observedInternet) {
                (activity as GetLoanActivity?)!!.get_loan_no_connection.visibility = View.VISIBLE
                (activity as GetLoanActivity?)!!.layout_get_loan_con.visibility = View.GONE
                (activity as GetLoanActivity?)!!.get_loan_technical_work.visibility = View.GONE
                (activity as GetLoanActivity?)!!.get_loan_access_restricted.visibility = View.GONE
                (activity as GetLoanActivity?)!!.get_loan_not_found.visibility = View.GONE
            } else {
                if (validate()) {
                    AppPreferences.isRepeat = false
                    initSaveLoan()
                }
            }
        }

        four_cross_back.setOnClickListener {
            AppPreferences.isRepeat = false
            shimmerStart((activity as GetLoanActivity?)!!.shimmer_step_loan, requireActivity())
            (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(2)
            hidingErrors()
        }

        six_available_country.setOnClickListener {
            six_available_country.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listAvailableSix.size) {
                    if (i <= listAvailableSix.size) {
                        itemDialog.add(
                            GeneralDialogModel(listAvailableSix[i - 1].name.toString(), "listAvailableSix", i - 1, 0, listAvailableSix[i - 1].name.toString()))
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, sixPosition, "Список доступных стран", six_available_country)
            }
        }

        loans_step_four_city.setOnClickListener {
            loans_step_four_city.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listCity.size) {
                    if (i <= listCity.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listCity[i - 1].name.toString(),
                                "listCity",
                                i - 1,
                                0,
                                listCity[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, cityPosition, "В каком городе России Вы живете?", loans_step_four_city)
            }
        }

        loans_step_four_status.setOnClickListener {
            loans_step_four_status.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listFamilyStatus.size) {
                    if (i <= listFamilyStatus.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listFamilyStatus[i - 1].name.toString(),
                                "listFamilyStatus",
                                i - 1,
                                0,
                                listFamilyStatus[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, familyPosition, "Семейное положение", loans_step_four_status)
            }
        }

        loans_step_four_family.setOnClickListener {
            loans_step_four_family.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listNumbers.size) {
                    if (i <= listNumbers.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listNumbers[i - 1].name.toString(),
                                "listNumbers",
                                i - 1,
                                0,
                                listNumbers[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, numbersPosition, "Количество членов семьи" , loans_step_four_family)
            }
        }

        loans_step_four_children.setOnClickListener {
            loans_step_four_children.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listNumbersChildren.size) {
                    if (i <= listNumbersChildren.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listNumbersChildren[i - 1].name.toString(),
                                "listNumbersChildren",
                                i - 1,
                                0, listNumbersChildren[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, childrenPosition, "Сколько человек работает у Вас в семье?", loans_step_four_children)
            }
        }

        loans_step_four_federation.setOnClickListener {
            loans_step_four_federation.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listYears.size) {
                    if (i <= listYears.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listYears[i - 1].name.toString(),
                                "listYears",
                                i - 1, 0,
                                listYears[i - 1].name.toString()
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, yearsPosition, "Сколько лет проживаете в РФ", loans_step_four_federation)
            }
        }

        loans_step_four_card.setOnClickListener {
            loans_step_four_card.isEnabled = false
            initClearList()
            //Мутод заполняет список данными дя адапера
            if (itemDialog.size == 0) {
                for (i in 1..listCatsNames.size) {
                    if (i <= listCatsNames.size) {
                        itemDialog.add(
                            GeneralDialogModel(
                                listCatsNames[i - 1],
                                "listCatsNames",
                                i - 1,
                                0,
                                listCatsNames[i - 1]
                            )
                        )
                    }
                }
            }
            if (itemDialog.size != 0) {
                initBottomSheet(itemDialog, catsNamesPosition, "Есть банковская карта", loans_step_four_card)
            }
        }
    }

    //метод удаляет все символы из строки
    private fun initCleaningRoom() {
        if (six_number_phone.text.toString() != "") {
            val matchedResults =
                Regex(pattern = """\d+""").findAll(input = six_available_country.text.toString() + six_number_phone.text.toString())
            val result = StringBuilder()
            for (matchedText in matchedResults) {
                reNum = result.append(matchedText.value).toString()
            }
        } else {
            reNum = ""
        }
    }

    //очещает список
    private fun initClearList() {
        itemDialog.clear()
    }

    //Получает данные на редактирование заёма
    private fun getLists() {
        if (status == true) {
            //Если applicationStatus == true меняем текст на кнопки
            if (applicationStatus == false) {
                (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.GONE
                bottom_loan_four.setText("Сохранить")
                four_cross_back.visibility = View.GONE
            }else{
                // Отоброожает кнопку если статус видем закрытия
                (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.VISIBLE
            }
            try {
            //city
            loans_step_four_city.setText(listCity.first { it.id == listLoan.city }.name)
            cityPosition = listCity.first { it.id == listLoan.city }.name.toString()
            cityId = listCity.first { it.id == listLoan.city }.id!!
            //address
            loans_step_four_residence.setText(listLoan.address.toString())
            //familyStatus
            loans_step_four_status.setText(listFamilyStatus.first { it.id == listLoan.familyStatus }.name)
            familyPosition =
                listFamilyStatus.first { it.id == listLoan.familyStatus }.name.toString()
            statusId = listFamilyStatus.first { it.id == listLoan.familyStatus }.id!!
            //countFamily
            loans_step_four_family.setText(listNumbers.first { it.id == listLoan.countFamily }.name)
            numbersPosition =
                listNumbers.first { it.id == listLoan.countFamily }.name.toString()
            familyId = listNumbers.first { it.id == listLoan.countFamily }.id!!
            //countFamilyWork
            loans_step_four_children.setText(listNumbersChildren.first { it.id == listLoan.countFamilyWork }.name)
            childrenPosition =
                listNumbersChildren.first { it.id == listLoan.countFamilyWork }.name.toString()
            childrenId = listNumbersChildren.first { it.id == listLoan.countFamilyWork }.id!!
            //liveInRu
            loans_step_four_federation.setText(listYears.first { it.id == listLoan.liveInRu }.name)
            yearsPosition = listYears.first { it.id == listLoan.liveInRu }.name.toString()
            liveId = listYears.first { it.id == listLoan.liveInRu }.id!!
            //bankCard
            loans_step_four_card.setText(listCatsNames[listLoan.bankCard!!.toInt()])
            catsNamesPosition = listCatsNames[listLoan.bankCard!!.toInt()]
            cardId = listLoan.bankCard.toString()
            //second_phone_country_id
            if (listLoan.second_phone_country_id != "0") {
                six_available_country.isClickable = true
                six_number_phone.error = null
                //Очещает старую маску при выборе новой
                six_number_phone.mask = ""
                sixPosition =
                    listAvailableSix.first { it.id == listLoan.second_phone_country_id!!.toInt() }.name.toString()
                phoneLength =
                    listAvailableSix.first { it.id == listLoan.second_phone_country_id!!.toInt() }.phoneLength.toString()
                val l =
                    MyUtils.toServerDate(listLoan.secondPhone.toString(), phoneLength.toInt())
                six_available_country.setText("+" + listAvailableSix.first { it.id == listLoan.second_phone_country_id!!.toInt() }.phoneCode)
                six_number_phone.mask =
                    listAvailableSix.first { it.id == listLoan.second_phone_country_id!!.toInt() }.phoneMaskSmall
                six_number_phone.setText(l)
            }
            }catch (e: Exception){
                e.printStackTrace()
            }
            alert.hide()
        }
    }

    // TODO: 21-2-12 Получает информацию из адаптера
    override fun listenerClickResult(model: GeneralDialogModel) {
        if (model.key == "listAvailableSix") {
            six_available_country.isEnabled = true
            six_number_phone.error = null
            //Очещает старую маску при выборе новой
            six_number_phone.mask = ""
            // Очещает поле
            six_number_phone.text = null
            sixPosition = listAvailableSix[model.position].name.toString()
            phoneLength = listAvailableSix[model.position].phoneLength.toString()
            six_available_country.setText("+" + listAvailableSix[model.position].phoneCode)
            six_number_phone.mask = listAvailableSix[model.position].phoneMaskSmall
        }

        if (model.key == "listCity") {
            loans_step_four_city.isEnabled = true
            loans_step_four_city.error = null
            loans_step_four_city.setText(listCity[model.position].name)
            cityPosition = listCity[model.position].name.toString()
            cityId = listCity[model.position].id!!
        }
        if (model.key == "listFamilyStatus") {
            loans_step_four_status.isEnabled = true
            loans_step_four_status.error = null
            loans_step_four_status.setText(listFamilyStatus[model.position].name)
            familyPosition = listFamilyStatus[model.position].name.toString()
            statusId = listFamilyStatus[model.position].id!!
        }

        if (model.key == "listNumbers") {
            loans_step_four_family.isEnabled = true
            loans_step_four_family.error = null
            loans_step_four_family.setText(listNumbers[model.position].name)
            numbersPosition = listNumbers[model.position].name.toString()
            familyId = listNumbers[model.position].id!!
        }

        if (model.key == "listNumbersChildren") {
            loans_step_four_children.isEnabled = true
            loans_step_four_children.error = null
            loans_step_four_children.setText(listNumbersChildren[model.position].name)
            childrenPosition = listNumbersChildren[model.position].name.toString()
            childrenId = listNumbersChildren[model.position].id!!
        }

        if (model.key == "listYears") {
            loans_step_four_federation.isEnabled = true
            loans_step_four_federation.error = null
            loans_step_four_federation.setText(listYears[model.position].name)
            yearsPosition = listYears[model.position].name.toString()
            liveId = listYears[model.position].id!!
        }

        if (model.key == "listCatsNames") {
            loans_step_four_card.isEnabled = true
            loans_step_four_card.error = null
            loans_step_four_card.setText(listCatsNames[model.position])
            catsNamesPosition = listCatsNames[model.position]
            cardId = model.position.toString()
        }
    }

    private fun initInternet() {
        getValueNull()
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            (activity as GetLoanActivity?)!!.get_loan_no_connection.visibility = View.VISIBLE
            (activity as GetLoanActivity?)!!.layout_get_loan_con.visibility = View.GONE
            (activity as GetLoanActivity?)!!.get_loan_technical_work.visibility = View.GONE
            (activity as GetLoanActivity?)!!.get_loan_access_restricted.visibility = View.GONE
            (activity as GetLoanActivity?)!!.get_loan_not_found.visibility = View.GONE
        } else {
            viewModel.errorSaveLoan.value = null
            initListCity()
            initListFamilyStatus()
            initListNumbers()
            initListNumbersChildren()
            initListYears()
            initAvailableCountry()
        }
    }

    // TODO: 21-2-8 Список доступных стран
    private fun initAvailableCountry() {
        val mapCountry = mutableMapOf<String, String>()
        mapCountry["id"] = ""
        viewModel.listAvailableSix(mapCountry)

        viewModel.listAvailableSixDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                listAvailableCountryDta = result.code.toString()
                listAvailableSix = result.result
                if (six_number_phone.text!!.length == 0) {
                    six_number_phone.mask = ""
                    six_number_phone.text = null
                    sixPosition = result.result[0].name.toString()
                    six_available_country.setText("+" + result.result[0].phoneCode)
                    six_number_phone.mask = result.result[0].phoneMaskSmall
                    phoneLength = result.result[0].phoneLength.toString()
                }
                getResultOk()
            } else {
                getErrorCode(result.error.code!!)
              alert.hide()
            }
        })

        viewModel.errorListAvailableSix.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                listAvailableCountryDta = error
                getErrorCode(error.toInt())
            }
            alert.hide()
        })
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
                listCity = result.result
                getResultOk()
            } else {
                getListCityDta = result.error.code.toString()

                getErrorCode(result.error.code!!)
                alert.hide()
            }
        })

        viewModel.errorListCity.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListCityDta = error

                getErrorCode(error.toInt())
            }
            alert.hide()
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
                listFamilyStatus = result.result
                getResultOk()
            } else {
                getListFamilyStatusDta = result.error.code.toString()

                getErrorCode(result.error.code!!)
                alert.hide()
            }
        })

        viewModel.errorListFamilyStatus.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListFamilyStatusDta = error

                getErrorCode(error.toInt())
            }
            alert.hide()
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
                listNumbers = result.result
                getResultOk()
            } else {
                getListNumbersDta = result.error.code.toString()

                getErrorCode(result.error.code!!)
                alert.hide()
            }
        })

        viewModel.errorListNumbers.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListNumbersDta = error

                getErrorCode(error.toInt())
            }
            alert.hide()
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
                listNumbersChildren = result.result
                getResultOk()

            } else {
                getListNumbersDta = result.error.code.toString()

                getErrorCode(result.error.code!!)
                alert.hide()
            }
        })

        viewModel.errorListNumbers.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListNumbersDta = error

                getErrorCode(error.toInt())
                alert.hide()
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
                listYears = result.result
                getResultOk()
            } else {
                getListYearsDta = result.error.code.toString()

                getErrorCode(result.error.code!!)
                alert.hide()
            }
        })

        viewModel.errorListYears.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                getListYearsDta = error

                getErrorCode(error.toInt())
            }
            alert.hide()
        })
    }

    //Сохронение на сервер данных
    private fun initSaveLoan() {
//        GetLoanActivity.alert.show()
        val mapSave = mutableMapOf<String, String>()
        mapSave.put("login", AppPreferences.login.toString())
        mapSave.put("token", AppPreferences.token.toString())
        mapSave.put("id", AppPreferences.applicationId.toString())
        mapSave.put("city", cityId)
        mapSave["second_phone"] = reNum
        mapSave.put("address", loans_step_four_residence.text.toString())
        mapSave.put("family_status", statusId)
        mapSave.put("count_family", familyId)
        mapSave.put("count_family_work", childrenId)
        mapSave.put("live_in_ru", liveId)
        mapSave.put("bank_card", cardId)
        mapSave.put("step", "3")

        if (status == true){
            //Сохроняет изменненые данные в масив
            listLoan.city = cityId
            listLoan.secondPhone = reNum
            listLoan.address = loans_step_four_residence.text.toString()
            listLoan.familyStatus = statusId
            listLoan.countFamily = familyId
            listLoan.countFamilyWork = childrenId
            listLoan.liveInRu = liveId
            listLoan.bankCard = cardId
        }
        shimmerStart((activity as GetLoanActivity?)!!.shimmer_step_loan, requireActivity())
        viewModel.saveLoans(mapSave).observe(viewLifecycleOwner, Observer { result ->
            val data = result.data
            val msg = result.msg
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        (activity as GetLoanActivity?)!!.layout_get_loan_con.visibility = View.VISIBLE
                        (activity as GetLoanActivity?)!!.get_loan_technical_work.visibility = View.GONE
                        (activity as GetLoanActivity?)!!.get_loan_no_connection.visibility = View.GONE
                        (activity as GetLoanActivity?)!!.get_loan_access_restricted.visibility = View.GONE
                        (activity as GetLoanActivity?)!!.get_loan_not_found.visibility = View.GONE
                        if (applicationStatus == false) {
                            if (status == true) {
                                requireActivity().finish()
                            }else{
                                (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(4)
                            }
                        } else {
                            (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(4)
                        }
                    } else if (data.error.code != null) {
                        listListResult(data.error.code!!.toInt(), activity as AppCompatActivity)
                    } else if (data.reject != null) {
                        initBottomSheet(data.reject.message!!)
                        (activity as GetLoanActivity?)!!.layout_get_loan_con.visibility = View.VISIBLE
                        (activity as GetLoanActivity?)!!.get_loan_technical_work.visibility = View.GONE
                        (activity as GetLoanActivity?)!!.get_loan_no_connection.visibility = View.GONE
                        (activity as GetLoanActivity?)!!.get_loan_not_found.visibility = View.GONE
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
    private fun initBottomSheet(list: ArrayList<GeneralDialogModel>, selectionPosition: String, title: String, id: AutoCompleteTextView) {
        val stepBottomFragment = GeneralDialogFragment(this, list, selectionPosition, title, id)
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    private fun initBottomSheet(message: String) {
        val stepBottomFragment = StepBottomFragment(this, message)
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    private fun getResultOk() {
        if (getListCityDta == "200" && getListFamilyStatusDta == "200" && getListNumbersDta == "200" && getListYearsDta == "200" && listAvailableCountryDta == "200") {
            (activity as GetLoanActivity?)!!.layout_get_loan_con.visibility = View.VISIBLE
            (activity as GetLoanActivity?)!!.get_loan_technical_work.visibility = View.GONE
            (activity as GetLoanActivity?)!!.get_loan_no_connection.visibility = View.GONE
            (activity as GetLoanActivity?)!!.get_loan_access_restricted.visibility = View.GONE
            (activity as GetLoanActivity?)!!.get_loan_not_found.visibility = View.GONE
            getLists()
            if (!AppPreferences.isRepeat){
                //генерирует анимацию перехода
                animationGenerator((activity as GetLoanActivity?)!!.shimmer_step_loan,handler,  requireActivity())
                AppPreferences.isRepeat = true
            }
        }
    }

    private fun getValueNull() {
        getListCityDta = ""
        getListFamilyStatusDta = ""
        getListNumbersDta = ""
        getListYearsDta = ""
    }


    private fun getErrorCode(error: Int) {
        listListResult(error, (activity as GetLoanActivity?)!!.get_loan_technical_work as LinearLayout, (activity as GetLoanActivity?)!!.get_loan_no_connection
                as LinearLayout, (activity as GetLoanActivity?)!!.layout_get_loan_con, (activity as GetLoanActivity?)!!.get_loan_access_restricted
                as LinearLayout, (activity as GetLoanActivity?)!!.get_loan_not_found as LinearLayout, requireActivity(), true)
    }

    private fun validate(): Boolean {
        var valid = true
        if (loans_step_four_residence.text.isEmpty()) {
            editUtils(loans_step_four_residence, loans_step_four_error, "Заполните поле", true)
            valid = false
        }

        if (loans_step_four_city.text.isEmpty()) {
            editUtils(loans_step_four_city, step_four_city_error, "Выберите из списка", true)
            valid = false
        }

        if (loans_step_four_status.text.isEmpty()) {
            editUtils(loans_step_four_status, step_four_status_error, "Выберите из списка", true)
            valid = false
        }

        if (loans_step_four_family.text.isEmpty()) {
            editUtils(loans_step_four_family, step_four_family_error, "Выберите из списка", true)
            valid = false
        }

        if (loans_step_four_children.text.isEmpty()) {
            editUtils(
                loans_step_four_children,
                step_four_children_error,
                "Выберите из списка",
                true
            )
            valid = false
        }

        if (loans_step_four_federation.text.isEmpty()) {
            editUtils(
                loans_step_four_federation,
                step_four_federation_error,
                "Выберите из списка",
                true
            )
            valid = false
        }

        if (loans_step_four_card.text.isEmpty()) {
            editUtils(loans_step_four_card, step_four_card_error, "Выберите из списка", true)
            valid = false
        }

        if (six_number_phone.text!!.isNotEmpty()) {
            if (phoneLength != reNum.length.toString()) {
                editUtils(
                    layout_phone_number,
                    six_number_phone,
                    six_number_phone_error,
                    "Введите правильный номер",
                    true
                )
                valid = false
            }
        }

        return valid
    }

    private fun initView() {
        loans_step_four_residence.addTextChangedListener {
            editUtils(loans_step_four_residence, loans_step_four_error, "", false)
        }
        loans_step_four_city.addTextChangedListener {
            editUtils(loans_step_four_city, step_four_city_error, "", false)
        }
        loans_step_four_status.addTextChangedListener {
            editUtils(loans_step_four_status, step_four_status_error, "", false)
        }
        loans_step_four_family.addTextChangedListener {
            editUtils(loans_step_four_family, step_four_family_error, "", false)
        }
        loans_step_four_children.addTextChangedListener {
            editUtils(loans_step_four_children, step_four_children_error, "", false)
        }
        loans_step_four_federation.addTextChangedListener {
            editUtils(loans_step_four_federation, step_four_federation_error, "", false)
        }
        loans_step_four_card.addTextChangedListener {
            editUtils(loans_step_four_card, step_four_card_error, "", false)
        }
    }

    //проверяет если был откат назад отключает ошибки
    private fun hidingErrors() {
        editUtils(loans_step_four_residence, loans_step_four_error, "", false)
        editUtils(loans_step_four_city, step_four_city_error, "", false)
        editUtils(loans_step_four_status, step_four_status_error, "", false)
        editUtils(loans_step_four_family, step_four_family_error, "", false)
        editUtils(loans_step_four_children, step_four_children_error, "", false)
        editUtils(loans_step_four_federation, step_four_federation_error, "", false)
        editUtils(loans_step_four_card, step_four_card_error, "", false)
        editUtils(layout_phone_number, six_number_phone, six_number_phone_error, "", false)
    }

    override fun onClickStepListener() {
        requireActivity().finish()
    }
}