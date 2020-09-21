package com.example.kotlincashloan.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.Loans.ListNewsResultModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder

class LoansAdapter (item: ArrayList<ListNewsResultModel> = arrayListOf()): GenericRecyclerAdapter<ListNewsResultModel>(item){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_loans)
    }

    override fun bind(item: ListNewsResultModel, holder: ViewHolder) {

    }
}