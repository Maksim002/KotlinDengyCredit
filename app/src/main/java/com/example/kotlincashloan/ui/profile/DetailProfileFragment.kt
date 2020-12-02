package com.example.kotlincashloan.ui.profile

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_detail_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.lang.Exception
import java.util.HashMap

class DetailProfileFragment : Fragment() {
    private var operationId = 0
    private var viewModel = ProfileViewModel()
    private val map = HashMap<String, String>()
    val handler = Handler()
    private var errorCode = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBundle()
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        map.put("id", operationId.toString())
        initClick()
    }

    private fun initClick() {
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
    }

    private fun initBundle() {
        operationId = try {
            requireArguments().getInt("operationId")
        }catch (e: Exception){
            0
        }
    }

    private fun initResult() {
        viewModel.listGetOperationDta.observe(viewLifecycleOwner, Observer { result->
            if (result.result != null){
                d_profile_title.text = result.result.title
                d_profile_date.text = result.result.date
                d_profile_description.text = result.result.description
                d_profile_text.loadMarkdown(result.result.text)
                errorCode = result.code.toString()
                d_profile_profile.visibility = View.VISIBLE
                d_profile_access_restricted.visibility = View.GONE
                d_profile_no_connection.visibility = View.GONE
                d_profile_technical_work.visibility = View.GONE
                d_profile_not_found.visibility = View.GONE
            }else{
                if (result.error.code != null){
                    errorCode = result.error.code.toString()
                }
               if (result.error.code == 400 || result.error.code == 500){
                   d_profile_technical_work.visibility = View.VISIBLE
                   d_profile_profile.visibility = View.GONE
                   d_profile_access_restricted.visibility = View.GONE
                   d_profile_no_connection.visibility = View.GONE
                   d_profile_not_found.visibility = View.GONE
               }else if (result.error.code == 403){
                   d_profile_access_restricted.visibility = View.VISIBLE
                   d_profile_technical_work.visibility = View.GONE
                   d_profile_profile.visibility = View.GONE
                   d_profile_no_connection.visibility = View.GONE
                   d_profile_not_found.visibility = View.GONE
               }else if (result.error.code == 404){
                   d_profile_not_found.visibility = View.VISIBLE
                   d_profile_access_restricted.visibility = View.GONE
                   d_profile_technical_work.visibility = View.GONE
                   d_profile_profile.visibility = View.GONE
                   d_profile_no_connection.visibility = View.GONE
               }else if (result.error.code == 401){
                   initAuthorized()
               }
            }
            HomeActivity.alert.hide()
        })

        viewModel.errorGetOperation.observe(viewLifecycleOwner, Observer { error->
            if (error != null){
                errorCode = error
            }
            if (error == "400" || error == "500" || error == "600"){
                d_profile_technical_work.visibility = View.VISIBLE
                d_profile_profile.visibility = View.GONE
                d_profile_access_restricted.visibility = View.GONE
                d_profile_no_connection.visibility = View.GONE
                d_profile_not_found.visibility = View.GONE
            }else if (error == "403"){
                d_profile_access_restricted.visibility = View.VISIBLE
                d_profile_technical_work.visibility = View.GONE
                d_profile_profile.visibility = View.GONE
                d_profile_no_connection.visibility = View.GONE
                d_profile_not_found.visibility = View.GONE
            }else if (error == "404"){
                d_profile_not_found.visibility = View.VISIBLE
                d_profile_access_restricted.visibility = View.GONE
                d_profile_technical_work.visibility = View.GONE
                d_profile_profile.visibility = View.GONE
                d_profile_no_connection.visibility = View.GONE
            }else if (error == "401"){
                initAuthorized()
            }else if (error == "601"){
                d_profile_no_connection.visibility = View.VISIBLE
                d_profile_technical_work.visibility = View.GONE
                d_profile_profile.visibility = View.GONE
                d_profile_access_restricted.visibility = View.GONE
                d_profile_not_found.visibility = View.GONE
            }
            HomeActivity.alert.hide()
        })
    }

    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            d_profile_no_connection.visibility = View.VISIBLE
            d_profile_technical_work.visibility = View.GONE
            d_profile_profile.visibility = View.GONE
            d_profile_access_restricted.visibility = View.GONE
            d_profile_not_found.visibility = View.GONE
            viewModel.errorGetOperation.value = null
            errorCode = "601"
        } else {
            if (viewModel.listGetOperationDta.value == null) {
                HomeActivity.alert.show()
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    viewModel.getOperation(map)
                    initResult()
                }, 500)
            }
        }
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.listGetOperationDta.value != null) {
            if (errorCode == "200") {
                initResult()
            }else{
                initRestart()
            }
        } else {
            initRestart()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.orangeColor))
            val decorView: View = (activity as AppCompatActivity).getWindow().getDecorView()
            var systemUiVisibilityFlags = decorView.systemUiVisibility
            systemUiVisibilityFlags =
                systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            decorView.systemUiVisibility = systemUiVisibilityFlags
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(ColorDrawable(requireActivity().getColor(R.color.orangeColor)))
            toolbar.setTitleTextColor(requireActivity().getColor(R.color.whiteColor))
            toolbar.getNavigationIcon()!!.setColorFilter(
                getResources().getColor(R.color.whiteColor),
                PorterDuff.Mode.SRC_ATOP
            )
        }
    }
}