package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_goal_weight.*
import kotlinx.android.synthetic.main.activity_helth_info.*

class GoalWeightActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_weight)
        goal_continueBtn.setOnClickListener {
            startActivity(Intent(this@GoalWeightActivity, ProfileActivity::class.java))
        }
    }
}
