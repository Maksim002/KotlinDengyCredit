package com.example.kotlincashloan.adapter.loans

import android.os.Build
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.Loans.LoansStepTwoModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import kotlinx.android.synthetic.main.item_loan_step.view.*

class LoansStepAdapter(item: ArrayList<LoansStepTwoModel> = arrayListOf()): GenericRecyclerAdapter<LoansStepTwoModel>(item){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_loan_step)
    }

    override fun bind(item: LoansStepTwoModel, holder: ViewHolder) {
        holder.itemView.loan_step_number.text = item.number.toString()
    }
}