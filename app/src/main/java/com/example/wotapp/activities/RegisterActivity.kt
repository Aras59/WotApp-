package com.example.wotapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.wotapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.example.wotapp.interfaces.PlayersInterface
import com.example.wotapp.models.playerInfo.PlayerInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextNickname: EditText
    private lateinit var signUpButton: Button
    private lateinit var spinner: Spinner
    private lateinit var serverSpinnerAdapter: ArrayAdapter<CharSequence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        signUpButton = findViewById(R.id.signUpButton)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextNickname = findViewById(R.id.editTextNickname)
        spinner = findViewById(R.id.spinner)
        serverSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.regions
            , R.layout.spinner_list
        )
        serverSpinnerAdapter.setDropDownViewResource(R.layout.spinner_list)
        spinner.adapter = serverSpinnerAdapter

        signUpButton.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val nickname = editTextNickname.text.toString()
            val usersFirestoreDataBase = FirebaseFirestore.getInstance()
            val name = nickname.lowercase().replaceFirstChar { it.uppercase() }
            usersFirestoreDataBase.collection("Users").document(name).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if(!document.exists()) {
                            if(email!="" && password !="" && nickname!="") {
                                createAccount(email,password,nickname,spinner.selectedItem.toString())
                            }
                        }else{
                            Toast.makeText(baseContext, "Authentication failed. User with this nickname already exist!",
                                Toast.LENGTH_LONG).show()
                        }
                    }else {
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
            }

        }

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
    private fun createAccount(email: String, password: String, nickname: String, server: String) {
        val firestore = FirebaseFirestore.getInstance()
        val playersInterface = when(server){
            "EU" -> {
                PlayersInterface.createEU()
            }
            "RU" -> {
                PlayersInterface.createRU()
            }
            "ASIA" -> {
                PlayersInterface.createASIA()
            }
            else -> {
                PlayersInterface.createNA()
            }
        }

        val getPlayers = playersInterface.getPlayers(nickname)
        getPlayers.enqueue(object : Callback<PlayerInfo> {
            override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                if (response.body()?.status != "error" && response.body()?.meta?.count != 0) {
                    val accountId = response.body()?.data?.first()?.account_id.toString()
                    val nickname = response.body()?.data?.first()?.nickname!!
                    val formatted = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("EE MMM dd yyyy"))

                    val followedUserData = hashMapOf(
                        "account_id" to accountId.toInt(),
                        "nickname" to nickname,
                        "server" to server,
                        "followingdate" to formatted
                    )
                    firestore.collection("followedusers").document(accountId)
                        .set(followedUserData)
                    firestore.collection("followingusers").document(nickname)
                        .collection(server).document(accountId).set(followedUserData)
                    createUser(email, password, server, nickname, firestore)
                }
                else {
                    Toast.makeText(baseContext, "Account with this username not exist in WOT database.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                Toast.makeText(baseContext, "Problem with internet connection!",
                    Toast.LENGTH_SHORT).show()
            }
        })



    }

    private fun createUser(email: String, password: String, server: String, nickname: String, firestore: FirebaseFirestore){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nickname)
                        .build()
                    user!!.updateProfile(profileUpdates)
                    val userCredential = hashMapOf(
                        "email" to email,
                        "nickname" to nickname,
                        "server" to server,
                        "uID" to user.uid
                    )


                    firestore.collection("Users").document(server)
                        .collection(nickname).document("data").set(userCredential)

                    Toast.makeText(baseContext, "Authentication Success.",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}