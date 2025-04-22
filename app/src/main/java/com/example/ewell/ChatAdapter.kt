package com.example.ewell

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ChatAdapter(private val messages: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val USER_VIEW = 1
    private val BOT_VIEW = 2

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isBot) BOT_VIEW else USER_VIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == USER_VIEW) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_self, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_watson, parent, false)
            BotViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserViewHolder) {
            holder.messageText.text = message.text
        } else if (holder is BotViewHolder) {
            holder.messageText.text = message.text
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.message)
    }

    inner class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.message)
    }
}
