package com.example.wotapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ForumPostActivity : AppCompatActivity() {
    private lateinit var titlePostTextView: TextView
    private lateinit var creatorTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var descriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_post)

        titlePostTextView = findViewById(R.id.titlePostTextView)
        creatorTextView = findViewById(R.id.creatorTextView)
        categoryTextView = findViewById(R.id.categoryTextView)
        dateTextView = findViewById(R.id.dateTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        val extras = intent.extras
        if (extras != null) {
            titlePostTextView.text = extras.getString("title")
            creatorTextView.text = extras.getString("creator")
            categoryTextView.text = " PostID: " + extras.getString("postId")
            dateTextView.text = extras.getString("date")
            descriptionTextView.text = extras.getString("desc")
        }


    }
}