package com.example.kotlincashloan.ui.profile



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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.ProfilePagerAdapter
import com.example.kotlincashloan.service.model.profile.ResultOperationModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.*


class ProfileFragment : Fragment() {
    private var indicatorWidth = 0
    private var viewModel = ProfileViewModel()
    private val map = HashMap<String, String>()
    val handler = Handler()
    private var refresh = false
    private var list: ArrayList<ResultOperationModel> = arrayListOf()
    private var errorCode = ""
    private var numberBar = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())

        setTitle("Профиль", resources.getColor(R.color.whiteColor))

        initRefresh()
        initClick()
    }

    private fun initClick() {
        profile_your.setOnClickListener {
            findNavController().navigate(R.id.profile_setting_navigation)
        }

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

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    private fun initRecycler() {
        viewModel.listListOperationDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                list = result.result
                profile_swipe.visibility = View.VISIBLE
                profile_technical_work.visibility = View.GONE
                profile_no_connection.visibility = View.GONE
                profile_access_restricted.visibility = View.GONE
                profile_not_found.visibility = View.GONE
                errorCode = result.code.toString()
                initPager()
            } else {
                if (result.error.code != null) {
                    errorCode = result.error.code.toString()
                } else if (result.code != null) {
                    errorCode = result.code.toString()
                }
                if (result.error.code == 400 || result.error.code == 500 || result.error.code == 409 || result.error.code == 429) {
                    profile_technical_work.visibility = View.VISIBLE
                    profile_no_connection.visibility = View.GONE
                    profile_swipe.visibility = View.GONE
                    profile_access_restricted.visibility = View.GONE
                    profile_not_found.visibility = View.GONE
                } else if (result.error.code == 403) {
                    profile_access_restricted.visibility = View.VISIBLE
                    profile_technical_work.visibility = View.GONE
                    profile_no_connection.visibility = View.GONE
                    profile_swipe.visibility = View.GONE
                    profile_not_found.visibility = View.GONE
                } else if (result.error.code == 404) {
                    profile_not_found.visibility = View.VISIBLE
                    profile_access_restricted.visibility = View.GONE
                    profile_technical_work.visibility = View.GONE
                    profile_no_connection.visibility = View.GONE
                    profile_swipe.visibility = View.GONE
                } else if (result.error.code == 401) {
                    initAuthorized()
                }
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            profile_swipe.isRefreshing = false
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                HomeActivity.alert.hide()
            },170)
        })

        viewModel.errorListOperation.observe(viewLifecycleOwner, Observer { error ->
            try {
                errorCode = error
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (error == "400" || error == "500" || error == "600" || error == "429" || error == "409") {
                profile_technical_work.visibility = View.VISIBLE
                profile_no_connection.visibility = View.GONE
                profile_swipe.visibility = View.GONE
                profile_access_restricted.visibility = View.GONE
                profile_not_found.visibility = View.GONE
            } else if (error == "403") {
                profile_access_restricted.visibility = View.VISIBLE
                profile_technical_work.visibility = View.GONE
                profile_no_connection.visibility = View.GONE
                profile_swipe.visibility = View.GONE
                profile_not_found.visibility = View.GONE
            } else if (error == "404") {
                profile_not_found.visibility = View.VISIBLE
                profile_access_restricted.visibility = View.GONE
                profile_technical_work.visibility = View.GONE
                profile_no_connection.visibility = View.GONE
                profile_swipe.visibility = View.GONE
            } else if (error == "601") {
                profile_no_connection.visibility = View.VISIBLE
                profile_swipe.visibility = View.GONE
                profile_technical_work.visibility = View.GONE
                profile_access_restricted.visibility = View.GONE
                profile_not_found.visibility = View.GONE
            } else if (error == "401") {
                initAuthorized()
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            profile_swipe.isRefreshing = false
            HomeActivity.alert.hide()
        })
    }

    private fun initPager() {
        val adapter = ProfilePagerAdapter(childFragmentManager)
        adapter.addFragment(MyOperationFragment(list), "Мои операции")
        adapter.addFragment(MyApplicationFragment(), "Мои заявки")
        profile_pager.setAdapter(adapter)
        adapter.notifyDataSetChanged()


        profile_pager.isEnabled = false

        v1.setOnClickListener {
            profile_pager.setCurrentItem(0)
        }

        v2.setOnClickListener {
            profile_pager.setCurrentItem(1)
        }

        profile_pager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                numberBar = position
            }
            override fun onPageSelected(position: Int) {
                if (position != 1) {
                    profile_bar_zero.visibility = View.VISIBLE
                    profile_bar_one.visibility = View.GONE
                } else {
                    profile_bar_one.visibility = View.VISIBLE
                    profile_bar_zero.visibility = View.GONE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun initRefresh() {
        profile_swipe.setOnRefreshListener {
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                refresh = true
                initRestart()
                profile_pager.setCurrentItem(0)
                profile_bar_zero.visibility = View.VISIBLE
                profile_bar_one.visibility = View.GONE
            }, 500)
        }
        profile_swipe.setColorSchemeResources(android.R.color.holo_orange_dark)
    }

    private fun initRestart() {
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_no_connection.visibility = View.VISIBLE
            profile_swipe.visibility = View.GONE
            profile_technical_work.visibility = View.GONE
            profile_access_restricted.visibility = View.GONE
            profile_not_found.visibility = View.GONE
            viewModel.errorListOperation.value = null
            errorCode = "601"
        } else {
            if (viewModel.listListOperationDta.value == null) {
                HomeActivity.alert.show()
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    viewModel.listOperation(map)
                    initRecycler()
                }, 500)
            } else {
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    viewModel.errorListOperation.value = null
                    viewModel.listOperation(map)
                    initRecycler()
                }, 400)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.listListOperationDta.value != null) {
            if (errorCode == "200") {
                initRecycler()
            } else {
                initRestart()
            }
        } else {
            initRestart()
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
        if (numberBar != 0){
            profile_pager.currentItem = numberBar
            profile_bar_one.visibility = View.VISIBLE
            profile_bar_zero.visibility = View.GONE
        }else{
            profile_bar_zero.visibility = View.VISIBLE
            profile_bar_one.visibility = View.GONE
        }

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