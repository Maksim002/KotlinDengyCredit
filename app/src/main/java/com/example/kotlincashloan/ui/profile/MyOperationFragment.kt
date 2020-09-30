package com.example.kotlincashloan.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.MyOperationAdapter
import com.example.kotlincashloan.adapter.profile.MyOperationModel
import kotlinx.android.synthetic.main.fragment_my_operation.*

class MyOperationFragment : Fragment() {
    private var myAdapter = MyOperationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_operation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var list: ArrayList<MyOperationModel> = arrayListOf()
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))
        list.add(MyOperationModel(""))

        myAdapter.update(list)
        operation_recycler.adapter = myAdapter
    }
}