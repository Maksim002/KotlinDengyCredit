package com.example.kotlincashloan.ui.notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.notification.NotificationAdapter
import com.example.kotlincashloan.adapter.notification.NotificationListener
import com.example.kotlincashloan.extension.animationNoMargaritas
import com.example.kotlincashloan.extension.shimmerStartProfile
import com.example.kotlincashloan.service.model.Notification.ResultListNoticeModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.*

class NotificationFragment : Fragment(), NotificationListener {
    private var myAdapter = NotificationAdapter(this)
    private var viewModel = NotificationViewModel()
    private val map = HashMap<String, String>()
    val handler = Handler()
    private var errorCode = ""
    private var notificationAnim = false
    private var genAnim = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())

        setTitle("Уведомление", resources.getColor(R.color.whiteColor))

        initRefresh()
        initClick()

        shimmer_notification.startShimmerAnimation()
        if (viewModel.listNoticeDta.value != null) {
            viewModel.errorNotice.value = null
            initRecycler()
            myAdapter.numberResult(0)
        } else {
            viewModel.refreshCode = false
            notificationAnim = true
            initRestart()
        }
    }

    private fun initClick() {
        access_restricted.setOnClickListener {
            initVisibilities()
            isRestart()
        }

        no_connection_repeat.setOnClickListener {
            initVisibilities()
            isRestart()
        }

        technical_work.setOnClickListener {
            initVisibilities()
            isRestart()
        }

        not_found.setOnClickListener {
            initVisibilities()
            isRestart()
        }
    }

    private fun initVisibilities() {
        shimmerStartProfile(shimmer_notification, requireActivity())
        notification_access_restricted.visibility = View.GONE
        notification_no_connection.visibility = View.GONE
        notification_technical_work.visibility = View.GONE
        notification_not_found.visibility = View.GONE
        notification_con.visibility = View.VISIBLE
    }

    private fun initRestart() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            notification_no_connection.visibility = View.VISIBLE
            notification_con.visibility = View.GONE
            notification_technical_work.visibility = View.GONE
            notification_access_restricted.visibility = View.GONE
            notification_not_found.visibility = View.GONE
            errorCode = "601"
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            if (viewModel.listNoticeDta.value == null || viewModel.errorNotice.value == null) {
                if (!viewModel.refreshCode) {
                    viewModel.refreshCode = false
                    viewModel.errorNotice.value = null
                    viewModel.listNotice(map)
                    initRecycler()
                } else {
                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
                        viewModel.refreshCode = false
                        viewModel.errorNotice.value = null
                        viewModel.listNotice(map)
                        initRecycler()
                    }, 500)
                }
            } else {
                if (viewModel.errorNotice.value != null) {
                    viewModel.errorNotice.value = null
                    viewModel.listNoticeDta.postValue(null)
                }
                viewModel.listNotice(map)
                initRecycler()
            }
        }
    }

    private fun isRestart() {
        if (viewModel.listNoticeDta.value != null) {
            viewModel.listNoticeDta.value = null
            viewModel.errorNotice.value = null
            viewModel.listNotice(map)
            initRecycler()
        } else if (viewModel.listNoticeDta.value == null) {
            viewModel.listNoticeDta.value = null
            viewModel.errorNotice.value = null
            viewModel.listNotice(map)
            initRecycler()
        }
    }

    private fun initRefresh() {
        notification_swipe.setOnRefreshListener {
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                isRestart()
            }, 500)
            viewModel.refreshCode = true
            myAdapter.numberResult(0)
        }
        notification_swipe.setColorSchemeResources(android.R.color.holo_orange_dark)
    }

    private fun initRecycler() {
        try {
            ObservedInternet().observedInternet(requireContext())
            if (!AppPreferences.observedInternet) {
                notification_no_connection.visibility = View.VISIBLE
                notification_con.visibility = View.GONE
                notification_technical_work.visibility = View.GONE
                notification_access_restricted.visibility = View.GONE
                notification_not_found.visibility = View.GONE
                shimmer_notification.visibility = View.GONE
                errorCode = "601"
            } else {
                viewModel.listNoticeDta.observe(viewLifecycleOwner, Observer { result ->
                    try {
                        if (result.result != null) {
                            myAdapter.update(result.result)
                            notification_recycler.adapter = myAdapter
                            if (genAnim){
                                shimmer_notification.visibility = View.GONE
                            }
                            notification_technical_work.visibility = View.GONE
                            notification_access_restricted.visibility = View.GONE
                            notification_no_connection.visibility = View.GONE
                            notification_not_found.visibility = View.GONE
                            notification_con.visibility = View.VISIBLE
                            errorCode = result.code.toString()
                            if (!genAnim) {
                                //генерирует анимацию перехода
                                animationNoMargaritas(shimmer_notification, handler, requireActivity())
                                genAnim = true
                            }
                            MainActivity.alert.hide()
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            notification_swipe.isRefreshing = false
                            myAdapter.notifyDataSetChanged()
                        } else {
                            if (result.error.code != null) {
                                errorCode = ""
                                if (result.error.code == 500 || result.error.code == 400 || result.error.code == 409 || result.error.code == 429) {
                                    notification_technical_work.visibility = View.VISIBLE
                                    notification_access_restricted.visibility = View.GONE
                                    notification_no_connection.visibility = View.GONE
                                    notification_not_found.visibility = View.GONE
                                    notification_con.visibility = View.GONE
                                    shimmer_notification.visibility = View.GONE
                                } else if (result.error.code == 403) {
                                    notification_access_restricted.visibility = View.VISIBLE
                                    notification_technical_work.visibility = View.GONE
                                    notification_no_connection.visibility = View.GONE
                                    notification_not_found.visibility = View.GONE
                                    notification_con.visibility = View.GONE
                                    shimmer_notification.visibility = View.GONE
                                } else if (result.error.code == 404) {
                                    notification_notification_null.visibility = View.VISIBLE
                                    notification_recycler.visibility = View.GONE
                                    notification_not_found.visibility = View.GONE
                                    notification_access_restricted.visibility = View.GONE
                                    notification_technical_work.visibility = View.GONE
                                    notification_no_connection.visibility = View.GONE
                                    shimmer_notification.visibility = View.GONE
                                } else if (result.error.code == 401) {
                                    initAuthorized()
                                }
                                if (!genAnim) {
                                    //генерирует анимацию перехода
                                    animationNoMargaritas(shimmer_notification, handler, requireActivity())
                                    genAnim = true
                                }
                                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                notification_swipe.isRefreshing = false
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                viewModel.errorNotice.observe(viewLifecycleOwner, Observer { error ->
                    if (error != null) {
                        errorCode = ""
                        if (error == "500" || error == "400" || error == "409" || error == "429" || error == "601") {
                            notification_technical_work.visibility = View.VISIBLE
                            notification_access_restricted.visibility = View.GONE
                            notification_no_connection.visibility = View.GONE
                            notification_not_found.visibility = View.GONE
                            notification_con.visibility = View.GONE
                            shimmer_notification.visibility = View.GONE
                        } else if (error == "403") {
                            notification_access_restricted.visibility = View.VISIBLE
                            notification_technical_work.visibility = View.GONE
                            notification_no_connection.visibility = View.GONE
                            notification_not_found.visibility = View.GONE
                            notification_con.visibility = View.GONE
                            shimmer_notification.visibility = View.GONE
                        } else if (error == "404") {
                            notification_notification_null.visibility = View.VISIBLE
                            notification_recycler.visibility = View.GONE
                            notification_not_found.visibility = View.GONE
                            notification_access_restricted.visibility = View.GONE
                            notification_technical_work.visibility = View.GONE
                            notification_no_connection.visibility = View.GONE
                            shimmer_notification.visibility = View.GONE
                        } else if (error == "401") {
                            initAuthorized()
                        }else if (error == "600"){
                            notification_no_connection.visibility = View.VISIBLE
                            notification_access_restricted.visibility = View.GONE
                            notification_technical_work.visibility = View.GONE
                            notification_not_found.visibility = View.GONE
                            notification_con.visibility = View.GONE
                            shimmer_notification.visibility = View.GONE
                        }
                        if (!genAnim) {
                            //генерирует анимацию перехода
                            animationNoMargaritas(shimmer_notification, handler, requireActivity())
                            genAnim = true
                        }
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        notification_swipe.isRefreshing = false
                    }
                })
//                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun notificationClickListener(position: Int, item: ResultListNoticeModel) {
        val bundle = Bundle()
        bundle.putInt("noticeId", item.id!!)
        bundle.putString("title", item.title)
        notificationAnim = false
        findNavController().navigate(R.id.navigation_detail_notification, bundle)
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

    override fun onStart() {
        super.onStart()
        if (!notificationAnim) {
            //notificationAnim анимация для перехода с адного дествия в другое
            TransitionAnimation(activity as AppCompatActivity).transitionLeft(notification_anim_layout)
            notificationAnim = true
        }
        //меняет цвета навигационной понели
        ColorWindows(activity as AppCompatActivity).noRollback()
    }
}