package com.example.wotapp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.wotapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class NewPostForumActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var headerPostText: EditText
    private lateinit var postDescriptionText: EditText
    private lateinit var postButton:Button
    private lateinit var category: Spinner
    private lateinit var categorySpinnerAdapter: ArrayAdapter<CharSequence>
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post_forum)
        auth = Firebase.auth
        headerPostText = findViewById(R.id.headerPostText)
        category = findViewById(R.id.categorySpinner)
        postDescriptionText = findViewById(R.id.postDescritpionText)
        postButton = findViewById(R.id.addPostButton)
        categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
            R.array.categoryWithOutAll,
            R.layout.spinner_category
        )
        category.adapter = categorySpinnerAdapter

        postButton.setOnClickListener {
            if(auth.currentUser != null) {
                if(headerPostText.text.isEmpty() || postDescriptionText.text.isEmpty()){
                    Toast.makeText(this,"You must add header and description!",Toast.LENGTH_SHORT).show()
                }else {
                    firestore.collection("Users")
                        .document("EU").collection(auth.currentUser!!.displayName.toString())
                        .document("data").get()
                        .addOnCompleteListener {
                                task ->
                            if (task.isSuccessful && task.result.data != null) {
                                val accountID = task.result.data!!.get("accountID").toString()
                                val nickname = task.result.data!!.get("nickname").toString()
                                val server = task.result.data!!.get("server").toString()
                                val formatted = LocalDateTime.now()
                                    .format(DateTimeFormatter.ofPattern("EE MMM dd yyyy"))
                                val postData = hashMapOf(
                                    "accountID" to accountID,
                                    "nickname" to nickname,
                                    "server" to server,
                                    "date" to formatted,
                                    "title" to headerPostText.text.toString(),
                                    "description" to postDescriptionText.text.toString()
                                )
                                createPost(postData,category.selectedItem.toString())
                            }

                        }
                }
            }
        }
    }

    private fun createPost(data: HashMap<String,String>,category:String){
        val generatePostId = kotlin.math.abs(
            Random(
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            ).nextLong())

        firestore.collection("forumPosts").document(category)
            .collection("post").document(generatePostId.toString()).set(data)
            .addOnSuccessListener {
                Toast.makeText(baseContext, "Post created!.",
                Toast.LENGTH_SHORT).show()
                this.finish()
            }
            .addOnFailureListener {  Toast.makeText(baseContext, "Problem with creating post. Check your internet connection",
                Toast.LENGTH_SHORT).show() }

    }


}