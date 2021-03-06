package com.example.kotlincashloan.ui.loans

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.loans.LoansAdapter
import com.example.kotlincashloan.adapter.loans.LoansListener
import com.example.kotlincashloan.extension.animationNoMargaritas
import com.example.kotlincashloan.extension.listListResult
import com.example.kotlincashloan.extension.shimmerStartProfile
import com.example.kotlincashloan.service.model.Loans.LoanInfoResultModel
import com.example.kotlincashloan.ui.profile.ProfileViewModel
import com.example.kotlincashloan.utils.ColorWindows
import com.example.kotlincashloan.utils.ObservedInternet
import com.example.kotlincashloan.utils.TransitionAnimation
import com.example.kotlinscreenscanner.service.model.CommonResponse
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.fragment_loans.*
import kotlinx.android.synthetic.main.fragment_loans.loans_layout
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import kotlin.Exception

class LoansFragment : Fragment(), LoansListener {
    private var myAdapter = LoansAdapter(this)
    private var viewModel = LoansViewModel()
    private var viewProfileModel = ProfileViewModel()
    val map = HashMap<String, String>()
    val handler = Handler()
    private var listNewsId: String = ""
    private var listLoanId: String = ""
    private var alertValid = false
    private var loansAnim = true
    //???????????????? ???????????????? ????????????????
    private var genAnim = false
    // ???????????????????? ???????????????? ????????????????
    private var genAnimRediscovery = false

    private var progressPositionMax = 0
    private var progressPositionRemains = 0

    private var editActive = ""
    private var editParallel = ""

    private var loanCode = ""

    private var clientStatus = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        requireActivity().onBackPressedDispatcher.addCallback(this) {}
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        initClick()
        initRefresh()

        setTitle("??????????", resources.getColor(R.color.whiteColor))
    }

    fun initCode() {
        listLoanId = viewModel.listLoanId
        listNewsId = viewModel.listNewsId
    }

    private fun initResult() {
        try {
            ObservedInternet().observedInternet(requireContext())
            if (!AppPreferences.observedInternet) {
                loans_no_connection.visibility = View.VISIBLE
                loans_constraint.visibility = View.GONE
                loans_access_restricted.visibility = View.GONE
                loans_not_found.visibility = View.GONE
                loans_technical_work.visibility = View.GONE
                layout_shimmer_frame.visibility = View.GONE
                listNewsId = "601"
                listLoanId = "601"
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } else {
                viewModel.listLoanInfo.observe(viewLifecycleOwner, Observer { result ->
                    try {
                        if (result.error != null) {
                            listLoanId = result.error.code.toString()
                            initErrorResult(result.error.code!!)
                            layout_shimmer_frame.visibility = View.GONE
                            shimmer_rediscovery_loan.stopShimmerAnimation()
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        } else {
                            initCode()
                            if (result.result != null) {
                                if (result.result.clientStatus == 3) {
                                    clientStatus = result.result.clientStatus.toString()
                                    if (loanCode != result.code.toString()) {
                                        val intent = Intent(context, GetLoanActivity::class.java)
                                        intent.putExtra("application", true)
                                        intent.putExtra("firstStart", true)
                                        startActivity(intent)
                                    }else{
                                        isLogicLoan(result)
                                    }
                                    loanCode = result.code.toString()
                                } else {
                                    isLogicLoan(result)
                                }
                            }
                        }
                        loans_layout.isRefreshing = false
                        if (!alertValid) {
                            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                                alertValid = true
                            }, 650)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                viewModel.errorLoanInfo.observe(viewLifecycleOwner, Observer { error ->
                    if (error != null) {
                        initError(error)
                        listLoanId = error
                        layout_shimmer_frame.visibility = View.GONE
                        shimmer_rediscovery_loan.stopShimmerAnimation()
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isLogicLoan(result: CommonResponse<LoanInfoResultModel>){
    // ???????????????? ???????????????????????? ????????, ???????????????????? ?? ???????????? ???????? ???????????? result ???????????????? ?????????????????? ???????????? parallel_loan ?? ?????????? ???????????????? status = true.
        if (result.result.parallelLoan!!.status == false) {
            loan_layout_parallel.visibility = View.GONE
        } else if (result.result.parallelLoan!!.status == true) {
            loan_layout_parallel.visibility = View.VISIBLE
        }
        //???????????? ???????????? ??????????????????. ???????????????????? ???????? ?????????????????? ?????? ?????????? ?????????? status = true
        if (result.result.activeLoan!!.status == false) {
            loan_active_status.visibility = View.GONE
        } else if (result.result.activeLoan!!.status == true) {
            loan_active_status.visibility = View.VISIBLE
        }
        // ???????????? ???????????????? ????????. ???????????????????? ???????? result ???????????????? get_active_loan = true
        if (result.result.getActiveLoan == false) {
            loan_get_active_loan.visibility = View.GONE
        } else if (result.result.getActiveLoan == true) {
            loan_get_active_loan.visibility = View.VISIBLE
        }
        // ???????????? ???????????????? ???????????????????????? ????????. ???????????????????? ???????? result ???????????????? get_parallel_loan = true
        if (result.result.getParallelLoan == false) {
            loan_get_parallel_loan.visibility = View.GONE
        } else if (result.result.getParallelLoan == true) {
            loan_get_parallel_loan.visibility = View.VISIBLE
        }

        // ???????????? ?????????????????????? ???? ?????????????????? ??????????. ???????????????????? ???????? result ???????????????? edit_active_loan = true
        if (result.result.editActiveLoan == 0) {
            editActive = ""
            loan_edit_active_loan.visibility = View.GONE
        } else {
            editActive = result.result.editActiveLoan.toString()
            loan_edit_active_loan.visibility = View.VISIBLE
        }
        // ???????????? ?????????????????????? ???? ?????????????????? ?????????????????????????? ??????????. ???????????????????? ???????? result ???????????????? edit_parallel_loan = true
        if (result.result.editParallelLoan == 0) {
            editParallel = ""
            loan_edit_parallel_loan.visibility = View.GONE
        } else {
            editParallel = result.result.editParallelLoan.toString()
            loan_edit_parallel_loan.visibility = View.VISIBLE
        }
        // ???????????? ???????? process_active_loan = true ???? ???????????????????? ???????????? ???????????? ?? ??????????????????
        if (result.result.processActiveLoan == false) {
            loan_process_active_loan.visibility = View.GONE
        } else if (result.result.processActiveLoan == true) {
            loan_process_active_loan.visibility = View.VISIBLE
        }
        // ???????????? ???????? process_parallel_loan = true ???? ???????????????????? ???????????? ???????????? ?? ??????????????????
        if (result.result.processParallelLoan == false) {
            loan_process_parallel_loan.visibility = View.GONE
        } else if (result.result.processParallelLoan == true) {
            loan_process_parallel_loan.visibility = View.VISIBLE
        }

        // ???????????????? ???????????????????? ??????????????????
        nextRepaymentActive(result)

        verificationArrayActive(result)

        if (result.result.activeLoan!!.paid != null || result.result.activeLoan!!.total != null) {
            progressPositionMax =
                result.result.activeLoan!!.total.toString().toInt()
            progressPositionRemains =
                result.result.activeLoan!!.paid.toString().toInt()
        } else {
            progressPositionMax = 0
            progressPositionRemains = 0
        }

        initLogicSeekBar()

        if (loan_switch.isChecked) {
            checkedTrue(result)
        } else {
            checkedFalse(result)
        }

        loan_switch.setOnClickListener {
            if (!loan_switch.isChecked) {
                checkedFalse(result)
            } else {
                checkedTrue(result)
            }
        }
        initRecycler()
    }

    //???????? ?????????????? true
    private fun checkedTrue(result: CommonResponse<LoanInfoResultModel>) {
        if (result.result.parallelLoan!!.paid != null || result.result.parallelLoan!!.total != null) {
            progressPositionMax = result.result.parallelLoan!!.total.toString().toInt()
            progressPositionRemains = result.result.parallelLoan!!.paid.toString().toInt()
        } else {
            progressPositionMax = 0
            progressPositionRemains = 0
        }

        verificationArrayParallel(result)
        //???????????? ???????????? ??????????????????. ???????????????????? ???????? ?????????????????? ?????? ?????????? ?????????? status = true
        if (result.result.parallelLoan!!.status == false) {
            loan_active_status.visibility = View.GONE
        } else if (result.result.parallelLoan!!.status == true) {
            loan_active_status.visibility = View.VISIBLE
        }
        // ???????????? ???????????????? ????????. ???????????????????? ???????? result ???????????????? get_active_loan = true
        if (result.result.getParallelLoan == false) {
            loan_get_active_loan.visibility = View.GONE
        } else if (result.result.getParallelLoan == true) {
            loan_get_active_loan.visibility = View.VISIBLE
        }

        // ???????????? ?????????????????????? ???? ?????????????????? ?????????????????????????? ??????????. ???????????????????? ???????? result ???????????????? edit_parallel_loan = true
        if (result.result.editParallelLoan == 0) {
            editParallel = ""
            loan_edit_parallel_loan.visibility = View.GONE
        } else {
            editParallel = result.result.editParallelLoan.toString()
            loan_edit_parallel_loan.visibility = View.VISIBLE
        }
        // ???????????? ???????? process_parallel_loan = true ???? ???????????????????? ???????????? ???????????? ?? ??????????????????
        if (result.result.processParallelLoan == false) {
            loan_process_parallel_loan.visibility = View.GONE
        } else if (result.result.processParallelLoan == true) {
            loan_process_parallel_loan.visibility = View.VISIBLE
        }

        // ???????????????? ???????????????????? ??????????????????
        nextRepaymentParallel(result)
        initLogicSeekBar()
    }

    //???????? ?????????????? false
    private fun checkedFalse(result: CommonResponse<LoanInfoResultModel>) {
        if (result.result.activeLoan != null) {
            if (result.result.activeLoan!!.paid != null || result.result.activeLoan!!.total != null) {
                progressPositionMax =
                    result.result.activeLoan!!.total.toString().toInt()
                progressPositionRemains =
                    result.result.activeLoan!!.paid.toString().toInt()
            } else {
                progressPositionMax = 0
                progressPositionRemains = 0
            }
            verificationArrayActive(result)
            //???????????? ???????????? ??????????????????. ???????????????????? ???????? ?????????????????? ?????? ?????????? ?????????? status = true
            if (result.result.activeLoan!!.status == false) {
                loan_active_status.visibility = View.GONE
            } else if (result.result.activeLoan!!.status == true) {
                loan_active_status.visibility = View.VISIBLE
            }
            // ???????????? ???????????????? ????????. ???????????????????? ???????? result ???????????????? get_active_loan = true
            if (result.result.getActiveLoan == false) {
                loan_get_active_loan.visibility = View.GONE
            } else if (result.result.getActiveLoan == true) {
                loan_get_active_loan.visibility = View.VISIBLE
            }

            // ???????????? ?????????????????????? ???? ?????????????????? ??????????. ???????????????????? ???????? result ???????????????? edit_active_loan = true
            if (result.result.editActiveLoan == 0) {
                editActive = ""
                loan_edit_active_loan.visibility = View.GONE
            } else {
                editActive = result.result.editActiveLoan.toString()
                loan_edit_active_loan.visibility = View.VISIBLE
            }
            // ???????????? ???????? process_active_loan = true ???? ???????????????????? ???????????? ???????????? ?? ??????????????????
            if (result.result.processActiveLoan == false) {
                loan_process_active_loan.visibility = View.GONE
            } else if (result.result.processActiveLoan == true) {
                loan_process_active_loan.visibility = View.VISIBLE
            }
        }

        // ???????????????? ???????????????????? ??????????????????
        nextRepaymentActive(result)
        initLogicSeekBar()
    }

    private fun nextRepaymentActive(result: CommonResponse<LoanInfoResultModel>) {
        // ???????????????? ???????????????????? ??????????????????
        if (result.result.activeLoan!!.status == false) {
            loan_text_active.visibility = View.VISIBLE
            text_center.visibility = View.GONE
            loan_currency_icon.visibility = View.GONE
            loan_payment_sum.visibility = View.GONE
            loan_payment_date.visibility = View.GONE
            loan_trait.visibility = View.GONE
        } else if (result.result.activeLoan!!.status == true) {
            loan_text_active.visibility = View.GONE
            text_center.visibility = View.VISIBLE
            loan_currency_icon.visibility = View.VISIBLE
            loan_payment_sum.visibility = View.VISIBLE
            loan_payment_date.visibility = View.VISIBLE
            loan_trait.visibility = View.VISIBLE
        }
    }

    private fun nextRepaymentParallel(result: CommonResponse<LoanInfoResultModel>) {
        // ???????????????? ???????????????????? ??????????????????
        if (result.result.parallelLoan!!.status == false) {
            loan_text_active.visibility = View.VISIBLE
            text_center.visibility = View.GONE
            loan_currency_icon.visibility = View.GONE
            loan_payment_sum.visibility = View.GONE
            loan_payment_date.visibility = View.GONE
            loan_trait.visibility = View.GONE
        } else if (result.result.parallelLoan!!.status == true) {
            loan_text_active.visibility = View.GONE
            text_center.visibility = View.VISIBLE
            loan_currency_icon.visibility = View.VISIBLE
            loan_payment_sum.visibility = View.VISIBLE
            loan_payment_date.visibility = View.VISIBLE
            loan_trait.visibility = View.VISIBLE
        }
    }

    private fun verificationArrayActive(result: CommonResponse<LoanInfoResultModel>) {
        if (result.result.activeLoan!!.balance == null
            || result.result.activeLoan!!.paid == null
            || result.result.activeLoan!!.total == null
            || result.result.activeLoan!!.paymentSum == null
            || result.result.activeLoan!!.paymentDate == null
        ) {
            loans_sum.text = "0"
            loan_paid.text = "0"
            loan_total.text = "0"
            loan_payment_sum.text = "0"
            loan_payment_date.text = "0-0-0"
        } else {
            resActiveLoan(result)
        }
    }

    private fun verificationArrayParallel(result: CommonResponse<LoanInfoResultModel>) {
        if (result.result.parallelLoan!!.balance == null
            || result.result.parallelLoan!!.paid == null
            || result.result.parallelLoan!!.total == null
            || result.result.parallelLoan!!.paymentSum == null
            || result.result.parallelLoan!!.paymentDate == null
        ) {
            loans_sum.text = "0"
            loan_paid.text = "0"
            loan_total.text = "0"
            loan_payment_sum.text = "0"
            loan_payment_date.text = "0-0-0"
        } else {
            resParallelLoan(result)
        }
    }

    private fun resActiveLoan(result: CommonResponse<LoanInfoResultModel>) {
        loans_sum.text = result.result.activeLoan!!.balance.toString()
        loan_paid.text = result.result.activeLoan!!.paid.toString()
        loan_total.text = result.result.activeLoan!!.total.toString()
        loan_payment_sum.text = result.result.activeLoan!!.paymentSum.toString()
        loan_payment_date.text = result.result.activeLoan!!.paymentDate
    }

    private fun resParallelLoan(result: CommonResponse<LoanInfoResultModel>) {
        loans_sum.text = result.result.parallelLoan!!.balance.toString()
        loan_paid.text = result.result.parallelLoan!!.paid.toString()
        loan_total.text = result.result.parallelLoan!!.total.toString()
        loan_payment_sum.text = result.result.parallelLoan!!.paymentSum.toString()
        loan_payment_date.text = result.result.parallelLoan!!.paymentDate
    }

    private fun initClick() {
        loan_get_active_loan.setOnClickListener {
            val intent = Intent(context, GetLoanActivity::class.java)
            if (clientStatus == "3"){
                intent.putExtra("firstStart", true)
            }
            startActivity(intent)
        }

        loan_get_parallel_loan.setOnClickListener {
            val intent = Intent(context, GetLoanActivity::class.java)
            if (clientStatus == "3"){
                intent.putExtra("firstStart", true)
            }
            startActivity(intent)
        }

        no_connection_repeat.setOnClickListener {
            initVisibilities()
            initRepeat()
        }

        access_restricted.setOnClickListener {
            initVisibilities()
            initRepeat()
        }

        not_found.setOnClickListener {
            initVisibilities()
            initRepeat()
        }

        technical_work.setOnClickListener {
            initVisibilities()
            initRepeat()
        }

        loan_edit_active_loan.setOnClickListener {
            continueApplication()
        }

        loan_edit_parallel_loan.setOnClickListener {
            continueApplication()
        }
    }

    private fun initVisibilities() {
        shimmerStartProfile(shimmer_loan, requireActivity())
        loans_access_restricted.visibility = View.GONE
        loans_no_connection.visibility = View.GONE
        loans_technical_work.visibility = View.GONE
        loans_not_found.visibility = View.GONE
        loans_constraint.visibility = View.VISIBLE
    }

    private fun initRepeat() {
        try {
            ObservedInternet().observedInternet(requireContext())
            if (!AppPreferences.observedInternet) {
                loans_no_connection.visibility = View.VISIBLE
                loans_constraint.visibility = View.GONE
                loans_access_restricted.visibility = View.GONE
                loans_not_found.visibility = View.GONE
                loans_technical_work.visibility = View.GONE
                layout_shimmer_frame.visibility = View.GONE
                listNewsId = "601"
                listLoanId = "601"
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } else {
                if (viewModel.listNewsDta.value != null) {
                    viewModel.errorNews.value = null
                    viewModel.listNewsDta.postValue(null)
                    viewModel.listNews(map)
                    initRecycler()
                } else if (viewModel.listNewsDta.value == null) {
                    viewModel.errorNews.value = null
                    viewModel.listNewsDta.postValue(null)
                    viewModel.listNews(map)
                }

                if (viewModel.listLoanInfo.value != null) {
                    viewModel.errorLoanInfo.value = null
                    viewModel.listLoanInfo.postValue(null)
                    viewModel.getLoanInfo(map)
                    initResult()
                } else if (viewModel.listNewsDta.value == null) {
                    viewModel.errorLoanInfo.value = null
                    viewModel.listLoanInfo.postValue(null)
                    viewModel.getLoanInfo(map)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //???????????? ???? ?????????????????? ???????????? ????????????
    private fun continueApplication() {
        val mapLOan = java.util.HashMap<String, String>()
        mapLOan.put("login", AppPreferences.login.toString())
        mapLOan.put("token", AppPreferences.token.toString())

        if (loan_switch.isChecked == true) {
            if (editParallel != "") {
                mapLOan.put("id", editParallel)
            }
        } else {
            if (editActive != "") {
                mapLOan.put("id", editActive)
            }
        }

        viewProfileModel.getApplication(mapLOan).observe(viewLifecycleOwner, Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        val intent = Intent(requireContext(), GetLoanActivity::class.java)
                        intent.putExtra("getLOan", data.result)
                        intent.putExtra("getBool", true)
                        intent.putExtra("application", true)
                        startActivity(intent)
                    } else if (data.error != null) {
                        getErrorCode(data.error.code!!)
                    }
                }
                Status.NETWORK, Status.ERROR -> {
                    getErrorCode(msg!!.toInt())
                }
            }
        })
    }

    private fun getErrorCode(error: Int) {
        listListResult(
            error, loans_technical_work as LinearLayout, loans_no_connection as LinearLayout,
            loans_constraint, loans_access_restricted as LinearLayout,
            loans_not_found as LinearLayout, activity as AppCompatActivity, true
        )
    }

    private fun initRefresh() {
        loans_layout.setOnRefreshListener {
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            viewModel.errorNews.value = null
            viewModel.listNewsDta.postValue(null)
            viewModel.errorLoanInfo.value = null
            viewModel.listLoanInfo.postValue(null)
            handler.postDelayed(Runnable {
                initRepeat()
            }, 700)
        }
        loans_layout.setColorSchemeResources(android.R.color.holo_orange_dark)
    }

    fun setTitle(title: String?, color: Int) {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.setTitle(title, color)
        }
    }

    private fun initRecycler() {
        try {
            ObservedInternet().observedInternet(requireContext())
            if (!AppPreferences.observedInternet) {
                loans_no_connection.visibility = View.VISIBLE
                loans_constraint.visibility = View.GONE
                loans_access_restricted.visibility = View.GONE
                loans_not_found.visibility = View.GONE
                loans_technical_work.visibility = View.GONE
            } else {
                viewModel.listNewsDta.observe(viewLifecycleOwner, Observer { result ->
                    try {
                        if (result.error != null) {
                            if (result.error.code != 404) {
                                listNewsId = result.error.toString()
                                initErrorResult(result.error.code!!)
                            } else {
                                loans_loans_null.visibility = View.VISIBLE
                                loans_recycler.visibility = View.GONE
                            }
                            shimmer_loan.visibility = View.GONE
                            layout_shimmer_frame.visibility = View.GONE
                            shimmer_rediscovery_loan.stopShimmerAnimation()
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        } else {
                            if (result.result != null) {
                                initCode()
                                if (listLoanId == "200" && listNewsId == "200") {
                                    myAdapter.update(result.result)
                                    loans_recycler.adapter = myAdapter
                                    if (genAnim) {
                                        shimmer_loan.visibility = View.GONE
                                    } else {
                                        shimmer_loan.visibility = View.VISIBLE
                                    }

                                    loans_constraint.visibility = View.VISIBLE
                                    loans_no_connection.visibility = View.GONE
                                    loans_loans_null.visibility = View.GONE
                                    loans_recycler.visibility = View.VISIBLE
                                    if (AppPreferences.refreshWindow != "true") {
                                        if (!genAnim) {
                                            //???????????????????? ???????????????? ????????????????
                                            animationNoMargaritas(shimmer_loan, handler, requireActivity())
                                            genAnim = true
                                        }else{
                                            if (!genAnimRediscovery){
                                                shimmer_rediscovery_loan.stopShimmerAnimation()
                                                shimmer_rediscovery_loan.visibility = View.GONE
                                                genAnimRediscovery = true
                                            }
                                        }
                                    }

                                } else {
                                    initResult()
                                }
                                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }
                        }
                        loans_layout.isRefreshing = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                viewModel.errorNews.observe(viewLifecycleOwner, Observer { error ->
                    if (error != null) {
                        if (error.toString() != "404") {
                            initError(error)
                            listNewsId = error
                        } else {
                            loans_loans_null.visibility = View.VISIBLE
                            loans_recycler.visibility = View.GONE
                        }
                        shimmer_loan.visibility = View.GONE
                        layout_shimmer_frame.visibility = View.GONE
                        shimmer_rediscovery_loan.stopShimmerAnimation()
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initAuthorized() {
        val intent = Intent(context, MainActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    private fun initLogicSeekBar() {
        loans_seekBar.max = progressPositionMax
        loans_seekBar.isEnabled = false
        loans_seekBar.progress = progressPositionRemains
    }

    override fun loansClickListener(position: Int, idNews: Int, title: String) {
        val build = Bundle()
        build.putInt("idNews", idNews)
        build.putString("title", title)
        loansAnim = true
        findNavController().navigate(R.id.loans_details_navigation, build)
    }

    private fun initErrorResult(result: Int) {
        if (result == 403) {
            loans_access_restricted.visibility = View.VISIBLE
            loans_not_found.visibility = View.GONE
            loans_technical_work.visibility = View.GONE
            loans_no_connection.visibility = View.GONE
            loans_constraint.visibility = View.GONE
            layout_shimmer_frame.visibility = View.GONE
            shimmer_loan.visibility = View.GONE
        } else if (result == 404) {
            loans_not_found.visibility = View.VISIBLE
            loans_technical_work.visibility = View.GONE
            loans_no_connection.visibility = View.GONE
            loans_constraint.visibility = View.GONE
            loans_access_restricted.visibility = View.GONE
            layout_shimmer_frame.visibility = View.GONE
            shimmer_loan.visibility = View.GONE
        } else if (result == 401) {
            initAuthorized()
        } else if (result == 500 || result == 400 || result == 409 || result == 429) {
            loans_technical_work.visibility = View.VISIBLE
            loans_no_connection.visibility = View.GONE
            loans_constraint.visibility = View.GONE
            loans_access_restricted.visibility = View.GONE
            loans_not_found.visibility = View.GONE
            layout_shimmer_frame.visibility = View.GONE
            shimmer_loan.visibility = View.GONE
        }
        loans_layout.isRefreshing = false
    }

    private fun initError(error: String) {
        if (error == "403") {
            loans_access_restricted.visibility = View.VISIBLE
            loans_not_found.visibility = View.GONE
            loans_technical_work.visibility = View.GONE
            loans_no_connection.visibility = View.GONE
            loans_constraint.visibility = View.GONE
            layout_shimmer_frame.visibility = View.GONE
            shimmer_loan.visibility = View.GONE
        } else if (error == "404") {
            loans_not_found.visibility = View.VISIBLE
            loans_technical_work.visibility = View.GONE
            loans_no_connection.visibility = View.GONE
            loans_constraint.visibility = View.GONE
            loans_access_restricted.visibility = View.GONE
            layout_shimmer_frame.visibility = View.GONE
            shimmer_loan.visibility = View.GONE
        } else if (error == "401") {
            initAuthorized()
        } else if (error == "500" || error == "400" || error == "409" || error == "429" || error == "601") {
            loans_technical_work.visibility = View.VISIBLE
            loans_no_connection.visibility = View.GONE
            loans_constraint.visibility = View.GONE
            loans_access_restricted.visibility = View.GONE
            loans_not_found.visibility = View.GONE
            layout_shimmer_frame.visibility = View.GONE
            shimmer_loan.visibility = View.GONE
        }else if (error == "600"){
            loans_no_connection.visibility = View.VISIBLE
            loans_technical_work.visibility = View.GONE
            loans_constraint.visibility = View.GONE
            loans_access_restricted.visibility = View.GONE
            loans_not_found.visibility = View.GONE
            layout_shimmer_frame.visibility = View.GONE
            shimmer_loan.visibility = View.GONE
        }
        loans_layout.isRefreshing = false
    }

    init {
        loansAnim = false
    }


    override fun onResume() {
        super.onResume()
        // animation ???????????????? ?? ???????????? ?????????????????? ?? ????????????
        if (loansAnim) {
            TransitionAnimation(activity as AppCompatActivity).transitionLeft(loans_layout_anim)
            loansAnim = false
        }
        val handler = Handler()
        if (AppPreferences.refreshWindow == "true") {
            handler.postDelayed(Runnable { // Do something after 5s = 500ms
                viewModel.errorNews.value = null
                viewModel.errorLoanInfo.value = null
                viewModel.listNewsDta.value = null
                viewModel.listLoanInfo.value = null
                viewModel.listNews(map)
                viewModel.getLoanInfo(map)
                initResult()
                AppPreferences.refreshWindow = ""
            }, 800)
        } else if (viewModel.listNewsDta.value == null && viewModel.listLoanInfo.value == null) {
            shimmer_loan.startShimmerAnimation()
            shimmer_loan.visibility = View.VISIBLE
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            viewModel.errorNews.value = null
            viewModel.errorLoanInfo.value = null
            viewModel.listNews(map)
            viewModel.getLoanInfo(map)
            initResult()
        } else {
            if (listNewsId == "200" && listLoanId == "200") {
                initRecycler()
                initResult()
            } else {
                initRepeat()
            }
        }
        //???????????? ?????????? ?????????????????????????? ????????????
        ColorWindows(activity as AppCompatActivity).noRollback()
    }

    var b = false
    override fun onStart() {
        super.onStart()
        if (AppPreferences.refreshWindow == "true") {
            genAnimRediscovery = false
            requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            shimmer_rediscovery_loan.startShimmerAnimation()
            loans_constraint.visibility = View.GONE
            shimmer_rediscovery_loan.visibility = View.VISIBLE
        }
//        if (b == false){
//            handler.postDelayed(Runnable { // Do something after 5s = 500ms
//                AppPreferences.urlApi = "https://crm-api-dev.molbulak2.ru/api/app/"
//                AppPreferences.tokenApi = "/?token=oYyxhIFgJjAb"
//            }, 30000)
//            b = true
//        }
//        handler.postDelayed(Runnable { // Do something after 5s = 500ms
//            AppPreferences.urlApi = "https://crm-api-dev.molbulak.ru/api/app/"
//            AppPreferences.tokenApi = "/?token=oYyxhIFgJjAb"
//        }, 30000)
    }
}