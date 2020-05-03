package com.eatburn.calorieday

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_add_on.*

class AddOnActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_on)


        addOn_mygoalBtn.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, MyGoalActivity::class.java))
            finish()
        }
        addOn_homeBtn.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, HomeActivity::class.java))
            finish()
        }
        addOn_profileBtn.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, ProfileActivity::class.java))
            finish()
        }
        addOn_breakfast.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, BreakfastActivity::class.java))
            finish()
        }
        addOn_lunch.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, LunchActivity::class.java))
            finish()
        }
        addOn_dinner.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, DinnerActivity::class.java))
            finish()
        }
        addOn_exercise.setOnClickListener {
            startActivity(Intent(this@AddOnActivity, ExerciseActivity::class.java))
            finish()
        }
        addOn_bookBtn.setOnClickListener{
            Dialog_Choice()
        }
    }

    private fun Dialog_Choice() {
        val builder = AlertDialog.Builder(this@AddOnActivity)
        builder.setTitle(getString(R.string.food_ex))
        // Display a message on alert dialog
        builder.setMessage(getString(R.string.popup_choice))
        builder.setPositiveButton(getString(R.string.food)){ dialog, which ->
            startActivity(Intent(this@AddOnActivity, OnTrackActivity::class.java))
            finish()
        }
        // Display a negative button on alert dialog
        builder.setNegativeButton(getString(R.string.ex)){ dialog, which ->
            startActivity(Intent(this@AddOnActivity, OnExerciseActivity::class.java))
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
