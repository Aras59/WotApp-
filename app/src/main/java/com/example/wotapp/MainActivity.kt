package com.example.wotapp

import accounts.Players
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner:Spinner = findViewById(R.id.spinner)
        if(spinner != null) {
            val adapter = ArrayAdapter.createFromResource(this,R.array.regions, android.R.layout.simple_dropdown_item_1line)
            spinner.adapter = adapter
        }
        val nicknamePlain:EditText = findViewById(R.id.nicknamePlain)
        val searchButton:Button = findViewById(R.id.searchButton)
    }

    fun onClick(){

    }
}