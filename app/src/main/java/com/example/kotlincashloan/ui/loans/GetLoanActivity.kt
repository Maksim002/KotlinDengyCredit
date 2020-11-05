package com.example.kotlincashloan.ui.loans


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.Loans.LoansListModel
import com.example.kotlincashloan.ui.loans.fragment.GetLoanFragment
import com.example.kotlinscreenscanner.ui.MainActivity
import com.example.kotlinviewpager.adapter.PagerAdapters
import kotlinx.android.synthetic.main.activity_get_loan.*

class GetLoanActivity : AppCompatActivity() {
    private var list = mutableListOf<LoansListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_loan)
        initViewPager()
    }

    private fun initViewPager() {
        list.add(LoansListModel(GetLoanFragment()))
        list.add(LoansListModel(GetLoanFragment()))
        list.add(LoansListModel(GetLoanFragment()))

        val adapters = PagerAdapters(supportFragmentManager)

        list.forEachIndexed { index, element ->
            adapters.addFragment(element.fragment, "")
        }

        get_loan_view_pagers.adapter = adapters

        get_loan_stepper_indicator.setViewPager(get_loan_view_pagers);
        // or keep last page as "end page"
        get_loan_stepper_indicator.setViewPager(get_loan_view_pagers, get_loan_view_pagers.getAdapter()!!.getCount() - 1); //
        // or manual change
        get_loan_stepper_indicator.setStepCount(5);
        get_loan_stepper_indicator.setCurrentStep(0);
    }

    override fun onResume() {
        super.onResume()
        MainActivity.timer.timeStop()
    }
}