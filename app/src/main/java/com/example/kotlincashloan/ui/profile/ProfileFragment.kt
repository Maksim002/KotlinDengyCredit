package com.example.kotlincashloan.ui.profile

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bumptech.glide.Glide
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.ProfilePagerAdapter
import com.example.kotlincashloan.extension.bitmapToFile
import com.example.kotlincashloan.extension.sendPicture
import com.example.kotlincashloan.service.model.profile.ClientInfoResultModel
import com.example.kotlincashloan.service.model.profile.ResultOperationModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile_setting.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {
    private var viewModel = ProfileViewModel()
    private val map = HashMap<String, String>()
    private val mapImg = HashMap<String, String>()
    val handler = Handler()
    private var list: ArrayList<ResultOperationModel> = arrayListOf()
    private var errorCode = ""
    private var errorCodeClient = ""
    private var errorGetImg = ""
    private var numberBar = 0
    private val bundle = Bundle()
    private var profAnim = false
    private var inputsAnim = 0

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
            if (sendPicture != ""){
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

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    private fun initRecycler() {
        //listListOperationDta Проверка на ошибки
        viewModel.errorListOperation.observe(viewLifecycleOwner, Observer { error ->
            try {
                errorCode = error
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (error != null) {
                errorList(error)
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        })

        //listClientInfoDta Проверка на ошибки
        viewModel.errorClientInfo.observe(viewLifecycleOwner, Observer { error ->
            try {
                errorCodeClient = error
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (error != null) {
                errorList(error)
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            profile_swipe.isRefreshing = false
        })

        //если все успешно
        viewModel.listListOperationDta.observe(viewLifecycleOwner, Observer { result ->
            try {
                if (result.result != null) {
                    list = result.result
                    initPager()
                    errorCode = result.code.toString()

                } else {
                    if (result.error.code != null) {
                        errorCode = result.error.code.toString()
                    } else if (result.code != null) {
                        errorCode = result.code.toString()
                    }
                    listListResult(result.error.code!!)
                }
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                profile_swipe.isRefreshing = false
                if (errorCode == "200" && errorCodeClient == "200" && errorGetImg == "200") {
                    resultSuccessfully()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
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
                        errorCodeClient = result.error.code.toString()
                    } else if (result.code != null) {
                        errorCodeClient = result.code.toString()
                    }
                    listListResult(result.error.code!!)
                }
                if (errorCode == "200" && errorCodeClient == "200" && errorGetImg == "200") {
                    resultSuccessfully()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        })
//        // запрос для выгрузки изоброжение с сервира
//        initGetImgDta()
    }
    // запрос для выгрузки изоброжение с сервира
    private fun initGetImgDta(){
        // запрос для выгрузки изоброжение с сервира
        viewModel.listGetImgDta.observe(viewLifecycleOwner, Observer { result ->
            try {
                if (result.result != null){
                    errorGetImg = result.code.toString()
                    var imageBytes = Base64.decode(result.result.data, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    val nh = (decodedImage.height * (512.0 / decodedImage.width)).toInt()
                    val scaled = Bitmap.createScaledBitmap(decodedImage, 512, nh, true)
                    image_profile.setImageBitmap(scaled)
                    bitmapToFile(decodedImage, requireContext())
                }else{
                    //если проиходит 404 то провека незаходит в метот для проверки общих ошибок
                    if (result.error.code != 404){
                        listListResult(result.error.code!!)
                    }else{
                        errorGetImg = "200"
                    }
                }
                if (errorCode == "200" && errorCodeClient == "200" && errorGetImg == "200") {
                    resultSuccessfully()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        })

        // запрос для выгрузки изоброжение с сервира если есть ошибка
        viewModel.errorGetImg.observe(viewLifecycleOwner, Observer { error ->
            try {
                errorGetImg = error
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (error != null) {
                errorList(error)
            }
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    private fun listListResult(result: Int) {
        if (result == 400 || result == 500 || result == 409 || result == 429) {
            profile_technical_work.visibility = View.VISIBLE
            profile_no_connection.visibility = View.GONE
            profile_swipe.visibility = View.GONE
            profile_access_restricted.visibility = View.GONE
            profile_not_found.visibility = View.GONE
        } else if (result == 403) {
            profile_access_restricted.visibility = View.VISIBLE
            profile_technical_work.visibility = View.GONE
            profile_no_connection.visibility = View.GONE
            profile_swipe.visibility = View.GONE
            profile_not_found.visibility = View.GONE
        } else if (result == 404) {
            profile_not_found.visibility = View.VISIBLE
            profile_access_restricted.visibility = View.GONE
            profile_technical_work.visibility = View.GONE
            profile_no_connection.visibility = View.GONE
            profile_swipe.visibility = View.GONE
        } else if (result == 401) {
            initAuthorized()
        }
        MainActivity.alert.hide()
    }

    private fun errorList(error: String) {
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
        MainActivity.alert.hide()
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
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
            viewModel.errorListOperation.value = null
            viewModel.errorClientInfo.value = null
            viewModel.errorGetImg.value = null
            errorCode = "601"
            errorCodeClient = "601"
            errorGetImg = "601"
        } else {
            if (viewModel.listListOperationDta.value == null && viewModel.listClientInfoDta.value == null && viewModel.listGetImgDta.value == null) {
                if (!viewModel.refreshCode) {
                    MainActivity.alert.show()
                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
                        viewModel.refreshCode = false
                        viewModel.listOperation(map)
                        viewModel.clientInfo(map)
                        viewModel.getImg(mapImg)
                        initRecycler()
                        initGetImgDta()
                    }, 500)
                }
            } else {
                handler.postDelayed(Runnable { // Do something after 5s = 500ms
                    if (viewModel.errorListOperation.value != null) {
                        viewModel.listListOperationDta.postValue(null)
                        viewModel.errorListOperation.value = null
                        viewModel.listOperation(map)
                    } else if (viewModel.errorClientInfo.value != null) {
                        viewModel.listClientInfoDta.postValue(null)
                        viewModel.errorClientInfo.value = null
                        viewModel.clientInfo(map)
                    }else if (viewModel.errorGetImg.value != null){
                        viewModel.listGetImgDta.postValue(null)
                        viewModel.errorGetImg.value = null
                        viewModel.getImg(mapImg)
                    }
                    viewModel.listOperation(map)
                    viewModel.clientInfo(map)
                    viewModel.getImg(mapImg)
                    initRecycler()
                    initGetImgDta()
                }, 500)
            }
        }
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

        if (AppPreferences.inputsAnim != 0){
            inputsAnim = AppPreferences.inputsAnim
        }
        if (viewModel.listListOperationDta.value != null || viewModel.listClientInfoDta.value != null || viewModel.listGetImgDta.value != null) {
            if (errorCode == "200" || errorCodeClient == "200" || errorGetImg == "200") {
                AppPreferences.reviewCode = 0
                if (inputsAnim != 0){
                    profAnim = true
                }
                viewModel.listGetImgDta.postValue(null)
                viewModel.getImg(mapImg)
                initGetImgDta()
                initRecycler()
            } else {
                AppPreferences.reviewCode = 1
                initRestart()
            }
        } else {
            AppPreferences.reviewCode = 1
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
}