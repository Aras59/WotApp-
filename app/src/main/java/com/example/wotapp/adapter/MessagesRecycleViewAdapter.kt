package com.example.wotapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wotapp.R
import com.example.wotapp.models.recycleViewModels.MessagesItemModel

class MessagesRecycleViewAdapter(private val messagesList: List<MessagesItemModel>) : RecyclerView.Adapter<MessagesRecycleViewAdapter.ViewHolder>()  {

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val messageTextView: TextView = itemView.findViewById<TextView>(R.id.messageTitleText)
        val creatorTextView: TextView  = itemView.findViewById<TextView>(R.id.messageCreatorText)
        val dateTextView: TextView  = itemView.findViewById<TextView>(R.id.messageDateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_view_design,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageItemModel = messagesList[position]
        holder.messageTextView.text = messageItemModel.message
        holder.creatorTextView.text = messageItemModel.messageCreator
        holder.dateTextView.text = messageItemModel.messageDate
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }
}