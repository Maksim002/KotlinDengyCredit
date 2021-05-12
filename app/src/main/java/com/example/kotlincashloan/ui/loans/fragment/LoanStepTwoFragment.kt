package com.example.kotlincashloan.ui.loans.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.general.ListenerGeneralResult
import com.example.kotlincashloan.adapter.loans.LoansStepAdapter
import com.example.kotlincashloan.adapter.loans.StepClickListener
import com.example.kotlincashloan.extension.listListResult
import com.example.kotlincashloan.service.model.Loans.LoansStepTwoModel
import com.example.kotlincashloan.service.model.profile.GetLoanModel
import com.example.kotlincashloan.ui.loans.GetLoanActivity
import com.example.kotlincashloan.ui.loans.LoansViewModel
import com.example.kotlincashloan.ui.loans.fragment.dialogue.StepBottomFragment
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlincashloan.utils.ObservedInternet
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.activity_get_loan.*
import kotlinx.android.synthetic.main.fragment_loan_step_four.*
import kotlinx.android.synthetic.main.fragment_loan_step_two.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.lang.Math.pow
import kotlin.math.round


class LoanStepTwoFragment(var status: Boolean, var applicationStatus: Boolean) : Fragment(), StepClickListener {
    private var myAdapter = LoansStepAdapter()
    private var viewModel = LoansViewModel()
    val map = HashMap<String, String>()

    private var maxCounter = 0
    private var minCounter = 0

    private var sumMax = 0
    private var sumMin = 0

    private var monthMax = 0

    private var position = 0

    private var totalSum = 0
    private var totalCounter = 0
    private var totalRate = 0.0

    val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_step_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        initRestart()
    }

    private fun initClick() {

        no_connection_repeat.setOnClickListener {
            initRestart()
        }

        access_restricted.setOnClickListener {
            initRestart()
        }

        not_found.setOnClickListener {
            initRestart()
        }

        technical_work.setOnClickListener {
            initRestart()
        }

        bottom_step_two.setOnClickListener {
            initSaveLoan()
        }
    }

    override fun onResume() {
        super.onResume()
        if (applicationStatus == false) {
            if (status == true) {
                bottom_step_two.setText("Сохранить")
            }else{
                bottom_step_two.text = "Следующий шаг"
            }
        }else{
            bottom_step_two.text = "Следующий шаг"
        }
    }

    private fun initСounter() {
        //Если запрос счётчика прошол успешно
        viewModel.getLoanInfoDta.observe(viewLifecycleOwner, Observer { result->
            if (result.result != null) {
                sumMin = result.result.minSum.toString().toDouble().toInt()
                sumMax = result.result.maxSum.toString().toDouble().toInt()
                maxCounter = result.result.maxCount.toString().toInt()
                minCounter = result.result.minCount.toString().toInt()

                totalRate = result.result.rate!!.toDouble()

                position = minCounter
                progressBarr(sumMin.toString())

                totalCounter = result.result.minCount.toString().toInt()
                totalSum = result.result.minSum!!.toDouble().toInt()

                minCounterLoan.setText(minCounter.toString())
                maxCounterLoan.setText(maxCounter.toString())

                monthMax = result.result.maxCount!!.toInt() - 1

                layout_two.visibility = View.VISIBLE
                loans_two_found.visibility = View.GONE
                loans_two_work.visibility = View.GONE
                loans_two_connection.visibility = View.GONE
                loans_two_restricted.visibility = View.GONE

                initImageSum()
                initImagMonth()
                totalSum()
                initResiscler()
                initSeekBar()
            }else{
                if (result.error.code != null) {
                    initErrorResult(result.error.code!!)
                }
            }
        })


        viewModel.errorGetLoanInfo.observe(viewLifecycleOwner, Observer { error->
            if (error != null){
                initError(error)
            }
        })
    }

    //Сохронение на сервер данных
    private fun initSaveLoan() {
        GetLoanActivity.alert.show()
        val mapSave = mutableMapOf<String, String>()
        mapSave.put("login", AppPreferences.login.toString())
        mapSave.put("token", AppPreferences.token.toString())
        mapSave.put("loan_type", "2");
        mapSave.put("loan_term", totalCounter.toString())
        mapSave.put("loan_sum", loan_step_sum.text.toString())
        mapSave.put("step", "1")

        viewModel.saveLoans(mapSave).observe(viewLifecycleOwner, Observer { result ->
            val data = result.data
            val msg = result.msg
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result != null) {
                        layout_two.visibility = View.VISIBLE
                        loans_two_work.visibility = View.GONE
                        loans_two_connection.visibility = View.GONE
                        loans_two_restricted.visibility = View.GONE
                        loans_two_found.visibility = View.GONE
                        AppPreferences.applicationId = data.result.id.toString()
                        if (applicationStatus == false) {
                            if (status == true) {
                                requireActivity().finish()
                            }else{
//                                (activity as GetLoanActivity?)!!.get_loan_view_pagers.currentItem = 2
                                (activity as GetLoanActivity?)!!.get_loan_view_pagers.currentItem = 3
                            }
                        }else{
//                            (activity as GetLoanActivity?)!!.get_loan_view_pagers.currentItem = 2
                            (activity as GetLoanActivity?)!!.get_loan_view_pagers.currentItem = 3
                        }
                    }else if (data.error.code != null) {
                        listListResult(data.error.code!!.toInt(), activity as AppCompatActivity)
                    }else if (data.reject != null) {
                        initBottomSheet(data.reject.message!!)
                        layout_two.visibility = View.VISIBLE
                        loans_two_work.visibility = View.GONE
                        loans_two_connection.visibility = View.GONE
                        loans_two_restricted.visibility = View.GONE
                        loans_two_found.visibility = View.GONE
                    }
                }
                Status.ERROR -> {
                    listListResult(msg!!, activity as AppCompatActivity)
                }
                Status.NETWORK -> {
                    listListResult(msg!!, activity as AppCompatActivity)
                }
            }
            GetLoanActivity.alert.hide()
        })
    }

    override fun onClickStepListener() {

    }

    private fun initBottomSheet(message: String) {
        val stepBottomFragment = StepBottomFragment(this, message)
        stepBottomFragment.isCancelable = false
        stepBottomFragment.show(requireActivity().supportFragmentManager, stepBottomFragment.tag)
    }

    private fun initResiscler() {
        var numberCount = minCounter
        val list: ArrayList<LoansStepTwoModel> = arrayListOf()
        while (numberCount <= maxCounter) {
            list.add(LoansStepTwoModel(numberCount))
            numberCount++
        }

        //Отключение прокрутки RecyclerView
        step_item_list.setOnTouchListener(OnTouchListener { v, event -> true })

        try {
            step_item_list.initialize(myAdapter)
            step_item_list.setViewsToChangeColor(listOf(R.id.loan_step_number, R.id.loan_step_number))
            myAdapter.update(list)

        }catch (e: Exception){
            e.printStackTrace()
        }


        step_item_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val position = getCurrentItem()
                    loan_step_month_seek.progress = position
                }
                if (newState == RecyclerView.SCREEN_STATE_ON){
                    val position = getCurrentItem()
                    AppPreferences.isSeekBar = position
                }
            }
        })
    }

    private fun totalSum(){
        val equ = round((totalSum * (totalRate / 100)) / (1 - pow((1 + (totalRate / 100)), - totalCounter.toDouble())))
        totalSumLoans.setText(equ.toInt().toString())
    }

    operator fun hasNext(): Boolean {
        return step_item_list.getAdapter() != null &&
                getCurrentItem() < step_item_list.getAdapter()!!.getItemCount() - 1
    }

    operator fun next() {
        val adapter: RecyclerView.Adapter<*> = step_item_list.getAdapter() ?: return
        val position = getCurrentItem()
        val count = adapter.itemCount
        if (position < count - 1) setCurrentItem(position + 1, true)
    }

    private fun getCurrentItem(): Int {
        return (step_item_list.getLayoutManager() as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    private fun setCurrentItem(position: Int, smooth: Boolean) {
        if (smooth) step_item_list.smoothScrollToPosition(position) else step_item_list.scrollToPosition(
            position
        )
    }

    @SuppressLint("NewApi")
    private fun initSeekBar() {
        loan_step_seek.max = sumMax / 1000 - 5
        loan_step_seek.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                val seekbarValue = i.toString()
                var resultSum = 0
                resultSum = seekbarValue.toInt() * 1000 + sumMin
                progressBarr(resultSum.toString())
                totalSum = resultSum
                totalSum()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        loan_step_month_seek.max = maxCounter - minCounter
        loan_step_month_seek.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                position = progress
                totalCounter = progress + 2
                totalSum()

                step_item_list.smoothScrollToPosition(position)
                AppPreferences.isSeekBar = position
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                step_item_list.smoothScrollToPosition(position)
//                AppPreferences.isSeekBar = position
            }
        })
    }

    fun progressBarr(i: String) {
        loan_step_sum.setText(i)
    }

    private fun initImagMonth() {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        params.weight = 1.0f
        params.gravity = Gravity.CENTER
        val sum = monthMax
        val pairs = arrayOfNulls<ImageView>(sum)
        val v = pairs.size - 1

        for (l in 0..v) {
            val p = pairs.size / 2
            if (l == 0) {
                nestedImage(l, pairs, params)
                pairs[l]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_coordinate_maxi))
            } else if (l == v) {
                nestedImage(l, pairs, params)
                pairs[l]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_coordinate_maxi))
            } else {
                nestedImage(l, pairs, params)
                pairs[l]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_coordinate_mini))
            }
            loan_layout_month.addView(pairs[l])
        }
    }

    private fun initImageSum() {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        params.weight = 1.0f
        params.gravity = Gravity.CENTER
        val sum = sumMax / 1000
        val pairs = arrayOfNulls<ImageView>(sum)
        val v = pairs.size - 1

        for (l in 0..v) {
            val p = pairs.size / 2
            if (l == 0) {
                nestedImage(l, pairs, params)
                pairs[l]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_coordinate_maxi))
            } else if (l == v) {
                nestedImage(l, pairs, params)
                pairs[l]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_coordinate_maxi))
            } else if (l == p) {
                nestedImage(l, pairs, params)
                pairs[l]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_coordinate_maxi))
            } else {
                nestedImage(l, pairs, params)
                pairs[l]!!.setImageDrawable(resources.getDrawable(R.drawable.ic_coordinate_mini))
            }
            loan_layout_parallel.addView(pairs[l])
        }
    }

    private fun nestedImage(
        number: Int,
        pairs: Array<ImageView?> = arrayOfNulls(0),
        params: LinearLayout.LayoutParams
    ) {
        pairs[number] = ImageView(context)
        pairs[number]!!.layoutParams = params
        pairs[number]!!.id = number
        pairs[number]!!.setPadding(10, 5, 10, 5)
    }

    private fun initErrorResult(result: Int) {
        if (result == 403) {
            loans_two_found.visibility = View.GONE
            loans_two_work.visibility = View.GONE
            loans_two_connection.visibility = View.GONE
            layout_two.visibility = View.GONE
            loans_two_restricted.visibility = View.VISIBLE
        } else if (result == 404) {
            loans_two_found.visibility = View.VISIBLE
            loans_two_work.visibility = View.GONE
            loans_two_connection.visibility = View.GONE
            layout_two.visibility = View.GONE
            loans_two_restricted.visibility = View.GONE
        } else if (result == 401) {
            initAuthorized()
        } else if (result == 500 || result == 400 || result == 409 || result == 429) {
            loans_two_work.visibility = View.VISIBLE
            loans_two_connection.visibility = View.GONE
            layout_two.visibility = View.GONE
            loans_two_restricted.visibility = View.GONE
            loans_two_found.visibility = View.GONE
        }
    }

    private fun initError(error: String) {
        if (error == "601") {
            loans_two_connection.visibility = View.VISIBLE
            layout_two.visibility = View.GONE
            loans_two_restricted.visibility = View.GONE
            loans_two_found.visibility = View.GONE
            loans_two_work.visibility = View.GONE
        } else if (error == "403") {
            loans_two_restricted.visibility = View.VISIBLE
            loans_two_found.visibility = View.GONE
            loans_two_work.visibility = View.GONE
            loans_two_connection.visibility = View.GONE
            layout_two.visibility = View.GONE
        } else if (error == "404") {
            loans_two_found.visibility = View.VISIBLE
            loans_two_work.visibility = View.GONE
            loans_two_connection.visibility = View.GONE
            layout_two.visibility = View.GONE
            loans_two_restricted.visibility = View.GONE
        } else if (error == "401") {
            initAuthorized()
        } else if (error == "500" || error == "400" || error == "409" || error == "429" || error == "600") {
            loans_two_work.visibility = View.VISIBLE
            loans_two_connection.visibility = View.GONE
            layout_two.visibility = View.GONE
            loans_two_restricted.visibility = View.GONE
            loans_two_found.visibility = View.GONE
        }
    }

    private fun initAuthorized() {
        val intent = Intent(context, HomeActivity::class.java)
        AppPreferences.token = ""
        startActivity(intent)
    }

    private fun initRestart(){
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        map.put("loan_type", "1")

        ObservedInternet().observedInternet(requireContext())
        if (!AppPreferences.observedInternet) {
            loans_two_connection.visibility = View.VISIBLE
            loans_two_work.visibility = View.GONE
            layout_two.visibility = View.GONE
            loans_two_restricted.visibility = View.GONE
            loans_two_found.visibility = View.GONE
        }else{
            viewModel.getInfo(map)
            initСounter()
        }
    }
}