package com.example.kotlincashloan.ui.loans.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.animationLoanGenerator
import com.example.kotlincashloan.extension.shimmerStart
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.activity_get_loan.*
import kotlinx.android.synthetic.main.fragment_step_one_loan.*

class LoanStepOneFragment(var status: Boolean) : Fragment() {
    private var viewModel = LoansViewModel()
    private var application = false
    private var handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step_one_loan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initResult()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (status) {
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                if (menuVisible && isResumed) {
                    if (!AppPreferences.isRepeat) {
                        //генерирует анимацию перехода
                        animationLoanGenerator((activity as GetLoanActivity?)!!.shimmer_step_loan, handler, requireActivity())
                    }
                }
            }, 500)
        }else{
//            handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                if (menuVisible && isResumed) {
//                    if (!AppPreferences.isRepeat) {
//                        //генерирует анимацию перехода
//                        animationLoanGenerator((activity as GetLoanActivity?)!!.shimmer_step_loan, handler, requireActivity())
//                    }
//                }
//            }, 500)
        }
    }

        private fun initClick() {
            try {
                application = requireActivity().intent.extras!!.getBoolean("application")
                if (application != false) {
                    AppPreferences.refreshWindow = "true"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            (activity as GetLoanActivity?)!!.loan_cross_clear.visibility = View.GONE

            bottom_step_one.setOnClickListener {
                shimmerStart((activity as GetLoanActivity?)!!.shimmer_step_loan , requireActivity())
                (activity as GetLoanActivity?)!!.get_loan_view_pagers.setCurrentItem(1)
            }
        }

        override fun onStart() {
            super.onStart()
            initClick()
            requireActivity().onBackPressedDispatcher.addCallback(this) {}
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