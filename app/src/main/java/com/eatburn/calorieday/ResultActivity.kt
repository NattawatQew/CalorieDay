package com.eatburn.calorieday

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var mDatabase: DatabaseReference
    private val TAG:String = "Result Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val user = mAuth!!.currentUser

        result_emailData.text = user!!.email
        result_uidData.text = user.uid

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users == null) {
                startActivity(Intent(this@ResultActivity, LoginActivity::class.java))
                finish()
            }
        }

        val userInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                result_signOutBtn.setOnClickListener {
                    mAuth!!.signOut()
                    startActivity(Intent(this@ResultActivity, MainActivity::class.java))
                    finish()
                }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                result_nameData.text = dataSnapshot.child(user.uid).child("UserInfo").child("Username").getValue(
                    String::class.java)
                result_genderData.text = dataSnapshot.child(user.uid).child("UserInfo").child("Gender").getValue(
                    String::class.java)
            }

        }
        mDatabase.addValueEventListener(userInfoListener)

        result_signOutBtn.setOnClickListener {
            mAuth!!.signOut()
            Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Signed out!")
            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener { mAuthListener }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) { mAuth!!.removeAuthStateListener { mAuthListener } }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { moveTaskToBack(true) }
        return super.onKeyDown(keyCode, event)
    }
}

