package com.example.kotlincashloan.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.MyApplicationAdapter
import com.example.kotlincashloan.adapter.profile.MyOperationModel
import kotlinx.android.synthetic.main.fragment_my_application.*

class MyApplicationFragment : Fragment() {
    private var myAdapter = MyApplicationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_application, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list: ArrayList<MyOperationModel> = arrayListOf()
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))

        myAdapter.update(list)
        application_recycler.adapter = myAdapter
    }
}