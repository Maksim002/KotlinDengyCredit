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
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_loan_step_five.*
import kotlinx.android.synthetic.main.fragment_loan_step_four.*

class LoanStepFiveFragment : Fragment() {
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
                val adapterIdType = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, result.result)
                fire_type_employment.setAdapter(adapterIdType)

                fire_type_employment.keyListener = null
                fire_type_employment.setOnItemClickListener { adapterView, view, position, l ->
                    workId = result.result[position].id!!
                    fire_type_employment.showDropDown()
                }
                fire_type_employment.setOnClickListener {
                    fire_type_employment.showDropDown()
                }
                fire_type_employment.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                fire_type_employment.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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
                val adapterIdPost = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, result.result)
                fire_post.setAdapter(adapterIdPost)

                fire_post.keyListener = null
                fire_post.setOnItemClickListener { adapterView, view, position, l ->
                    typeId = result.result[position].id!!
                    fire_post.showDropDown()
                }
                fire_post.setOnClickListener {
                    fire_post.showDropDown()
                }
                fire_post.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                fire_post.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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
                val adapterIdExperience = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, result.result)
                fire_work_experience_r_f.setAdapter(adapterIdExperience)

                fire_work_experience_r_f.keyListener = null
                fire_work_experience_r_f.setOnItemClickListener { adapterView, view, position, l ->
                    yearsRfId = result.result[position].id!!
                    fire_work_experience_r_f.showDropDown()
                }
                fire_work_experience_r_f.setOnClickListener {
                    fire_work_experience_r_f.showDropDown()
                }
                fire_work_experience_r_f.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                fire_work_experience_r_f.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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
                val adapterIdExperience = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, result.result)
                fire_work_experience.setAdapter(adapterIdExperience)

                fire_work_experience.keyListener = null
                fire_work_experience.setOnItemClickListener { adapterView, view, position, l ->
                    yearsId = result.result[position].id!!
                    fire_work_experience.showDropDown()
                }
                fire_work_experience.setOnClickListener {
                    fire_work_experience.showDropDown()
                }
                fire_work_experience.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                fire_work_experience.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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
                val adapterIdIncome = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, result.result)
                fire_list_income.setAdapter(adapterIdIncome)

                fire_list_income.keyListener = null
                fire_list_income.setOnItemClickListener { adapterView, view, position, l ->
                    incomeId = result.result[position].id!!
                    fire_list_income.showDropDown()
                }
                fire_list_income.setOnClickListener {
                    fire_list_income.showDropDown()
                }
                fire_list_income.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                fire_list_income.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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
                val adapterIdIncome = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, result.result)
                fire_additional_income.setAdapter(adapterIdIncome)

                fire_additional_income.keyListener = null
                fire_additional_income.setOnItemClickListener { adapterView, view, position, l ->
                    typeIncomeId = result.result[position].id!!
                    fire_additional_income.showDropDown()
                }
                fire_additional_income.setOnClickListener {
                    fire_additional_income.showDropDown()
                }
                fire_additional_income.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                fire_additional_income.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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
                val adapterIdIncome = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, result.result)
                fire_additional_amount.setAdapter(adapterIdIncome)

                fire_additional_amount.keyListener = null
                fire_additional_amount.setOnItemClickListener { adapterView, view, position, l ->
                    additionalId = result.result[position].id!!
                    fire_additional_amount.showDropDown()
                }
                fire_additional_amount.setOnClickListener {
                    fire_additional_amount.showDropDown()
                }
                fire_additional_amount.onFocusChangeListener =
                    View.OnFocusChangeListener { view, hasFocus ->
                        try {
                            if (hasFocus) {
                                closeKeyboard()
                                fire_additional_amount.showDropDown()
                            }
                        } catch (e: Exception) {
                        }
                    }
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
}
