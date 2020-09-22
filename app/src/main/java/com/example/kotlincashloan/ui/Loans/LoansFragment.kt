package com.example.kotlincashloan.ui.Loans


import android.content.Intent
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
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_loans.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
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
        MainActivity.alert = LoadingAlert(activity as AppCompatActivity)
        initLogicSeekBar()
        iniRecyclerView()
        initClick()
    }

    private fun initClick() {
        no_connection_repeat.setOnClickListener {
            iniRecyclerView()
        }

        access_restricted.setOnClickListener {
            iniRecyclerView()
        }

        not_found.setOnClickListener {
            iniRecyclerView()
        }

        technical_work.setOnClickListener {
            iniRecyclerView()
        }
    }

    private fun iniRecyclerView() {
        val map = HashMap<String, String>()
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        MainActivity.alert.show()
        viewModel.listNews(map).observe(viewLifecycleOwner, Observer { result ->
            val msg = result.msg
            val data = result.data
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.result == null){
                        if (data.error.code == 403){
                            loans_no_connection.visibility = View.GONE
                            loans_access_restricted.visibility = View.VISIBLE
                            loans_layout.visibility = View.GONE
                        }else if (data.error.code == 404){
                            loans_no_connection.visibility = View.GONE
                            loans_not_found.visibility = View.VISIBLE
                            loans_layout.visibility = View.GONE
                        }else if (data.error.code == 401){
                            initAuthorized()
                        }else{
                            loans_no_connection.visibility = View.GONE
                            loans_technical_work.visibility = View.VISIBLE
                            loans_layout.visibility = View.GONE
                        }
                    }else{
                        myAdapter.update(data.result)
                        loans_recycler.adapter = myAdapter
                        myAdapter.notifyDataSetChanged()
                        loans_layout.visibility = View.VISIBLE
                        loans_no_connection.visibility = View.GONE
                    }
                }
                Status.ERROR -> {
                    if (msg == "403"){
                        loans_no_connection.visibility = View.GONE
                        loans_access_restricted.visibility = View.VISIBLE
                        loans_layout.visibility = View.GONE
                    }else if (msg == "404"){
                        loans_no_connection.visibility = View.GONE
                        loans_not_found.visibility = View.VISIBLE
                        loans_layout.visibility = View.GONE
                    }else if (msg == "401"){
                        initAuthorized()
                    }else{
                        loans_no_connection.visibility = View.GONE
                        loans_technical_work.visibility = View.VISIBLE
                        loans_layout.visibility = View.GONE
                    }
                }
                Status.NETWORK -> {
                    loans_layout.visibility = View.GONE
                    loans_access_restricted.visibility = View.GONE
                    loans_not_found.visibility = View.GONE
                    loans_technical_work.visibility = View.GONE
                    loans_no_connection.visibility = View.VISIBLE
                }
            }
            MainActivity.alert.hide()
        })
    }

    private fun initAuthorized(){
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }

    private fun initLogicSeekBar() {
        loans_sum.setText("1000")
        loans_seekBar.max = 2000
        loans_seekBar.isEnabled = false
        loans_seekBar.progress = loans_sum.text.toString().toInt()
    }
}