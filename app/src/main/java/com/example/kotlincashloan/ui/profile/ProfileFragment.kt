package com.example.kotlincashloan.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.ApplicationListener
import com.example.kotlincashloan.adapter.profile.ProfilePagerAdapter
import com.example.kotlincashloan.extension.bitmapToFile
import com.example.kotlincashloan.extension.listListResult
import com.example.kotlincashloan.extension.sendPicture
import com.example.kotlincashloan.service.model.profile.ResultApplicationModel
import com.example.kotlincashloan.service.model.profile.ResultOperationModel
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.fragment_loan_step_four.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment(), ApplicationListener {
    private var viewModel = ProfileViewModel()
    private val map = HashMap<String, String>()
    private val mapImg = HashMap<String, String>()
    val handler = Handler()
    private var listOperation: ArrayList<ResultOperationModel> = arrayListOf()
    private var listApplication: ArrayList<ResultApplicationModel> = arrayListOf()
    private var errorCode = ""
    private var errorCodeAp = ""
    private var errorCodeClient = ""
    private var errorGetImg = ""
    private var numberBar = 0
    private val bundle = Bundle()
    private var profAnim = false
    private var inputsAnim = 0
    private var errorNull = ""
    private var errorNullAp = ""
    private var pagerPosition = -1

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

        mapImg.put("login", AppPreferences.login.toString())
        mapImg.put("token", AppPreferences.token.toString())
        mapImg.put("type", "profile")
        mapImg.put("doc_id", "0")
        mapImg.put("type_id", "0")

        setTitle("Профиль", resources.getColor(R.color.whiteColor))
        initRefresh()
        initClick()
    }

    private fun initArgument() {
        profAnim = AppPreferences.boleanCode
    }

    private fun initClick() {

        profile_your.setOnClickListener {
            if (sendPicture != "") {
                bundle.putString("sendPicture", sendPicture)
            }
            inputsAnim = 1
            findNavController().navigate(R.id.profile_setting_navigation, bundle)
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

    private fun initRecycler() {

        //Маи операции если все успешно
        viewModel.listListOperationDta.observe(viewLifecycleOwner, Observer { result ->
            try {
                if (result.result != null) {
                    errorNull = ""
                    listOperation = result.result
                    initPager()
                    errorCode = result.code.toString()
                } else if (result.error != null) {
                    if (errorCode != result.error.code.toString()) {
                        if (result.error.code != 404) {
                            if (result.error.code != null) {
                                getErrorCode(result.error.code!!)
                            }
                        } else {
                            resultTrue()
                            errorNull = result.error.code.toString()
                            initPager()
                        }
                    }
                    errorCode = result.error.code.toString()
                }
                if (errorCode == "200" && errorCodeClient == "200" && errorGetImg == "200" && errorCodeAp == "200") {
                    resultSuccessfully()
                }
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                profile_swipe.isRefreshing = false

            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        if (AppPreferences.status == true) {
            viewModel.listApplication(map)
            //Маи заявки если все успешно
            MyApplication()
        } else {
            MyApplication()
        }

        //listListOperationDta Проверка на ошибки
        viewModel.errorListOperation.observe(viewLifecycleOwner, Observer { error ->
            try {
                if (errorCode != error) {
                    if (error != "404") {
                        errorCode = error
                        if (error != null) {
                            getErrorCode(error.toInt())
                        }
                    } else {
                        resultTrue()
                        errorNull = error
                        initPager()
                    }
                }
                errorCode = error
            } catch (e: Exception) {
                e.printStackTrace()
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            profile_swipe.isRefreshing = false
        })


        //если все успешно
        viewModel.listClientInfoDta.observe(viewLifecycleOwner, Observer { result ->
            try {
                if (result.result != null) {
                    profile_fio.setText(result.result.firstName + " " + result.result.lastName)
                    bundle.putSerializable("client", result.result)
                    errorCodeClient = result.code.toString()
                } else {
                    if (result!!.error.code != null) {
                        if (errorCodeClient != result.error.code.toString()) {
                            getErrorCode(result.error.code!!)
                        }
                        errorCodeClient = result.error.code.toString()
                    }
                }
                if (errorCode == "200" && errorCodeClient == "200" && errorGetImg == "200" && errorCodeAp == "200") {
                    resultSuccessfully()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        //listClientInfoDta Проверка на ошибки
        viewModel.errorClientInfo.observe(viewLifecycleOwner, Observer { error ->
            try {
                if (error != null) {
                    if (errorCodeClient != error) {
                        getErrorCode(error.toInt())
                    }
                }
                errorCodeClient = error
            } catch (e: Exception) {
                e.printStackTrace()
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            profile_swipe.isRefreshing = false
        })

    }

    // запрос для выгрузки изоброжение с сервира
    private fun initGetImgDta() {
        // запрос для выгрузки изоброжение с сервира
        viewModel.listGetImgDta.observe(viewLifecycleOwner, Observer { result ->
            try {
                if (result.result != null) {
                    errorGetImg = result.code.toString()
                    val imageBytes = Base64.decode(result.result.data, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    val nh = (decodedImage.height * (512.0 / decodedImage.width)).toInt()
                    val scaled = Bitmap.createScaledBitmap(decodedImage, 512, nh, true)
                    image_profile.setImageBitmap(scaled)
                    bitmapToFile(decodedImage, requireContext())
                } else {
                    if (result.error != null) {
                        if (errorGetImg != result.error.code.toString()) {
                            //если проиходит 404 то провека незаходит в метот для проверки общих ошибок
                            if (result.error.code != 404) {
                                getErrorCode(result.error.code!!)
                                errorGetImg = result.error.code.toString()
                            } else {
                                errorGetImg = "200"
                            }
                        }
                    }
                }
                if (errorCode == "200" && errorCodeClient == "200" && errorGetImg == "200" && errorCodeAp == "200") {
                    resultSuccessfully()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        // запрос для выгрузки изоброжение с сервира если есть ошибка
        viewModel.errorGetImg.observe(viewLifecycleOwner, Observer { error ->
            try {
                if (error != null) {
                    if (errorGetImg != error) {
                        getErrorCode(error.toInt())
                    }
                }
                errorGetImg = error
            } catch (e: Exception) {
                e.printStackTrace()
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        })
    }

    private fun MyApplication() {
        //Маи заявки если все успешно
        viewModel.listListApplicationDta.observe(viewLifecycleOwner, Observer { result ->
            try {
                if (result.result != null) {
                    errorNullAp = ""
                    listApplication = result.result
                    initPager()
                    AppPreferences.status = false
                    errorCodeAp = result.code.toString()
                } else if (result.error != null) {
                    if (errorCodeAp != result.error.code.toString()) {
                        if (result.error.code != 404) {
                            if (result.error.code != null) {
                                getErrorCode(result.error.code!!)
                            }
                        } else {
                            resultTrue()
                            errorNullAp = result.error.code.toString()
                            initPager()
                        }
                    }
                    errorCodeAp = result.error.code.toString()
                }
                if (errorCode == "200" && errorCodeClient == "200" && errorGetImg == "200" && errorCodeAp == "200") {
                    resultSuccessfully()
                }
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                profile_swipe.isRefreshing = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        //Маи заявки если ошибка
        viewModel.errorListApplication.observe(viewLifecycleOwner, Observer { error ->
            try {
                if (errorCodeAp != error) {
                    if (error != "404") {
                        errorCodeAp = error
                        if (error != null) {
                            getErrorCode(error.toInt())
                        }
                    } else {
                        resultTrue()
                        errorNullAp = error
                        initPager()
                    }
                }
                errorCodeAp = error
            } catch (e: Exception) {
                e.printStackTrace()
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            profile_swipe.isRefreshing = false
        })

    }

    // проверка если errorCode и errorCodeClient == 200
    private fun resultSuccessfully() {
        profile_swipe.visibility = View.VISIBLE
        profile_technical_work.visibility = View.GONE
        profile_no_connection.visibility = View.GONE
        profile_access_restricted.visibility = View.GONE
        profile_not_found.visibility = View.GONE
        if (profAnim) {
            //profileAnim анимация для перехода с адного дествия в другое
            TransitionAnimation(activity as AppCompatActivity).transitionLeft(profile_anim)
            inputsAnim = 0
            AppPreferences.inputsAnim = 0
            profAnim = false
            AppPreferences.boleanCode = false
        }
    }

    private fun resultTrue() {
        profile_swipe.visibility = View.VISIBLE
        profile_technical_work.visibility = View.GONE
        profile_no_connection.visibility = View.GONE
        profile_access_restricted.visibility = View.GONE
        profile_not_found.visibility = View.GONE
    }

    private fun getErrorCode(error: Int) {
        listListResult(
            error, profile_technical_work as LinearLayout, profile_no_connection as LinearLayout,
            profile_swipe as SwipeRefreshLayout, profile_access_restricted as LinearLayout,
            profile_not_found as LinearLayout, activity as AppCompatActivity
        )
        MainActivity.alert.hide()
    }

    //Запрос на получение масива заявки
    override fun applicationListener(int: Int, item: ResultApplicationModel) {
        HomeActivity.alert.show()
        val mapLOan = HashMap<String, String>()
        mapLOan.put("login", AppPreferences.login.toString())
        mapLOan.put("token", AppPreferences.token.toString())
        mapLOan.put("id", item.id!!)

        viewModel.getApplication(mapLOan).observe(viewLifecycleOwner, Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        val intent = Intent(requireContext(), GetLoanActivity::class.java)
                        intent.putExtra("getLOan", data.result)
                        intent.putExtra("getBool", true)
                        intent.putExtra("application", false)
                        startActivity(intent)
                    } else if (data.error != null) {
                        getErrorCode(data.error.code!!)
                    }
                }
                Status.NETWORK, Status.ERROR -> {
                    getErrorCode(msg!!.toInt())
                }
            }
            HomeActivity.alert.hide()
        })
    }

    private fun initPager() {
        val adapter = ProfilePagerAdapter(childFragmentManager)
        adapter.addFragment(MyOperationFragment(listOperation, errorNull), "Мои операции")
        adapter.addFragment(MyApplicationFragment(this, listApplication, errorNullAp), "Мои заявки")
        profile_pager.setAdapter(adapter)

        profile_pager.isEnabled = false

        v1.setOnClickListener {
            pagerPosition = 0
            profile_pager.currentItem = 0
        }

        v2.setOnClickListener {
            pagerPosition = 1
            profile_pager.currentItem = 1
        }

        //проверяет если пежер currentItem = 1 то выдемы нопредел)нные поля или на оборот
        if (pagerPosition != -1) {
            if (pagerPosition == 1) {
                profile_bar_one.visibility = View.VISIBLE
                profile_bar_zero.visibility = View.GONE
            } else {
                profile_bar_zero.visibility = View.VISIBLE
                profile_bar_one.visibility = View.GONE
            }
        }

        profile_bar_zero.visibility = View.VISIBLE
        profile_bar_one.visibility = View.GONE

        profile_pager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
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
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                viewModel.refreshCode = true
                initRestart()
                profile_pager.setCurrentItem(0)
                profile_bar_zero.visibility = View.VISIBLE
                profile_bar_one.visibility = View.GONE
                AppPreferences.reviewCode = 0
            }, 500)
        }
        profile_swipe.setColorSchemeResources(android.R.color.holo_orange_dark)
    }

    private fun initRestart() {
        //проверка на интернет
        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            profile_no_connection.visibility = View.VISIBLE
            profile_swipe.visibility = View.GONE
            profile_technical_work.visibility = View.GONE
            profile_access_restricted.visibility = View.GONE
            profile_not_found.visibility = View.GONE
            errorValue()
            clearError()
        } else {
            if (viewModel.listListOperationDta.value == null && viewModel.listClientInfoDta.value == null && viewModel.listGetImgDta.value == null && viewModel.listListApplicationDta.value == null
                && viewModel.errorListOperation.value == null && viewModel.errorClientInfo.value == null && viewModel.errorGetImg.value == null && viewModel.errorListApplication.value == null
            ) {
                if (!viewModel.refreshCode) {
                    MainActivity.alert.show()
                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
                        viewModel.refreshCode = false
                        viewModel.listOperation(map)
                        viewModel.clientInfo(map)
                        viewModel.getImg(mapImg)
                        viewModel.listApplication(map)
                        initRecycler()
                        initGetImgDta()
                    }, 500)
                }
            } else {
                clearError()
                viewModel.listOperation(map)
                viewModel.listApplication(map)
                viewModel.clientInfo(map)
                viewModel.getImg(mapImg)
            }
        }
    }

    //Очещает в запросе данные
    private fun clearError() {
        viewModel.errorListOperation.value = null
        viewModel.errorClientInfo.value = null
        viewModel.errorGetImg.value = null
    }

    fun setTitle(title: String?, color: Int) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color)
        }
    }

    override fun onStop() {
        super.onStop()
        profAnim = false
    }

    override fun onResume() {
        super.onResume()

        initArgument()
        errorValue()
        if (AppPreferences.inputsAnim != 0) {
            inputsAnim = AppPreferences.inputsAnim
        }
        if (viewModel.listListOperationDta.value != null || viewModel.errorListOperation.value != null || viewModel.listClientInfoDta.value != null || viewModel.listListApplicationDta.value != null
            || viewModel.errorClientInfo.value != null || viewModel.listGetImgDta.value != null || viewModel.errorGetImg.value != null || viewModel.errorListApplication.value != null
        ) {
            if (errorCode == "200" || errorCodeClient == "200" || errorGetImg == "200" || errorCodeAp == "200") {
                AppPreferences.reviewCode = 0
                AppPreferences.reviewCodeAp = 0
                if (inputsAnim != 0) {
                    profAnim = true
                }
                viewModel.listGetImgDta.postValue(null)
                viewModel.getImg(mapImg)
                initGetImgDta()
                initRecycler()
            } else {
                AppPreferences.reviewCode = 0
                AppPreferences.reviewCodeAp = 0
//                initRestart()
                viewModel.listGetImgDta.postValue(null)
                viewModel.getImg(mapImg)
                initGetImgDta()
                initRecycler()
            }
        } else {
            AppPreferences.reviewCode = 1
            AppPreferences.reviewCodeAp = 1
            viewModel.refreshCode = false
            initRestart()
        }

        if (numberBar != 0) {
            profile_pager.currentItem = numberBar
            profile_bar_one.visibility = View.VISIBLE
            profile_bar_zero.visibility = View.GONE
        } else {
            profile_bar_zero.visibility = View.VISIBLE
            profile_bar_one.visibility = View.GONE
        }

        //меняет цвета навигационной понели
        ColorWindows(activity as AppCompatActivity).noRollback()
    }

    private fun errorValue() {
        errorCode = ""
        errorCodeClient = ""
        errorCodeAp = ""
        errorGetImg = ""
    }
}