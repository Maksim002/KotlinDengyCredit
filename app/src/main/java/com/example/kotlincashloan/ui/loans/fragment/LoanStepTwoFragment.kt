package com.example.kotlincashloan.ui.loans.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.loans.LoansStepAdapter
import com.example.kotlincashloan.service.model.Loans.LoansStepTwoModel
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.utils.MyUtils
import kotlinx.android.synthetic.main.fragment_loan_step_two.*


class LoanStepTwoFragment : Fragment() {
    private var myAdapter = LoansStepAdapter()

    private val sumMax = 25
    private val sumMin = 0

    private val monthMax = 12
    private val monthMin = 0

    private var position = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_step_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSeekBar()
        initImageSum()
        initImagMonth()
        initResiscler()
    }

    private fun initResiscler() {
        val list: ArrayList<LoansStepTwoModel> = arrayListOf()
        list.add(LoansStepTwoModel(1))
        list.add(LoansStepTwoModel(2))
        list.add(LoansStepTwoModel(3))
        list.add(LoansStepTwoModel(4))
        list.add(LoansStepTwoModel(5))
        list.add(LoansStepTwoModel(6))
        list.add(LoansStepTwoModel(7))
        list.add(LoansStepTwoModel(8))
        list.add(LoansStepTwoModel(9))
        list.add(LoansStepTwoModel(10))
        list.add(LoansStepTwoModel(11))
        list.add(LoansStepTwoModel(12))

        step_item_list.initialize(myAdapter)
        step_item_list.setViewsToChangeColor(listOf(R.id.loan_step_number, R.id.loan_step_number))
        myAdapter.update(list)

        step_item_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val position = getCurrentItem()
                    loan_step_month_seek.progress = position
                }
            }
        })
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
        val max = sumMax - 5
        val min = sumMin
        loan_step_seek.max = max
        loan_step_seek.min = min
        loan_step_seek.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

                seekBar.min = sumMin
                val seekbarValue = i.toString()
                var resultSum = 0
                resultSum = seekbarValue.toInt() * 1000 + 5000


                progressBarr(MyUtils.toServerDate(resultSum.toString()))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        loan_step_month_seek.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                position = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                step_item_list.smoothScrollToPosition(position)
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
        val sum = monthMax - monthMin
        val pairs = arrayOfNulls<ImageView>(sum)
        var v = pairs.size - 1

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
        val sum = sumMax - sumMin
        val pairs = arrayOfNulls<ImageView>(sum)
        var v = pairs.size - 1

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
            loan_layout.addView(pairs[l])
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

    override fun onResume() {
        super.onResume()
        MainActivity.timer.timeStop()
        loan_step_sum.text = "5 000"
    }
}