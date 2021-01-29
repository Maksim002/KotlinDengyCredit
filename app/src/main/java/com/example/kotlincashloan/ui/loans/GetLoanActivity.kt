package com.example.kotlincashloan.ui.loans


import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.Loans.LoansListModel
import com.example.kotlincashloan.ui.loans.fragment.LoanStepOneFragment
import com.example.kotlincashloan.ui.loans.fragment.LoanStepTwoFragment
import com.example.kotlincashloan.utils.TimerListenerLoan
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.kotlinviewpager.adapter.PagerAdapters
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.activity_get_loan.*


class GetLoanActivity : AppCompatActivity() {
    private var list = mutableListOf<LoansListModel>()
    val handler = Handler()

    companion object{
        lateinit var timer: TimerListenerLoan
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_loan)
        onBackPressedDispatcher.addCallback(this) {}
        timer = TimerListenerLoan(this)
        if (savedInstanceState == null) {
            initViewPager()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        initViewPager()
    }

    private fun initViewPager() {
        list.add(LoansListModel(LoanStepOneFragment()))
        list.add(LoansListModel(LoanStepTwoFragment()))

        get_loan_view_pagers.isEnabled = true

        val adapters = PagerAdapters(supportFragmentManager)

        list.forEachIndexed { index, element ->
            adapters.addFragment(element.fragment, "")
        }

        get_loan_view_pagers.adapter = adapters

        get_loan_view_click.setOnClickListener {
            get_loan_view_pagers.setCurrentItem(get_loan_view_pagers.currentItem  + 1)
        }

        loan_cross_clear.setOnClickListener {
            if (get_loan_view_pagers.currentItem == 0){
                finish()
            }else{
                get_loan_view_pagers.setCurrentItem(get_loan_view_pagers.currentItem  - 1)
            }
        }

        get_loan_stepper_indicator.setViewPager(get_loan_view_pagers);
        // or keep last page as "end page"
        get_loan_stepper_indicator.setViewPager(get_loan_view_pagers, get_loan_view_pagers.getAdapter()!!.getCount() - 1); //
        // or manual change
        get_loan_stepper_indicator.setStepCount(5);
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