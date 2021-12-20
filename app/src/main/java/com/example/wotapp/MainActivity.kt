package com.example.wotapp

import accounts.*
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ArrayAdapter
import android.widget.TextView

import android.text.Editable

import android.text.TextWatcher
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fragments.ClanFragment
import fragments.ForumFragment
import fragments.PlayerFragment


class MainActivity : AppCompatActivity() {
    private val nickLenght:Int = 2;
    private lateinit var spinner:Spinner
    private lateinit var adapter:Adapter

    private lateinit var searchButton:Button
    private lateinit var textView: TextView
    private lateinit var nicknameSearch:AutoCompleteTextView
    private var nickname:String = ""
    private var account_id:String = ""
    private val playerFragment = PlayerFragment()
    private val clanFragment = ClanFragment()
    private val forumFragment = ForumFragment()
    private lateinit var bottomNavigationView:BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_nav)
        replaceFragment(playerFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_player -> replaceFragment(playerFragment)
                R.id.ic_clan -> replaceFragment(clanFragment)
                R.id.ic_forum -> replaceFragment(forumFragment)
            }
            true
        }
        
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment!=null){
            val tran = supportFragmentManager.beginTransaction()
            tran.replace(R.id.fragments_view,fragment)
            tran.commit()
        }
    }





}