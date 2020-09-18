package com.example.kotlincashloan.ui.main.registration.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.listener.ExistingBottomListener
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.main.registration.recovery.PasswordRecoveryActivity
import com.example.kotlinscreenscanner.adapter.PintCodeBottomListener
import com.example.kotlinscreenscanner.ui.HomeActivity
import com.example.kotlinscreenscanner.ui.login.NumberActivity
import com.example.kotlinscreenscanner.ui.login.fragment.ExistingBottomFragment
import com.example.kotlinscreenscanner.ui.login.fragment.PinCodeBottomFragment
import com.example.myapplication.LoginViewModel
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_no_connection.*
import java.util.HashMap
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity(), PintCodeBottomListener,
    ExistingBottomListener {
    private var viewModel = LoginViewModel()
    private var tokenId = ""
    companion object {
        lateinit var alert: LoadingAlert
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppPreferences.init(application)
        iniClick()
        initCheck()
        alert = LoadingAlert(this)
    }


    private fun iniClick() {
        main_show.setOnClickListener {
            if (AppPreferences.isValid) {
                AppPreferences.isValid = false
                main_text_password.transformationMethod = PasswordTransformationMethod()
            } else {
                AppPreferences.isValid = true
                main_text_password.transformationMethod = null
            }
        }

        main_forget_password.setOnClickListener {
            val intent = Intent(this, PasswordRecoveryActivity::class.java)
            startActivity(intent)
        }

        main_registration.setOnClickListener {
            val intent = Intent(this, NumberActivity::class.java)
            startActivity(intent)
        }

        no_connection_repeat.setOnClickListener {
            if (main_touch_id.isChecked == true){
                iniTouchId()
                main_incorrect.visibility = View.GONE
            }else{
                iniResult()
            }
        }

        main_enter.setOnClickListener {
            if (validate()) {
                iniResult()
            }
        }
    }


    private fun iniResult() {
        val map = HashMap<String, String>()
        map.put("password", main_text_password.text.toString())
        map.put("login", main_text_login.text.toString())
        alert.show()
        viewModel.auth(map).observe(this, Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result == null) {
                        if (data.error.code == 400 || data.error.code == 500 || data.error.code == 409){
                            main_incorrect.visibility = View.VISIBLE
                            main_no_connection.visibility = View.GONE
                            main_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        }else{
                            main_no_connection.visibility = View.GONE
                            main_layout.visibility = View.VISIBLE
                            main_incorrect.visibility = View.VISIBLE
                            loadingMistake(this)
                        }
                    } else {
                        main_no_connection.visibility = View.GONE
                        main_layout.visibility = View.VISIBLE

                        AppPreferences.isLogined = true
                        tokenId = data.result.token
                        if (main_login_code.isChecked) {
                            main_incorrect.visibility = View.GONE
                            initBottomSheet()
                        } else {
                            main_incorrect.visibility = View.GONE
                            startMainActivity()
                        }
                        if (main_remember_username.isChecked) {
                            AppPreferences.isRemember = main_remember_username.isChecked
                            AppPreferences.isTouchId = main_touch_id.isChecked
                            AppPreferences.isLoginCode = main_login_code.isChecked
                            viewModel.save(
                                main_text_login.text.toString(),
                                data.result.token
                            )
                            AppPreferences.password = main_text_password.text.toString()
                        } else {
                            AppPreferences.isRemember = false
                            AppPreferences.clearLogin()
                        }
                    }
                }
                Status.ERROR -> {
                    main_no_connection.visibility = View.GONE
                    main_layout.visibility = View.VISIBLE
                    main_incorrect.visibility = View.VISIBLE
                    loadingMistake(this)
                }
                Status.NETWORK -> {
                    main_no_connection.visibility = View.VISIBLE
                    main_layout.visibility = View.GONE
                }
            }
            alert.hide()
        })
    }

    private fun initCheck() {
        if (AppPreferences.isRemember) {
            main_remember_username.isChecked = AppPreferences.isRemember
            main_text_login.setText(AppPreferences.login)
        }

        if (AppPreferences.isTouchId) {
            main_touch_id.isChecked = AppPreferences.isTouchId
            iniTouchId()
        }

        if (AppPreferences.isLoginCode) {
            main_login_code.isChecked = AppPreferences.isLoginCode
            initBottomSheet()
        }

        main_login_code.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                main_touch_id.isChecked = false
                main_remember_username.isChecked = true
                main_remember_username.isClickable = false
            } else {
                main_remember_username.isClickable = true
                AppPreferences.isLoginCode = false
            }
        }

        main_touch_id.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                main_login_code.isChecked = false
                main_remember_username.isChecked = true
                main_remember_username.isClickable = false
            } else {
                main_remember_username.isClickable = true
                AppPreferences.isTouchId = false
            }
        }
    }

    private fun initBottomSheet() {
        if (AppPreferences.savePin!!.isNotEmpty()) {
            val bottomSheetDialogFragment = ExistingBottomFragment(this)
            bottomSheetDialogFragment.isCancelable = false;
            bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
        } else if (AppPreferences.savePin!!.isEmpty()) {
            val bottomSheetDialogFragment = PinCodeBottomFragment(this)
            bottomSheetDialogFragment.isCancelable = false;
            bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
        }
    }

    override fun pinCodeClockListener() {
        main_login_code.isChecked = false
    }

    override fun existingClockListener() {
        main_login_code.isChecked = false
    }

    private fun validate(): Boolean {
        var valid = true
        if (main_text_login.text.toString().isEmpty()) {
            main_text_login.error = "Введите логин"
            main_incorrect.visibility = View.GONE
            valid = false
        }

        if (main_text_password.text.toString().isEmpty()) {
            main_text_password.error = "Введите пароль"
            main_incorrect.visibility = View.GONE
            valid = false
        }
        if (!valid){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }

        return valid

    }

    private fun startMainActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        AppPreferences.isValid = false
        main_text_login.getPaint().clearShadowLayer();
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun iniTouchId() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                authUser(executor)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(
                    this,
                    getString(R.string.error_msg_no_biometric_hardware),
                    Toast.LENGTH_LONG
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(
                    this,
                    getString(R.string.error_msg_biometric_hw_unavailable),
                    Toast.LENGTH_LONG
                ).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    this,
                    getString(R.string.error_msg_biometric_not_setup),
                    Toast.LENGTH_LONG
                ).show()
                AppPreferences.isTouchId = false
                main_touch_id.isChecked = false
            }
        }
    }

    private fun authUser(executor: Executor) {
        // 1
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            // 2
            .setTitle(getString(R.string.auth_title))
            // 3
            .setSubtitle(getString(R.string.auth_subtitle))
            // 4
            .setDescription(getString(R.string.auth_description))
            // 5
            .setDeviceCredentialAllowed(false)
            // 6
            .setNegativeButtonText("Отмена")
            // 7
            .build()

        // 1
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                // 2
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    val map = HashMap<String, String>()
                    map.put("password", AppPreferences.password.toString())
                    map.put("login", main_text_login.text.toString())
                    alert.show()
                    viewModel.auth(map).observe(this@MainActivity, Observer { result ->
                        val msg = result.msg
                        val data = result.data
                        when (result.status) {
                            Status.SUCCESS -> {
                                if (data!!.result == null) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        data.error.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    main_no_connection.visibility = View.GONE
                                    main_layout.visibility = View.VISIBLE
                                    tokenId = data.result.token
                                    viewModel.save(
                                        main_text_login.text.toString(),
                                        data.result.token
                                    )
                                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            Status.ERROR -> {
                                loadingMistake(this@MainActivity)
                            }
                            Status.NETWORK -> {
                                main_no_connection.visibility = View.VISIBLE
                                main_layout.visibility = View.GONE
                            }
                        }
                        alert.hide()
                    })
                }

                // 3
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.error_msg_auth_error, errString),
                        Toast.LENGTH_SHORT
                    ).show()
                    main_touch_id.isChecked = false
                }

                // 4
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.error_msg_auth_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }
}