package com.example.kotlincashloan.ui.registration.login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.listener.ExistingBottomListener
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.registration.recovery.PasswordRecoveryActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlinscreenscanner.adapter.PintCodeBottomListener
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.kotlinscreenscanner.ui.login.NumberActivity
import com.example.kotlinscreenscanner.ui.login.fragment.ExistingBottomFragment
import com.example.kotlinscreenscanner.ui.login.fragment.PinCodeBottomFragment
import com.example.myapplication.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.item_no_connection.*
import java.util.*
import java.util.concurrent.Executor


class HomeActivity : AppCompatActivity(), PintCodeBottomListener,
    ExistingBottomListener {
    private var viewModel = LoginViewModel()
    private var tokenId = ""
    companion object {
        lateinit var alert: LoadingAlert
    }

    init {
        try {
            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result?.token
                if (token != null) {
                    AppPreferences.pushNotificationsId = token
                }
            })

        } catch (e: Exception) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        AppPreferences.init(application)
        iniClick()
        initCheck()
        alert = LoadingAlert(this)
    }



    private fun iniClick() {
        home_show.setOnClickListener {
            if (AppPreferences.isValid) {
                AppPreferences.isValid = false
                home_text_password.transformationMethod = PasswordTransformationMethod()
            } else {
                AppPreferences.isValid = true
                home_text_password.transformationMethod = null
            }
        }

        home_forget_password.setOnClickListener {
            val intent = Intent(this, PasswordRecoveryActivity::class.java)
            startActivity(intent)
        }

        home_registration.setOnClickListener {
            val intent = Intent(this, NumberActivity::class.java)
            startActivity(intent)
        }

        no_connection_repeat.setOnClickListener {
            if (home_touch_id.isChecked == true){
                iniTouchId()
                home_incorrect.visibility = View.GONE
            }else{
                iniResult()
            }
        }

        home_enter.setOnClickListener {
            if (validate()) {
                iniResult()
            }
        }
    }

    private fun iniResult() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            home_no_connection.visibility = View.VISIBLE
            home_layout.visibility = View.GONE
        } else {
            alert.show()
            val map = HashMap<String, String>()
            map.put("password", home_text_password.text.toString())
            map.put("login", home_text_login.text.toString())
            map.put("uid", AppPreferences.pushNotificationsId.toString())
            map.put("system", "1")
            home_enter.isEnabled = false
            viewModel.auth(map).observe(this, Observer { result ->
                var msg  = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result == null) {
                            if (data.error.code == 500 || data.error.code == 409 || data.error.code == 400) {
                                home_incorrect.visibility = View.VISIBLE
                                home_no_connection.visibility = View.GONE
                                home_layout.visibility = View.VISIBLE
                                loadingMistake(this)
                            } else {
                                home_no_connection.visibility = View.GONE
                                home_layout.visibility = View.VISIBLE
                                home_incorrect.visibility = View.VISIBLE
                                loadingMistake(this)
                            }
                        } else {
                            home_no_connection.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE

                            AppPreferences.isLogined = true
                            tokenId = data.result.token
                            if (home_login_code.isChecked) {
                                home_incorrect.visibility = View.GONE
                                initBottomSheet()
                            } else {
                                AppPreferences.token = data.result.token
                                AppPreferences.login = data.result.login
                                AppPreferences.password = home_text_password.text.toString()
                                if (AppPreferences.token != null) {
                                    home_incorrect.visibility = View.GONE
                                    startMainActivity()
                                }
                            }
                            if (home_remember_username.isChecked) {
                                AppPreferences.isRemember = home_remember_username.isChecked
                                AppPreferences.isTouchId = home_touch_id.isChecked
                                AppPreferences.isLoginCode = home_login_code.isChecked
                                viewModel.save(
                                    home_text_login.text.toString(),
                                    data.result.token
                                )
                                AppPreferences.password = home_text_password.text.toString()
                            } else {
                                AppPreferences.isRemember = false
                                AppPreferences.clearLogin()
                            }
                        }
                    }
                    Status.ERROR ->{
                        if (msg == "500" || msg == "409" || msg == "400"){
                            home_incorrect.visibility = View.VISIBLE
                            home_no_connection.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE
                            loadingMistake(this)
                        }else{
                            home_no_connection.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE
                            home_incorrect.visibility = View.VISIBLE
                            loadingMistake(this)
                        }
                    }
                    Status.NETWORK ->{
                        if (msg == "600"){
                            home_no_connection.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE
                            home_incorrect.visibility = View.VISIBLE
                            loadingMistake(this)
                        }else{
                            home_no_connection.visibility = View.VISIBLE
                            home_layout.visibility = View.GONE
                        }
                    }
                }
                home_enter.isEnabled = true
                alert.hide()
            })
        }
    }

    private fun initCheck() {
        if (AppPreferences.isRemember) {
            home_remember_username.isChecked = AppPreferences.isRemember
            home_text_login.setText(AppPreferences.login)
        }

        if (AppPreferences.isTouchId) {
            home_touch_id.isChecked = AppPreferences.isTouchId
            iniTouchId()
        }

        if (AppPreferences.isLoginCode) {
            home_login_code.isChecked = AppPreferences.isLoginCode
            initBottomSheet()
        }

        home_login_code.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                home_touch_id.isChecked = false
                home_remember_username.isChecked = true
                home_remember_username.isClickable = false
            } else {
                home_remember_username.isClickable = true
                AppPreferences.isLoginCode = false
            }
        }

        home_touch_id.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                home_login_code.isChecked = false
                home_remember_username.isChecked = true
                home_remember_username.isClickable = false
            } else {
                home_remember_username.isClickable = true
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
        home_login_code.isChecked = false
    }

    override fun existingClockListener() {
        home_login_code.isChecked = false
    }

    private fun validate(): Boolean {
        var valid = true
        if (home_text_login.text.toString().isEmpty()) {
            home_text_login.error = "Введите логин"
            home_incorrect.visibility = View.GONE
            valid = false
        }

        if (home_text_password.text.toString().isEmpty()) {
            home_text_password.error = "Введите пароль"
            home_incorrect.visibility = View.GONE
            valid = false
        }
        if (!valid){
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }

        return valid

    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        AppPreferences.isValid = false
        home_text_login.getPaint().clearShadowLayer();
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
                home_touch_id.isChecked = false
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
                    map.put("login", home_text_login.text.toString())
                    alert.show()
                    viewModel.auth(map).observe(this@HomeActivity, Observer { result ->
                        val msg = result.msg
                        val data = result.data
                        when (result.status) {
                            Status.SUCCESS -> {
                                if (data!!.result == null) {
                                    Toast.makeText(
                                        this@HomeActivity,
                                        data.error.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    home_no_connection.visibility = View.GONE
                                    home_layout.visibility = View.VISIBLE
                                    tokenId = data.result.token
                                    viewModel.save(
                                        home_text_login.text.toString(),
                                        data.result.token
                                    )
                                    val intent = Intent(this@HomeActivity, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            Status.ERROR -> {
                                loadingMistake(this@HomeActivity)
                            }
                            Status.NETWORK -> {
                                home_no_connection.visibility = View.VISIBLE
                                home_layout.visibility = View.GONE
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
                    home_touch_id.isChecked = false
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