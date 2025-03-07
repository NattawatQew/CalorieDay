package com.eatburn.calorieday

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_goal.*
import java.util.*

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
        mDatabase.keepSynced(true)

        var dayNeed: String?
        var dayStart: String?

        val user = mAuth!!.currentUser
        val userInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                get value form real time database into the string
                mygoal_weightData.text = dataSnapshot.child(user!!.uid).child("UserInfo").child("Goal Weight").getValue(
                    String::class.java)
                mygoal_calData.text = dataSnapshot.child(user!!.uid).child("Calories").child("Cal per day").getValue(
                    Double::class.java).toString()
            }
        }
        mDatabase.addValueEventListener(userInfoListener)

        val dayListener = object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot){
//                pull the value from the database by using dataSnapshot and getValue
                dayNeed =
                    dataSnapshot.child(user!!.uid).child("Calories").child("Days Need").value?.toString()
                dayStart =
                    dataSnapshot.child(user!!.uid).child("Calories").child("Days Start in mil").value?.toString()
                // Set Current Date
                val currentDate = Calendar.getInstance()
//                    calculate event date by plus with the day need in milli second
                val eventDate = dayStart!!.toLong() + (dayNeed!!.toLong() * (24*60*60*1000))
                var dayleft = ((eventDate - currentDate.timeInMillis) / (24*60*60*1000)) + 1
                if (dayleft < 0){
                    dayleft = 0
                }
                mygoal_dayData.text = dayleft.toString()
                mDatabase.child(user!!.uid).child("Calories").child("Days Remaining").setValue(dayleft)
            }
        }
        mDatabase.addValueEventListener(dayListener)

        mygoal_profileBtn.setOnClickListener {
            startActivity(Intent(this@MyGoalActivity, ProfileActivity::class.java))
            finish()
        }
        mygoal_homeBtn.setOnClickListener {
            startActivity(Intent(this@MyGoalActivity, HomeActivity::class.java))
            finish()
        }
        mygoal_addBtn.setOnClickListener {
            startActivity(Intent(this@MyGoalActivity, AddOnActivity::class.java))
            finish()
        }
        mygoal_bookBtn.setOnClickListener{
            Dialog_Choice()
        }
    }

    private fun Dialog_Choice() {
        val builder = AlertDialog.Builder(this@MyGoalActivity)
        builder.setTitle(getString(R.string.food_ex))
        // Display a message on alert dialog
        builder.setMessage(getString(R.string.popup_choice))
        builder.setPositiveButton(getString(R.string.food)){ dialog, which ->
            startActivity(Intent(this@MyGoalActivity, OnTrackActivity::class.java))
            finish()
        }
        // Display a negative button on alert dialog
        builder.setNegativeButton(getString(R.string.ex)){ dialog, which ->
            startActivity(Intent(this@MyGoalActivity, OnExerciseActivity::class.java))
            finish()
        }
        // Display a neutral button on alert dialog
        builder.setNeutralButton(R.string.cancel){_,_ ->
            Toast.makeText(applicationContext,
                R.string.cancel, Toast.LENGTH_SHORT).show()
        }
        // Display the alert dialog on app interface
        val dialog: AlertDialog = builder.create()
        dialog.setIcon(R.mipmap.ic_launcher)
        dialog.show()
        return
    }
}


