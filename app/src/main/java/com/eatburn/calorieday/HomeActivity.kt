package com.eatburn.calorieday

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var mDatabase: DatabaseReference
    private val TAG:String = "Home Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val user = mAuth!!.currentUser
        val currentDate = Calendar.getInstance()
        var water: Long?

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users == null) {
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
            }
        }

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val date: String = sdf.format(Date())
        mDatabase.child(user!!.uid).child(date).child("Water").setValue(0)

        val userInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                get value form real time database into the string
                home_nameData.text = dataSnapshot.child(user!!.uid).child("UserInfo").child("Username").getValue(
                    String::class.java)
                home_calData.text = dataSnapshot.child(user.uid).child("Calories").child("Cal per day").getValue(
                    Double::class.java)?.toInt().toString()
                home_waterData.text = dataSnapshot.child(user.uid).child(date).child("Water").getValue(Double::class.java)?.toInt().toString()
            }
        }
        mDatabase.addValueEventListener(userInfoListener)

        var time = currentDate.get(Calendar.HOUR_OF_DAY);

        if(time in 5..11) {
            home_timeData.text = "GOOD MORNING"
        }
        if(time in 12..17) {
            home_timeData.text = "GOOD AFTERNOON"
        }
        if(time in 18..23) {
            home_timeData.text = "GOOD EVENING"
        }
        if(time in 0..4) {
            home_timeData.text = "GOODNIGHT"
        }

        home_profileBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        }
        home_ontrackBtn.setOnClickListener{
            startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
        }
        home_mygoalBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MyGoalActivity::class.java))
        }
        home_addBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, AddOnActivity::class.java))
        }
        val waterListener = object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot){
//                pull the value from the database by using dataSnapshot and getValue
                water = dataSnapshot.child(user!!.uid).child(date).child("Water").value as Long?
                home_plusWater.setOnClickListener {
                    water = water?.plus(1)
                    mDatabase.child(user.uid).child(date).child("Water").setValue(water)
                    //startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
                    if (water!! > 8){
                        water = 8
                        mDatabase.child(user.uid).child(date).child("Water").setValue(water)
                        Toast.makeText(this@HomeActivity, "You already drank 8 glasses of water", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "already drank 8 glasses of water")
                        return@setOnClickListener
                    }
                    return@setOnClickListener
                }
                home_MinusWater.setOnClickListener {
                    water = water?.minus(1)
                    mDatabase.child(user.uid).child(date).child("Water").setValue(water)
                    //startActivity(Intent(this@HomeActivity, HomeActivity::class.java))
                    if (water!! < 0){
                        water = 0
                        mDatabase.child(user.uid).child(date).child("Water").setValue(water)
                        Toast.makeText(this@HomeActivity, "Must have at least 0 glasses of water", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Must have at least 0 glasses of water")
                        return@setOnClickListener
                    }
                    return@setOnClickListener
                }
            }
        }
        mDatabase.addValueEventListener(waterListener)
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

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}
