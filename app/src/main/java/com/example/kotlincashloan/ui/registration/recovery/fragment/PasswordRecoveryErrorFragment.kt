package com.example.kotlinscreenscanner.ui.login.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.ui.registration.recovery.ContactingServiceActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.fragment_password_recovery_error.*

class PasswordRecoveryErrorFragment() : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_recovery_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HomeActivity.alert = LoadingAlert(requireActivity())
        initClick()
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
    }

    private fun initClick() {
        password_sheer_time.setOnClickListener {
            this.dismiss()
        }

        password_sheer_service.setOnClickListener {
            HomeActivity.alert.show()
            val intent = Intent(context, ContactingServiceActivity::class.java)
            intent.putExtra("number", "0")
            startActivity(intent)
            HomeActivity.alert.hide()
        }
    }
}