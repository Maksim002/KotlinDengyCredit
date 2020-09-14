package com.example.kotlinscreenscanner.ui.login.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.login.MainActivity
import com.example.kotlinscreenscanner.ui.Top
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_shipped_sheet.*

class ShippedSheetFragment() : BottomSheetDialogFragment() {
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

    private fun initClick() {
        shipped_sheet_enter.setOnClickListener {
            if (!isInLayout){
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(context, Top::class.java)
                startActivity(intent)
            }
        }
    }
}