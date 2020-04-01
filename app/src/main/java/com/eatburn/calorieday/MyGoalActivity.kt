package com.eatburn.calorieday

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_my_goal.*
import kotlinx.android.synthetic.main.activity_profile.*

class MyGoalActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var mDatabase: DatabaseReference
    private val TAG:String = "My Goal Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_goal)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        val user = mAuth!!.currentUser
        val userInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                get value form real time database into the string
                mygoal_dayData.text = dataSnapshot.child(user!!.uid).child("Calories").child("Days Need").getValue(
                    Double::class.java).toString()
                mygoal_weightData.text = dataSnapshot.child(user!!.uid).child("UserInfo").child("Goal Weight").getValue(
                    String::class.java)
                mygoal_calData.text = dataSnapshot.child(user!!.uid).child("Calories").child("Cal per day").getValue(
                    Double::class.java).toString()
            }
        }
        mDatabase.addValueEventListener(userInfoListener)
    }
}
