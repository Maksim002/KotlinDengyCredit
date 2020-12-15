package com.example.kotlincashloan.ui.profile


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.profile.ClientInfoResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import java.text.SimpleDateFormat
import java.util.*

class ProfileSettingFragment : Fragment(){
    private var viewModel = ProfileViewModel()
    private var errorCodeGender = ""
    private var errorCodeNationality = ""
    private var errorListAvailableCountry = ""
    val handler = Handler()
    var clientResult = ClientInfoResultModel()
    private lateinit var simpleDateFormat: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

        initResultGetProfilе()
        initClick()
    }

    private fun initClick() {
        initFocus()

        click_s_second.setOnClickListener {
            profile_setting_second_phone.isEnabled = true
            profile_setting_second_phone.requestFocus()
            profile_setting_second_phone.setSelection(profile_setting_second_phone.text!!.length);
            profile_setting_second_phone.isFocusableInTouchMode = true

            val img = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            img.showSoftInput(profile_setting_second_phone, 3)
        }
    }

    private fun initFocus(){
        profile_setting_second_phone.isEnabled = false
    }

    private fun initRestart() {
        val mapNationality = HashMap<String, String>()
        mapNationality.put("login", clientResult.gender.toString())

        val mapGender = HashMap<String, String>()
        mapGender.put("login", clientResult.nationality.toString())

        val mapAvailable = HashMap<String, String>()
        mapAvailable.put("id", "")
        //проверка на интернет
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            viewModel.errorListGender.value = null
            viewModel.errorListNationality.value = null
            viewModel.errorListAvailableCountry.value = null
            errorCodeGender = "601"
            errorCodeNationality = "601"
            errorListAvailableCountry = "601"
        } else {
            if (viewModel.errorListGender.value == null && viewModel.errorListNationality.value == null && viewModel.errorListAvailableCountry.value == null) {
                if (!viewModel.refreshCode) {
                    HomeActivity.alert.show()
                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
                        viewModel.refreshCode = false
                        viewModel.listGender(mapGender)
                        viewModel.getListNationality(mapNationality)
                        viewModel.listAvailableCountry(mapAvailable)
                        initResult()
                    }, 500)
                }
            } else {
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    if (viewModel.errorListGender.value != null) {
                        viewModel.errorListGender.value = null
                    } else if (viewModel.errorListNationality.value != null) {
                        viewModel.errorListNationality.value = null
                    }else if (viewModel.errorListAvailableCountry.value != null){
                        viewModel.errorListAvailableCountry.value = null
                    }
                    viewModel.listGender(mapGender)
                    viewModel.getListNationality(mapNationality)
                    viewModel.listAvailableCountry(mapAvailable)
                    initResult()
                }, 500)
            }
        }
    }

    private fun initResult() {
        //получение полов
        viewModel.listGenderDta.observe(this, androidx.lifecycle.Observer { result ->
            if (result.result != null) {
                profile_setting_gender.setText(result.result[clientResult.gender!!.toInt()].name)
                resultSuccessfully()
            } else {
                listListResult(result.error.code!!)
            }
        })

        viewModel.errorListGender.observe(this, androidx.lifecycle.Observer { error ->
            if (error != null) {
                errorList(error)
            }
        })

        //получение гражданства
        viewModel.listNationalityDta.observe(this, androidx.lifecycle.Observer { result ->
            if (result.result != null) {
                profile_s_nationality.setText(result.result[clientResult.nationality!!.toInt()].name)
                resultSuccessfully()
            } else {
                listListResult(result.error.code!!)
            }
        })

        viewModel.errorListNationality.observe(this, androidx.lifecycle.Observer { error ->
            if (error != null) {
                errorList(error)
            }
        })

        viewModel.listAvailableCountryDta.observe(this, androidx.lifecycle.Observer { result->
            if (result.result != null) {
                val nationality = clientResult.nationality

                profile_setting_second_phone.mask = result.result[1].phoneMaskSmall
                profile_setting_second_phone.setText(MyUtils.toMask("79995552021"))
            }
        })
    }

    private fun listListResult(result: Int) {
        if (result == 400 || result == 500 || result == 409 || result == 429) {
            profile_s_technical_work.visibility = View.VISIBLE
            profile_s_no_connection.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
        } else if (result == 403) {
            profile_s_access_restricted.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
        } else if (result == 404) {
            profile_s_not_found.visibility = View.VISIBLE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
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
        } else if (error == "403") {
            profile_s_access_restricted.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
        } else if (error == "404") {
            profile_s_not_found.visibility = View.VISIBLE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_technical_work.visibility = View.GONE
            profile_s_no_connection.visibility = View.GONE
        } else if (error == "601") {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
        } else if (error == "401") {
            initAuthorized()
        }
    }

    // проверка если errorCode и errorCodeClient == 200
    private fun resultSuccessfully() {
        profile_setting_layout.visibility = View.VISIBLE
        profile_s_technical_work.visibility = View.GONE
        profile_s_no_connection.visibility = View.GONE
        profile_s_access_restricted.visibility = View.GONE
        profile_s_not_found.visibility = View.GONE
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    // метод получает данные со страницы profile
    private fun initResultGetProfilе() {
         clientResult = try {
             requireArguments().getSerializable("client") as ClientInfoResultModel
        }catch (e: Exception){
             clientResult
        }

        profile_setting_fio.setText(clientResult.firstName + " " + clientResult.lastName)
        profile_setting_phone.setText(clientResult.firstPhone)
        profile_setting_second_name.setText(clientResult.secondName)
        profile_setting_first_name.setText(clientResult.firstName)
        profile_setting_last_name.setText(clientResult.lastName)
        profile_setting_data.setText(MyUtils.toMyDate(clientResult.uDate.toString()))
        profile_s_question.setText(clientResult.question)
        profile_s_response.setText(clientResult.response)
    }

    fun setTitle(title: String?, color: Int) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.listGenderDta.value != null) {
            if (errorCodeGender == "200") {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.orangeColor))
            val decorView: View = (activity as AppCompatActivity).getWindow().getDecorView()
            var systemUiVisibilityFlags = decorView.systemUiVisibility
            systemUiVisibilityFlags =
                systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            decorView.systemUiVisibility = systemUiVisibilityFlags
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(
                ColorDrawable(
                    requireActivity().getColor(
                        R.color.orangeColor
                    )
                )
            )
            toolbar.setTitleTextColor(requireActivity().getColor(R.color.whiteColor))
        }
        val backArrow = resources.getDrawable(R.drawable.ic_baseline_arrow_back_24)
        backArrow.setColorFilter(
            resources.getColor(android.R.color.white),
            PorterDuff.Mode.SRC_ATOP
        )
        (activity as AppCompatActivity?)!!.getSupportActionBar()!!.setHomeAsUpIndicator(backArrow)
    }
}