package com.example.kotlincashloan.ui.support

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.support.SupportAdapter
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
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
//    private var refresh = false
    private var errorCode = ""

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
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())

        setTitle("FAQ", resources.getColor(R.color.whiteColor))

        iniClick()
        initRefresh()
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.listFaqDta.value != null){
            if (errorCode == "200"){
                initRecycler()
            }else{
                initRestart()
            }
        }else{
            viewModel.refreshCode = false
            initRestart()
        }
    }

    fun setTitle(title: String?, color: Int) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color)
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
            errorCode = "601"
            viewModel.error.value = null
        } else {
            if (viewModel.listFaqDta.value == null) {
//                HomeActivity.alert.show()
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    viewModel.refreshCode = false
                    viewModel.listFaq(map)
                    initRecycler()
                }, 500)
            } else {
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    viewModel.error.value = null
                    viewModel.listFaq(map)
                    initRecycler()
                }, 500)
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
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                viewModel.refreshCode = true
//                refresh = true
                initRestart()
            }, 500)
        }
        support_swipe_layout.setColorSchemeResources(android.R.color.holo_orange_dark)
    }

    private fun initRecycler() {
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
                errorCode = result.code.toString()
            } else {
                if (result.error.code != null){
                    errorCode = result.error.code.toString()
                }
                if (result.error.code == 403) {
                    layout_access_restricted.visibility = View.VISIBLE
                    support_swipe_layout.visibility = View.GONE
                    support_no_connection.visibility = View.GONE
                    support_technical_work.visibility = View.GONE
                    support_not_found.visibility = View.GONE
                } else if (result.error.code == 500 || result.error.code == 400 || result.error.code == 409 || result.error.code == 429) {
                    support_technical_work.visibility = View.VISIBLE
                    support_swipe_layout.visibility = View.GONE
                    support_no_connection.visibility = View.GONE
                    layout_access_restricted.visibility = View.GONE
                    support_not_found.visibility = View.GONE
                } else if (result.error.code == 404) {
                    support_not_found.visibility = View.VISIBLE
                    support_swipe_layout.visibility = View.GONE
                    support_no_connection.visibility = View.GONE
                    layout_access_restricted.visibility = View.GONE
                    support_technical_work.visibility = View.GONE
                } else if (result.error.code == 401) {
                    initAuthorized()
                }
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            support_swipe_layout.isRefreshing = false
//            handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                HomeActivity.alert.hide()
//            },600)
        })
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (error != null){
                errorCode = error
            }
            if (error == "404") {
                support_not_found.visibility = View.VISIBLE
                support_swipe_layout.visibility = View.GONE
                support_no_connection.visibility = View.GONE
                layout_access_restricted.visibility = View.GONE
                support_technical_work.visibility = View.GONE

            } else if (error == "500" || error == "400" || error == "600" || error == "409" || error == "429") {
                support_technical_work.visibility = View.VISIBLE
                support_swipe_layout.visibility = View.GONE
                support_no_connection.visibility = View.GONE
                layout_access_restricted.visibility = View.GONE
                support_not_found.visibility = View.GONE

            } else if (error == "403") {
                layout_access_restricted.visibility = View.VISIBLE
                support_swipe_layout.visibility = View.GONE
                support_no_connection.visibility = View.GONE
                support_technical_work.visibility = View.GONE
                support_not_found.visibility = View.GONE
            } else if (error == "401") {
                initAuthorized()
            } else if (error == "601") {
                layout_access_restricted.visibility = View.GONE
                support_technical_work.visibility = View.GONE
                support_swipe_layout.visibility = View.GONE
                support_no_connection.visibility = View.VISIBLE
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            support_swipe_layout.isRefreshing = false
//                HomeActivity.alert.hide()
        })
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    fun initVisibilities() {
        profile_recycler.visibility = View.VISIBLE
        support_no_connection.visibility = View.GONE
        layout_access_restricted.visibility = View.GONE
        support_technical_work.visibility = View.GONE
        support_not_found.visibility == View.GONE
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            requireActivity().getWindow().setStatusBarColor(requireActivity().getColor(R.color.orangeColor))
            val decorView: View = (activity as AppCompatActivity).getWindow().getDecorView()
            var systemUiVisibilityFlags = decorView.systemUiVisibility
            systemUiVisibilityFlags = systemUiVisibilityFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            decorView.systemUiVisibility = systemUiVisibilityFlags
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar);
            toolbar.setBackgroundDrawable(ColorDrawable(requireActivity().getColor(R.color.orangeColor)))
        }
    }
}