package com.example.kotlinwebsocet.holder

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONException
import org.json.JSONObject

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(message: JSONObject, holder: MessageViewHolder) {
        try {
            if (message.getBoolean("isSent")) {
                if (message.has("msg")) {
                    val messageHolder = SentMessageHolder(itemView)
                    messageHolder.messageTxt.text = message.getString("msg")
                } else {
//                    val imageHolder = SentImageHolder(itemView)
//                    val bitmap = getBitmapFromString(message.getString("image"))
//                    imageHolder.imageView.setImageBitmap(bitmap)
                }
            } else {
                if (message.has("method")) {
//                    val messageHolder = ReceivedMessageHolder(itemView)
//                    messageHolder.messageTxt.text = message.getString("ms")
                } else {
//                    val imageHolder = ReceivedImageHolder(itemView)
//                    imageHolder.nameTxt.text = message.getString("name")
//                    val bitmap = getBitmapFromString(message.getString("image"))
//                    imageHolder.imageView.setImageBitmap(bitmap)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getBitmapFromString(image: String): Bitmap {
        val bytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}