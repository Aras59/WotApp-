package com.example.wotapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextNickname: EditText
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        signUpButton = findViewById(R.id.signUpButton)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextNickname = findViewById(R.id.editTextNickname)

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
                                createAccount(email,password,nickname)
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


    private fun createAccount(email: String, password: String, nickname: String) {
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
                        "uID" to user.uid
                    )

                    val usersFirestoreDataBase = FirebaseFirestore.getInstance()
                    usersFirestoreDataBase.collection("Users")
                        .document(nickname).set(userCredential)

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