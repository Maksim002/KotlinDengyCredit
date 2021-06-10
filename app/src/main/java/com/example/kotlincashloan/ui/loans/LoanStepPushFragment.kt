package com.example.kotlincashloan.ui.loans

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.animationGenerator
import com.example.kotlincashloan.extension.animationLoanGenerator
import com.example.kotlincashloan.extension.shimmerStop
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.activity_get_loan.*
import kotlinx.android.synthetic.main.fragment_loan_step_push.*

class LoanStepPushFragment(var status: Boolean, var applicationStatus: Boolean) : Fragment() {

    private val handler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan_step_push, container, false)
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        handler.postDelayed(Runnable { // Do something after 5s = 500ms
            if (menuVisible && isResumed) {
                if (!AppPreferences.isRepeat) {
                    //генерирует анимацию перехода
                    animationGenerator((activity as GetLoanActivity?)!!.shimmer_step_loan,handler,  requireActivity())
                }
            }
        }, 500)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (applicationStatus == false) {
            // Отоброожает кнопку если статус true видем закрытия
            (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.GONE
        } else {
            (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.VISIBLE
        }

        no_connection_repeat.setOnClickListener {
            AppPreferences.applicationId = ""
            requireActivity().finish()
        }
    }
}
