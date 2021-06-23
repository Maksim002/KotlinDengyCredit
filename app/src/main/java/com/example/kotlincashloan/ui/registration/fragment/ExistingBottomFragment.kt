package com.example.kotlinscreenscanner.ui.login.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.listener.ExistingBottomListener
import com.example.kotlincashloan.adapter.listener.GetApiListener
import com.example.kotlincashloan.extension.getApi
import com.example.kotlincashloan.extension.loadingConnection
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.myapplication.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_existing_bottom.*
import java.util.*


class ExistingBottomFragment(private val listener: ExistingBottomListener) : BottomSheetDialogFragment(), GetApiListener{
    private var viewModel = LoginViewModel()
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
        HomeActivity.alert = LoadingAlert(requireActivity())
        initClick()

    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme;
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
            listener.existingClockListener()
            this.dismiss()
            AppPreferences.password = ""
            AppPreferences.savePin = null
        }
        existing_removal.setOnClickListener {
            if (currentPinInput.isNotEmpty())
                currentPinInput = currentPinInput.substring(0, currentPinInput.length - 1)
            pin_verification_code.setText(currentPinInput)
        }
    }

    fun check() {
        if (currentPinInput.length == 4) {
            ObservedInternet().observedInternet(requireContext())
            if (!AppPreferences.observedInternet) {
                pin_verification_code.setText(initpin)
                currentPinInput = ""
                loadingConnection(activity as AppCompatActivity)
            } else {
                if (AppPreferences.tokenApi == "" && AppPreferences.urlApi == "") {
                    getApi(requireActivity(), this)
                } else {
                    pinCode()
                }
            }
        }
    }

    private fun pinCode(){
        val number = MyUtils.toCodeNumber(currentPinInput)
        if (AppPreferences.savePin == number) {
            val map = HashMap<String, String>()
            map.put("password", AppPreferences.password.toString())
            map.put("login", AppPreferences.login.toString())
            map.put("uid", AppPreferences.pushNotificationsId.toString())
            map.put("system", "1")
            HomeActivity.alert.show()
            viewModel.auth(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result != null) {
                            AppPreferences.token = data.result.token
                            val intent = Intent(context, MainActivity::class.java)
                            startActivity(intent)
                            this.dismiss()
                        } else {
                            if (data.error.code == 401) {
                                initAuthorized()
                            } else {
                                pin_verification_code.setText(initpin)
                                currentPinInput = ""
                                loadingMistake(activity as AppCompatActivity)
                            }
                        }
                    }
                    Status.ERROR -> {
                        if (msg == "401") {
                            initAuthorized()
                        } else {
                            pin_verification_code.setText(initpin)
                            currentPinInput = ""
                            loadingMistake(activity as AppCompatActivity)
                        }
                    }
                    Status.NETWORK -> {
                        if (msg == "600" || msg == "601") {
                            pin_verification_code.setText(initpin)
                            currentPinInput = ""
                            loadingMistake(activity as AppCompatActivity)
                        } else {
                            pin_verification_code.setText(initpin)
                            currentPinInput = ""
                            loadingConnection(activity as AppCompatActivity)
                        }
                    }
                }
                HomeActivity.alert.hide()
            })
        } else {
            currentPinInput = ""
            pin_verification_code.setText(initpin)
            existing_liner_anim.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake))
        }
    }

    private fun initAuthorized() {
        this.dismiss()
    }

    override fun onClickListenerApi() {
        pinCode()
    }

    override fun onClickListenerError() {
        layout_existing.isClickable = true
        layout_existing.isEnabled = true
        currentPinInput = ""
        pin_verification_code.setText(initpin)
        listener.existingClockListenerError()
    }
}