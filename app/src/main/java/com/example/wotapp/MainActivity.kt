package com.example.wotapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    private lateinit var url: URL
    override fun onCreate(savedInstanceState: Bundle?) {
        url = URL("https://api.worldoftanks.eu/wot/account/list/?application_id=098b54f4d269cc5f29f074e671fdcc00&search=Lody_z_posypkom")
        val myConnection: HttpsURLConnection = url.openConnection() as HttpsURLConnection
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

o