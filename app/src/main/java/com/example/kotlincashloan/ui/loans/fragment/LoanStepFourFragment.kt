package com.example.kotlincashloan.ui.loans.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_loan_step_four.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*

class LoanStepFourFragment : Fragment() {
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
            (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(5)
            initSaveLoan()
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
//            loans_step_owner.requestFocus()
        } else {
            initListCity()
            initListFamilyStatus()
            initListNumbers()
            initListNumbersChildren()
            initListYears()
            defaultList()
        }
    }

    //выберите город
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
                val adapterIdCity = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    result.result
                )
                loans_step_four_city.setAdapter(adapterIdCity)

                loans_step_four_city.keyListener = null
                loans_step_four_city.setOnItemClickListener { adapterView, view, position, l ->
                    cityId = result.result[position].id!!
                    loans_step_four_city.showDropDown()
                }
                loans_step_four_city.setOnClickListener {
                    loans_step_four_city.showDropDown()
                }
                loans_step_four_city.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                loans_step_four_city.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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

    //Ваше семейное положение
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
                val adapterIdStatus = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    result.result
                )
                loans_step_four_status.setAdapter(adapterIdStatus)

                loans_step_four_status.keyListener = null
                loans_step_four_status.setOnItemClickListener { adapterView, view, position, l ->
                    statusId = result.result[position].id!!
                    loans_step_four_status.showDropDown()
                }
                loans_step_four_status.setOnClickListener {
                    loans_step_four_status.showDropDown()
                }
                loans_step_four_status.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                loans_step_four_status.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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

    //Численность семьи
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
                val adapterIdFamily = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    result.result
                )
                loans_step_four_family.setAdapter(adapterIdFamily)

                loans_step_four_family.keyListener = null
                loans_step_four_family.setOnItemClickListener { adapterView, view, position, l ->
                    familyId = result.result[position].id!!
                    loans_step_four_family.showDropDown()
                }
                loans_step_four_family.setOnClickListener {
                    loans_step_four_family.showDropDown()
                }
                loans_step_four_family.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                loans_step_four_family.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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

    //Количество детей
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
                val adapterIdChildren = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    result.result
                )
                loans_step_four_children.setAdapter(adapterIdChildren)

                loans_step_four_children.keyListener = null
                loans_step_four_children.setOnItemClickListener { adapterView, view, position, l ->
                    childrenId = result.result[position].id!!
                    loans_step_four_children.showDropDown()
                }
                loans_step_four_children.setOnClickListener {
                    loans_step_four_children.showDropDown()
                }
                loans_step_four_children.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                loans_step_four_children.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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

    // Сколько лет проживаете в РФ
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
                val adapterIdFederation = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    result.result
                )
                loans_step_four_federation.setAdapter(adapterIdFederation)

                loans_step_four_federation.keyListener = null
                loans_step_four_federation.setOnItemClickListener { adapterView, view, position, l ->
                    liveId = result.result[position].id!!
                    loans_step_four_federation.showDropDown()
                }
                loans_step_four_federation.setOnClickListener {
                    loans_step_four_federation.showDropDown()
                }
                loans_step_four_federation.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                loans_step_four_federation.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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

    //Наличие банковской карты
    private fun defaultList() {
        val catsNames = arrayOf("Нет", "Да")

        val adapterIdCard =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, catsNames)
        loans_step_four_card.setAdapter(adapterIdCard)

        loans_step_four_card.keyListener = null
        loans_step_four_card.setOnItemClickListener { adapterView, view, position, l ->
            cardId = position.toString()
            loans_step_four_card.showDropDown()
        }
        loans_step_four_card.setOnClickListener {
            loans_step_four_card.showDropDown()
        }
        loans_step_four_card.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                try {
                    if (hasFocus) {
                        closeKeyboard()
                        loans_step_four_card.showDropDown()
                    }
                } catch (e: Exception) {
                }
            }
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

    private fun initBottomSheet(message: String) {
        val stepBottomFragment = StepBottomFragment(message)
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
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

    override fun onResume() {
        super.onResume()
//        loans_step_owner.requestFocus()
    }
}