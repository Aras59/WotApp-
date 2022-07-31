package com.example.wotapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wotapp.activities.NewPostForumActivity
import com.example.wotapp.R
import com.example.wotapp.adapter.PostsRecycleViewAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.example.wotapp.models.recycleViewModels.PostItemModel
import java.util.ArrayList
import java.util.HashMap

class ForumFragment : Fragment() {
    private lateinit var addPostButton: ImageButton
    private lateinit var category: Spinner
    private lateinit var categorySpinnerAdapter: ArrayAdapter<CharSequence>
    private val firestore = FirebaseFirestore.getInstance()
    private var postList = HashMap<String, QuerySnapshot>()
    private lateinit var serach: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var postRecycleView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forum, container, false)
        addPostButton = view.findViewById(R.id.addPostButton)
        postRecycleView = view.findViewById(R.id.postsRecyclerView)
        postRecycleView.layoutManager = LinearLayoutManager(activity)
        postList.clear()
        category = view.findViewById(R.id.categorySpiner)
        serach = view.findViewById(R.id.search)
        progressBar = view.findViewById(R.id.progressBar4)
        categorySpinnerAdapter = activity?.let { ArrayAdapter.createFromResource(it,
            com.example.wotapp.R.array.category,
            com.example.wotapp.R.layout.spinner_category) } as ArrayAdapter<CharSequence>
        category.adapter = categorySpinnerAdapter


        val categoriesArray = resources.getStringArray(R.array.category)
        progressBar.visibility = View.VISIBLE
        for(categories in categoriesArray){
            if(!"All".equals(categories) ) {
                getPostsFromFirebase(categories)
            }

        }

        serach.setOnClickListener {
            initializeViewByCategory(category.selectedItem.toString())
        }

        addPostButton.setOnClickListener {
            val intent = Intent(activity, NewPostForumActivity::class.java)
            startActivity(intent)
        }


        return view
    }


    private fun initializeView() {
        if (postList.size == 4) {
            val postRecyclerViewList = ArrayList<PostItemModel>()
            for (documents in postList) {
                for (document in documents.value) {
                    val postTitle = document.data["title"] as String?
                    val postCreator = document.data["nickname"] as String?
                    val postDate = document.data["date"] as String?
                    val postId = document.id
                    val postDescription = document.data["description"] as String?
                    if (postTitle != null && postCreator != null && postDate != null && postDescription != null && postId != null) {
                        postRecyclerViewList.add(
                            PostItemModel(
                                postTitle,
                                postCreator,
                                postDate,
                                postId,
                                postDescription
                            )
                        )
                    }
                }
            }
            if (postRecyclerViewList.isNotEmpty()) {
                initializePostRecycleView(postRecyclerViewList)
            }
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun initializePostRecycleView(postList : ArrayList<PostItemModel>){
        val adapter = activity?.let { PostsRecycleViewAdapter(it,postList) }
        postRecycleView.adapter = adapter
    }

    private fun initializeViewByCategory(category: String){
        if(postList.size == 4) {
            val postRecyclerViewList = ArrayList<PostItemModel>()
            if("All".equals(category)){
                for (documents in postList) {
                    for (document in documents.value) {
                        val postTitle = document.data["title"] as String?
                        val postCreator = document.data["nickname"] as String?
                        val postDate = document.data["date"] as String?
                        val postId = document.id
                        val postDescription = document.data["description"] as String?
                        if (postTitle != null && postCreator != null && postDate != null && postDescription != null && postId != null) {
                            postRecyclerViewList.add(
                                PostItemModel(
                                    postTitle,
                                    postCreator,
                                    postDate,
                                    postId,
                                    postDescription
                                )
                            )
                        }
                    }
                }
            } else {
                val documents = postList[category]
                if (documents != null) {
                    for (document in documents) {
                        val postTitle = document.data["title"] as String?
                        val postCreator = document.data["nickname"] as String?
                        val postDate = document.data["date"] as String?
                        val postId = document.id
                        val postDescription = document.data["description"] as String?
                        if (postTitle != null && postCreator != null && postDate != null && postDescription != null && postId != null) {
                            postRecyclerViewList.add(
                                PostItemModel(
                                    postTitle,
                                    postCreator,
                                    postDate,
                                    postId,
                                    postDescription
                                )
                            )
                        }
                    }
                }
            }
            if (postRecyclerViewList.isNotEmpty()) {
                val adapter : PostsRecycleViewAdapter = postRecycleView.adapter as PostsRecycleViewAdapter
                adapter.updateData(postRecyclerViewList)
            }
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun getPostsFromFirebase(category:String){
        firestore.collection("forumPosts").document(category)
            .collection("post").get()
            .addOnSuccessListener { documents ->
                postList[category] = documents
                initializeView()
            }
            .addOnFailureListener {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(activity, "Problem with getting posts. Check your internet connection",
                    Toast.LENGTH_SHORT).show()
            }
    }

}