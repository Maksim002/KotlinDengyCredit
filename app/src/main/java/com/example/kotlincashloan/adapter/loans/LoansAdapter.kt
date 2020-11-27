package com.example.kotlincashloan.adapter.loans

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.Loans.ListNewsResultModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import kotlinx.android.synthetic.main.item_loans.view.*

class LoansAdapter (var listener: LoansListener, item: ArrayList<ListNewsResultModel> = arrayListOf()): GenericRecyclerAdapter<ListNewsResultModel>(item){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_loans)
    }

    override fun bind(item: ListNewsResultModel, holder: ViewHolder) {
        holder.itemView.loans_name.text = item.name
        holder.itemView.loans_description.text = item.description

        Glide
            .with(holder.itemView.loans_image)
            .load(item.thumbnail)
            .into(holder.itemView.loans_image)

        holder.itemView.setOnClickListener {
            listener.loansClickListener(holder.adapterPosition, item.id!!, item.name.toString())
        }
    }
}