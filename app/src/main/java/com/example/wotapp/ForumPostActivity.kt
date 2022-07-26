package com.example.wotapp

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ForumPostActivity : AppCompatActivity() {
    private lateinit var titlePostTextView: TextView
    private lateinit var creatorTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var discussionTable: TableLayout
    private lateinit var addMessageEditText: EditText
    private lateinit var addMessageButton: Button
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
        discussionTable = findViewById(R.id.discusionTable)
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
        val postID = extras?.getString("postId")
        if (postID != null) {
            initMessagesTable(postID)
        }

        addMessageButton.setOnClickListener {
            if(addMessageEditText.text != null && addMessageEditText.text.isNotEmpty()
                && extras != null){
                val postID = extras.getString("postId")
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

    private fun initMessagesTable(postID: String){
        firestore.collection("forumPostsDiscussion")
            .document("messages").collection(postID)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val messageCreator = document.data["messageCreator"] as String
                    if(messageCreator == auth.currentUser?.displayName) {
                        val tableRow = TableRow(this)
                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                        val param = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        ).apply {
                            gravity = Gravity.RIGHT
                        }
                        param.setMargins(0,0,20,0);
                        tableRow.layoutParams = params

                        val paramsForLayout = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,1.0f)

                        val firstLayout = LinearLayout(this)
                        firstLayout.layoutParams = paramsForLayout
                        firstLayout.orientation = LinearLayout.VERTICAL
                        firstLayout.background = resources.getDrawable(R.drawable.radiusblueborder)

                        val messageTextView = TextView(this)
                        messageTextView.setTextColor(Color.WHITE)

                        messageTextView.layoutParams = param
                        messageTextView.textSize = 20.0f
                        messageTextView.text = document.data["message"] as String?

                        val messageDate = TextView(this)
                        messageDate.setTextColor(Color.GRAY)
                        messageDate.layoutParams = param
                        messageDate.textSize = 15.0f
                        messageDate.text = document.data["messageDate"] as String?

                        val creatorPost = TextView(this)
                        creatorPost.setTextColor(Color.WHITE)
                        creatorPost.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                        creatorPost.textSize = 25.0f
                        creatorPost.text = document.data["messageCreator"] as String?

                        val secondLayout = LinearLayout(this)
                        secondLayout.orientation = LinearLayout.VERTICAL

                        firstLayout.addView(messageTextView)
                        firstLayout.addView(messageDate)
                        tableRow.addView(secondLayout ,200,150)
                        tableRow.addView(firstLayout,850,150)

                        discussionTable.addView(tableRow)
                    } else {

                        val tableRow = TableRow(this)
                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                        val param = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        ).apply {
                            gravity = Gravity.LEFT
                        }
                        param.setMargins(20,0,0,0);
                        tableRow.layoutParams = params

                        val paramsForLayout = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,1.0f)

                        val firstLayout = LinearLayout(this)
                        firstLayout.layoutParams = paramsForLayout
                        firstLayout.orientation = LinearLayout.VERTICAL
                        firstLayout.background = resources.getDrawable(R.drawable.radiuscolorborder)

                        val messageTextView = TextView(this)
                        messageTextView.setTextColor(Color.WHITE)
                        messageTextView.layoutParams = param
                        messageTextView.textSize = 20.0f
                        messageTextView.text = document.data["message"] as String?

                        val messageDate = TextView(this)
                        messageDate.setTextColor(Color.GRAY)
                        messageDate.layoutParams = param
                        messageDate.textSize = 15.0f
                        messageDate.text = document.data["messageDate"] as String?

                        val creatorPost = TextView(this)
                        creatorPost.setTextColor(Color.GRAY)
                        creatorPost.layoutParams = param
                        creatorPost.textSize = 17.0f
                        creatorPost.text = document.data["messageCreator"] as String?

                        val secondLayout = LinearLayout(this)
                        secondLayout.orientation = LinearLayout.VERTICAL


                        firstLayout.addView(messageTextView)
                        firstLayout.addView(creatorPost)
                        firstLayout.addView(messageDate)
                        tableRow.addView(firstLayout,850,220)
                        tableRow.addView(secondLayout ,200,220)

                        discussionTable.addView(tableRow)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"Problem with getting discussion from database!",Toast.LENGTH_SHORT).show()
            }
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
            .addOnSuccessListener { Toast.makeText(this,"Successfully added message to this post!",
                Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(this,"Problem with adding message to this post!",Toast.LENGTH_SHORT).show() }
    }
}