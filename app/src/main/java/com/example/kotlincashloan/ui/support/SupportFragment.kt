package com.example.kotlincashloan.ui.support

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.support.SupportAdapter
import com.example.kotlincashloan.extension.banPressed
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*


class SupportFragment : Fragment() {
    private var myAdapter = SupportAdapter()
    private var viewModel = SupportViewModel()
    private val map = HashMap<String, String>()
    val handler = Handler()
    private var refresh = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
        initRecycler()
        iniClick()
        initRefresh()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.listFaqDta.value == null) {
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                viewModel.listFaq(map)
                initRecycler()
            }, 500)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().getWindow()
                .setStatusBarColor(requireActivity().getColor(R.color.whiteColor))
            requireActivity().getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(ColorDrawable(requireActivity().getColor(R.color.whiteColor)))
            toolbar.setTitleTextColor(requireActivity().getColor(R.color.orangeColor))
        }
    }

    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            support_no_connection.visibility = View.VISIBLE
            support_swipe_layout.visibility = View.GONE
            support_not_found.visibility = View.GONE
            support_technical_work.visibility = View.GONE
            layout_access_restricted.visibility = View.GONE
        } else {
            initRecycler()
            if (viewModel.listFaqDta.value != null) {
                viewModel.listFaq(map)
            } else {
                viewModel.error.value = null
                viewModel.listFaq(map)
            }
        }
    }

    private fun iniClick() {
        no_connection_repeat.setOnClickListener {
            initRestart()
        }

        access_restricted.setOnClickListener {
            initRestart()
        }

        technical_work.setOnClickListener {
            initRestart()
        }

        not_found.setOnClickListener {
            initRestart()
        }
    }

    private fun initRefresh() {
        support_swipe_layout.setOnRefreshListener {
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                refresh = true
                initRestart()
                support_swipe_layout.isRefreshing = false
            }, 1000)
        }
        support_swipe_layout.setColorSchemeResources(android.R.color.holo_orange_dark)
    }

    private fun initRecycler() {
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        if (!refresh) {
            HomeActivity.alert.show()
        }
        viewModel.listFaqDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                myAdapter.update(result.result)
                profile_recycler.adapter = myAdapter
                initVisibilities()
                support_swipe_layout.visibility = View.VISIBLE
                support_no_connection.visibility = View.GONE
                support_not_found.visibility = View.GONE
                support_technical_work.visibility = View.GONE
                layout_access_restricted.visibility = View.GONE
            } else if (result.error.code == 403) {
                layout_access_restricted.visibility = View.VISIBLE
                support_swipe_layout.visibility = View.GONE
            } else if (result.error.code == 500 || result.error.code == 400) {
                support_technical_work.visibility = View.VISIBLE
                support_swipe_layout.visibility = View.GONE
            } else if (result.error.code == 404) {
                support_not_found.visibility = View.VISIBLE
                support_swipe_layout.visibility = View.GONE
            } else if (result.error.code == 401) {
                initAuthorized()
            }
            HomeActivity.alert.hide()
        })
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (error == "404") {
                support_not_found.visibility = View.VISIBLE
                support_swipe_layout.visibility = View.GONE
                support_no_connection.visibility = View.GONE

            } else if (error == "500" || error == "400" || error == "600") {
                support_technical_work.visibility = View.VISIBLE
                support_swipe_layout.visibility = View.GONE
                support_no_connection.visibility = View.GONE

            } else if (error == "403") {
                layout_access_restricted.visibility = View.VISIBLE
                support_swipe_layout.visibility = View.GONE
                support_no_connection.visibility = View.GONE
            } else if (error == "401") {
                initAuthorized()
            }
            HomeActivity.alert.hide()
        })
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
    }

    fun initVisibilities() {
        profile_recycler.visibility = View.VISIBLE
        support_no_connection.visibility = View.GONE
        layout_access_restricted.visibility = View.GONE
        support_technical_work.visibility = View.GONE
        support_not_found.visibility == View.GONE
    }
}