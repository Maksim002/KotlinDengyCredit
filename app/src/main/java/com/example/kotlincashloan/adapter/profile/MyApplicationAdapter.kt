package com.example.kotlincashloan.adapter.profile

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder

class MyApplicationAdapter (item: ArrayList<MyOperationModel> = arrayListOf()): GenericRecyclerAdapter<MyOperationModel>(item){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_application)
    }

    override fun bind(item: MyOperationModel, holder: ViewHolder) {

    }
}