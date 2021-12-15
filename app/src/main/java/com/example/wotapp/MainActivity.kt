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
        spinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(this,R.array.regions, android.R.layout.simple_dropdown_item_1line)
        if(spinner != null) {
            spinner.adapter = adapter
        }
        searchButton = findViewById(R.id.searchButton)
        textView = findViewById(R.id.textview)
        nicknameSearch = findViewById(R.id.autoCompleteTextView)

        replaceFragment(playerFragment)
        bottomNavigationView = findViewById(R.id.bottom_nav)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_player -> replaceFragment(playerFragment)
                R.id.ic_clan -> replaceFragment(clanFragment)
                R.id.ic_forum -> replaceFragment(forumFragment)
            }
            true
        }


        var playersInterface = PlayersInterface.create().getPlayers("")

        nicknameSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.length>nickLenght){
                    playersInterface = PlayersInterface.create().getPlayers(nicknameSearch.text.toString())

                    playersInterface.enqueue(object : Callback<PlayersList>{
                        override fun onResponse(call: Call<PlayersList>, response: Response<PlayersList>) {
                            if(!response.body()?.data!!.isEmpty()){
                                var nicknames: List<String> = emptyList()
                                nicknames = response.body()?.data?.map{it.nickname}!!
                                var nicknamesAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, nicknames)
                                nicknameSearch.setAdapter(nicknamesAdapter)
                                account_id = response.body()?.data?.first()?.account_id.toString()!!
                                nickname= response.body()?.data?.first()?.nickname!!
                            }
                        }
                        override fun onFailure(call: Call<PlayersList>, t: Throwable) {
                            account_id = ""
                            nickname = ""
                            textView.text = "Problem z internetem!"
                        }
                    })
                    textView.text = nicknameSearch.text
                }
            }
        })

        searchButton.setOnClickListener {
            if(nicknameSearch.text.length>nickLenght){
                var playersPersonalDataInterface = PlayersPersonalData.create().getPlayerPersonalData(account_id)

                playersPersonalDataInterface.enqueue(object : Callback<Datas>{
                    override fun onResponse(call: Call<Datas>, response: Response<Datas>) {
                        var player: Player? = response.body()?.player?.get(account_id)
                        textView.text = (player?.statistics?.all?.damage_dealt!! / player?.statistics?.all?.battles).toString()
                    }

                    override fun onFailure(call: Call<Datas>, t: Throwable) {
                        Toast.makeText(applicationContext,"Błąd danych",Toast.LENGTH_SHORT).show()
                    }

                })
            }
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