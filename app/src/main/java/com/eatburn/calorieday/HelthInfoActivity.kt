package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_helth_info.*

class HelthInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helth_info)
        health_continueBtn.setOnClickListener {
            startActivity(Intent(this@HelthInfoActivity, ProfileActivity::class.java))
        }
    }
}
