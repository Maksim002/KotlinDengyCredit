package com.example.kotlincashloan.ui.loans

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.kotlincashloan.R
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
import kotlinx.android.synthetic.main.activity_number.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GetLoanActivity : AppCompatActivity() {
    private var list = mutableListOf<LoansListModel>()
    val handler = Handler()
    lateinit var get_loan_view_pagers: ViewPager
    private var listLoan = GetLoanModel()
    var viewModel = ProfileViewModel()
    var states = ArrayList<String>()
    private var statusValue = false
    private var errorCodeIm = ""
    private lateinit var thread: Thread
    private var o: Int = 0
    private val getImList: ArrayList<String> = arrayListOf()

    companion object {
        lateinit var timer: TimerListenerLoan
        lateinit var alert: LoadingAlert
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_loan)
        //меняет цвет статус бара
        ColorWindows(this).statusBarTextColor()
        timer = TimerListenerLoan(this)
        alert = LoadingAlert(this)

        get_loan_view_pagers = findViewById(R.id.get_loan_view_pagers)

        try {
            val valod = intent.extras!!.getBoolean("getBool")
            listLoan = intent.extras!!.getSerializable("getLOan") as GetLoanModel
            statusValue = valod
        } catch (e: Exception) {
            e.printStackTrace()
        }

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

    private fun initGetLoan() {
        if (statusValue == true) {
            loan_cross_clear.visibility = View.GONE
            alert.show()
        }
        try {
            states = listLoan.docs!!
            statusValue = true

            if (o <= states.size) {
                if (errorCodeIm == "200" || errorCodeIm == "404" || errorCodeIm == "") {
                    val mapImg = HashMap<String, String>()
                    mapImg.put("login", AppPreferences.login.toString())
                    mapImg.put("token", AppPreferences.token.toString())
                    mapImg.put("type", "doc")
                    mapImg.put("doc_id", listLoan.id.toString())
                    mapImg.put("type_id", states[o])
                    getImList.add(o.toString())

                    viewModel.getImgLoan(mapImg).observe(this, androidx.lifecycle.Observer { result ->
                            val msg = result.msg
                            val data = result.data
                            when (result.status) {
                                Status.SUCCESS -> {
                                    if (data!!.result != null) {
                                        errorCodeIm = data.code.toString()
                                        if (getImList.size != states.size) {
                                            o++
                                            initGetLoan()
                                        }else if (viewModel.repository.mitmap.size == states.size-1) {
                                            LoanStepFifthFragment(statusValue, viewModel.repository.mitmap, listLoan)
                                            transition()
                                            alert.hide()
                                        }
                                    }else{
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
                                Status.NETWORK, Status.ERROR ->{
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        if (listLoan.step!!.toInt() == 2) {
            get_loan_view_pagers.currentItem = 3
        }

//        if (listLoan.step!!.toInt() == 5) {
//            get_loan_view_pagers.currentItem = 6
//        } else if (listLoan.step!!.toInt() == 4) {
//            get_loan_view_pagers.currentItem = 5
//        } else if (listLoan.step!!.toInt() == 3) {
//            get_loan_view_pagers.currentItem = 4
//        } else if (listLoan.step!!.toInt() == 2) {
//            get_loan_view_pagers.currentItem = 3
//        } else if (listLoan.step!!.toInt() == 1) {
//            get_loan_view_pagers.currentItem = 2
//        } else if (listLoan.step!!.toInt() == 0) {
//            get_loan_view_pagers.currentItem = 1
//        }
        alert.hide()
    }

    private fun initViewPager() {
//        list.add(LoansListModel(LoanStepFifthFragment()))

        list.add(LoansListModel(LoanStepOneFragment()))
        list.add(LoansListModel(LoanStepTwoFragment(statusValue)))
        list.add(LoansListModel(LoanStepThreeFragment()))
        list.add(LoansListModel(LoanStepFourFragment(statusValue, listLoan)))
        list.add(LoansListModel(LoanStepFiveFragment()))
        list.add(LoansListModel(LoanStepSixFragment()))
        list.add(LoansListModel(LoanStepFifthFragment(statusValue, viewModel.repository.mitmap, listLoan)))
        list.add(LoansListModel(LoanStepPushFragment()))

        get_loan_view_pagers.isEnabled = true

        val adapters = PagerAdapters(supportFragmentManager)

        list.forEachIndexed { index, element ->
            adapters.addFragment(element.fragment, "")
        }

        get_loan_view_pagers.adapter = adapters

//        get_loan_view_click.setOnClickListener {
//            get_loan_view_pagers.setCurrentItem(get_loan_view_pagers.currentItem + 1)
//        }

        loan_cross_clear.setOnClickListener {
            this.onBackPressed()
        }

        get_loan_stepper_indicator.setViewPager(get_loan_view_pagers);
        // or keep last page as "end page"
        get_loan_stepper_indicator.setViewPager(
            get_loan_view_pagers, get_loan_view_pagers.getAdapter()!!.getCount() - 1
        ); //
        // or manual change
        get_loan_stepper_indicator.setStepCount(7);
        get_loan_stepper_indicator.setCurrentStep(0);
    }

    override fun onPause() {
        super.onPause()
//        MainActivity.timer.timeStart()
        timer.timeStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppPreferences.isSeekBar = 0
    }


    override fun onResume() {
        super.onResume()
        handler.postDelayed(Runnable { // Do something after 5s = 500ms
            MainActivity.timer.timeStop()
            timer.timeStop()
        }, 1200)
    }
}