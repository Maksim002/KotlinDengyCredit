package com.example.kotlincashloan.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.text.method.PasswordTransformationMethod
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.cookiebar.CookieBar
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.profile.ClientInfoResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.ui.registration.recovery.ContactingServiceActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.service.model.profile.CounterNumResultModel
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import kotlinx.android.synthetic.main.fragment_profile_setting.home_forget_password
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileSettingFragment : Fragment() {
    private var viewModel = ProfileViewModel()
    private var errorCodeGender = ""
    private var errorCodeNationality = ""
    private var errorListAvailableCountry = ""
    private var errorListSecretQuestion = ""
    private var errorSaveProfile = ""
    private var errorClientInfo = ""
    val handler = Handler()
    var clientResult = ClientInfoResultModel()
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var list: ArrayList<CounterNumResultModel> = arrayListOf()
    private var codeNationality = 0
    private var numberAvailable = 0
    private var checkNumber = 0
    private var codeMack = ""
    private var reNum = ""
    private var question = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //форма даты
        simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)
        setTitle("Профиль", resources.getColor(R.color.whiteColor))
        initClick()
    }

    private fun initRestart() {
        val mapNationality = HashMap<String, String>()
        mapNationality.put("login", clientResult.gender.toString())

        val mapGender = HashMap<String, String>()
        mapGender.put("login", clientResult.nationality.toString())

        val mapRegistration = HashMap<String, String>()
        mapRegistration.put("id", "")

        val mapInfo = HashMap<String, String>()
        mapInfo.put("login", AppPreferences.login.toString())
        mapInfo.put("token", AppPreferences.token.toString())

        //проверка на интернет
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
            viewModel.errorClientInfo.value = null
            viewModel.errorListGender.value = null
            viewModel.errorListNationality.value = null
            viewModel.errorListAvailableCountry.value = null
            viewModel.errorListSecretQuestion.value = null
            viewModel.errorSaveProfile.value = null
            errorClientInfo = "601"
            errorCodeGender = "601"
            errorCodeNationality = "601"
            errorListAvailableCountry = "601"
            errorListSecretQuestion = "601"
        } else {
            if (viewModel.errorListGender.value == null && viewModel.errorListNationality.value == null && viewModel.errorListAvailableCountry.value == null &&
                viewModel.errorListSecretQuestion.value == null && viewModel.errorSaveProfile.value == null && viewModel.errorClientInfo.value == null
            ) {
                if (!viewModel.refreshCode) {
                    HomeActivity.alert.show()
                }
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    viewModel.refreshCode = false
                    viewModel.clientInfo(mapInfo)
                    viewModel.listGender(mapGender)
                    viewModel.getListNationality(mapNationality)
                    viewModel.listAvailableCountry(mapRegistration)
                    viewModel.listSecretQuestion(mapRegistration)
                    initResult()
                }, 500)
            } else {
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    if (viewModel.errorListGender.value != null) {
                        viewModel.errorListGender.value = null
                        viewModel.listGenderDta.postValue(null)
                        viewModel.listGender(mapGender)
                    } else if (viewModel.errorListNationality.value != null) {
                        viewModel.errorListNationality.value = null
                        viewModel.listNationalityDta.postValue(null)
                        viewModel.getListNationality(mapNationality)
                    } else if (viewModel.errorListAvailableCountry.value != null) {
                        viewModel.errorListAvailableCountry.value = null
                        viewModel.listAvailableCountryDta.postValue(null)
                        viewModel.listAvailableCountry(mapRegistration)
                    } else if (viewModel.errorListSecretQuestion.value != null) {
                        viewModel.errorListSecretQuestion.value = null
                        viewModel.listSecretQuestionDta.postValue(null)
                        viewModel.listSecretQuestion(mapRegistration)
                    } else if (viewModel.errorClientInfo.value != null) {
                        viewModel.listClientInfoDta.postValue(null)
                        viewModel.errorClientInfo.value = null
                        viewModel.clientInfo(mapInfo)
                    } else {
                        viewModel.clientInfo(mapInfo)
                        viewModel.listGender(mapGender)
                        viewModel.getListNationality(mapNationality)
                        viewModel.listAvailableCountry(mapRegistration)
                        viewModel.listSecretQuestion(mapRegistration)
                        initResult()
                    }
                    initResult()
                }, 500)
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun initResult() {
        //если все успешно получает информацию о пользователе
        viewModel.listClientInfoDta.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { result ->
                try {
                    if (result.result != null) {
                        clientResult = result.result
                        profile_setting_fio.setText(clientResult.firstName + " " + clientResult.lastName)
                        profile_setting_second_name.setText(clientResult.secondName)
                        profile_setting_first_name.setText(clientResult.firstName)
                        profile_setting_last_name.setText(clientResult.lastName)
                        profile_setting_data.setText(MyUtils.toMyDate(clientResult.uDate.toString()))
                        profile_s_response.setText(clientResult.response)
                        errorClientInfo = result.code.toString()
                        resultSuccessfully()

                        //получение полов
                        viewModel.listGenderDta.observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer { result ->
                                if (result.result != null) {
                                    profile_setting_gender.setText(result.result[clientResult.gender!!.toInt() - 1].name)
                                    errorCodeGender = result.code.toString()
                                    resultSuccessfully()
                                } else {
                                    listListResult(result.error.code!!)
                                }
                            })

                        viewModel.errorListGender.observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer { error ->
                                if (error != null) {
                                    errorCodeGender = error
                                    errorList(error)
                                }
                            })

                        //получение гражданства
                        viewModel.listNationalityDta.observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer { result ->
                                try {
                                    if (result.result != null) {
                                        profile_s_nationality.setText(result.result[clientResult.nationality!!.toInt() - 1].name)
                                        errorCodeNationality = result.code.toString()
                                        resultSuccessfully()
                                    } else {
                                        listListResult(result.error.code!!)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            })

                        viewModel.errorListNationality.observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer { error ->
                                try {
                                    if (error != null) {
                                        errorCodeNationality = error
                                        errorList(error)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            })

                        //Список доступных стран
                        viewModel.listAvailableCountryDta.observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer { result ->
                                try {
                                    if (result.result != null) {
                                        if (clientResult.phoneFirst != "" && clientResult.phoneSecond != "") {
                                            profile_setting_second_phone.mask = null
                                            profile_setting_first.mask = null

                                            val firstNationality =
                                                clientResult.phoneFirst!!.toInt() - 1
                                            val secondNationality =
                                                clientResult.phoneSecond!!.toInt() - 1

                                            codeNationality = secondNationality

                                            checkNumber = secondNationality

                                            numberAvailable =
                                                result.result[checkNumber].phoneLength!!.toInt()

                                            codeMack =
                                                result.result[secondNationality].phoneCode.toString()

                                            profile_setting_first.mask =
                                                result.result[firstNationality].phoneMask
                                            profile_setting_first.setText(
                                                MyUtils.toMask(
                                                    clientResult.firstPhone.toString(),
                                                    result.result[firstNationality].phoneCode!!.length,
                                                    result.result[firstNationality].phoneLength!!.toInt()
                                                )
                                            )

                                            profile_setting_second_phone.mask =
                                                result.result[secondNationality].phoneMaskSmall
                                            profile_setting_second_phone.setText(
                                                MyUtils.toMask(
                                                    clientResult.secondPhone.toString(),
                                                    result.result[secondNationality].phoneCode!!.length,
                                                    result.result[secondNationality].phoneLength!!.toInt()
                                                )
                                            )
                                            profile_s_mask.setText("+" + result.result[secondNationality].phoneCode)
                                            list = result.result
                                        }
                                        val adapterListCountry = ArrayAdapter(
                                            requireContext(),
                                            android.R.layout.simple_dropdown_item_1line,
                                            result.result
                                        )
                                        profile_s_mask.setAdapter(adapterListCountry)

                                        profile_s_mask.keyListener = null
                                        profile_s_mask.setOnItemClickListener { adapterView, view, position, l ->
                                            codeNationality = position
                                            codeMack = result.result[position].phoneCode.toString()
                                            numberAvailable =
                                                result.result[position].phoneLength!!.toInt()
                                            profile_setting_second_phone.setText("")
                                            profile_s_mask.showDropDown()
                                            profile_s_mask.clearFocus()
                                        }
                                        profile_s_mask.setOnClickListener {
                                            profile_s_mask.showDropDown()
                                        }
                                        profile_s_mask.onFocusChangeListener =
                                            View.OnFocusChangeListener { view, hasFocus ->
                                                try {
                                                    if (hasFocus) {
                                                        closeKeyboard()
                                                        profile_s_mask.showDropDown()
                                                    }
                                                } catch (e: Exception) {
                                                }
                                            }
                                        errorListAvailableCountry = result.code.toString()
                                        resultSuccessfully()
                                    } else {
                                        listListResult(result.error.code!!)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            })

                        viewModel.errorListAvailableCountry.observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer { error ->
                                if (error != null) {
                                    errorListAvailableCountry = error
                                    errorList(error)
                                }
                            })

                        //Список секретных вопросов
                        viewModel.listSecretQuestionDta.observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer { result ->
                                try {
                                    if (result.result != null) {
                                        profile_s_question.setText(result.result[clientResult.question!!.toInt() - 1].name)
                                        var numberPosition = 0
                                        if (question == "") {
                                            numberPosition = clientResult.question!!.toInt()
                                            question = numberPosition.toString()
                                        }
                                        val adapterListCountry = ArrayAdapter(
                                            requireContext(),
                                            android.R.layout.simple_dropdown_item_1line,
                                            result.result
                                        )
                                        profile_s_question.setAdapter(adapterListCountry)

                                        profile_s_question.keyListener = null
                                        profile_s_question.setOnItemClickListener { adapterView, view, position, l ->

                                            question = result.result[position].id.toString()
                                            profile_s_question.showDropDown()
                                            profile_s_question.clearFocus()
                                        }
                                        click_s_question.setOnClickListener {
                                            closeKeyboard()
                                            profile_s_question.showDropDown()
                                        }
                                        profile_s_question.onFocusChangeListener =
                                            View.OnFocusChangeListener { view, hasFocus ->
                                                try {
                                                    if (hasFocus) {
                                                        closeKeyboard()
                                                        profile_s_question.showDropDown()
                                                    }
                                                } catch (e: Exception) {
                                                }
                                            }
                                        errorListSecretQuestion = result.code.toString()
                                        resultSuccessfully()
                                    } else {
                                        listListResult(result.error.code!!)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                profile_s_swipe.isRefreshing = false
                            })

                        viewModel.errorListSecretQuestion.observe(
                            viewLifecycleOwner,
                            androidx.lifecycle.Observer { error ->
                                if (error != null) {
                                    errorListSecretQuestion = error
                                    errorList(error)
                                }
                                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                profile_s_swipe.isRefreshing = false
                            })

                    } else {
                        listListResult(result.error.code!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                profile_s_swipe.isRefreshing = false
            })

        //listClientInfoDta Проверка на ошибки
        viewModel.errorClientInfo.observe(viewLifecycleOwner, androidx.lifecycle.Observer { error ->
            try {
                errorClientInfo = error
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (error != null) {
                errorList(error)
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            profile_s_swipe.isRefreshing = false
        })


        //результат о сохронение данных
        viewModel.listSaveProfileDta.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { result ->
                try {
                    if (result.result != null) {
                        CookieBar.build(requireActivity())
                            .setTitle("Успешно сохранено")
                            .setTitleColor(R.color.blackColor)
                            .setDuration(5000)
                            .setCookiePosition(Gravity.TOP)
                            .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

        viewModel.errorSaveProfile.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { error ->
                if (error != null) {
                    errorSaveProfile = error
                    errorList(error)
                }
            })
    }

    //Метотд для скрытия клавиатуры
    private fun closeKeyboard() {
        val view: View = requireActivity().currentFocus!!
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            val manager = requireActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager?
            manager!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun initClick() {
        var textPasswordOne = ""
        var textPasswordTwo = ""


        profile_s_swipe.setOnRefreshListener {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                viewModel.refreshCode = true
                initRestart()
            }, 500)
        }
        profile_s_swipe.setColorSchemeResources(android.R.color.holo_orange_dark)


        profile_s_one_password.addTextChangedListener {
            textPasswordOne = it.toString()
        }

        profile_s_one_password.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (profile_s_one_password.text.isNotEmpty()) {
                    profile_s_one_password.setSelection(profile_s_one_password.text!!.length);
                }
            }
        }

        profile_s_two_password.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if (profile_s_two_password.text.isNotEmpty()) {
                    profile_s_two_password.setSelection(profile_s_one_password.text!!.length);
                }
            }
        }

        click_s_one_password.setOnClickListener {
            profile_s_one_password.requestFocus()
            profile_s_one_password.setSelection(profile_s_one_password.text!!.length);
            val img =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_s_one_password, 0)
        }

        profile_s_two_password.addTextChangedListener {
            textPasswordTwo = it.toString()
        }

        click_s_two_password.setOnClickListener {
            profile_s_two_password.requestFocus()
            profile_s_two_password.setSelection(profile_s_two_password.text!!.length);
            val img =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_s_two_password, 0)
        }

        //метод удаляет все символы из строки
        profile_setting_second_phone.addTextChangedListener {
            if (profile_setting_second_phone.text.toString() != "") {
                val matchedResults =
                    Regex(pattern = """\d+""").findAll(input = codeMack + profile_setting_second_phone.text.toString())
                val result = StringBuilder()
                for (matchedText in matchedResults) {
                    reNum = result.append(matchedText.value).toString()
                }
            }
        }
        // видем пороль или нет
        var isValidOne = false
        profile_s_one_password_show.setOnClickListener {
            if (!isValidOne) {
                profile_s_one_password.transformationMethod = null
                profile_s_one_password.setSelection(profile_s_one_password.text!!.length);
                isValidOne = true
            } else {
                profile_s_one_password.transformationMethod = PasswordTransformationMethod()
                profile_s_one_password.setSelection(profile_s_one_password.text!!.length);
                isValidOne = false
            }
        }
        // видем пороль или нет
        var isValidTwo = false
        profile_s_two_password_show.setOnClickListener {
            if (!isValidTwo) {
                profile_s_two_password.transformationMethod = null
                profile_s_two_password.setSelection(profile_s_two_password.text!!.length);
                isValidTwo = true
            } else {
                profile_s_two_password.transformationMethod = PasswordTransformationMethod()
                profile_s_two_password.setSelection(profile_s_two_password.text!!.length);
                isValidTwo = false
            }
        }


        access_restricted.setOnClickListener {
            initRestart()
        }

        no_connection_repeat.setOnClickListener {
            initRestart()
        }

        technical_work.setOnClickListener {
            initRestart()
        }

        not_found.setOnClickListener {
            initRestart()
        }

        home_forget_password.setOnClickListener {
            val intent = Intent(context, ContactingServiceActivity::class.java)
            intent.putExtra("number", "1")
            startActivity(intent)
        }


        profile_s_enter.setOnClickListener {
            if (textPasswordOne == textPasswordTwo) {
                if (textPasswordOne != "") {
                    AppPreferences.password = textPasswordOne
                }
            }
            val mapProfile = HashMap<String, String>()
            mapProfile.put("login", AppPreferences.login.toString())
            mapProfile.put("token", AppPreferences.token.toString())
            mapProfile.put("password", AppPreferences.password.toString())
            mapProfile.put("second_phone", reNum)
            mapProfile.put("question", question)
            mapProfile.put("response", profile_s_response.text.toString())
            if (isValid()) {
                viewModel.saveProfile(mapProfile)
            }
        }

        profile_setting_second_phone.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (profile_setting_second_phone != null) {
                    profile_setting_second_phone.mask = null
                    profile_setting_second_phone.mask = list[codeNationality].phoneMaskSmall
                    profile_setting_second_phone.setSelection(profile_setting_second_phone.text!!.length);
                    profile_setting_second_phone.isFocusableInTouchMode = true
                }
            }

        click_s_response.setOnClickListener {
            profile_s_response.requestFocus()
            profile_s_response.setSelection(profile_s_response.text!!.length);
            val img =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_s_response, 0)
        }

        click_s_second.setOnClickListener {
            profile_setting_second_phone.requestFocus()
            val img =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_setting_second_phone, 0)
        }
    }

    private fun listListResult(result: Int) {
        if (result == 400 || result == 500 || result == 409 || result == 429) {
            profile_s_technical_work.visibility = View.VISIBLE
            profile_s_no_connection.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (result == 403) {
            profile_s_access_restricted.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (result == 404) {
            profile_s_not_found.visibility = View.VISIBLE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (result == 401) {
            initAuthorized()
        }
    }

    private fun errorList(error: String) {
        if (error == "400" || error == "500" || error == "600" || error == "429" || error == "409") {
            profile_s_technical_work.visibility = View.VISIBLE
            profile_s_no_connection.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (error == "403") {
            profile_s_access_restricted.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (error == "404") {
            profile_s_not_found.visibility = View.VISIBLE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (error == "601") {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            profile_s_swipe.visibility = View.GONE
        } else if (error == "401") {
            initAuthorized()
        }
    }

    // проверка если errorCode и errorCodeClient == 200
    private fun resultSuccessfully() {
        if (errorCodeGender == "200" && errorCodeNationality == "200" && errorListAvailableCountry == "200" && errorListSecretQuestion == "200" && errorClientInfo == "200") {
            profile_s_swipe.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
        }
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    fun setTitle(title: String?, color: Int) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color)
        }
    }

    override fun onResume() {
        super.onResume()
        profile_s_two_password.transformationMethod = PasswordTransformationMethod()
        profile_s_one_password.transformationMethod = PasswordTransformationMethod()
        if (viewModel.listGenderDta.value != null && viewModel.listGenderDta.value != null && viewModel.listNationalityDta.value != null
            && viewModel.listAvailableCountryDta.value != null && viewModel.listSecretQuestionDta.value != null && viewModel.listClientInfoDta.value != null
        ) {
            if (errorCodeGender == "200" && errorCodeNationality == "200" && errorListAvailableCountry == "200" && errorListSecretQuestion == "200" && errorClientInfo == "200") {
                AppPreferences.reviewCode = 1
                initResult()
            } else {
                AppPreferences.reviewCode = 1
                initRestart()
            }
        } else {
            AppPreferences.reviewCode = 0
            viewModel.refreshCode = false
            initRestart()
        }

        //меняет цвета навигационной понели
        ColorWindows(activity as AppCompatActivity).rollback()

        val backArrow = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        backArrow.setColorFilter(
            resources.getColor(android.R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )
        (activity as AppCompatActivity?)!!.getSupportActionBar()!!.setHomeAsUpIndicator(backArrow)
        profile_s_owner.requestFocus()
    }

    private fun isValid(): Boolean {
        var valid = true
        if (profile_s_one_password.text.toString() != profile_s_two_password.text.toString()) {
            profile_s_two_password.error = "Поля должны совпадать"
            profile_s_one_password.error = "Поля должны совпадать"
            valid = false
        } else {
            profile_s_two_password.error = null
            profile_s_one_password.error = null
        }

        if (profile_setting_second_phone.text!!.toString().isEmpty()) {
            profile_setting_second_phone.error = "Поле не должно быть пустым"
            valid = false
        } else if (reNum.length != numberAvailable) {
            profile_setting_second_phone.error = "Номер введен неправильно"
            profile_setting_second_phone.requestFocus()
            valid = false
        }

        if (profile_s_response.text!!.toString().isEmpty()) {
            profile_s_response.error = "Ответ не должно быть пустым"
            valid = false
        }

        return valid
    }

    override fun onStart() {
        super.onStart()
        // проверка если с timer приходит token null
        if (AppPreferences.token == "") {
            initAuthorized()
        }
    }
}