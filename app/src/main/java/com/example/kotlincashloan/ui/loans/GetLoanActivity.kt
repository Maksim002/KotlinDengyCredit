package com.example.kotlincashloan.ui.loans


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.Loans.LoansListModel
import com.example.kotlincashloan.service.model.profile.GetLoanModel
import com.example.kotlincashloan.ui.loans.fragment.*
import com.example.kotlincashloan.ui.profile.ProfileViewModel
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.TimerListenerLoan
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.kotlinviewpager.adapter.PagerAdapters
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_get_loan.*
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
        }catch (e: Exception){
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
    }

    private fun initGetLoan() {
        if (statusValue == true){
            loan_cross_clear.visibility = View.GONE
            alert.show()
        }
        try {
            states = listLoan.docs!!
            statusValue = true
            for (i in 1..states.size) {
                if (i <= states.size) {
                    val mapImg = HashMap<String, String>()
                    mapImg.put("login", AppPreferences.login.toString())
                    mapImg.put("token", AppPreferences.token.toString())
                    mapImg.put("type", "doc")
                    mapImg.put("doc_id", listLoan.id.toString())
                    mapImg.put("type_id", states[i - 1])
                    viewModel.getImg(mapImg)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        viewModel.listGetImgDta.observe(this, androidx.lifecycle.Observer { result->
            if (viewModel.mitmap.size == states.size){
                LoanStepFifthFragment(statusValue, viewModel.mitmap, listLoan)
                get_loan_view_pagers.currentItem = 6
                alert.hide()
            }
        })
    }

    private fun initViewPager() {
//        list.add(LoansListModel(LoanStepFifthFragment()))

        list.add(LoansListModel(LoanStepOneFragment()))
        list.add(LoansListModel(LoanStepTwoFragment()))
        list.add(LoansListModel(LoanStepThreeFragment()))
        list.add(LoansListModel(LoanStepFourFragment()))
        list.add(LoansListModel(LoanStepFiveFragment()))
        list.add(LoansListModel(LoanStepSixFragment()))
        list.add(LoansListModel(LoanStepFifthFragment(statusValue, viewModel.mitmap, listLoan)))
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