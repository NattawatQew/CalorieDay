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
//    TAG for toast (pop-up message at bottom)
    private val TAG: String1 = "Goal Weight Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_weight)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val user = mAuth!!.currentUser
        val uid = user!!.uid
        var weight: kotlin.String?

        goal_continueBtn.setOnClickListener {
//            declare the variable into the string and trim by space
            val goalWeight = goal_weightEditText.text.toString().trim(){it <= ' '}
            val wantWeight = goal_weightWantEditText.text.toString().trim(){it <= ' '}
//            if the input text is empty show the error bt toast
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

//            For the real-time database
            val healthInfoListener = object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {}
                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    pull the value from the database by using dataSnapshot and getValue
                    weight = dataSnapshot.child(user.uid).child("UserInfo").child("Weight").getValue(toString()::class.java)
//                    change the string to int and check the condition then toast
                    if (weight!!.toInt() <= goalWeight.toInt()){
                        Toast.makeText(this@GoalWeightActivity, "Please enter Correct Weight. (Goal Weight < Your Current Weight)", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Goal Weight was incorrect!")
                    }
                    if ((weight!!.toInt() - goalWeight.toInt()) >= 21) {
                        Toast.makeText(this@GoalWeightActivity, "Please enter Correct Weight. (Goal Weight is too less)", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Goal Weight was incorrect!")
                    }
//                    If pass the condition it will add the new value to real time database.
                    else {
                        mDatabase.child(uid).child("UserInfo").child("Goal Weight").setValue(goalWeight)
                        mDatabase.child(uid).child("UserInfo").child("Want Weight").setValue(wantWeight)
                        startActivity(Intent(this@GoalWeightActivity, MyProgramActivity::class.java))
                    }
                }
            }
//            add value into database
            mDatabase.addListenerForSingleValueEvent(healthInfoListener)
        }
    }
}
