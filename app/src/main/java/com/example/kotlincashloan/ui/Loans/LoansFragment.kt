package com.example.kotlincashloan.ui.Loans


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.LoansAdapter
import com.example.kotlincashloan.adapter.LoansModel
import com.example.kotlincashloan.ui.main.registration.login.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_loans.*
import java.util.HashMap
import kotlin.collections.ArrayList


class LoansFragment : Fragment() {
    private var myAdapter = LoansAdapter()
    private var viewModel = LoansViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
        initLogicSeekBar()
        initClick()
        iniRecyclerView()
    }

    private fun initClick() {

    }

    private fun iniRecyclerView() {
        val list: ArrayList<LoansModel> = arrayListOf()
        list.add(LoansModel(""))
        list.add(LoansModel(""))
        list.add(LoansModel(""))
        list.add(LoansModel(""))
        list.add(LoansModel(""))

        val map = HashMap<String, String>()
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())

        viewModel.listNews(map).observe(viewLifecycleOwner, Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
//                    if (data!!.result != null){
//
//
                    myAdapter.update(data!!.result)
                    loans_recycler.adapter = myAdapter
                    myAdapter.notifyDataSetChanged()
                }
                Status.ERROR -> {

                }
                Status.NETWORK -> {
                    loans_no_connection.visibility = View.VISIBLE
                    loans_layout.visibility = View.GONE
                }
            }
            MainActivity.alert.hide()
        })
    }

    private fun initLogicSeekBar() {
        loans_sum.setText("1000")
        loans_seekBar.max = 2000
        loans_seekBar.isEnabled = false
        loans_seekBar.progress = loans_sum.text.toString().toInt()
    }
}