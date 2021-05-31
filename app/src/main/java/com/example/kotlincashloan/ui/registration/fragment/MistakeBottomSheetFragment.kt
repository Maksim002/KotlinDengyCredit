package com.example.kotlinscreenscanner.ui.login.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.recovery.fragment.BottomDialogListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_mistake_bottom_sheet.*

class MistakeBottomSheetFragment(var listener: BottomDialogListener? = null, var string: String? = null) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mistake_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
    }

    private fun initClick() {
        mistake_ok.setOnClickListener {
            if (AppPreferences.token != ""){
                requireActivity().finish()
            }else{
                try {
                    listener!!.bottomOnClickListener(true)
                }catch (e: Exception){
                    e.printStackTrace()
                }
                this.dismiss()
            }
        }
    }
}