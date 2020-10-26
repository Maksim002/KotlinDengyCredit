package com.example.kotlincashloan.ui.profile

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.MyOperationAdapter
import com.example.kotlincashloan.adapter.profile.MyOperationModel
import com.example.kotlincashloan.ui.registration.login.HomeActivity
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_my_operation.*
import java.util.HashMap

class MyOperationFragment : Fragment() {
    private var myAdapter = MyOperationAdapter()
    private var viewModel = ProfileViewModel()
    private val map = HashMap<String, String>()
    val handler = Handler()
    private var refresh = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_operation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map.put("login", AppPreferences.login.toString())
        map.put("token", AppPreferences.token.toString())

        initRecycler()
    }

    private fun initRecycler() {
        var list: ArrayList<MyOperationModel> = arrayListOf()
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))

        if (!refresh){
            HomeActivity.alert.show()
        }
        viewModel.listListOperationDta.observe(viewLifecycleOwner, Observer { result->
            if (result.result != null){
                myAdapter.update(result.result)
                operation_recycler.adapter = myAdapter
            }
            HomeActivity.alert.hide()
        })
    }

    override fun onResume() {
        super.onResume()
        MainActivity.timer.timeStop()
        HomeActivity.alert.hide()
        if (viewModel.listListOperationDta .value == null) {
            HomeActivity.alert.show()
            viewModel.listOperation (map)
            initRecycler()
            HomeActivity.alert.hide()
        }
    }
}