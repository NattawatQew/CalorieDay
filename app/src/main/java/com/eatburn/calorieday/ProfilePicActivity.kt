package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_profile_pic.*

class ProfilePicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_pic)
        pic_continueBtn.setOnClickListener {
            startActivity(Intent(this@ProfilePicActivity, ProfileActivity::class.java))
        }
    }
}
