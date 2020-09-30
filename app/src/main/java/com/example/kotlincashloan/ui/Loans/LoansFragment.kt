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
import kotlinx.android.synthetic.main.fragment_loans.*
import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*

class LoansFragment : Fragment(), LoansListener {
    private var myAdapter = LoansAdapter(this)
    private var viewModel = LoansViewModel()
    val map = HashMap<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loans, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        initLogicSeekBar()
        initRecycler()
        initClick()
        initRefresh()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.listNewsDta.value == null)
            viewModel.listNews(map)
        initRecycler()
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
    }

    private fun initRefresh(){
        loans_layout.setOnRefreshListener {
            initRestart()
            loans_layout.isRefreshing = false
        }
        loans_layout.setColorSchemeResources(android.R.color.holo_orange_dark)
    }

    private fun initRestart() {
        initRecycler()
        if (viewModel.listNewsDta.value != null) {
            viewModel.listNews(map)
        }else{
            viewModel.errorNews.value = null
            viewModel.listNews(map)
        }
    }

    private fun initRecycler() {
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        HomeActivity.alert.show()
        viewModel.listNewsDta.observe(viewLifecycleOwner, Observer { result ->
            if (result.result != null) {
                myAdapter.update(result.result)
                loans_recycler.adapter = myAdapter
                loans_layout.visibility = View.VISIBLE
                loans_no_connection.visibility = View.GONE
            } else if (result.error.code == 403) {
                loans_no_connection.visibility = View.GONE
                loans_access_restricted.visibility = View.VISIBLE
                loans_layout.visibility = View.GONE
            } else if (result.error.code == 404) {
                loans_no_connection.visibility = View.GONE
                loans_not_found.visibility = View.VISIBLE
                loans_layout.visibility = View.GONE
            } else if (result.error.code == 401) {
                initAuthorized()
            } else if (result.error.code == 500 || result.error.code == 400) {
                loans_no_connection.visibility = View.GONE
                loans_technical_work.visibility = View.VISIBLE
                loans_layout.visibility = View.GONE
            }
            HomeActivity.alert.hide()
        })

        viewModel.errorNews.observe(viewLifecycleOwner, Observer { error ->
            if (error == "600") {
                loans_no_connection.visibility = View.VISIBLE
                loans_layout.visibility = View.GONE
                loans_access_restricted.visibility = View.GONE
                loans_not_found.visibility = View.GONE
                loans_technical_work.visibility = View.GONE
            } else if (error == "403") {
                loans_no_connection.visibility = View.GONE
                loans_access_restricted.visibility = View.VISIBLE
                loans_layout.visibility = View.GONE
            } else if (error == "404") {
                loans_no_connection.visibility = View.GONE
                loans_not_found.visibility = View.VISIBLE
                loans_layout.visibility = View.GONE
            } else if (error == "401") {
                initAuthorized()
            } else if (error == "500" || error == "400") {
                loans_no_connection.visibility = View.GONE
                loans_technical_work.visibility = View.VISIBLE
                loans_layout.visibility = View.GONE
            }
            HomeActivity.alert.hide()
        })
    }

    private fun initAuthorized() {
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
        findNavController().navigate(R.id.loans_details_navigation, build)
    }
}