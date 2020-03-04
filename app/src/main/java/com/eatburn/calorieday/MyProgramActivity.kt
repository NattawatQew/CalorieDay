package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_goal_weight.*
import kotlinx.android.synthetic.main.activity_my_program.*

class MyProgramActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_program)
        myprogram_continueBtn.setOnClickListener {
            startActivity(Intent(this@MyProgramActivity, ReadyActivity::class.java))
        }
    }
}
