package com.example.kotlincashloan.ui.support

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.SupportAdapter
import com.example.kotlincashloan.extension.loadingMistake
import com.example.kotlincashloan.ui.main.registration.login.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import com.timelysoft.tsjdomcom.service.Status
import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.android.synthetic.main.item_access_restricted.*
import kotlinx.android.synthetic.main.item_no_connection.*
import kotlinx.android.synthetic.main.item_not_found.*
import kotlinx.android.synthetic.main.item_technical_work.*

class SupportFragment : Fragment(){
    private var myAdapter = SupportAdapter()
    private var viewModel = SupportViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        initRecycler()
        iniClick()
    }

    private fun iniClick() {
        no_connection_repeat.setOnClickListener {
            initRecycler()
        }

        access_restricted.setOnClickListener {
            initRecycler()
        }

        technical_work.setOnClickListener {
            initRecycler()
        }

        not_found.setOnClickListener {
            initRecycler()
        }
    }

    private fun initRecycler() {
        MainActivity.alert.show()
        val map = HashMap<String, String>()
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())
        viewModel.listFaq(map).observe(viewLifecycleOwner, Observer { result ->
            val data = result.data
            val msg = result.msg
            when (result.status) {
                Status.SUCCESS -> {
                    if (data!!.error != null){
                        if (data.error.code == 403){
                            layout_access_restricted.visibility = View.VISIBLE
                            profile_recycler.visibility = View.GONE

                        }else if (data.error.code == 500){
                            support_technical_work.visibility = View.VISIBLE
                            profile_recycler.visibility = View.GONE

                        }else if (data.error.code == 404){
                            support_not_found.visibility = View.VISIBLE
                            profile_recycler.visibility = View.GONE
                        }
                    }

                    if (data.result != null){
                        myAdapter.update(data.result)
                        profile_recycler.adapter = myAdapter
                        initVisibilities()
                    }
                }
                Status.ERROR -> {
                    if (msg == "404"){
                        support_not_found.visibility = View.VISIBLE
                        profile_recycler.visibility = View.GONE

                    }else if (msg == "500"){
                        support_technical_work.visibility = View.VISIBLE
                        profile_recycler.visibility = View.GONE

                    }else if (msg == "403"){
                        layout_access_restricted.visibility = View.VISIBLE
                        profile_recycler.visibility = View.GONE
                    }
                }

                Status.NETWORK ->{
                    support_no_connection.visibility = View.VISIBLE
                    profile_recycler.visibility = View.GONE
                }
            }
            MainActivity.alert.hide()
        })
    }
    fun initVisibilities(){
        profile_recycler.visibility = View.VISIBLE
        support_no_connection.visibility = View.GONE
        layout_access_restricted.visibility = View.GONE
        support_technical_work.visibility = View.GONE
        support_not_found.visibility == View.GONE
    }
}