package com.eatburn.calorieday

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Main Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
//        set theme back to normal
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null) {
            Log.d(TAG, "Continue with: " + mAuth!!.currentUser!!.email)
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        }

        main_btn.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }

        signin_btn.setOnClickListener { startActivity(Intent(this@MainActivity, LoginActivity::class.java)) }
    }
}