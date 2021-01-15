package com.example.kotlinscreenscanner.ui.login.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlinscreenscanner.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_shipped_sheet.*
import kotlinx.android.synthetic.main.fragment_your_application.*

class ShippedSheetFragment(var profNumber: String) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shipped_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
    }

    override fun onResume() {
        super.onResume()
        if (profNumber != "0"){
            shipped_sheet_enter.text = "Назад"
        }
    }

    private fun initClick() {
        shipped_sheet_enter.setOnClickListener {
            if (AppPreferences.token == ""){
                val intent = Intent(context, HomeActivity::class.java)
                startActivity(intent)
            }else{
                requireActivity().finish()
            }
        }
    }
}