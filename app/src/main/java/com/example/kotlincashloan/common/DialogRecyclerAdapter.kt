package com.timelysoft.tsjdomcom.common

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class DialogRecyclerAdapter<T>(var items: ArrayList<T>, val dalogList: ArrayList<T> = ArrayList()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    abstract fun bind(item: T, holder: ViewHolder)
    val temp = ArrayList<T>()

    fun update(items: ArrayList<T>) {
        this.items = items
        dalogList.addAll(items)
        notifyItemRangeChanged(items.size ,items.size)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    fun getListPosition(position: Int): T {
        return items[position]
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bind(items[position], holder as ViewHolder)
    }

    fun filter(sequence: CharSequence?) {
        if (!TextUtils.isEmpty(sequence)) {
            for (s in dalogList) {
                if (s.toString().toLowerCase().contains(sequence!!)) {
                    temp.add(s)
                }
            }
        } else {
            temp.addAll(dalogList)
        }
        items.clear()
        items.addAll(temp)
        notifyDataSetChanged()
        temp.clear()
    }
}