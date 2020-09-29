package com.example.kotlincashloan.ui.Loans


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.loans.LoansAdapter
import com.example.kotlincashloan.adapter.loans.LoansListener
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import com.timelysoft.tsjdomcom.utils.LoadingAlert
import kotlinx.android.synthetic.main.fragment_loans.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*
import java.util.HashMap

class LoansFragment : Fragment(), LoansListener {
    private var myAdapter = LoansAdapter(this)
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
        HomeActivity.alert.show()
        val map = HashMap<String, String>()
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
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
            HomeActivity.alert.hide()
        })
    }

    private fun initAuthorized(){
        val intent = Intent(context, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun initLogicSeekBar() {
        loans_sum.setText("1000")
        loans_seekBar.max = 2000
        loans_seekBar.isEnabled = false
        loans_seekBar.progress = loans_sum.text.toString().toInt()
    }

    override fun loansClickListener(position: Int, idNews: Int) {
        val build = Bundle()
        build.putInt("idNews", idNews)
//        findNavController().navigate(R.id.navigation_loans_details, build)
    }
}