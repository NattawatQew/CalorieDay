package com.eatburn.calorieday

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_gender.*

class GenderActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private lateinit var mDatabase: DatabaseReference
    private val TAG: String = "Gender Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)
        male_btn.setOnClickListener{
            startActivity(Intent(this@GenderActivity, ResultActivity::class.java))
        }
        female_btn.setOnClickListener{
            startActivity(Intent(this@GenderActivity, ResultActivity::class.java))
        }
    }
}
