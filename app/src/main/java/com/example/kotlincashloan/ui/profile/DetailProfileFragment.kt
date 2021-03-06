package com.example.kotlincashloan.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.animationGenerator
import com.example.kotlincashloan.extension.shimmerStartProfile
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_detail_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.HashMap
import kotlin.Exception

class DetailProfileFragment : Fragment() {
    private var operationId = 0
    private var titlt = ""
    private var viewModel = ProfileViewModel()
    private val map = HashMap<String, String>()
    val handler = Handler()
    private var errorCode = ""
    private var singleAnimation = false
    private var genAnim = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if (viewModel.listGetOperationDta.value != null) {
            if (errorCode == "200") {
                shimmer_detail_profile.visibility = View.GONE
                initResult()
            } else {
                shimmer_detail_profile.startShimmerAnimation()
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    initRestart()
                }, 500)
            }
        } else {
            shimmer_detail_profile.startShimmerAnimation()
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                initRestart()
            }, 500)
        }
    }

    private fun initClick() {
        access_restricted.setOnClickListener {
            initVisibilities()
            initRestart()
        }

        no_connection_repeat.setOnClickListener {
            initVisibilities()
            initRestart()
        }

        technical_work.setOnClickListener {
            initVisibilities()
            initRestart()
        }

        not_found.setOnClickListener {
            initVisibilities()
            initRestart()
        }
    }

    private fun initVisibilities() {
        shimmerStartProfile(shimmer_detail_profile, requireActivity())
        d_profile_access_restricted.visibility = View.GONE
        d_profile_no_connection.visibility = View.GONE
        d_profile_technical_work.visibility = View.GONE
        d_profile_not_found.visibility = View.GONE
        profile_detail.visibility = View.VISIBLE
    }

    private fun initBundle() {
        operationId = try {
            requireArguments().getInt("operationId")
        } catch (e: Exception) {
            0
        }

        titlt = try {
            requireArguments().getString("title").toString()
        } catch (e: Exception) {
            ""
        }
    }

    private fun initResult() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            d_profile_no_connection.visibility = View.VISIBLE
            d_profile_technical_work.visibility = View.GONE
            d_profile_profile.visibility = View.GONE
            d_profile_access_restricted.visibility = View.GONE
            d_profile_not_found.visibility = View.GONE
            shimmer_detail_profile.visibility = View.GONE
            viewModel.errorGetOperation.value = null
            errorCode = "601"
        } else {
            viewModel.listGetOperationDta.observe(viewLifecycleOwner, Observer { result ->
                if (result.result != null) {
                    d_profile_title.text = result.result.title
                    d_profile_date.text = result.result.date
                    d_profile_description.text = result.result.description
                    d_profile_text.loadMarkdown(result.result.text)
                    errorCode = result.code.toString()
                    d_profile_access_restricted.visibility = View.GONE
                    d_profile_no_connection.visibility = View.GONE
                    d_profile_technical_work.visibility = View.GONE
                    d_profile_not_found.visibility = View.GONE
                    profile_detail.visibility = View.VISIBLE
                    if (!genAnim) {
                        //???????????????????? ???????????????? ????????????????
                        animationGenerator(shimmer_detail_profile, handler, requireActivity())
                        genAnim = true
                    }
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                } else {
                    if (result.error.code != null) {
                        errorCode = result.error.code.toString()
                        if (result.error.code == 400 || result.error.code == 500) {
                            d_profile_technical_work.visibility = View.VISIBLE
                            profile_detail.visibility = View.GONE
                            d_profile_access_restricted.visibility = View.GONE
                            d_profile_no_connection.visibility = View.GONE
                            d_profile_not_found.visibility = View.GONE
                            shimmer_detail_profile.visibility = View.GONE
                        } else if (result.error.code == 403) {
                            d_profile_access_restricted.visibility = View.VISIBLE
                            d_profile_technical_work.visibility = View.GONE
                            profile_detail.visibility = View.GONE
                            d_profile_no_connection.visibility = View.GONE
                            d_profile_not_found.visibility = View.GONE
                            shimmer_detail_profile.visibility = View.GONE
                        } else if (result.error.code == 404) {
                            d_profile_not_found.visibility = View.VISIBLE
                            d_profile_access_restricted.visibility = View.GONE
                            d_profile_technical_work.visibility = View.GONE
                            profile_detail.visibility = View.GONE
                            d_profile_no_connection.visibility = View.GONE
                            shimmer_detail_profile.visibility = View.GONE
                        } else if (result.error.code == 401) {
                            initAuthorized()
                        }
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            })

            viewModel.errorGetOperation.observe(viewLifecycleOwner, Observer { error ->
                if (error != null) {
                    errorCode = error
                    if (error == "400" || error == "500" || error == "601") {
                        d_profile_technical_work.visibility = View.VISIBLE
                        profile_detail.visibility = View.GONE
                        d_profile_access_restricted.visibility = View.GONE
                        d_profile_no_connection.visibility = View.GONE
                        d_profile_not_found.visibility = View.GONE
                        shimmer_detail_profile.visibility = View.GONE
                    } else if (error == "403") {
                        d_profile_access_restricted.visibility = View.VISIBLE
                        d_profile_technical_work.visibility = View.GONE
                        profile_detail.visibility = View.GONE
                        d_profile_no_connection.visibility = View.GONE
                        d_profile_not_found.visibility = View.GONE
                        shimmer_detail_profile.visibility = View.GONE
                    } else if (error == "404") {
                        d_profile_not_found.visibility = View.VISIBLE
                        d_profile_access_restricted.visibility = View.GONE
                        d_profile_technical_work.visibility = View.GONE
                        profile_detail.visibility = View.GONE
                        d_profile_no_connection.visibility = View.GONE
                        shimmer_detail_profile.visibility = View.GONE
                    } else if (error == "401") {
                        initAuthorized()
                    }else if (error == "600"){
                        d_profile_no_connection.visibility = View.VISIBLE
                        d_profile_not_found.visibility = View.GONE
                        d_profile_access_restricted.visibility = View.GONE
                        d_profile_technical_work.visibility = View.GONE
                        profile_detail.visibility = View.GONE
                        shimmer_detail_profile.visibility = View.GONE
                    }
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            })
        }
    }

    private fun initRestart() {
        try {
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
                    viewModel.errorGetOperation.value = null
                    viewModel.getOperation(map)
                    initResult()
                } else {
                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
                        if (viewModel.errorGetOperation.value != null) {
                            viewModel.errorGetOperation.value = null
                            viewModel.listGetOperationDta.postValue(null)
                        }
                        viewModel.getOperation(map)
                        initResult()
                    }, 500)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setTitle(title: String?, color: Int) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color)
        }
    }

    override fun onStart() {
        super.onStart()
         if (!singleAnimation) {
            //detailProfileAnim ???????????????? ?????? ???????????????? ?? ???????????? ?????????????? ?? ????????????
            TransitionAnimation(activity as AppCompatActivity).transitionRight(detail_profile_anim)
            singleAnimation = true
        }
    }

    override fun onResume() {
        super.onResume()
        setTitle(titlt, resources.getColor(R.color.whiteColor))
        //???????????? ?????????? ?????????????????????????? ????????????
        ColorWindows(activity as AppCompatActivity).rollback()
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppPreferences.singleAnimation = false
    }
}