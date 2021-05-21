package com.example.kotlincashloan.ui.loans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kotlincashloan.R
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.activity_get_loan.*
import kotlinx.android.synthetic.main.fragment_loan_step_push.*

class LoanStepPushFragment(var status: Boolean, var applicationStatus: Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_step_push, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (status == false){
            (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.GONE
        }else{
            // Отоброожает кнопку если статус true видем закрытия
            (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.VISIBLE
        }

        no_connection_repeat.setOnClickListener {
            AppPreferences.applicationId = ""
            requireActivity().finish()
        }
    }
}
