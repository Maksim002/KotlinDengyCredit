package com.example.kotlincashloan.adapter.profile

import android.text.Html
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlincashloan.service.model.support.ListFaqResultModel
import com.timelysoft.tsjdomcom.common.GenericRecyclerAdapter
import com.timelysoft.tsjdomcom.common.ViewHolder
import kotlinx.android.synthetic.main.item_profile.view.*

class ProfileAdapter (item: ArrayList<ListFaqResultModel> = arrayListOf()): GenericRecyclerAdapter<ListFaqResultModel>(item){
    override fun bind(item: ListFaqResultModel, holder: ViewHolder) {
        holder.itemView.themed_dropdown_text_view.setTitleText(item.name.toString())
        holder.itemView.themed_dropdown_text_view.setContentText(item.text.toString())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.itemView.themed_dropdown_text_view.setContentText(Html.fromHtml(item.text, Html.FROM_HTML_MODE_LEGACY).trim().toString())
        } else {
            holder.itemView.themed_dropdown_text_view.setContentText(Html.fromHtml(item.text).toString())
        }
    }
//
//    holder.itemView.discount_image.loadImage(AppPreferences.baseUrl+item.prefix+"/"+item.infoImages.firstOrNull()?.fileName)
//
//    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//        holder.itemView.discount_description.text = Html.fromHtml(item.boldDescription, Html.FROM_HTML_MODE_LEGACY).trim()
//    } else {
//        holder.itemView.discount_description.text = Html.fromHtml(item.boldDescription)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(parent, R.layout.item_profile)
    }
}