package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_breakfast.*
import java.text.SimpleDateFormat
import java.util.*

class BreakfastActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    lateinit var mDatabase: DatabaseReference
    private val TAG:String = "Breakfast Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breakfast)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val user = mAuth!!.currentUser
        val currentDate = Calendar.getInstance()
        var hour = currentDate.get(Calendar.HOUR_OF_DAY);
        var min = currentDate.get(Calendar.MINUTE);

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users == null) {
                startActivity(Intent(this@BreakfastActivity, LoginActivity::class.java))
                finish()
            }
        }

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val date: String = sdf.format(Date())

        breakfast_submitBtn.setOnClickListener {
            val menu = breakfast_menuEditText.text.toString().trim(){it <= ' '}
            val cal = breakfast_calEditText.text.toString().trim(){it <= ' '}
            if (menu.isEmpty()){
                Toast.makeText(this, "Please enter your Menu.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Menu was empty!")
                return@setOnClickListener
            }
            if (cal.isEmpty()){
                Toast.makeText(this, "Please enter your Calories.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Cal was empty!")
                return@setOnClickListener
            }
            mDatabase.child(user!!.uid).child(date).child("Breakfast").child("Menu").setValue(menu)
            mDatabase.child(user!!.uid).child(date).child("Breakfast").child("Calories").setValue(cal)
            startActivity(Intent(this@BreakfastActivity, HomeActivity::class.java))
        }
    }
}
