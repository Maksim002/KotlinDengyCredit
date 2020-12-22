package com.example.kotlincashloan.adapter.profile

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.profile.ResultOperationModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.item_notification.view.*
import kotlinx.android.synthetic.main.item_operation.view.*

class MyOperationAdapter (var listener: OperationListener ,item: ArrayList<ResultOperationModel> = arrayListOf()): GenericRecyclerAdapter<ResultOperationModel>(item){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_operation)
    }

    override fun bind(item: ResultOperationModel, holder: ViewHolder) {
        holder.itemView.operation_data.text = item.date
        holder.itemView.operation_title.text = item.title
        holder.itemView.operation_description.text = item.description

        if (AppPreferences.reviewCode == 1) {
            if (item.review == false) {
                holder.itemView.operation_review.visibility = View.VISIBLE
            } else {
                holder.itemView.operation_review.visibility = View.GONE
            }
        }else{
            holder.itemView.operation_review.visibility = View.GONE
        }

        if (item.detail == true){
            holder.itemView.operation_detail.visibility = View.VISIBLE
            holder.itemView.operation_item.setOnClickListener {
                listener.operationClickListener(holder.adapterPosition, item)
            }
        }else{
            holder.itemView.operation_detail.visibility = View.GONE
        }
    }
}