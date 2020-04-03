package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_goal_weight.*
import kotlinx.android.synthetic.main.activity_my_program.*
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

class MyProgramActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_program)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val user = mAuth!!.currentUser
        val uid = user!!.uid
        var BMR = 0
        var BMI: Double?
        var cal = 0
        var weight: String?
        var age: String?
        var height: String?
        var gender: String?
        var weightlose: String?
        var goalWeight: String?
        var days = 0

        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())

        val currentDatemil = Calendar.getInstance()
        currentDatemil[Calendar.HOUR] = 0
        currentDatemil[Calendar.MINUTE] = 0
        currentDatemil[Calendar.SECOND] = 0
        currentDatemil[Calendar.MILLISECOND] = 0

        val calroryListener = object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {}
            override fun onDataChange(dataSnapshot: DataSnapshot){
//                pull the value from the database by using dataSnapshot and getValue
                gender = dataSnapshot.child(uid).child("UserInfo").child("Gender").getValue(toString()::class.java)
                age = dataSnapshot.child(uid).child("UserInfo").child("Age").getValue(toString()::class.java)
                weight = dataSnapshot.child(uid).child("UserInfo").child("Weight").getValue(toString()::class.java)
                height = dataSnapshot.child(uid).child("UserInfo").child("Height").getValue(toString()::class.java)
                weightlose = dataSnapshot.child(uid).child("UserInfo").child("Want Weight").getValue(toString()::class.java)
                goalWeight = dataSnapshot.child(uid).child("UserInfo").child("Goal Weight").getValue(toString()::class.java)
//                calcurate the BMI and cal per day
                BMI = (weight!!.toInt() / ((height!!.toInt().toDouble())/100).pow(2.0)).round(2)
//                For Male
                if (gender.equals("Male")){
                    BMR = (66 + (13.7 * weight!!.toInt()) + (5 * height!!.toInt()) - (6.8 * age!!.toInt())).roundToInt()
                    days = ((weight!!.toInt() - goalWeight!!.toInt()) / weightlose!!.toFloat()).roundToInt() * 7
                    cal = ((BMR * 1.3) - (7700 * (weight!!.toInt() - goalWeight!!.toInt())) / days).roundToInt()
                    myprogram_cal.text = cal.toString()
                    myprogram_days.text = days.toString()
                    mDatabase.child(uid).child("Calories").child("Cal per day").setValue(cal)
                    mDatabase.child(uid).child("Calories").child("Days Need").setValue(days)
                } else {
                    BMR = (665 + (9.6 * weight!!.toInt()) + (1.8 * height!!.toInt()) - (4.7 * age!!.toInt())).roundToInt()
                    days = ((weight!!.toInt() - goalWeight!!.toInt()) / weightlose!!.toFloat()).roundToInt() * 7
                    cal = ((BMR * 1.3) - (7700 * (weight!!.toInt() - goalWeight!!.toInt())) / days).roundToInt()
                    myprogram_cal.text = cal.toString()
                    myprogram_days.text = days.toString()
                    mDatabase.child(uid).child("Calories").child("Cal per day").setValue(cal)
                    mDatabase.child(uid).child("Calories").child("Days Need").setValue(days)
                }
                mDatabase.child(uid).child("Calories").child("BMI").setValue(BMI)
                mDatabase.child(user!!.uid).child("Calories").child("Days Start").setValue(currentDate)
                mDatabase.child(user!!.uid).child("Calories").child("Days Start in mil").setValue(currentDatemil.timeInMillis)
            }
        }
        mDatabase.addValueEventListener(calroryListener)
        myprogram_continueBtn.setOnClickListener {
            startActivity(Intent(this@MyProgramActivity, ReadyActivity::class.java))
        }
    }
}

// calculate 2 decimal
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}
