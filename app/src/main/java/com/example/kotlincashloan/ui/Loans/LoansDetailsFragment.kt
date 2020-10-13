package com.example.kotlincashloan.ui.Loans

import android.content.Intent
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
import com.bumptech.glide.Glide
import com.example.kotlincashloan.R
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.fragment_loans_details.*
import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.lang.Exception
import java.util.*

class LoansDetailsFragment : Fragment() {
    private var viewModel = LoansViewModel()
    private var isNews: Int = 0
    val map = HashMap<String, String>()
    val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loans_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        iniArgument()
        initRequest()
        initClick()
    }

    private fun initClick() {
        no_connection_repeat.setOnClickListener {
            HomeActivity.alert.show()
            initRestart()
            HomeActivity.alert.hide()
        }

        access_restricted.setOnClickListener {
            initRestart()
        }

        not_found.setOnClickListener {
            initRestart()
        }

        technical_work.setOnClickListener {
            initRestart()
        }
    }

    private fun iniArgument() {
        isNews = try {
            requireArguments().getInt("idNews")
        } catch (e: Exception) {
            0
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.listGetDta.value == null){
            handler.postDelayed(Runnable {
            initRequest()
            viewModel.getNews(map)
            }, 800)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.whiteColor))
            requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(ColorDrawable(requireActivity().getColor(R.color.whiteColor)))
            toolbar.setTitleTextColor(requireActivity().getColor(R.color.orangeColor))
        }
    }

    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            loans_detail_no_connection.visibility = View.VISIBLE
            loans_detail_layout.visibility = View.GONE
            loans_detail_access_restricted.visibility = View.GONE
            loans_detail_not_found.visibility = View.GONE
            loans_detail_technical_work.visibility = View.GONE
        } else {
            initRequest()
            if (viewModel.listGetDta.value != null) {
                viewModel.getNews(map)
            } else {
                viewModel.errorGet.value = null
                viewModel.getNews(map)
            }
        }
    }

    private fun initRequest() {
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        map.put("id", isNews.toString())
        HomeActivity.alert.show()
        viewModel.listGetDta.observe(viewLifecycleOwner, Observer { result ->
                if (result.code == 200 && result.result != null){
                    loans_details_name.setText(result.result.name)
                    loans_details_description.setText(result.result.description)
                    loans_details_text.loadMarkdown(result.result.text)
                    Glide
                        .with(loans_details_image)
                        .load(result.result.thumbnail)
                        .into(loans_details_image)

                    loans_detail_layout.visibility = View.VISIBLE
                    loans_detail_no_connection.visibility = View.GONE
                    loans_detail_access_restricted.visibility = View.GONE
                    loans_detail_not_found.visibility = View.GONE
                    loans_detail_technical_work.visibility = View.GONE
                }else if (result.error.code == 403) {
                    loans_detail_no_connection.visibility = View.GONE
                    loans_detail_access_restricted.visibility = View.VISIBLE
                    loans_detail_layout.visibility = View.GONE
                } else if (result.error.code == 404) {
                    loans_detail_no_connection.visibility = View.GONE
                    loans_detail_not_found.visibility = View.VISIBLE
                    loans_detail_layout.visibility = View.GONE
                } else if (result.error.code == 401) {
                    initAuthorized()
                } else if (result.error.code == 500 || result.error.code == 400) {
                    loans_detail_no_connection.visibility = View.GONE
                    loans_detail_technical_work.visibility = View.VISIBLE
                    loans_detail_layout.visibility = View.GONE
                }
            HomeActivity.alert.hide()
        })

        viewModel.errorGet.observe(viewLifecycleOwner, Observer { error ->
            if (error == "403") {
                loans_detail_no_connection.visibility = View.GONE
                loans_detail_access_restricted.visibility = View.VISIBLE
                loans_detail_layout.visibility = View.GONE
            } else if (error == "404") {
                loans_detail_no_connection.visibility = View.GONE
                loans_detail_not_found.visibility = View.VISIBLE
                loans_detail_layout.visibility = View.GONE
            } else if (error == "401") {
                initAuthorized()
            } else if (error == "500" || error == "400") {
                loans_detail_no_connection.visibility = View.GONE
                loans_detail_technical_work.visibility = View.VISIBLE
                loans_detail_layout.visibility = View.GONE
            } else if (error == "600") {
                loans_detail_no_connection.visibility = View.GONE
                loans_detail_technical_work.visibility = View.VISIBLE
                loans_detail_layout.visibility = View.GONE
            }else if(error == "601"){
                loans_detail_layout.visibility = View.GONE
                loans_detail_access_restricted.visibility = View.GONE
                loans_detail_no_connection.visibility = View.VISIBLE
                loans_detail_not_found.visibility = View.GONE
                loans_detail_technical_work.visibility = View.GONE
            }
            HomeActivity.alert.hide()
        })
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
    }
}