package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_goal_weight.*
import java.lang.String as String
import kotlin.String as String1

class GoalWeightActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private lateinit var mDatabase: DatabaseReference
    private val TAG: String1 = "Goal Weight Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_weight)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val user = mAuth!!.currentUser
        val uid = user!!.uid
        var weight: kotlin.String? = null

        goal_continueBtn.setOnClickListener {
            val goalWeight = goal_weightEditText.text.toString().trim(){it <= ' '}
            val wantWeight = goal_weightWantEditText.text.toString().trim(){it <= ' '}
            if (goalWeight.isEmpty()){
                Toast.makeText(this, "Please enter your Goal Weight.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Weight was empty!")
                return@setOnClickListener
            }
            if (wantWeight.isEmpty()){
                Toast.makeText(this, "Please enter your Want Weight.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Want Weight was empty!")
                return@setOnClickListener
            }
            if (wantWeight.toFloat() > 1 || wantWeight.toFloat() <= 0){
                Toast.makeText(this, "Please enter Correct weight. (weight have to more than 0 and less than 1)", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Want Weight was incorrect!")
                return@setOnClickListener
            }

            val healthInfoListener = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    weight = dataSnapshot.child(user.uid).child("UserInfo").child("Weight").getValue(toString()::class.java)
                    if (weight!!.toInt() <= goalWeight.toInt() || (weight!!.toInt() - goalWeight.toInt()) <= 21){
                        Toast.makeText(this@GoalWeightActivity, "Please enter Correct Weight. (Goal Weight < Your Current Weight)", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Goal Weight was incorrect!")
                    } else {
                        mDatabase.child(uid).child("UserInfo").child("Goal Weight").setValue(goalWeight)
                        mDatabase.child(uid).child("UserInfo").child("Want Weight").setValue(wantWeight)
                        startActivity(Intent(this@GoalWeightActivity, MyProgramActivity::class.java))
                    }
                }
            }
            mDatabase.addListenerForSingleValueEvent(healthInfoListener)
        }
    }
}
