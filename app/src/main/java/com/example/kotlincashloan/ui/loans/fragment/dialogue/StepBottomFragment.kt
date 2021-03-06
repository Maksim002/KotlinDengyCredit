package com.example.kotlincashloan.ui.loans.fragment.dialogue

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.loans.StepClickListener
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_step_bottom.*

class StepBottomFragment(var listener: StepClickListener, var message: String) : BottomSheetDialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_step_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        step_message_text.text = message

        step_next.setOnClickListener {
            listener.onClickStepListener()
            this.dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
    }
}