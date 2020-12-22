package com.example.kotlincashloan.ui.notification

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
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
import kotlinx.android.synthetic.main.fragment_detail_notification.*
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.lang.Exception
import java.util.HashMap

class DetailNotificationFragment : Fragment() {
    private var notificationId = 0
    private var viewModel = NotificationViewModel()
    private val map = HashMap<String, String>()
    val handler = Handler()
    private var errorCode = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.show()
        return inflater.inflate(R.layout.fragment_detail_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGetBundle()
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        map.put("id", notificationId.toString())
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

    private fun initGetBundle() {
        notificationId = try {
            requireArguments().getInt("noticeId")
        } catch (e: Exception) {
            0
        }
    }


    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            d_notification_no_connection.visibility = View.VISIBLE
            d_notification_not_found.visibility = View.GONE
            d_notification_access_restricted.visibility = View.GONE
            d_notification_technical_work.visibility = View.GONE
            layout_detail.visibility = View.GONE
            viewModel.errorDetailNotice.value = null
            errorCode = "601"
        } else {
            if (viewModel.listNoticeDetailDta.value == null) {
                HomeActivity.alert.show()
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    viewModel.getNotice(map)
                    initRequest()
                }, 500)
            }else{
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    if (viewModel.errorDetailNotice.value != null){
                        viewModel.errorDetailNotice.value = null
                        viewModel.listNoticeDetailDta.postValue(null)
                    }
                    viewModel.getNotice(map)
                    initRequest()
                }, 500)
            }
        }
    }

    private fun initRequest() {
        viewModel.listNoticeDetailDta.observe(viewLifecycleOwner, Observer { result ->
            try {
                if (result.result != null) {
                    detail_notification_title.text = result.result.title
                    detail_notification_data.text = result.result.date
                    detail_notification_description.text = result.result.description
                    detail_notification_text.loadMarkdown(result.result.text)
                    layout_detail.visibility = View.VISIBLE
                    d_notification_access_restricted.visibility = View.GONE
                    d_notification_no_connection.visibility = View.GONE
                    d_notification_technical_work.visibility = View.GONE
                    d_notification_not_found.visibility = View.GONE
                    errorCode = result.code.toString()
                    setTitle(result.result.title, resources.getColor(R.color.whiteColor))
                } else {
                    if (result.error.code != null) {
                        errorCode = result.error.code.toString()
                    }
                    if (result.error.code == 400 || result.error.code == 500) {
                        d_notification_technical_work.visibility = View.VISIBLE
                        d_notification_access_restricted.visibility = View.GONE
                        d_notification_no_connection.visibility = View.GONE
                        d_notification_not_found.visibility = View.GONE
                        layout_detail.visibility = View.GONE
                    } else if (result.error.code == 403) {
                        d_notification_access_restricted.visibility = View.VISIBLE
                        d_notification_technical_work.visibility = View.GONE
                        d_notification_no_connection.visibility = View.GONE
                        d_notification_not_found.visibility = View.GONE
                        layout_detail.visibility = View.GONE
                    } else if (result.error.code == 404) {
                        d_notification_not_found.visibility = View.VISIBLE
                        d_notification_access_restricted.visibility = View.GONE
                        d_notification_technical_work.visibility = View.GONE
                        d_notification_no_connection.visibility = View.GONE
                        layout_detail.visibility = View.GONE
                    } else if (result.error.code == 401) {
                        initAuthorized()
                    }
                }
//            handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                HomeActivity.alert.hide()
//            },450)
            }catch (e: Exception){
                e.printStackTrace()
            }
        })
        viewModel.errorDetailNotice.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                errorCode = error
            }
            if (error == "400" || error == "500" || error == "600") {
                d_notification_technical_work.visibility = View.VISIBLE
                d_notification_access_restricted.visibility = View.GONE
                d_notification_no_connection.visibility = View.GONE
                d_notification_not_found.visibility = View.GONE
                layout_detail.visibility = View.GONE
            } else if (error == "403") {
                d_notification_access_restricted.visibility = View.VISIBLE
                d_notification_technical_work.visibility = View.GONE
                d_notification_no_connection.visibility = View.GONE
                d_notification_not_found.visibility = View.GONE
                layout_detail.visibility = View.GONE
            } else if (error == "404") {
                d_notification_not_found.visibility = View.VISIBLE
                d_notification_access_restricted.visibility = View.GONE
                d_notification_technical_work.visibility = View.GONE
                d_notification_no_connection.visibility = View.GONE
                layout_detail.visibility = View.GONE
            } else if (error == "601") {
                d_notification_no_connection.visibility = View.VISIBLE
                d_notification_not_found.visibility = View.GONE
                d_notification_access_restricted.visibility = View.GONE
                d_notification_technical_work.visibility = View.GONE
                layout_detail.visibility = View.GONE
            } else if (error == "401") {
                initAuthorized()
            }
//            HomeActivity.alert.hide()
        })
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.errorDetailNotice.value == null) {
            if (errorCode == "200") {
                initRequest()
            } else {
                viewModel.refreshCode = false
                initRestart()
            }
        }
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
            toolbar.setBackgroundDrawable(ColorDrawable(requireActivity().getColor(R.color.orangeColor)))
            toolbar.getNavigationIcon()!!.setColorFilter(getResources().getColor(R.color.whiteColor), PorterDuff.Mode.SRC_ATOP)
        }
    }
}