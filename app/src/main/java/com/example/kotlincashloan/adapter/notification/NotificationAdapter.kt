package com.example.kotlincashloan.adapter.notification

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.adapter.profile.MyOperationModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder

class NotificationAdapter(item: ArrayList<MyOperationModel> = arrayListOf()): GenericRecyclerAdapter<MyOperationModel>(item){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_notification)
    }

    override fun bind(item: MyOperationModel, holder: ViewHolder) {

    }
}