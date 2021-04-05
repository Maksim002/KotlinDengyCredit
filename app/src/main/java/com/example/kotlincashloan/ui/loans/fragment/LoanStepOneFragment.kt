package com.example.kotlincashloan.ui.loans.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_step_one_loan.*

class LoanStepOneFragment() : Fragment() {
    private var viewModel = LoansViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_one_loan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
//        initResult()
    }

    private fun initClick() {

        bottom_step_one.setOnClickListener {
           (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(1)
        }
    }

    override fun onStart() {
        super.onStart()
        bottom_step_one.text = "Начать"
        AppPreferences.nationality = ""
        initRequest()
    }

    private fun initRequest() {
        val map = HashMap<String, String>()
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        map.put("id", "1")
        viewModel.listYears(map)
    }

//    private fun initResult() {
//        viewModel.getListYearsDta.observe(viewLifecycleOwner, Observer { result->
//            println()
//        })
//    }
}