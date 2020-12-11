package com.example.kotlincashloan.ui.profile


import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.profile.ClientInfoResultModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.spinnerdatepickerlib.DatePickerDialog
import com.example.spinnerdatepickerlib.DatePickerDialog.OnDateCancelListener
import com.example.spinnerdatepickerlib.SpinnerDatePickerDialogBuilder
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import java.text.SimpleDateFormat
import java.util.*


class ProfileSettingFragment : Fragment(), DatePickerDialog.OnDateSetListener{
    private var viewModel = ProfileViewModel()
    private val map = HashMap<String, String>()
    private var errorCode = ""
    val handler = Handler()
    var clientResult = ClientInfoResultModel()
    private val onCancel: OnDateCancelListener? = null
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var yearSelective = 0
    private var monthSelective = 0
    private var dayOfMonthSelective = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //формат даты
        simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)

        setTitle("Профиль", resources.getColor(R.color.whiteColor))

        initResultGetProfil()
        initClick()
    }

    private fun initClick() {
        //сохронение выброной даты
        profile_setting_choice_data.setOnClickListener {
            if (yearSelective != 0 && monthSelective != 0 && dayOfMonthSelective != 0) {
                showDate(yearSelective, monthSelective, dayOfMonthSelective, R.style.DatePickerSpinner)
            }else{
                showDate(
                    MyUtils.toYear(clientResult.uDate.toString()).toInt(),
                    MyUtils.toMonth(clientResult.uDate.toString()).toInt(),
                    MyUtils.toDay(clientResult.uDate.toString()).toInt(),
                    R.style.DatePickerSpinner
                )
            }
        }
    }

    private fun initRestart() {
        val map = HashMap<String, String>()
        map.put("login", clientResult.gender.toString())
        //проверка на интернет
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_s_no_connection.visibility = View.VISIBLE
            profile_s_technical_work.visibility = View.GONE
            profile_s_access_restricted.visibility = View.GONE
            profile_s_not_found.visibility = View.GONE
            viewModel.errorListGender.value = null
            errorCode = "601"
        } else {
            if (viewModel.errorListGender.value == null) {
                if (!viewModel.refreshCode) {
                    HomeActivity.alert.show()
                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
                        viewModel.refreshCode = false
                        viewModel.listGender(map)
                        initResult()
                    }, 500)
                }
            } else {
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    if (viewModel.errorListGender.value != null) {
                        viewModel.errorListGender.value = null
                    }
                    viewModel.listGender(map)
                    initResult()
                }, 500)
            }
        }
    }

    private fun initResult() {
        viewModel.listGenderDta.observe(this, androidx.lifecycle.Observer { result->

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

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    private fun initResultGetProfil() {
         clientResult = try {
             requireArguments().getSerializable("client") as ClientInfoResultModel
        }catch (e: Exception){
             clientResult
        }

        profile_setting_fio.setText(clientResult.firstName + " " + clientResult.lastName)
        profile_setting_first.setText(clientResult.firstPhone)
        profile_setting_second_name.setText(clientResult.secondName)
        profile_setting_first_name.setText(clientResult.firstName)
        profile_setting_last_name.setText(clientResult.lastName)
        profile_setting_data.setText(MyUtils.toMyDate(clientResult.uDate.toString()))
        profile_setting_phone.setText(clientResult.firstPhone)
        profile_setting_second_phone.setText(clientResult.secondPhone)


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
            if (errorCode == "200") {
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

    override fun onDateSet(view: com.example.spinnerdatepickerlib.DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar: Calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
        profile_setting_data.setText(simpleDateFormat.format(calendar.getTime()))
        yearSelective = year
        monthSelective = monthOfYear
        dayOfMonthSelective = dayOfMonth
    }

    @VisibleForTesting
    fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        SpinnerDatePickerDialogBuilder()
            .context(requireContext())
            .callback(this)
            .onCancel(onCancel)
            .spinnerTheme(spinnerTheme)
            .defaultDate(year, monthOfYear, dayOfMonth)
            .build()
            .show()
    }
}