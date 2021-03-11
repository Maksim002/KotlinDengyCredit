package com.example.kotlinscreenscanner.ui.login.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.recovery.ContactingServiceActivity
import com.example.kotlincashloan.ui.registration.recovery.PasswordRecoveryActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_authorization_busy_bottom.*

class AuthorizationBusyBottomFragment() : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authorization_busy_bottom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
    }

    private fun initClick() {
        authorization_busy_esc.setOnClickListener {
            this.dismiss()
        }

        authorization_busy_password.setOnClickListener {
            val intent = Intent(requireContext(), PasswordRecoveryActivity::class.java)
            startActivity(intent)
        }

        authorization_busy_support.setOnClickListener {
            val intent = Intent(requireContext(), ContactingServiceActivity::class.java)
            startActivity(intent)
        }
    }
}