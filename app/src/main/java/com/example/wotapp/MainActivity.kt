package com.example.wotapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import calculatorWn8.Wn8Calculator
import calculatorWn8.Wn8ExpValue
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import fragments.ClanFragment
import fragments.ForumFragment
import fragments.PlayerFragment
import java.lang.reflect.Type


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val playerFragment = PlayerFragment()
    private val clanFragment = ClanFragment()
    private val forumFragment = ForumFragment()
    private lateinit var bottomNavigationView:BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val values: List<Wn8ExpValue> = readWn8File()
        val calculator = Wn8Calculator(values)
        val bundle = Bundle()
        bundle.putSerializable("calculator",calculator)
        auth = Firebase.auth
        playerFragment.arguments = bundle
        clanFragment.arguments = bundle
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

        Toast.makeText(baseContext, "Hello! "+auth.currentUser!!.displayName,
            Toast.LENGTH_SHORT).show()
        
    }

    private fun replaceFragment(fragment: Fragment){
        val tran = supportFragmentManager.beginTransaction()
        tran.replace(R.id.fragments_view,fragment)
        tran.commit()
    }

    private fun readWn8File():List<Wn8ExpValue>{
        val input = applicationContext.assets.open("wn8exp.json")
        val size = input.available()
        val buffer = ByteArray(size)
        input.read(buffer)
        input.close()
        val wn8text = String(buffer)
        val gson = Gson()
        val typeWN8: Type? = object : TypeToken<List<Wn8ExpValue?>?>() {}.type
        return gson.fromJson(wn8text, typeWN8)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.logout,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.ic_logout->{
                val currentUser = auth.currentUser
                if(currentUser != null){
                    Firebase.auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }



}

