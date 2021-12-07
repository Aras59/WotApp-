package com.example.wotapp

import accounts.*
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


class MainActivity : AppCompatActivity() {
    private lateinit var spinner:Spinner
    private lateinit var adapter:Adapter

    private lateinit var searchButton:Button
    private lateinit var textView: TextView
    private lateinit var nicknameSearch:AutoCompleteTextView
    private lateinit var nickname:String
    private lateinit var account_id:String


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
        nicknameSearch = findViewById(R.id.autoCompleteTextView2)
        var playersInterface = PlayersInterface.create().getPlayers("")

        nicknameSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s.length>3){
                    playersInterface = PlayersInterface.create().getPlayers(nicknameSearch.text.toString())

                    playersInterface.enqueue(object : Callback<PlayersList>{
                        override fun onResponse(call: Call<PlayersList>, response: Response<PlayersList>) {
                            var nicknames: List<String> = emptyList()
                            nicknames = response.body()?.data?.map{it.nickname}!!
                            var nicknamesAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, nicknames)
                            nicknameSearch.setAdapter(nicknamesAdapter)
                            account_id = response.body()?.data?.first()?.account_id.toString()!!
                            nickname= response.body()?.data?.first()?.nickname!!
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