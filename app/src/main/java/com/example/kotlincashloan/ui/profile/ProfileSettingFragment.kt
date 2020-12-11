package com.example.kotlincashloan.ui.profile


import android.app.Activity
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.profile.ClientInfoResultModel
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import java.util.*


class ProfileSettingFragment : Fragment(){
    private var viewModel = ProfileViewModel()
    private val map = HashMap<String, String>()
    private var errorCode = ""
    val handler = Handler()
    var clientResult = ClientInfoResultModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())

        setTitle("Профиль", resources.getColor(R.color.whiteColor))

        initResultView()
    }

    private fun initResultView() {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            requireActivity().getWindow()
                .setStatusBarColor(requireActivity().getColor(R.color.orangeColor))
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