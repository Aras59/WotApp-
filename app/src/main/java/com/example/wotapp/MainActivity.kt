package com.example.wotapp

import accounts.PList
import accounts.Players
import accounts.PlayersInterface
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.doOnTextChanged
import android.widget.TextView

import android.text.Editable

import android.text.TextWatcher
import android.view.View


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner:Spinner = findViewById(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(this,R.array.regions, android.R.layout.simple_dropdown_item_1line)
        if(spinner != null) {
            spinner.adapter = adapter
        }
        val searchButton:Button = findViewById(R.id.searchButton)
        val textView:TextView = findViewById(R.id.textview)
        val nicknameSearch: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView2)
        var playersInterface = PlayersInterface.create().getPlayers("Lody_z_posypkom")

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

                    playersInterface.enqueue(object : Callback<PList>{
                        override fun onResponse(call: Call<PList>, response: Response<PList>) {
                            var nicknames: List<String> = emptyList()
                            nicknames = response.body()?.data?.map{it.nickname}!!
                            var nicknamesAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, nicknames)
                            nicknameSearch.setAdapter(nicknamesAdapter)
                            //textView.text = response.code().toString()


                        }

                        override fun onFailure(call: Call<PList>, t: Throwable) {
                            textView.text = "Problem z internetem!"
                        }

                    })
                    textView.text = nicknameSearch.text
                }

            }
        })



    }

    private val COUNTRIES = arrayOf(
        "Belgium", "France", "Italy", "Germany", "Spain"
    )


}