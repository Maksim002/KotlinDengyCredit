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
import com.example.kotlincashloan.service.model.Notification.ResultListNoticeModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_notification.*
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
    }

    private fun initClick() {
        access_restricted.setOnClickListener {
            isRestart()
        }

        no_connection_repeat.setOnClickListener {
            isRestart()
        }

        technical_work.setOnClickListener {
            isRestart()
        }

        not_found.setOnClickListener {
            isRestart()
        }
    }

    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            notification_no_connection.visibility = View.VISIBLE
            notification_swipe.visibility = View.GONE
            notification_technical_work.visibility = View.GONE
            notification_access_restricted.visibility = View.GONE
            notification_not_found.visibility = View.GONE
            errorCode = "601"
        } else {
            if (viewModel.listNoticeDta.value == null || viewModel.errorNotice.value == null) {
                if (!viewModel.refreshCode) {
                    MainActivity.alert.show()
                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
                        viewModel.refreshCode = false
                        viewModel.errorNotice.value = null
                        viewModel.listNotice(map)
                        initRecycler()
                    }, 500)
                } else {
                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
                        viewModel.refreshCode = false
                        viewModel.errorNotice.value = null
                        viewModel.listNotice(map)
                        initRecycler()
                    }, 500)
                }
            } else {
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    if (viewModel.errorNotice.value != null) {
                        viewModel.errorNotice.value = null
                        viewModel.listNoticeDta.postValue(null)
                    }
                    viewModel.listNotice(map)
                    initRecycler()
                }, 500)
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
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
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
                notification_swipe.visibility = View.GONE
                notification_technical_work.visibility = View.GONE
                notification_access_restricted.visibility = View.GONE
                notification_not_found.visibility = View.GONE
                errorCode = "601"
            } else {
                viewModel.listNoticeDta.observe(viewLifecycleOwner, Observer { result ->
                    try {
                        if (result.result != null) {
                            myAdapter.update(result.result)
                            notification_recycler.adapter = myAdapter
                            myAdapter.notifyDataSetChanged()
                            notification_swipe.visibility = View.VISIBLE
                            notification_technical_work.visibility = View.GONE
                            notification_access_restricted.visibility = View.GONE
                            notification_no_connection.visibility = View.GONE
                            notification_not_found.visibility = View.GONE
                            errorCode = result.code.toString()
                        } else {
                            if (result.error.code != null) {
                                errorCode = ""
                                if (result.error.code == 500 || result.error.code == 400 || result.error.code == 409 || result.error.code == 429) {
                                    notification_technical_work.visibility = View.VISIBLE
                                    notification_access_restricted.visibility = View.GONE
                                    notification_no_connection.visibility = View.GONE
                                    notification_not_found.visibility = View.GONE
                                    notification_swipe.visibility = View.GONE
                                } else if (result.error.code == 403) {
                                    notification_access_restricted.visibility = View.VISIBLE
                                    notification_technical_work.visibility = View.GONE
                                    notification_no_connection.visibility = View.GONE
                                    notification_not_found.visibility = View.GONE
                                    notification_swipe.visibility = View.GONE
                                } else if (result.error.code == 404) {
                                    notification_notification_null.visibility = View.VISIBLE
                                    notification_recycler.visibility = View.GONE
                                    notification_not_found.visibility = View.GONE
                                    notification_access_restricted.visibility = View.GONE
                                    notification_technical_work.visibility = View.GONE
                                    notification_no_connection.visibility = View.GONE
                                } else if (result.error.code == 401) {
                                    initAuthorized()
                                }
                            }
                            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                                MainActivity.alert.hide()
                            }, 200)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    notification_swipe.isRefreshing = false
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                })

                viewModel.errorNotice.observe(viewLifecycleOwner, Observer { error ->
                    if (error != null) {
                        errorCode = ""
                        if (error == "500" || error == "400" || error == "600" || error == "409" || error == "429" || error == "601") {
                            notification_technical_work.visibility = View.VISIBLE
                            notification_access_restricted.visibility = View.GONE
                            notification_no_connection.visibility = View.GONE
                            notification_not_found.visibility = View.GONE
                            notification_swipe.visibility = View.GONE
                        } else if (error == "403") {
                            notification_access_restricted.visibility = View.VISIBLE
                            notification_technical_work.visibility = View.GONE
                            notification_no_connection.visibility = View.GONE
                            notification_not_found.visibility = View.GONE
                            notification_swipe.visibility = View.GONE
                        } else if (error == "404") {
                            notification_notification_null.visibility = View.VISIBLE
                            notification_recycler.visibility = View.GONE
                            notification_not_found.visibility = View.GONE
                            notification_access_restricted.visibility = View.GONE
                            notification_technical_work.visibility = View.GONE
                            notification_no_connection.visibility = View.GONE
                        } else if (error == "401") {
                            initAuthorized()
                        }
//            else if (error == "601") {
//                notification_no_connection.visibility = View.VISIBLE
//                notification_not_found.visibility = View.GONE
//                notification_access_restricted.visibility = View.GONE
//                notification_technical_work.visibility = View.GONE
//                notification_swipe.visibility = View.GONE
//            }
                        handler.postDelayed(Runnable { // Do something after 5s = 500ms
                            MainActivity.alert.hide()
                        }, 200)
                    }
                })
                notification_swipe.isRefreshing = false
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    override fun onResume() {
        super.onResume()
        if (viewModel.listNoticeDta.value != null) {
            viewModel.errorNotice.value = null
            initRecycler()
            myAdapter.numberResult(0)
        } else {
            viewModel.refreshCode = false
            notificationAnim = true
            initRestart()
        }
        if (!notificationAnim) {
            //notificationAnim анимация для перехода с адного дествия в другое
            TransitionAnimation(activity as AppCompatActivity).transitionLeft(
                notification_anim_layout
            )
            notificationAnim = true
        }
        //меняет цвета навигационной понели
        ColorWindows(activity as AppCompatActivity).noRollback()
    }
}