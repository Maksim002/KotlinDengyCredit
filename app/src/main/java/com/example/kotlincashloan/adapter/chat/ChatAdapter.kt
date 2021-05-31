package com.example.kotlincashloan.adapter.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlincashloan.R
import com.example.kotlinwebsocet.holder.*
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class ChatAdapter : RecyclerView.Adapter<MessageViewHolder>() {
    private lateinit var view: View
    private val messages: MutableList<JSONObject> = ArrayList()

    fun addItem(jsonObject: JSONObject) {
        messages.add(jsonObject)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        when (viewType) {
            TYPE_MESSAGE_SENT -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_chat, parent, false)
                SentMessageHolder(view)
            }
            TYPE_MESSAGE_RECEIVED -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_chat, parent, false)
                ReceivedMessageHolder(view)
            }
            TYPE_IMAGE_SENT -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_image, parent, false)
                SentImageHolder(view)
            }
            TYPE_IMAGE_RECEIVED -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_photo, parent, false)
                ReceivedImageHolder(view)
            }
        }
        return MessageViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        try {
            return if (message.getBoolean("isSent")) {
                if (message.has("msg")) TYPE_MESSAGE_SENT else TYPE_IMAGE_SENT
            } else {
                if (message.has("method")) TYPE_MESSAGE_RECEIVED else TYPE_IMAGE_RECEIVED
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return -1
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message, holder)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    companion object {
        private const val TYPE_MESSAGE_SENT = 0
        private const val TYPE_MESSAGE_RECEIVED = 1
        private const val TYPE_IMAGE_SENT = 2
        private const val TYPE_IMAGE_RECEIVED = 3
    }
}