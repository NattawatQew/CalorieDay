package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_on.*

class AddOnActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_on)


        addOn_mygoalBtn.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, MyGoalActivity::class.java))
        }
        addOn_homeBtn.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, HomeActivity::class.java))
        }
        addOn_profileBtn.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, ProfileActivity::class.java))
        }
        addOn_breakfast.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, BreakfastActivity::class.java))
        }
        addOn_lunch.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, LunchActivity::class.java))
        }
        addOn_dinner.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, DinnerActivity::class.java))
        }
        addOn_exercise.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, ExerciseActivity::class.java))
        }
        addOn_bookBtn.setOnClickListener{
            startActivity(Intent(this@AddOnActivity, OnTrackActivity::class.java))
        }
    }
}
