package com.example.kotlincashloan.adapter.notification

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.MyOperationModel
import com.example.kotlincashloan.service.model.Notification.ResultListNoticeModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import kotlinx.android.synthetic.main.item_notification.view.*
import kotlinx.serialization.PrimitiveKind

class NotificationAdapter(var listener: NotificationListener, item: ArrayList<ResultListNoticeModel> = arrayListOf()) : GenericRecyclerAdapter<ResultListNoticeModel>(item) {
    var reviewCode  = 0
    fun numberResult(v: Int){
        reviewCode = v
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_notification)
    }

    override fun bind(item: ResultListNoticeModel, holder: ViewHolder) {
        holder.itemView.notification_title.text = item.title
        holder.itemView.notification_description.text = item.description
        holder.itemView.notification_date.text = item.date

        if (reviewCode == 0) {
            if (item.review == false) {
                holder.itemView.notification_review.visibility = View.VISIBLE
            } else {
                holder.itemView.notification_review.visibility = View.GONE
            }
        }else{
            holder.itemView.notification_review.visibility = View.GONE
        }

        if (item.detail == true) {
            holder.itemView.notification_detail.visibility = View.VISIBLE
            holder.itemView.layout_item.setOnClickListener {
                listener.notificationClickListener(holder.adapterPosition, item)
            }
        } else {
            holder.itemView.notification_detail.visibility = View.GONE
        }
    }
}