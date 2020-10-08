package com.example.kotlincashloan.adapter.support

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.support.ListFaqResultModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import kotlinx.android.synthetic.main.item_support.view.*


class SupportAdapter(var date: ArrayList<ListFaqResultModel> = arrayListOf()) : GenericRecyclerAdapter<ListFaqResultModel>(date) {

    override fun bind(item: ListFaqResultModel, holder: ViewHolder) {
        holder.itemView.support_name.text = item.name
        holder.itemView.support_text.loadMarkdown(item.text)
        holder.itemView.support_text.getSettings().loadWithOverviewMode = true

        holder.itemView.setOnClickListener {
            if (item.clicked) {
                notifyItemChanged(upClear())
            } else {
                notifyItemChanged(upClear())
                item.clicked = true
                notifyItemChanged(holder.adapterPosition)
            }
        }

        if (item.clicked) {
            holder.itemView.support_text.visibility = View.VISIBLE
            holder.itemView.support_image_anim.animate().rotation(90F).start()
        } else {
            holder.itemView.support_text.visibility = View.GONE
            holder.itemView.support_image_anim.animate().rotation(0F).start()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_support)
    }

    private fun upClear(): Int {
        items.forEachIndexed { pos, it ->
            if (it.clicked) {
                it.clicked = false
                return pos
            }
        }
        return -1
    }
}