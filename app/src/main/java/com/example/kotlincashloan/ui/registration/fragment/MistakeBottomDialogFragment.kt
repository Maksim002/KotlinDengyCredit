package com.example.kotlinscreenscanner.ui.login.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_mistake_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_mistake_bottom_sheet.mistake_ok
import kotlinx.android.synthetic.main.fragment_mistake_dialog_sheet.*

class MistakeBottomDialogFragment() : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mistake_dialog_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
    }

    private fun initClick() {
        mistake_code_ok.setOnClickListener {
            requireActivity().finish()
        }
    }
}