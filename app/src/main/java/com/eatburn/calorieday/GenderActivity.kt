package com.eatburn.calorieday

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_gender.*

class GenderActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        val user = mAuth!!.currentUser
        val uid = user!!.uid
        male_btn.setOnClickListener{
            mDatabase.child(uid).child("UserInfo").child("Gender").setValue("Male")
            startActivity(Intent(this@GenderActivity, ResultActivity::class.java))
        }
        female_btn.setOnClickListener{
            mDatabase.child(uid).child("UserInfo").child("Gender").setValue("Female")
            startActivity(Intent(this@GenderActivity, ResultActivity::class.java))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) { moveTaskToBack(true) }
        return super.onKeyDown(keyCode, event)
    }
}
