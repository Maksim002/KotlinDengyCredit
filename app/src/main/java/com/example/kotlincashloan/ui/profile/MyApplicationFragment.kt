package com.example.kotlincashloan.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.ApplicationListener
import com.example.kotlincashloan.adapter.profile.MyApplicationAdapter
import com.example.kotlincashloan.service.model.profile.ResultApplicationModel
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_my_application.*

class MyApplicationFragment(var listener: ApplicationListener, var list: ArrayList<ResultApplicationModel> = arrayListOf(), var error: String) : Fragment(), ApplicationListener {
    private var myAdapter = MyApplicationAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_application, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()


    }

    private fun initRecycler() {
        if (error == "404"){
            profile_aplication_null.visibility = View.VISIBLE
            application_recycler.visibility = View.GONE
        }else{
            application_recycler.visibility = View.VISIBLE
            profile_aplication_null.visibility = View.GONE
            myAdapter.update(list)
            application_recycler.adapter = myAdapter
        }

    }

    override fun applicationListener(int: Int, item: ResultApplicationModel) {
        listener.applicationListener(int, item)
    }

    override fun onResume() {
        super.onResume()
        MainActivity.timer.timeStop()

    }
}