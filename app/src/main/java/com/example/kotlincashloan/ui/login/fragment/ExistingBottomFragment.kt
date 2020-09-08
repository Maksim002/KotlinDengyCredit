package com.example.kotlinscreenscanner.ui.login.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlinscreenscanner.ui.login.QuestionnaireActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_existing_bottom.*

class ExistingBottomFragment : BottomSheetDialogFragment() {
    var currentPinInput = ""
    var initpin = ""

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_existing_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() {
        existing_one.setOnClickListener {
            currentPinInput += "1"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_two.setOnClickListener {
            currentPinInput += "2"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_three.setOnClickListener {
            currentPinInput += "3"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_four.setOnClickListener {
            currentPinInput += "4"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_five.setOnClickListener {
            currentPinInput += "5"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_six.setOnClickListener {
            currentPinInput += "6"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_seven.setOnClickListener {
            currentPinInput += "7"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_eight.setOnClickListener {
            currentPinInput += "8"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_nine.setOnClickListener {
            currentPinInput += "9"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_zero.setOnClickListener {
            currentPinInput += "0"
            pin_verification_code.setText(currentPinInput)
            check()
        }
        existing_btn.setOnClickListener {
            currentPinInput = ""
            pin_verification_code.setText(initpin)
        }
        existing_removal.setOnClickListener {
            if (currentPinInput.isNotEmpty())
                currentPinInput = currentPinInput.substring(0, currentPinInput.length - 1)
                pin_verification_code.setText(currentPinInput)
        }
    }

    fun check(){
        if (currentPinInput.length == 4){
            if (AppPreferences.savePin == currentPinInput){
                val intent = Intent(context, QuestionnaireActivity::class.java)
                startActivity(intent)
            }
        }
    }
}