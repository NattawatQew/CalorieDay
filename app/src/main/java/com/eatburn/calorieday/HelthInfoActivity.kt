package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_helth_info.*

class HelthInfoActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private lateinit var mDatabase: DatabaseReference
    private val TAG: String = "Health Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helth_info)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        health_continueBtn.setOnClickListener {
            val age = health_ageEditText.text.toString().trim(){it <= ' '}
            val height = health_heightEditText.text.toString().trim(){it <= ' '}
            val weight = health_weightEditText.text.toString().trim(){it <= ' '}

            if (age.isEmpty()){
                Toast.makeText(this, "Please enter your Age.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Age was empty!")
                return@setOnClickListener
            }
            if (height.isEmpty()){
                Toast.makeText(this, "Please enter your Height.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Height was empty!")
                return@setOnClickListener
            }
            if (weight.isEmpty()){
                Toast.makeText(this, "Please enter your Weight.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Weight was empty!")
                return@setOnClickListener
            }
            val user = mAuth!!.currentUser
            val uid = user!!.uid
            mDatabase.child(uid).child("UserInfo").child("Age").setValue(age)
            mDatabase.child(uid).child("UserInfo").child("Height").setValue(height)
            mDatabase.child(uid).child("UserInfo").child("Weight").setValue(weight)
            startActivity(Intent(this@HelthInfoActivity, GoalWeightActivity::class.java))
        }
    }
}
