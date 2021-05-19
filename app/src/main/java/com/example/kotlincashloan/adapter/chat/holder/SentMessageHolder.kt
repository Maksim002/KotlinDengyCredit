package com.example.kotlinwebsocet.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R

class SentMessageHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    var messageTxt: TextView = itemView.findViewById(R.id.send_chat_text)
}