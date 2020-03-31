package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_my_program.*
import kotlinx.android.synthetic.main.activity_ready.*

class ReadyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ready)
        ready_continueBtn.setOnClickListener {
            startActivity(Intent(this@ReadyActivity, HomeActivity::class.java))
        }
    }
}
