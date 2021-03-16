package com.example.kotlincashloan.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.MyOperationAdapter
import com.example.kotlincashloan.adapter.profile.OperationListener
import com.example.kotlincashloan.service.model.profile.ResultOperationModel
import com.example.kotlinscreenscanner.ui.MainActivity
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.fragment_my_operation.*

class MyOperationFragment(var list: ArrayList<ResultOperationModel> = arrayListOf(), var error: String) : Fragment(), OperationListener {
    private var myAdapter = MyOperationAdapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_operation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        if (error == "404"){
            profile_my_operation_null.visibility = View.VISIBLE
            operation_recycler.visibility = View.GONE
        }else{
            profile_my_operation_null.visibility = View.GONE
            operation_recycler.visibility = View.VISIBLE
            myAdapter.update(list)
            operation_recycler.adapter = myAdapter
        }
    }

    override fun operationClickListener(int: Int, item: ResultOperationModel) {
        val bundle = Bundle()
        bundle.putInt("operationId", item.id!!)
        bundle.putString("title", item.title)
        AppPreferences.inputsAnim = 1
        findNavController().navigate(R.id.navigation_detail_profile, bundle)
    }

    override fun onResume() {
        super.onResume()
        MainActivity.timer.timeStop()
    }
}