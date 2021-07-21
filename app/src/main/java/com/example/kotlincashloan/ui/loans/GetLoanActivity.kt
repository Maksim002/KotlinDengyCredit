package com.example.kotlincashloan.ui.loans

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.kotlincashloan.R
import com.example.kotlincashloan.extension.hashMapBitmap
import com.example.kotlincashloan.extension.initClear
import com.example.kotlincashloan.extension.listListResult
import com.example.kotlincashloan.service.model.Loans.LoansListModel
import com.example.kotlincashloan.service.model.profile.GetLoanModel
import com.example.kotlincashloan.ui.loans.fragment.*
import com.example.kotlincashloan.ui.profile.ProfileViewModel
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.TimerListenerLoan
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.kotlinviewpager.adapter.PagerAdapters
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_get_loan.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GetLoanActivity : AppCompatActivity(), LoanClearListener {
    private var list = mutableListOf<LoansListModel>()
    val handler = Handler()
    lateinit var get_loan_view_pagers: ViewPager
    lateinit var loanCrossClear: ImageView
    private var listLoan = GetLoanModel()
    var viewModel = ProfileViewModel()
    var states = ArrayList<String>()
    private var statusValue = false
    private var applicationStatus = false
    private var firstStartStatus = false
    private var errorCodeIm = ""
    private var position: Int = 0
    private val getImList: ArrayList<String> = arrayListOf()
    private var permission = 0
    var mitmap = HashMap<String, Bitmap>()

    companion object {
        lateinit var timer: TimerListenerLoan
        lateinit var alert: LoadingAlert
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_loan)

        // TODO: 02.04.21 Если мешает убратиь
        AppPreferences.status = false

        try {
            val valod = intent.extras!!.getBoolean("getBool")
            val firstStart = intent.extras!!.getBoolean("firstStart")
            firstStartStatus = firstStart
            val application = intent.extras!!.getBoolean("application")
            listLoan = intent.extras!!.getSerializable("getLOan") as GetLoanModel
            statusValue = valod
            applicationStatus = application
            if (valod) {
                if (application != false){
                    AppPreferences.refreshWindow = "true"
                }
                AppPreferences.applicationId = listLoan.id
                AppPreferences.status = valod
                AppPreferences.applicationStatus = application
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //меняет цвет статус бара
        ColorWindows(this).statusBarTextColor()
        timer = TimerListenerLoan(this)
        alert = LoadingAlert(this)

        loanCrossClear = findViewById(R.id.loan_cross_clear)
        get_loan_view_pagers = findViewById(R.id.get_loan_view_pagers)

        getPermission()

        if (savedInstanceState == null) {
            initViewPager()
        } // Else, need to wait for onRestoreInstanceState

        initGetLoan()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        initViewPager()
        initClick()
    }

    private fun initClick() {
        access_restricted.setOnClickListener {
            initGetLoan()
            access_restricted.isClickable = false
        }

        no_connection_repeat.setOnClickListener {
            initGetLoan()
            no_connection_repeat.isClickable = false
        }

        technical_work.setOnClickListener {
            initGetLoan()
            technical_work.isClickable = false
        }

        not_found.setOnClickListener {
            initGetLoan()
            not_found.isClickable = false
        }
    }

    override fun loanClearClickListener() {
        this.finish()
    }


    //проверяет какое деалог заработает
    private fun getPermission() {
        if (listLoan.step == "1") {
            permission = 1
        } else if (listLoan.step == "2") {
            permission = 2
        } else if (listLoan.step == "3") {
            permission = 3
        } else if (listLoan.step == "4") {
            permission = 4
        } else if (listLoan.step == "5") {
            permission = 5
        } else if (listLoan.step == "6") {
            permission = 6
        } else if (listLoan.step == "7") {
            permission = 7
        }
    }

    private fun initGetLoan() {
            if (statusValue == true) {
                if (applicationStatus == false) {
                    loan_cross_clear.visibility = View.GONE
                }
//                alert.show()
            }
            try {
                if (listLoan.docs!!.size != 0) {
                    states = listLoan.docs!!
                    statusValue = true

                    if (listLoan.step!!.toInt() >= 6) {
                        //getListsFifth ОЧЕЩАЕТ ВСЕ КАРТИНКИ ПЕРЕД ЗАПОЛНЕНИЕМ
                        hashMapBitmap.clear()

                        if (position <= states.size) {
                            if (errorCodeIm == "200" || errorCodeIm == "404" || errorCodeIm == "") {
                                val mapImg = HashMap<String, String>()
                                mapImg.put("login", AppPreferences.login.toString())
                                mapImg.put("token", AppPreferences.token.toString())
                                mapImg.put("type", "doc")
                                mapImg.put("doc_id", listLoan.id.toString())
                                mapImg.put("type_id", states[position])
                                getImList.add(position.toString())

                                viewModel.getImgLoan(mapImg).observe(this, androidx.lifecycle.Observer { result ->
                                        val msg = result.msg
                                        val data = result.data
                                        when (result.status) {
                                            Status.SUCCESS -> {
                                                if (data!!.result != null) {
                                                    errorCodeIm = data.code.toString()
                                                    if (getImList.size != states.size) {
                                                        convert(data.result.data.toString())
                                                        position++
                                                        initGetLoan()
                                                    } else if (mitmap.size == states.size - 1) {
                                                        convert(data.result.data.toString())
                                                        LoanStepFifthFragment(statusValue, mitmap, listLoan, permission, applicationStatus,this)
                                                        transition()
                                                        alert.hide()
                                                    }
                                                } else {
                                                    if (data.error.code == 404) {
                                                        if (errorCodeIm != "404") {
                                                            Toast.makeText(this, "Фото нет", Toast.LENGTH_LONG).show()
                                                            transition()
                                                            isClickableBottom()
                                                            errorCodeIm = "404"
                                                            alert.hide()
                                                        }
                                                    } else {
                                                        if (errorCodeIm != data.error.code.toString()) {
                                                            listListResult(data.error.code!!.toInt(), this)
                                                            isClickableBottom()
                                                            errorCodeIm = data.error.code.toString()
                                                            alert.hide()
                                                        }
                                                    }
                                                    alert.hide()
                                                }
                                            }
                                            Status.NETWORK, Status.ERROR -> {
                                                if (msg!! == "404") {
                                                    if (errorCodeIm != "404") {
                                                        Toast.makeText(this, "Фото нет", Toast.LENGTH_LONG).show()
                                                        transition()
                                                        isClickableBottom()
                                                        errorCodeIm = "404"
                                                    }
                                                } else {
                                                    if (errorCodeIm != msg) {
                                                        listListResult(msg, this)
                                                        isClickableBottom()
                                                        errorCodeIm = msg
                                                    }
                                                }
                                                alert.hide()
                                            }
                                        }
                                    })
                            }
                        }
                    } else {
                        handler.postDelayed(Runnable { // Do something after 5s = 500ms
                            transition()
                        }, 2500)
                    }
                } else {
                    handler.postDelayed(Runnable { // Do something after 5s = 500ms
                        transition()
                    }, 2500)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    private fun convert(string: String) {
        val imageBytes = Base64.decode(string, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val nh = (decodedImage.height * (512.0 / decodedImage.width)).toInt()
        val scaled = Bitmap.createScaledBitmap(decodedImage, 512, nh, true)
        mitmap.put(states[position], scaled)
    }


    //Блакирует кнопку на время запроса
    private fun isClickableBottom() {
        access_restricted.isClickable = true
        no_connection_repeat.isClickable = true
        technical_work.isClickable = true
        not_found.isClickable = true
    }

    //Срвнивает со степом на какой экран перети
    private fun transition() {
        get_loan_view_pagers.currentItem = listLoan.step!!.toInt()
        alert.hide()
    }

    private fun initViewPager() {
//        list.add(LoansListModel(LoanStepThreeFragment()))

        list.add(LoansListModel(LoanStepOneFragment(firstStartStatus)))
        list.add(LoansListModel(LoanStepTwoFragment(statusValue, listLoan, applicationStatus, this)))
        list.add(LoansListModel(LoanStepThreeFragment()))
        list.add(LoansListModel(LoanStepFourFragment(statusValue, listLoan, permission, applicationStatus, this)))
        list.add(LoansListModel(LoanStepFiveFragment(statusValue, listLoan, permission, applicationStatus, this)))
        list.add(LoansListModel(LoanStepSixFragment(statusValue, listLoan, permission, applicationStatus, this)))
        list.add(LoansListModel(LoanStepFifthFragment(statusValue, mitmap, listLoan, permission, applicationStatus,this)))
        list.add(LoansListModel(LoanStepFaceFragment(statusValue, applicationStatus, this)))
        list.add(LoansListModel(LoanStepPushFragment(statusValue, applicationStatus)))

        get_loan_view_pagers.isEnabled = true

        val adapters = PagerAdapters(supportFragmentManager)

        list.forEachIndexed { index, element ->
            adapters.addFragment(element.fragment, "")
        }

        get_loan_view_pagers.adapter = adapters

        loan_cross_clear.setOnClickListener {
            this.finish()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
        }

        get_loan_stepper_indicator.setViewPager(get_loan_view_pagers);
        // or keep last page as "end page"
        get_loan_stepper_indicator.setViewPager(
            get_loan_view_pagers, get_loan_view_pagers.getAdapter()!!.getCount() - 1
        ); //
        // or manual change
        get_loan_stepper_indicator.setStepCount(8);
        get_loan_stepper_indicator.setCurrentStep(0);
    }

    override fun onPause() {
        super.onPause()
        timer.timeStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppPreferences.isSeekBar = 0
    }

    override fun onStart() {
        super.onStart()
        shimmer_step_loan.startShimmerAnimation()
        //Переключает флаг для работы анимации
        AppPreferences.isRepeat = false
    }

    override fun onResume() {
        super.onResume()
        initClear()
        handler.postDelayed(Runnable { // Do something after 5s = 500ms
            MainActivity.timer.timeStop()
            timer.timeStop()
        }, 1200)
    }
}