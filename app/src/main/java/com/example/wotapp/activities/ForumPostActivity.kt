package com.example.wotapp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wotapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.example.wotapp.adapter.MessagesRecycleViewAdapter
import com.example.wotapp.models.recycleViewModels.MessagesItemModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ForumPostActivity : AppCompatActivity() {
    private lateinit var titlePostTextView: TextView
    private lateinit var creatorTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var addMessageEditText: EditText
    private lateinit var addMessageButton: Button
    private lateinit var messageRecycleView: RecyclerView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_post)

        titlePostTextView = findViewById(R.id.titlePostTextView)
        creatorTextView = findViewById(R.id.creatorTextView)
        categoryTextView = findViewById(R.id.categoryTextView)
        dateTextView = findViewById(R.id.dateTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        messageRecycleView = findViewById(R.id.messagesRecycle)
        addMessageEditText = findViewById(R.id.addMessage)
        addMessageButton = findViewById(R.id.addMessageButton)

        val extras = intent.extras
        if (extras != null) {
            titlePostTextView.text = extras.getString("title")
            creatorTextView.text = extras.getString("creator")
            categoryTextView.text = " PostID: " + extras.getString("postId")
            dateTextView.text = extras.getString("date")
            descriptionTextView.text = extras.getString("desc")
        }
        messageRecycleView.layoutManager = LinearLayoutManager(this)
        val postID = extras?.getString("postId")
        if (postID != null) {
            initMessagesTable(postID)
        }

        addMessageButton.setOnClickListener {
            if(addMessageEditText.text != null && addMessageEditText.text.isNotEmpty()
                && extras != null){
                if (postID != null) {
                    firestore.collection("forumPostsDiscussion")
                        .document("messages").collection(postID)
                        .get()
                        .addOnSuccessListener { doc ->
                            addMessageToDataBase(doc.size(),postID)
                        }

                }
            }
        }
    }

    private fun updateView(postId : String ) {
        val adapter = messageRecycleView.adapter as MessagesRecycleViewAdapter
        val messagesModels = ArrayList<MessagesItemModel>()
        firestore.collection("forumPostsDiscussion")
            .document("messages").collection(postId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val message = document.data["message"] as String?
                    val creator = document.data["messageCreator"] as String?
                    val date = document.data["messageDate"] as String?
                    if(message != null && creator != null && date != null){
                        messagesModels.add(MessagesItemModel(message,creator,date))
                    }
                }
                if(messagesModels.isNotEmpty()) {
                    adapter.updateData(messagesModels)
                }

            }
            .addOnFailureListener {
                Toast.makeText(this,"Problem with getting discussion from database!",Toast.LENGTH_SHORT).show()
            }
    }

    private fun initMessagesTable(postID: String){
        val messagesModels = ArrayList<MessagesItemModel>()
        firestore.collection("forumPostsDiscussion")
            .document("messages").collection(postID)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val message = document.data["message"] as String?
                    val creator = document.data["messageCreator"] as String?
                    val date = document.data["messageDate"] as String?
                    if(message != null && creator != null && date != null){
                        messagesModels.add(MessagesItemModel(message,creator,date))
                    }
                }
                if(messagesModels.isNotEmpty()) {
                    initRecycleViewAdapter(messagesModels)
                }else {
                    Toast.makeText(this,"This post have no answers yet! Type first messages!",Toast.LENGTH_LONG).show()
                }

            }
            .addOnFailureListener {
                Toast.makeText(this,"Problem with getting discussion from database!",Toast.LENGTH_SHORT).show()
            }
    }

    private fun initRecycleViewAdapter(messageList : ArrayList<MessagesItemModel>) {
        val adapter = MessagesRecycleViewAdapter(messageList)
        messageRecycleView.adapter = adapter
    }

    private fun addMessageToDataBase(index:Int,postID:String){
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val messageData = hashMapOf(
            "messageDate" to LocalDateTime.now().format(formatter).toString(),
            "messageCreator" to auth.currentUser?.displayName.toString(),
            "message" to addMessageEditText.text.toString(),

        )
        firestore.collection("forumPostsDiscussion")
            .document("messages").collection(postID)
            .document(index.toString()).set(messageData)
            .addOnSuccessListener {
                updateView(postID)
                Toast.makeText(this,"Successfully added message to this post!",
                Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(this,"Problem with adding message to this post!",Toast.LENGTH_SHORT).show() }
    }
}