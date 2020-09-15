package com.example.kotlincashloan.adapter.profile


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.support.ListFaqResultModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import kotlinx.android.synthetic.main.item_support.view.*
import java.util.Collections.addAll

class SupportAdapter (item: ArrayList<ListFaqResultModel> = arrayListOf()): GenericRecyclerAdapter<ListFaqResultModel>(item){
    override fun bind(item: ListFaqResultModel, holder: ViewHolder) {
        holder.itemView.support_name.text = item.name
        holder.itemView.support_text.setMarkDownText(item.text)

        var number = 0
        holder.itemView.support_layout.setOnClickListener {
            if (number != 0) {
                holder.itemView.support_image_anim.animate().rotation(0F).start()
                holder.itemView.support_text.visibility = View.GONE
                number = 0
            } else {
                holder.itemView.support_image_anim.animate().rotation(90F).start()
                holder.itemView.support_text.visibility = View.VISIBLE
                number = 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_support)
    }
}