package com.example.kotlincashloan.adapter.profile

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.profile.ResultApplicationModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import com.timelysoft.tsjdomcom.service.AppPreferences
import kotlinx.android.synthetic.main.item_application.view.*
import kotlinx.android.synthetic.main.item_operation.view.*

class MyApplicationAdapter (var listener: ApplicationListener,item: ArrayList<ResultApplicationModel> = arrayListOf()): GenericRecyclerAdapter<ResultApplicationModel>(item){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_application)
    }

    override fun bind(item: ResultApplicationModel, holder: ViewHolder) {
        holder.itemView.app_data.text = item.date
        holder.itemView.app_title.text = item.name
        holder.itemView.application_status.text = item.status

        if (AppPreferences.reviewCode == 1) {
            if (item.review == false) {
                holder.itemView.application_review.visibility = View.VISIBLE
            } else {
                holder.itemView.application_review.visibility = View.GONE
            }
        }else{
            holder.itemView.application_review.visibility = View.GONE
        }

        if (item.detail == true){
            holder.itemView.application_detail.visibility = View.VISIBLE
            holder.itemView.application_item.setOnClickListener {
                listener.applicationListener(holder.adapterPosition, item)
            }
        }else{
            holder.itemView.application_detail.visibility = View.GONE
        }
    }
}