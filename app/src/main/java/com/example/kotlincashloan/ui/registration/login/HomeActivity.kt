package com.example.kotlincashloan.ui.registration.login

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.listener.ExistingBottomListener
import com.example.kotlincashloan.adapter.listener.ListenerApiInternet
import com.example.kotlincashloan.extension.*
import com.example.kotlincashloan.ui.registration.recovery.PasswordRecoveryActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TimerListener
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.adapter.PintCodeBottomListener
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.kotlinscreenscanner.ui.login.NumberActivity
import com.example.kotlinscreenscanner.ui.login.fragment.ExistingBottomFragment
import com.example.kotlinscreenscanner.ui.login.fragment.PinCodeBottomFragment
import com.example.myapplication.LoginViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.himanshurawat.hasher.HashType
import com.himanshurawat.hasher.Hasher
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.home_forget_password
import kotlinx.android.synthetic.main.activity_number.*
import kotlinx.android.synthetic.main.actyviti_questionnaire.*
import kotlinx.android.synthetic.main.fragment_loans.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import kotlinx.android.synthetic.main.item_no_connection.*
import java.util.*
import java.util.concurrent.Executor

class HomeActivity : AppCompatActivity(), PintCodeBottomListener, ExistingBottomListener, ListenerApiInternet {
    private var viewModel = LoginViewModel()
    private var tokenId = ""
    private lateinit var timer: TimerListener
    private var inputsAnim = false
    private var recPosition = 0
    private var isEntrance = false
    private var genAnim = false
    private lateinit var handler: Handler
    private var splash = false
    private var registration = false
    private var forgetPassword = false
    private var biometrics = false

    companion object {
        var repeatedClick = 0
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
        alert = LoadingAlert(this)
        timer = TimerListener(this)
        handler = Handler()
        iniClick()
        initCheck()
        initView()

        if (!AppPreferences.isRestart) {
            try {
                splash = intent.extras!!.getBoolean("splash")
                if (splash && !home_login_code.isChecked) {
                    //profileAnim анимация для перехода с адного дествия в другое
                    TransitionAnimation(this).transitionRightActivity(notification_con_lay)
                }
                AppPreferences.isRestart = splash
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    // сервис получает логен токен с firebase
    private fun initApyService() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            home_no_connection.visibility = View.VISIBLE
            home_layout.visibility = View.GONE
            this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            home_layout.visibility = View.VISIBLE
            home_no_connection.visibility = View.GONE
            if (AppPreferences.savePin == "") {
                shimmerStart(shimmer_home, this)
            }else{
                alert.show()
            }
            val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder().build()
            remoteConfig.setConfigSettingsAsync(configSettings);
            remoteConfig.fetch(0).addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    remoteConfig.fetchAndActivate()
                    val urlApi = remoteConfig.getString("url_dev")
                    val tokenApi = remoteConfig.getString("token_dev")
                    if (AppPreferences.urlApi != urlApi || AppPreferences.tokenApi != tokenApi){
                        if (urlApi != "" && tokenApi != ""){
                            AppPreferences.urlApi = urlApi
                            AppPreferences.tokenApi = tokenApi
                        }
                        selectedInput()
                        alert.hide()
                    }else if (AppPreferences.tokenApi == "" && AppPreferences.urlApi == "") {
                        AppPreferences.urlApi = urlApi
                        AppPreferences.tokenApi = tokenApi
                        if (recPosition <= 5) {
                            initApyService()
                            recPosition++
                        } else {
                            if (!genAnim) {
                                //генерирует анимацию перехода
                                shimmerStop(shimmer_home, this)
                                genAnim = true
                            }
                            home_no_connection.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE
                            home_incorrect.visibility = View.GONE
                            apiConnection(this, this)
                        }
                    } else {
                        selectedInput()
                        alert.hide()
                    }
                } else {
                    if (AppPreferences.tokenApi != "" && AppPreferences.urlApi != ""){
                        selectedInput()
                    }else{
                        home_no_connection.visibility = View.GONE
                        home_layout.visibility = View.VISIBLE
                        home_incorrect.visibility = View.GONE
                        apiConnection(this, this)
                    }
                    alert.hide()
                }
            })
        }
    }
    //Обрабатывает вид входа в приложение
    private fun selectedInput(){
        if (!registration && !forgetPassword && !biometrics) {
            if (isEntrance) {
                isEntrance = false
                //todo проверить
                iniResult()
            }
        } else {
            if (registration) {
                registration()
                registration = false
            } else if (forgetPassword) {
                forgetPassword()
                forgetPassword = false
            } else if (biometrics) {
                biometrics()
                biometrics = false
            }
        }
    }

    private fun iniClick() {
        home_show.setOnClickListener {
            if (AppPreferences.isValid) {
                AppPreferences.isValid = false
                home_text_password.transformationMethod = PasswordTransformationMethod()
                home_text_password.setSelection(home_text_password.text!!.length);
            } else {
                AppPreferences.isValid = true
                home_text_password.transformationMethod = null
                home_text_password.setSelection(home_text_password.text!!.length);
            }
        }

        home_forget_password.setOnClickListener {
            genAnim = false
            this.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (AppPreferences.tokenApi == "" && AppPreferences.urlApi == "") {
                forgetPassword = true
                initApyService()
            } else {
                forgetPassword()
            }

        }

        home_registration.setOnClickListener {
            genAnim = false
            this.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (AppPreferences.tokenApi == "" && AppPreferences.urlApi == "") {
                registration = true
                initApyService()
            } else {
                registration()
            }
        }

        no_connection_repeat.setOnClickListener {
            if (home_touch_id.isChecked == true) {
                iniTouchId()
                home_incorrect.visibility = View.GONE
            } else {
                if (AppPreferences.tokenApi == "" && AppPreferences.urlApi == "") {
                    initApyService()
                } else {
                    iniResult()
                }
            }
        }

        home_enter.setOnClickListener {
            isEntrance = true
            if (validate()) {
                this.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                initApyService()
            }
        }
    }

    // Преход на окно забыли пароль
    private fun forgetPassword() {
        val intent = Intent(this, PasswordRecoveryActivity::class.java)
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        inputsAnim = true
        home_touch_id.isChecked = false
        home_login_code.isChecked = false
        shimmerStop(shimmer_home, this)
        startActivity(intent)
    }

    // Преход на окно регистрации
    private fun registration() {
        val intent = Intent(this, NumberActivity::class.java)
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        inputsAnim = true
        home_touch_id.isChecked = false
        home_login_code.isChecked = false
        shimmerStop(shimmer_home, this)
        startActivity(intent)
    }


    lateinit var t: Thread
    private fun iniResult() {
        ObservedInternet().observedInternet(this)
        if (!AppPreferences.observedInternet) {
            home_no_connection.visibility = View.VISIBLE
            home_layout.visibility = View.GONE
        } else {
            if (!home_login_code.isChecked) {
                shimmerStart(shimmer_home, this)
            } else {
                alert.show()
            }
            val map = HashMap<String, String>()
            map.put("password", Hasher.hash(home_text_password.text.toString(), HashType.MD5))
            map.put("login", home_text_login.text.toString())
            map.put("uid", AppPreferences.pushNotificationsId.toString())
            map.put("system", "1")
            home_enter.isEnabled = false
            viewModel.auth(map).observe(this, Observer { result ->
                val msg = result.msg
                val data = result.data
                when (result.status) {
                    Status.SUCCESS -> {
                        if (data!!.result == null) {
                            if (data.code == 200){
                                if (data.error.code == 500 || data.error.code == 409) {
                                    home_incorrect.visibility = View.VISIBLE
                                    home_no_connection.visibility = View.GONE
                                    home_layout.visibility = View.VISIBLE
                                    shimmer_home.visibility = View.GONE
                                    loadingMistake(this)
                                } else if (data.error.code == 400) {
                                    home_incorrect.visibility = View.VISIBLE
                                    home_no_connection.visibility = View.GONE
                                    home_layout.visibility = View.VISIBLE
                                    shimmer_home.visibility = View.GONE
                                } else {
                                    home_no_connection.visibility = View.GONE
                                    home_layout.visibility = View.VISIBLE
                                    home_incorrect.visibility = View.VISIBLE
                                    shimmer_home.visibility = View.GONE
                                    loadingMistake(this)
                                }
                            }else{
                                if (data.code == 500 || data.code == 409) {
                                    home_incorrect.visibility = View.VISIBLE
                                    home_no_connection.visibility = View.GONE
                                    home_layout.visibility = View.VISIBLE
                                    shimmer_home.visibility = View.GONE
                                    loadingMistake(this)
                                } else if (data.code == 400) {
                                    home_incorrect.visibility = View.VISIBLE
                                    home_no_connection.visibility = View.GONE
                                    home_layout.visibility = View.VISIBLE
                                    shimmer_home.visibility = View.GONE
                                } else {
                                    home_no_connection.visibility = View.GONE
                                    home_layout.visibility = View.VISIBLE
                                    home_incorrect.visibility = View.VISIBLE
                                    shimmer_home.visibility = View.GONE
                                    loadingMistake(this)
                                }
                            }
                        } else {
                            home_no_connection.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE
                            AppPreferences.isLogined = true
                            tokenId = data.result.token
                            if (home_login_code.isChecked) {
                                home_incorrect.visibility = View.GONE
                                if (!AppPreferences.isNumber) {
                                    //генерирует анимацию перехода
                                    shimmerStop(shimmer_home, this)
                                    initBottomSheet()
                                    AppPreferences.isNumber = true
                                }
                            } else {
                                AppPreferences.token = data.result.token
                                AppPreferences.login = data.result.login
                                AppPreferences.password = Hasher.hash(home_text_password.text.toString(), HashType.MD5)
                                if (AppPreferences.token != null) {
                                    home_incorrect.visibility = View.GONE
                                    startMainActivity()
                                }
                            }
                            if (home_remember_username.isChecked) {
                                AppPreferences.isRemember = home_remember_username.isChecked
                                AppPreferences.isTouchId = home_touch_id.isChecked
                                AppPreferences.isLoginCode = home_login_code.isChecked
                                viewModel.save(home_text_login.text.toString(), data.result.token)
                                AppPreferences.password = Hasher.hash(home_text_password.text.toString(), HashType.MD5)
                            } else {
                                AppPreferences.isRemember = false
                                AppPreferences.clearLogin()
                            }
                        }
                    }
                    Status.ERROR -> {
                        if (msg == "500" || msg == "409" || msg == "400" || msg == "429") {
                            home_incorrect.visibility = View.VISIBLE
                            home_no_connection.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE
                            shimmer_home.visibility = View.GONE
                            loadingMistake(this)
                        } else {
                            home_no_connection.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE
                            home_incorrect.visibility = View.VISIBLE
                            shimmer_home.visibility = View.GONE
                            loadingMistake(this)
                        }
                    }
                    Status.NETWORK -> {
                        if (msg == "601") {
                            home_no_connection.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE
                            home_incorrect.visibility = View.VISIBLE
                            shimmer_home.visibility = View.GONE
                            loadingMistake(this)
                        } else if (msg == "600"){
                            home_no_connection.visibility = View.GONE
                            home_incorrect.visibility = View.GONE
                            home_layout.visibility = View.VISIBLE
                            shimmer_home.visibility = View.GONE
                            loadingConnection(this)
                        }
                    }
                }
                home_enter.isEnabled = true
                if (home_login_code.isChecked) {
                    alert.hide()
                } else {
                    shimmerStop(shimmer_home, this)
                }
                 this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            })
        }
    }

    override fun onStop() {
        super.onStop()
//        if (!home_login_code.isChecked) {
//            shimmerStop(shimmer_home, this)
//        }
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
            repeatedClick = 1
        }

        home_login_code.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                AppPreferences.resultPassword = "2"
                home_touch_id.isChecked = false
                home_remember_username.isChecked = true
                home_remember_username.isClickable = false
                AppPreferences.isNumber = false
            } else {
                home_remember_username.isClickable = true
                AppPreferences.isLoginCode = false
                AppPreferences.resultPassword = "2"
                AppPreferences.isNumber = true
            }
        }

        home_touch_id.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                AppPreferences.resultPassword = "1"
                home_login_code.isChecked = false
                home_remember_username.isChecked = true
                home_remember_username.isClickable = false
            } else {
                home_remember_username.isClickable = true
                AppPreferences.isTouchId = false
                AppPreferences.resultPassword = ""
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

    override fun existingClockListenerError() {
        home_no_connection.visibility = View.GONE
        home_layout.visibility = View.VISIBLE
        home_incorrect.visibility = View.GONE
        apiConnection(this, this)
    }

    override fun existingClockListenerResult() {
        iniResult()
    }

    private fun validate(): Boolean {
        var valid = true
        if (home_text_login.text.toString().isEmpty()) {
            editUtils(home_text_login, home_login_error, "Заполните поле", true)
            home_incorrect.visibility = View.GONE
            valid = false
        }

        if (home_text_password.text.toString().isEmpty()) {
            editUtils(home_text_password, home_show_error, "Заполните поле", true)
            home_show.setColorFilter(
                ContextCompat.getColor(this, R.color.colorRed),
                PorterDuff.Mode.SRC_IN
            );
            home_incorrect.visibility = View.GONE
            valid = false
        }
        if (!valid) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
        }

        return valid

    }

    private fun initView() {
        home_text_password.addTextChangedListener {
            editUtils(home_text_password, home_show_error, "Заполните поле", false)
            home_show.setColorFilter(
                ContextCompat.getColor(this, R.color.blackColor),
                PorterDuff.Mode.SRC_IN
            );
        }

        home_text_login.addTextChangedListener {
            editUtils(home_text_login, home_login_error, "Заполните поле", false)
        }

    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        //меняет цвет статус бара
        ColorWindows(this).statusBarTextColor()
        if (AppPreferences.password == "") {
            home_touch_id.isChecked = false
            home_login_code.isChecked = false
        }

        //проверка если галочка пинкода отключена и галочка отпичатка пальза тоже
        // и токен не пустой  то переди сразу на главный экран
        if (home_touch_id.isChecked == false && home_login_code.isChecked == false) {
            if (AppPreferences.token != "") {
                startMainActivity()
            }
            // если токен не пустой и пин код сохранён то переди на главный экран
            // иначе удоляй токен
        } else if (AppPreferences.token != "") {
            if (AppPreferences.savePin != "") {
                startMainActivity()
            } else {
                AppPreferences.token = ""
            }
        } else {
            if (AppPreferences.isNumber) {
                if (repeatedClick != 0) {
                    if (home_login_code.isChecked) {
                        if (AppPreferences.isPinCode) {
                            if (AppPreferences.savePin != "") {
                                initBottomSheet()
                            }
                        }
                    }
                }
                AppPreferences.isNumber = false
            }
        }
        AppPreferences.isValid = false
        home_text_login.getPaint().clearShadowLayer();
    }

    override fun onResume() {
        super.onResume()

        //проверяет Checked включон или нет!
        if (AppPreferences.passwordRecovery == "" && AppPreferences.loginRecovery == "") {
            home_remember_username.isChecked = true
            home_login_code.isChecked = true
            AppPreferences.passwordRecovery = "1"
            AppPreferences.loginRecovery == "1"
        }
        //Анимация перехода
        if (inputsAnim) {
            TransitionAnimation(this).transitionLeft(home_layout_anim)
            inputsAnim = false
        }

        if (AppPreferences.password != "") {
            if (home_touch_id.isChecked) {
                iniTouchId()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Переход в другое акно по отпичатку пальца
    private fun iniTouchId() {
        if (!biometrics) {
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
                    AppPreferences.isTouchId = false
                    home_touch_id.isChecked = false
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
        val biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                // 2
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    if (AppPreferences.tokenApi == "" && AppPreferences.urlApi == "") {
                        biometrics = true
                        initApyService()
                    } else {
                        biometrics()
                    }
                }

                // если я отключаю галочку
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    AppPreferences.password = ""
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

    override fun onPause() {
        super.onPause()
        apiConnection(this, this, true)
    }

    fun biometrics() {
        val map = HashMap<String, String>()
        map.put("password", AppPreferences.password.toString())
        map.put("login", home_text_login.text.toString())
        map.put("uid", AppPreferences.pushNotificationsId.toString())
        map.put("system", "1")
        if (!home_login_code.isChecked) {
            shimmerStart(shimmer_home, this@HomeActivity)
        } else {
            alert.show()
        }
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
                    home_layout.visibility = View.VISIBLE
                    loadingMistake(this@HomeActivity)
                }
                Status.NETWORK -> {
                    if (msg == "601") {
                        home_layout.visibility = View.VISIBLE
                        loadingMistake(this@HomeActivity)
                    } else if (msg == "600"){
                        home_no_connection.visibility = View.GONE
                        home_layout.visibility = View.VISIBLE
                        loadingConnection(this)
                    }
                }
            }
            if (!home_login_code.isChecked) {
                shimmerStop(shimmer_home, this@HomeActivity)
            } else {
                alert.hide()
            }
        })
    }

    override fun onBackPressed() {}

    //Клик повторяет запрос на получение api если раниие был потерен интернет
    override fun listenerOnClickInternet() {
        initApyService()
    }
}