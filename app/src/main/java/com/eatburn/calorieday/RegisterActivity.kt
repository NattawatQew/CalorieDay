package com.eatburn.calorieday

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private lateinit var mDatabase: DatabaseReference
    private val TAG: String = "Register Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this@RegisterActivity, ProfileActivity::class.java))
            finish()
        }

        register_continueBtn.setOnClickListener {
            val username = register_nameEditText.text.toString().trim(){it <= ' '}
            val email = register_emailEditText.text.toString().trim { it <= ' ' }
            val password = register_passwordEditText.text.toString().trim { it <= ' ' }
            val confirmpass = register_passConEditText.text.toString().trim() { it <= ' '}

            if (username.isEmpty()){
                Toast.makeText(this, "Please enter your Username.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Username was empty!")
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your Email Address.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Email was empty!")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter your Password.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Password was empty!")
                return@setOnClickListener
            }
            if (confirmpass.isEmpty()) {
                Toast.makeText(this, "Please enter your Confirm-Password.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Password was empty!")
                return@setOnClickListener
            }

            mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (password.length < 6) {
                        Toast.makeText(this, "Password too short! Please enter minimum 6 characters.", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Enter password less than 6 characters.")
                    }
                    if (password != confirmpass){
                        Toast.makeText(this, "Password doesn't matched with confirm-password.", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Password doesn't matched with confirm-password.")
                    } else {
                        Toast.makeText(this, "Authentication Failed: " + task.exception!!.message, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Authentication Failed: " + task.exception!!.message)
                    }
                } else {
                    val user = mAuth!!.currentUser
                    val uid = user!!.uid
                    mDatabase.child(uid).child("UserInfo").child("Username").setValue(username)
                    Toast.makeText(this, "Create account successfully!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Create account successfully!")
                    startActivity(Intent(this@RegisterActivity, GenderActivity::class.java))
                    finish()
                }
            }
        }
    }
}
