package com.example.kotlincashloan.adapter.general

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.general.GeneralDialogModel
import com.timelysoft.tsjdomcom.common.DialogRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import kotlinx.android.synthetic.main.item_general_dialog_fragment.view.*

class GeneralDialogAdapter(var position: String, var listener: ListenerGeneralDialog, var list: ArrayList<GeneralDialogModel> = arrayListOf()): DialogRecyclerAdapter<GeneralDialogModel>(list){
    private var selectedPosition = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_general_dialog_fragment)
    }

    override fun bind(item: GeneralDialogModel, holder: ViewHolder) {
        holder.itemView.text_dialog.text = item.name

        if (selectedPosition == item.name) {
            holder.itemView.isSelected = true; //using selector drawable
            holder.itemView.radio_dialog.isChecked = true
        } else {
            holder.itemView.isSelected = false;
            holder.itemView.radio_dialog.isChecked = false
        }

        holder.itemView.layout_dialog.setOnClickListener {
            if (selectedPosition >= item.name)
                notifyItemChanged(holder.adapterPosition);
            selectedPosition = item.name
            notifyItemChanged(holder.adapterPosition);
            listener.listenerClickDialog(holder.adapterPosition, item.name, item.key)
        }
    }
}