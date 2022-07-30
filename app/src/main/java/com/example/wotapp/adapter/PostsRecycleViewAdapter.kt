package com.example.wotapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wotapp.R
import com.example.wotapp.activities.ForumPostActivity
import com.example.wotapp.models.recycleViewModels.PostItemModel

class PostsRecycleViewAdapter(private val context : Context, private val postList : ArrayList<PostItemModel>) : RecyclerView.Adapter<PostsRecycleViewAdapter.ViewHolder>(){

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val titleTextView: TextView = itemView.findViewById<TextView>(R.id.postTitleText)
        val creatorTextView: TextView = itemView.findViewById<TextView>(R.id.postCreatorText)
        val dateTextView: TextView = itemView.findViewById<TextView>(R.id.postDateText)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postItemModel = postList[position]
        holder.titleTextView.text = postItemModel.title
        holder.creatorTextView.text = postItemModel.creator
        holder.dateTextView.text = postItemModel.date

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ForumPostActivity::class.java)
            intent.putExtra("title",postItemModel.title)
            intent.putExtra("creator",postItemModel.creator)
            intent.putExtra("date",postItemModel.date)
            intent.putExtra("postId",postItemModel.postId)
            intent.putExtra("desc",postItemModel.description)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_view_design,parent,false)

        return PostsRecycleViewAdapter.ViewHolder(view)
    }

    fun updateData( newPostsList : ArrayList<PostItemModel>){
        postList.clear()
        postList.addAll(newPostsList)
        notifyDataSetChanged()
    }
}