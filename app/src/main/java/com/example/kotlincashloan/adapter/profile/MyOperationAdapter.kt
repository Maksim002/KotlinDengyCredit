package com.example.kotlincashloan.adapter.profile

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.profile.ResultOperationModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import kotlinx.android.synthetic.main.item_operation.view.*

class MyOperationAdapter (item: ArrayList<ResultOperationModel> = arrayListOf()): GenericRecyclerAdapter<ResultOperationModel>(item){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_operation)
    }

    override fun bind(item: ResultOperationModel, holder: ViewHolder) {
        holder.itemView.operation_data.text = item.date
        holder.itemView.operation_title.text = item.title
        holder.itemView.operation_description.text = item.description
    }
}