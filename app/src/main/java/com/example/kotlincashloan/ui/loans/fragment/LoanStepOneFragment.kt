package com.example.kotlincashloan.ui.loans.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlinscreenscanner.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_step_one_loan.*

class LoanStepOneFragment : Fragment() {

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


        shipped_sheet_enter.setOnClickListener {

        }
    }

    private fun initClick() {

    }

    override fun onResume() {
        super.onResume()
        MainActivity.timer.timeStop()
    }
}